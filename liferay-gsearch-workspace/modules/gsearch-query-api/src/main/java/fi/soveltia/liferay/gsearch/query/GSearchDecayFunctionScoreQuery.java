package fi.soveltia.liferay.gsearch.query;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.search.query.FunctionScoreQuery;
import com.liferay.portal.search.query.Query;

/**
 * Decay Function Score Query.
 *
 * See https://www.elastic.co/guide/en/elasticsearch/guide/current/decay-functions.html
 *
 * @author Petteri Karttunen
 */
public class GSearchDecayFunctionScoreQuery
	extends GSearchFunctionScoreQuery implements Query, FunctionScoreQuery {

	public GSearchDecayFunctionScoreQuery(Query query) {
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
	 * Get field name.
	 *
	 * @return
	 */
	public String getFieldName() {
		return _fieldName;
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
	 * Get multivalue mode
	 *
	 * @return
	 */
	public String getMultiValueMode() {
		return _multiValueMode;
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
	 * Get origin
	 *
	 * @return
	 */
	public Object getOrigin() {
		return _origin;
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
	 * Get weight.
	 *
	 * @return
	 */
	public Float getWeight() {
		return _weight;
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
	 * Set field name.
	 *
	 * @param fieldName
	 */
	public void setFieldName(String fieldName) {
		_fieldName = fieldName;
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
	 * Set multivalue mode
	 *
	 * Possible values:
	 * 		min: Distance is the minimum distance
	 * 		max: Distance is the maximum distance
	 * 		avg: Distance is the average distance
	 * 		sum: Distance is the sum of all distances
	 *
	 * @param multiValueMode
	 */
	public void setMultiValueMode(String multiValueMode) {
		_multiValueMode = multiValueMode;
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
	 * Set origin
	 *
	 * @param origin
	 */
	public void setOrigin(Object origin) {
		_origin = origin;
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
	 * Set weight.
	 *
	 * @param weight
	 */
	public void setWeight(float weight) {
		_weight = weight;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(29);

		sb.append(", boost=");
		sb.append(super.getBoost());
		sb.append(", boostMode=");
		sb.append(super.getBoostMode());
		sb.append(", decay=");
		sb.append(_decay);
		sb.append(", field_name=");
		sb.append(_fieldName);
		sb.append(", function_type=");
		sb.append(_functionType);
		sb.append(", multivalue_mode=");
		sb.append(_multiValueMode);
		sb.append(", offset=");
		sb.append(_offSet);
		sb.append(", origin=");
		sb.append(_origin);
		sb.append(", scale=");
		sb.append(_scale);
		sb.append(", weight=");
		sb.append(_weight);
		sb.append(", max_boost=");
		sb.append(super.getMaxBoost());
		sb.append(", min_score=");
		sb.append(super.getMinScore());
		sb.append("}");

		return sb.toString();
	}

	private static final long serialVersionUID = 1L;

	private Double _decay;
	private String _fieldName;
	private String _functionType;
	private String _multiValueMode;
	private String _offSet;
	private Object _origin;
	private String _scale;
	private Float _weight;
}