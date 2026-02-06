package Development.DTOs;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventDTO {
    
    private String type;
    private Object data;

    @Builder.Default
    private Instant timeStamp = Instant.now();

    
}
