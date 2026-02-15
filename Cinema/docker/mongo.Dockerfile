FROM mongo:8.2

COPY mongodb.key /opt/mongo/mongodb.key
COPY mongod.conf /etc/mongo/mongod.conf

USER root
RUN chown mongodb:mongodb /opt/mongo/mongodb.key /etc/mongo/mongod.conf && \
    chmod 400 /opt/mongo/mongodb.key

USER mongodb