package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryProperty;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetCategoryPropertyLocalService;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import fi.soveltia.liferay.gsearch.core.api.results.SearchResultCategory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component(
    immediate = true,
    service = ResultItemCommonService.class
)
public class ResultItemCommonService {

    private static final Log log = LogFactoryUtil.getLog(ResultItemCommonService.class);

    private GroupLocalService _groupLocalService;
    private LayoutLocalService _layoutLocalService;
    private AssetCategoryLocalService _assetCategoryLocalService;
    private AssetCategoryPropertyLocalService _assetCategoryPropertyLocalService;

    @Reference(unbind = "-")
    protected void setLayoutLocalService(
        LayoutLocalService layoutLocalService) {

        _layoutLocalService = layoutLocalService;
    }

    @Reference(unbind = "-")
    protected void setAssetCategoryLocalService(
        AssetCategoryLocalService assetCategoryLocalService) {

        _assetCategoryLocalService = assetCategoryLocalService;
    }

    @Reference(unbind = "-")
    protected void setAssetCategoryPropertyLocalService(
        AssetCategoryPropertyLocalService assetCategoryPropertyLocalService) {

        _assetCategoryPropertyLocalService = assetCategoryPropertyLocalService;
    }

    @Reference(unbind = "-")
    protected void setGroupLocalService(
        GroupLocalService groupLocalService) {

        _groupLocalService = groupLocalService;
    }

    String getGroupName(long groupId, Locale locale) {
        String groupName = "";
        try {
            Group group = _groupLocalService.getGroup(groupId);
            groupName = group.getDescriptiveName(locale);
        } catch (PortalException e) {
            log.warn(String.format("Group with id %s not found", groupId));
        }
        return groupName;
    }

    Layout getAssetLayout(long groupId, String link) throws Exception {

        if (link != null) {
            String regex = ".*https?://[\\w.-]+(/.*?)(?:/-/.*|)";

            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(link);

            if (matcher.matches()) {
                String fullPath = matcher.group(1);

                String friendlyURL = "";
                if (fullPath.startsWith("/group/")) {
                    List<String> path = Arrays.asList(fullPath.split("/"));
                    friendlyURL = "/" + String.join("/", path.subList(3, path.size()));
                } else {
                    friendlyURL = fullPath;
                }

                try {
                    return _layoutLocalService.getFriendlyURLLayout(groupId, true, friendlyURL);
                } catch (NoSuchLayoutException e) {
                    // do nothing
                }
            } else {
                log.warn(String.format("Link '%s' does not match with regex '%s", link, regex));
            }
        }
        return null;
    }

    SearchResultCategory[] getCategories(Document document, Locale locale) {
        String[] categoryIds = document.getValues(Field.ASSET_CATEGORY_IDS);

        List<SearchResultCategory> categories = new ArrayList<>();

        if (categoryIds != null) {
            for (String id : categoryIds) {
                try {
                    AssetCategory category = _assetCategoryLocalService.getCategory(Long.valueOf(id));
                    List<AssetCategoryProperty> properties = _assetCategoryPropertyLocalService.getCategoryProperties(category.getCategoryId());

                    String name = "";
                    String colorCode = "";
                    for (AssetCategoryProperty property : properties) {
                        if (property.getKey().equals("abbreviation")) {
                            name = property.getValue();
                        } else if (property.getKey().equals("colorCode")) {
                            colorCode = property.getValue();
                        }
                    }

                    if (name.isEmpty()) {
                        name = category.getTitle(locale);
                    }
                    SearchResultCategory searchResultCategory =
                        SearchResultCategory
                            .newBuilder()
                            .name(name)
                            .colorCode("#" + colorCode)
                            .build();
                    categories.add(searchResultCategory);
                } catch (PortalException e) {
                    log.error(String.format("Cannot get asset category for id %s", id));
                } catch (NumberFormatException e) {
                    // do nothing
                }
            }

        }
        return categories.toArray(new SearchResultCategory[] {});
    }

}
