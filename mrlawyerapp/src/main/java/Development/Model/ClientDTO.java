package Development.Model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
        private Long identification;
        private String firstName;
        private String lastName;
        private String email;
        private Long phoneNumber;
}
