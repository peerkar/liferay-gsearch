<%@page import="javax.portlet.PortletPreferences"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>

<%@ include file="/init.jsp" %>

<%
	
	PortletPreferences preferences = renderRequest.getPreferences();
	String queryConfiguration = preferences.getValue("queryConfiguration", "");
	String resultLayout = preferences.getValue("resultLayout", "list");
	String itemsToShow = preferences.getValue("itemsToShow", "5");
	String classNames = preferences.getValue("classNames", "");

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
		
		<aui:input label="query-configuration-name" name="preferences--queryConfiguration--" type="textarea" value="<%=queryConfiguration %>" />

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