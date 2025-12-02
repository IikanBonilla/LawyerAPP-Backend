package Development.DTOs;

import lombok.Data;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProceedingDTO {
    private String id;
    private String proceeding;
    private String anotation;
    
    private LocalDate proceedingDate;
    private LocalDate startTermDate;
    private LocalDate endTermDate;
    private LocalDate registerDate;
}
