FROM flomesh/adoptopenjdk:8-alpine-slim
ARG JAR_FILE=target/bookinfo-reviews.jar
COPY ${JAR_FILE} app.jar

ENV JAVA_OPTS="-Xms256M -Xmx256M"

ENTRYPOINT ["java", "-javaagent:/opentelemetry-javaagent.jar", "-Dotel.traces.exporter=logging", "-Dotel.metrics.exporter=none", "-Dotel.propagators=tracecontext,baggage,b3multi", "-Dotel.resource.attributes=service.name=${_samples_pod_serviceFullName},service.version=${_samples_pod_serviceVersion},service.namespace=${_samples_pod_ns},service.instance.id=${_samples_pod_UID}", "-Dotel.javaagent.debug=false", "-Dotel.traces.sampler=parentbased_always_on", "-Djava.security.egd=file:/dev/./urandom ${JAVA_OPTS}", "-jar", "/app.jar"]