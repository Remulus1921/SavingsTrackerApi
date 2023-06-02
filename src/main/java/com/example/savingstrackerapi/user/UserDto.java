package com.example.savingstrackerapi.user;

public record UserDto (
        String firstname,
        String lastname,
        String email,
        Role role
){
}
