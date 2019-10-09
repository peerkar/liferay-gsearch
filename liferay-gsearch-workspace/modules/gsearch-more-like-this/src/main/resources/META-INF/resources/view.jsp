<%@ include file="/init.jsp" %>

<%--Check configuration --%>

<%
if (!configured) {
%>

	<div class="alert alert-info text-center">
		<liferay-ui:message key="not-configured" />
	</div>

<%
}
%>

<div class="container-fluid gsearch-container">
	<div
		class="col-xs-12 col-sm-12 col-md-12 col-lg-12 gsearch-centered gsearch-results <%= currentResultLayout %>"
	id="<portlet:namespace />SearchResults"></div
>
</div>

<aui:script>
	function <portlet:namespace />generateDefaultLayout(items) {

		var html = '<div class="row">';

		var count = items.length;

		for (var i = 0; i < count; i++) {

			html += '<div class="item">';
			html += <portlet:namespace />getRow(items[i]);
			html += '</div>';
		}

		html += '</div>';

		return html;
	}

	function <portlet:namespace />generateStripLayout(items) {

		var html = '<div class="strip-container">';

		var count = items.length;

		for (var i = 0; i < count; i++) {

			html += '<div class="layoutitem item col-sm-12 col-md-6 col-lg-4">';
			html += <portlet:namespace />getRow(items[i]);
			html += '</div>';
		}

		html += '</div>';

		return html;
	}

	function <portlet:namespace />getRow(item) {

		var html = '';

		var link = item.link;

		if (<%= appendRedirect %>) {
			link += item.redirect;
		}

		if (item.imageSrc && item.imageSrc != '') {

			html += '<div class="smallimage col-md-2 col-lg-2 hidden-xs hidden-sm">';
			html += '<a href="' + link + '">';
			html += '<img alt="' + item.title + '" src="' + item.imageSrc + '" title="' + item.title + '" />';
			html += '</a>';
			html += '</div>';
			html += '<div class="col-xs-12 col-sm-12 col-md-10 col-lg-10 content">';

		}
			else if (item.userInitials) {

			html += '<div class="smallimage col-md-2 col-lg-2 hidden-xs hidden-sm">';
			html += '<span class="user-avatar-image">';
			html += '<div class="user-icon-color-9 user-icon user-icon-lg user-icon-default">';
			html += '<span>' + item.userInitials + '</span>';
			html += '</div>';
			html += '</span>';
			html += '</div>';
			html += '<div class="col-xs-12 col-sm-12 col-md-10 col-lg-10 content">';

		}
			else if (item.userPortraitUrl) {

			html += '<div class="smallimage col-md-3 col-lg-3 hidden-xs hidden-sm">';
			html += '<a href="' + link + '">';
			html += '<img alt="' + item.title + '" class="user-icon-color-9 user-icon-lg user-icon user-icon-default" src="' + item.userPortraitUrl + '" title="' + item.title + '" />';
			html += '</a>';
			html += '</div>';
			html += '<div class="col-xs-12 col-sm-12 col-md-9 col-lg-9 content">';
		}
else {
			html += '<div class="content">';
		}

		html += '<div class="heading">';

		if (item.type != '') {
			html += '<span class="type"><a href="' + link + '">[' + item.type + ']</a></span>';
		}

		html += '<h1>';
		html += '<a class="highlightable" href="' + link + '" title="' + item.title + '">' + item.title + '</a>';
		html += '</h1>';

		if (item.highlight) {
			html += '<span title="' + Liferay.Language.get('official-article') + '" class="glyphicon glyphicon-check"></span>';
		}
		html += '</div>';

		html += '<div class="link">';
		html += '<a class="highlightable" href="' + link + '">' + item.link + '</a>';
		html += '</div>';

		html += '<div class="description ">';

		if (item.date != '') {
			html += '<strong>' + item.date + '</strong> - ';
		}

		if (item.description != '') {
			html += '<span class="highlightable">' + item.description + '</span>';
		}
		html += '</div>';

		if (item.assetTagNames) {
			html += '<div class="tags">';
			html += '<span class="meta-title">' + Liferay.Language.get('tags') + ':</span>';

			let length = item.assetTagNames.length;

			for (let i = 0; i < length; i++) {
				html += '<span class="label label-lg text-uppercase tag">' + item.assetTagNames[i] + '</span>';
			}
			html += '</div>';
		}
		html += '</div>';

		return html;
	}

	AUI().ready(function()
		{
			if (<%= configured %>) {

				jQuery('#p_p_id<portlet:namespace />').show();

				jQuery.ajax({
					type : 'POST',
					url : '<%= getSearchResultsURL %>',
					success : function(data) {

						if (data.items && data.items.length > 0) {

							var resultLayout = '<%= currentResultLayout %>';

							var html;

							if (resultLayout == 'strip') {

								html = <portlet:namespace />generateStripLayout(data.items);

							}
								else {

								html = <portlet:namespace />generateDefaultLayout(data.items);
							}

							jQuery('#<portlet:namespace />SearchResults').html(html);
						}
					}
				});

			}
				else if (<%= themeDisplay.isSignedIn() %>) {

				jQuery('#p_p_id<portlet:namespace />').show();

			}
				else {

				jQuery('#p_p_id<portlet:namespace />').hide();
			}
		}
	);
</aui:script>