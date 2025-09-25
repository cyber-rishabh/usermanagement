User Management App

Description

This project is a Java Swing-based User Management Application that connects to an H2 database using JDBC. It provides a form-based interface to perform CRUD operations (Create, Read, Update, Delete) on user data.

The application demonstrates the integration of a GUI with a database backend for simple user management.


---

Features

Add User: Insert new users into the database.

View All Users: Retrieve and display all user records.

Update User: Edit and update existing user details.

Delete User: Remove user records from the database.

H2 Database Integration: Uses H2 in-memory / file-based database for storing user data.



---

Technologies Used

Java 17+ (or latest JDK)

Java Swing (for GUI)

JDBC (for database connectivity)

H2 Database (lightweight Java database)



---

Project Structure

src/
 ├── Main.java             # Entry point of the application
 ├── UserForm.java         # Swing GUI form for user management
 ├── DatabaseManager.java  # Handles JDBC connection and CRUD operations
 └── User.java             # User model class


---

Setup Instructions

1. Clone or Download the Project
Ensure all .java files are in the src folder.


2. Download H2 Database
H2 Download Link
Add the h2-*.jar to your classpath.


3. Compile the Project
Open terminal/PowerShell in src folder:

javac *.java


4. Run the Application
Make sure H2 JAR is included in classpath:

java -cp ".;path\to\h2-*.jar" Main


5. Using the Application

Add User → Enter Name + Email → Click Add.

View Users → Click View All Users.

Update User → Select user → Edit → Click Update.

Delete User → Select user → Click Delete.





---

H2 Database Configuration

JDBC URL: jdbc:h2:~/test

Username: sa

Password: (leave blank)


You can also open the H2 console at:

http://localhost:8082

to view stored user records.


---

Author

Rishabh Arora
Second-Year, 2023 Batch


---

License

This project is for educational purposes only.
