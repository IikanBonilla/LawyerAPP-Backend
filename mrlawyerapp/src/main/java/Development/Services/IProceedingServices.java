package Development.Services;

import java.util.List;

import Development.DTOs.CreateProceedingDTO;
import Development.DTOs.GetProceedingDTO;
import Development.Model.Proceeding;

public interface IProceedingServices {
    public List<GetProceedingDTO> findByProcess(String idProcess);
    public Proceeding createProceedingForProcess(String idProcess, CreateProceedingDTO proceedingDTO);
    public Proceeding findById(String id);
    public void delete(String id);
    
}
