package Development.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import Development.DTOs.CreateAudienceDTO;
import Development.DTOs.GetAudienceDTO;
import Development.DTOs.UpdateAudienceDTO;
import Development.Model.Audience;
import Development.Model.Status;
import Development.Services.AudienceServices;
import jakarta.persistence.*;

import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

@RestController
@RequestMapping("/api/audience")
public class AudienceController {
    
    private final Logger logger = LoggerFactory.getLogger(AudienceController.class);
    
    @Autowired
    private AudienceServices audienceServices;

    // ✅ CREATE - Crear nueva audiencia
    @PostMapping("/save/{idLawyer}/client/{idClient}")
    @PreAuthorize("hasRole('LAWYER') and @LawyerStatusChecker.isActive()")
    public ResponseEntity<?> createAudience(@PathVariable String idLawyer, @PathVariable String idClient, @RequestBody CreateAudienceDTO audienceDTO) {
        try {
            logger.info("Creando nueva audiencia para Cliente: {}", idClient);
            
            Audience audience = audienceServices.saveForClient(idLawyer, idClient, audienceDTO);
            
            logger.info("Audiencia creada exitosamente - ID: {}, Cliente: {}", 
                       audience.getId(), idClient);
            
            return ResponseEntity.ok(audience);
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Recurso no encontrado: {}", ex.getMessage());
            return ResponseEntity.notFound().build();
            
        } catch (Exception ex) {
            logger.error("Error inesperado creando audiencia: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al crear audiencia");
        }
    }

    // ✅ GET BY ID - Obtener audiencia por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getAudienceById(@PathVariable String id) {
        try {
            logger.info("Buscando audiencia ID: {}", id);
            
            GetAudienceDTO audience = audienceServices.findById(id);
            
            logger.info("Audiencia encontrada - ID: {}", id);
            return ResponseEntity.ok(audience);
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Audiencia no encontrada: {}", id);
            return ResponseEntity.notFound().build();
            
        } catch (Exception ex) {
            logger.error("Error obteniendo audiencia: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al obtener audiencia");
        }
    }

    // ✅ UPDATE - Actualizar audiencia
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('LAWYER') and @LawyerStatusChecker.isActive()")
    public ResponseEntity<?> updateAudience(
            @PathVariable String id,
            @RequestBody UpdateAudienceDTO updateDTO) {
        try {
            logger.info("Actualizando audiencia ID: {}", id);
            
            Audience audience = audienceServices.update(id, updateDTO);
            
            logger.info("Audiencia actualizada exitosamente - ID: {}", id);
            return ResponseEntity.ok(audience);
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Audiencia no encontrada: {}", id);
            return ResponseEntity.notFound().build();
            
        } catch (Exception ex) {
            logger.error("Error actualizando audiencia: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al actualizar audiencia");
        }
    }

    // ✅ DELETE - Eliminar audiencia
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('LAWYER') and @LawyerStatusChecker.isActive()")
    public ResponseEntity<?> deleteAudience(@PathVariable String id) {
        try {
            logger.info("Eliminando audiencia ID: {}", id);
            
            audienceServices.delete(id);
            
            logger.info("Audiencia eliminada exitosamente - ID: {}", id);
            return ResponseEntity.ok("Audiencia eliminada exitosamente");
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Audiencia no encontrada: {}", id);
            return ResponseEntity.notFound().build();
            
        } catch (Exception ex) {
            logger.error("Error eliminando audiencia: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al eliminar audiencia");
        }
    }

    // GET BY CLIENT - Obtener audiencias por cliente
    @GetMapping("/client/{idClient}")
    public ResponseEntity<?> getAudiencesByClient(@PathVariable String idClient) {
        try {
            logger.info("Buscando audiencias para cliente ID: {}", idClient);
            
            List<GetAudienceDTO> audiences = audienceServices.findByClient(idClient);
            
            logger.info("Encontradas {} audiencias para cliente {}", audiences.size(), idClient);
            return ResponseEntity.ok(audiences);
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Cliente no encontrado: {}", idClient);
            return ResponseEntity.notFound().build();
            
        } catch (RuntimeException ex) {
            logger.error("Error del servicio: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(ex.getMessage());
            
        } catch (Exception ex) {
            logger.error("Error inesperado obteniendo audiencias: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al obtener audiencias");
        }
    }

    // ✅ GET BY USER - Obtener audiencias por usuario
    @GetMapping("/user/{idUser}")
    public ResponseEntity<?> getAudiencesByUser(@PathVariable String idUser) {
        try {
            logger.info("Buscando audiencias para Usuario ID: {}", idUser);
            
            List<GetAudienceDTO> audiences = audienceServices.findByUser(idUser);
            
            logger.info("Encontradas {} audiencias para usuario {}", audiences.size(), idUser);
            return ResponseEntity.ok(audiences);
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Usuario no encontrado: {}", idUser);
            return ResponseEntity.notFound().build();
            
        } catch (RuntimeException ex) {
            logger.error("Error del servicio: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(ex.getMessage());
            
        } catch (Exception ex) {
            logger.error("Error inesperado obteniendo audiencias: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al obtener audiencias");
        }
    }

    // ✅ GET BY STATUS - Obtener audiencias por estado
    @GetMapping("user/{idUser}/status/{status}")
    public ResponseEntity<?> getAudiencesByStatus(@PathVariable String idUser, @PathVariable Status status) {
        try {
            logger.info("Buscando audiencias con estado: {}", status);
            
            List<GetAudienceDTO> audiences = audienceServices.findByStatus(idUser, status);
            
            logger.info("Encontradas {} audiencias con estado {}", audiences.size(), status);
            return ResponseEntity.ok(audiences);
            
        } catch (Exception ex) {
            logger.error("Error obteniendo audiencias por estado: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al obtener audiencias");
        }
    }

    // PATCH STATUS - Cambiar estado de audiencia
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('LAWYER') and @LawyerStatusChecker.isActive()")
    public ResponseEntity<?> updateAudienceStatus(
            @PathVariable String id,
            @RequestParam Status status) {
        try {
            logger.info("Cambiando estado de audiencia {} a: {}", id, status);
            
            UpdateAudienceDTO updateDTO = new UpdateAudienceDTO();
            updateDTO.setStatus(status);
            
            Audience audience = audienceServices.update(id, updateDTO);
            
            logger.info("Estado de audiencia cambiado exitosamente - ID: {}, Nuevo estado: {}", id, status);
            return ResponseEntity.ok(audience);
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Audiencia no encontrada: {}", id);
            return ResponseEntity.notFound().build();
            
        } catch (Exception ex) {
            logger.error("Error cambiando estado de audiencia: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al cambiar estado");
        }
    }
}