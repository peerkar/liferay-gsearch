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

package fi.soveltia.liferay.gsearch.click.tracking.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import fi.soveltia.liferay.gsearch.click.tracking.exception.NoSuchClicksException;
import fi.soveltia.liferay.gsearch.click.tracking.model.Clicks;
import fi.soveltia.liferay.gsearch.click.tracking.model.impl.ClicksImpl;
import fi.soveltia.liferay.gsearch.click.tracking.model.impl.ClicksModelImpl;
import fi.soveltia.liferay.gsearch.click.tracking.service.persistence.ClicksPersistence;
import fi.soveltia.liferay.gsearch.click.tracking.service.persistence.impl.constants.GSearchClickTrackingPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the clicks service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = ClicksPersistence.class)
@ProviderType
public class ClicksPersistenceImpl
	extends BasePersistenceImpl<Clicks> implements ClicksPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ClicksUtil</code> to access the clicks persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ClicksImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByUuid;
	private FinderPath _finderPathWithoutPaginationFindByUuid;
	private FinderPath _finderPathCountByUuid;

	/**
	 * Returns all the clickses where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching clickses
	 */
	@Override
	public List<Clicks> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
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
	@Override
	public List<Clicks> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
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
	@Override
	public List<Clicks> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Clicks> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
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
	@Override
	public List<Clicks> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Clicks> orderByComparator,
		boolean retrieveFromCache) {

		uuid = Objects.toString(uuid, "");

		boolean pagination = true;
		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			pagination = false;
			finderPath = _finderPathWithoutPaginationFindByUuid;
			finderArgs = new Object[] {uuid};
		}
		else {
			finderPath = _finderPathWithPaginationFindByUuid;
			finderArgs = new Object[] {uuid, start, end, orderByComparator};
		}

		List<Clicks> list = null;

		if (retrieveFromCache) {
			list = (List<Clicks>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (Clicks clicks : list) {
					if (!uuid.equals(clicks.getUuid())) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler query = null;

			if (orderByComparator != null) {
				query = new StringBundler(
					3 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				query = new StringBundler(3);
			}

			query.append(_SQL_SELECT_CLICKS_WHERE);

			boolean bindUuid = false;

			if (uuid.isEmpty()) {
				query.append(_FINDER_COLUMN_UUID_UUID_3);
			}
			else {
				bindUuid = true;

				query.append(_FINDER_COLUMN_UUID_UUID_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					query, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else if (pagination) {
				query.append(ClicksModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (bindUuid) {
					qPos.add(uuid);
				}

				if (!pagination) {
					list = (List<Clicks>)QueryUtil.list(
						q, getDialect(), start, end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<Clicks>)QueryUtil.list(
						q, getDialect(), start, end);
				}

				cacheResult(list);

				finderCache.putResult(finderPath, finderArgs, list);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first clicks in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching clicks
	 * @throws NoSuchClicksException if a matching clicks could not be found
	 */
	@Override
	public Clicks findByUuid_First(
			String uuid, OrderByComparator<Clicks> orderByComparator)
		throws NoSuchClicksException {

		Clicks clicks = fetchByUuid_First(uuid, orderByComparator);

		if (clicks != null) {
			return clicks;
		}

		StringBundler msg = new StringBundler(4);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("uuid=");
		msg.append(uuid);

		msg.append("}");

		throw new NoSuchClicksException(msg.toString());
	}

	/**
	 * Returns the first clicks in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	@Override
	public Clicks fetchByUuid_First(
		String uuid, OrderByComparator<Clicks> orderByComparator) {

		List<Clicks> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last clicks in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching clicks
	 * @throws NoSuchClicksException if a matching clicks could not be found
	 */
	@Override
	public Clicks findByUuid_Last(
			String uuid, OrderByComparator<Clicks> orderByComparator)
		throws NoSuchClicksException {

		Clicks clicks = fetchByUuid_Last(uuid, orderByComparator);

		if (clicks != null) {
			return clicks;
		}

		StringBundler msg = new StringBundler(4);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("uuid=");
		msg.append(uuid);

		msg.append("}");

		throw new NoSuchClicksException(msg.toString());
	}

	/**
	 * Returns the last clicks in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	@Override
	public Clicks fetchByUuid_Last(
		String uuid, OrderByComparator<Clicks> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<Clicks> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
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
	@Override
	public Clicks[] findByUuid_PrevAndNext(
			long clickId, String uuid,
			OrderByComparator<Clicks> orderByComparator)
		throws NoSuchClicksException {

		uuid = Objects.toString(uuid, "");

		Clicks clicks = findByPrimaryKey(clickId);

		Session session = null;

		try {
			session = openSession();

			Clicks[] array = new ClicksImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, clicks, uuid, orderByComparator, true);

			array[1] = clicks;

			array[2] = getByUuid_PrevAndNext(
				session, clicks, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	protected Clicks getByUuid_PrevAndNext(
		Session session, Clicks clicks, String uuid,
		OrderByComparator<Clicks> orderByComparator, boolean previous) {

		StringBundler query = null;

		if (orderByComparator != null) {
			query = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			query = new StringBundler(3);
		}

		query.append(_SQL_SELECT_CLICKS_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			query.append(_FINDER_COLUMN_UUID_UUID_3);
		}
		else {
			bindUuid = true;

			query.append(_FINDER_COLUMN_UUID_UUID_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				query.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				query.append(_ORDER_BY_ENTITY_ALIAS);
				query.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						query.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(WHERE_GREATER_THAN);
					}
					else {
						query.append(WHERE_LESSER_THAN);
					}
				}
			}

			query.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				query.append(_ORDER_BY_ENTITY_ALIAS);
				query.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						query.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(ORDER_BY_ASC);
					}
					else {
						query.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			query.append(ClicksModelImpl.ORDER_BY_JPQL);
		}

		String sql = query.toString();

		Query q = session.createQuery(sql);

		q.setFirstResult(0);
		q.setMaxResults(2);

		QueryPos qPos = QueryPos.getInstance(q);

		if (bindUuid) {
			qPos.add(uuid);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(clicks)) {

				qPos.add(orderByConditionValue);
			}
		}

		List<Clicks> list = q.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the clickses where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (Clicks clicks :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(clicks);
		}
	}

	/**
	 * Returns the number of clickses where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching clickses
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(2);

			query.append(_SQL_COUNT_CLICKS_WHERE);

			boolean bindUuid = false;

			if (uuid.isEmpty()) {
				query.append(_FINDER_COLUMN_UUID_UUID_3);
			}
			else {
				bindUuid = true;

				query.append(_FINDER_COLUMN_UUID_UUID_2);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (bindUuid) {
					qPos.add(uuid);
				}

				count = (Long)q.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_UUID_UUID_2 = "clicks.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(clicks.uuid IS NULL OR clicks.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;
	private FinderPath _finderPathCountByUUID_G;

	/**
	 * Returns the clicks where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchClicksException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching clicks
	 * @throws NoSuchClicksException if a matching clicks could not be found
	 */
	@Override
	public Clicks findByUUID_G(String uuid, long groupId)
		throws NoSuchClicksException {

		Clicks clicks = fetchByUUID_G(uuid, groupId);

		if (clicks == null) {
			StringBundler msg = new StringBundler(6);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("uuid=");
			msg.append(uuid);

			msg.append(", groupId=");
			msg.append(groupId);

			msg.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(msg.toString());
			}

			throw new NoSuchClicksException(msg.toString());
		}

		return clicks;
	}

	/**
	 * Returns the clicks where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	@Override
	public Clicks fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the clicks where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	@Override
	public Clicks fetchByUUID_G(
		String uuid, long groupId, boolean retrieveFromCache) {

		uuid = Objects.toString(uuid, "");

		Object[] finderArgs = new Object[] {uuid, groupId};

		Object result = null;

		if (retrieveFromCache) {
			result = finderCache.getResult(
				_finderPathFetchByUUID_G, finderArgs, this);
		}

		if (result instanceof Clicks) {
			Clicks clicks = (Clicks)result;

			if (!Objects.equals(uuid, clicks.getUuid()) ||
				(groupId != clicks.getGroupId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler query = new StringBundler(4);

			query.append(_SQL_SELECT_CLICKS_WHERE);

			boolean bindUuid = false;

			if (uuid.isEmpty()) {
				query.append(_FINDER_COLUMN_UUID_G_UUID_3);
			}
			else {
				bindUuid = true;

				query.append(_FINDER_COLUMN_UUID_G_UUID_2);
			}

			query.append(_FINDER_COLUMN_UUID_G_GROUPID_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (bindUuid) {
					qPos.add(uuid);
				}

				qPos.add(groupId);

				List<Clicks> list = q.list();

				if (list.isEmpty()) {
					finderCache.putResult(
						_finderPathFetchByUUID_G, finderArgs, list);
				}
				else {
					Clicks clicks = list.get(0);

					result = clicks;

					cacheResult(clicks);
				}
			}
			catch (Exception e) {
				finderCache.removeResult(_finderPathFetchByUUID_G, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		if (result instanceof List<?>) {
			return null;
		}
		else {
			return (Clicks)result;
		}
	}

	/**
	 * Removes the clicks where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the clicks that was removed
	 */
	@Override
	public Clicks removeByUUID_G(String uuid, long groupId)
		throws NoSuchClicksException {

		Clicks clicks = findByUUID_G(uuid, groupId);

		return remove(clicks);
	}

	/**
	 * Returns the number of clickses where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching clickses
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUUID_G;

		Object[] finderArgs = new Object[] {uuid, groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(3);

			query.append(_SQL_COUNT_CLICKS_WHERE);

			boolean bindUuid = false;

			if (uuid.isEmpty()) {
				query.append(_FINDER_COLUMN_UUID_G_UUID_3);
			}
			else {
				bindUuid = true;

				query.append(_FINDER_COLUMN_UUID_G_UUID_2);
			}

			query.append(_FINDER_COLUMN_UUID_G_GROUPID_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (bindUuid) {
					qPos.add(uuid);
				}

				qPos.add(groupId);

				count = (Long)q.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"clicks.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(clicks.uuid IS NULL OR clicks.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"clicks.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the clickses where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching clickses
	 */
	@Override
	public List<Clicks> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
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
	@Override
	public List<Clicks> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
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
	@Override
	public List<Clicks> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Clicks> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
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
	@Override
	public List<Clicks> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Clicks> orderByComparator,
		boolean retrieveFromCache) {

		uuid = Objects.toString(uuid, "");

		boolean pagination = true;
		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			pagination = false;
			finderPath = _finderPathWithoutPaginationFindByUuid_C;
			finderArgs = new Object[] {uuid, companyId};
		}
		else {
			finderPath = _finderPathWithPaginationFindByUuid_C;
			finderArgs = new Object[] {
				uuid, companyId, start, end, orderByComparator
			};
		}

		List<Clicks> list = null;

		if (retrieveFromCache) {
			list = (List<Clicks>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (Clicks clicks : list) {
					if (!uuid.equals(clicks.getUuid()) ||
						(companyId != clicks.getCompanyId())) {

						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler query = null;

			if (orderByComparator != null) {
				query = new StringBundler(
					4 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				query = new StringBundler(4);
			}

			query.append(_SQL_SELECT_CLICKS_WHERE);

			boolean bindUuid = false;

			if (uuid.isEmpty()) {
				query.append(_FINDER_COLUMN_UUID_C_UUID_3);
			}
			else {
				bindUuid = true;

				query.append(_FINDER_COLUMN_UUID_C_UUID_2);
			}

			query.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					query, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else if (pagination) {
				query.append(ClicksModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (bindUuid) {
					qPos.add(uuid);
				}

				qPos.add(companyId);

				if (!pagination) {
					list = (List<Clicks>)QueryUtil.list(
						q, getDialect(), start, end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<Clicks>)QueryUtil.list(
						q, getDialect(), start, end);
				}

				cacheResult(list);

				finderCache.putResult(finderPath, finderArgs, list);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
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
	@Override
	public Clicks findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<Clicks> orderByComparator)
		throws NoSuchClicksException {

		Clicks clicks = fetchByUuid_C_First(uuid, companyId, orderByComparator);

		if (clicks != null) {
			return clicks;
		}

		StringBundler msg = new StringBundler(6);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("uuid=");
		msg.append(uuid);

		msg.append(", companyId=");
		msg.append(companyId);

		msg.append("}");

		throw new NoSuchClicksException(msg.toString());
	}

	/**
	 * Returns the first clicks in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	@Override
	public Clicks fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<Clicks> orderByComparator) {

		List<Clicks> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
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
	@Override
	public Clicks findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<Clicks> orderByComparator)
		throws NoSuchClicksException {

		Clicks clicks = fetchByUuid_C_Last(uuid, companyId, orderByComparator);

		if (clicks != null) {
			return clicks;
		}

		StringBundler msg = new StringBundler(6);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("uuid=");
		msg.append(uuid);

		msg.append(", companyId=");
		msg.append(companyId);

		msg.append("}");

		throw new NoSuchClicksException(msg.toString());
	}

	/**
	 * Returns the last clicks in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching clicks, or <code>null</code> if a matching clicks could not be found
	 */
	@Override
	public Clicks fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<Clicks> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<Clicks> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
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
	@Override
	public Clicks[] findByUuid_C_PrevAndNext(
			long clickId, String uuid, long companyId,
			OrderByComparator<Clicks> orderByComparator)
		throws NoSuchClicksException {

		uuid = Objects.toString(uuid, "");

		Clicks clicks = findByPrimaryKey(clickId);

		Session session = null;

		try {
			session = openSession();

			Clicks[] array = new ClicksImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, clicks, uuid, companyId, orderByComparator, true);

			array[1] = clicks;

			array[2] = getByUuid_C_PrevAndNext(
				session, clicks, uuid, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	protected Clicks getByUuid_C_PrevAndNext(
		Session session, Clicks clicks, String uuid, long companyId,
		OrderByComparator<Clicks> orderByComparator, boolean previous) {

		StringBundler query = null;

		if (orderByComparator != null) {
			query = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			query = new StringBundler(4);
		}

		query.append(_SQL_SELECT_CLICKS_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			query.append(_FINDER_COLUMN_UUID_C_UUID_3);
		}
		else {
			bindUuid = true;

			query.append(_FINDER_COLUMN_UUID_C_UUID_2);
		}

		query.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				query.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				query.append(_ORDER_BY_ENTITY_ALIAS);
				query.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						query.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(WHERE_GREATER_THAN);
					}
					else {
						query.append(WHERE_LESSER_THAN);
					}
				}
			}

			query.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				query.append(_ORDER_BY_ENTITY_ALIAS);
				query.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						query.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(ORDER_BY_ASC);
					}
					else {
						query.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			query.append(ClicksModelImpl.ORDER_BY_JPQL);
		}

		String sql = query.toString();

		Query q = session.createQuery(sql);

		q.setFirstResult(0);
		q.setMaxResults(2);

		QueryPos qPos = QueryPos.getInstance(q);

		if (bindUuid) {
			qPos.add(uuid);
		}

		qPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(clicks)) {

				qPos.add(orderByConditionValue);
			}
		}

		List<Clicks> list = q.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the clickses where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (Clicks clicks :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(clicks);
		}
	}

	/**
	 * Returns the number of clickses where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching clickses
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(3);

			query.append(_SQL_COUNT_CLICKS_WHERE);

			boolean bindUuid = false;

			if (uuid.isEmpty()) {
				query.append(_FINDER_COLUMN_UUID_C_UUID_3);
			}
			else {
				bindUuid = true;

				query.append(_FINDER_COLUMN_UUID_C_UUID_2);
			}

			query.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (bindUuid) {
					qPos.add(uuid);
				}

				qPos.add(companyId);

				count = (Long)q.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_UUID_C_UUID_2 =
		"clicks.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(clicks.uuid IS NULL OR clicks.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"clicks.companyId = ?";

	private FinderPath _finderPathFetchByC_G_K_E;
	private FinderPath _finderPathCountByC_G_K_E;

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
	@Override
	public Clicks findByC_G_K_E(
			long companyId, long groupId, String keywords, long entryClassPK)
		throws NoSuchClicksException {

		Clicks clicks = fetchByC_G_K_E(
			companyId, groupId, keywords, entryClassPK);

		if (clicks == null) {
			StringBundler msg = new StringBundler(10);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("companyId=");
			msg.append(companyId);

			msg.append(", groupId=");
			msg.append(groupId);

			msg.append(", keywords=");
			msg.append(keywords);

			msg.append(", entryClassPK=");
			msg.append(entryClassPK);

			msg.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(msg.toString());
			}

			throw new NoSuchClicksException(msg.toString());
		}

		return clicks;
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
	@Override
	public Clicks fetchByC_G_K_E(
		long companyId, long groupId, String keywords, long entryClassPK) {

		return fetchByC_G_K_E(companyId, groupId, keywords, entryClassPK, true);
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
	@Override
	public Clicks fetchByC_G_K_E(
		long companyId, long groupId, String keywords, long entryClassPK,
		boolean retrieveFromCache) {

		keywords = Objects.toString(keywords, "");

		Object[] finderArgs = new Object[] {
			companyId, groupId, keywords, entryClassPK
		};

		Object result = null;

		if (retrieveFromCache) {
			result = finderCache.getResult(
				_finderPathFetchByC_G_K_E, finderArgs, this);
		}

		if (result instanceof Clicks) {
			Clicks clicks = (Clicks)result;

			if ((companyId != clicks.getCompanyId()) ||
				(groupId != clicks.getGroupId()) ||
				!Objects.equals(keywords, clicks.getKeywords()) ||
				(entryClassPK != clicks.getEntryClassPK())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler query = new StringBundler(6);

			query.append(_SQL_SELECT_CLICKS_WHERE);

			query.append(_FINDER_COLUMN_C_G_K_E_COMPANYID_2);

			query.append(_FINDER_COLUMN_C_G_K_E_GROUPID_2);

			boolean bindKeywords = false;

			if (keywords.isEmpty()) {
				query.append(_FINDER_COLUMN_C_G_K_E_KEYWORDS_3);
			}
			else {
				bindKeywords = true;

				query.append(_FINDER_COLUMN_C_G_K_E_KEYWORDS_2);
			}

			query.append(_FINDER_COLUMN_C_G_K_E_ENTRYCLASSPK_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(companyId);

				qPos.add(groupId);

				if (bindKeywords) {
					qPos.add(keywords);
				}

				qPos.add(entryClassPK);

				List<Clicks> list = q.list();

				if (list.isEmpty()) {
					finderCache.putResult(
						_finderPathFetchByC_G_K_E, finderArgs, list);
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							_log.warn(
								"ClicksPersistenceImpl.fetchByC_G_K_E(long, long, String, long, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					Clicks clicks = list.get(0);

					result = clicks;

					cacheResult(clicks);
				}
			}
			catch (Exception e) {
				finderCache.removeResult(_finderPathFetchByC_G_K_E, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		if (result instanceof List<?>) {
			return null;
		}
		else {
			return (Clicks)result;
		}
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
	@Override
	public Clicks removeByC_G_K_E(
			long companyId, long groupId, String keywords, long entryClassPK)
		throws NoSuchClicksException {

		Clicks clicks = findByC_G_K_E(
			companyId, groupId, keywords, entryClassPK);

		return remove(clicks);
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
	@Override
	public int countByC_G_K_E(
		long companyId, long groupId, String keywords, long entryClassPK) {

		keywords = Objects.toString(keywords, "");

		FinderPath finderPath = _finderPathCountByC_G_K_E;

		Object[] finderArgs = new Object[] {
			companyId, groupId, keywords, entryClassPK
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(5);

			query.append(_SQL_COUNT_CLICKS_WHERE);

			query.append(_FINDER_COLUMN_C_G_K_E_COMPANYID_2);

			query.append(_FINDER_COLUMN_C_G_K_E_GROUPID_2);

			boolean bindKeywords = false;

			if (keywords.isEmpty()) {
				query.append(_FINDER_COLUMN_C_G_K_E_KEYWORDS_3);
			}
			else {
				bindKeywords = true;

				query.append(_FINDER_COLUMN_C_G_K_E_KEYWORDS_2);
			}

			query.append(_FINDER_COLUMN_C_G_K_E_ENTRYCLASSPK_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(companyId);

				qPos.add(groupId);

				if (bindKeywords) {
					qPos.add(keywords);
				}

				qPos.add(entryClassPK);

				count = (Long)q.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_C_G_K_E_COMPANYID_2 =
		"clicks.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_G_K_E_GROUPID_2 =
		"clicks.groupId = ? AND ";

	private static final String _FINDER_COLUMN_C_G_K_E_KEYWORDS_2 =
		"clicks.keywords = ? AND ";

	private static final String _FINDER_COLUMN_C_G_K_E_KEYWORDS_3 =
		"(clicks.keywords IS NULL OR clicks.keywords = '') AND ";

	private static final String _FINDER_COLUMN_C_G_K_E_ENTRYCLASSPK_2 =
		"clicks.entryClassPK = ?";

	public ClicksPersistenceImpl() {
		setModelClass(Clicks.class);

		setModelImplClass(ClicksImpl.class);
		setModelPKClass(long.class);

		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);
	}

	/**
	 * Caches the clicks in the entity cache if it is enabled.
	 *
	 * @param clicks the clicks
	 */
	@Override
	public void cacheResult(Clicks clicks) {
		entityCache.putResult(
			entityCacheEnabled, ClicksImpl.class, clicks.getPrimaryKey(),
			clicks);

		finderCache.putResult(
			_finderPathFetchByUUID_G,
			new Object[] {clicks.getUuid(), clicks.getGroupId()}, clicks);

		finderCache.putResult(
			_finderPathFetchByC_G_K_E,
			new Object[] {
				clicks.getCompanyId(), clicks.getGroupId(),
				clicks.getKeywords(), clicks.getEntryClassPK()
			},
			clicks);

		clicks.resetOriginalValues();
	}

	/**
	 * Caches the clickses in the entity cache if it is enabled.
	 *
	 * @param clickses the clickses
	 */
	@Override
	public void cacheResult(List<Clicks> clickses) {
		for (Clicks clicks : clickses) {
			if (entityCache.getResult(
					entityCacheEnabled, ClicksImpl.class,
					clicks.getPrimaryKey()) == null) {

				cacheResult(clicks);
			}
			else {
				clicks.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all clickses.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ClicksImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the clicks.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(Clicks clicks) {
		entityCache.removeResult(
			entityCacheEnabled, ClicksImpl.class, clicks.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		clearUniqueFindersCache((ClicksModelImpl)clicks, true);
	}

	@Override
	public void clearCache(List<Clicks> clickses) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Clicks clicks : clickses) {
			entityCache.removeResult(
				entityCacheEnabled, ClicksImpl.class, clicks.getPrimaryKey());

			clearUniqueFindersCache((ClicksModelImpl)clicks, true);
		}
	}

	protected void cacheUniqueFindersCache(ClicksModelImpl clicksModelImpl) {
		Object[] args = new Object[] {
			clicksModelImpl.getUuid(), clicksModelImpl.getGroupId()
		};

		finderCache.putResult(
			_finderPathCountByUUID_G, args, Long.valueOf(1), false);
		finderCache.putResult(
			_finderPathFetchByUUID_G, args, clicksModelImpl, false);

		args = new Object[] {
			clicksModelImpl.getCompanyId(), clicksModelImpl.getGroupId(),
			clicksModelImpl.getKeywords(), clicksModelImpl.getEntryClassPK()
		};

		finderCache.putResult(
			_finderPathCountByC_G_K_E, args, Long.valueOf(1), false);
		finderCache.putResult(
			_finderPathFetchByC_G_K_E, args, clicksModelImpl, false);
	}

	protected void clearUniqueFindersCache(
		ClicksModelImpl clicksModelImpl, boolean clearCurrent) {

		if (clearCurrent) {
			Object[] args = new Object[] {
				clicksModelImpl.getUuid(), clicksModelImpl.getGroupId()
			};

			finderCache.removeResult(_finderPathCountByUUID_G, args);
			finderCache.removeResult(_finderPathFetchByUUID_G, args);
		}

		if ((clicksModelImpl.getColumnBitmask() &
			 _finderPathFetchByUUID_G.getColumnBitmask()) != 0) {

			Object[] args = new Object[] {
				clicksModelImpl.getOriginalUuid(),
				clicksModelImpl.getOriginalGroupId()
			};

			finderCache.removeResult(_finderPathCountByUUID_G, args);
			finderCache.removeResult(_finderPathFetchByUUID_G, args);
		}

		if (clearCurrent) {
			Object[] args = new Object[] {
				clicksModelImpl.getCompanyId(), clicksModelImpl.getGroupId(),
				clicksModelImpl.getKeywords(), clicksModelImpl.getEntryClassPK()
			};

			finderCache.removeResult(_finderPathCountByC_G_K_E, args);
			finderCache.removeResult(_finderPathFetchByC_G_K_E, args);
		}

		if ((clicksModelImpl.getColumnBitmask() &
			 _finderPathFetchByC_G_K_E.getColumnBitmask()) != 0) {

			Object[] args = new Object[] {
				clicksModelImpl.getOriginalCompanyId(),
				clicksModelImpl.getOriginalGroupId(),
				clicksModelImpl.getOriginalKeywords(),
				clicksModelImpl.getOriginalEntryClassPK()
			};

			finderCache.removeResult(_finderPathCountByC_G_K_E, args);
			finderCache.removeResult(_finderPathFetchByC_G_K_E, args);
		}
	}

	/**
	 * Creates a new clicks with the primary key. Does not add the clicks to the database.
	 *
	 * @param clickId the primary key for the new clicks
	 * @return the new clicks
	 */
	@Override
	public Clicks create(long clickId) {
		Clicks clicks = new ClicksImpl();

		clicks.setNew(true);
		clicks.setPrimaryKey(clickId);

		String uuid = PortalUUIDUtil.generate();

		clicks.setUuid(uuid);

		clicks.setCompanyId(CompanyThreadLocal.getCompanyId());

		return clicks;
	}

	/**
	 * Removes the clicks with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param clickId the primary key of the clicks
	 * @return the clicks that was removed
	 * @throws NoSuchClicksException if a clicks with the primary key could not be found
	 */
	@Override
	public Clicks remove(long clickId) throws NoSuchClicksException {
		return remove((Serializable)clickId);
	}

	/**
	 * Removes the clicks with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the clicks
	 * @return the clicks that was removed
	 * @throws NoSuchClicksException if a clicks with the primary key could not be found
	 */
	@Override
	public Clicks remove(Serializable primaryKey) throws NoSuchClicksException {
		Session session = null;

		try {
			session = openSession();

			Clicks clicks = (Clicks)session.get(ClicksImpl.class, primaryKey);

			if (clicks == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchClicksException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(clicks);
		}
		catch (NoSuchClicksException nsee) {
			throw nsee;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected Clicks removeImpl(Clicks clicks) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(clicks)) {
				clicks = (Clicks)session.get(
					ClicksImpl.class, clicks.getPrimaryKeyObj());
			}

			if (clicks != null) {
				session.delete(clicks);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (clicks != null) {
			clearCache(clicks);
		}

		return clicks;
	}

	@Override
	public Clicks updateImpl(Clicks clicks) {
		boolean isNew = clicks.isNew();

		if (!(clicks instanceof ClicksModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(clicks.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(clicks);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in clicks proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Clicks implementation " +
					clicks.getClass());
		}

		ClicksModelImpl clicksModelImpl = (ClicksModelImpl)clicks;

		if (Validator.isNull(clicks.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			clicks.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date now = new Date();

		if (isNew && (clicks.getCreateDate() == null)) {
			if (serviceContext == null) {
				clicks.setCreateDate(now);
			}
			else {
				clicks.setCreateDate(serviceContext.getCreateDate(now));
			}
		}

		if (!clicksModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				clicks.setModifiedDate(now);
			}
			else {
				clicks.setModifiedDate(serviceContext.getModifiedDate(now));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (clicks.isNew()) {
				session.save(clicks);

				clicks.setNew(false);
			}
			else {
				clicks = (Clicks)session.merge(clicks);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (!_columnBitmaskEnabled) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}
		else if (isNew) {
			Object[] args = new Object[] {clicksModelImpl.getUuid()};

			finderCache.removeResult(_finderPathCountByUuid, args);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindByUuid, args);

			args = new Object[] {
				clicksModelImpl.getUuid(), clicksModelImpl.getCompanyId()
			};

			finderCache.removeResult(_finderPathCountByUuid_C, args);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindByUuid_C, args);

			finderCache.removeResult(_finderPathCountAll, FINDER_ARGS_EMPTY);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindAll, FINDER_ARGS_EMPTY);
		}
		else {
			if ((clicksModelImpl.getColumnBitmask() &
				 _finderPathWithoutPaginationFindByUuid.getColumnBitmask()) !=
					 0) {

				Object[] args = new Object[] {
					clicksModelImpl.getOriginalUuid()
				};

				finderCache.removeResult(_finderPathCountByUuid, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByUuid, args);

				args = new Object[] {clicksModelImpl.getUuid()};

				finderCache.removeResult(_finderPathCountByUuid, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByUuid, args);
			}

			if ((clicksModelImpl.getColumnBitmask() &
				 _finderPathWithoutPaginationFindByUuid_C.getColumnBitmask()) !=
					 0) {

				Object[] args = new Object[] {
					clicksModelImpl.getOriginalUuid(),
					clicksModelImpl.getOriginalCompanyId()
				};

				finderCache.removeResult(_finderPathCountByUuid_C, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByUuid_C, args);

				args = new Object[] {
					clicksModelImpl.getUuid(), clicksModelImpl.getCompanyId()
				};

				finderCache.removeResult(_finderPathCountByUuid_C, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByUuid_C, args);
			}
		}

		entityCache.putResult(
			entityCacheEnabled, ClicksImpl.class, clicks.getPrimaryKey(),
			clicks, false);

		clearUniqueFindersCache(clicksModelImpl, false);
		cacheUniqueFindersCache(clicksModelImpl);

		clicks.resetOriginalValues();

		return clicks;
	}

	/**
	 * Returns the clicks with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the clicks
	 * @return the clicks
	 * @throws NoSuchClicksException if a clicks with the primary key could not be found
	 */
	@Override
	public Clicks findByPrimaryKey(Serializable primaryKey)
		throws NoSuchClicksException {

		Clicks clicks = fetchByPrimaryKey(primaryKey);

		if (clicks == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchClicksException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return clicks;
	}

	/**
	 * Returns the clicks with the primary key or throws a <code>NoSuchClicksException</code> if it could not be found.
	 *
	 * @param clickId the primary key of the clicks
	 * @return the clicks
	 * @throws NoSuchClicksException if a clicks with the primary key could not be found
	 */
	@Override
	public Clicks findByPrimaryKey(long clickId) throws NoSuchClicksException {
		return findByPrimaryKey((Serializable)clickId);
	}

	/**
	 * Returns the clicks with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param clickId the primary key of the clicks
	 * @return the clicks, or <code>null</code> if a clicks with the primary key could not be found
	 */
	@Override
	public Clicks fetchByPrimaryKey(long clickId) {
		return fetchByPrimaryKey((Serializable)clickId);
	}

	/**
	 * Returns all the clickses.
	 *
	 * @return the clickses
	 */
	@Override
	public List<Clicks> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
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
	@Override
	public List<Clicks> findAll(int start, int end) {
		return findAll(start, end, null);
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
	@Override
	public List<Clicks> findAll(
		int start, int end, OrderByComparator<Clicks> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
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
	@Override
	public List<Clicks> findAll(
		int start, int end, OrderByComparator<Clicks> orderByComparator,
		boolean retrieveFromCache) {

		boolean pagination = true;
		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			pagination = false;
			finderPath = _finderPathWithoutPaginationFindAll;
			finderArgs = FINDER_ARGS_EMPTY;
		}
		else {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<Clicks> list = null;

		if (retrieveFromCache) {
			list = (List<Clicks>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				query.append(_SQL_SELECT_CLICKS);

				appendOrderByComparator(
					query, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_CLICKS;

				if (pagination) {
					sql = sql.concat(ClicksModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<Clicks>)QueryUtil.list(
						q, getDialect(), start, end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<Clicks>)QueryUtil.list(
						q, getDialect(), start, end);
				}

				cacheResult(list);

				finderCache.putResult(finderPath, finderArgs, list);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the clickses from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (Clicks clicks : findAll()) {
			remove(clicks);
		}
	}

	/**
	 * Returns the number of clickses.
	 *
	 * @return the number of clickses
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(_SQL_COUNT_CLICKS);

				count = (Long)q.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception e) {
				finderCache.removeResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "clickId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CLICKS;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ClicksModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the clicks persistence.
	 */
	@Activate
	public void activate() {
		ClicksModelImpl.setEntityCacheEnabled(entityCacheEnabled);
		ClicksModelImpl.setFinderCacheEnabled(finderCacheEnabled);

		_finderPathWithPaginationFindAll = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, ClicksImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, ClicksImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll",
			new String[0]);

		_finderPathCountAll = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0]);

		_finderPathWithPaginationFindByUuid = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, ClicksImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			});

		_finderPathWithoutPaginationFindByUuid = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, ClicksImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
			new String[] {String.class.getName()},
			ClicksModelImpl.UUID_COLUMN_BITMASK |
			ClicksModelImpl.CLICKCOUNT_COLUMN_BITMASK);

		_finderPathCountByUuid = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
			new String[] {String.class.getName()});

		_finderPathFetchByUUID_G = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, ClicksImpl.class,
			FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
			new String[] {String.class.getName(), Long.class.getName()},
			ClicksModelImpl.UUID_COLUMN_BITMASK |
			ClicksModelImpl.GROUPID_COLUMN_BITMASK);

		_finderPathCountByUUID_G = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUUID_G",
			new String[] {String.class.getName(), Long.class.getName()});

		_finderPathWithPaginationFindByUuid_C = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, ClicksImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			});

		_finderPathWithoutPaginationFindByUuid_C = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, ClicksImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			ClicksModelImpl.UUID_COLUMN_BITMASK |
			ClicksModelImpl.COMPANYID_COLUMN_BITMASK |
			ClicksModelImpl.CLICKCOUNT_COLUMN_BITMASK);

		_finderPathCountByUuid_C = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()});

		_finderPathFetchByC_G_K_E = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, ClicksImpl.class,
			FINDER_CLASS_NAME_ENTITY, "fetchByC_G_K_E",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Long.class.getName()
			},
			ClicksModelImpl.COMPANYID_COLUMN_BITMASK |
			ClicksModelImpl.GROUPID_COLUMN_BITMASK |
			ClicksModelImpl.KEYWORDS_COLUMN_BITMASK |
			ClicksModelImpl.ENTRYCLASSPK_COLUMN_BITMASK);

		_finderPathCountByC_G_K_E = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_G_K_E",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Long.class.getName()
			});
	}

	@Deactivate
	public void deactivate() {
		entityCache.removeCache(ClicksImpl.class.getName());
		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	@Reference(
		target = GSearchClickTrackingPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
		super.setConfiguration(configuration);

		_columnBitmaskEnabled = GetterUtil.getBoolean(
			configuration.get(
				"value.object.column.bitmask.enabled.fi.soveltia.liferay.gsearch.click.tracking.model.Clicks"),
			true);
	}

	@Override
	@Reference(
		target = GSearchClickTrackingPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = GSearchClickTrackingPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	private boolean _columnBitmaskEnabled;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_CLICKS =
		"SELECT clicks FROM Clicks clicks";

	private static final String _SQL_SELECT_CLICKS_WHERE =
		"SELECT clicks FROM Clicks clicks WHERE ";

	private static final String _SQL_COUNT_CLICKS =
		"SELECT COUNT(clicks) FROM Clicks clicks";

	private static final String _SQL_COUNT_CLICKS_WHERE =
		"SELECT COUNT(clicks) FROM Clicks clicks WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "clicks.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No Clicks exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No Clicks exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ClicksPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

}