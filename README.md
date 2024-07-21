# Shopping-Map-Backend



## Description
This is the server part of the **shopping map app** which performs the functions of adding/taking data from the SQL database and send data to 
[image-generator-module](https://github.com/GalievDev/image-generator-module) for image processing.

## Features
- Adding clothes
- Creating outfits from added clothes 
- Creating capsules from added outfits
- Image background removing from added cloth
- Image generation for outfits and clothes

## Deployement
This section more oriented **for customer**

<details>
<summary>SQL Database schema</summary>

```sql
DROP TABLE IF EXISTS capsules_outfits;
DROP TABLE IF EXISTS outfits_clothes;
DROP TABLE IF EXISTS capsules;
DROP TABLE IF EXISTS outfits;
DROP TABLE IF EXISTS clothes;
DROP TABLE IF EXISTS images;

CREATE TABLE images (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    bytes BYTEA NOT NULL
);

CREATE TABLE clothes (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    link VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    type VARCHAR(255) NOT NULL,
    image_id INT REFERENCES images(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE outfits (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    image_id INT REFERENCES images(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE capsules (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    image_id INT REFERENCES images(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE outfits_clothes (
    outfit_id INT,
    cloth_id INT,
    PRIMARY KEY (outfit_id, cloth_id),
    CONSTRAINT fk_outfit
        FOREIGN KEY (outfit_id)
            REFERENCES outfits (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_cloth
        FOREIGN KEY (cloth_id)
            REFERENCES clothes (id)
            ON DELETE CASCADE
);

CREATE TABLE capsules_outfits (
    capsule_id INT,
    outfit_id INT,
    PRIMARY KEY (capsule_id, outfit_id),
    CONSTRAINT fk_capsule
        FOREIGN KEY (capsule_id)
            REFERENCES capsules (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_outfit
        FOREIGN KEY (outfit_id)
            REFERENCES outfits (id)
            ON DELETE CASCADE
);
```
</details>

Firstly you need have to installed `jdk17`

|OS|Download Away|
|-|-|
|Windows|https://adoptium.net/temurin/releases/|
|Ubuntu|`sudo apt install openjdk-17-jre`|
|Arch Linux|`sudo pacman -S jre-openjdk`|

Clone the repository: 
```bash
git clone https://github.com/GalievDev/shopping-map-backend
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
