import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';
import Ajax from 'metal-ajax/src/Ajax';

import GSearchUtils from '../js/GSearchUtils.es';

import templates from './GSearchHelp.soy';

/**
 * GSearch gelp component.
 */
class GSearchHelp extends Component {
	
	/**
	 * @inheritDoc
	 * 
	 */
	constructor(opt_config, opt_parentElement) {
	
		super(opt_config, opt_parentElement);

		this.debug = opt_config.JSDebugEnabled;
		
		console.log("HELP");

		this.helpTextURL = opt_config.helpTextURL;

		this.portletNamespace = opt_config.portletNamespace;

		this.requestTimeout = opt_config.requestTimeout;
	}
	
	/**
	 * @inheritDoc
	 */ 
	attached() {

		if (this.debug) {
			console.log("GSearchHelp.attached()");
		}

		let _self = this;

		// Create help button click event to load help data on request.
		
		$('#' + this.portletNamespace + 'HelpModal').on('shown.bs.modal', function (e) {
			if (!self.helpText) {
				_self.getHelpText();
			}
		});
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
				
				$('#' + this.portletNamespace + 'HelpModal .modal-body').html(this.helpText);
				$('#' + this.portletNamespace + 'HelpModal .modal-body').removeClass('ajax-loader-placeholder');
			} else {
				
				// Assume here simply that there was an error if response was empty.
				// Make better by sending proper response codes from backend etc.

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
	helpText: {
		value: null
	}
};

// Register component

Soy.register(GSearchHelp, templates);

export default GSearchHelp;