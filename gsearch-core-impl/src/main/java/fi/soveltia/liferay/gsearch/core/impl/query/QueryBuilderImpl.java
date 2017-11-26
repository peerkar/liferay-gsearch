
package fi.soveltia.liferay.gsearch.core.impl.query;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringBundler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.query.QueryBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.query.ct.CTQueryBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.filter.QueryFilterBuilder;
import fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration;
import fi.soveltia.liferay.gsearch.query.Operator;
import fi.soveltia.liferay.gsearch.query.QueryStringQuery;

/**
 * Query builder implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration", 
	immediate = true, 
	service = QueryBuilder.class
)
public class QueryBuilderImpl implements QueryBuilder {


	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_gSearchConfiguration = ConfigurableUtil.createConfigurable(
			GSearchConfiguration.class, properties);
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public BooleanQuery buildQuery(
		PortletRequest portletRequest, QueryParams queryParams)
		throws Exception {
		
		// Build query

		BooleanQuery query = abuildQuery(portletRequest, queryParams);

		// Add filters

		BooleanFilter preBooleanFilter =
			_queryFilterBuilder.buildQueryFilter(portletRequest, queryParams);
		
		query.setPreBooleanFilter(preBooleanFilter);

		return query;
	}
	
	/**
	 * Build query.
     * 
	 * We are using here the custom QueryStringQuery type which is an
	 * extension of Liferay StringQuery. Thus, if you don't want to use
	 * the custom search adapter, this falls silently to the default StringQuery.
	 * Remember however that with standard adapter you loose the possibility to
	 * define target fields or boosts (configuration) - or, they just don't get applied.
	 * 
	 * @param queryParams
	 * @return
	 * @throws Exception
	 */
	protected BooleanQuery abuildQuery(PortletRequest portletRequest, QueryParams queryParams) throws Exception {
		
		BooleanQuery query = new BooleanQueryImpl();
		
		StringBundler searchPhrase = new StringBundler();
		
		searchPhrase.append(queryParams.getKeywords());
		
		// Content targeting
		
		if (_gSearchConfiguration.enableAudienceTargeting()) {
		
			String ctQuery = _ctQueryBuilder.buildCTQuery(portletRequest);
	
			if (ctQuery != null) {
				searchPhrase.append(" ").append(ctQuery);
			}
		}		
		
		// Build query
		
		QueryStringQuery queryStringQuery = new QueryStringQuery(searchPhrase.toString());

		JSONArray configurationArray = JSONFactoryUtil.createJSONArray(_gSearchConfiguration.searchFieldConfiguration());
		
		for (int i = 0; i < configurationArray.length(); i++) {
			
			JSONObject item = configurationArray.getJSONObject(i);

			// Add non translated version
			
			String fieldName = item.getString("fieldName");
			float boost =  GetterUtil.getFloat(item.getString("boost"), 1f);

			queryStringQuery.addField(fieldName, boost);
			
			// Add translated version
			
			boolean isLocalized = GetterUtil.getBoolean(item.get("localized"), false);
			
			if (isLocalized) {
				
				String localizedFieldName = fieldName + "_" + queryParams.getLocale().toString();
				float localizedBoost =  GetterUtil.getFloat(item.getString("boostForLocalizedVersion"), 1f);

				queryStringQuery.addField(localizedFieldName, localizedBoost);
			}
		}
		
		// Set default operator AND
 		
		queryStringQuery.setDefaultOperator(Operator.AND);
		
		query.add(queryStringQuery, BooleanClauseOccur.MUST);
		
		return query;
	}	

	@Reference(unbind = "-")
	protected void setCTQueryBuilder(CTQueryBuilder ctQueryBuilder) {

		_ctQueryBuilder = ctQueryBuilder;
	}

	
	@Reference(unbind = "-")
	protected void setPortal(Portal portal) {

		_portal = portal;
	}

	@Reference(unbind = "-")
	protected void setQueryFilterBuilder(QueryFilterBuilder queryFilterBuilder) {

		_queryFilterBuilder = queryFilterBuilder;
	}

	public static final DateFormat INDEX_DATE_FORMAT =
					new SimpleDateFormat("yyyyMMddHHmmss");

	protected volatile GSearchConfiguration _gSearchConfiguration;

	@Reference
	protected CTQueryBuilder _ctQueryBuilder;
	
	@Reference
	protected Portal _portal;

	@Reference
	protected QueryFilterBuilder _queryFilterBuilder;

	@SuppressWarnings("unused")
	private static final Log _log =
		LogFactoryUtil.getLog(QueryBuilderImpl.class);
}
