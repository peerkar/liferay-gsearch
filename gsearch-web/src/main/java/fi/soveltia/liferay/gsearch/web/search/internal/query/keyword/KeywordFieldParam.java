
package fi.soveltia.liferay.gsearch.web.search.internal.query.keyword;

/**
 * Keyword field param pojo.
 * 
 * @author Petteri Karttunen
 */
public class KeywordFieldParam {

	private Float boost;
	boolean localized;
	private String name;

	public KeywordFieldParam(String name, boolean localized, Float boost) {
		this.name = name;
		this.localized = localized;
		this.boost = boost;
	}

	public float getBoost() {

		return boost;
	}

	public void setBoost(float boost) {

		this.boost = boost;
	}

	public boolean isLocalized() {

		return localized;
	}

	public void setLocalized(boolean localized) {

		this.localized = localized;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

}
