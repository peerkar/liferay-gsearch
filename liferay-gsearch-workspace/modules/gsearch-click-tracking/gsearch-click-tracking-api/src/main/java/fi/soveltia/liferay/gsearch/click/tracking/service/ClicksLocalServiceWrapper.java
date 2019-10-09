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
 * Provides a wrapper for {@link ClicksLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ClicksLocalService
 * @generated
 */
@ProviderType
public class ClicksLocalServiceWrapper
	implements ClicksLocalService, ServiceWrapper<ClicksLocalService> {

	public ClicksLocalServiceWrapper(ClicksLocalService clicksLocalService) {
		_clicksLocalService = clicksLocalService;
	}

	/**
	 * Adds the clicks to the database. Also notifies the appropriate model listeners.
	 *
	 * @param clicks the clicks
	 * @return the clicks that was added
	 */
	@Override
	public fi.soveltia.liferay.gsearch.click.tracking.model.Clicks addClicks(
		fi.soveltia.liferay.gsearch.click.tracking.model.Clicks clicks) {

		return _clicksLocalService.addClicks(clicks);
	}

	@Override
	public fi.soveltia.liferay.gsearch.click.tracking.model.Clicks addClicks(
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clicksLocalService.addClicks(serviceContext);
	}

	/**
	 * Creates a new clicks with the primary key. Does not add the clicks to the database.
	 *
	 * @param clickId the primary key for the new clicks
	 * @return the new clicks
	 */
	@Override
	public fi.soveltia.liferay.gsearch.click.tracking.model.Clicks createClicks(
		long clickId) {

		return _clicksLocalService.createClicks(clickId);
	}

	/**
	 * Deletes the clicks from the database. Also notifies the appropriate model listeners.
	 *
	 * @param clicks the clicks
	 * @return the clicks that was removed
	 */
	@Override
	public fi.soveltia.liferay.gsearch.click.tracking.model.Clicks deleteClicks(
		fi.soveltia.liferay.gsearch.click.tracking.model.Clicks clicks) {

		return _clicksLocalService.deleteClicks(clicks);
	}

	/**
	 * Deletes the clicks with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param clickId the primary key of the clicks
	 * @return the clicks that was removed
	 * @throws PortalException if a clicks with the primary key could not be found
	 */
	@Override
	public fi.soveltia.liferay.gsearch.click.tracking.model.Clicks deleteClicks(
			long clickId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clicksLocalService.deleteClicks(clickId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clicksLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _clicksLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _clicksLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>fi.soveltia.liferay.gsearch.click.tracking.model.impl.ClicksModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _clicksLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>fi.soveltia.liferay.gsearch.click.tracking.model.impl.ClicksModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _clicksLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _clicksLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _clicksLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public fi.soveltia.liferay.gsearch.click.tracking.model.Clicks fetchClicks(
		long clickId) {

		return _clicksLocalService.fetchClicks(clickId);
	}

	/**
	 * Returns the clicks matching the UUID and group.
	 *
	 * @param uuid the clicks's UUID
	 * @param groupId the primary key of the group
	 * @return the matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	@Override
	public fi.soveltia.liferay.gsearch.click.tracking.model.Clicks
		fetchClicksByUuidAndGroupId(String uuid, long groupId) {

		return _clicksLocalService.fetchClicksByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _clicksLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List
		<fi.soveltia.liferay.gsearch.click.tracking.model.Clicks> getClicks(
			int start, int end) {

		return _clicksLocalService.getClicks(start, end);
	}

	/**
	 * Returns the clicks with the primary key.
	 *
	 * @param clickId the primary key of the clicks
	 * @return the clicks
	 * @throws PortalException if a clicks with the primary key could not be found
	 */
	@Override
	public fi.soveltia.liferay.gsearch.click.tracking.model.Clicks getClicks(
			long clickId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clicksLocalService.getClicks(clickId);
	}

	/**
	 * Returns the clicks matching the UUID and group.
	 *
	 * @param uuid the clicks's UUID
	 * @param groupId the primary key of the group
	 * @return the matching clicks
	 * @throws PortalException if a matching clicks could not be found
	 */
	@Override
	public fi.soveltia.liferay.gsearch.click.tracking.model.Clicks
			getClicksByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clicksLocalService.getClicksByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the clickses.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>fi.soveltia.liferay.gsearch.click.tracking.model.impl.ClicksModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of clickses
	 * @param end the upper bound of the range of clickses (not inclusive)
	 * @return the range of clickses
	 */
	@Override
	public java.util.List
		<fi.soveltia.liferay.gsearch.click.tracking.model.Clicks> getClickses(
			int start, int end) {

		return _clicksLocalService.getClickses(start, end);
	}

	/**
	 * Returns all the clickses matching the UUID and company.
	 *
	 * @param uuid the UUID of the clickses
	 * @param companyId the primary key of the company
	 * @return the matching clickses, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<fi.soveltia.liferay.gsearch.click.tracking.model.Clicks>
			getClicksesByUuidAndCompanyId(String uuid, long companyId) {

		return _clicksLocalService.getClicksesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of clickses matching the UUID and company.
	 *
	 * @param uuid the UUID of the clickses
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of clickses
	 * @param end the upper bound of the range of clickses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching clickses, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<fi.soveltia.liferay.gsearch.click.tracking.model.Clicks>
			getClicksesByUuidAndCompanyId(
				String uuid, long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<fi.soveltia.liferay.gsearch.click.tracking.model.Clicks>
						orderByComparator) {

		return _clicksLocalService.getClicksesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of clickses.
	 *
	 * @return the number of clickses
	 */
	@Override
	public int getClicksesCount() {
		return _clicksLocalService.getClicksesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _clicksLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _clicksLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _clicksLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clicksLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the clicks in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param clicks the clicks
	 * @return the clicks that was updated
	 */
	@Override
	public fi.soveltia.liferay.gsearch.click.tracking.model.Clicks updateClicks(
		fi.soveltia.liferay.gsearch.click.tracking.model.Clicks clicks) {

		return _clicksLocalService.updateClicks(clicks);
	}

	@Override
	public fi.soveltia.liferay.gsearch.click.tracking.model.Clicks updateClicks(
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clicksLocalService.updateClicks(serviceContext);
	}

	@Override
	public ClicksLocalService getWrappedService() {
		return _clicksLocalService;
	}

	@Override
	public void setWrappedService(ClicksLocalService clicksLocalService) {
		_clicksLocalService = clicksLocalService;
	}

	private ClicksLocalService _clicksLocalService;

}