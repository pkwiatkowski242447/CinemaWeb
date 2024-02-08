# fundamentals_of_network_applications
This repository contains source codes of web applications designed for Fundamentals of Network Applications course on TUL.

**How to run the project?**

Basically, it is advised to used zip archives, available in each release, since backend changes that took place after given release could make its functionality not available (like adding security in exercise 5. breaks MVC exercise [changes in URL-s and also required authenticataion and authorization]).

To run the required infrastructure, that is MongoDB cluster, go to the docker or mongo-conf folder (depending on the release) located in Cinema folder and execute command:
```
docker compose up
```
Then, using IDE like IntellJ run the CinemaApp (in release 2.1.1) or CinemaApplication (from 3.1.1 onwards) as the Main class using Payara or Spring Boot configuration respectively.

In order to run MVC application (release 3.1.1), create Payara configuration (in IntellJ run configurations) and add artifact in deployment tab - MVC war exploded. After running this configuration, browser with MVC application should open. If not then type in URL search bar http://localhost:8080/MVC-0.0.1-SNAPSHOT/ and press Enter.

For releases that also include SPA frontend application (releases 4.1.1 and 5.1.1), go to the spa-webapp folder and execute command:
```
npm install
```
and also:
```
npm run dev
```
