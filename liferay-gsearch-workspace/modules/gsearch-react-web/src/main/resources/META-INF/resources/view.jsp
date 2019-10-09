<%@ include file="/init.jsp" %>

<div id="<portlet:namespace />-root"></div>

<aui:script require="<%= mainRequire %>">
	var config = JSON.parse('<%=configuration.toJSONString() %>');
	main.default('<portlet:namespace />-root', config);
</aui:script>