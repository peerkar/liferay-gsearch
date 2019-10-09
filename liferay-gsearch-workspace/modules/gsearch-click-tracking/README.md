# Liferay GSearch Click Tracking

Lets you to track the user behaviour by tracking clicks on the search results in the main portlet.

Service stores the following information about the clicks. No personalized / identifiable information is stored:

	<column name="keywords" type="String" />
	<column name="entryClassPK" type="long" />
	<column name="clickCount" type="int" />
	
This information can be used to improve search results relevancy. Currently it's used with the Learn to Rank functionality. 

## Configuration

After this module has been installed, the tracking has to enabled in the Liferay Gsearch React Web module.

The Learn To Rank functionality and index has to be setup manually. See folder `gsearch-learn-to-rank-scripts` for more information.

## Changelog

### 2019-10-01 (Version 1.0.0)

* Initial version