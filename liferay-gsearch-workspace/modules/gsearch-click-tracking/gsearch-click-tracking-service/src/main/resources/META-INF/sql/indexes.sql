create index IX_E61C0215 on GSearchClickTracking_Clicks (companyId, groupId, keywords[$COLUMN_LENGTH:75$], entryClassPK);
create index IX_E58E78BE on GSearchClickTracking_Clicks (keywords[$COLUMN_LENGTH:75$]);
create index IX_DCD2E294 on GSearchClickTracking_Clicks (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_F11AFE16 on GSearchClickTracking_Clicks (uuid_[$COLUMN_LENGTH:75$], groupId);