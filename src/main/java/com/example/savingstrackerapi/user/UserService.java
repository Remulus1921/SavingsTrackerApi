package com.example.savingstrackerapi.user;

import com.example.savingstrackerapi.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final UserDtoMapper userDtoMapper;
  private final JwtService jwtService;

  @Autowired
  public UserService(UserRepository userRepository,
                     UserDtoMapper userDtoMapper,
                     JwtService jwtService) {
    this.userRepository = userRepository;
    this.userDtoMapper = userDtoMapper;
    this.jwtService = jwtService;
  }


  public UserDto getUserDetails(HttpServletRequest request) {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String Token;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      throw new IllegalStateException("No user token");
    }
    Token = authHeader.substring(7);
    userEmail = jwtService.extractUsername(Token);
    if(userEmail == null) {
      throw new IllegalStateException("There is no user email in given token");
    }

    return  this.userRepository.findByEmail(userEmail)
            .map(userDtoMapper)
            .orElseThrow(() -> new UsernameNotFoundException("User with mail: " + userEmail + " not found"));
  }
}
