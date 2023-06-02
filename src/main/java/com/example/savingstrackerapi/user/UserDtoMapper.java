package com.example.savingstrackerapi.user;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserDtoMapper implements Function<User, UserDto> {
  @Override
  public UserDto apply(User user) {
    return new UserDto(
            user.getFirstname(),
            user.getLastname(),
            user.getEmail(),
            user.getRole()
    );
  }
}
