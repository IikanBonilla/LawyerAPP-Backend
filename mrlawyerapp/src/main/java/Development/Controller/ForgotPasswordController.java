package Development.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Development.DTOs.ChangePasswordDTO;
import Development.DTOs.EmailBodyDTO;
import Development.Model.ForgotPassword;
import Development.Model.User;
import Development.Repository.ForgotPasswordRepository;
import Development.Repository.UserRepository;
import Development.Services.EmailServices;
import Development.Services.UserDetailsServiceImpl;
import jakarta.persistence.EntityNotFoundException;

import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailServices emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;
    
    @PostMapping("/verifyEmail")
    public ResponseEntity<?> verifyEmail(@RequestParam String email) { 
       User user = userRepository.findByEmail(email).orElseThrow(() ->
            new EntityNotFoundException("No existe un usuario con ese correo electrónico"));

        ForgotPassword existFp = forgotPasswordRepository.findByIdUser(user);

        if (existFp != null) {
            existFp.setIdUser(null);
            forgotPasswordRepository.deleteById(existFp.getId());

            forgotPasswordRepository.flush();
        }

        

        try{
        
        int otp = emailService.otpGenerator();
        EmailBodyDTO emailBody = new EmailBodyDTO();
        emailBody.setTo(email);
        emailBody.setText("Este es el código de verificación para restablecer su contraseña. \n\n " + 
                            "Código: " + otp);
        emailBody.setSubject("Codígo de restablecimiento de contraseña");

        ForgotPassword fp = new ForgotPassword();
        
        fp.setOtp(otp);
        fp.setExpirationTime(new Date(System.currentTimeMillis() + 100 * 1000));
        fp.setIdUser(user);


        emailService.sendSimpleMessage(emailBody);
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok("Correo electrónico verificado. Se ha enviado un código de restablecimiento.");
    
        }catch (DataIntegrityViolationException e) {
            // Manejar específicamente el error de duplicado
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Error: Ya existe un código activo para este usuario. Por favor, espere unos momentos.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor: " + e.getMessage());
        }
    }
     

    
    @PostMapping("/verifyOTP/{email}")
    public ResponseEntity<?> verifyOtp(@PathVariable String email, @RequestParam int otp){
        User user = userRepository.findByEmail(email).orElseThrow(() ->
            new EntityNotFoundException("No existe un usuario con ese correo electrónico"));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user).orElseThrow(() ->
                    new EntityNotFoundException("OTP inválido o usuario no encontrado"));
        
        if(fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepository.deleteById(fp.getId());
            return ResponseEntity.badRequest().body("OTP expirado");
        }


        return ResponseEntity.ok("OTP verificado.");
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<?> changePasswordHandler(@PathVariable String email, @RequestBody ChangePasswordDTO newPassword){
        User user = userRepository.findByEmail(email).orElseThrow(() ->
            new EntityNotFoundException("No existe un usuario con ese correo electrónico"));
        
        if(!newPassword.getPassword().equals(newPassword.getRepeatPassword()))
            return ResponseEntity.badRequest().body("Las contraseñas no coinciden.");


        userDetailsService.changePassword(user, passwordEncoder.encode(newPassword.getPassword()));

        return ResponseEntity.ok("Contraseña cambiada exitosamente.");
    }
    
}
