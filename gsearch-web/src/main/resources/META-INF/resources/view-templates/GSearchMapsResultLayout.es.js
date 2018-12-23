import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import core from 'metal/src/core';

import templates from './GSearchMapsResultLayout.soy';

/**
 * GSearch maps result layout component.
 */
class GSearchMapsResultLayout extends Component {
	
	/**
	 * @inheritDoc
	 */
	attached() {

		if (this.debug) {
			console.log("GSearchMapsResultLayout.attached()");
		}
	}
		
	/**
	 * @inheritDoc
	 */
	rendered() {
		
		if (this.debug) {
			console.log("GSearchResultLayoutOptions.rendered()");
		}
		
		let _self = this;

		Liferay.Loader.require('GoogleMapsLoader', function(GoogleMapsLoader) {
			
			GoogleMapsLoader.KEY = _self.googleMapsAPIKey;

			GoogleMapsLoader.load(function(google) {

				// Center map on the first hit. Fall back to London

				let centerPoint = {lat: 51.522525, lng: -0.130456};
				
				if (_self.results.items[0].latitude) {
					centerPoint = {lat: _self.results.items[0].latitude, lng: _self.results.items[0].longitude};
				}
								 
				var map = new google.maps.Map(
					document.getElementById('gsearch-map'), {zoom: 2, center: centerPoint});

				// Create markers
				
				let count = _self.results.items.length;
								
				for (let i = 0; i < count; i++) {
					
					let latitude = _self.results.items[i].latitude;
					let longitude = _self.results.items[i].longitude;


					if (!latitude || !longitude) {
						continue;
					}
					
					let location = {lat: latitude, lng: longitude};

					let content = '<strong>' + _self.results.items[i].title + '</strong><br /><br />';
					content += _self.results.items[i].description;
					content += '<br /><br />';
					content += '<a href="' + _self.results.items[i].link + '">' + Liferay.Language.get('open') + '</a>';
					
					let infowindow = new google.maps.InfoWindow({
						content: content
					});

					let marker = new google.maps.Marker({
						position: location,
						map: map,
						title: _self.results.items[i].title
					});					
					
					marker.addListener('click', function() {
						infowindow.open(map, marker);
					});
				}
			});
		});
	}
}

/** 
 * State definition.
 * 
 * @type {!Object}
 * @static
 */
GSearchMapsResultLayout.STATE = {
	debug: {
		value: false
	},
	googleMapsAPIKey: {
		value: null
	}
};

// Register component

Soy.register(GSearchMapsResultLayout, templates);

export default GSearchMapsResultLayout;	
