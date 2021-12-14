docker build --no-cache -f ./Dockerfile.build -t lex-core-build .

docker run --name lex-core-build lex-core-build:latest &&  docker cp lex-core-build:/opt/target/bodhi-1.0-SNAPSHOT.jar .

docker rm -f lex-core-build
docker rmi -f lex-core-build

docker build --no-cache -t lexplatform.azurecr.io/lex-core-service:space-v3-sprint-1 .
docker push lexplatform.azurecr.io/lex-core-service:space-v3-sprint-1
