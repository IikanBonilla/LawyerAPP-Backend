package Development.Services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import Development.Config.AuthorizedEmailsConfig;
import Development.DTOs.AuthResponseDTO;
import Development.DTOs.LoginRequestDTO;
import Development.DTOs.RegisterRequestDTO;
import Development.Model.LawyerProfile;
import Development.Model.Role;
import Development.Model.User;
import Development.Repository.LawyerRepository;
import Development.Repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServices {
    
    private final UserRepository userRepository;
    private final LawyerRepository lawyerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtServices jwtService;
    private final AuthorizedEmailsConfig authorizedEmails;
    private final PasswordValidatorServices passwordValidator;

        public AuthResponseDTO register(RegisterRequestDTO registerRequest) {
        // Validar que el username no exista
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        // Validar email autorizado
        if (!authorizedEmails.isEmailAuthorized(registerRequest.getEmail())) {
            throw new RuntimeException("Email no autorizado para registro. Contacte al administrador.");
        }

        // Validar formato de contraseña
        if (!passwordValidator.isValidPassword(registerRequest.getPassword())) {
            throw new RuntimeException(passwordValidator.getPasswordRequirements());
        }

        // Validar que el email no esté ya registrado en algún lawyer
        if (lawyerRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Este email ya está registrado");
        }

        // Crear el usuario
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.LAWYER);

        User savedUser = userRepository.save(user);

        // Crear el perfil de abogado
        LawyerProfile lawyer = new LawyerProfile();
        lawyer.setIdentification(registerRequest.getIdentification());
        lawyer.setFullName(registerRequest.getFullName());
        lawyer.setEmail(registerRequest.getEmail()); // Asignar el email validado
        lawyer.setIdUser(savedUser);

        LawyerProfile savedLawyer = lawyerRepository.save(lawyer);

        // Generar el token JWT
        String token = jwtService.generateToken(savedUser);

        return new AuthResponseDTO(token, savedUser.getUsername(), savedUser.getRole().name(), savedLawyer.getId());
    }

    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            User user = (User) authentication.getPrincipal();
            LawyerProfile lawyer = lawyerRepository.findByIdUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Perfil de abogado no encontrado"));

            String token = jwtService.generateToken(user);

            return new AuthResponseDTO(token, user.getUsername(), user.getRole().name(), lawyer.getId());
        } catch (AuthenticationException e) {
            throw new RuntimeException("Credenciales inválidas");
        }
    }
}
