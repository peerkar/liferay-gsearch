package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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

    private static GroupLocalService _groupLocalService;
    private static LayoutLocalService _layoutLocalService;

    @Reference(unbind = "-")
    protected void setLayoutLocalService(
        LayoutLocalService layoutLocalService) {

        _layoutLocalService = layoutLocalService;
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
            String regex = ".*https?://[\\w\\.]+(/.*?)(?:/-/.*|)";

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
            }
        }
        return null;
    }
}
