package Development.DTOs;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProceedingDTO {
    private String proceeding;
    private String anotation;
    private LocalDate proceedingDate;
    private LocalDate startTermDate;
    private LocalDate endTermDate;
    private LocalDate registerDate;
    private String idProcess;
}