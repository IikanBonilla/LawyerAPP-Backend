package Development.DTOs;

import lombok.Data;

import java.time.LocalDateTime;

import Development.Model.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Enumerated(EnumType.STRING)
    private Status status;
    private String idClient;
}
