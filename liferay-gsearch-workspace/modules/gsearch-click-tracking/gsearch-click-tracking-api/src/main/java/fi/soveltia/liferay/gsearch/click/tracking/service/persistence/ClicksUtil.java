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

package fi.soveltia.liferay.gsearch.click.tracking.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import fi.soveltia.liferay.gsearch.click.tracking.model.Clicks;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The persistence utility for the clicks service. This utility wraps <code>fi.soveltia.liferay.gsearch.click.tracking.service.persistence.impl.ClicksPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ClicksPersistence
 * @generated
 */
@ProviderType
public class ClicksUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(Clicks clicks) {
		getPersistence().clearCache(clicks);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, Clicks> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<Clicks> findWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<Clicks> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<Clicks> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<Clicks> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static Clicks update(Clicks clicks) {
		return getPersistence().update(clicks);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static Clicks update(Clicks clicks, ServiceContext serviceContext) {
		return getPersistence().update(clicks, serviceContext);
	}

	/**
	 * Returns all the clickses where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching clickses
	 */
	public static List<Clicks> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the clickses where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>ClicksModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of clickses
	 * @param end the upper bound of the range of clickses (not inclusive)
	 * @return the range of matching clickses
	 */
	public static List<Clicks> findByUuid(String uuid, int start, int end) {
		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the clickses where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>ClicksModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of clickses
	 * @param end the upper bound of the range of clickses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching clickses
	 */
	public static List<Clicks> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Clicks> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the clickses where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>ClicksModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of clickses
	 * @param end the upper bound of the range of clickses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of matching clickses
	 */
	public static List<Clicks> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Clicks> orderByComparator,
		boolean retrieveFromCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, retrieveFromCache);
	}

	/**
	 * Returns the first clicks in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching clicks
	 * @throws NoSuchClicksException if a matching clicks could not be found
	 */
	public static Clicks findByUuid_First(
			String uuid, OrderByComparator<Clicks> orderByComparator)
		throws fi.soveltia.liferay.gsearch.click.tracking.exception.
			NoSuchClicksException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first clicks in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	public static Clicks fetchByUuid_First(
		String uuid, OrderByComparator<Clicks> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last clicks in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching clicks
	 * @throws NoSuchClicksException if a matching clicks could not be found
	 */
	public static Clicks findByUuid_Last(
			String uuid, OrderByComparator<Clicks> orderByComparator)
		throws fi.soveltia.liferay.gsearch.click.tracking.exception.
			NoSuchClicksException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last clicks in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	public static Clicks fetchByUuid_Last(
		String uuid, OrderByComparator<Clicks> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the clickses before and after the current clicks in the ordered set where uuid = &#63;.
	 *
	 * @param clickId the primary key of the current clicks
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next clicks
	 * @throws NoSuchClicksException if a clicks with the primary key could not be found
	 */
	public static Clicks[] findByUuid_PrevAndNext(
			long clickId, String uuid,
			OrderByComparator<Clicks> orderByComparator)
		throws fi.soveltia.liferay.gsearch.click.tracking.exception.
			NoSuchClicksException {

		return getPersistence().findByUuid_PrevAndNext(
			clickId, uuid, orderByComparator);
	}

	/**
	 * Removes all the clickses where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of clickses where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching clickses
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the clicks where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchClicksException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching clicks
	 * @throws NoSuchClicksException if a matching clicks could not be found
	 */
	public static Clicks findByUUID_G(String uuid, long groupId)
		throws fi.soveltia.liferay.gsearch.click.tracking.exception.
			NoSuchClicksException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the clicks where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	public static Clicks fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the clicks where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	public static Clicks fetchByUUID_G(
		String uuid, long groupId, boolean retrieveFromCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, retrieveFromCache);
	}

	/**
	 * Removes the clicks where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the clicks that was removed
	 */
	public static Clicks removeByUUID_G(String uuid, long groupId)
		throws fi.soveltia.liferay.gsearch.click.tracking.exception.
			NoSuchClicksException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of clickses where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching clickses
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns all the clickses where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching clickses
	 */
	public static List<Clicks> findByUuid_C(String uuid, long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the clickses where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>ClicksModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of clickses
	 * @param end the upper bound of the range of clickses (not inclusive)
	 * @return the range of matching clickses
	 */
	public static List<Clicks> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the clickses where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>ClicksModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of clickses
	 * @param end the upper bound of the range of clickses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching clickses
	 */
	public static List<Clicks> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Clicks> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the clickses where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>ClicksModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of clickses
	 * @param end the upper bound of the range of clickses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of matching clickses
	 */
	public static List<Clicks> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Clicks> orderByComparator,
		boolean retrieveFromCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, retrieveFromCache);
	}

