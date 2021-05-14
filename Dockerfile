
# For solc binary
FROM ethereum/solc:0.5.17 as solc-builder

FROM openjdk:8-jre-alpine
MAINTAINER Francesco Donini <francesco.donini@iit.cnr.it>

ENV APP_USER app
ENV HOME /app
ENV TZ Europe/Rome
ARG INSTALL_BASE=/bin

RUN mkdir /app && addgroup -g 1000 $APP_USER && adduser -u 1000 -G $APP_USER -D -h $HOME -g "App user" $APP_USER
RUN apk --update add tzdata bash
#RUN apk  --update add tzdata bash openjdk8 npm
#RUN npm install -g solc@0.5


#RUN apt-get update && apt-get install tzdata bash openjdk8 npm solc@5

USER $APP_USER

COPY target/chorchain-service-0.0.1-SNAPSHOT.jar $HOME

WORKDIR $HOME

EXPOSE 8080

COPY --from=solc-builder /usr/bin/solc $INSTALL_BASE/
COPY --chown=1000:1000 wait-for-it.sh wait-for-it.sh

RUN chmod +x wait-for-it.sh
#
#CMD ["java", "-Djava.security.egd=file:/dev/./urandom","-jar", "chorchain-service-0.0.1-SNAPSHOT.jar"]
#ENTRYPOINT [ "/bin/bash", "-c" ]
CMD ["./wait-for-it.sh" ,  "-h", "database" , "-p", "3306", "-t", "30", "--" , "java", "-Djava.security.egd=file:/dev/./urandom","-jar", "chorchain-service-0.0.1-SNAPSHOT.jar"]