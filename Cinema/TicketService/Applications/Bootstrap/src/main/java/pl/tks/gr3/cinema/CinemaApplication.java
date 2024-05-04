package pl.tks.gr3.cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * @SpringBootApplication is an annotation that tells Spring Boot that this class is a Spring Boot application.
 *
 * Basically, it enables three features:
 *
 * @EnableAutoConfiguration	- enables Spring Boot auto-configuration mechanism
 * @ComponentScan			- enable @Component scan on the package where application is located
 * @Configuration			- this allows to register extra beans in the context or import additional configuration classes
 */

@SpringBootApplication(scanBasePackages = "pl.tks.gr3")
public class CinemaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinemaApplication.class, args);
	}
}
