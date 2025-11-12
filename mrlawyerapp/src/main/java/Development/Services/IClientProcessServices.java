package Development.Services;


import Development.DTOs.AssociateClientProcessDTO;
import Development.Model.ClientProcess;

public interface IClientProcessServices {
    ClientProcess associateClientToProcess(AssociateClientProcessDTO associationDTO);
    void removeClientFromProcess(String clientProcessId);
    boolean existsAssociation(String clientId, String processId);
}
