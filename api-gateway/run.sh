#!/bin/sh
java -javaagent:/opentelemetry-javaagent.jar \
  -Dotel.traces.exporter=logging \
  -Dotel.metrics.exporter=none \
  -Dotel.propagators=tracecontext,baggage,b3multi \
  -Dotel.resource.attributes="service.name=${_pod_serviceFullName},service.version=${_pod_serviceVersion},service.namespace=${_pod_ns},service.instance.id=${_pod_UID}" \
  -Dotel.javaagent.debug=false \
  -Dotel.traces.sampler=parentbased_always_on \
  -Djava.security.egd=file:/dev/./urandom \
  -Xms256M -Xmx256M \
  -jar /app.jar