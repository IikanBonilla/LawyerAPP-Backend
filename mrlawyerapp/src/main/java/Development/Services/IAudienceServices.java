package Development.Services;

import java.util.List;

import Development.DTOs.CreateAudienceDTO;
import Development.DTOs.GetAudienceDTO;
import Development.DTOs.UpdateAudienceDTO;
import Development.Model.Audience;
import Development.Model.Status;

public interface IAudienceServices {
    public List<GetAudienceDTO> findByClient(String idClient);
    public List<GetAudienceDTO> findByProcess(String idProcess);
    public Audience update(String id, UpdateAudienceDTO updateDTO);
    public Audience save(CreateAudienceDTO audienceDTO);
    public Audience findById(String id);
    public void delete(String id);
    public List<Audience> findByStatus(Status status);

}
