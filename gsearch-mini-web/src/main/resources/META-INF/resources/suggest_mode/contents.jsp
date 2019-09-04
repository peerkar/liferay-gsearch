<div class="container-fluid gsearch-container">

	<div class="input-group">
	
		<input 
			autocomplete="off"
			class="form-control input-lg textinput minisearch-input" 
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
				type="button"
				aria-label="<liferay-ui:message key='do-search'/>">
				<svg class="lexicon-icon" role="presentation">
			        <use xlink:href="/o/classic-theme/images/lexicon/icons.svg#search" />
				</svg>
			</button>
		</span>
	</div>
		
	<%-- Hidden element for anchoring messages tooltip  --%>
	
	<div class="message-wrapper" data-title="" id="<portlet:namespace />MiniSearchFieldMessage"></div>


	<div class="autocomplete-suggestion" id="gsearchPastSearchTemplate" style="display: none;">
		<div class="search-suggestion-item">
			<div class="item-icon col-md-1 col-lg-1" style="display: none;">
				<div class="user-portrait" style="display: none;">
					<div class="image-holder one-to-one">
						<div class="content">
							<img alt="" title="" src="">
						</div>
					</div>
				</div>
				<span class="user-avatar-image" style="display: none;">
					<div title="" class="user-icon user-icon-default">
						<span></span>
					</div>
				</span>
				<span class="tool-icon" style="display: none;"></span>
			</div>
			<div class="item-data col-md-12 col-lg-12">
				<div class="search-suggestion-item-title">
					<div title="" class="title"></div>
				</div>
				<div class="search-suggestion-item-description">
					<span class="suggestion-breadcrumb"></span>
					<span class="date"></span>
				</div>
			</div>
		</div>
	</div>

</div>

