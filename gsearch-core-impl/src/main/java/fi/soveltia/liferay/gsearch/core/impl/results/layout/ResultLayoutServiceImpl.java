
package fi.soveltia.liferay.gsearch.core.impl.results.layout;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import fi.soveltia.liferay.gsearch.core.api.results.layout.ResultLayout;
import fi.soveltia.liferay.gsearch.core.api.results.layout.ResultLayoutService;
import fi.soveltia.liferay.gsearch.core.impl.configuration.ModuleConfiguration;

@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.impl.configuration.ModuleConfiguration", 
	immediate = true, 
	service = ResultLayoutService.class
)
public class ResultLayoutServiceImpl implements ResultLayoutService {

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	@Override
	public List<ResultLayout> getResultLayouts() {

		return _resultLayouts;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param portletRequest
	 * @return
	 */
	@Override
	public List<ResultLayout> getAvailableResultLayouts(
		PortletRequest portletRequest) {

		List<ResultLayout> availableResultLayouts =
			new ArrayList<ResultLayout>();

		for (ResultLayout resultLayout : _resultLayouts) {

			if (checkResultLayoutFilters(portletRequest, resultLayout)) {
				availableResultLayouts.add(resultLayout);
			}
		}
		return availableResultLayouts;
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public String getDefaultResultLayoutKey() {
		return _moduleConfiguration.defaultResultLayout();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public boolean isValidResultLayoutKey(String key) {

		for (ResultLayout resultLayout : _resultLayouts) {
			if (resultLayout.getKey().equals(key)) {
				return true;
			}
		}
		return false;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}
	
	/**
	 * Add result layout.
	 * 
	 * @param resultLayout
	 */
	protected void addResultLayout(ResultLayout resultLayout) {
		if (_resultLayouts == null) {
			_resultLayouts = new ArrayList<ResultLayout>();
		}
	}

	/**
	 * Remove a result layout from list.
	 * 
	 * @param resultLayout
	 */
	protected void removeResultLayout(ResultLayout resultLayout) {

		_resultLayouts.remove(resultLayout);
	}

	/**
	 * Check whether filter conditions for showing a particular result layout
	 * are met.
	 * 
	 * @param portletRequest
	 * @param resultLayout
	 * @return
	 */
	private boolean checkResultLayoutFilters(
		PortletRequest portletRequest, ResultLayout resultLayout) {

		boolean showLayout = true;

		String filterParam;

		// Check OR filters.

		Map<String, String> paramFiltersOR = resultLayout.getParamFiltersOR();

		if (paramFiltersOR != null) {

			showLayout = false;

			for (Entry<String, String> entry : paramFiltersOR.entrySet()) {
				filterParam =
					ParamUtil.getString(portletRequest, entry.getKey(), null);
				if (entry.getValue().equals(filterParam)) {
					showLayout = true;
					break;
				}
			}
		}

		// Continue loop if OR filters not valid

		if (!showLayout) {
			return false;
		}

		// Check AND filters.

		Map<String, String> paramFiltersAND = resultLayout.getParamFiltersAND();

		if (paramFiltersAND != null) {

			for (Entry<String, String> entry : paramFiltersAND.entrySet()) {
				filterParam =
					ParamUtil.getString(portletRequest, entry.getKey(), null);
				if (!entry.getValue().equals(filterParam)) {
					showLayout = false;
					break;
				}
			}
		}

		return showLayout;
	}
	
	private volatile ModuleConfiguration _moduleConfiguration;

	@Reference(
		bind = "addResultLayout", 
		cardinality = ReferenceCardinality.AT_LEAST_ONE, 
		policy = ReferencePolicy.DYNAMIC, 
		policyOption = ReferencePolicyOption.GREEDY,
		service = ResultLayout.class, 
		unbind = "removeResultLayout"
	)
	private volatile List<ResultLayout> _resultLayouts = null;
}
