package com.exadel.booking.entities.office.floor.room;

import com.exadel.booking.entities.office.floor.room.place.Place;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RoomDto {

    private UUID id;
    private Integer number;
    private UUID floorId;
    private List<Place> place;

}