<aui:script>

	Liferay.Portlet.ready(
			
			
	
	    function(portletId, node) {
	    	if ('<portlet:namespace/>'.indexOf(portletId) >= 0) {
			<portlet:namespace />initAutocomplete();
			
			$('#<portlet:namespace />MiniSearchField').on('keyup', function(event) {
				<portlet:namespace />handleKeyUp(event);
			});	

			if ('<%=pastSearchesEnabled%>' === 'true') {
				<portlet:namespace />initPastSearches();

				$('#<portlet:namespace />MiniSearchField').on('keydown', function(event) {
					<portlet:namespace />handleKeyDown(event);
				});

				$('#<portlet:namespace />MiniSearchField').on('click', function(event) {
					<portlet:namespace />showPastSearches();
				});
			}

			$('#<portlet:namespace />MiniSearchButton').on('click', function(event) {
				<portlet:namespace />doSearch();
			});
	    	}
	    }
	);
	
	/**
	 * Do search from icon or enter press.
	 */
	function <portlet:namespace />doSearch() {

		let q = $('#<portlet:namespace />MiniSearchField').val();

		if (q.length < <%=queryMinLength %>) {
			<portlet:namespace />showMessage('<liferay-ui:message key="min-character-count-is" />' + ' <%=queryMinLength %> ');
			return false;
		}

		let url = "<%= searchPageURL %>?q=" + q;

		window.location.href = url;
	}

	function <portlet:namespace />showPastSearches() {
		var pastSearchesDiv = $('#<portlet:namespace/>gsearchPastSearches');
		if (!pastSearchesDiv.hasClass('past-searches-prevented')) {
			pastSearchesDiv.show();
		}
	}

	function <portlet:namespace />hidePastSearches() {
		var pastSearchesDiv = $('#<portlet:namespace/>gsearchPastSearches');
		pastSearchesDiv.hide();
	}

	function <portlet:namespace />initPastSearches() {

		var pastSearchesDiv = $('#<portlet:namespace/>gsearchPastSearches');

		var pastSearches = [];

		if (localStorage["pastSearches"]) {
			pastSearches = JSON.parse(localStorage["pastSearches"]);
		}

		if (pastSearchesDiv.length == 0) {
			pastSearchesDiv = $('<div class="past-searches" id="<portlet:namespace/>gsearchPastSearches"></div>');
		} else {
			pastSearchesDiv.empty();
		}

		var miniSearchField = $('#<portlet:namespace />MiniSearchField');

		pastSearchesDiv.css({
			'display': 'none',
			'position': 'absolute',
			'z-index': 9999,
		    'top': miniSearchField.css('height'),
	    	'left': '0px',
			'width': miniSearchField.css('width')
		});

		pastSearchesDiv.addClass('autocomplete-suggestions');
		pastSearchesDiv.append($('<div class="autocomplete-group"><span class="category"><liferay-ui:message key="past-searches"/></span></div>'));

		if (pastSearches.length > 0) {
			for (var i = 0; i < pastSearches.length; i++) {
				var currentItemData = pastSearches[i];
				var currentItemElement = $('#gsearchPastSearchTemplate').clone();
				if (currentItemData.typeKey === 'person') {
					currentItemElement.find('.item-icon').show();
					if (currentItemData.hasOwnProperty('userPortraitUrl')) {
						var img = currentItemElement.find('img');
						img.attr('alt', currentItemData.userInitials);
						img.attr('title', currentItemData.title);
						img.attr('src', currentItemData.userPortraitUrl);
						currentItemElement.find('.user-portrait').show();
					} else {
						var userIcon = currentItemElement.find('.user-icon');
						userIcon.attr('title', currentItemData.title);
						userIcon.find('span').html(currentItemData.userInitials);
						currentItemElement.find('.user-avatar-image').show();

					}
				} else if (currentItemData.typeKey === 'tool') {
					var toolIcon = currentItemElement.find('.tool-icon');
					toolIcon.html(currentItemData.icon);
					toolIcon.css('display', 'flex');
					var itemIcon = currentItemElement.find('.item-icon');
					itemIcon.addClass('tool');
					itemIcon.show();
				} else if (currentItemData.typeKey === 'news') {
					currentItemElement.find('.date').html(currentItemData.date);
				}
				currentItemElement.find('.title').html(currentItemData.title);
				currentItemElement.append($('<div class="hidden past-search-link">' + currentItemData.link + '</div>'));
				<portlet:namespace/>addPastSearchClickHandler(currentItemElement, currentItemData);
				currentItemElement.find('.suggestion-breadcrumb').html(currentItemData.breadcrumbs);
				currentItemElement.show();
				pastSearchesDiv.append(currentItemElement);
			}

			pastSearchesDiv.appendTo(miniSearchField.parent());

			$(document).on('click', function(event) {
				var eventTarget = $(event.target);
				if (!($.contains(pastSearchesDiv[0], eventTarget[0]) ||
						pastSearchesDiv[0] === eventTarget[0] ||
						eventTarget.is('#<portlet:namespace/>MiniSearchField'))) {
					<portlet:namespace/>hidePastSearches();
				}
			});
		}

	}

	function <portlet:namespace/>addPastSearchClickHandler(element, data) {
		element.click(function(event) {
			<portlet:namespace/>storeSearchResultToPastSearches(data);
			window.location.href = $(event.currentTarget).find('.past-search-link').html();
		});

	}

	function <portlet:namespace />handleKeyDown(event) {
		var keycode = (event.keyCode ? event.keyCode : event.which);

		if (keycode === 9) {
			<portlet:namespace/>hidePastSearches();
		}
	}

	/**
	 * Handle keyup  events.
	 */
	function <portlet:namespace />handleKeyUp(event) {
		

        var keycode = (event.keyCode ? event.keyCode : event.which);
		let q = $('#<portlet:namespace />MiniSearchField').val();

		if ('<%=pastSearchesEnabled%>' === 'true') {
			if ((keycode !== 9) && (keycode !== 16)) {
				<portlet:namespace/>hidePastSearches();
			} else {
				var eventTarget = $(event.target);
				if (eventTarget.is('#<portlet:namespace/>MiniSearchField')) {
					<portlet:namespace/>showPastSearches()
				}
			}
			var pastSearchesDiv = $('#<portlet:namespace/>gsearchPastSearches');

			if (q.length === 0) {
				pastSearchesDiv.removeClass('past-searches-prevented');
			} else {
				pastSearchesDiv.addClass('past-searches-prevented');
			}
		}

        if(keycode === 13){
        	
        	// If enter is hit on suggestion, do the default.
        	
    		if (!$('.gsearch-mini-suggestions .autocomplete-selected').length) {
            	<portlet:namespace />doSearch();
    		}
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

								let userPortrait = $('<div/>');
								userPortrait.addClass('user-portrait');
								let imgHolderOneToOne = $('<div/>');
								imgHolderOneToOne.addClass('image-holder one-to-one');
								let portraitContent = $('<div/>');
								portraitContent.addClass('content');

								let img = $('<img/>');
								img.prop('alt', suggestion.data.userInitials);
								<%--img.addClass('user-icon user-icon-default');--%>
								img.prop('title', suggestion.data.userName);
								img.prop('src', suggestion.data.userPortraitUrl);

								portraitContent.append(img);
								imgHolderOneToOne.append(portraitContent);
								userPortrait.append(imgHolderOneToOne);

								iconDiv.append(userPortrait);
							} else {
								let spanOuter = $('<span/>');
								spanOuter.addClass('user-avatar-image');
								let div = $('<div/>');
								div.prop('title', suggestion.data.userName);
								div.addClass('user-icon user-icon-default');
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

						let breadcrumbSpan = $('<span/>').addClass('suggestion-breadcrumb').html(suggestion.data.breadcrumbs);
		                descriptionDiv.append(breadcrumbSpan);
					}

	                // Date

	                if ((suggestion.data.date !== '') && (suggestion.data.typeKey === 'news')) {
	                    let dateSpan = $('<span/>').addClass('date').html(suggestion.data.date);
	                	descriptionDiv.append(dateSpan)
					}
	                dataDiv.append(titleDiv);
	                dataDiv.append(descriptionDiv);
					return $('<div/>').addClass('search-suggestion-item').append(iconDiv).append(dataDiv).prop('outerHTML');
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
			    	    			'<a href="#" onClick="<portlet:namespace />openMoreLink(\'' + link + '\')">' + 
									'<liferay-ui:message key="show-more-results" />' +
		       				'</a></span></div>';
			    },					
				groupBy: 'group_localized',
				minChars: <%=queryMinLength %>,
				noCache: false,
				noSuggestionNotice: '<liferay-ui:message key="no-content-suggestions" />',
			    onSelect: function (suggestion) {

					if ('<%=pastSearchesEnabled%>' === 'true') {
						<portlet:namespace/>storeSearchResultToPastSearches(suggestion.data);
					}

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

						let results =  $.map(response.items, function(dataItem) {

			            	// Shorten to (for now) fixed 75 chars.
				        	
							var description = dataItem.description;
			            	
							description = description.replace(/<liferay-hl>/g, '').replace(/<\/liferay-hl>/g, '');
							
					        if (description.length > 75) {
					        	description = description.substring(0, 75) + '...';
					        }
							
								var value = '<div title="' +  dataItem.title + '" class="title">' + dataItem.title + '</div>';
			            	
			                return {
			                	value: value, data: dataItem
			                };
			            });

						// Show all link
						
						if (results.length > 0) {
						
							let q = $('#<portlet:namespace />MiniSearchField').val();
	
							let url = "<%= searchPageURL %>?q=" + q;
							
							let showAllLink = {
								value: '<div title="" class="show-all"><%=LanguageUtil.get(request, "show-all-results") %></div>',
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

	function <portlet:namespace />storeSearchResultToPastSearches(data) {
		var pastSearches = [];

		if (localStorage["pastSearches"]) {
			pastSearches = JSON.parse(localStorage["pastSearches"]);
		}

		var existingIndex = -1;
		for (var i = 0; i < pastSearches.length; i++) {
			if (pastSearches[i].link === data.link) {
				existingIndex = i;
				break;
			}
		}

		if (existingIndex >= 0) {
			pastSearches.splice(existingIndex, 1);
		}

		pastSearches.unshift(data);

		if (pastSearches.length > 5) {
			pastSearches.pop();
		}

		localStorage["pastSearches"] = JSON.stringify(pastSearches);

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
