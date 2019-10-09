import React from 'react';
import config from 'react-global-configuration';
import 'url-search-params-polyfill';

import GSearchCommonUtil from './GSearchCommonUtil';
import { RequestParameterNames } from '../constants/requestparameters';
import { ConfigKeys } from '../constants/configkeys';


/**
 * GSearch utility class
 */

class GSearchURLUtil {

    static buildAddressBarURL(parameters) {

        let url = [location.protocol, '//', location.host, location.pathname, '?'].join('');

        let params = '';

        for (let p in parameters) {

            if (parameters.hasOwnProperty(p) && GSearchCommonUtil.isValueNotNull(parameters[p])) {

                let value = parameters[p];

                if (Array.isArray(value)) {
                    for (let v of value) {
                        v = GSearchURLUtil.encodeValues(v);

                        if (params.length > 0) {
                            params = params.concat('&');
                        }

                        params = params.concat(p).concat('=').concat(v);
                    }
                } else {

                    if (params.length > 0) {
                        params = params.concat('&');
                    }

                    value = GSearchURLUtil.encodeValues(value);
                    params = params.concat(p).concat('=').concat(value);
                }
            }
        }

        url = url.concat(params);

        GSearchURLUtil.updateAddressBar(parameters[RequestParameterNames.KEYWORDS], url);
    }

    static encodeValues(value) {
        if (value) {
            value = value.toString();
            return value.replace(/ /g, '%20').replace(/\"/g, '%22');
        }
    }

    /**
     * Parse parameters from URL
     */
    static parseURLParameters() {

        const params = new URLSearchParams(location.search);

        // Keywords ('q') is the only mandatory parameter.

        if (!params.get(RequestParameterNames.KEYWORDS)) {
            return;
        }

        let initialParams = {};

        // Parse parameters.

        for (let p of params) {

            const key = p[0];
            const value = p[1];

            // Check valid facet parameters.

            let isValidFacetParameter = Object.keys(config.get(ConfigKeys.FACET)).some(function (k) {
                return k === key;
            });

            if (isValidFacetParameter) {
                if (!initialParams[key]) {
                    initialParams[key] = [];
                }
                initialParams[key].push(value);
                continue;
            }

            // Chek other valid parameters

            var isValidParameter = Object.keys(RequestParameterNames).some(function (k) {
                return RequestParameterNames[k] === key;
            });

            if (isValidParameter) {
                initialParams[key] = value;
            }
        }
        return initialParams;
    }

	/**
	 * Update address bar.
	 * 
	 * @param {address} key
	 */
    static updateAddressBar(keywords, url) {

        if (window.history.replaceState) {
            window.history.replaceState(null, keywords + '- Search', url);
        } else {
            document.location.hash = url;
        }
    }

}

export default GSearchURLUtil;