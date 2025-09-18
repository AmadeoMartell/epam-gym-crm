package com.epam.crm.api;

import com.epam.crm.api.dto.auth.ChangePasswordRequest;
import com.epam.crm.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Api(tags = "Auth")
public class AuthController {

    private final AuthService authService;

    @ApiOperation("Login with username/password")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Invalid credentials"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @GetMapping("/login")
    public ResponseEntity<Void> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        authService.authenticate(username, password);
        return ResponseEntity.ok().build();
    }

    @ApiOperation("Change password")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Invalid old password"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @PutMapping("/login/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        authService.changePassword(req.getUsername(), req.getOldPassword(), req.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
