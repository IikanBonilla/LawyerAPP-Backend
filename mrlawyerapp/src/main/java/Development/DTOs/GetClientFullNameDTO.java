package Development.DTOs;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetClientFullNameDTO {
    private String firstName;
    private String lastName;

    public String getFullName() {
        return (firstName + " " + lastName).trim();
    }
}
