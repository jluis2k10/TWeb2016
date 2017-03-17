drop table IF EXISTS Accounts;
create table Accounts (
  id BIGINT NOT NULL AUTO_INCREMENT,
  User_Name varchar(20) not null,
  Email VARCHAR(100) not null,
  Active bit not null,
  Password varchar(100) not null,
  Provincia VARCHAR (25) not null,
  primary key (id),
  UNIQUE (User_Name)
);

drop table IF EXISTS Account_Roles;
create table Account_Roles (
  id BIGINT NOT NULL AUTO_INCREMENT,
  role VARCHAR(30) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (role)
);

/* Relación many-to-many entre Accounts y Account_Roles */
drop table IF EXISTS Accounts_to_Roles;
create table Accounts_to_Roles (
  account_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (account_id, role_id),
  CONSTRAINT FK_Accounts FOREIGN KEY (account_id) REFERENCES Accounts (id),
  CONSTRAINT FK_Account_Roles FOREIGN KEY (role_id) REFERENCES Account_Roles (id)
);

drop table if exists Films;
create table Films (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  description text NOT NULL,
  duration VARCHAR(3) NOT NULL,
  year VARCHAR(4) NOT NULL,
  poster VARCHAR(100) NOT NULL,
  rating VARCHAR(5),
  trailer VARCHAR(11),
  score DECIMAL(3,2) NOT NULL,
  nvotes INT NOT NULL,
  views INT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (title)
);

drop table if exists Genres;
create table Genres (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name)
);

drop table if exists Directors;
create table Directors (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name)
);

drop table if exists Actors;
create table Actors (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name)
);

drop table if exists Countries;
create table Countries (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name)
);

drop table if exists Films_to_Genres;
create table Films_to_Genres (
  film_id BIGINT NOT NULL,
  genre_id BIGINT NOT NULL,
  PRIMARY KEY (film_id, genre_id),
  CONSTRAINT F2G_FK_Films FOREIGN KEY (film_id) REFERENCES Films (id),
  CONSTRAINT F2G_FK_Genres FOREIGN KEY (genre_id) REFERENCES Genres (id)
);

drop table if exists Films_to_Directors;
create table Films_to_Directors (
  film_id BIGINT NOT NULL,
  director_id BIGINT NOT NULL,
  PRIMARY KEY (film_id, director_id),
  CONSTRAINT F2D_FK_Films FOREIGN KEY (film_id) REFERENCES Films (id),
  CONSTRAINT F2D_FK_Directors FOREIGN KEY (director_id) REFERENCES Directors (id)
);

drop table if exists Films_to_Stars;
create table Films_to_Stars (
  film_id BIGINT NOT NULL,
  actor_id BIGINT NOT NULL,
  PRIMARY KEY (film_id, actor_id),
  CONSTRAINT F2S_FK_Films FOREIGN KEY (film_id) REFERENCES Films (id),
  CONSTRAINT F2S_FK_Actors FOREIGN KEY (actor_id) REFERENCES Actors (id)
);

drop table if exists Films_to_Supportings;
create table Films_to_Supportings (
  film_id BIGINT NOT NULL,
  actor_id BIGINT NOT NULL,
  PRIMARY KEY (film_id, actor_id),
  CONSTRAINT F2SP_FK_Films FOREIGN KEY (film_id) REFERENCES Films (id),
  CONSTRAINT F2SP_FK_Actors FOREIGN KEY (actor_id) REFERENCES Actors (id)
);

drop table if exists Films_to_Countries;
create table Films_to_Countries (
  film_id BIGINT NOT NULL,
  country_id BIGINT NOT NULL,
  PRIMARY KEY (film_id, country_id),
  CONSTRAINT F2C_FK_Films FOREIGN KEY (film_id) REFERENCES Films (id),
  CONSTRAINT F2C_FK_Countries FOREIGN KEY (country_id) REFERENCES Countries (id)
);

drop table if exists Votes;
CREATE TABLE Votes (
  film_id BIGINT NOT NULL,
  account_id BIGINT NOT NULL,
  score SMALLINT NOT NULL,
  PRIMARY KEY (film_id, account_id),
  CONSTRAINT V_FK_Films FOREIGN KEY (film_id) REFERENCES Films (id),
  CONSTRAINT V_FK_Accounts FOREIGN KEY (account_id) REFERENCES Accounts (id)
);

drop table if exists Watchlist;
CREATE TABLE Watchlist (
  film_id BIGINT NOT NULL,
  account_id BIGINT NOT NULL,
  PRIMARY KEY (film_id, account_id),
  CONSTRAINT W_FK_Films FOREIGN KEY (film_id) REFERENCES Films (id),
  CONSTRAINT W_FK_Accounts FOREIGN KEY (account_id) REFERENCES Accounts (id)
);

drop table if EXISTS persistent_logins;
CREATE TABLE persistent_logins (
  username VARCHAR(64) NOT NULL,
  series VARCHAR(64) NOT NULL,
  token VARCHAR(64) NOT NULL,
  last_used TIMESTAMP NOT NULL,
  PRIMARY KEY (series)
);