package Development.Services;

import java.util.List;

import Development.DTOs.CreateProceedingDTO;
import Development.DTOs.GetProceedingDTO;
import Development.Model.Proceeding;
import Development.Model.Process;

public interface IProceedingServices {
    public List<GetProceedingDTO> findByProcess(String idProcess);
    public Proceeding createProceedingForProcess(String idProcess, CreateProceedingDTO proceedingDTO, String userEmail);
    public Proceeding findById(String id);
    public void delete(String id);
    public void sendProceedingEmail(Proceeding proceeding, Process process, String userEmail);
    
}
