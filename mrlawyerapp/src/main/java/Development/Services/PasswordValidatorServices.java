package Development.Services;

import org.springframework.stereotype.Service;

@Service
public class PasswordValidatorServices {
    public boolean isValidPassword(String password){
        // Verifica que no sea nula y tenga al menos 8 caracteres
        if (password == null || password.length() < 8) {
            return false;
        }

        // Al menos una minúscula, una mayúscula y un número
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

        return password.matches(passwordPattern);
    }

    public String getPasswordRequirements(){
        return "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número";
    }
}
