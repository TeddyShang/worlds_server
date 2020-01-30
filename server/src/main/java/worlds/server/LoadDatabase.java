package worlds.server;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;




@Configuration
@Slf4j
class LoadDatabase {

  @Bean
  CommandLineRunner initDatabase(UserRepository userRepository, BookingRepository bookingRepository, MediaMetaDataRepository mediaMetaDataRepository, UserProfileRepository userProfileRepository) {
    User user1 = new User("Bilbo", "Baggins", UserType.CREATOR);
    User user2 = new User("Frodo", "Baggins", UserType.REALTOR);
    String[] details1 = {"Eye of Sauron, 2, HDR pictures", "Fort, 2, drone videos"};
    String[] details2 = {"The Lake, 20, drone videos","Sam's House, 3, HDR Photos"};
    Booking booking1 = new Booking("testId1", "12.1.1", "Mordor", details1);
    Booking booking2 = new Booking("testId2", "1.2.3.4", "The Shire", details2);
    MediaMetaData meta1 = new MediaMetaData("testid1","Sam's House", "youtube.com");
    MediaMetaData meta2 = new MediaMetaData("testid23","Mordor", "imgur.com");
    UserProfile profile1 = new UserProfile();
    UserProfile profile2 = new UserProfile();
    UserProfile userProfile1 =  userProfileRepository.save(profile1);
    UserProfile userProfile2 =  userProfileRepository.save(profile2);
    user1.setProfileId(userProfile1.getId());
    user2.setProfileId(userProfile2.getId());
    userRepository.save(user1);
    userRepository.save(user2);

    return args -> {

      log.info("Preloading " + bookingRepository.save(booking1));
      log.info("Preloading " + bookingRepository.save(booking2));
      log.info("Preloading " + mediaMetaDataRepository.save(meta1));
      log.info("Preloading " + mediaMetaDataRepository.save(meta2));

    };
  }
}