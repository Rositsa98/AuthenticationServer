# AuthenticationServer
Final course project from Modern java technologies course, winter 2019. 

Authentication Server 

The system contains client and server part.

Server
The server must be able to serve multiple clients simultaneously.
The server must be able to log in to the system. Registration will be done with username (unique for the server), password, first name, last name, email.
User information must be stored in a file (will play the role of a database server) and the password should not be stored in plain text.
the first name, last name and email fields of the user must be editable.
The user password must be reset.
The client must be able to authenticate to the server with their username and password.
A session is an object that holds a unique identifier and time-to-live (ttl). After the time-to-live period has expired, the session is destroyed. The system must be able to create a new session with successful user name and password authentication and return a unique session id to the client as well as the session ttl.
The system must allow session id authentication when a session has been successfully created for a user.
The system must allow logout for a given session id, and the operation must destroy the corresponding session.
When logging in again to provide a user with username and password, the previously created session must be terminated and a new one created.
The system must offer a user deletion option that deletes any stored information in the database for that user, as well as terminates all sessions created for the user.
Client
The client side of the application has the ability to consume the operations offered by the server. The client must implement the following commands:

- register --username <username> --password <password> --first-name <firstName> --last-name <lastName> --email <email>

- login -–username <username> --password <password>

- login -–session-id <sessionId>

- reset-password –-username <username> --old-password <oldPassword> --new-password <newPassword>

- update-user -–session-id <session-id> -–new-username <newUsername> --new-first-name <newFirstName> --new-last-name <newLastName> --new-email <email>. All parameters except --session-id in this command are optional.

- logout –session-id <sessionId>

- delete-user –username <username>
