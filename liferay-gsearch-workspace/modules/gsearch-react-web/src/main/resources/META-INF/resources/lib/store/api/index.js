import React from 'react';
import config from 'react-global-configuration';
import axios from 'axios';
import 'url-search-params-polyfill';

import { ConfigKeys } from '../../constants/configkeys';

/**
 * Gets help text.
 */
export function getHelp() {

  let urlConfig = config.get(ConfigKeys.URL);
  let url = urlConfig.helpTextURL;

  // Do Axios request

  const request = axios.post(url);

  return request
    .then(response => {
      return response.data.helpText;
    })
    .catch(error => {
      throw error;
    });
}

/**
 * Gets search results.
 * 
 * @param {*} args 
 */
export function getSearchResults(args) {

  let urlConfig = config.get(ConfigKeys.URL);
  let url = urlConfig.searchResultsURL;

  // Build parameters.

  var urlParams = new URLSearchParams();

  Object.keys(args[0]).forEach((key, index) => {
    urlParams.append(key, args[0][key]);
  });

  // Do Axios request

  const request = axios.post(url, urlParams);

  return request
    .then(response => {
      return response.data;
    })
    .catch(error => {
      throw error;
    });
}

/**
 * Gets suggestions.
 * 
 * @param {*} keywords 
 */
export function getSuggestions(keywords) {

  let urlConfig = config.get(ConfigKeys.URL);
  let url = urlConfig.suggestionsURL;

  // Build parameters.

  var urlParams = new URLSearchParams();
  urlParams.append('q', keywords);

  // Do Axios request

  const request = axios.post(url, urlParams);

  return request
    .then(response => {
      return response.data;
    })
    .catch(error => {
      throw error;
    });
}
