import React from 'react';
import ReactGoogleMapLoader from "react-google-maps-loader"
import ReactGoogleMap, { Marker } from "react-google-map"
import config from 'react-global-configuration';

import Thumbnails from './Thumbnails';
import { ConfigKeys } from '../../../constants/configkeys';

/**
 * Maps view.
 */
class Maps extends React.Component {

	/**
	 * Gets Maps markers.
	 * 
	 * @param {Array} items 
	 */
	getMarkers(items) {

		let markers = [];

		for (let i = 0; i < items.length; i++) {

			const item = items[i];

			if (!item.latitude || !item.longitude) {
				continue;
			}

			let marker = {
				key: 'marker_' + Math.floor(Math.random() * 1000000),
				title: item.title,
				position: {
					lat: item.latitude,
					lng: item.longitude,
				},
				onLoaded: (googleMaps, map, marker) => {

					const infoWindow = new googleMaps.InfoWindow({
						content: `
							<div>
								<h3>${item.title}</h3>
								<div>${item.description}</div>
								<div class="marker-link"><a href="${item.link}">Open</a></div>
							</div>`
					})

					googleMaps.event.addListener(marker, 'click', () => {
						infoWindow.open(map, marker)
					})

					// Open InfoWindow by default

					// infoWindow.open(map, marker)
				}
			}
			markers.push(marker);

		}
		return markers;
	}

	/**
	 * Renders results list.
	 */
	renderItems() {

		const { items } = this.props;

		return (
			<Thumbnails items={items} />
		)
	}

	/**
	 * Render.
	 */
	render() {

		const googleMapsConfig = config.get(ConfigKeys.GOOGLE_MAPS);

		const { items } = this.props;

		const markers = this.getMarkers(items);

		return (
			<div>
				<div className="google-map-wrapper">
					<ReactGoogleMapLoader
						params={{
							key: googleMapsConfig.gmapAPIKey,
							libraries: 'places,geometry'
						}}
						render={googleMaps =>
							googleMaps && (
								<div style={{ height: "350px" }}>
									<ReactGoogleMap
										autoFitBounds
										googleMaps={googleMaps}
										center={{ lat: googleMapsConfig.gmapDefaultCenter.lat, lng: googleMapsConfig.gmapDefaultCenter.lng }}
										zoom={googleMapsConfig.zoom}
										coordinates={markers}
									>

										{markers.map(marker => (
											<Marker key={marker.key}
												position={{ lat: marker.lat, lng: marker.lng }}
											/>
										))}
									</ReactGoogleMap>
								</div>
							)}
					/>
				</div>

				{this.renderItems(items)}

			</div>
		)
	}
}

export default Maps;