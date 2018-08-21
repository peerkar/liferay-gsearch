import Ajax from 'metal-ajax/src/Ajax';
import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';

import templates from './GSearchHelp.soy';

/**
 * GSearch gelp component.
 */
class GSearchHelp extends Component {

	/**
	 * @inheritDoc
	 */ 
	attached() {

		if (this.debug) {
			console.log("GSearchHelp.attached()");
		}
		
		if (this.helpText == '') {
			this.getHelpText();
		}
	}

	/**
	 * Get help text.
	 *
	 */
	getHelpText() {
				
		Ajax.request(
			this.helpTextURL,
			'GET',
			null,
			null,
			null,
			this.requestTimeout
		)
		.then((response) => {
			
			if (response.responseText) {

				this.helpText = JSON.parse(response.responseText).helpText;

			} else {
				
				alert(Liferay.Language.get('there-was-an-error'));
			}
		}).catch(function(reason) {
			
			alert(Liferay.Language.get('there-was-an-error'));
		});
	}
	
	/**
	 * @inheritDoc
	 */
	rendered() {
		if (this.debug) {
			console.log("GSearchHelp.rendered()");
		}
	}
}

/** 
 * State definition.
 * 
 * @type {!Object}
 * @static
 */
GSearchHelp.STATE = {
	debug: {
		value: false
	},
	helpText: {
		internal: true,
		value: ''
	},
	helpTextURL: {
		value: null
	},
	requestTimeout: {
		value: 10000
	}
};

// Register component

Soy.register(GSearchHelp, templates);

export default GSearchHelp;