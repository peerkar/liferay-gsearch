<%@ include file="/init.jsp" %>

<%
	
	PortletPreferences preferences = renderRequest.getPreferences();
	String resolveUIDClauses = preferences.getValue("resolveUIDClauses", "");
	String moreLikeThisClauses = preferences.getValue("moreLikeThisClauses", "");
	String resultLayout = preferences.getValue("resultLayout", "list");
	String itemsToShow = preferences.getValue("itemsToShow", "5");
	String classNames = preferences.getValue("classNames", "");
	String assetPublisherPage = preferences.getValue("assetPublisherPage", "/viewasset");
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />
 
 <liferay-frontend:edit-form
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

		<aui:input label="resolve-uid-clause-configuration-name" name="preferences--resolveUIDClauses--" type="textarea" value="<%=resolveUIDClauses %>" />

		<aui:input label="more-like-this-clause-configuration-name" name="preferences--moreLikeThisClauses--" type="textarea" value="<%=moreLikeThisClauses %>" />

		<aui:input label="classnames-configuration-name" name="preferences--classNames--" type="textarea" value="<%=classNames %>" />

		<aui:select label="result-layout-name" name="preferences--resultLayout--" >
		    <aui:option selected='<%=resultLayout.equals("list") ? true : false %>' value="list">Plain list</aui:option>
		    <aui:option selected='<%=resultLayout.equals("thumbnailList") ? true : false %>' value="thumbnailList">List with thumbnails</aui:option>
		    <aui:option selected='<%=resultLayout.equals("userImageList") ? true : false %>' value="userImageList">List with user images</aui:option>
		    <aui:option selected='<%=resultLayout.equals("image") ? true : false %>' value="image">Image</aui:option> 
		</aui:select>
		
		<aui:input label="items-to-show" name="preferences--itemsToShow--" type="text" value="<%=itemsToShow %>" />

	</aui:fieldset>
	
	<aui:button-row>
		<aui:button type="submit"></aui:button>
	</aui:button-row>	

</liferay-frontend:edit-form>