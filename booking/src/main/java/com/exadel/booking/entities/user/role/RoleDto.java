package com.exadel.booking.entities.user.role;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
public class RoleDto {

    private UUID id;
    @NotBlank
    private String name;
}