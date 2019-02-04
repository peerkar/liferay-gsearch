
package fi.soveltia.liferay.gsearch.resultitemtagger.results.item.processor;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.Validator;

import java.util.Arrays;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.item.processor.ResultItemProcessor;
import fi.soveltia.liferay.gsearch.resultitemtagger.configuration.ModuleConfiguration;

/**
 * Results item tag highlighter implementation.
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

		return _moduleConfiguration.enableFeature();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryContext queryParams,
		Document document, ResultItemBuilder resultItemBuilder,
		JSONObject resultItem)
		throws Exception {

		if (!isEnabled()) {
			return;
		}

		String fieldName = _moduleConfiguration.fieldName();
		String fieldValue = _moduleConfiguration.fieldValue();
		String property = _moduleConfiguration.property();

		if (Validator.isNull(fieldName) || Validator.isNull(fieldValue) ||
			Validator.isNull(property)) {
			_log.warn("Configuration is invalid or missing. Not proceeding.");
			return;
		}

		String[] values = document.getValues(fieldName);

		if (Arrays.stream(values).anyMatch(fieldValue::equals)) {
			resultItem.put(property, true);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}

	private static final Logger _log =
		LoggerFactory.getLogger(TaggerResultItemProcessor.class);

	private volatile ModuleConfiguration _moduleConfiguration;

}
