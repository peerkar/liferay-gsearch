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

		window.location.replace(url);
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
				containerClass: 'gsearch-mini-suggestions',
				dataType: 'json',
				deferRequestBy: <%=autoCompleteRequestDelay %>,
				formatResult: function (suggestion, currentValue) {

		            return suggestion.value;
				},
				formatResult: function (suggestion, currentValue) {

		            return suggestion.value;
				},
				formatGroup: function(suggestion, category) {
					
					var link = '<%= searchPageURL %>' + '?type=' + 
						suggestion.data.key + '&q=' + 
						$('#<portlet:namespace />MiniSearchField').val();

			        return '<div class="autocomplete-group">' +
			    	    		'<span class="category">' + category + '</span>' + 
			    	    		'<span class="more">' +
			    	    			'<a href="#" onClick="<portlet:namespace />openMoreLink(\'' + link + '\')">' + 
			       					 	Liferay.Language.get('more') + 
		       				'</a></span></div>';
			    },				
				groupBy: 'type',
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
						
					    response.items.sort(<portlet:namespace />sortByType);
						
						return {
							
				            suggestions: $.map(response.items, function(dataItem) {

				            	// Shorten to (for now) fixed 75 chars.
					        	
								var description = dataItem.description;
				            	
								description = description.replace(/<liferay-hl>/g, '').replace(/<\/liferay-hl>/g, '');
								
						        if (description.length > 75) {
						        	description = description.substring(0, 75) + '...';
						        }
								
								var value = '<div title="' +  dataItem.title_raw + '" class="title">' + dataItem.title_raw + '</div><div class="description">' + description + '</div>';
				            	
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
 	* Sort objects by type.
	*/
	function <portlet:namespace />sortByType (A, B) {
		return ((A.type == B.type) ? 0 : ((A.type > B.type) ? 1 : -1 ));
	}

	function <portlet:namespace />openMoreLink(link) { 

		$('#<portlet:namespace />MiniSearchField').autocomplete('dispose');
		
		window.location.href = link;	
	}
	
</aui:script>
