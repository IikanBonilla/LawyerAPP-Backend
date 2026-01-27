package Development.Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import Development.DTOs.ClientDTO;
import Development.Model.Client;
import Development.Model.Status;
import Development.Services.ClientServices;

import org.springframework.web.bind.annotation.PutMapping;




//Controller
@RestController
//url + localhost
@RequestMapping("/api/client")
public class ClientController {
    private Logger logger = LoggerFactory.getLogger(Client.class);
    @Autowired
    private ClientServices clientService;



    @GetMapping("/user/{idUser}")
    public ResponseEntity<?> getClientsByUserId(@PathVariable String idUser) {
        try{
            logger.info("Buscando clientes para Usuario: {}", idUser);
            List<ClientDTO> clients = clientService.findByUserId(idUser);
            logger.info("Clientes encontrados: {}", clients.size());
            return ResponseEntity.ok(clients);
        }catch(IllegalArgumentException ex){
            // Manejo de error de validación (400 Bad Request)
            return ResponseEntity.badRequest().body(ex.getMessage());
        }catch(RuntimeException ex){
            // Manejo de error interno (500 Internal Server Error)
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }catch(Exception ex){
            return ResponseEntity.internalServerError().body("Error inesperado al obtener clientes");
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getClientById(@PathVariable String id) {
        try{
            ClientDTO client = clientService.findById(id);
            logger.info("Cliente encontrado con id: " + id);
            return ResponseEntity.ok(client);
        }catch(IllegalArgumentException ex){
            // Manejo de error de validación (400 Bad Request)
            return ResponseEntity.badRequest().body(ex.getMessage());
        }catch(RuntimeException ex){
            // Manejo de error interno (500 Internal Server Error)
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }catch(Exception ex){
            return ResponseEntity.internalServerError().body("Error inesperado al obtener clientes");
        }
    }

    @PostMapping("/save/{idLawyer}")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<?> saveClient(@PathVariable String idLawyer,@RequestBody ClientDTO clientDTO) {
        try{
            Client client = clientService.createClientForLawyer(idLawyer, clientDTO);
            logger.info("Cliente ingresado: {}", client.getId(), client);
            return ResponseEntity.ok(client);

        }catch(IllegalArgumentException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }catch(RuntimeException ex){
            //Manejo de error interno (500 Internal server error)
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }catch(Exception ex){
            return ResponseEntity.internalServerError().body("Error inesperado al obtener clientes");
        }
        
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<?> updateClient(@PathVariable String id, @RequestBody ClientDTO clientDTO) {
        try{
            Client client = clientService.updateClient(id, clientDTO);
            logger.info("Cliente ingresado: {}", client.getId(), client);
            return ResponseEntity.ok(client);
        }catch(IllegalArgumentException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }catch(RuntimeException ex){
            //Manejo de error interno (500 Internal server error)
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }catch(Exception ex){
            return ResponseEntity.internalServerError().body("Error inesperado al actualizar cliente");
        }
        
    }

    @GetMapping("/user/{idUser}/status")
    public ResponseEntity<?> getClientsByUserAndStatus(@PathVariable String idUser, @RequestParam Status status){
        try{
            logger.info("Buscando clientes para Usuario: {} con estado: {}", idUser, status);
            List<ClientDTO> clients = clientService.findByUserIdAndStatus(idUser, status);
            logger.info("Clientes encontrados: {}", clients.size());
            return ResponseEntity.ok(clients);
        }catch(IllegalArgumentException ex){
            // Manejo de error de validación (400 Bad Request)
            return ResponseEntity.badRequest().body(ex.getMessage());
        }catch(RuntimeException ex){
            // Manejo de error interno (500 Internal Server Error)
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }catch(Exception ex){
            return ResponseEntity.internalServerError().body("Error inesperado al obtener clientes");
        }
    }

    @PutMapping("/update-status/{idClient}")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<?> updateStatusClient(@PathVariable String idClient, @RequestParam Status status) {
        try{
            Client client = clientService.updateStatus(idClient, status);
            logger.info("Estado de cliente actualizado: {}", client.getId(), client);
            return ResponseEntity.ok(client);
        }catch(IllegalArgumentException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }catch(RuntimeException ex){
            //Manejo de error interno (500 Internal server error)
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }catch(Exception ex){
            return ResponseEntity.internalServerError().body("Error inesperado al actualizar estado de cliente");
        }
        
    }
    @DeleteMapping("/delete/{idClient}")
    @PreAuthorize("hasRole('LAWYER')")
        public ResponseEntity<?> deleteClient(@PathVariable String idClient) {
            try {
                logger.info("Eliminando cliente con ID: {}", idClient);
                clientService.deleteClient(idClient);
                logger.info("Cliente eliminado exitosamente - ID: {}", idClient);
                
                return ResponseEntity.noContent().build();
                
            } catch (IllegalArgumentException ex) {
                logger.warn("Error de validación al eliminar cliente: {}", ex.getMessage());
                return ResponseEntity.badRequest().body(ex.getMessage());
                
            } catch (Exception ex) {
                logger.error("Error inesperado al eliminar cliente: {}", ex.getMessage(), ex);
                return ResponseEntity.internalServerError().body("Error interno del servidor");
            }
        }

    @DeleteMapping("/delete-definitive/{idClient}/user/{idUser}")
    @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<?> deleteClientDefinitively(@PathVariable String idClient, @PathVariable String idUser, @RequestBody String password) {
            try {
                logger.info("Eliminando definitivamente cliente con ID: {}", idClient);
                clientService.deleteClientDefinitively(idClient, idUser, password);
                
                return ResponseEntity.ok("Cliente eliminado definitivamente - ID: " + idClient);
                
            } catch (IllegalArgumentException ex) {
                logger.warn("Error de validación al eliminar definitivamente cliente: {}", ex.getMessage());
                return ResponseEntity.badRequest().body(ex.getMessage());
                
            }catch (Exception ex) {
                logger.error("Error inesperado al eliminar definitivamente cliente: {}", ex.getMessage(), ex);
                return ResponseEntity.internalServerError().body("Error interno del servidor");
            }
        }

    
}
