
package fi.soveltia.liferay.gsearch.query;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.search.query.FunctionScoreQuery;
import com.liferay.portal.search.query.Query;

/**
 * Field value factor function score query.
 *
 * See https://www.elastic.co/guide/en/elasticsearch/reference/6.5/query-dsl-function-score-query.html
 *
 * @author Petteri Karttunen
 */
public class GSearchFieldValueFactorFunctionScoreQuery
	extends GSearchFunctionScoreQuery implements Query, FunctionScoreQuery {

	/**
	 * Default constructor inherited from our base class.
	 *
	 * @param query
	 */
	public GSearchFieldValueFactorFunctionScoreQuery(Query query) {
		super(query);
	}

	public Float getFactor() {
		return _factor;
	}

	public String getFieldName() {
		return _fieldName;
	}

	public Double getMissing() {
		return _missing;
	}

	public String getModifier() {
		return _modifier;
	}

	public void setFactor(Float factor) {
		_factor = factor;
	}

	public void setFieldName(String fieldName) {
		_fieldName = fieldName;
	}

	public void setMissing(Double missing) {
		_missing = missing;
	}

	public void setModifier(String modifier) {
		_modifier = modifier;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(29);

		sb.append(", boost=");
		sb.append(super.getBoost());
		sb.append(", boostMode=");
		sb.append(super.getBoostMode());
		sb.append(", factor=");
		sb.append(_factor);
		sb.append(", field_name=");
		sb.append(_fieldName);
		sb.append(", missing=");
		sb.append(_missing);
		sb.append(", modifer=");
		sb.append(_modifier);
		sb.append("}");

		return sb.toString();
	}

	private static final long serialVersionUID = 1L;

	private Float _factor;
	private String _fieldName;
	private Double _missing;
	private String _modifier;

}