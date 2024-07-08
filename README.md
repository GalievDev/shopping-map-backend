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

## Installation

Clone the repository: 
```bash
git clone https://gitlab.pg.innopolis.university/ise25/shopping-map-backend
```

Open and run with command:

```bash
./gradlew build run
```

## Used technologies
![Static Badge](https://img.shields.io/badge/ktor-server?style=for-the-badge&color=%23c94aff&link=https%3A%2F%2Fktor.io%2F)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)