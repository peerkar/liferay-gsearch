<div class="container-fluid gsearch-container">

	<div class="input-group">

		<input
			autofocus
			autocomplete="off"
			class="form-control input-lg textinput"
			id="<portlet:namespace />MiniSearchField"
			name="<portlet:namespace />MiniSearchField"
			maxlength="100"
			placeholder="<%=LanguageUtil.get(request, "keywords") %>"
			required="required"
			type="text"
			value=""
		/>

		<span class="input-group-btn">
			<button
				class="btn btn-secondary"
				id="<portlet:namespace />MiniSearchButton"
				type="button">
				<svg class="lexicon-icon">
			        <use xlink:href="/o/classic-theme/images/lexicon/icons.svg#search" />
				</svg>
			</button>
		</span>
	</div>

	<%-- Hidden element for anchoring messages tooltip  --%>

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
	 * Do search from icon or enter press.
	 */
	function <portlet:namespace />doSearch() {

		let q = $('#<portlet:namespace />MiniSearchField').val();

		if (q.length < <%=queryMinLength %>) {
			<portlet:namespace />showMessage(Liferay.Language.get('min-character-count-is') + ' <%=queryMinLength %> ');
			return false;
		}

		let url = "<%= searchPageURL %>?q=" + q;

		window.location.href = url;
	}

	/**
	 * Handle keyup  events.
	 */
	function <portlet:namespace />handleKeyUp(event) {

        var keycode = (event.keyCode ? event.keyCode : event.which);

        if(keycode === 13){
        	<portlet:namespace />doSearch();
	    }
	}

	/**
	 * Init autocomplete / suggester.
	 */
	 function <portlet:namespace />initAutocomplete() {

		var searchFieldElement = $('#<portlet:namespace />MiniSearchField');

		let suggestionGroups = {};

		Liferay.Loader.require('devbridge-autocomplete', function() {

			$(searchFieldElement).devbridgeAutocomplete({

				dataType: 'json',
				deferRequestBy: <%=autoCompleteRequestDelay %>,
				formatResult: function(suggestion, currentValue) {

					// Icon

					let iconDiv = '';

					if (suggestion.data.typeKey === 'tool' || suggestion.data.typeKey === 'person') {

						iconDiv = $('<div/>').addClass('item-icon col-md-1 col-lg-1');

						if (suggestion.data.typeKey === 'tool') {
							let span = $('<span/>');
							iconDiv.addClass('tool');
							span.html(suggestion.data.icon);
							span.addClass('tool-icon');
							iconDiv.append(span);
						} else if (suggestion.data.typeKey === 'person') {

							if ((typeof suggestion.data.userPortraitUrl !== 'undefined') && (suggestion.data.userPortraitUrl !== '')) {
								let img = $('<img/>');
								img.prop('alt', suggestion.data.userInitials);
								img.addClass('user-icon-color-9 user-icon-lg user-icon user-icon-default');
								img.prop('title', suggestion.data.userName);
								img.prop('src', suggestion.data.userPortraitUrl);
								iconDiv.append(img);
							} else {
								let spanOuter = $('<span/>');
								spanOuter.addClass('user-avatar-image');
								let div = $('<div/>');
								div.prop('title', suggestion.data.userName);
								div.addClass('user-icon-color-9 user-icon user-icon-lg user-icon-default');
								let spanInner = $('<span/>');
								spanInner.html(suggestion.data.userInitials);
								div.append(spanInner);
								spanOuter.append(div);
								iconDiv.append(spanOuter);
							}
						}
					}

					// Data

					let dataDiv = $('<div/>').addClass('item-data  col-md-12 col-lg-12');

					// Title

					let titleDiv = $('<div/>').addClass('search-suggestion-item-title').html(suggestion.value);

					if (suggestion.data.typeKey === 'file') {
						let svg = $('<svg/>');
						svg.attr('role', 'img');
						let use = $('<use/>');
						use.attr('xlink:href', '/o/flamma-theme/images/flamma/svg/svg.svg#icon-file-text');
						svg.append(use);
						titleDiv.prepend(svg);
					}

					// Description

					let descriptionDiv = $('<div/>').addClass('search-suggestion-item-description')

					// Breadcrumbs

					if (suggestion.data.breadcrumbs) {

						let breadcrumbSpan = $('<span/>').addClass('breadcrumb').html(suggestion.data.breadcrumbs);
		                descriptionDiv.append(breadcrumbSpan);
					}

	                // Date

	                if ((suggestion.data.date !== '') && (suggestion.data.typeKey !== 'person') && (suggestion.data.typeKey !== 'tool')) {
	                    let dateSpan = $('<span/>').addClass('date').html(suggestion.data.date);
	                	descriptionDiv.append(dateSpan)
					}
	                dataDiv.append(titleDiv);
	                dataDiv.append(descriptionDiv);
					return $('<div/>').addClass('search-suggestion-item').append(iconDiv).append(dataDiv).prop('outerHTML');
				},
				formatGroup: function(suggestion, category) {
					var facets = suggestion.data.facets;
					var facetUrlParams = 'hf=' + facets.join('&hf=');
					var link = '<%= searchPageURL %>' + '?' +
						facetUrlParams + '&q=' +
						$('#<portlet:namespace />MiniSearchField').val();

			        return '<div class="autocomplete-group">' +
			    	    		'<span class="category">' + category + '</span>' +
			    	    		'<span class="more">' +
			    	    			'<a href="#" onClick="<portlet:namespace />openMoreLink(\'' + link + '\')">' +
									'<liferay-ui:message key="show-more-results" />' +
		       				'</a></span></div>';
			    },
				groupBy: 'group_localized',
				minChars: <%=queryMinLength %>,
				noCache: false,
				noSuggestionNotice: '<liferay-ui:message key="no-content-suggestions" />',
			    onSelect: function (suggestion) {

			    	var link = suggestion.data.link;

			    	if (<%=appendRedirect%>) {
				    	link += suggestion.data.redirect;
			    	}
			    	location.href = link;
			    },
				paramName: 'q',
				preserveInput: true,
				preventBadQueries: false,
				serviceUrl: '<%=suggestionsURL %>',
			    showNoSuggestionNotice: true,
				transformResult: function(response) {

					if (response) {

						// Sort array by type.

						response.items.sort(<portlet:namespace />sortByGroup);

						return {

				            suggestions: $.map(response.items, function(dataItem) {

				            	// Shorten to (for now) fixed 75 chars.

								var description = dataItem.description;

								description = description.replace(/<liferay-hl>/g, '').replace(/<\/liferay-hl>/g, '');

						        if (description.length > 75) {
						        	description = description.substring(0, 75) + '...';
						        }

								var value = '<div title="' +  dataItem.title_raw + '" class="title">' + dataItem.title_raw + '</div>';

				                return {
				                	value: value, data: dataItem
				                };
				            })
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
	 * Show message
	 */
	function <portlet:namespace />showMessage(title) {

		var elementId = '<portlet:namespace />MiniSearchFieldMessage';

		$('#' + elementId).tooltip({title: title}).tooltip('show');

		// Setting delay doesn't work on manual show

		setTimeout(function(){
			$('#' + elementId).tooltip('hide');
		}, 2000);
	}

	/**
 	* Sort objects by group.
	*/
	function <portlet:namespace />sortByGroup (A, B) {
		let groupOrder = { 'content': 1, 'person': 2, 'tool': 3 };
		let aValue = groupOrder[A.group];
		let bValue = groupOrder[B.group];
		return aValue == bValue ? 0 : ((aValue > bValue) ? 1 : -1 );
	}

	function <portlet:namespace />openMoreLink(link) {

		$('#<portlet:namespace />MiniSearchField').autocomplete('dispose');

		window.location.href = link;
	}

</aui:script>
