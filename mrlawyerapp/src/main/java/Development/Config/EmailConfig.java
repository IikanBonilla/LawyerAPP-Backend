package Development.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailConfig {

    @Value("${app.email.enabled}")
    private boolean emailEnabled;

    public boolean isEmailEnabled(){
        return emailEnabled;
    }
}
