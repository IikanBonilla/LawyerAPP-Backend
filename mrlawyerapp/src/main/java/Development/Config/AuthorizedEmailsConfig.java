package Development.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthorizedEmailsConfig {
 
    private final List<String> authorizedEmails;

    public AuthorizedEmailsConfig(@Value("${app.authorized.emails}") String emails) {
        this.authorizedEmails = Arrays.asList(emails.split(","));
    }

    public boolean isEmailAuthorized(String email) {
        return authorizedEmails.contains(email.toLowerCase().trim());
    }

    public List<String> getAuthorizedEmails() {
        return authorizedEmails;
    }
}
