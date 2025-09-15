package com.epam.crm.api;

import com.epam.crm.api.dto.auth.ChangePasswordRequest;
import com.epam.crm.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public ResponseEntity<Void> login(
            @RequestParam String username,
            @RequestParam String password
    ) {
        authService.authenticate(username, password);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/login/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        authService.changePassword(req.getUsername(), req.getOldPassword(), req.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
