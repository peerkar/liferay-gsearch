<div class="container-fluid gsearch-container">

	<div class="input-group">
		<input
			autofocus
			autocomplete="off"
			class="form-control input-lg textinput"
			id="<portlet:namespace />MiniSearchField"
			name="<portlet:namespace />MiniSearchField"
			maxlength="100"
			placeholder="<liferay-ui:message key="keywords" />"
			required="required"
			type="text"
			value=""
		/>

		<span class="input-group-btn">
			<button
				class="btn btn-secondary"
				id="<portlet:namespace />MiniSearchButton"
				type="button"
			>
				<svg class="lexicon-icon">
					<use xlink:href="/o/classic-theme/images/lexicon/icons.svg#search" />
				</svg>
			</button>
		</span>
	</div>

	<%-- Hidden element for anchoring messages tooltip --%>

	<div class="message-wrapper" data-title="" id="<portlet:namespace />MiniSearchFieldMessage"></div>
</div>

<aui:script>

	Liferay.Portlet.ready(

		function(portletId, node) {
			<portlet:namespace />initAutocomplete();

			$('#<portlet:namespace />MiniSearchField').on('keyup', function(event) {
				<portlet:namespace />handleKeyUp(event);
			});

			$('#<portlet:namespace />MiniSearchButton').on('click', function(event) {
				<portlet:namespace />doSearch();
			});
		}
	);

	/**
	 * Does search from icon click or enter press.
	 */
	function <portlet:namespace />doSearch() {

		let q = $('#<portlet:namespace />MiniSearchField').val();

		if (q.length < <%= queryMinLength %>) {
			<portlet:namespace />showMessage('<liferay-ui:message key="min-character-count-is" />' + ' <%= queryMinLength %> ');
			return false;
		}

		let url = "<%= searchPageURL %>?q=" + q;

		window.location.href = url;
	}

	/**
	 * Handles keyup  events.
	 */
	function <portlet:namespace />handleKeyUp(event) {

		var keycode = (event.keyCode ? event.keyCode : event.which);

		if (keycode === 13) {

			// If enter is hit on suggestion, do the default.

			if (!$('.gsearch-mini-suggestions .autocomplete-selected').length) {
				<portlet:namespace />doSearch();
			}
		}
	}

	/**
	 * Inits autocomplete / suggester.
	 */
	function <portlet:namespace />initAutocomplete() {

		var searchFieldElement = $('#<portlet:namespace />MiniSearchField');

		let suggestionGroups = {};

		<%-- Autocomplete plugin gets attached to jQuery 3.4.1 --%> 

		Liferay.Loader.require('gsearch-mini-web$devbridge-autocomplete@1.4.10/dist/jquery.autocomplete', 'gsearch-mini-web$jquery@3.4.1/dist/jquery', function(module, jQuery) {

			jQuery(searchFieldElement).devbridgeAutocomplete({
				containerClass: 'gsearch-mini-suggestions',
				dataType: 'json',
				deferRequestBy: <%= requestDelay %>,
				formatResult: function(suggestion, currentValue) {

					return suggestion.value;
				},
				formatGroup: function(suggestion, category) {

					if (category == 'show-more') {
						return '';
					}

					var link = '<%= searchPageURL %>' + '?entryClassName=' +
						suggestion.data.entryClassName + '&q=' +
						$('#<portlet:namespace />MiniSearchField').val();

					return '<div class="autocomplete-group">' +
								'<span class="category">' + category + '</span>' +
								'<span class="more">' +
									'<a href="#" onClick="<portlet:namespace />openMoreLink(\'' + link + '\')"><liferay-ui:message key="more" /></a>' +
								'</span>' +
							'</div>';
				},
				groupBy: 'type',
				minChars: <%= queryMinLength %>,
				noCache: false,
				noSuggestionNotice: '<liferay-ui:message key="no-content-suggestions" />',
				onSelect: function(suggestion) {

					var link = suggestion.data.link;

					if (<%= appendRedirect %>) {
						link += suggestion.data.redirect;
					}
					location.href = link;
				},
				paramName: 'q',
				preserveInput: true,
				preventBadQueries: false,
				serviceUrl: '<%= suggestionsURL %>',
				showNoSuggestionNotice: true,
				transformResult: function(response) {

					if (response) {

						// Sort array by type.

						response.items.sort(<portlet:namespace />sortByType);

						let results = $.map(response.items, function(dataItem) {

							// Shorten to (for now) fixed 75 chars.

							let description = dataItem.description ? dataItem.description : dataItem.content_highlight;

							if (description == null) {
								description = '';
							}
							
							description = description.replace(/<liferay-hl>/g, '').replace(/<\/liferay-hl>/g, '');

							if (description.length > 75) {
								description = description.substring(0, 75) + '...';
							}

							var value = '<div title="' + dataItem.title + '" class="title">' + dataItem.title + '</div><div class="description">' + description + '</div>';

							return {
								value: value, data: dataItem
							};
						});

						// Show all link

						if (results.length > 0) {

							let q = $('#<portlet:namespace />MiniSearchField').val();

							let url = "<%= searchPageURL %>?q=" + q;

							let showAllLink = {
								value: '<div title="" class="show-all"><%= LanguageUtil.get(request, "show-all-results") %></div>',
								data: {
									entryClassName: '',
									link: url,
									redirect: '',
									type: 'show-more'
								}
							}

							results.push(showAllLink);
						}

						return {
							suggestions: results
						};
					} else {
						return {
							suggestions: []
						};
					}
				},
				triggerSelectOnValidInput: false
			});
		});
	}

	/**
	 * Shows message.
	 */
	function <portlet:namespace />showMessage(title) {

		var elementId = '<portlet:namespace />MiniSearchFieldMessage';

		$('#' + elementId).tooltip({title: title}).tooltip('show');

		// Setting delay doesn't work on manual show

		setTimeout(function() {
			$('#' + elementId).tooltip('hide');
		}, 2000);
	}

	/**
	* Sorts objects by type.
	*/
	function <portlet:namespace />sortByType (A, B) {
	
		return ((A.type == B.type) ? 0 : ((A.type > B.type) ? 1 : -1 ));
	}

	function <portlet:namespace />openMoreLink(link) {

		//$('#<portlet:namespace />MiniSearchField').autocomplete('dispose');

		window.location.href = link;
	}

</aui:script>