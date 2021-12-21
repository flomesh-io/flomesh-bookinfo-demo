const express = require('express');
const axios = require('axios');

const { services } = require('../config');

const router = express.Router();
const { products } = require('../data');

async function getDetails(productId, headers) {
  try {
    const url = `${services.details.name}/${services.details.endpoint}/${productId}`;
    console.log(`Calling ${url}`);
    const response = await axios.get(url, {headers: headers});
    if (response.error) console.error(response.error);
    return response.data;
  } catch (err) {
    console.error(err);
    return err;
  }
}

async function getReviews(productId, headers) {
  try {
    const url = `${services.reviews.name}/${services.reviews.endpoint}/${productId}`;
    console.log(`Calling ${url}`);
    const response = await axios.get(url, {headers: headers});
    console.log(response);
    if (response.error) console.error(response.error);
    return response.data;
  } catch (err) {
    console.error(err);
    return err;
  }
}

/* GET home page. */
router.get('/', (req, res) => {
  res.render('index', { title: 'Bookstore' });
});

router.get('/productpage', async (req, res, next) => {
  try {
    const product = products[0];
    const [details, reviews] = await Promise.all(
      [getDetails(product.id, req.headers), getReviews(product.id, req.headers)],
    );
    return res.render('productpage', { product, details, reviews });
  } catch (err) {
    return next(err);
  }
});

router.get('/health', (req, res) => res.send('OK'));


module.exports = router;
