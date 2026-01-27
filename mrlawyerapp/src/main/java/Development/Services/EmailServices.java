package Development.Services;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import Development.DTOs.EmailBodyDTO;
import Development.Repository.ForgotPasswordRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServices {
    
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

       /**
     * Envía email (detecta automáticamente si es HTML o texto)
     */
    public void sendSimpleMessage(EmailBodyDTO emailBody) {
        try {
            // Detectar si es HTML
            boolean isHtml = emailBody.getText() != null && 
                           (emailBody.getText().contains("<html") || 
                            emailBody.getText().contains("<div") ||
                            emailBody.getText().contains("<!DOCTYPE"));
            
            if (isHtml) {
                sendHtmlMessage(emailBody);
            } else {
                sendPlainTextMessage(emailBody);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error enviando email: " + e.getMessage(), e);
        }
    }
    
    /**
     * Envía texto plano
     */
    private void sendPlainTextMessage(EmailBodyDTO emailBody) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailBody.getTo());
        message.setSubject(emailBody.getSubject());
        message.setText(emailBody.getText());
        message.setFrom("noreply@mrasociados.com");
        
        javaMailSender.send(message);
    }
    
    /**
     * Envía HTML (privado, solo se llama desde sendSimpleMessage)
     * @throws UnsupportedEncodingException 
     */
    private void sendHtmlMessage(EmailBodyDTO emailBody) throws MessagingException, UnsupportedEncodingException {
        final MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(emailBody.getTo());
        helper.setSubject(emailBody.getSubject());
        helper.setText(emailBody.getText(), true); // true = HTML
        
        // Para que se vea bien en más clientes
        helper.setFrom("iikanyoharybonilla@gmail.com", "MR Asociados");
        
        javaMailSender.send(message);
    }
    
    public int otpGenerator(){
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }

    public void deleteOldOtp(String id) {
        forgotPasswordRepository.findById(id).orElseThrow(() ->
            new RuntimeException("No existe un OTP con ese ID"));
        
        forgotPasswordRepository.deleteById(id);
    }

}
