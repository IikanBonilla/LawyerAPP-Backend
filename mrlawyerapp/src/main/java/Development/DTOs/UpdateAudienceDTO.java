package Development.DTOs;

import java.time.LocalDate;

import Development.Model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAudienceDTO{
    private String meetingLink;
    private LocalDate date;
    private Status status;
}