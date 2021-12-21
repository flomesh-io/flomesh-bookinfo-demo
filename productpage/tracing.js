/* tracing.js */

// Require dependencies
const opentelemetry = require("@opentelemetry/sdk-node");
const { getNodeAutoInstrumentations } = require("@opentelemetry/auto-instrumentations-node");
const { B3Propagator, B3InjectEncoding } = require('@opentelemetry/propagator-b3');

const sdk = new opentelemetry.NodeSDK({
  traceExporter: new opentelemetry.tracing.ConsoleSpanExporter(),
  instrumentations: [getNodeAutoInstrumentations()],
  textMapPropagator: new B3Propagator({ injectEncoding: B3InjectEncoding.MULTI_HEADER })
});

sdk.start()