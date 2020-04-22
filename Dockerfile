FROM openjdk:8 

WORKDIR /opt/equipment-shop

VOLUME /opt/equipment-shop/apps

ADD ./target/equipment-shop-0.0.1-SNAPSHOT.jar /opt/equipment-shop/equipment-shop.jar

ENV PLATFORM_ADMIN_FLYWAY_ENABLE=false
ENV PLATFORM_ADMIN_DB_SCHEMA=public
ENV PLATFORM_ADMIN_DB_CONNECTION=jdbc:postgresql://databasehost/postgres
ENV PLATFORM_ADMIN_DB_USER=postgres
ENV PLATFORM_ADMIN_DB_PASSWORD=1
ENV PLATFORM_ADMIN_DEBUG=true
ENV PLATFORM_ADMIN_DB_DDLMODE=validate


EXPOSE 8080 8000
CMD ["java", "-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000", "-jar", "equipment-shop.jar"]
