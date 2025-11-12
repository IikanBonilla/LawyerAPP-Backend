package Development.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateClientDTO {
    private Long identification;
    private String firstName;
    private String lastName;
    private String email;
    private Long phoneNumber;
}