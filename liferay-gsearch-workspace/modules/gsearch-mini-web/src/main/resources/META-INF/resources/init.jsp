<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>

<%@ page import="fi.soveltia.liferay.gsearch.mini.web.constants.GSearchMiniWebKeys" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
	Boolean appendRedirect = (Boolean)request.getAttribute(GSearchMiniWebKeys.APPEND_REDIRECT);
	Integer queryMinLength = (Integer)request.getAttribute(GSearchMiniWebKeys.QUERY_MIN_LENGTH);
	Integer requestDelay = (Integer)request.getAttribute(GSearchMiniWebKeys.REQUEST_DELAY);
	Integer requestTimeout = (Integer)request.getAttribute(GSearchMiniWebKeys.REQUEST_TIMEOUT);
	String searchPageURL = (String)request.getAttribute(GSearchMiniWebKeys.SEARCHPAGE_URL);
	String suggestionsURL = (String)request.getAttribute(GSearchMiniWebKeys.SUGGESTIONS_URL);
	String suggestMode = (String)request.getAttribute(GSearchMiniWebKeys.SUGGEST_MODE);
%>