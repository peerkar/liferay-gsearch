
package fi.soveltia.lifefay.gsearch.hy.util;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Localization helper util implementation.
 *
 * @author Petteri Karttunen
 */
@Component(immediate = true, service = LocalizationHelper.class)
public class LocalizationHelperImpl implements LocalizationHelper {

	@Override
	public String getLocalization(
		Locale locale, String key, Object... objects) {

		if (_resourceBundle == null) {
			_resourceBundle =
				_resourceBundleLoader.loadResourceBundle(locale.toString());
		}

		String value =
			ResourceBundleUtil.getString(_resourceBundle, key, objects);

		return value == null ? _language.format(locale, key, objects) : value;
	}

	@Reference
	private Language _language;

	private ResourceBundle _resourceBundle;

	@Reference(
		target = "(bundle.symbolic.name=fi.soveltia.liferay.gsearch.hy)",
		unbind = "-"
	)
	private ResourceBundleLoader _resourceBundleLoader;
}
