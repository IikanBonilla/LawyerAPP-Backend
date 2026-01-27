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
public class UpdateLawyerDTO {
    @Enumerated(EnumType.STRING)
    private Status status;
}
