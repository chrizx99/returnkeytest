**ReturnKey Technical Test Project**<br />
This project is a REST API built using Spring Boot and Kotlin, utilizing an H2 in-memory database. It serves as a technical test for an interview at ReturnKey.

**Prerequisites**
To run this project, you will need the following:
- Java Development Kit (JDK)
- Gradle
- Postman

**Installation and Setup**
1. Clone the project repository:<br />
  ```git clone https://github.com/chrizx99/returnkeytest.git```
2. Navigate to the project directory:<br />
  ```cd returnkey-technical-test```
3. Install project dependencies:<br />
  ```gradle build```

**Running the Application**<br />
- Start the application:<br />
  ```gradle bootRun```

**Alternative method to Install and Run the Application**<br />
1. Open the project in IntelliJ IDEA, which can be downloaded from https://jetbrains.com/idea/download.
2. Open the ```DemoApplication.kt``` file inside ```returnkeytest/src/main/kotlin/com/example/demo``` and run the project.

**Testing with Postman**<br />
Import the Postman collection:<br />
1. Download the Postman collection from the project's ```Christianto ReturnKey Technical Test API Documentation.postman_collection.json``` file.<br />
2. Open Postman and import the collection by dragging and dropping the .json file onto the Postman window.<br />

**Execute the Postman collection:**
1. Select the imported collection and click the "Run" button.<br />
2. Postman will execute the collection's requests, allowing you to test the API's features.<br />

The orders.csv file contains a list of orders. This file placedf in the returnkeytest/src/main/resources/data/ directory.

The database can be accessed via http://localhost:8080/console with the following login data:
- Driver Class: org.h2.Driver
- JDBC URL: jdbc:h2:mem:returns-db
- User Name: root
- root: Password: (empty)
