package main.java.Development.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data; 
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailBodyDTO {
    private String to;
    private String subject;
    private String text;
    
}
