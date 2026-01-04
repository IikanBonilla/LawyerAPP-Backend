package Development.Services;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Development.Model.LawFirm;
import Development.Model.LawyerProfile;
import Development.Model.Role;
import Development.Model.User;
import Development.Model.UserStatus;
import Development.Repository.LawFirmRepository;
import Development.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AdminManagementServices implements IAdminManagementServices{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LawFirmRepository lawFirmRepository;
    @Override
    public void suspendAdmin(String idAdmin) {
        User admin = userRepository.findById(idAdmin)
        .orElseThrow(() -> new EntityNotFoundException("Admin no encontrado con id: " + idAdmin)
        );

        if(admin.getRole() != Role.ADMIN){
            throw new RuntimeException("El usuario no es administrador");
        }

        admin.setUserStatus(UserStatus.SUSPENDED);
        userRepository.save(admin);
        
        LawFirm lawFirm = lawFirmRepository.findByIdAdminId(idAdmin)
        .orElseThrow(() -> new RuntimeException("Firma no encontrada"));

        lawFirm.getLawyers().forEach(lawyer ->{
            lawyer.getIdUser().setUserStatus(UserStatus.SUSPENDED);
        });

        userRepository.saveAll(
            lawFirm.getLawyers().stream()
                .map(LawyerProfile::getIdUser)
                .collect(Collectors.toList())
        );

    }

    @Override
    public void activateAdmin(String idAdmin) {
        User admin = userRepository.findById(idAdmin)
        .orElseThrow(() -> new RuntimeException("Admin no encontrado"));
        
        admin.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(admin);

        // Reactivar todos los lawyers de la firma
        LawFirm lawFirm = lawFirmRepository.findByIdAdminId(idAdmin)
            .orElseThrow(() -> new RuntimeException("Firma no encontrada"));
        
        lawFirm.getLawyers().forEach(lawyer -> {
            lawyer.getIdUser().setUserStatus(UserStatus.ACTIVE);
        });
        
        userRepository.saveAll(
            lawFirm.getLawyers().stream()
                .map(LawyerProfile::getIdUser)
                .collect(Collectors.toList())
        );
    }

    @Override
    public void inactivateAdmin(String idAdmin) {
        User admin = userRepository.findById(idAdmin)
        .orElseThrow(() -> new RuntimeException("Admin no encontrado"));
        
        admin.setUserStatus(UserStatus.INACTIVE);
        userRepository.save(admin);
    }

    @Override
    public Map<String,Object> getAdminStatus(String idAdmin) {
          User admin = userRepository.findById(idAdmin)
            .orElseThrow(() -> new RuntimeException("Admin no encontrado"));
        
        LawFirm lawFirm = lawFirmRepository.findByIdAdminId(idAdmin)
            .orElseThrow(() -> new RuntimeException("Firma no encontrada"));
        
        return Map.of(
            "adminId", admin.getId(),
            "username", admin.getUsername(),
            "status", admin.getUserStatus(),
            "firmName", lawFirm != null ? lawFirm.getFirmName() : "No asignada",
            "activeLawyers", lawFirm != null ? 
                lawFirm.getLawyers().stream()
                    .filter(l -> l.getIdUser().getUserStatus() == UserStatus.ACTIVE)
                    .count() : 0,
            "totalLawyers", lawFirm != null ? lawFirm.getLawyers().size() : 0
        );
    }

    
}