package Development;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MrlawyerappApplication /*implements CommandLineRunner*/{

	// @Autowired
    //private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(MrlawyerappApplication.class, args);
	}
/*
	 @Override
    public void run(String... args) {
        // Lambda simple para contar clientes en la base de datos
        ((Runnable) () -> {
            try {
                // Primero verificar si la tabla Client existe
                Integer tableExists = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM USER_TABLES WHERE TABLE_NAME = 'CLIENT'", Integer.class);
                
                if (tableExists > 0) {
                    // Si la tabla existe, contar clientes
                    Integer clientCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM Client", Integer.class);
                    System.out.println("✅ Conexión exitosa - Clientes en BD: " + clientCount);
                } else {
                    // Si no existe la tabla, solo probar conexión
                    String user = jdbcTemplate.queryForObject("SELECT USER FROM DUAL", String.class);
                    System.out.println("✅ Conexión exitosa - Usuario: " + user + " (Tabla Client no existe aún)");
                }
                
            } catch (Exception e) {
                System.err.println("❌ Error de conexión: " + e.getMessage());
            }
        }).run();
    }
	 */
}
