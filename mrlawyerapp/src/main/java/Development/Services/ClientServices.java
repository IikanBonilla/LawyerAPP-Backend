package Development.Services;

import Development.DTOs.ClientDTO;
import Development.DTOs.GetClientFullNameDTO;
import Development.Model.Client;
import Development.Model.LawyerProfile;
import Development.Model.Status;

import java.util.List;
import java.util.Map;

import Development.Repository.ClientProcessRepository;
import Development.Repository.ClientRepository;
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
    private ClientProcessRepository clientProcessRepository;
    @Autowired
    private NotificationServices notificationService;


    private ClientDTO convertToClientDTO(Client client) {
    ClientDTO dto = new ClientDTO();
    dto.setId(client.getId());
    dto.setIdentification(client.getIdentification());
    dto.setFirstName(client.getFirstName());
    dto.setLastName(client.getLastName());
    dto.setEmail(client.getEmail());
    dto.setPhoneNumber(client.getPhoneNumber());
    dto.setStatus(client.getStatus());
    return dto;
    }
    @Override
    public List<ClientDTO> findByUserId(String idUser) {
        // Validación básica
        if (idUser == null || idUser.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }

        try {
         /*   List<ClientDTO> orderList = new ArrayList<>(); 
            List<ClientDTO> clients = clientRepository.findByUserId(idUser);
            for (int i = clients.size() -1; i >= 0; i--){
                    orderList.add(clients.get(i));

            }
            */
            return clientRepository.findByUserId(idUser);

        }catch (Exception ex) {
            logger.error("Error obteniendo clientes para usuario {}", idUser, ex);
            throw new RuntimeException("Error al cargar la lista de clientes");
        }
    }
    



    @Transactional
    @Override
    public Client createClientForLawyer(String idLawyer, ClientDTO clientDTO) {
        try{//Validar que existe un abogado con el id recibido
        LawyerProfile lawyer = lawyerRepository.findById(idLawyer).orElseThrow(
            () -> new EntityNotFoundException("No existe un ABOGADO con ID: " + idLawyer)
        );

        //Validar que no exista un cliente con la misma identificacion para ese abogado
        if(clientRepository.existsByIdentificationAndIdLawyerId(clientDTO.getIdentification(), idLawyer)){
            throw new IllegalStateException("Ya existe un cliente con identificacion: " + clientDTO.getIdentification());
        }

        //Crear cliente
        Client client = new Client();
        client.setIdentification(clientDTO.getIdentification());
        client.setFirstName(clientDTO.getFirstName());
        client.setLastName(clientDTO.getLastName());
        client.setEmail(clientDTO.getEmail());
        client.setPhoneNumber(clientDTO.getPhoneNumber());
        client.setIdLawyer(lawyer);

        Client savedClient = clientRepository.save(client);

        notificationService.sendNotificationAccount(idLawyer, "CLIENT_CREATED", savedClient);
        notificationService.sendNotificationAdmin(lawyer.getIdLawFirm().getId(), "CLIENT_CREATED", savedClient);
        //Crear asociacion a abogado
        return savedClient;
        }catch (Exception ex) {
            throw new RuntimeException("Error interno: " + ex);
        }


    }




    @Override
    public List<GetClientFullNameDTO> findByProcessId(String idProcess) {
        if(!processRepository.existsById(idProcess)) 
        throw new EntityNotFoundException("No existe un proceso con id: " + idProcess);

        try{
            return clientRepository.findAllNamesByIdProcess(idProcess);
        }catch(Exception ex){
            throw new RuntimeException("Error al obtener clientes de proceso: " + idProcess);
        }
    }

    @Override
    public ClientDTO findById(String id) {
        Client client = clientRepository.findById(id).orElseThrow(
        () -> new EntityNotFoundException("No existe un cliente con id: " + id)
    );
        return convertToClientDTO(client);
    }

    @Transactional
    @Override
    public void deleteClient(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de cliente inválido");
        }
        Client client = clientRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
          
        LawyerProfile lawyer = client.getIdLawyer();
        // Verificar si tiene procesos activos antes de eliminar
        if(!client.getAudiences().isEmpty()){
            throw new IllegalArgumentException("No se puede eliminar cliente porque tiene: "+ client.getAudiences().size()+" audiencias activas");
        }
        if (!client.getDocuments().isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar cliente porque tiene: "+ client.getDocuments().size()+" documentos asociados");
        }
        if (!client.getProcesses().isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar cliente porque tiene: "+ client.getProcesses().size()+" procesos activos");
        }
        clientRepository.deleteById(id);

        Map<String, String> payload = Map.of("idClient", id);

        notificationService.sendNotificationAccount(lawyer.getId(), "CLIENT_DELETED", payload);
        notificationService.sendNotificationAdmin(lawyer.getIdLawFirm().getId(), "CLIENT_DELETED", payload);
    }


    @Override
    @Transactional
    public void deleteClientDefinitively(String idClient) {

        // Validaciones básicas
        if (idClient == null || idClient.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de cliente inválido");
        }
    
        Client client = clientRepository.findById(idClient)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + idClient));
        
        // Eliminar en cascada (más eficiente)
        clientProcessRepository.deleteAllByIdClientId(idClient);
        
        LawyerProfile lawyer = client.getIdLawyer();

        // Eliminar client
        clientRepository.deleteById(idClient);


        Map<String, String> payload = Map.of("idClient", idClient);

        notificationService.sendNotificationAccount(lawyer.getId(), "CLIENT_DELETED", payload);
        notificationService.sendNotificationAdmin(lawyer.getIdLawFirm().getId(), "CLIENT_DELETED", payload);
    }

    @Override
    public Client updateClient(String id, ClientDTO clientDTO) {
       Client existingClient = clientRepository.findById(id).orElseThrow(
        () -> new EntityNotFoundException("No existe un cliente con id: " + id)
       );

        if(!existingClient.getIdentification().equals(clientDTO.getIdentification()) &&
            clientRepository.existsByIdentificationAndIdLawyerId(clientDTO.getIdentification(), existingClient.getIdLawyer().getId())) {
                throw new IllegalStateException("Ya existe un cliente con identificacion: " + clientDTO.getIdentification());
        }

        LawyerProfile lawyer = existingClient.getIdLawyer();

        existingClient.setFirstName(clientDTO.getFirstName());
        existingClient.setLastName(clientDTO.getLastName());
        existingClient.setIdentification(clientDTO.getIdentification());
        existingClient.setEmail(clientDTO.getEmail());
        existingClient.setPhoneNumber(clientDTO.getPhoneNumber());

        Client updateClient = clientRepository.save(existingClient);

        notificationService.sendNotificationAccount(lawyer.getId(), "CLIENT_UPDATED", updateClient);
        notificationService.sendNotificationAdmin(lawyer.getIdLawFirm().getId(), "CLIENT_UPDATED", updateClient);

        return updateClient;


    }
    @Override
    public Client updateStatus(String idClient, Status status) {
        Client client = clientRepository.findById(idClient).orElseThrow(
            () -> new EntityNotFoundException("No existe un cliente con id: " + idClient)
        );

        try {
            client.setStatus(status);
            return clientRepository.save(client);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Estado inválido: " + status);
        }
        
    }
    @Override
    public List<ClientDTO> findByUserIdAndStatus(String idUser, Status status) {
        // Validación básica
        if (idUser == null || idUser.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }
        
        try {
            return clientRepository.findByUserIdAndStatus(idUser, status);
        }catch (Exception ex) {
            logger.error("Error obteniendo clientes para usuario {} con estado {}", idUser, status, ex);
            throw new RuntimeException("Error al cargar la lista de clientes por estado");
        }
    }



}