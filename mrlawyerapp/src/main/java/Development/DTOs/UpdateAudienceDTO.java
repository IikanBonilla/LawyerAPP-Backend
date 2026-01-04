package Development.DTOs;

import java.time.LocalDateTime;

import Development.Model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAudienceDTO{
    private String meetingLink;
    private LocalDateTime audience_date;
    private Status status;
}