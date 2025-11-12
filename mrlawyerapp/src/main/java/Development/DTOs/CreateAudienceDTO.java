package Development.DTOs;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAudienceDTO {
    private String address;
    private String meetingLink;
    private LocalDate date;
    private String idProcess;
}
