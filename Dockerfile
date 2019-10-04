FROM java:8

COPY jgrapht-core-1.0.0.jar /
COPY keyDataPair.java /
COPY alltogether.java /
COPY pathAndDistance.java /
COPY client.java /


RUN  javac -Xlint:unchecked -cp jgrapht-core-1.0.0.jar *.java

EXPOSE 4000
CMD  java -cp jgrapht-core-1.0.0.jar:. alltogether
