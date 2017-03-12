drop table IF EXISTS Accounts;
create table Accounts (
  id BIGINT NOT NULL AUTO_INCREMENT,
  User_Name varchar(20) not null,
  Active bit not null,
  Password varchar(100) not null,
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

drop table IF EXISTS Order_Details;
create table Order_Details (
  ID varchar(50) not null,
  Amount double precision not null,
  Price double precision not null,
  Quanity integer not null,
  ORDER_ID varchar(50) not null,
  PRODUCT_ID varchar(20) not null,
  primary key (ID)
);

drop table IF EXISTS Orders;
create table Orders (
  ID varchar(50) not null,
  Amount double precision not null,
  Customer_Address varchar(255) not null,
  Customer_Email varchar(128) not null,
  Customer_Name varchar(255) not null,
  Customer_Phone varchar(128) not null,
  Order_Date datetime not null,
  Order_Num integer not null,
  primary key (ID)
);

drop table IF EXISTS Products;
create table Products (
  Code varchar(20) not null,
  Create_Date datetime not null,
  Image longblob,
  Name varchar(255) not null,
  Price double precision not null,
  primary key (Code)
);

drop table if EXISTS persistent_logins;
CREATE TABLE persistent_logins (
  username VARCHAR(64) NOT NULL,
  series VARCHAR(64) NOT NULL,
  token VARCHAR(64) NOT NULL,
  last_used TIMESTAMP NOT NULL,
  PRIMARY KEY (series)
);

alter table Orders
  add constraint UK_sxhpvsj665kmi4f7jdu9d2791  unique (Order_Num);

alter table Order_Details
  add constraint ORDER_DETAIL_ORD_FK
foreign key (ORDER_ID)
references Orders (ID);

alter table Order_Details
  add constraint ORDER_DETAIL_PROD_FK
foreign key (PRODUCT_ID)
references Products (Code);

insert into Accounts (id, USER_NAME, ACTIVE, PASSWORD)
  values (1, 'employee1', 1, '123');
insert into Accounts (id, USER_NAME, ACTIVE, PASSWORD)
  values (2, 'joseluis', 1, '$2a$10$yAnz9wYWhO.Ia3scjKUoZeAyJ6Fg8XtLPuQ08jkpCfvOjht4wANw6');
insert into Accounts (id, User_Name, Active, Password)
    values (3,'admin', 1, '$2a$10$E1RIu3WFFeezHMjXFrasduaDD6FpdUU91k3DDU0YOqr/ihpg5DAlm');

insert into Account_Roles(id, role) VALUES (1, 'USER');
insert into Account_Roles(id, role) VALUES (2, 'ADMIN');

insert into Accounts_to_Roles(account_id, role_id) VALUES (1,1);
insert INTO Accounts_to_Roles(account_id, role_id) VALUES (2,2);
insert into Accounts_to_Roles(account_id, role_id) VALUES (2,1);
insert INTO Accounts_to_Roles(account_id, role_id) VALUES (3,1);
insert into Accounts_to_Roles(account_id, role_id) VALUES (3,2);

insert into products (CODE, NAME, PRICE, CREATE_DATE)
values ('S001', 'Core Java', 100, CURRENT_DATE() );

insert into products (CODE, NAME, PRICE, CREATE_DATE)
values ('S002', 'Spring for Beginners', 50, CURRENT_DATE() );

insert into products (CODE, NAME, PRICE, CREATE_DATE)
values ('S003', 'Swift for Beginners', 120, CURRENT_DATE() );

insert into products (CODE, NAME, PRICE, CREATE_DATE)
values ('S004', 'Oracle XML Parser', 120, CURRENT_DATE() );

insert into products (CODE, NAME, PRICE, CREATE_DATE)
values ('S005', 'CSharp Tutorial for Beginers', 110, CURRENT_DATE() );