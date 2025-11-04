package Development.Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.PutMapping;
import Development.Model.Client;
import Development.Services.ClientServices;


//Controller
@RestController
//url + localhost
@RequestMapping("clientApi")
//Direccion de angular
@CrossOrigin(origins = "*")
public class ClientController {
    private Logger logger = LoggerFactory.getLogger(Client.class);
    @Autowired
    private ClientServices clientService;

    @GetMapping("/client/listall")
    public List<Client> getAllClients() {
        List<Client> clients = clientService.listClient();
        logger.info("get: {} clients found", clients.size());
        clients.forEach(client -> {logger.info("➡️ {}", client);
        });
        return clients;
    }
    @PostMapping("/client/save")
    public Client createClient(@RequestBody Client client){
        Client savedCLient = clientService.saveClient(client);
        logger.info("New Client: {}", savedCLient);
        return savedCLient;
    }
    @DeleteMapping("/client/delete")
    public void deleteClient(@RequestParam String id){
        clientService.deleteClient(id);
        logger.info("Client deleted with ID: {}", id);
    }
    // Update an existing client by ID
    /*
    @PutMapping("/client/update/{id}")
    public Client updateClient(@PathVariable String id, @RequestBody Client newClient){
        Client oldClient = clientService.searchByIdClient(id);
        if(oldClient != null){
            // Update client fields
            oldClient.setName(newClient.getName());
            oldClient.setEmail(newClient.getEmail());
            oldClient.setAddress(newClient.getAddress());
            clientService.saveClient(oldClient);
            logger.info("Client updated: {}", oldClient);            
            return oldClient;
        }
        else{
            logger.warn("Client with ID: {} not found", id);
            return null;
        }
    }
    */

}
