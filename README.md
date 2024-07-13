# Shopping-Map-Backend



## Description
This is the server part of the **shopping map app** which performs the functions of adding/taking data from the SQL database and send data to 
[image-generator-module](https://gitlab.pg.innopolis.university/ise25/image-generator-module) for image processing.

## Features
- Adding clothes
- Creating outfits from added clothes 
- Creating capsules from added outfits
- Image background removing from added cloth
- Image generation for outfits and clothes

## Deployed url
**There are deployed url of [server](http://10.90.136.54:5252/api/v1/clothes)** 

## Deployement
This section more oriented **for customer**

Firstly you need have to installed `jdk17`

|OS|Download Away|
|-|-|
|Windows|https://adoptium.net/temurin/releases/|
|Ubuntu|`sudo apt install openjdk-17-jre`|
|Arch Linux|`sudo pacman -S jre-openjdk`|

Clone the repository: 
```bash
git clone https://gitlab.pg.innopolis.university/ise25/shopping-map-backend
```

Build the application:

```bash
./gradlew build
```

And run the `.jar` file:

```bash
java -jar weather-spring-app-1.0.0.jar
```

## Used technologies
- ![Static Badge](https://img.shields.io/badge/ktor-server?style=for-the-badge&color=%23c94aff&link=https%3A%2F%2Fktor.io%2F)
- ![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
- ![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
- ![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)
