package Development.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LawyerInvitationDTO {
    private String id;
    private String identification;
    private String email;
    private boolean used;
}
