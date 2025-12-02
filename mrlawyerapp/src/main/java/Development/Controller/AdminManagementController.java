package Development.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Development.Services.AdminManagementServices;

@RestController
@RequestMapping("api/admin-management")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminManagementController {
    @Autowired
    private AdminManagementServices adminManagementService;

    @PostMapping("/suspend/{adminId}")
    public ResponseEntity<?> suspendAdmin(@PathVariable String adminId) {
        try {
            adminManagementService.suspendAdmin(adminId);
            return ResponseEntity.ok(Map.of(
                "message", "Admin y sus lawyers han sido suspendidos",
                "adminId", adminId
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/activate/{adminId}")
    public ResponseEntity<?> activateAdmin(@PathVariable String adminId) {
        try {
            adminManagementService.activateAdmin(adminId);
            return ResponseEntity.ok(Map.of(
                "message", "Admin y sus lawyers han sido reactivados",
                "adminId", adminId
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status/{adminId}")
    public ResponseEntity<?> getAdminStatus(@PathVariable String adminId) {
        try {
            Map<String, Object> status = adminManagementService.getAdminStatus(adminId);
            return ResponseEntity.ok(status);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
