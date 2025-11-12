package Development.Services;

import Development.DTOs.AssociateClientLawyerDTO;
import Development.Model.ClientLawyer;

public interface IClientLawyerServices {
    
    ClientLawyer associateClientToLawyer(AssociateClientLawyerDTO associationDTO);
    void removeClientFromLawyer(String clientLawyerId);
    boolean existsAssociation(String clientId, String lawyerId);
}