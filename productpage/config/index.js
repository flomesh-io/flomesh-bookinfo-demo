const apiGatewayHostname = process.env.K8S_SAMPLES_API_GATEWAY_HOSTNAME ? `.${process.env.K8S_SAMPLES_API_GATEWAY_HOSTNAME}` : '127.0.0.1';
const apiGatewayPort = process.env.K8S_SAMPLES_API_GATEWAY_PORT ? `.${process.env.K8S_SAMPLES_API_GATEWAY_PORT}` : '10000';
//const servicesDomain = process.env.SERVICES_DOMAIN ? `.${process.env.SERVICES_DOMAIN}` : '';

const details = {
  name: `http://${apiGatewayHostname}:${apiGatewayPort}/bookinfo-details`,
  endpoint: 'details',
  children: [],
};

const ratings = {
  name: `http://${apiGatewayHostname}:${apiGatewayPort}/bookinfo-ratings`,
  endpoint: 'ratings',
  children: [],
};

const reviews = {
  name: `http://${apiGatewayHostname}:${apiGatewayPort}/bookinfo-reviews`,
  endpoint: 'reviews',
  children: [ratings],
};

const productpage = {
  name: `http://${apiGatewayHostname}:${apiGatewayPort}/bookinfo-details`,
  endpoint: 'details',
  children: [details, reviews],
};


module.exports.services = {
  productpage,
  details,
  reviews,
};
