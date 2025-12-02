package Development.Services;

import java.util.List;

import Development.DTOs.CreateLawyerInvitationDTO;
import Development.DTOs.LawyerInvitationDTO;
import Development.Model.LawyerInvitation;

public interface ILawyerInvitationServices {
    public List<LawyerInvitationDTO> findByIdFirm(String idFirm);
    public LawyerInvitation createInvitation(String idFirm, CreateLawyerInvitationDTO invitationDTO);
    public void deleteInvitation(String id);
    
}