package Development.Services;

import java.util.List;

import Development.DTOs.ClientDTO;
import Development.DTOs.GetClientFullNameDTO;
import Development.Model.Client;
import Development.Model.Status;
public interface IClientServices{
    public List<ClientDTO> findByUserId(String idUser);
    public List<ClientDTO> findByUserIdAndStatus(String idUser, Status status);
    public ClientDTO findById(String id);
    public Client createClientForLawyer(String idLawyer, ClientDTO clientDTO);
    public Client updateClient(String idClient, ClientDTO clientDTO);
    public Client updateStatus(String idClient, Status status);
    public void deleteClient(String id);
    public void deleteClientDefinitively(String id, String idUser, String adminPass);
    public List<GetClientFullNameDTO> findByProcessId(String idProcess);

    
}
