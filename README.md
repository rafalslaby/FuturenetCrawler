# FuturenetCrawler

Simple web crawler to count people in futurenet.club.

### Prerequisites

JDK, JDBC mysql module, mysql database on localhost.

### Description

It counts people in social networking service futurenet.club and extracts basic information about them.

### How it works

FuturenetCrawler logs in to a website using example profile and checks for next unvisited profile in the database, so
you have to insert first seed profile to the database manually. When crawler gets unvisited profile it adds all
friends of that person to the database each marked as unvisited and gets next unvisited person, and so on...