	/**
	 * Returns the first clicks in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching clicks
	 * @throws NoSuchClicksException if a matching clicks could not be found
	 */
	public static Clicks findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<Clicks> orderByComparator)
		throws fi.soveltia.liferay.gsearch.click.tracking.exception.
			NoSuchClicksException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first clicks in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	public static Clicks fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<Clicks> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last clicks in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching clicks
	 * @throws NoSuchClicksException if a matching clicks could not be found
	 */
	public static Clicks findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<Clicks> orderByComparator)
		throws fi.soveltia.liferay.gsearch.click.tracking.exception.
			NoSuchClicksException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last clicks in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	public static Clicks fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<Clicks> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the clickses before and after the current clicks in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param clickId the primary key of the current clicks
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next clicks
	 * @throws NoSuchClicksException if a clicks with the primary key could not be found
	 */
	public static Clicks[] findByUuid_C_PrevAndNext(
			long clickId, String uuid, long companyId,
			OrderByComparator<Clicks> orderByComparator)
		throws fi.soveltia.liferay.gsearch.click.tracking.exception.
			NoSuchClicksException {

		return getPersistence().findByUuid_C_PrevAndNext(
			clickId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the clickses where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of clickses where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching clickses
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the clicks where companyId = &#63; and groupId = &#63; and keywords = &#63; and entryClassPK = &#63; or throws a <code>NoSuchClicksException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param keywords the keywords
	 * @param entryClassPK the entry class pk
	 * @return the matching clicks
	 * @throws NoSuchClicksException if a matching clicks could not be found
	 */
	public static Clicks findByC_G_K_E(
			long companyId, long groupId, String keywords, long entryClassPK)
		throws fi.soveltia.liferay.gsearch.click.tracking.exception.
			NoSuchClicksException {

		return getPersistence().findByC_G_K_E(
			companyId, groupId, keywords, entryClassPK);
	}

	/**
	 * Returns the clicks where companyId = &#63; and groupId = &#63; and keywords = &#63; and entryClassPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param keywords the keywords
	 * @param entryClassPK the entry class pk
	 * @return the matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	public static Clicks fetchByC_G_K_E(
		long companyId, long groupId, String keywords, long entryClassPK) {

		return getPersistence().fetchByC_G_K_E(
			companyId, groupId, keywords, entryClassPK);
	}

	/**
	 * Returns the clicks where companyId = &#63; and groupId = &#63; and keywords = &#63; and entryClassPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param keywords the keywords
	 * @param entryClassPK the entry class pk
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	public static Clicks fetchByC_G_K_E(
		long companyId, long groupId, String keywords, long entryClassPK,
		boolean retrieveFromCache) {

		return getPersistence().fetchByC_G_K_E(
			companyId, groupId, keywords, entryClassPK, retrieveFromCache);
	}

	/**
	 * Removes the clicks where companyId = &#63; and groupId = &#63; and keywords = &#63; and entryClassPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param keywords the keywords
	 * @param entryClassPK the entry class pk
	 * @return the clicks that was removed
	 */
	public static Clicks removeByC_G_K_E(
			long companyId, long groupId, String keywords, long entryClassPK)
		throws fi.soveltia.liferay.gsearch.click.tracking.exception.
			NoSuchClicksException {

		return getPersistence().removeByC_G_K_E(
			companyId, groupId, keywords, entryClassPK);
	}

	/**
	 * Returns the number of clickses where companyId = &#63; and groupId = &#63; and keywords = &#63; and entryClassPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param keywords the keywords
	 * @param entryClassPK the entry class pk
	 * @return the number of matching clickses
	 */
	public static int countByC_G_K_E(
		long companyId, long groupId, String keywords, long entryClassPK) {

		return getPersistence().countByC_G_K_E(
			companyId, groupId, keywords, entryClassPK);
	}

	/**
	 * Caches the clicks in the entity cache if it is enabled.
	 *
	 * @param clicks the clicks
	 */
	public static void cacheResult(Clicks clicks) {
		getPersistence().cacheResult(clicks);
	}

	/**
	 * Caches the clickses in the entity cache if it is enabled.
	 *
	 * @param clickses the clickses
	 */
	public static void cacheResult(List<Clicks> clickses) {
		getPersistence().cacheResult(clickses);
	}

	/**
	 * Creates a new clicks with the primary key. Does not add the clicks to the database.
	 *
	 * @param clickId the primary key for the new clicks
	 * @return the new clicks
	 */
	public static Clicks create(long clickId) {
		return getPersistence().create(clickId);
	}

	/**
	 * Removes the clicks with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param clickId the primary key of the clicks
	 * @return the clicks that was removed
	 * @throws NoSuchClicksException if a clicks with the primary key could not be found
	 */
	public static Clicks remove(long clickId)
		throws fi.soveltia.liferay.gsearch.click.tracking.exception.
			NoSuchClicksException {

		return getPersistence().remove(clickId);
	}

	public static Clicks updateImpl(Clicks clicks) {
		return getPersistence().updateImpl(clicks);
	}

	/**
	 * Returns the clicks with the primary key or throws a <code>NoSuchClicksException</code> if it could not be found.
	 *
	 * @param clickId the primary key of the clicks
	 * @return the clicks
	 * @throws NoSuchClicksException if a clicks with the primary key could not be found
	 */
	public static Clicks findByPrimaryKey(long clickId)
		throws fi.soveltia.liferay.gsearch.click.tracking.exception.
			NoSuchClicksException {

		return getPersistence().findByPrimaryKey(clickId);
	}

	/**
	 * Returns the clicks with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param clickId the primary key of the clicks
	 * @return the clicks, or <code>null</code> if a clicks with the primary key could not be found
	 */
	public static Clicks fetchByPrimaryKey(long clickId) {
		return getPersistence().fetchByPrimaryKey(clickId);
	}

	/**
	 * Returns all the clickses.
	 *
	 * @return the clickses
	 */
	public static List<Clicks> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the clickses.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>ClicksModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of clickses
	 * @param end the upper bound of the range of clickses (not inclusive)
	 * @return the range of clickses
	 */
	public static List<Clicks> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the clickses.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>ClicksModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of clickses
	 * @param end the upper bound of the range of clickses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of clickses
	 */
	public static List<Clicks> findAll(
		int start, int end, OrderByComparator<Clicks> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the clickses.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>ClicksModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of clickses
	 * @param end the upper bound of the range of clickses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of clickses
	 */
	public static List<Clicks> findAll(
		int start, int end, OrderByComparator<Clicks> orderByComparator,
		boolean retrieveFromCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, retrieveFromCache);
	}

	/**
	 * Removes all the clickses from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of clickses.
	 *
	 * @return the number of clickses
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static ClicksPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<ClicksPersistence, ClicksPersistence>
		_serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(ClicksPersistence.class);

		ServiceTracker<ClicksPersistence, ClicksPersistence> serviceTracker =
			new ServiceTracker<ClicksPersistence, ClicksPersistence>(
				bundle.getBundleContext(), ClicksPersistence.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}