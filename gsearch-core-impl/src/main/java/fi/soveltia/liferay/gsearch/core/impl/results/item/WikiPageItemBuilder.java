
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.wiki.model.WikiPage;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;

/**
 * Wiki page result item builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	service = ResultItemBuilder.class
)
public class WikiPageItemBuilder extends BaseResultItemBuilder
	implements ResultItemBuilder {

	@Override
	public boolean canBuild(String name) {

		return NAME.equals(name);
	}

	private static final String NAME = WikiPage.class.getName();
}
