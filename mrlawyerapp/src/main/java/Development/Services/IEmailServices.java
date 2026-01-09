package main.java.Development.Services;

import main.java.Development.DTOs.MailBodyDTO;

public interface IEmailServices {
    public void sendSimpleEmail(MailBodyDTO mailbody, String email);

}