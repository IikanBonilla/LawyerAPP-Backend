package Development.Services;
import java.util.List;

import Development.DTOs.CreateProcessDTO;
import Development.DTOs.GetProcessDTO;
import Development.DTOs.GetProcessIdentificationDTO;
import Development.DTOs.UpdateProcessDTO;
import Development.Model.Process;
public interface IProcessServices {
    public GetProcessDTO findProcessById(String id);
    public List<GetProcessIdentificationDTO> radicadoByClientId(String idClient);
    public void associateClientToProcess(String idProcess, String idClient);
    public Process findById(String id);
    public Process createProcessForClient(String idClient, CreateProcessDTO processDTO);
    public Process updateProcess(String id, UpdateProcessDTO processDTO);
    public void deleteProcess(String id);
    public Process changeProcessStatus(String id, String status);
    public List<GetProcessIdentificationDTO> findProcessByStatus(String idLawyer, String status);

}
