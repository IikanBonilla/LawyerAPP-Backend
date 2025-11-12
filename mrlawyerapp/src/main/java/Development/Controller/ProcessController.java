package Development.Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import Development.DTOs.CreateProcessDTO;
import Development.DTOs.GetClientFullNameDTO;
import Development.DTOs.GetProcessDTO;
import Development.DTOs.GetProcessIdentificationDTO;
import Development.DTOs.UpdateProcessDTO;
import Development.Model.Process;
import Development.Services.ClientServices;
import Development.Services.ProcessServices;
import jakarta.persistence.EntityNotFoundException;



//Controller
@RestController
//url + localhost
@RequestMapping("/api/process")
//Direccion de angular
@CrossOrigin(origins = "*")
public class ProcessController {
    private Logger logger = LoggerFactory.getLogger(Process.class);
    @Autowired
    private ProcessServices processService;
    @Autowired 
    private ClientServices clientService;
    
     @GetMapping("/radicado/{identification}")
    public ResponseEntity<?> getProcessByIdentification(@PathVariable String identification) {
        try {
            logger.info("Buscando proceso por radicado: {}", identification);
            GetProcessDTO process = processService.findProcessByIdentification(identification);
            logger.info("Proceso encontrado - Radicado: {}", identification);
            return ResponseEntity.ok(process);
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Proceso no encontrado con radicado: {}", identification);
            return ResponseEntity.notFound().build();
            
        } catch (Exception ex) {
            logger.error("Error inesperado obteniendo proceso: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al obtener proceso");
        }
    }
     // CREATE - Crear proceso para un cliente
    @PostMapping("/client/{idClient}")
    public ResponseEntity<?> createProcessForClient(
            @PathVariable String idClient,
            @RequestBody CreateProcessDTO processDTO) {
        try {
            logger.info("Creando proceso para cliente ID: {}", idClient);
            Process process = processService.createProcessForClient(idClient, processDTO);
            logger.info("Proceso creado exitosamente - ID: {}", process.getId());
            return ResponseEntity.ok(process);
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Recurso no encontrado: {}", ex.getMessage());
            return ResponseEntity.notFound().build();
            
        } catch (IllegalStateException ex) {
            logger.warn("Error de negocio: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
            
        } catch (Exception ex) {
            logger.error("Error inesperado creando proceso: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al crear proceso");
        }
    }
    //Obtener radicados por cliente
    @GetMapping("/client/{idClient}/radicados")
    public ResponseEntity<?> getRadicadosByClientId(@PathVariable String idClient) {
        try {
            logger.info("Buscando radicados para cliente ID: {}", idClient);
            List<GetProcessIdentificationDTO> radicados = processService.radicadoByClientId(idClient);
            logger.info("Radicados encontrados: {}", radicados.size());
            return ResponseEntity.ok(radicados);
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Cliente no encontrado: {}", idClient);
            return ResponseEntity.notFound().build();
            
        } catch (RuntimeException ex) {
            logger.error("Error del servicio: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(ex.getMessage());
            
        } catch (Exception ex) {
            logger.error("Error inesperado obteniendo radicados: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al obtener radicados");
        }
    }


    //Asociar documento a proceso
    @PostMapping("/{idProcess}/documents/{idDocument}")
    public ResponseEntity<?> associateDocumentToProcess(
            @PathVariable String idProcess,
            @PathVariable String idDocument) {
        try {
            logger.info("Asociando documento {} a proceso {}", idDocument, idProcess);
            boolean success = processService.associateDocumentToProcess(idProcess, idDocument);
            
            if (success) {
                logger.info("Documento asociado exitosamente");
                return ResponseEntity.ok("Documento asociado al proceso exitosamente");
            } else {
                return ResponseEntity.badRequest().body("No se pudo asociar el documento");
            }
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Recurso no encontrado: {}", ex.getMessage());
            return ResponseEntity.notFound().build();
            
        } catch (RuntimeException ex) {
            logger.warn("Error de negocio: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
            
        } catch (Exception ex) {
            logger.error("Error inesperado asociando documento: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al asociar documento");
        }
    }

    // UPDATE - Actualizar proceso
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProcess(
            @PathVariable String id,
            @RequestBody UpdateProcessDTO processDTO) {
        try {
            logger.info("Actualizando proceso ID: {}", id);
            Process process = processService.updateProcess(id, processDTO);
            logger.info("Proceso actualizado exitosamente - ID: {}", id);
            return ResponseEntity.ok(process);
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Proceso no encontrado: {}", id);
            return ResponseEntity.notFound().build();
            
        } catch (IllegalStateException | IllegalArgumentException ex) {
            logger.warn("Error de validación: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
            
        } catch (Exception ex) {
            logger.error("Error inesperado actualizando proceso: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al actualizar proceso");
        }
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeProcessStatus(
            @PathVariable String id,
            @RequestParam String status) {
        try {
            logger.info("Cambiando estado del proceso {} a: {}", id, status);
            Process process = processService.changeProcessStatus(id, status);
            logger.info("Estado cambiado exitosamente - Proceso: {}, Nuevo estado: {}", id, status);
            return ResponseEntity.ok(process);
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Proceso no encontrado: {}", id);
            return ResponseEntity.notFound().build();
            
        } catch (IllegalArgumentException ex) {
            logger.warn("Estado inválido: {}", status);
            return ResponseEntity.badRequest().body(ex.getMessage());
            
        } catch (Exception ex) {
            logger.error("Error inesperado cambiando estado: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al cambiar estado");
        }
    }

    // DELETE - Eliminar proceso
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProcess(@PathVariable String id) {
        try {
            logger.info("Eliminando proceso ID: {}", id);
            processService.deleteProcess(id);
            logger.info("Proceso eliminado exitosamente - ID: {}", id);
            return ResponseEntity.ok("Proceso eliminado exitosamente");
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Proceso no encontrado: {}", id);
            return ResponseEntity.notFound().build();
            
        } catch (Exception ex) {
            logger.error("Error inesperado eliminando proceso: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al eliminar proceso");
        }
    }

    //

    @GetMapping("/{idProcess}/clients")
    public ResponseEntity<?> getClientsByProcessId(@PathVariable String idProcess) {
        try{
            logger.info("Buscando clientes para Proceso: {}" );
            List<GetClientFullNameDTO> clients = clientService.findByProcessId(idProcess);
            logger.info("Clientes encontrados: {}", clients.size());
            return ResponseEntity.ok(clients);
        }catch(IllegalArgumentException ex){
        //Manejo de error de validacion (400 bad request)
         return ResponseEntity.badRequest().body(ex.getMessage());
        }catch(RuntimeException ex){
            //Manejo de error interno (500 Internal server error)
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }catch(Exception ex){
            return ResponseEntity.internalServerError().body("Error inesperado al obtener clientes");
        }
    }

    @GetMapping("/lawyer/{idLawyer}/processes")
    public ResponseEntity<?> getProcessesByStatus(
        @PathVariable String idLawyer,
        @RequestParam String status) {
        
        try {
            List<GetProcessIdentificationDTO> processes = processService.findProcessByStatus(idLawyer, status);
            return ResponseEntity.ok(processes);
            
        } catch (IllegalArgumentException ex) {
            logger.warn("Error de validación: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
            
        } catch (RuntimeException ex) {
            logger.error("Error del servicio: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(ex.getMessage());
            
        } catch (Exception ex) {
            logger.error("Error inesperado: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }
    
}