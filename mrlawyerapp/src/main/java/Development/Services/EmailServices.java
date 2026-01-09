package main.java.Development.Services;

import main.java.Development.DTOs.MailBodyDTO;
import Development.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServices implements IEmailServices{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendSimpleEmail(MailBodyDTO mailbody, String email) {
        // TODO Auto-generated method stub
        
    }
}