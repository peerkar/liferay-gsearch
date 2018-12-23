<%@ include file="/init.jsp" %>

<c:choose>
	<c:when test="<%= suggestMode.equals("contents") %>">
		<%@ include file="/suggest_mode/contents.jsp" %>
	</c:when>
	<c:otherwise>
		<%@ include file="/suggest_mode/keywords.jsp" %>
	</c:otherwise>
</c:choose>

