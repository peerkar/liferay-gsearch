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

package fi.soveltia.liferay.gsearch.click.tracking.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <p>
 * This class is a wrapper for {@link Clicks}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see Clicks
 * @generated
 */
@ProviderType
public class ClicksWrapper
	extends BaseModelWrapper<Clicks> implements Clicks, ModelWrapper<Clicks> {

	public ClicksWrapper(Clicks clicks) {
		super(clicks);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("uuid", getUuid());
		attributes.put("clickId", getClickId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("keywords", getKeywords());
		attributes.put("entryClassPK", getEntryClassPK());
		attributes.put("clickCount", getClickCount());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		Long clickId = (Long)attributes.get("clickId");

		if (clickId != null) {
			setClickId(clickId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		String keywords = (String)attributes.get("keywords");

		if (keywords != null) {
			setKeywords(keywords);
		}

		Long entryClassPK = (Long)attributes.get("entryClassPK");

		if (entryClassPK != null) {
			setEntryClassPK(entryClassPK);
		}

		Integer clickCount = (Integer)attributes.get("clickCount");

		if (clickCount != null) {
			setClickCount(clickCount);
		}
	}

	/**
	 * Returns the click count of this clicks.
	 *
	 * @return the click count of this clicks
	 */
	@Override
	public int getClickCount() {
		return model.getClickCount();
	}

	/**
	 * Returns the click ID of this clicks.
	 *
	 * @return the click ID of this clicks
	 */
	@Override
	public long getClickId() {
		return model.getClickId();
	}

	/**
	 * Returns the company ID of this clicks.
	 *
	 * @return the company ID of this clicks
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this clicks.
	 *
	 * @return the create date of this clicks
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the entry class pk of this clicks.
	 *
	 * @return the entry class pk of this clicks
	 */
	@Override
	public long getEntryClassPK() {
		return model.getEntryClassPK();
	}

	/**
	 * Returns the group ID of this clicks.
	 *
	 * @return the group ID of this clicks
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the keywords of this clicks.
	 *
	 * @return the keywords of this clicks
	 */
	@Override
	public String getKeywords() {
		return model.getKeywords();
	}

	/**
	 * Returns the modified date of this clicks.
	 *
	 * @return the modified date of this clicks
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the primary key of this clicks.
	 *
	 * @return the primary key of this clicks
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the uuid of this clicks.
	 *
	 * @return the uuid of this clicks
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the click count of this clicks.
	 *
	 * @param clickCount the click count of this clicks
	 */
	@Override
	public void setClickCount(int clickCount) {
		model.setClickCount(clickCount);
	}

	/**
	 * Sets the click ID of this clicks.
	 *
	 * @param clickId the click ID of this clicks
	 */
	@Override
	public void setClickId(long clickId) {
		model.setClickId(clickId);
	}

	/**
	 * Sets the company ID of this clicks.
	 *
	 * @param companyId the company ID of this clicks
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this clicks.
	 *
	 * @param createDate the create date of this clicks
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the entry class pk of this clicks.
	 *
	 * @param entryClassPK the entry class pk of this clicks
	 */
	@Override
	public void setEntryClassPK(long entryClassPK) {
		model.setEntryClassPK(entryClassPK);
	}

	/**
	 * Sets the group ID of this clicks.
	 *
	 * @param groupId the group ID of this clicks
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the keywords of this clicks.
	 *
	 * @param keywords the keywords of this clicks
	 */
	@Override
	public void setKeywords(String keywords) {
		model.setKeywords(keywords);
	}

	/**
	 * Sets the modified date of this clicks.
	 *
	 * @param modifiedDate the modified date of this clicks
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the primary key of this clicks.
	 *
	 * @param primaryKey the primary key of this clicks
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the uuid of this clicks.
	 *
	 * @param uuid the uuid of this clicks
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected ClicksWrapper wrap(Clicks clicks) {
		return new ClicksWrapper(clicks);
	}

}