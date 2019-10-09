<%@ include file="init.jsp" %>

<portlet:actionURL name="<%= MVCActionCommandNames.IMPORT %>" var="importActionURL" />

<h1>A Simplistic Wikipedia Article Importer</h1>

<p>This portlet is meant for creating test data for Liferay GSearch application. For testing purposes only.</p>

<aui:form action="<%= importActionURL %>" name="fm">
	<aui:input label="Comma separated list of Wikipedia articles, for example: Software,Liferay" name="wikiArticles" />
	<aui:input label="Comma separated list of user Ids to be used as Liferay JournalArticle creators" name="userIds" value="<%= themeDisplay.getUserId() %>" />
	<aui:input label="Comma separated list of group Ids to be used as Liferay JournalArticle groups" name="groupIds" value="<%= themeDisplay.getScopeGroupId() %>" />
	<aui:input label="Language Id for the JournalArticles" name="languageId" value="<%= themeDisplay.getLanguageId() %>" />
	<aui:input label="Number of articles to import" name="limit" value="100" />

	<aui:button-row>
		<aui:button cssClass="btn btn-primary" type="submit" />
	</aui:button-row>
</aui:form>