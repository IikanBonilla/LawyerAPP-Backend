package Development.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterAdminRequestDTO {
    private String username;
    private String password;
    private String firmName; // Nombre de la firma legal
    private String email;  //Email de contacto
}