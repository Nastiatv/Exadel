package com.exadel.booking.entities.booking;

import com.exadel.booking.entities.office.floor.room.place.PlaceService;
import com.exadel.booking.entities.queue.Queue;
import com.exadel.booking.entities.queue.QueueService;
import com.exadel.booking.entities.user.User;
import com.exadel.booking.entities.user.UserService;
import com.exadel.booking.utils.mail.EmailSender;
import com.exadel.booking.utils.modelmapper.AMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final AMapper<Booking, BookingDto> bookingMapper;
    private final PlaceService placeService;
    private final UserService userService;
    private final EmailSender emailSender;
    private final QueueService queueService;

    public BookingDto createBooking(UUID placeId, UUID userId, LocalDateTime bookingDate, LocalDateTime dueDate) {
        if (checkDateTimeIsFree(placeId, bookingDate, dueDate)) {
            Booking booking = Booking.builder().place(placeService.getPlaceById(placeId)).user(userService.getUserById(userId)).bookingDate(bookingDate).dueDate(dueDate).build();
            try {
                emailSender.sendEmailsFromAdminAboutNewBooking(booking);
            } catch (MessagingException e) {
                log.info("Mailing error", e);
            }
            return bookingMapper.toDto(bookingRepository.save(booking));
        } else {
            new IllegalArgumentException("sorry time is busy");
        }
        return null;
    }

    public BookingDto updateBookingTime(UUID bookingId, LocalDateTime start, LocalDateTime end) {
        BookingDto bookingdto = getBookingDtoById(bookingId);
        Booking booking = bookingRepository.findBookingById(bookingId);
        if (checkDateTimeIsFree(booking.getPlace().getId(), start, end)) {
            booking.setBookingDate(start);
            booking.setDueDate(end);
            sendEmailsFromAdminAboutNewBooking(booking);
            BookingDto newBookingDto = bookingMapper.toDto(bookingRepository.save(booking));
            checkIfSomeOneNeedThisPlace(bookingdto);
            return newBookingDto;
        } else {
            new IllegalArgumentException("sorry time is busy");
        }
        return null;
    }

    private void sendEmailsFromAdminAboutNewBooking(Booking booking) {
        try {
            emailSender.sendEmailsFromAdminAboutNewBooking(booking);
        } catch (MessagingException e) {
            log.info("Mailing error", e);
        }
    }

    public Boolean checkDateTimeIsFree(UUID placeId, LocalDateTime bookingDate, LocalDateTime dueDate) {
        return bookingRepository.numberOfIntersection(placeId, bookingDate, dueDate) == 0 ? true : false;
    }

    public BookingDto getBookingDtoById(UUID id) {
        return bookingMapper.toDto(Optional.ofNullable(bookingRepository.findBookingById(id)).orElseThrow(() ->
                new EntityNotFoundException("no booking with id" + id)));
    }

    public List<BookingDto> getAllBookingsByUserId(UUID id) throws EntityNotFoundException {
        return bookingMapper.toListDto(Optional.ofNullable(bookingRepository.findListBookingsByUserId(id))
                .orElseThrow(() -> new EntityNotFoundException("user with such id" + id + "has no orders")));
    }

    public Page<BookingDto> getAllActiveBookingsByUserId(UUID id, LocalDateTime now, Pageable pageable) throws EntityNotFoundException {
        Page<Booking> bookings = bookingRepository.findListBookingsByUserIdAndBYDueDateFromNow(id, now, pageable);
        return bookings.map(x -> bookingMapper.toDto(x));
    }

    public void deleteBookingById(UUID bookingId) {
        BookingDto bookingdto = getBookingDtoById(bookingId);
        bookingRepository.deleteById(bookingId);
        checkIfSomeOneNeedThisPlace(bookingdto);
    }

    public void checkIfThisPlace(BookingDto bookingdto) {
        List<Queue> queueList = queueService.findQueueThatIntersectByPlaceAndTimeWithBooking(bookingdto);
        checkIfQueuesOverlapWithOtherBookings(queueList);
    }

    private void checkIfSomeOneNeedThisPlace(BookingDto bookingdto) {
        List<Queue> queueList = queueService.findQueueThatIntersectByPlaceAndTimeWithBooking(bookingdto);
        checkIfQueuesOverlapWithOtherBookings(queueList);
    }

    private void sendEmailsFromAdminAboutYourPlaceIsFree(Queue queue) {
        for (User user : queue.getUsers()) {
            try {
                emailSender.sendEmailsFromAdminAboutYourPlaceIsFree(queue, user.getId());
            } catch (MessagingException e) {
                log.info("Mailing error", e);
            }
        }
    }

    private void checkIfQueuesOverlapWithOtherBookings(List<Queue> queueList) {
        for (Queue queue : queueList) {
            Integer num = bookingRepository.numberOfIntersection(queue.getPlace().getId(), queue.getRequestedStart(), queue.getRequestedEnd());
            if (num == 0) {
                sendEmailsFromAdminAboutYourPlaceIsFree(queue);
            }
        }
    }
}