package com.exadel.booking.entities.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    public Booking findBookingById(UUID id);

    public List<Booking> findListBookingsByUserId(UUID id);

    @Query("SELECT b FROM Booking b WHERE b_user_id =:us_id AND b_due_date >= :now")
   public List<Booking> findListBookingsByUserIdAndBYDueDateFromNow(@Param("us_id") UUID id, @Param("now") LocalDateTime now);
}