package Development.DTOs;

import Development.Model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
    private String id;
    private Long identification;
    private String firstName;
    private String lastName;
    private String email;
    private Long phoneNumber;
    private Status status;
}