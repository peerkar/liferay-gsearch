package fi.soveltia.liferay.gsearch.query;

import com.liferay.portal.kernel.search.Query;

/**
 * Decay Function Score Query for GSearch.
 * 
 * Please see https://www.elastic.co/guide/en/elasticsearch/guide/current/decay-functions.html 
 * for more information.
 * 
 * @author Petteri Karttunen
 */
public class DecayFunctionScoreQuery extends FunctionScoreQuery implements Query {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor inherited from our base class.
	 * 
	 * @param query
	 */
	public DecayFunctionScoreQuery(String query) {
		super(query);
	}
	
	/**
	 * Get decay.
	 * 
	 * @return
	 */
	public Double getDecay() {
	
		return _decay;
	}

	/**
	 * Set decay
	 * 
	 * @param decay
	 */
	public void setDecay(Double decay) {
	
		_decay = decay;
	}
	
	/**
	 * Get field name.
	 * 
	 * @return
	 */
	public String getFieldName() {
	
		return fieldName;
	}

	/**
	 * Set field name.
	 * 
	 * @param fieldName
	 */
	public void setFieldName(String fieldName) {
	
		this.fieldName = fieldName;
	}

	/**
	 * Get function type
	 * 
	 * @return
	 */
	public String getFunctionType() {
		
		return _functionType;
	}

	/**
	 * Set function type
	 * 
	 * @param functionType
	 */
	
	public void setFunctionType(String functionType) {
	
		_functionType = functionType;
	}

	/**
	 * Get offset
	 * 
	 * @return
	 */
	public String getOffset() {
	
		return _offSet;
	}
	
	/**
	 *	Set offset.
	 *
	 * @param offset
	 */
	public void setOffset(String offSet) {
		_offSet = offSet;
	}	

	/**
	 * Get origin
	 * 
	 * @return
	 */
	public Object getOrigin() {
		
		return _origin;
	}

	/**
	 * Set origin
	 * 
	 * @param origin
	 */
	public void setOrigin(Object origin) {
	
		_origin = origin;
	}	
	/**
	 * Get scale.
	 * 
	 * @return
	 */
	public String getScale() {
	
		return _scale;
	}

	/**
	 *	Set scale.
	 *
	 * @param scale
	 */
	public void setScale(String scale) {
		_scale = scale;
	}	

	/**
	 * Get weight.
	 * 
	 * @return
	 */
	public Float getWeight() {
	
		return _weight;
	}

	/**
	 * Set weight.
	 * 
	 * @param weight
	 */
	public void setWeight(float weight) {
	
		_weight = weight;
	}

	private Double _decay;
	private String fieldName;
	private String _functionType;
	private Object _origin;
	private String _offSet;
	private String _scale;
	private Float _weight;
}