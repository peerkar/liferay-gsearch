<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@page import="fi.soveltia.liferay.gsearch.mini.web.constants.GSearchMiniWebKeys"%>
<%@page import="fi.soveltia.liferay.gsearch.mini.web.constants.GSearchMiniPortletKeys"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
	Boolean autoCompleteEnabled = (Boolean)request.getAttribute(GSearchMiniWebKeys.AUTO_COMPLETE_ENABLED);
	Integer autoCompleteRequestDelay = (Integer)request.getAttribute(GSearchMiniWebKeys.AUTO_COMPLETE_REQUEST_DELAY);
	Integer queryMinLength = (Integer)request.getAttribute(GSearchMiniWebKeys.QUERY_MIN_LENGTH);
	Integer requestTimeout = (Integer)request.getAttribute(GSearchMiniWebKeys.REQUEST_TIMEOUT);
	String searchPageURL = (String)request.getAttribute(GSearchMiniWebKeys.SEARCHPAGE_URL);
	String suggestionsURL = (String)request.getAttribute(GSearchMiniWebKeys.SUGGESTIONS_URL);	
	String suggestMode = (String)request.getAttribute(GSearchMiniWebKeys.SUGGEST_MODE);
%>
