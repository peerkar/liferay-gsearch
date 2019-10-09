<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>

<%@ page import="fi.soveltia.liferay.gsearch.morelikethis.constants.ModuleConfigurationKeys"%>
<%@ page import="fi.soveltia.liferay.gsearch.morelikethis.util.ConfigurationUtil" %>

<%@ page import="javax.portlet.PortletPreferences" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
	boolean appendRedirect = GetterUtil.getBoolean(portletPreferences.getValue(ModuleConfigurationKeys.APPEND_REDIRECT, "true"));
	boolean configured = GetterUtil.getBoolean(request.getAttribute("configured"));
	String currentResultLayout = portletPreferences.getValue(ModuleConfigurationKeys.RESULT_LAYOUT, "list");
	String getSearchResultsURL = (String)request.getAttribute(ModuleConfigurationKeys.SEARCH_RESULTS_URL);
%>