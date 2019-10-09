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

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the remote service utility for Clicks. This utility wraps
 * <code>fi.soveltia.liferay.gsearch.click.tracking.service.impl.ClicksServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see ClicksService
 * @generated
 */
@ProviderType
public class ClicksServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>fi.soveltia.liferay.gsearch.click.tracking.service.impl.ClicksServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static java.util.List
		<fi.soveltia.liferay.gsearch.click.tracking.model.Clicks> getClicks(
			int start, int end) {

		return getService().getClicks(start, end);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static ClicksService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<ClicksService, ClicksService> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(ClicksService.class);

		ServiceTracker<ClicksService, ClicksService> serviceTracker =
			new ServiceTracker<ClicksService, ClicksService>(
				bundle.getBundleContext(), ClicksService.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}