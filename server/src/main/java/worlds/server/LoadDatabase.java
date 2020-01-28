package worlds.server;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;




@Configuration
@Slf4j
class LoadDatabase {

  @Bean
  CommandLineRunner initDatabase(UserRepository userRepository, BookingRepository bookingRepository) {
    String[] details1 = {"Eye of Sauron, 2, HDR pictures", "Fort, 2, drone videos"};
    String[] details2 = {"The Lake, 20, drone videos","Sam's House, 3, HDR Photos"};
    return args -> {
      log.info("Preloading " + userRepository.save(new User("Bilbo", "Baggins", UserType.CREATOR)));
      log.info("Preloading " + userRepository.save(new User("Frodo", "Baggins", UserType.REALTOR)));
      log.info("Preloading " + bookingRepository.save(new Booking("testId1", "12.1.1", "Mordor", details1)));
      log.info("Preloading " + bookingRepository.save(new Booking("testId2", "1.2.3.4", "The Shire", details2)));
    };
  }
}