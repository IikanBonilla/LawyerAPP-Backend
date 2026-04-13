package Development.DTOs;

import java.time.LocalDateTime;

import Development.Model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
    private String id;
    private String identification;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Status status;
    private LocalDateTime creationDate;
}