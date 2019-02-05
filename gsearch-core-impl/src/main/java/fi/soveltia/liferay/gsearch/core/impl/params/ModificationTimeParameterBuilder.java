
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.util.ParamUtil;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.params.FilterParameter;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

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
	public void addParameter(
		PortletRequest portletRequest, QueryContext queryContext)
		throws Exception {

		String timeFilterType =
			ParamUtil.getString(portletRequest, ParameterNames.TIME);

		Date timeFrom = null;

		Date timeTo = null;

		if ("range".equals(timeFilterType)) {

			String timeStartParameter = ParamUtil.getString(
				portletRequest, ParameterNames.TIME_FROM, "");

			String timeEndParameter =
				ParamUtil.getString(portletRequest, ParameterNames.TIME_TO, "");

			String datePickerFormat = ParamUtil.getString(
				portletRequest, ParameterNames.DATEPICKER_FORMAT, "yyyy-MM-dd");

			DateTimeFormatter rangeDateFormatter =
				DateTimeFormatter.ofPattern(datePickerFormat);

			timeFrom = getDateFromString(
				rangeDateFormatter, timeStartParameter, false);
			
			timeTo =
				getDateFromString(rangeDateFormatter, timeEndParameter, true);
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

		if (timeFrom != null || timeTo != null) {
		
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

	@Override
	public boolean validate(PortletRequest portletRequest)
		throws ParameterValidationException {

		return true;
	}

	/**
	 * Get date from string.
	 * 
	 * @param dateTimeFormatter
	 * @param dateString
	 * @param isRangeEnd
	 * @return
	 */
	protected Date getDateFromString(
		DateTimeFormatter dateTimeFormatter, String dateString,
		boolean isRangeEnd) {

		if (!dateString.isEmpty()) {

			try {
				LocalDate localDate =
					LocalDate.parse(dateString, dateTimeFormatter);

				// Use the beginning of next day as the range end date.

				if (isRangeEnd) {
					localDate = localDate.plusDays(1);
				}
				return GregorianCalendar.from(
					localDate.atStartOfDay(ZoneId.systemDefault())).getTime();

			}
			catch (NullPointerException | IllegalArgumentException
							| DateTimeParseException e) {
				_log.warn(
					String.format("Cannot get date from '%s'", dateString));
			}
		}
		return null;
	}

	private static final Logger _log =
		LoggerFactory.getLogger(ModificationTimeParameterBuilder.class);

}
