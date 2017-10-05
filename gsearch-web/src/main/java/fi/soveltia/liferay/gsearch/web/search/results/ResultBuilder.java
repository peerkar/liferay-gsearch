
package fi.soveltia.liferay.gsearch.web.search.results;

/**
 * Result Builder Interface
 * 
 * @author Petteri Karttunen
 */
public interface ResultBuilder {

	/**
	 * Get hit date
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getDate()
		throws Exception;

	/**
	 * Get description
	 * 
	 * @return
	 */
	public String getDescription()
		throws Exception;

	/**
	 * Get Link
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getLink()
		throws Exception;

	/**
	 * Get Title
	 * 
	 * @return
	 */
	public String getTitle()
		throws Exception;

	/**
	 * Get Type
	 * 
	 * @return
	 */
	public String getType()
		throws Exception;

}
