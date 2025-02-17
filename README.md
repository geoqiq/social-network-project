> [!NOTE]
> This Java project, built with Gradle and developed in IntelliJ IDEA 2024.3.2.2, uses JavaFX for the GUI, PostgreSQL for database management and JDBC for database operations. It follows the MVC design pattern and employs the Observer pattern for UI updates. Key features include user management, encrypted passwords, message handling and friend invitations, with a focus on JAVA8 priciples, separation of concerns, exception handling, and data validation. The UI includes various JavaFX components, such as PasswordFields and ComboBox.

The user is greeted by the login window:

![login](https://github.com/geoqiq/social-network-project/blob/d464586344edc27230ec6f96df0e5a5769543a17/screenshots/login.png)
![confirmare](https://github.com/geoqiq/social-network-project/blob/d464586344edc27230ec6f96df0e5a5769543a17/screenshots/confirmare.png)

If the account is not present in the database, the user can sign up with a new account:

![signup](https://github.com/geoqiq/social-network-project/blob/d464586344edc27230ec6f96df0e5a5769543a17/screenshots/signup.png)

Once the user is logged in, it will be able to accept/reject friendship requests, invite other users to form a connection, open conversations with friends and reply to their messages, or text multiple people at once:

![userview](https://github.com/geoqiq/social-network-project/blob/d464586344edc27230ec6f96df0e5a5769543a17/screenshots/user.png)
![mesajcontact](https://github.com/geoqiq/social-network-project/blob/d464586344edc27230ec6f96df0e5a5769543a17/screenshots/text.png)
![mesajcontacte](https://github.com/geoqiq/social-network-project/blob/d464586344edc27230ec6f96df0e5a5769543a17/screenshots/maimulti.png)
![mesajlista](https://github.com/geoqiq/social-network-project/blob/d464586344edc27230ec6f96df0e5a5769543a17/screenshots/catre.png)

If the one logging in is one of the administrators of the social network (user 'admin'), they will gain access to all sorts of private information related to the network, such as conversations between 2 users or the biggest community in the social network:

![admin1](https://github.com/geoqiq/social-network-project/blob/d464586344edc27230ec6f96df0e5a5769543a17/screenshots/adminpanel1.png)
![admin2](https://github.com/geoqiq/social-network-project/blob/d464586344edc27230ec6f96df0e5a5769543a17/screenshots/adminpanel2.png)
