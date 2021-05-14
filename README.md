This was a group project completed for the COMP30220 "Distributed Systems" module at University College Dublin. The contributors were:

* Finn O'Neill [@o-Fon-o](https://github.com/o-Fon-o)
* Dónal Mac Fhionnlaoich [@donalmacfhionnlaoich](https://github.com/donalmacfhionnlaoich)
* Husni Khalaf [@HusniKhalaf](https://github.com/HusniKhalaf)
* Ronan Mascarenhas [@RSM61123](https://github.com/RSM61123)
* Joseph Dempsey [@Joe-Demp](https://github.com/Joe-Demp)

Our submission documents: Report.pdf and VideoReport.mov have been left out of this repository. The original README has also been edited to exclude student numbers and references to these documents. [BFG Repo-Cleaner](https://rtyley.github.io/bfg-repo-cleaner/) was used to scrub the repository of the documents and sensitive information. The remainder of that README follows.

# Socially Distanced Systems Contact Tracing System

## How To Run
- Make sure that there is no 'data' folder
    - If it is there, delete it
    - It will be automatically created at build
- Run these commands:
```bash
mvn package
docker-compose up --build
```

### Please allow some time for the services to become discoverable and for the queues to become active
### It may also take some time for the patients to make it through the queue to the contact tracing service

To access:
- web-ui: localhost:8080
- eureka1 dashboard: localhost:9001
- eureka2 dashboard: localhost:9002
- activeMQ dashboard: localhost:8161
- mongoDB: localhost:27018
