
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ParamUtil;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Entry class name parameter builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ParameterBuilder.class
)
public class AssetTypeParameterBuilder implements ParameterBuilder {

	@Override
	public void addParameter(
		PortletRequest portletRequest, QueryContext queryContext)
		throws Exception {
		
		String assetType =
			ParamUtil.getString(portletRequest, ParameterNames.ASSET_TYPE);

		List<String> entryClassNames = new ArrayList<String>();

		String entryClassName = parseEntryClass(
			assetType, queryContext.getConfiguration(
				ConfigurationKeys.ENTRY_CLASS_NAME));

		if (entryClassName != null) {
			entryClassNames.add(entryClassName);
			queryContext.setParameter(
				ParameterNames.ENTRY_CLASS_NAMES, entryClassNames);
		}
	}

	@Override
	public boolean validate(PortletRequest portletRequest)
		throws ParameterValidationException {

		return true;
	}
	
	/**
	 * Parse entry classname corresponding the key.
	 * 
	 * @param key
	 * @param configuration
	 * @return
	 * @throws JSONException
	 * @throws ClassNotFoundException
	 */
	protected String parseEntryClass(String key, String[] configuration)
		throws JSONException, ClassNotFoundException {

		String entryClassName = null;

		for (int i = 0; i < configuration.length; i++) {

			JSONObject item =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			if (key.equals(item.getString("key"))) {

				entryClassName = item.getString("entry_class_name");
				break;
			}
		}

		return entryClassName;
	}
}
