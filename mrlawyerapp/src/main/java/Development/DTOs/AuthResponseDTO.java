package Development.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String username;
    private String lawyerName;
    private String role;
    private String idLawyer;
    private String idUser;
    private String idLawFirm; 
    private String firmName; 
    private String email;
    private String lawyerStatus;
}
