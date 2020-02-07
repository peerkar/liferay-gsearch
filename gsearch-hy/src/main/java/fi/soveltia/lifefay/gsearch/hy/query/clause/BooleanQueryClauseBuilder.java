package fi.soveltia.lifefay.gsearch.hy.query.clause;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import org.osgi.service.component.annotations.Component;

@Component(
    immediate = true,
    service = ClauseBuilder.class
)
public class BooleanQueryClauseBuilder implements ClauseBuilder {

    private static final String QUERY_TYPE = "boolean";
    private static final Log log = LogFactoryUtil.getLog(BooleanQueryClauseBuilder.class);

    @Override
    public boolean canBuild(String queryType) {
        return (queryType.equals(QUERY_TYPE));
    }

    @Override
    public Query buildClause(QueryContext queryContext, JSONObject configuration) throws Exception {

        BooleanQuery booleanQuery = new BooleanQueryImpl();

        JSONArray queries = configuration.getJSONArray("queries");

        if (Validator.isNull(queries) || (queries.length() == 0)) {
            return null;
        }

        for (int i = 0; i < queries.length(); i++) {
            JSONObject queryObj = queries.getJSONObject(i);
            if (Validator.isNotNull(queryObj)) {

                String fieldName = queryObj.getString("field_name");
                String query = queryObj.getString("query");
                BooleanClauseOccur occur = getOccur(queryObj.getString("occur"));

                query = query.replace("$_keywords_$", queryContext.getKeywords());

                if (Validator.isNull(fieldName) || Validator.isNull(query) || Validator.isNull(occur)) {
                    log.warn(String.format("Invalid boolean query: %s", queryObj.toJSONString()));
                    return null;
                }

                BooleanQuery bq = new BooleanQueryImpl();
                bq.addExactTerm(fieldName, query);
                booleanQuery.add(bq, occur);
            }
        }

        if (Validator.isNotNull(configuration.get("boost"))) {
            booleanQuery.setBoost(GetterUtil.getFloat(configuration.get("boost")));
        }

        return booleanQuery.hasClauses() ? booleanQuery : null;
    }

    private BooleanClauseOccur getOccur(String occur) {
        if (Validator.isNotNull(occur)) {
            occur = occur.toLowerCase().trim();
            switch (occur) {
                case "must":
                    return BooleanClauseOccur.MUST;
                case "must_not":
                    return BooleanClauseOccur.MUST_NOT;
                case "should":
                    return BooleanClauseOccur.SHOULD;
            }
        }
        return null;
    }

}
