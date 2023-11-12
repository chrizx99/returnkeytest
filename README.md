ReturnKey Technical Test Project
This project is a REST API built using Spring Boot and Kotlin, utilizing an H2 in-memory database. It serves as a technical test for an interview at ReturnKey.

Prerequisites
To run this project, you will need the following:
- Java Development Kit (JDK)
- Gradle
- Postman

Installation and Setup
- Clone the project repository:
  git clone https://github.com/chrizx99/returnkeytest.git
- Navigate to the project directory:
  cd returnkey-technical-test
- Install project dependencies:
  gradle build 

Running the Application
- Start the application:
  gradle bootRun

Alternative method to Install and Run the Application is to open the project via IntelliJ IDEA downloadable via https://jetbrains.com/idea/download
open the DemoApplication.kt and and run the Project

Testing with Postman
Import the Postman collection:
a. Download the Postman collection from the project's postman-collection.json file.
b. Open Postman and import the collection by dragging and dropping the .json file onto the Postman window.

Execute the Postman collection:
a. Select the imported collection and click the "Run" button.
b. Postman will execute the collection's requests, allowing you to test the API's features.
