package com.exadel.booking.security.rest;

import com.exadel.booking.security.dto.AuthenticationRequestDto;
import com.exadel.booking.security.jwt.JwtTokenProvider;
import com.exadel.booking.user.User;
import com.exadel.booking.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth/")
@RequiredArgsConstructor
public class AuthenticationRestControllerV1 {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;


    @PostMapping("login")
    public ResponseEntity login(@RequestBody AuthenticationRequestDto requestDto) {
        try {
            String email = requestDto.getEmail();
            String password = requestDto.getPassword();
            User user = userService.findUserByEmail(email);

            if (user == null) {
                return new ResponseEntity<String>("Invalid email or password", HttpStatus.UNAUTHORIZED);
            }

            String username = user.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            String token = jwtTokenProvider.createToken(username, user.getRoles());

            return ResponseEntity.ok(userService.getUserById(user.getId()));
        } catch (AuthenticationException e) {
            return new ResponseEntity<String>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
    }
}