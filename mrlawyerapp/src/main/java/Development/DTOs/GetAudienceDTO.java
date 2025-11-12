package Development.DTOs;

import lombok.Data;

import java.time.LocalDate;

import Development.Model.Status;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAudienceDTO {
    private String address;
    private String meetingLink;
    private LocalDate date;
    private Status status;
}
