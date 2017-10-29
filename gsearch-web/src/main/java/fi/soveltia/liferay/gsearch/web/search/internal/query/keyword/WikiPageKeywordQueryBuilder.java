package fi.soveltia.liferay.gsearch.web.search.internal.query.keyword;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Query;

import java.util.ArrayList;

import org.osgi.service.component.annotations.Component;

/**
 * Wiki page keyword query builder.
 * 
 * @author Petteri Karttunen
 *
 */
@Component(
	immediate = true
)
public class WikiPageKeywordQueryBuilder extends BaseKeywordQueryBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query buildKeywordQuery()
		throws Exception {

		// TODO Auto-generated method stub
		return null;
	}
		
	static {
		_keywordFieldParams = new ArrayList<KeywordFieldParam>();		
		
		_keywordFieldParams.add(new KeywordFieldParam(Field.TITLE, false, 3f));
		_keywordFieldParams.add(new KeywordFieldParam(Field.CONTENT, false, null));
	}
}