package Development.Config;

import Development.DTOs.GetLawyerDTO;
import Development.Model.*;
import Development.Services.JwtServices;
import Development.Services.UserDetailsServiceImpl;
import Development.Repository.LawyerRepository; // Importa el repositorio
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtServices jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final LawyerRepository lawyerRepository; // Cambia esto por tu repositorio

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getServletPath();
        
        // Rutas públicas que no requieren autenticación
        if (path.startsWith("/auth") && !path.equals("/auth/register-admin")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            
            if (jwtService.isTokenValid(jwt, userDetails)) {
                User user = (User) userDetails;
                
                
                // Si es abogado, verificar estado del perfil de abogado
                if (user.getRole() == Role.LAWYER) {
                    // Usa el mismo método que en AuthServices
                    GetLawyerDTO lawyer = lawyerRepository.findByIdUser(user.getId());
                    
                    if (lawyer == null) {
                        handleInvalidLawyer(response);
                        return;
                    }
                    
                    if (lawyer.getStatus() != Status.ACTIVE) {
                        handleInactiveLawyer(response, lawyer.getStatus());
                        return;
                    }
                    
                    // Agregar información del abogado a los detalles
                    Map<String, String> details = new HashMap<>();
                    details.put("idLawFirm", user.getLawFirm() != null ? user.getLawFirm().getId() : null);
                    details.put("idUser", user.getId());
                    details.put("username", username);
                    details.put("role", user.getRole().name());
                    details.put("lawyerId", lawyer.getId());
                    details.put("lawyerStatus", lawyer.getStatus().name());
                    details.put("lawyerName", lawyer.getFullName());
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(details);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    // Para otros roles, mantener la lógica original
                    String idLawFirm = jwtService.extractLawFirmId(jwt);
                    String idUser = jwtService.extractUserId(jwt);
                    
                    Map<String, String> details = new HashMap<>();
                    details.put("idLawFirm", idLawFirm);
                    details.put("idUser", idUser);
                    details.put("username", username);
                    details.put("role", user.getRole().name());
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(details);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
    
    private void handleInactiveLawyer(HttpServletResponse response, Status lawyerStatus) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        
        String message = "";
        if (lawyerStatus == Status.INACTIVE) {
            message = "Su perfil de abogado está inactivo. No puede acceder al sistema.";
        }
        
        Map<String, Object> errorResponse = Map.of(
            "error", "ABOGADO_INACTIVO",
            "message", message,
            "lawyerStatus", lawyerStatus.name(),
            "timestamp", LocalDateTime.now()
        );
        
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
    
    private void handleInvalidLawyer(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        
        Map<String, Object> errorResponse = Map.of(
            "error", "PERFIL_NO_ENCONTRADO",
            "message", "Perfil de abogado no encontrado. Contacte al administrador del sistema.",
            "timestamp", LocalDateTime.now()
        );
        
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}