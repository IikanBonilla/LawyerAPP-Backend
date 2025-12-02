package Development.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Development.DTOs.GetLawyerDTO;
import Development.Model.Status;
import Development.Services.LawyerServices;

@RestController
@RequestMapping("/api/lawyer")
@PreAuthorize("hasRole('ADMIN')")
public class LawyerController {
    
    @Autowired
    private LawyerServices lawyerService;
    @GetMapping("/firm/{idFirm}")
    public ResponseEntity<?> getLawyersByFirm(@PathVariable String idFirm) {
        try{
            List<GetLawyerDTO> lawyers = lawyerService.getLawyersByFirm(idFirm);
            return ResponseEntity.ok(lawyers);
        }catch(IllegalArgumentException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        catch(Exception ex){
            return ResponseEntity.internalServerError().body("Error inesperado al obtener abogados");
        }

    }

    // Obtener abogado por userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getLawyerByUserId(@PathVariable String userId) {
        try{
            GetLawyerDTO lawyer = lawyerService.getLawyerByIdUser(userId);
            
            if (lawyer != null) {
                return ResponseEntity.ok(lawyer);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Abogado no encontrado para el usuario ID: " + userId);
            }
            
        } catch (IllegalArgumentException ex) {
            // Manejo de error de validación (400 Bad Request)
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            // Manejo de error interno (500 Internal Server Error)
            return ResponseEntity.internalServerError()
                .body("Error inesperado al buscar abogado: " + ex.getMessage());
        }
    }

    @PutMapping("/update/{idLawyer}")
    public ResponseEntity<?> updateLawyerStatus(@PathVariable String idLawyer, @RequestParam Status status) {
        try{
            lawyerService.updateLawyerStatus(idLawyer, status);
            return ResponseEntity.ok("Estado del abogado actualizado correctamente");
        }catch(Exception ex){
            return ResponseEntity.internalServerError().body("Error inesperado al actualizar el estado del abogado");
        }
    }
    
    @DeleteMapping("/delete/{idLawyer}")
    public ResponseEntity<?> deleteLawyer(@PathVariable String idLawyer) {
        try{
            lawyerService.deleteLawyer(idLawyer);
            return ResponseEntity.ok("Abogado eliminado correctamente");
        }catch(Exception ex){
            return ResponseEntity.internalServerError().body("Error inesperado al eliminar el abogado");
        }
    }
}
