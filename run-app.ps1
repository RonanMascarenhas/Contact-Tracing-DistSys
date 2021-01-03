# package the app
mvn clean package

# run
# todo check if it should be `docker-compose build up`
docker-compose up --build
