package Development.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProcessDTO {
    private String identification;
    private LocalDate radicationDate;
    private String officeName;
    private String ponente;
    private String processType;
    private String processClass;
    private String subClassProcess;
    private String recurso;
    private String contenidoDeRadicacion;
    private String idLawyer; // Solo el ID del abogado, no el objeto completo
}