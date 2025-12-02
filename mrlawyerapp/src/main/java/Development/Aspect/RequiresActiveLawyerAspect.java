package Development.Aspect;

import Development.Config.LawyerStatusChecker;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class RequiresActiveLawyerAspect {

    private final LawyerStatusChecker lawyerStatusChecker;

    @Before("@annotation(RequiresActiveLawyer)")
    public void checkActiveLawyer() {
        if (lawyerStatusChecker.isLawyer() && !lawyerStatusChecker.isActive()) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, 
                "No puede realizar esta acción porque su perfil de abogado está inactivo."
            );
        }
    }
}