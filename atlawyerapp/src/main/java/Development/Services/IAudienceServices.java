package Development.Services;

import java.util.List;

import Development.DTOs.CreateAudienceDTO;
import Development.DTOs.GetAudienceDTO;
import Development.DTOs.UpdateAudienceDTO;
import Development.Model.Audience;

public interface IAudienceServices {
    public List<GetAudienceDTO> findByClient(String idClient);
    public List<GetAudienceDTO> findByUser(String idUser);
    public Audience update(String id, UpdateAudienceDTO updateDTO);
    public Audience saveForClient(String idLawyer, String idClient, CreateAudienceDTO audienceDTO);
    public GetAudienceDTO findById(String id);
    public void delete(String id);

    public String findClientName(String id, String idClient);

}
