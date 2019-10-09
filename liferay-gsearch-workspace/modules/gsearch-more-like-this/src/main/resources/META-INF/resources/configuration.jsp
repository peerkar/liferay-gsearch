<%@ include file="/init.jsp" %>

<%
	PortletPreferences preferences = renderRequest.getPreferences();

	String recommendationClauses = preferences.getValue(ModuleConfigurationKeys.RECOMMENDATION_CLAUSES, ConfigurationUtil.getDefaultConfigurationValue(ModuleConfigurationKeys.RECOMMENDATION_CLAUSES));
	String resultLayout = preferences.getValue(ModuleConfigurationKeys.RESULT_LAYOUT, "strip");
	String itemsToShow = preferences.getValue(ModuleConfigurationKeys.ITEMS_TO_SHOW, "8");
	String filterClauses = preferences.getValue(ModuleConfigurationKeys.FILTER_CLAUSES, ConfigurationUtil.getDefaultConfigurationValue(ModuleConfigurationKeys.FILTER_CLAUSES));
	String assetPublisherPage = preferences.getValue(ModuleConfigurationKeys.ASSET_PUBLISHER_PAGE, "/viewasset");
	boolean showResultsInContext = GetterUtil.getBoolean(preferences.getValue(ModuleConfigurationKeys.SHOW_RESULTS_IN_CONTEXT, "true"));
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />

<div class="portlet-configuration-body-content">
	<div class="container-fluid-1280">
		<aui:form
			action="<%= configurationActionURL %>"
			method="post"
			name="fm"
		>
			<aui:input
				name="<%= Constants.CMD %>"
				type="hidden"
				value="<%= Constants.UPDATE %>"
			/>

			<aui:fieldset>
				<aui:input name="cmd" type="hidden" value="update" />

				<aui:input label="asset-publisher-page" name="preferences--assetPublisherPage--" type="text" value="<%= assetPublisherPage %>" />

				<aui:input label="show-results-in-context" name="preferences--showResultsInContext--" type="checkbox" value="<%= showResultsInContext %>" />

				<aui:input label="append-redirect" name="preferences--appendRedirect--" type="checkbox" value="<%= appendRedirect %>" />

				<aui:input label="recommendation-clauses-configuration-name" name="preferences--recommendationClauses--" type="textarea" value="<%= recommendationClauses %>" />

				<aui:input label="filter-clauses-configuration-name" name="preferences--filterClauses--" type="textarea" value="<%= filterClauses %>" />

				<aui:select label="result-layout-name" name="preferences--resultLayout--">
					<aui:option selected='<%= resultLayout.equals("list") ? true : false %>' value="list">Plain list</aui:option>
					<aui:option selected='<%= resultLayout.equals("thumbnailList") ? true : false %>' value="thumbnailList">List with thumbnails</aui:option>
					<aui:option selected='<%= resultLayout.equals("userImageList") ? true : false %>' value="userImageList">List with user images</aui:option>
					<aui:option selected='<%= resultLayout.equals("image") ? true : false %>' value="image">Image</aui:option>
					<aui:option selected='<%= resultLayout.equals("strip") ? true : false %>' value="strip">Strip</aui:option>
				</aui:select>

				<aui:input label="items-to-show" name="preferences--itemsToShow--" type="text" value="<%= itemsToShow %>" />
			</aui:fieldset> 

			<aui:button-row>
				<aui:button type="submit"></aui:button>
			</aui:button-row>
		</aui:form>
	</div>
</div>