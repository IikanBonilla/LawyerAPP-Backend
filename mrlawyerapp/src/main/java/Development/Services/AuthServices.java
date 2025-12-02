package Development.Services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import Development.DTOs.AuthResponseDTO;
import Development.DTOs.GetLawyerDTO;
import Development.DTOs.LoginRequestDTO;
import Development.DTOs.RegisterAdminRequestDTO;
import Development.DTOs.RegisterRequestDTO;
import Development.Model.LawFirm;
import Development.Model.LawyerInvitation;
import Development.Model.LawyerProfile;
import Development.Model.Role;
import Development.Model.Status;
import Development.Model.User;
import Development.Model.UserStatus;
import Development.Repository.LawFirmRepository;
import Development.Repository.LawyerInvitationRepository;
import Development.Repository.LawyerRepository;
import Development.Repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
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
    private final PasswordValidatorServices passwordValidator;
    @Autowired
    private LawyerInvitationRepository invitationRepository;
    @Autowired
    private LawFirmRepository lawFirmRepository;
    

    @PostConstruct
    public void init(){
        if(userRepository.count() == 0){
            User superAdmin  = new User();
            superAdmin.setUsername("iikanAdmin0506");
            superAdmin.setPassword(passwordEncoder.encode("Iikan123.0506"));
            superAdmin.setRole(Role.SUPER_ADMIN);
            superAdmin.setUserStatus(UserStatus.ACTIVE);
            userRepository.save(superAdmin);

        }
    }

     public AuthResponseDTO registerAdmin(RegisterAdminRequestDTO registerRequest) {
        // Validar que el username no exista
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        // Validar que el nombre de la firma no exista
        if (lawFirmRepository.existsByFirmName(registerRequest.getFirmName())) {
            throw new RuntimeException("Ya existe una firma con ese nombre");
        }

        // Validar formato de contraseña
        if (!passwordValidator.isValidPassword(registerRequest.getPassword())) {
            throw new RuntimeException(passwordValidator.getPasswordRequirements());
        }

        // Crear el usuario ADMIN
        User adminUser = new User();
        adminUser.setUsername(registerRequest.getUsername());
        adminUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        adminUser.setRole(Role.ADMIN);
        adminUser.setUserStatus(UserStatus.ACTIVE);
        User savedAdmin = userRepository.save(adminUser);

        // Crear la LawFirm para el admin
        LawFirm lawFirm = new LawFirm();
        lawFirm.setFirmName(registerRequest.getFirmName());
        lawFirm.setIdAdmin(savedAdmin);
        lawFirm.setEmail(registerRequest.getEmail());
        LawFirm savedLawFirm = lawFirmRepository.save(lawFirm);

        // Generar el token JWT
        String token = jwtService.generateToken(savedAdmin, savedLawFirm.getId());

        return new AuthResponseDTO(
            token,
            savedAdmin.getUsername(),
            "Administrador", // Nombre por defecto para admin
            savedAdmin.getRole().name(),
            null, 
            savedAdmin.getId(),
            savedLawFirm.getId(),
            savedLawFirm.getFirmName(),
            savedLawFirm.getEmail(),
            null
        );
    }

    public AuthResponseDTO register(RegisterRequestDTO registerRequest) {
        // Validar que el username no exista
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        if(lawyerRepository.existsByIdentification(registerRequest.getIdentification())){
            throw new RuntimeException("Esta identificación ya está siendo usada");
        }

        // Validar invitación en lugar de emails autorizados
        LawyerInvitation invitation = invitationRepository
            .findByEmailAndIdentificationAndUsedFalse(registerRequest.getEmail(), registerRequest.getIdentification())
            .orElseThrow(() -> new RuntimeException("No tiene una invitación válida para registrarse. Contacte al administrador de su firma."));

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

        // Crear el perfil de abogado y asignar a la firma
        LawyerProfile lawyer = new LawyerProfile();
        lawyer.setIdentification(registerRequest.getIdentification());
        lawyer.setFullName(registerRequest.getFullName());
        lawyer.setEmail(registerRequest.getEmail());
        lawyer.setIdUser(savedUser);
        lawyer.setIdLawFirm(invitation.getIdLawFirm()); 

        LawyerProfile savedLawyer = lawyerRepository.save(lawyer);

        // Marcar invitación como usada
        invitation.setUsed(true);
        invitationRepository.save(invitation);

        // Generar el token JWT
        String token = jwtService.generateToken(savedUser, invitation.getIdLawFirm().getId());

        return new AuthResponseDTO(
            token, 
            savedUser.getUsername(), 
            savedLawyer.getFullName(), 
            savedUser.getRole().name(), 
            savedLawyer.getId(), 
            savedUser.getId(),
            invitation.getIdLawFirm().getId(),
            invitation.getIdLawFirm().getFirmName(),
            invitation.getEmail(),
            savedLawyer.getStatus().toString()
        );
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
            
            if(user.getUserStatus() != UserStatus.ACTIVE){
                String message = user.getUserStatus() == UserStatus.SUSPENDED ?
                    "Cuenta suspendida. Renueve su suscripción." : 
                    "Cuenta inactiva. Contacte al administrador.";
                throw new RuntimeException(message);
            }
            String idLawFirm = null;
            String firmName = null;
            String fullName = null;
            String idLawyer = null;
            String lawyerStatus = null;
            String email = null;
            switch(user.getRole()) {
                case SUPER_ADMIN:
                    fullName = "Super Administrador";
                    idLawFirm = null; // SUPER_ADMIN no pertenece a una firma
                    firmName = "Sistema Principal";
                    idLawyer = null;
                    break;
                    
                case ADMIN:
                    LawFirm lawFirm = lawFirmRepository.findByIdAdminId(user.getId())
                        .orElseThrow(() -> new RuntimeException("Admin no tiene firma asociada"));
                    idLawFirm = lawFirm.getId();
                    firmName = lawFirm.getFirmName();
                    fullName = "Administrador";
                    idLawyer = null;
                    email = lawFirm.getEmail();
                    break;
                    
                case LAWYER:
                    GetLawyerDTO lawyer = lawyerRepository.findByIdUser(user.getId());
                    if(lawyer == null){
                        throw new EntityNotFoundException("Abogado no encontrado para usuario:" + user.getId());
                    }
                     // VERIFICAR ESTADO DEL ABOGADO
                    if (lawyer.getStatus() != Status.ACTIVE) {
                        throw new RuntimeException("Su perfil de abogado está " + 
                            (lawyer.getStatus() == Status.INACTIVE ? "inactivo" : lawyer.getStatus().name().toLowerCase()) + 
                            ". Contacte al administrador de su firma.");
                    }
                    LawFirm lawyerFirm = lawFirmRepository.findById(lawyer.getIdLawFirm())
                        .orElseThrow(() -> new RuntimeException("Firma no encontrada para el abogado"));
                    idLawFirm = lawyerFirm.getId();
                    firmName = lawyerFirm.getFirmName();
                    fullName = lawyer.getFullName();
                    idLawyer = lawyer.getId();
                    email = lawyer.getEmail();
                    lawyerStatus = lawyer.getStatus().toString();
                    break;
                    
                default:
                    throw new RuntimeException("Rol de usuario no reconocido");
            }

            String token = jwtService.generateToken(user, idLawFirm);

            return new AuthResponseDTO(
                token,
                user.getUsername(),
                fullName,
                user.getRole().name(),
                idLawyer,
                user.getId(),
                idLawFirm,
                firmName,
                email,
                lawyerStatus
            );

        } catch (AuthenticationException e) {
            throw new RuntimeException("Credenciales inválidas");
        }
    }
}
