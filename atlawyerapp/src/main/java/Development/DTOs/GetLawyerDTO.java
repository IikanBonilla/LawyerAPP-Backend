package Development.DTOs;

import Development.Model.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetLawyerDTO {
    private String id;
    private Long identification;
    private String fullName;
    private String email;
    private String idUser;
    private String idLawFirm;
    @Enumerated(EnumType.STRING)
    private Status status;
}
