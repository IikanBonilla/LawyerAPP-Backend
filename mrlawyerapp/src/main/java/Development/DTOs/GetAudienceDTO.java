package Development.DTOs;

import lombok.Data;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAudienceDTO {
    private String id;
    private String address;
    private String meetingLink;
    private LocalDateTime audience_date;
    private String idClient;
}
