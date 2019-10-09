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

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import fi.soveltia.liferay.gsearch.click.tracking.model.Clicks;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for Clicks. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see ClicksLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface ClicksLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ClicksLocalServiceUtil} to access the clicks local service. Add custom service methods to <code>fi.soveltia.liferay.gsearch.click.tracking.service.impl.ClicksLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */

	/**
	 * Adds the clicks to the database. Also notifies the appropriate model listeners.
	 *
	 * @param clicks the clicks
	 * @return the clicks that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public Clicks addClicks(Clicks clicks);

	public Clicks addClicks(ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new clicks with the primary key. Does not add the clicks to the database.
	 *
	 * @param clickId the primary key for the new clicks
	 * @return the new clicks
	 */
	@Transactional(enabled = false)
	public Clicks createClicks(long clickId);

	/**
	 * Deletes the clicks from the database. Also notifies the appropriate model listeners.
	 *
	 * @param clicks the clicks
	 * @return the clicks that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public Clicks deleteClicks(Clicks clicks);

	/**
	 * Deletes the clicks with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param clickId the primary key of the clicks
	 * @return the clicks that was removed
	 * @throws PortalException if a clicks with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public Clicks deleteClicks(long clickId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Clicks fetchClicks(long clickId);

	/**
	 * Returns the clicks matching the UUID and group.
	 *
	 * @param uuid the clicks's UUID
	 * @param groupId the primary key of the group
	 * @return the matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Clicks fetchClicksByUuidAndGroupId(String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Clicks> getClicks(int start, int end);

	/**
	 * Returns the clicks with the primary key.
	 *
	 * @param clickId the primary key of the clicks
	 * @return the clicks
	 * @throws PortalException if a clicks with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Clicks getClicks(long clickId) throws PortalException;

	/**
	 * Returns the clicks matching the UUID and group.
	 *
	 * @param uuid the clicks's UUID
	 * @param groupId the primary key of the group
	 * @return the matching clicks
	 * @throws PortalException if a matching clicks could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Clicks getClicksByUuidAndGroupId(String uuid, long groupId)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Clicks> getClickses(int start, int end);

	/**
	 * Returns all the clickses matching the UUID and company.
	 *
	 * @param uuid the UUID of the clickses
	 * @param companyId the primary key of the company
	 * @return the matching clickses, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Clicks> getClicksesByUuidAndCompanyId(
		String uuid, long companyId);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Clicks> getClicksesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Clicks> orderByComparator);

	/**
	 * Returns the number of clickses.
	 *
	 * @return the number of clickses
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getClicksesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Updates the clicks in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param clicks the clicks
	 * @return the clicks that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public Clicks updateClicks(Clicks clicks);

	public Clicks updateClicks(ServiceContext serviceContext)
		throws PortalException;

}