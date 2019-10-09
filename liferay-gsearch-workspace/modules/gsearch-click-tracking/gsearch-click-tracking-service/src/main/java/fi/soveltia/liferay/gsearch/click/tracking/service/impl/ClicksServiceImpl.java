/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package fi.soveltia.liferay.gsearch.click.tracking.service.impl;

import com.liferay.portal.aop.AopService;

import java.util.List;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.click.tracking.model.Clicks;
import fi.soveltia.liferay.gsearch.click.tracking.service.base.ClicksServiceBaseImpl;

/**
 * The implementation of the clicks remote service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the <code>fi.soveltia.liferay.gsearch.click.tracking.service.ClicksService</code> interface.
 *
 * <p>
 * This is a remote service. Methods of this service are expected to have security checks based on the propagated JAAS credentials because this service can be accessed remotely.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ClicksServiceBaseImpl
 */
@Component(
	property = {
		"json.web.service.context.name=gsearchclicktracking",
		"json.web.service.context.path=Clicks"
	},
	service = AopService.class
)
public class ClicksServiceImpl extends ClicksServiceBaseImpl {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Always use <code>fi.soveltia.liferay.gsearch.click.tracking.service.ClicksServiceUtil</code> to access the clicks remote service.
	 */
	public List<Clicks> getClicks(int start, int end) {
		
		// For demonstration purposes. Please implement permission checks here if needed.
		
		return clicksLocalService.getClicks(start, end);
	}
}