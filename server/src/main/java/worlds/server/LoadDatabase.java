package worlds.server;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
class LoadDatabase {

  @Bean
  CommandLineRunner initDatabase(UserRepository repository) {
    return args -> {
      log.info("Preloading " + repository.save(new User("Bilbo", "Baggins", UserType.CREATOR)));
      log.info("Preloading " + repository.save(new User("Frodo", "Baggins", UserType.REALTOR)));
    };
  }
}