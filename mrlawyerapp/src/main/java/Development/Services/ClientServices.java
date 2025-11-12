package Development.Services;

import Development.DTOs.CreateClientDTO;
import Development.DTOs.GetClientDTO;
import Development.DTOs.GetClientFullNameDTO;
import Development.DTOs.UpdateClientDTO;

import Development.Model.Client;
import Development.Model.ClientLawyer;
import Development.Model.LawyerProfile;

import java.util.List;

import Development.Repository.ClientRepository;
import Development.Repository.ClientLawyerRepository;
import Development.Repository.LawyerRepository;
import Development.Repository.ProcessRepository;
import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientServices implements IClientServices{
    private static final Logger logger = LoggerFactory.getLogger(ClientServices.class);
    
    @Autowired
    private ClientRepository clientRepository;
    @Autowired 
    private ProcessRepository processRepository;
    @Autowired
    private LawyerRepository lawyerRepository;
    @Autowired
    private ClientLawyerRepository clientLawyerRepository;


    @Override
    public List<GetClientDTO> findByUserId(String idUser) {
        // Validación básica
        if (idUser == null || idUser.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }
        
        try {
            return clientRepository.findByUserId(idUser);
        } catch (Exception ex) {
            logger.error("Error obteniendo clientes para usuario {}", idUser, ex);
            throw new RuntimeException("Error al cargar la lista de clientes");
        }
    }
    



    @Transactional
    @Override
    public Client createClientForLawyer(String idLawyer, CreateClientDTO clientDTO) {
        //Validar que existe un abogado con el id recibido
        LawyerProfile lawyer = lawyerRepository.findById(idLawyer).orElseThrow(
            () -> new EntityNotFoundException("No existe un ABOGADO con ID: " + idLawyer)
        );

        //Validar que no exista un cliente con la misma identificacion
        if(clientRepository.existsByIdentification(clientDTO.getIdentification())){
            throw new IllegalStateException("Ya existe un cliente con identificacion: " + clientDTO.getIdentification());
        }

        //Crear cliente
        Client client = new Client();
        client.setIdentification(clientDTO.getIdentification());
        client.setFirstName(clientDTO.getFirstName());
        client.setLastName(clientDTO.getLastName());
        client.setEmail(clientDTO.getEmail());
        client.setPhoneNumber(clientDTO.getPhoneNumber());

        Client savedClient = clientRepository.save(client);

        //Crear asociacion a abogado
        ClientLawyer clientLawyer = new ClientLawyer();
        clientLawyer.setIdClient(savedClient);
        clientLawyer.setIdLawyer(lawyer);
        clientLawyerRepository.save(clientLawyer);

        return savedClient;
    }




    @Override
    public List<GetClientFullNameDTO> findByProcessId(String idProcess) {
        if(!processRepository.existsById(idProcess)) 
        throw new EntityNotFoundException("No existe un proceso con id: " + idProcess);

        try{
            return clientRepository.findByIdProcess(idProcess);
        }catch(Exception ex){
            throw new RuntimeException("Error al obtener clientes de proceso: " + idProcess);
        }
    }

    @Override
    public Client findById(String id) {
        return clientRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("No existe un cliente con id: " + id)
        );
    }

    @Override
    public void deleteClient(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de cliente inválido");
        }
        Client client = clientRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
          
            
        // Verificar si tiene procesos activos antes de eliminar
        if (!client.getProcesses().isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar cliente con procesos activos");
        }
        clientRepository.delete(client);
    }




    @Override
    public Client updateClient(String id, UpdateClientDTO clientDTO) {
       Client existingClient = clientRepository.findById(id).orElseThrow(
        () -> new EntityNotFoundException("No existe un cliente con id: " + id)
       );

        if(!existingClient.getIdentification().equals(clientDTO.getIdentification()) &&
            clientRepository.existsByIdentification(clientDTO.getIdentification())) {
                throw new IllegalStateException("Ya existe un cliente con identificacion: " + clientDTO.getIdentification());
        }

        existingClient.setFirstName(clientDTO.getFirstName());
        existingClient.setLastName(clientDTO.getLastName());
        existingClient.setEmail(clientDTO.getEmail());
        existingClient.setPhoneNumber(clientDTO.getPhoneNumber());

        return clientRepository.save(existingClient);
    }

}
