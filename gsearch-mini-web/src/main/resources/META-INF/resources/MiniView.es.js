import Component from 'metal-component/src/Component';
import Soy from 'metal-soy/src/Soy';

import devbridgeAutocomplete from './js/DevbridgeAutocomplete.es';

import templates from './MiniView.soy';

/**
 * View component.
 */
class MiniView extends Component {

	/**
	 * @inheritDoc
	 */
	constructor(opt_config) {

		super(opt_config);

		this.autoCompleteEnabled = opt_config.autoCompleteEnabled;

		this.autoCompleteRequestDelay = opt_config.autoCompleteRequestDelay;

		this.portletNamespace = opt_config.portletNamespace;

		this.queryMinLength = opt_config.queryMinLength;

		this.requestTimeout = opt_config.requestTimeout;

		this.searchPageURL = opt_config.searchPageURL;

		this.suggestionsURL = opt_config.suggestionsURL;

		this.noSuggestionsNotice = opt_config.noSuggestionsNotice;

		// Init autocomplete.

		if (this.autoCompleteEnabled) {
			this.initAutocomplete();
		}

		// Set click events

		this.setClickEvents();
	}

	/**
	 * Execute search
	 */
	doSearch() {

		let q = $('#' + this.portletNamespace + 'MiniSearchField').val();

		if (q.length < this.queryMinLength) {
			this.showMessage(Liferay.Language.get('min-character-count-is') + ' ' +
					this.queryMinLength);
			return false;
		}

		let url = this.searchPageURL + "?q=" + q;

		window.location.replace(url);
	}

	/**
	 * Init autocomplete / suggester.
	 */
	initAutocomplete() {

		let _self = this;

		let suggestionGroups = {};

		$('#' + this.portletNamespace + 'MiniSearchField').devbridgeAutocomplete({
			dataType: 'json',
			deferRequestBy: _self.autoCompleteRequestDelay,
			minChars: _self.queryMinLength,
			noCache: true,
		    onSelect: function (suggestion) {
		    	window.location.href = suggestion.data.url;
		    },
			paramName: 'q',
			serviceUrl: _self.suggestionsURL,
			groupBy: "type",
			triggerSelectOnValidInput: false,
            transformResult: function(response) {
                if (response) {
                	let resultObject = {};
					let suggestionsArray = [];
                    let content = [];
                    let persons = [];
                    let tools = [];

                    for (let i = 0; i < response.suggestions.length; i++) {
						let result = response.suggestions[i];
						if (result.data.typeKey === 'person') {
							persons.push(result);

						} else if (result.data.typeKey === 'tool') {
                            tools.push(result);
						} else {
							content.push(result);
						}
					}
                    suggestionsArray = suggestionsArray.concat(content, persons, tools);
                    resultObject.suggestions = suggestionsArray;
                    suggestionGroups = response.groups;
					return resultObject;
                } else {
                    return {
                        suggestions: []
                    }
                }
            },
			formatResult: function(suggestion, currentValue) {
				let iconDiv = $('<div/>');
				iconDiv.addClass('item-icon');
				if (suggestion.data.typeKey !== 'tool') {
                    let svg = $('<svg/>');
                    svg.attr('role', 'img');
                    let use = $('<use/>');
                    use.attr('xlink:href', '/o/flamma-theme/images/flamma/svg/svg.svg#icon-' + suggestion.data.icon);
                    svg.append(use);
                    iconDiv.append(svg);
				} else {
					let span = $('<span/>');
					iconDiv.addClass('tool');
					span.html(suggestion.value.trim().substring(0,1).toUpperCase());
					iconDiv.append(span);
				}

				let dataDiv = $('<div/>');
				dataDiv.addClass('item-data');
				let titleDiv = $('<div/>').html(suggestion.value);
				titleDiv.addClass('search-suggestion-item-title');
				let breadcrumbSpan = $('<span/>').addClass('breadcrumb').html(suggestion.data.description);
                let descriptionDiv = $('<div/>').append(breadcrumbSpan);
                if (suggestion.data.date !== '') {
                    let dateSpan = $('<span/>').addClass('date').html(suggestion.data.date);
                	descriptionDiv.append(dateSpan)
				}
				descriptionDiv.addClass('search-suggestion-item-description');
                dataDiv.append(titleDiv);
                dataDiv.append(descriptionDiv);
				return $('<div/>').addClass('search-suggestion-item').append(iconDiv).append(dataDiv).prop('outerHTML');
			},
			formatGroup: function (suggestion, category) {
				let div = $('<div/>').addClass('search-suggestion-group-header');
				let categorySpan = $('<span/>');
				categorySpan.addClass('search-suggestion-category');
				categorySpan.html(category);
				categorySpan.appendTo(div);
				let typeKey = suggestion.data.typeKey;
				let searchPage = _self.searchPageURL;
				if (suggestionGroups.hasOwnProperty(typeKey) && (suggestionGroups[typeKey].count >= suggestionGroups[typeKey].group.maxSuggestions)) {
                    let q = $('#' + _self.portletNamespace + 'MiniSearchField').val();
					let href = searchPage + '?q=' + q;
					for (let i = 0; i < suggestionGroups[typeKey].group.types.length; i++) {
						href += '&type=' + suggestionGroups[typeKey].group.types[i];
					}
					let showMoreA = $('<a/>', {
						href: href
					});
                    showMoreA.addClass('right search-suggestion-show-more');
                    showMoreA.html(Liferay.Language.get('suggestions-show-more'));
                    showMoreA.appendTo(div);
				}
                return div.prop('outerHTML');
			},
            showNoSuggestionNotice: true,
            noSuggestionNotice: _self.noSuggestionsNotice,
            preventBadQueries: false,
			preserveInput: true
		});
	}

	/**
	 * Set click events.
	 */
	setClickEvents() {

		let _self = this;

		// Bind button click event

		$('#' + this.portletNamespace + 'MiniSearchButton').on('click', function (event) {
			_self.doSearch();
		});

		// Bind search field keypress event.

		$('#' + this.portletNamespace + 'MiniSearchField').keypress(function (event) {
	        var keycode = (event.keyCode ? event.keyCode : event.which);
	        if(keycode === 13){
				_self.doSearch();
	        }
	    });
	}

	/**
	 * Show message
	 *
	 * @param {String} title
	 */
	showMessage(title) {

		let elementId = this.portletNamespace + 'MiniSearchFieldMessage';

		$('#' + elementId).tooltip({title: title}).tooltip('show');

		// Setting delay doesn't work on manual show

		setTimeout(function(){
			$('#' + elementId).tooltip('hide');
		}, 2000);
	}
}

// Register component

Soy.register(MiniView, templates);

export default MiniView;