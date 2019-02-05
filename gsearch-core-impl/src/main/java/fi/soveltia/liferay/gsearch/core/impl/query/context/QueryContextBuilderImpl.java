
package fi.soveltia.liferay.gsearch.core.impl.query.context;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContextBuilder;

/**
 * Query params builder implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = QueryContextBuilder.class
)
public class QueryContextBuilderImpl implements QueryContextBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryContext buildQueryContext(
		PortletRequest portletRequest, int pageSize)
		throws Exception {

		return buildQueryContext(
			portletRequest, null, null, null, null, pageSize);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryContext buildQueryContext(
		PortletRequest portletRequest, String[] filterConfiguration,
		String[] clauseConfiguration, String[] facetConfiguration,
		String[] sortConfiguration, int pageSize)
		throws Exception {

		QueryContext queryContext = new QueryContext();

		queryContext.setPageSize(pageSize);

		setConfigurations(
			queryContext, filterConfiguration, clauseConfiguration,
			facetConfiguration, sortConfiguration);

		// Run parameter builders

		if (_parameterBuilders == null) {
			throw new RuntimeException();
		}

		for (ParameterBuilder parameterBuilder : _parameterBuilders) {

			if (parameterBuilder.validate(portletRequest)) {
				parameterBuilder.addParameter(portletRequest, queryContext);
			}
		}

		return queryContext;
	}

	/**
	 * Set configurations.
	 * 
	 * @param assetTypeConfiguration
	 * @param clauseConfiguration
	 * @param facetConfiguration
	 * @param sortConfiguration
	 */
	protected void setConfigurations(
		QueryContext queryContext, String[] filterConfiguration,
		String[] clauseConfiguration, String[] facetConfiguration,
		String[] sortConfiguration) {

		if (filterConfiguration == null) {
			queryContext.setConfiguration(ConfigurationKeys.FILTER,
				_configurationHelper.getFilterConfiguration());
		}

		if (clauseConfiguration == null) {
			queryContext.setConfiguration(ConfigurationKeys.CLAUSE,
				_configurationHelper.getClauseConfiguration());
		}

		if (facetConfiguration == null) {
			queryContext.setConfiguration(ConfigurationKeys.FACET,
				_configurationHelper.getFacetConfiguration());
		}

		if (sortConfiguration == null) {
			queryContext.setConfiguration(ConfigurationKeys.SORT,
				_configurationHelper.getSortConfiguration());
		}
	}

	/**
	 * Add parameter builder.
	 * 
	 * @param clauseBuilder
	 */
	protected void addParameterBuilder(ParameterBuilder parameterBuilder) {

		if (_parameterBuilders == null) {
			_parameterBuilders = new ArrayList<ParameterBuilder>();
		}
		_parameterBuilders.add(parameterBuilder);
	}

	/**
	 * Remove parameter builder.
	 * 
	 * @param parameterBuilder
	 */
	protected void removeParameterBuilder(ParameterBuilder parameterBuilder) {

		_parameterBuilders.remove(parameterBuilder);
	}

	@Reference(
		bind = "addParameterBuilder", 
		cardinality = ReferenceCardinality.MULTIPLE, 
		policy = ReferencePolicy.DYNAMIC, 
		service = ParameterBuilder.class, 
		unbind = "removeParameterBuilder"
	)
	private volatile List<ParameterBuilder> _parameterBuilders = null;

	@Reference
	private ConfigurationHelper _configurationHelper;
}
