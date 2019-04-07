FROM java:8

MAINTAINER yangkizhang@gmail.com

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    MAPP_SLEEP=0
# add directly the jar
COPY *.jar /app.jar

VOLUME /tmp
EXPOSE 2333
CMD echo "The application will start in ${MAPP_SLEEP}s..." && \
    sleep ${MAPP_SLEEP} && \
    java -Djava.security.egd=file:/dev/./urandom -jar /app.jar
