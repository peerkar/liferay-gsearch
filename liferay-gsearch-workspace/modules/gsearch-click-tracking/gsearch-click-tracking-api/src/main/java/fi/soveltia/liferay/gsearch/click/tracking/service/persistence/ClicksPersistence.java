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

import com.liferay.portal.kernel.service.persistence.BasePersistence;

import fi.soveltia.liferay.gsearch.click.tracking.exception.NoSuchClicksException;
import fi.soveltia.liferay.gsearch.click.tracking.model.Clicks;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the clicks service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ClicksUtil
 * @generated
 */
@ProviderType
public interface ClicksPersistence extends BasePersistence<Clicks> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ClicksUtil} to access the clicks persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the clickses where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching clickses
	 */
	public java.util.List<Clicks> findByUuid(String uuid);

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
	public java.util.List<Clicks> findByUuid(String uuid, int start, int end);

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
	public java.util.List<Clicks> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Clicks>
			orderByComparator);

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
	public java.util.List<Clicks> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Clicks>
			orderByComparator,
		boolean retrieveFromCache);

	/**
	 * Returns the first clicks in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching clicks
	 * @throws NoSuchClicksException if a matching clicks could not be found
	 */
	public Clicks findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<Clicks>
				orderByComparator)
		throws NoSuchClicksException;

	/**
	 * Returns the first clicks in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	public Clicks fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<Clicks>
			orderByComparator);

	/**
	 * Returns the last clicks in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching clicks
	 * @throws NoSuchClicksException if a matching clicks could not be found
	 */
	public Clicks findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<Clicks>
				orderByComparator)
		throws NoSuchClicksException;

	/**
	 * Returns the last clicks in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	public Clicks fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<Clicks>
			orderByComparator);

	/**
	 * Returns the clickses before and after the current clicks in the ordered set where uuid = &#63;.
	 *
	 * @param clickId the primary key of the current clicks
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next clicks
	 * @throws NoSuchClicksException if a clicks with the primary key could not be found
	 */
	public Clicks[] findByUuid_PrevAndNext(
			long clickId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<Clicks>
				orderByComparator)
		throws NoSuchClicksException;

	/**
	 * Removes all the clickses where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of clickses where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching clickses
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the clicks where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchClicksException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching clicks
	 * @throws NoSuchClicksException if a matching clicks could not be found
	 */
	public Clicks findByUUID_G(String uuid, long groupId)
		throws NoSuchClicksException;

	/**
	 * Returns the clicks where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	public Clicks fetchByUUID_G(String uuid, long groupId);

	/**
	 * Returns the clicks where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	public Clicks fetchByUUID_G(
		String uuid, long groupId, boolean retrieveFromCache);

	/**
	 * Removes the clicks where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the clicks that was removed
	 */
	public Clicks removeByUUID_G(String uuid, long groupId)
		throws NoSuchClicksException;

	/**
	 * Returns the number of clickses where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching clickses
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns all the clickses where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching clickses
	 */
	public java.util.List<Clicks> findByUuid_C(String uuid, long companyId);

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
	public java.util.List<Clicks> findByUuid_C(
		String uuid, long companyId, int start, int end);

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
	public java.util.List<Clicks> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Clicks>
			orderByComparator);

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
	public java.util.List<Clicks> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Clicks>
			orderByComparator,
		boolean retrieveFromCache);

	/**
	 * Returns the first clicks in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching clicks
	 * @throws NoSuchClicksException if a matching clicks could not be found
	 */
	public Clicks findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Clicks>
				orderByComparator)
		throws NoSuchClicksException;

	/**
	 * Returns the first clicks in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	public Clicks fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<Clicks>
			orderByComparator);

	/**
	 * Returns the last clicks in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching clicks
	 * @throws NoSuchClicksException if a matching clicks could not be found
	 */
	public Clicks findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Clicks>
				orderByComparator)
		throws NoSuchClicksException;

	/**
	 * Returns the last clicks in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	public Clicks fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<Clicks>
			orderByComparator);

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
	public Clicks[] findByUuid_C_PrevAndNext(
			long clickId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Clicks>
				orderByComparator)
		throws NoSuchClicksException;

	/**
	 * Removes all the clickses where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of clickses where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching clickses
	 */
	public int countByUuid_C(String uuid, long companyId);

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
	public Clicks findByC_G_K_E(
			long companyId, long groupId, String keywords, long entryClassPK)
		throws NoSuchClicksException;

	/**
	 * Returns the clicks where companyId = &#63; and groupId = &#63; and keywords = &#63; and entryClassPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param keywords the keywords
	 * @param entryClassPK the entry class pk
	 * @return the matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	public Clicks fetchByC_G_K_E(
		long companyId, long groupId, String keywords, long entryClassPK);

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
	public Clicks fetchByC_G_K_E(
		long companyId, long groupId, String keywords, long entryClassPK,
		boolean retrieveFromCache);

	/**
	 * Removes the clicks where companyId = &#63; and groupId = &#63; and keywords = &#63; and entryClassPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param keywords the keywords
	 * @param entryClassPK the entry class pk
	 * @return the clicks that was removed
	 */
	public Clicks removeByC_G_K_E(
			long companyId, long groupId, String keywords, long entryClassPK)
		throws NoSuchClicksException;

	/**
	 * Returns the number of clickses where companyId = &#63; and groupId = &#63; and keywords = &#63; and entryClassPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupId the group ID
	 * @param keywords the keywords
	 * @param entryClassPK the entry class pk
	 * @return the number of matching clickses
	 */
	public int countByC_G_K_E(
		long companyId, long groupId, String keywords, long entryClassPK);

	/**
	 * Caches the clicks in the entity cache if it is enabled.
	 *
	 * @param clicks the clicks
	 */
	public void cacheResult(Clicks clicks);

	/**
	 * Caches the clickses in the entity cache if it is enabled.
	 *
	 * @param clickses the clickses
	 */
	public void cacheResult(java.util.List<Clicks> clickses);

	/**
	 * Creates a new clicks with the primary key. Does not add the clicks to the database.
	 *
	 * @param clickId the primary key for the new clicks
	 * @return the new clicks
	 */
	public Clicks create(long clickId);

	/**
	 * Removes the clicks with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param clickId the primary key of the clicks
	 * @return the clicks that was removed
	 * @throws NoSuchClicksException if a clicks with the primary key could not be found
	 */
	public Clicks remove(long clickId) throws NoSuchClicksException;

	public Clicks updateImpl(Clicks clicks);

	/**
	 * Returns the clicks with the primary key or throws a <code>NoSuchClicksException</code> if it could not be found.
	 *
	 * @param clickId the primary key of the clicks
	 * @return the clicks
	 * @throws NoSuchClicksException if a clicks with the primary key could not be found
	 */
	public Clicks findByPrimaryKey(long clickId) throws NoSuchClicksException;

	/**
	 * Returns the clicks with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param clickId the primary key of the clicks
	 * @return the clicks, or <code>null</code> if a clicks with the primary key could not be found
	 */
	public Clicks fetchByPrimaryKey(long clickId);

	/**
	 * Returns all the clickses.
	 *
	 * @return the clickses
	 */
	public java.util.List<Clicks> findAll();

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
	public java.util.List<Clicks> findAll(int start, int end);

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
	public java.util.List<Clicks> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Clicks>
			orderByComparator);

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
	public java.util.List<Clicks> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Clicks>
			orderByComparator,
		boolean retrieveFromCache);

	/**
	 * Removes all the clickses from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of clickses.
	 *
	 * @return the number of clickses
	 */
	public int countAll();

}