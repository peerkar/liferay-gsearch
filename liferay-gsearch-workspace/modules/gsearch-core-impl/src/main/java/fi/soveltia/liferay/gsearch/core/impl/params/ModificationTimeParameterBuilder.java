
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.params.FilterParameter;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.impl.util.GSearchUtil;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modification time parameter builder.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ParameterBuilder.class
)
public class ModificationTimeParameterBuilder implements ParameterBuilder {

	@Override
	public void addParameter(QueryContext queryContext) throws Exception {
		PortletRequest portletRequest =
			GSearchUtil.getPortletRequestFromContext(queryContext);

		String timeFilterType = ParamUtil.getString(
			portletRequest, ParameterNames.TIME);

		if ("range".equals(timeFilterType)) {
			String timeStartParameter = ParamUtil.getString(
				portletRequest, ParameterNames.TIME_FROM, "");
			String timeEndParameter = ParamUtil.getString(
				portletRequest, ParameterNames.TIME_TO, "");
			String datePickerFormat = ParamUtil.getString(
				portletRequest, ParameterNames.DATE_FORMAT, "yyyy-MM-dd");

			addTimeParameters(
				queryContext, timeFilterType, timeStartParameter,
				timeEndParameter, datePickerFormat);
		}
		else {
			addTimeParameters(queryContext, timeFilterType, null, null, null);
		}
	}

	@Override
	public void addParameterHeadless(
			QueryContext queryContext, Map<String, Object> parameters)
		throws Exception {

		String timeFilterType = GetterUtil.getString(ParameterNames.TIME);

		if ("range".equals(timeFilterType)) {
			String timeStartParameter = GetterUtil.getString(
				parameters.get(ParameterNames.TIME_FROM), "");
			String timeEndParameter = GetterUtil.getString(
				parameters.get(ParameterNames.TIME_TO), "");
			String datePickerFormat = GetterUtil.getString(
				parameters.get(ParameterNames.DATE_FORMAT), "yyyy-MM-dd");

			addTimeParameters(
				queryContext, timeFilterType, timeStartParameter,
				timeEndParameter, datePickerFormat);
		}
		else {
			addTimeParameters(queryContext, timeFilterType, null, null, null);
		}
	}

	@Override
	public boolean validate(QueryContext queryContext)
		throws ParameterValidationException {

		return true;
	}

	@Override
	public boolean validateHeadless(
			QueryContext queryContext, Map<String, Object> parameters)
		throws ParameterValidationException {

		return true;
	}

	protected void addTimeParameters(
			QueryContext queryContext, String timeFilterType,
			String timeStartParameter, String timeEndParameter,
			String datePickerFormat)
		throws Exception {

		Date timeFrom = null;

		Date timeTo = null;

		if ("range".equals(timeFilterType)) {
			DateTimeFormatter rangeDateFormatter = DateTimeFormatter.ofPattern(
				datePickerFormat);

			timeFrom = getDateFromString(
				rangeDateFormatter, timeStartParameter, false);

			timeTo = getDateFromString(
				rangeDateFormatter, timeEndParameter, true);
		}
		else if ("last-day".equals(timeFilterType)) {
			Calendar calendar = Calendar.getInstance();

			calendar.add(Calendar.DAY_OF_MONTH, -1);
			timeFrom = calendar.getTime();
		}
		else if ("last-hour".equals(timeFilterType)) {
			Calendar calendar = Calendar.getInstance();

			calendar.add(Calendar.HOUR_OF_DAY, -1);
			timeFrom = calendar.getTime();
		}
		else if ("last-month".equals(timeFilterType)) {
			Calendar calendar = Calendar.getInstance();

			calendar.add(Calendar.MONTH, -1);
			timeFrom = calendar.getTime();
		}
		else if ("last-week".equals(timeFilterType)) {
			Calendar calendar = Calendar.getInstance();

			calendar.add(Calendar.WEEK_OF_MONTH, -1);
			timeFrom = calendar.getTime();
		}
		else if ("last-year".equals(timeFilterType)) {
			Calendar calendar = Calendar.getInstance();

			calendar.add(Calendar.YEAR, -1);
			timeFrom = calendar.getTime();
		}

		if ((timeFrom != null) || (timeTo != null)) {
			FilterParameter filter = new FilterParameter(ParameterNames.TIME);

			if (timeFrom != null) {
				filter.setAttribute("timeFrom", timeFrom);
			}

			if (timeTo != null) {
				filter.setAttribute("timeTo", timeTo);
			}

			queryContext.addFilterParameter(ParameterNames.TIME, filter);
		}
	}

	/**
	 * Get date from string.
	 *
	 * @param dateTimeFormatter
	 * @param dateString
	 * @param rangeEnd
	 * @return
	 */
	protected Date getDateFromString(
		DateTimeFormatter dateTimeFormatter, String dateString,
		boolean rangeEnd) {

		if (!dateString.isEmpty()) {
			try {
				LocalDate localDate = LocalDate.parse(
					dateString, dateTimeFormatter);

				// Use the beginning of next day as the range end date.

				if (rangeEnd) {
					localDate = localDate.plusDays(1);
				}

				return GregorianCalendar.from(
					localDate.atStartOfDay(ZoneId.systemDefault())
				).getTime();
			}
			catch (DateTimeParseException | IllegalArgumentException |
				   NullPointerException e) {

				_log.warn(
					String.format("Cannot get date from '%s'", dateString));
			}
		}

		return null;
	}

	private static final Logger _log = LoggerFactory.getLogger(
		ModificationTimeParameterBuilder.class);

}