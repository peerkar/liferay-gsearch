
package fi.soveltia.liferay.gsearch.query;

import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.util.StringBundler;

public class FieldValueFactorFunctionScoreQuery extends FunctionScoreQuery
	implements Query {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor inherited from our base class.
	 * 
	 * @param query
	 */
	public FieldValueFactorFunctionScoreQuery(String query) {

		super(query);
	}

	public Float getFactor() {

		return _factor;
	}

	public void setFactor(Float factor) {

		_factor = factor;
	}

	public String getFieldName() {

		return _fieldName;
	}

	public void setFieldName(String fieldName) {

		_fieldName = fieldName;
	}

	public Double getMissing() {

		return _missing;
	}

	public void setMissing(Double missing) {

		_missing = missing;
	}

	public String getModifier() {

		return _modifier;
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

	private Float _factor;
	private String _fieldName;
	private Double _missing;
	private String _modifier;
}
