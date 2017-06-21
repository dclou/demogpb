FROM openjdk:8-jre-alpine
EXPOSE 8082
ADD target/customer-*.jar /app.jar
CMD java $JAVA_MEM $JAVA_EXT -jar /app.jar
