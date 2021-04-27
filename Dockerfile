FROM java:8-jre-alpine
MAINTAINER Francesco Donini <francesco.donini@iit.cnr.it>

ENV APP_USER app
ENV HOME /app
ENV TZ Europe/Rome

RUN mkdir /app && addgroup -g 1000 $APP_USER && adduser -u 1000 -G $APP_USER -D -h $HOME -g "App user" $APP_USER
RUN apk --update add tzdata bash

USER $APP_USER

COPY target/chorchain-service-0.0.1-SNAPSHOT.jar $HOME

WORKDIR $HOME

EXPOSE 8080


CMD ["java", "-Djava.security.egd=file:/dev/./urandom","-jar", "chorchain-service-0.0.1-SNAPSHOT.jar"]
