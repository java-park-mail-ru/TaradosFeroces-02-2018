FROM ubuntu:16.04

MAINTAINER Alex Kuznetsov

# Обвновление списка пакетов
RUN apt-get -y update


#
# Установка postgresql
#
ENV PGVER 9.5
RUN apt-get install -y postgresql-$PGVER tree maven openjdk-8-jdk-headless build-essential


USER postgres

ARG db_user="tf_alex"
ARG db_password="tarados"

ARG db_name="deadlinez_db"
#ARG db_test_name="test_deadlinez_db"


RUN /etc/init.d/postgresql start && \
    psql --command "CREATE USER ${db_user} WITH SUPERUSER PASSWORD '${db_password}';" && \
    createdb -O "${db_user}" ${db_name} && \
    #createdb -O "${db_user}" ${db_test_name} && \
    /etc/init.d/postgresql stop


RUN echo "host all all 0.0.0.0/0 md5" >> /etc/postgresql/$PGVER/main/pg_hba.conf
RUN echo "listen_addresses='localhost'" >> /etc/postgresql/$PGVER/main/postgresql.conf

ENV PGDATA /var/lib/pgsql/data/
ENV PG_PORT=5432
EXPOSE ${PG_PORT}

ENV DatabaseUrl="postgresql://localhost:${PG_PORT}/${db_name}"

# Add VOLUMEs to allow backup of config, logs and databases
VOLUME  [ "/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql" ]

# Back to the root user
USER root

# Копируем исходный код в Docker-контейнер
ENV WORK /opt/deadlinez
ADD ./ $WORK/server

WORKDIR $WORK/server

RUN cd $WORK/server && mvn dependency:copy-dependencies clean


# Объявлем порт сервера
EXPOSE 8080

ENV DB_USER ${db_user}
ENV DB_PASS ${db_password}
ENV DB_NAME ${db_name}

#RUN make install

# Запускаем PostgreSQL и сервер
ENTRYPOINT ["make"]
CMD ["deadlinez"]
