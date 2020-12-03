FROM openjdk:11-jre
MAINTAINER Francesco Donini <francesco.donini@iit.cnr.it>

ENV APP_USER app
ENV HOME /app
ENV TZ Europe/Rome

RUN addgroup --gid 1000 $APP_USER && adduser --uid 1000 --ingroup $APP_USER --home $HOME --gecos "" --quiet --disabled-password $APP_USER

USER $APP_USER
COPY target/chorchain-service.jar $HOME

WORKDIR $HOME

EXPOSE 8080

CMD ["java", "-jar", "chorchain-service-0.0.1-SNAPSHOT.jar"]

