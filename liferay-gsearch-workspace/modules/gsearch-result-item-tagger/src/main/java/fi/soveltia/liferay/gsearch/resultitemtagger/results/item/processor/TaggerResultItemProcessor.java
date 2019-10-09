
package fi.soveltia.liferay.gsearch.resultitemtagger.results.item.processor;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.item.processor.ResultItemProcessor;
import fi.soveltia.liferay.gsearch.resultitemtagger.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.resultitemtagger.constants.TaggerConfigurationKeys;

/**
 * Results item tagger.
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.resultitemtagger.configuration.ModuleConfiguration",
	immediate = true,
	service = ResultItemProcessor.class
)
public class TaggerResultItemProcessor implements ResultItemProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {

		return _moduleConfiguration.isEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(QueryContext queryParams, 
			Document document, ResultItemBuilder resultItemBuilder,
			JSONObject resultItem) throws Exception {

		if (!isEnabled()) {
			return;
		}

		String[] rules = _moduleConfiguration.rules();

		if (rules == null || rules.length == 0) {
			return;
		}

		for (int i = 0; i < rules.length; i++) {

			JSONObject rule = JSONFactoryUtil.createJSONObject(rules[i]);

			String matchFieldName = rule.getString(
					TaggerConfigurationKeys.MATCH_FIELD_NAME);
			Boolean matchFieldMultiValued = rule.getBoolean(
					TaggerConfigurationKeys.MATCH_FIELD_MULTIVALUED, false);
			String matchValue = rule.getString(
					TaggerConfigurationKeys.MATCH_VALUE);
			String propertyFieldName = rule.getString(
					TaggerConfigurationKeys.TAG_PROPERTY_NAME);
			String propertyFieldValue = rule.getString(
					TaggerConfigurationKeys.TAG_PROPERTY_VALUE);

			if (Validator.isBlank(matchFieldName) || Validator.isBlank(matchValue)
					|| Validator.isBlank(propertyFieldName) 
					|| Validator.isBlank(propertyFieldValue)) {
				_log.warn("Rule configuration is missing properties. Not proceeding.");
				continue;
			}

			boolean match = false;

			if (matchFieldMultiValued) {
				List<Object> values = document.getValues(matchFieldName);

				String[] strings = values.stream().toArray(String[]::new);
				
				if (Arrays.stream(strings).anyMatch(matchValue::equals)) {
					match = true;
				}

			} else {
				String value = document.getString(matchFieldName);

				if (value.equals(matchValue)) {
					match = true;
				}
			}

			if (match) {

				if (_log.isDebugEnabled()) {
					_log.debug("Tagger match. Adding " 
							+ propertyFieldName + "=" + propertyFieldValue
							+ " to the result item.");
				}

				resultItem.put(propertyFieldName, propertyFieldValue);
			}
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_moduleConfiguration = 
				ConfigurableUtil.createConfigurable(
						ModuleConfiguration.class, properties);
	}

	private static final Logger _log = LoggerFactory.getLogger(
			TaggerResultItemProcessor.class);

	private volatile ModuleConfiguration _moduleConfiguration;

}