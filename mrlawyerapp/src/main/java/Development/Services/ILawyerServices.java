package Development.Services;

import java.util.List;

import Development.DTOs.GetLawyerDTO;
import Development.Model.LawyerProfile;
import Development.Model.Status;

public interface ILawyerServices {
    public List<GetLawyerDTO> getLawyersByFirm(String idFirm);
    public GetLawyerDTO getLawyerByIdUser(String userId);
    public LawyerProfile findLawyerByUsername(String username);
    public void updateLawyerStatus(String idLawyer, Status status);
    public void deleteLawyer(String idLawyer);
}
