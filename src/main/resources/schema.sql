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

drop table if EXISTS persistent_logins;
CREATE TABLE persistent_logins (
  username VARCHAR(64) NOT NULL,
  series VARCHAR(64) NOT NULL,
  token VARCHAR(64) NOT NULL,
  last_used TIMESTAMP NOT NULL,
  PRIMARY KEY (series)
);

/*insert into Accounts (id, USER_NAME, Email, ACTIVE, PASSWORD, Provincia)
  values (1, 'usuario', 'usuario@uned.es', 1, '$2a$10$tH66ER6iETjU0xLDfuh4ie/vef7erOXql8FYrjEsuhGASixAhGNIy', 'Baleares');
insert into Accounts (id, USER_NAME, Email, ACTIVE, PASSWORD, Provincia)
  values (2,'admin', 'admin@uned.es', 1, '$2a$10$E1RIu3WFFeezHMjXFrasduaDD6FpdUU91k3DDU0YOqr/ihpg5DAlm', 'Baleares');

insert into Account_Roles(id, role) VALUES (1, 'USER');
insert into Account_Roles(id, role) VALUES (2, 'ADMIN');

insert into Accounts_to_Roles(account_id, role_id) VALUES (1,1);
insert INTO Accounts_to_Roles(account_id, role_id) VALUES (2,1);
insert into Accounts_to_Roles(account_id, role_id) VALUES (2,2);

insert into Films (id, title, description, duration, year, poster, rating, trailer)
    VALUES (1, 'Logan', 'Sin sus poderes, por primera vez, Wolverine es verdaderamente vulnerable. Después de una vida de dolor y angustia, sin rumbo y perdido en el mundo donde los X-Men son leyenda, su mentor Charles Xavier lo convence de asumir una última misión: proteger a una joven que será la única esperanza para la raza mutante... Tercera y última película protagonizada por Hugh Jackman en el papel de Lobezno. ',
    135, 2017, 'logan.jpg', '18+', 'ny3hScFgCIQ');
insert into Genres (id, name) VALUES (1, 'Acción');
insert into Genres (id, name) VALUES (2, 'Drama');
insert into Genres (id, name) VALUES (3, 'Ciencia Ficción');
insert into Directors (id, name) VALUES (1, 'James Mangold');
insert into Actors (id, name) VALUES (1, 'Hugh Jackman');
insert into Actors (id, name) VALUES (2, 'Patrick Stewart');
insert into Actors (id, name) VALUES (3, 'Dafne Keen');
insert into Actors (id, name) VALUES (4, 'Boyd Holbrook');
insert into Actors (id, name) VALUES (5, 'Stephen Merchant');
insert into Actors (id, name) VALUES (6, 'Elizabeth Rodriguez');
insert into Actors (id, name) VALUES (7, 'Richard E. Grant');
insert into Countries (id, name) VALUES (1, 'Estados Unidos');
insert into Films_to_Genres (film_id, genre_id) VALUES (1, 1);
insert into Films_to_Genres (film_id, genre_id) VALUES (1, 2);
insert into Films_to_Genres (film_id, genre_id) VALUES (1, 3);
insert into Films_to_Directors (film_id, director_id) VALUES (1, 1);
insert into Films_to_Stars (film_id, actor_id) VALUES (1, 1);
insert into Films_to_Stars (film_id, actor_id) VALUES (1, 2);
insert into Films_to_Stars (film_id, actor_id) VALUES (1, 3);
insert into Films_to_Supportings (film_id, actor_id) VALUES (1, 4);
insert into Films_to_Supportings (film_id, actor_id) VALUES (1, 5);
insert into Films_to_Supportings (film_id, actor_id) VALUES (1, 6);
insert into Films_to_Supportings (film_id, actor_id) VALUES (1, 7);
insert INTO Films_to_Countries (film_id, country_id) VALUES (1, 1);*/
