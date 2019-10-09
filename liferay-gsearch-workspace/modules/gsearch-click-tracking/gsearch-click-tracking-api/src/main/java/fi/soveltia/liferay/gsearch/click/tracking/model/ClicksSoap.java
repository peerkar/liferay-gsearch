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

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * This class is used by SOAP remote services, specifically {@link fi.soveltia.liferay.gsearch.click.tracking.service.http.ClicksServiceSoap}.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public class ClicksSoap implements Serializable {

	public static ClicksSoap toSoapModel(Clicks model) {
		ClicksSoap soapModel = new ClicksSoap();

		soapModel.setUuid(model.getUuid());
		soapModel.setClickId(model.getClickId());
		soapModel.setGroupId(model.getGroupId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setKeywords(model.getKeywords());
		soapModel.setEntryClassPK(model.getEntryClassPK());
		soapModel.setClickCount(model.getClickCount());

		return soapModel;
	}

	public static ClicksSoap[] toSoapModels(Clicks[] models) {
		ClicksSoap[] soapModels = new ClicksSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static ClicksSoap[][] toSoapModels(Clicks[][] models) {
		ClicksSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new ClicksSoap[models.length][models[0].length];
		}
		else {
			soapModels = new ClicksSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static ClicksSoap[] toSoapModels(List<Clicks> models) {
		List<ClicksSoap> soapModels = new ArrayList<ClicksSoap>(models.size());

		for (Clicks model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new ClicksSoap[soapModels.size()]);
	}

	public ClicksSoap() {
	}

	public long getPrimaryKey() {
		return _clickId;
	}

	public void setPrimaryKey(long pk) {
		setClickId(pk);
	}

	public String getUuid() {
		return _uuid;
	}

	public void setUuid(String uuid) {
		_uuid = uuid;
	}

	public long getClickId() {
		return _clickId;
	}

	public void setClickId(long clickId) {
		_clickId = clickId;
	}

	public long getGroupId() {
		return _groupId;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public Date getCreateDate() {
		return _createDate;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		_modifiedDate = modifiedDate;
	}

	public String getKeywords() {
		return _keywords;
	}

	public void setKeywords(String keywords) {
		_keywords = keywords;
	}

	public long getEntryClassPK() {
		return _entryClassPK;
	}

	public void setEntryClassPK(long entryClassPK) {
		_entryClassPK = entryClassPK;
	}

	public int getClickCount() {
		return _clickCount;
	}

	public void setClickCount(int clickCount) {
		_clickCount = clickCount;
	}

	private String _uuid;
	private long _clickId;
	private long _groupId;
	private long _companyId;
	private Date _createDate;
	private Date _modifiedDate;
	private String _keywords;
	private long _entryClassPK;
	private int _clickCount;

}