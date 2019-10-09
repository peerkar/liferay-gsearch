import React from 'react';
import axios from 'axios';
import config from 'react-global-configuration';
import 'regenerator-runtime/runtime';
import 'url-search-params-polyfill';

import { ConfigKeys } from '../constants/configkeys';

/**
 * GSearch utility class.
 */

class GSearchCommonUtil {

	/**
	 * Checks if object is empty.
	 * 
 	 * @param {Object} component
	 */
	static isEmptyObject(obj) {

		if (!obj) {
			return true;
		}

		for (var x in obj) {
			return false;
		}
		return true;
	}

    /**
	 * Is value not null / empty
	 * 
	 * @param{String} value
	 */
	static isValueNotNull(value) {
		return value && typeof value !== 'undefined' && value.toString().length > 0;
	}

	// Thanks to https://gist.github.com/felipenmoura/650e7e1292c1e7638bcf6c9f9aeb9dd5

	static scrollPageTo(to, duration = 400) {
		//t = current time
		//b = start value
		//c = change in value
		//d = duration
		const easeInOutQuad = function (t, b, c, d) {
			t /= d / 2;
			if (t < 1) return c / 2 * t * t + b;
			t--;
			return -c / 2 * (t * (t - 2) - 1) + b;
		};

		const easeInOutCubic = function (t, b, c, d) {
			t /= d / 2;
			if (t < 1) return c / 2 * t * t * t + b;
			t -= 2;
			return c / 2 * (t * t * t + 2) + b;
		};

		return new Promise((resolve, reject) => {
			const element = document.scrollingElement;

			if (typeof to === 'string') {
				to = document.querySelector(to) || reject();
			}
			if (typeof to !== 'number') {
				to = to.getBoundingClientRect().top + element.scrollTop;
			}

			let start = element.scrollTop,
				change = to - start,
				currentTime = 0,
				increment = 20;

			// Animation using cubic function.

			const animateScroll = function () {
				currentTime += increment;
				let val = easeInOutCubic(currentTime, start, change, duration);
				element.scrollTop = val;
				if (currentTime < duration) {
					setTimeout(animateScroll, increment);
				} else {
					resolve();
				}
			};
			animateScroll();
		});
	}

	/**
	 * Sends a tracking event to the backend.
	 * 
	 * @param Object event 
	 */
	static async trackClick(event) {

		const trackId = event.currentTarget.getAttribute('data-trackid');
		
		let urlConfig = config.get(ConfigKeys.URL);
		let url = urlConfig.clickTrackingURL;

		var params = new URLSearchParams();
		params.append('trackId', trackId);

		await axios.post(url, params)
			.then(response => {
				// console.log(response);
			})
			.catch(error => {
				throw error;
			});
	}
}

export default GSearchCommonUtil;