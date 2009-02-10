This README file contains information about the files needed
to install the Dataverse Network (DVN) software version 1.4.

For installation instructions, visit: http://thedata.org/guides/installers

 
Files needed for Fresh installation:

- DVN-EAR_v1_4.xxx.ear 

- config_v1_4.zip, config files to be put into the glassfish
  domain directory (includes its own short README).  

- referenceData_v1_4.sql

(follow instructions for a fresh installation from http://thedata.org)


Files needed for an upgrade from v1.3:

- DVN-EAR_v1_4.xxx.ear

- buildupdate_v1_3_v1_4.sql

(to set up Google Analytics, follow instruction from http://thedata.org)


Note: the schema for organizing dataverses into groups has changed in 1.4.
In the new schema, top level groups cannot contain dataverses, only sub-groups.
(The dataverse groups are called "classifications" in DVN Network Options.)

If you have existing dataverse groups, you can add them to a default top level group
by running the following sql:

	insert into vdcgroup (id, description, displayorder, version, parent, name) values
	(nextval('vdcgroup_id_seq'),'',0,1,null,'Filter by Type');

	update vdcgroup set parent = (select id from vdcgroup where name = 'Filter by Type')
	where name != 'Filter by Type';


If you already have a group named 'Filter by Type', or you want your top level group 
to be another name, substitute 'Filter by Type' in the given sql with your preferred name.