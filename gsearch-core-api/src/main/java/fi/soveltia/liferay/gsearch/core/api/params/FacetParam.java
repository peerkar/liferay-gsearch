package fi.soveltia.liferay.gsearch.core.api.params;

import com.liferay.portal.kernel.search.BooleanClauseOccur;

/**
 * Facet parameter pojo
 * 
 * @author Petteri Karttunen
 *
 */
public class FacetParam {

	String fieldName;
	String[] values;
	BooleanClauseOccur occur;
	
	public FacetParam(String fieldName, String[]values, BooleanClauseOccur occur) {
		this.fieldName = fieldName;
		this.values = values;
		this.occur = occur;
	}
	
	public String getFieldName() {
	
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
	
		this.fieldName = fieldName;
	}
	
	public String[] getValues() {
	
		return values;
	}
	
	public void setValues(String[] values) {
	
		this.values = values;
	}
	
	public BooleanClauseOccur getOccur() {
	
		return occur;
	}
	
	public void setOccur(BooleanClauseOccur occur) {
	
		this.occur = occur;
	}
	
}
