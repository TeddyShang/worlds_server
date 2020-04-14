# worlds_server
## Description
* RESTful API for WorldsiVue. Uses Spring Boot and MongoDB
# Release Notes Version 1.0:
## New Features:
* Created private profile object
* API support for private profiles (GET single and PUT)
## Bug Fixes:
* Fixed issue in UserController.java where incoming PUT needed a field that was unnecessary
* Removed unnecessary file BookingRoomInfo.java
## Known Bugs:
* RealtorVerification.java potentially throws unhandled HttpStatusException or SocketTimeoutException
## Install Information:
### Prerequisites
* Some type of IDE (VS Code, Intellij IDEA, etc.)
* JDK 8 or higher (https://adoptopenjdk.net/)
* MongoDB Atlas account (https://www.mongodb.com/cloud/atlas)
* Latest version of Gradle (https://gradle.org/install/)
### Dependent Libraries
* See build.gradle
  * 'org.springframework.boot' version ‘2.1.12.RELEASE'
  * ‘io.spring.dependency-management' version '1.0.9.RELEASE'
  * 'org.springframework.boot:spring-boot-starter-data-mongodb'
  * 'org.springframework.boot:spring-boot-starter-data-rest'
  * 'org.springframework.boot:spring-boot-starter-hateoas'
  * 'org.springframework.boot:spring-boot-starter-web'
  * 'org.projectlombok:lombok'
  * 'org.jsoup:jsoup:1.13.1'
  * 'org.springframework.security' '3.1.0.RELEASE'
  * 'com.google.code.gson' '2.8.5'
### Download instructions
* Clone or download from this repository
### Build instructions
* Connecting to MongoDB Atlas
  1. Sign in to https://www.mongodb.com/cloud
  2. Create a new project and give it a name
  3. Click “Build a Cluster” and select a tier of your choosing, choose configuration options most appropriate for you
  4. Create a file “application.properties” in worlds_server/server/src/main/resources
  5. Configure the cluster once it is set up by whitelisting certain IP addresses and creating a MongoDB User
  6. Select Java version 3.7 or later and save the connection string
  7. In application.properties, enter the following line:
    * spring.data.mongodb.uri = {connectionString}
* To create  executable .jar file
  1. Navigate to worlds_server/server in terminal
  2. Enter “gradle clean”
  3. Enter “gradle bootjar”
  4. An executable .jar file should be found in worlds_server/server/build/libs
### Installation
* Place .jar file wherever
### Run instructions
* With .jar file
  * NOT RECOMMENDED
    1. Simply double click (will not see output)
  * RECOMMENDED
    1. Open terminal in same folder as .jar file
    2. Enter “java -jar filename.jar”
    3. Navigate to localhost:8080 to ensure that it works
* From IDE
  1. Run the file called “ServerApplication.java” in worlds_server/server/src/main/java/worlds/server
  2. Navigate to localhost:8080 to ensure that it works
* From terminal
  1. Navigate to worlds_server/server in terminal
  2. Enter “gradle bootrun”
  3. Navigate to localhost:8080 to ensure that it works
### Troubleshooting
* Ensure all prerequisites are installed
* On Windows make sure Gradle and Java are added to your environmental variables
  * Enter “gradle -version” and/or “java -version” in terminal to check
* Ensure application.properties has the connection string with the MongoDB username and password added to the right place in the string
