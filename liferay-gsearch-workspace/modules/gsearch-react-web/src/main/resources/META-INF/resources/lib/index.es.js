import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import config from 'react-global-configuration';

import App from './App';
import { configureStore } from './store/configureStore';

const store = configureStore();

export default function(elementId, configuration) {

	// Freeze set to false because of SPA.

    config.set(configuration, {freeze: false});

	ReactDOM.render(
	    <Provider store={store}>
	        <App appConfiguration={configuration} />
	    </Provider>, document.getElementById(elementId)
	);
}