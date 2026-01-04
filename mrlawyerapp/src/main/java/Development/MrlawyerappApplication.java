package Development;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MrlawyerappApplication{



	public static void main(String[] args) {
		SpringApplication.run(MrlawyerappApplication.class, args);
	}
}
