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

package fi.soveltia.liferay.gsearch.click.tracking.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;

import fi.soveltia.liferay.gsearch.click.tracking.model.Clicks;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The cache model class for representing Clicks in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public class ClicksCacheModel implements CacheModel<Clicks>, Externalizable {

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ClicksCacheModel)) {
			return false;
		}

		ClicksCacheModel clicksCacheModel = (ClicksCacheModel)obj;

		if (clickId == clicksCacheModel.clickId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, clickId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(19);

		sb.append("{uuid=");
		sb.append(uuid);
		sb.append(", clickId=");
		sb.append(clickId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", keywords=");
		sb.append(keywords);
		sb.append(", entryClassPK=");
		sb.append(entryClassPK);
		sb.append(", clickCount=");
		sb.append(clickCount);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public Clicks toEntityModel() {
		ClicksImpl clicksImpl = new ClicksImpl();

		if (uuid == null) {
			clicksImpl.setUuid("");
		}
		else {
			clicksImpl.setUuid(uuid);
		}

		clicksImpl.setClickId(clickId);
		clicksImpl.setGroupId(groupId);
		clicksImpl.setCompanyId(companyId);

		if (createDate == Long.MIN_VALUE) {
			clicksImpl.setCreateDate(null);
		}
		else {
			clicksImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			clicksImpl.setModifiedDate(null);
		}
		else {
			clicksImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (keywords == null) {
			clicksImpl.setKeywords("");
		}
		else {
			clicksImpl.setKeywords(keywords);
		}

		clicksImpl.setEntryClassPK(entryClassPK);
		clicksImpl.setClickCount(clickCount);

		clicksImpl.resetOriginalValues();

		return clicksImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		uuid = objectInput.readUTF();

		clickId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		keywords = objectInput.readUTF();

		entryClassPK = objectInput.readLong();

		clickCount = objectInput.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		if (uuid == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(uuid);
		}

		objectOutput.writeLong(clickId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);
		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		if (keywords == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(keywords);
		}

		objectOutput.writeLong(entryClassPK);

		objectOutput.writeInt(clickCount);
	}

	public String uuid;
	public long clickId;
	public long groupId;
	public long companyId;
	public long createDate;
	public long modifiedDate;
	public String keywords;
	public long entryClassPK;
	public int clickCount;

}