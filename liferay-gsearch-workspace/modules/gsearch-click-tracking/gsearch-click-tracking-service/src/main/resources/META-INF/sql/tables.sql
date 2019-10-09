create table GSearchClickTracking_Clicks (
	uuid_ VARCHAR(75) null,
	clickId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	createDate DATE null,
	modifiedDate DATE null,
	keywords VARCHAR(75) null,
	entryClassPK LONG,
	clickCount INTEGER
);