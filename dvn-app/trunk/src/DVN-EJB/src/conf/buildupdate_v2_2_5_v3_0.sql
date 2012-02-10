-- changes for default header and footer
update vdcnetwork set defaultvdcheader='<style type="text/css">
body {margin:0; padding:0;}
</style>
<div style="width:100%; height:40px; background: url(/dvn/resources/images/customizationpattern.png) repeat-x left -35px #698DA2;"></div>
<div style="margin:0 auto; max-width:1000px;">';

update vdcnetwork set defaultvdcfooter='</div>';

-- removal of default description (replaced by new logic)
update vdcnetwork set defaultvdcannouncements = '';

update vdc  set announcements = ''
where announcements like 'A description of your Dataverse or announcements may be added here.%';


-- Changes for new template logic
Alter table studyField add dcmField boolean;

Alter table studyField add fieldType character varying(255);

update studyField set dcmField = false;

alter table templateField ADD dcmSortOrder  int;
update templatefield set dcmsortorder = studyfield_id - 44 where studyfield_id > 44 and studyfield_id < 65;

alter table templateField add ALLOWMULTIPLES boolean;
update templateField set ALLOWMULTIPLES = false;

alter table template ADD status character varying(255);

Alter table template add network boolean;
UPDATE template SET  network=false WHERE vdc_id is not null;
UPDATE template SET  network=true WHERE vdc_id is null;


-- Author related changes
Alter table studyAuthor add IDType  character varying(255);
Alter table studyAuthor add IDValue  character varying(255);


-- Adding mimetype column to the datafileformattype table
ALTER TABLE DataFileFormatType ADD COLUMN mimeType character varying(255);
UPDATE DataFileFormatType SET mimeType='application/x-rlang-transport' WHERE id=1;
UPDATE DataFileFormatType SET mimeType='application/x-stata' WHERE id=2;
UPDATE DataFileFormatType SET mimeType='application/x-R-2' WHERE id=3;


