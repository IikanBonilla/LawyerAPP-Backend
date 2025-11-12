package Development.Services;

import java.util.List;

import Development.DTOs.CreateClientDTO;
import Development.DTOs.GetClientDTO;
import Development.DTOs.GetClientFullNameDTO;
import Development.DTOs.UpdateClientDTO;
import Development.Model.Client;
public interface IClientServices{
    public List<GetClientDTO> findByUserId(String idUser);
    public Client findById(String id);
    public Client createClientForLawyer(String idLawyer, CreateClientDTO clientDTO);
    public Client updateClient(String id, UpdateClientDTO clientDTO);
    public void deleteClient(String id);
    public List<GetClientFullNameDTO> findByProcessId(String idProcess);
    
}
