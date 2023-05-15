package com.example.savingstrackerapi.user;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.savingstrackerapi.user.RegisterRequest;
import com.example.savingstrackerapi.user.AuthenticationRequest;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/register")
        public ResponseEntity<AuthenticationResponse> register(
                @RequestBody RegisterRequest request
        ) {
            return ResponseEntity.ok(service.register(request));
        }



    @PostMapping("/authenticate")
        public ResponseEntity<AuthenticationResponse> authenticate(
                @RequestBody AuthenticationRequest request
        ) {
            return ResponseEntity.ok(service.authenticate(request));
        }
}