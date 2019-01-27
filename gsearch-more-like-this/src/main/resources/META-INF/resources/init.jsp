<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.util.Constants"%>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil"%>

<%@ page import="javax.portlet.PortletPreferences"%>

<%@page import="fi.soveltia.liferay.gsearch.morelikethis.util.ConfigurationUtil"%>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%

	boolean isConfigured = 	GetterUtil.getBoolean(request.getAttribute("isConfigured"), false);

	String getSearchResultsURL = (String)request.getAttribute("searchResultsURL");
	
	String currentResultLayout = portletPreferences.getValue("resultLayout", "list");

	boolean appendRedirect = GetterUtil.getBoolean(portletPreferences.getValue("appendRedirect", "true"));

%>
