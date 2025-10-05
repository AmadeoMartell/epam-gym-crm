package com.epam.crm.api;

import com.epam.crm.api.dto.auth.ChangePasswordRequest;
import com.epam.crm.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        var username = body.getOrDefault("username", "").trim();
        var password = body.getOrDefault("password", "");
        String token = authService.authenticateAndIssueToken(username, password);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        authService.changePassword(req.getUsername(), req.getOldPassword(), req.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }
}
