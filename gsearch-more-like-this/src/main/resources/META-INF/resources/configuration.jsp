<%@ include file="/init.jsp" %>

<%
	PortletPreferences preferences = renderRequest.getPreferences();
	String resolveUIDClauses = preferences.getValue("resolveUIDClauses", ConfigurationUtil.getDefaultConfigurationValue("resolveUIDClauses"));
	String moreLikeThisClauses = preferences.getValue("moreLikeThisClauses", ConfigurationUtil.getDefaultConfigurationValue("moreLikeThisClauses"));
	String resultLayout = preferences.getValue("resultLayout", "list");
	String itemsToShow = preferences.getValue("itemsToShow", "5");
	String entryClassNames = preferences.getValue("entryClassNames", ConfigurationUtil.getDefaultConfigurationValue("entryClassNames"));
	String assetPublisherPage = preferences.getValue("assetPublisherPage", "/viewasset");
	boolean showResultsInContext = GetterUtil.getBoolean(preferences.getValue("showResultsInContext", "true"));
	boolean appendRedirect = GetterUtil.getBoolean(preferences.getValue("appendRedirect", "true"));
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
				
				<aui:input label="asset-publisher-page" name="preferences--assetPublisherPage--" type="text" value="<%=assetPublisherPage %>" />

				<aui:input label="show-results-in-context" name="preferences--showResultsInContext--" type="checkbox" value="<%=showResultsInContext %>" />

				<aui:input label="append-redirect" name="preferences--appendRedirect--" type="checkbox" value="<%=appendRedirect %>" />
		
				<aui:input label="resolve-uid-clause-configuration-name" name="preferences--resolveUIDClauses--" type="textarea" value="<%=resolveUIDClauses %>" />
		
				<aui:input label="more-like-this-clause-configuration-name" name="preferences--moreLikeThisClauses--" type="textarea" value="<%=moreLikeThisClauses %>" />
		
				<aui:input label="entryclassnames-configuration-name" name="preferences--entryClassNames--" type="textarea" value="<%=entryClassNames %>" />
		
				<aui:select label="result-layout-name" name="preferences--resultLayout--" >
				    <aui:option selected='<%=resultLayout.equals("list") ? true : false %>' value="list">Plain list</aui:option>
				    <aui:option selected='<%=resultLayout.equals("thumbnailList") ? true : false %>' value="thumbnailList">List with thumbnails</aui:option>
				    <aui:option selected='<%=resultLayout.equals("userImageList") ? true : false %>' value="userImageList">List with user images</aui:option>
				    <aui:option selected='<%=resultLayout.equals("image") ? true : false %>' value="image">Image</aui:option> 
				    <aui:option selected='<%=resultLayout.equals("strip") ? true : false %>' value="strip">Strip</aui:option> 
				</aui:select>
				
				<aui:input label="items-to-show" name="preferences--itemsToShow--" type="text" value="<%=itemsToShow %>" />
		
			</aui:fieldset>
			
			<aui:button-row>
				<aui:button type="submit"></aui:button>
			</aui:button-row>	
		
		</aui:form>
	</div>
</div>