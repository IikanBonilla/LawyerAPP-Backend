package Development.Config;

import Development.DTOs.GetLawyerDTO;
import Development.Model.LawyerProfile;
import Development.Model.Role;
import Development.Model.Status;
import Development.Services.LawyerServices;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LawyerStatusChecker {

    private final LawyerServices lawyerService;

    public boolean isActive() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        
        // Verificar si el usuario es abogado
        Object details = auth.getDetails();
        if (details instanceof Map) {
            Map<?, ?> detailsMap = (Map<?, ?>) details;
            String role = (String) detailsMap.get("role");
            
            // Solo verificar para abogados
            if (!Role.LAWYER.name().equals(role)) {
                return true; // Si no es abogado, siempre permitir
            }
            
            String idUser = (String) detailsMap.get("idUser");
            if (idUser != null) {
                GetLawyerDTO lawyer = lawyerService.getLawyerByIdUser(idUser);
                return lawyer != null && lawyer.getStatus() == Status.ACTIVE;
            }
        }
        
        // Si no hay detalles o no es abogado, verificar por username
        String username = auth.getName();
        LawyerProfile lawyer = lawyerService.findLawyerByUsername(username);
        return lawyer != null && lawyer.getStatus() == Status.ACTIVE;
    }
    
    public Status getLawyerStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Status.INACTIVE;
        }
        
        String username = auth.getName();
        LawyerProfile lawyer = lawyerService.findLawyerByUsername(username);
        return lawyer != null ? lawyer.getStatus() : Status.INACTIVE;
    }
    
    public LawyerProfile getCurrentLawyer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        
        String username = auth.getName();
        return lawyerService.findLawyerByUsername(username);
    }
    
    public boolean isLawyer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        
        Object details = auth.getDetails();
        if (details instanceof Map) {
            Map<?, ?> detailsMap = (Map<?, ?>) details;
            String role = (String) detailsMap.get("role");
            return Role.LAWYER.name().equals(role);
        }
        
        // Fallback: verificar por username
        String username = auth.getName();
        LawyerProfile lawyer = lawyerService.findLawyerByUsername(username);
        return lawyer != null;
    }
}