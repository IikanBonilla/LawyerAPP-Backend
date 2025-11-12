package Development.Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import Development.DTOs.CreateClientDTO;
import Development.DTOs.GetClientDTO;
import Development.DTOs.UpdateClientDTO;
//import org.springframework.web.bind.annotation.PutMapping;
import Development.Model.Client;

import Development.Services.ClientServices;


import org.springframework.web.bind.annotation.PutMapping;




//Controller
@RestController
//url + localhost
@RequestMapping("api/client")
//Direccion de angular
@CrossOrigin(origins = "*")
public class ClientController {
    private Logger logger = LoggerFactory.getLogger(Client.class);
    @Autowired
    private ClientServices clientService;



    @GetMapping("/user/{idUser}")
    public ResponseEntity<?> getClientsByUserId(@PathVariable String idUser) {
        try{
            logger.info("Buscando clientes para Usuario: {}", idUser);
            List<GetClientDTO> clients = clientService.findByUserId(idUser);
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
            Client client = clientService.findById(id);
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
    public ResponseEntity<?> saveClient(@PathVariable String idLawyer,@RequestBody CreateClientDTO clientDTO) {
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
    public ResponseEntity<?> updateClient(@PathVariable String id, @RequestBody UpdateClientDTO clientDTO) {
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

    @DeleteMapping("/delete/{clientId}")
        public ResponseEntity<?> deleteClient(@PathVariable String clientId) {
            try {
                logger.info("Eliminando cliente con ID: {}", clientId);
                clientService.deleteClient(clientId);
                logger.info("Cliente eliminado exitosamente - ID: {}", clientId);
                
                return ResponseEntity.ok("Cliente eliminado exitosamente");
                
            } catch (IllegalArgumentException ex) {
                logger.warn("Error de validación al eliminar cliente: {}", ex.getMessage());
                return ResponseEntity.badRequest().body(ex.getMessage());
                
            } catch (Exception ex) {
                logger.error("Error inesperado al eliminar cliente: {}", ex.getMessage(), ex);
                return ResponseEntity.internalServerError().body("Error interno del servidor");
            }
        }



    
}
