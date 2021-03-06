package com.exadel.booking.entities.office.floor;

import com.exadel.booking.entities.office.floor.room.RoomDto;
import com.exadel.booking.entities.office.floor.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/floor")
@Validated
public class FloorController {
    private final FloorService floorService;
    private final RoomService roomService;

    @PreAuthorize("hasAuthority('FLOOR_READ')")
    @GetMapping(value = "/{id}")
    public FloorDto getFloorById(@PathVariable UUID id) {
        return floorService.getFloorById(id);
    }

    @PreAuthorize("hasAuthority('ROOM_READ')")
    @GetMapping(value = "/{id}/rooms")
    public List<RoomDto> getAllRoomsByFloorId(@PathVariable UUID id) {
        return roomService.getAllRoomsByFloorId(id);
    }


    @PreAuthorize("hasAuthority('FLOOR_WRITE')")
    @PostMapping
    public FloorDto saveFloor(@RequestBody @Valid FloorDto floorDto) {
        return floorService.saveFloorFromDto(floorDto);
    }

    @PreAuthorize("hasAuthority('FLOOR_WRITE')")
    @PutMapping
    public FloorDto updateFloor(@RequestBody @Valid FloorDto floorDto) {
        return floorService.saveFloorFromDto(floorDto);
    }

    @PreAuthorize("hasAuthority('FLOOR_DELETE')")
    @DeleteMapping(value = "/{id}")
    public void deleteFloorById(@PathVariable UUID id) {
        floorService.deleteFloorById(id);
    }
}
