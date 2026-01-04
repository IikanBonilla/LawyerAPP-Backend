package Development.Controller;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Development.DTOs.CreateProceedingDTO;
import Development.DTOs.GetProceedingDTO;
import Development.Model.Proceeding;
import Development.Services.ProceedingServices;
import jakarta.persistence.EntityNotFoundException;


//Controller
@RestController
//url + localhost
@RequestMapping("api/proceeding")

public class ProceedingController {
       
    private final Logger logger = LoggerFactory.getLogger(ProceedingController.class);
    
    @Autowired
    private ProceedingServices proceedingServices;

    // ✅ CREATE - Crear nueva actuación
    @PostMapping("/process/{idProcess}/save")
    @PreAuthorize("hasRole('LAWYER') and @LawyerStatusChecker.isActive()")
    public ResponseEntity<?> createProceeding(@PathVariable String idProcess, @RequestBody CreateProceedingDTO proceedingDTO) {
        try {
            logger.info("Creando nueva actuación para proceso: {}", idProcess);
            
            Proceeding proceeding = proceedingServices.createProceedingForProcess(idProcess, proceedingDTO);
            
            logger.info("Actuación creada exitosamente - ID: {}, Proceso: {}", 
                       proceeding.getId(), idProcess);
            
            return ResponseEntity.ok(proceeding);
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Proceso no encontrado: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
            
        } catch (Exception ex) {
            logger.error("Error inesperado creando actuación: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al crear actuación");
        }
    }

    // ✅ GET BY ID - Obtener actuación por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProceedingById(@PathVariable String id) {
        try {
            logger.info("Buscando actuación ID: {}", id);
            
            Proceeding proceeding = proceedingServices.findById(id);
            
            logger.info("Actuación encontrada - ID: {}", id);
            return ResponseEntity.ok(proceeding);
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Actuación no encontrada: {}", id);
            return ResponseEntity.notFound().build();
            
        } catch (Exception ex) {
            logger.error("Error obteniendo actuación: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al obtener actuación");
        }
    }

    // DELETE - Eliminar actuación
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('LAWYER') and @LawyerStatusChecker.isActive()")
    public ResponseEntity<?> deleteProceeding(@PathVariable String id) {
        try {
            logger.info("Eliminando actuación ID: {}", id);
            
            proceedingServices.delete(id);
            
            logger.info("Actuación eliminada exitosamente - ID: {}", id);
            return ResponseEntity.ok("Actuación eliminada exitosamente");
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Actuación no encontrada: {}", id);
            return ResponseEntity.notFound().build();
            
        } catch (Exception ex) {
            logger.error("Error eliminando actuación: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al eliminar actuación");
        }
    }

    // GET BY PROCESS - Obtener actuaciones por proceso
    @GetMapping("/process/{idProcess}")
    public ResponseEntity<?> getProceedingsByProcess(@PathVariable String idProcess) {
        try {
            logger.info("Buscando actuaciones para proceso ID: {}", idProcess);
            
            List<GetProceedingDTO> proceedings = proceedingServices.findByProcess(idProcess);
            
            logger.info("Encontradas {} actuaciones para proceso {}", proceedings.size(), idProcess);
            return ResponseEntity.ok(proceedings);
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Proceso no encontrado: {}", idProcess);
            return ResponseEntity.notFound().build();
            
        } catch (RuntimeException ex) {
            logger.error("Error del servicio: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(ex.getMessage());
            
        } catch (Exception ex) {
            logger.error("Error inesperado obteniendo actuaciones: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al obtener actuaciones");
        }
    }

}
