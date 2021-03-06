package com.exadel.booking.entities.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
public class UserDto {

    private UUID id;
    @NotBlank
    private String email;
    @NotBlank
    private String username;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotNull
    private Boolean isActive;
    @NotNull
    private List<String> roleNames;
    @NotBlank
    private String position;
    @NotBlank
    private String department;
    @NotBlank
    private String location;
    @NotBlank
    private String phone;
    @NotBlank
    private String skype;
    @NotBlank
    private String hr;
}
