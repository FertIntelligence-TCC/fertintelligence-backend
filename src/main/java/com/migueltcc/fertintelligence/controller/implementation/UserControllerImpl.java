package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.UserController;
import com.migueltcc.fertintelligence.dto.user.UserCreateRequestDto;
import com.migueltcc.fertintelligence.dto.user.UserPostRequestDto;
import com.migueltcc.fertintelligence.dto.user.UserResponseDto;
import com.migueltcc.fertintelligence.service.documentation.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Validated
@CrossOrigin
@Slf4j
public class UserControllerImpl implements UserController {
    private final UserService userService;
    @Autowired
    public UserControllerImpl(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> createUser(@Validated @RequestBody UserCreateRequestDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(
            @Validated @RequestBody UserPostRequestDto userDto,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(authentication.getName(), userDto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.deleteUser(authentication.getName()));
    }

    @GetMapping("/get")
    public ResponseEntity<UserResponseDto> getUser(
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(authentication.getName()));
    }
}
