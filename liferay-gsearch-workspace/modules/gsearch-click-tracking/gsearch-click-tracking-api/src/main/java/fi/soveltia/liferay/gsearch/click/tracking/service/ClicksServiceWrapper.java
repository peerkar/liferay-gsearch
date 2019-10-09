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

package fi.soveltia.liferay.gsearch.click.tracking.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides a wrapper for {@link ClicksService}.
 *
 * @author Brian Wing Shun Chan
 * @see ClicksService
 * @generated
 */
@ProviderType
public class ClicksServiceWrapper
	implements ClicksService, ServiceWrapper<ClicksService> {

	public ClicksServiceWrapper(ClicksService clicksService) {
		_clicksService = clicksService;
	}

	@Override
	public java.util.List
		<fi.soveltia.liferay.gsearch.click.tracking.model.Clicks> getClicks(
			int start, int end) {

		return _clicksService.getClicks(start, end);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _clicksService.getOSGiServiceIdentifier();
	}

	@Override
	public ClicksService getWrappedService() {
		return _clicksService;
	}

	@Override
	public void setWrappedService(ClicksService clicksService) {
		_clicksService = clicksService;
	}

	private ClicksService _clicksService;

}