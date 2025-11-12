package Development.DTOs;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetClientDTO {
        private Long identification;
        private String firstName;
        private String lastName;
        private String email;
        private Long phoneNumber;
}
