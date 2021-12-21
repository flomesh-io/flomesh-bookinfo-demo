/* tracing.js */

// Require dependencies
const opentelemetry = require("@opentelemetry/sdk-node");
const { getNodeAutoInstrumentations } = require("@opentelemetry/auto-instrumentations-node");
const { B3Propagator, B3InjectEncoding } = require('@opentelemetry/propagator-b3');
const { Resource } = require('@opentelemetry/resources');
const { SemanticResourceAttributes } = require('@opentelemetry/semantic-conventions');

const sdk = new opentelemetry.NodeSDK({
  resource: new Resource({
    [SemanticResourceAttributes.SERVICE_NAME]: process.env['_pod_serviceFullName'],
    [SemanticResourceAttributes.SERVICE_VERSION]: process.env['_pod_serviceVersion'],
    [SemanticResourceAttributes.SERVICE_NAMESPACE]: process.env['_pod_ns'],
    [SemanticResourceAttributes.SERVICE_INSTANCE_ID]: process.env['_pod_UID'],
  }),
  traceExporter: new opentelemetry.tracing.ConsoleSpanExporter(),
  instrumentations: [getNodeAutoInstrumentations()],
  textMapPropagator: new B3Propagator({ injectEncoding: B3InjectEncoding.MULTI_HEADER })
});

sdk.start()