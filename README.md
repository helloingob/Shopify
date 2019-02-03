# Shopify

An online shoppinglist with offer highlighting from known german supermarkets

## Features:
- Add/Delete product to shopping list
- Increase/Decrease amount of a product
- Offer Sniper: Parse offers from known supermarkets (last checked 12.01.2019)
  - Aldi
  - Edeka
  - Penny
  - Rewe
  - Sky
  

## Requirements
- [Tomcat 9](https://tomcat.apache.org/download-90.cgi)
- [Java 1.8](https://java.com/de/download/)
- [MariaDB 5.5](https://mariadb.com/downloads/)
- [Maven](https://maven.apache.org/download.cgi)


## Build
  - Run "**mvn clean package**"
  
## Database
Add user "**shopper**" with "**helloingob**" password & create **database**.

  ```
  CREATE USER 'shopper'@'localhost' IDENTIFIED BY 'helloingob';
  GRANT ALL PRIVILEGES ON shopper.* TO 'shopper'@'localhost';
  FLUSH PRIVILEGES;

  CREATE DATABASE shopper;
  ```
  Execute [schema.sql](/sql/schema.sql)

## Setup GUI:
1) Copy shopify.war file to tomcat webapps directory
2) Start Tomcat server
3) Access http://localhost:8080/shopify
4) Add products
