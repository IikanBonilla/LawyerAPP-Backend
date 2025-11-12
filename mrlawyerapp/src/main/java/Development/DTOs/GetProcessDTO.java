package Development.DTOs;

import java.time.LocalDate;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProcessDTO {
    private String identification;
    private LocalDate radicationDate;
    private String officeName;
    private String ponente;
    private String processType;
    private String processClass;
    private String subClassProcess;
    private String recurso;
    private String contenidoDeRadicacion;
}
