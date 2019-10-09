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
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.click.tracking.model.Clicks;
import fi.soveltia.liferay.gsearch.click.tracking.service.base.ClicksLocalServiceBaseImpl;

/**
 * The implementation of the clicks local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the <code>fi.soveltia.liferay.gsearch.click.tracking.service.ClicksLocalService</code> interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ClicksLocalServiceBaseImpl
 */
@Component(
	property = "model.class.name=fi.soveltia.liferay.gsearch.click.tracking.model.Clicks",
	service = AopService.class
)
public class ClicksLocalServiceImpl extends ClicksLocalServiceBaseImpl {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Use <code>fi.soveltia.liferay.gsearch.click.tracking.service.ClicksLocalService</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>fi.soveltia.liferay.gsearch.click.tracking.service.ClicksLocalServiceUtil</code>.
	 */
	
	public Clicks addClicks(ServiceContext serviceContext) 
			throws PortalException {
		
		long groupId = serviceContext.getScopeGroupId();
		long companyId = serviceContext.getCompanyId();
		
		String keywords = (String)serviceContext.getAttribute("keywords");
		Long entryClassPK = (Long)serviceContext.getAttribute("entryClassPK");
		
		if (Validator.isBlank(keywords) || entryClassPK == null) {
			throw new PortalException("Cannot create record. Keywords or entryClassPK was empty");
		}
		
		// Generate primary key for the assignment.

		long clicksId =
			counterLocalService.increment(Clicks.class.getName());

		Clicks clicks = createClicks(clicksId);

		clicks.setClickCount(1);
		clicks.setCompanyId(companyId);
		clicks.setCreateDate(new Date());
		clicks.setEntryClassPK(entryClassPK);
		clicks.setGroupId(groupId);
		clicks.setKeywords(keywords);
		clicks.setModifiedDate(new Date());

		return super.addClicks(clicks);
	}	
	
	public List<Clicks> getClicks(int start, int end) {
		return clicksPersistence.findAll(start, end);
		
	}
	
	public Clicks updateClicks (ServiceContext serviceContext) throws PortalException {

		long groupId = serviceContext.getScopeGroupId();
		long companyId = serviceContext.getCompanyId();

		String keywords = (String)serviceContext.getAttribute("keywords");
		Long entryClassPK = (Long)serviceContext.getAttribute("entryClassPK");
		
		if (Validator.isBlank(keywords) || entryClassPK == null) {
			throw new PortalException("Cannot update record. Keywords or entryClassPK was empty");
		}
		
		// Check if there's a record for the keywords;

		Clicks clicks = clicksPersistence.fetchByC_G_K_E(companyId, groupId, keywords, entryClassPK);

		if (clicks == null) {
			return addClicks(serviceContext);
		}
		
		int clickCount = clicks.getClickCount() + 1;
		clicks.setClickCount(clickCount);
		clicks.setModifiedDate(new Date());
		
		return super.updateClicks(clicks);
	}
}