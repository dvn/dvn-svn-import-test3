
--
-- first do DDL (this cannot be rollbacked)
--

--- NONE SO FAR

--
-- and now DML
--
begin;


update dvnversion set buildnumber=5;


INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ContributorRequestInfoPage', '/login/ContributorRequestInfoPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'CreatorRequestInfoPage','/login/CreatorRequestInfoPage.xhtml', null,null );
update pagedef set networkrole_id = 2 where name = 'EditHarvestSitePage';
update pagedef set networkrole_id = 2 where name =  'NetworkOptionsPage';
update pagedef set networkrole_id = 2 where name =  'HarvestSitesPage';

-- remove date of deposit from templates
update metadata set dateofdeposit = '' where id in (select metadata_id from template);

commit;
