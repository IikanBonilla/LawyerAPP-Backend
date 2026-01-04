package Development.Controller;

import Development.DTOs.AuthResponseDTO;
import Development.DTOs.LoginRequestDTO;
import Development.DTOs.RegisterAdminRequestDTO;
import Development.DTOs.RegisterRequestDTO;
import Development.Services.AuthServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/auth")

@RequiredArgsConstructor
public class AuthController {

    private final AuthServices authService;

    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<AuthResponseDTO> registerAdmin(@Valid @RequestBody RegisterAdminRequestDTO adminRequest) {
        AuthResponseDTO response = authService.registerAdmin(adminRequest);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        AuthResponseDTO response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        AuthResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

}