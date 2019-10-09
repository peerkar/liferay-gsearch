
package fi.soveltia.liferay.gsearch.core.impl.results.item.processor;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.item.processor.ResultItemProcessor;

/**
 * Additional fields result item processor.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ResultItemProcessor.class
)
public class AdditionalFieldsResultItemProcessor implements ResultItemProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void process(
			QueryContext queryContext, Document document,
			ResultItemBuilder resultItemBuilder, JSONObject resultItem)
		throws Exception {

		Map<String, Class<?>> additionalResultFields =
			(Map<String, Class<?>>)queryContext.getParameter(
				ParameterNames.ADDITIONAL_RESULT_FIELDS);

		if (additionalResultFields != null) {

			// Loop for additional result fields. These have to be 1-1 index fields.

			for (Map.Entry<String, Class<?>> entry :
					additionalResultFields.entrySet()) {

				if (entry.getValue().isAssignableFrom(String[].class)) {
					List<Object>values = document.getValues(entry.getKey());

					if ((values != null) && (values.size() > 0)) {

						resultItem.put(entry.getKey(), values);
					}
				}
				else {
					String value = document.getString(entry.getKey());

					if (Validator.isNotNull(value)) {
						resultItem.put(entry.getKey(), value);
					}
				}
			}
		}
	}
}