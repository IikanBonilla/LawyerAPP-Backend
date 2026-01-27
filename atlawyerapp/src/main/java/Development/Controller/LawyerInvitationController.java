package Development.Controller;

import org.slf4j.LoggerFactory;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Development.DTOs.CreateLawyerInvitationDTO;
import Development.DTOs.LawyerInvitationDTO;
import Development.Model.LawyerInvitation;
import Development.Services.LawyerInvitationServices;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/invitation")
@PreAuthorize("hasRole('ADMIN')")
public class LawyerInvitationController {
    private final Logger logger = LoggerFactory.getLogger(LawyerController.class);
    @Autowired
    private LawyerInvitationServices invitationService;

    @PostMapping("/create/{idFirm}")
    public ResponseEntity<?> createInvitation(@PathVariable String idFirm, @RequestBody CreateLawyerInvitationDTO invitationDTO) {
        try{
            logger.info("Creando invitacion para admin: {}", idFirm);
            LawyerInvitation invitation = invitationService.createInvitation(idFirm, invitationDTO);
            return ResponseEntity.ok(invitation);
        }catch(IllegalArgumentException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }catch(RuntimeException ex){
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }catch(Exception ex){
            return ResponseEntity.internalServerError().body("Error inesperado al crear invitación");
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteInvitation(@PathVariable String id){
        try{
            logger.info("Eliminando cliente con id: ", id);
            invitationService.deleteInvitation(id);
            return ResponseEntity.ok("Eliminado con exito");
        }catch(EntityNotFoundException ex){
            logger.warn("Invitación no encontrada: {}", id);
            return ResponseEntity.notFound().build();
        }catch(RuntimeException ex){
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }catch(Exception ex){
            logger.error("Error al eliminar invitacion: {}",  id , ex.getMessage());
            return ResponseEntity.internalServerError().body("Error inesperado al eliminar Invitacion");
        }
    }

    @GetMapping("/firm/{idFirm}")
    public ResponseEntity<?> getInvitationByFirm(@PathVariable String idFirm) {
        try{
            logger.info("Buscando invitaciones para firma con ID: {}", idFirm);
            List<LawyerInvitationDTO> invitations = invitationService.findByIdFirm(idFirm);
            return ResponseEntity.ok(invitations);
        } catch (EntityNotFoundException ex) {
            logger.warn("Firma no encontrada con ID: {}", idFirm);
            return ResponseEntity.notFound().build();
            
        } catch (RuntimeException ex) {
            logger.error("Error del servicio: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(ex.getMessage());
            
        } catch (Exception ex) {
            logger.error("Error inesperado obteniendo invitaciones: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error interno al obtener invitaciones");
        }
    }
    

}
