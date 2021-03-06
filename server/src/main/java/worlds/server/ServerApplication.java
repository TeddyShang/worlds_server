package worlds.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ServerApplication {

  public static void main(String... args) {
    SpringApplication.run(ServerApplication.class, args);
  }
  @Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/bookings").allowedOrigins("http://localhost:3000");
				registry.addMapping("/bookings/**").allowedOrigins("http://localhost:3000").allowedMethods("PUT", "GET", "DELETE");
				registry.addMapping("/userprofiles").allowedOrigins("http://localhost:3000");
				registry.addMapping("/userprofiles/**").allowedOrigins("http://localhost:3000").allowedMethods("PUT", "GET", "DELETE");
				registry.addMapping("/users").allowedOrigins("http://localhost:3000");
				registry.addMapping("/users/**").allowedOrigins("http://localhost:3000").allowedMethods("PUT", "GET", "DELETE");;
				registry.addMapping("/login").allowedOrigins("http://localhost:3000");
				registry.addMapping("/mediametadatas").allowedOrigins("http://localhost:3000");
				registry.addMapping("/mediametadatas/**").allowedOrigins("http://localhost:3000").allowedMethods("PUT", "GET", "DELETE");
			}
		};
	}
}