# Text Social Media

## Description
The Text Social Media is the API of a text-based social media that allows users to
register, login, post text, comment on posts, follow other users, and view different types of posts and
comments. All posts are text only.


## Features

#### 1. User Registration, Login and User Roles

Users can register securely in the system with email as username and a password. The email address is validated 
and the password must be at least 8 characters long. The authentication mechanism is Json Web Token which maintains
stateless sessions when a user logins. Users can be of types, Free and Premium. The option is done during registration.

#### 2. Posts and Comments

The distinction between a Free and a Premium user is that a premium user can create posts with character limit of 3000 
while a Free user is limited to 1000 character posts. Another difference is that Free users can comment up to 5 times per post 
while Premium users comment unlimited. The implementation of the above feature utilizes the JWT token provided at every request 
to extract the user's role and identity.

#### 3. Following and followers

The users can follow each other to be able to see their posts. The follow relationship is stored in a dedicated database table.
There is also the option for unfollowing for every user.

#### 4. Views

All users regardless of their type can retrieve the same data from the system. The currently supported data view methods are:
- View all follower posts sorted by the latest (sorted by reverse chronological order)
- View their own posts. including the latest 100 comments (sorted by reverse chronological order).
- View all comments on their own posts.
- View the latest comments on their posts or any follower post.
- View the list of followers for every user in the system.
- Search for the available users to follow.

#### 5. Shareable Link

Every user can create a link associated with an original post, including the latest 100 comments. This link allows
everyone who has it to see the post and the latest 100 comments at this moment. 
Note: The post associated with the link must belong to the user who creates the link. 
To prevent data leaks through url manipulation each generated link is stored in database and is validated before the data retrieval.

## REST API Documentation

Click [here](https://documenter.getpostman.com/view/29541731/2s9YRB2rbN) to view the documentation with examples.

## Tech Stack

The API was developed using the technologies bellow:
- Java SE 17
- Maven
- Javalin Java Web Framework
- PostgreSQL 15 for main system and testing
- JDBC for database interaction in pure SQL
- JUnit 5

The project dependencies also include:
- JWT
- Spring Security for the BCrypt password encoder
- Tiny Validator for the email and password validation

## How to install and Run

1. Create the TextSocial and Text Social Test databases in Postgres (e.g. pgAdmin, psql) 
2. Run the schema creation script on both of them. (schema_initialize.sql)
3. Populate the tables of TextSocial database using the data script (data.sql) through postgres restore.
   (The test database does not need population)
4. Build the Maven project
5. Start the application web server by running the main method in the App.java file.

The passwords for all users is "1234". The requests can be sent with any client such as Postman. 
For the complete documentation of the rest API click [here](https://documenter.getpostman.com/view/29541731/2s9YRB2rbN).

