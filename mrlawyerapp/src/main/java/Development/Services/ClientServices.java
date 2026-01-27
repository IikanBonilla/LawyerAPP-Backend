package Development.Services;

import Development.DTOs.ClientDTO;
import Development.DTOs.GetClientFullNameDTO;
import Development.Model.Client;
import Development.Model.LawyerProfile;
import Development.Model.Status;
import Development.Model.User;

import java.sql.SQLException;
import java.util.List;

import Development.Repository.ClientProcessRepository;
import Development.Repository.ClientRepository;
import Development.Repository.LawyerRepository;
import Development.Repository.ProcessRepository;
import Development.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
    private UserRepository userRepository;

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

        //Crear asociacion a abogado
        return savedClient;
        }catch (DataAccessException ex) {
        // Verificar si es el error del trigger de email
        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof SQLException) {
            SQLException sqlEx = (SQLException) rootCause;
            // El código de error de tu trigger es 20020
            if (sqlEx.getErrorCode() == 20020) {
                throw new IllegalArgumentException("Formato de email inválido. Debe contener '@' y '.'");
            }
        }
        // Si no es el error del trigger, relanzar la excepción
        throw ex;
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
        clientRepository.deleteById(id);
    }


    @Override
    @Transactional
    public void deleteClientDefinitively(String idClient, String idUser, String adminPass) {
        User user = userRepository.findById(idUser)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUser));
            
        if(!user.getPassword().equals(adminPass)){
            throw new IllegalArgumentException("Contraseña de administrador incorrecta");
        }
        // Validaciones básicas
        if (idClient == null || idClient.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de cliente inválido");
        }
    
        Client client = clientRepository.findById(idClient)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + idClient));
        
        // Eliminar en cascada (más eficiente)
        clientProcessRepository.deleteAllByIdClientId(idClient);
        
        // Eliminar client
        clientRepository.delete(client);
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

        existingClient.setFirstName(clientDTO.getFirstName());
        existingClient.setLastName(clientDTO.getLastName());
        existingClient.setIdentification(clientDTO.getIdentification());
        existingClient.setEmail(clientDTO.getEmail());
        existingClient.setPhoneNumber(clientDTO.getPhoneNumber());

        return clientRepository.save(existingClient);
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
