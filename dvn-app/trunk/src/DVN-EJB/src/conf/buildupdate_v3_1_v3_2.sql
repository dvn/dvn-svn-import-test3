ALTER TABLE guestbookresponse ADD downloadtype character varying(255);
ALTER TABLE guestbookresponse ADD sessionId character varying(255);

/*for any dv that lacks a guest book - create one*/
INSERT INTO guestbookquestionnaire(
              enabled, firstnamerequired, lastnamerequired, emailrequired, institutionrequired, 
             positionrequired, vdc_id)
SELECT true, true, true, true, 
            false, false, id
FROM vdc
WHERE id not in (select vdc_id from guestbookquestionnaire);

/*Allow null vdc_id so that a network guestbook may be created*/
ALTER TABLE guestbookquestionnaire alter column vdc_id drop not null;

/*create network guest book*/
INSERT INTO guestbookquestionnaire(
             enabled,   firstnamerequired, lastnamerequired, emailrequired, 
             institutionrequired,  positionrequired, vdc_id)
SELECT true, true, true, true, 
            false, false, null;


ALTER TABLE studyField ADD allowControlledVocabulary boolean;

update studyField set allowControlledVocabulary = true;

update studyField set allowControlledVocabulary = false
where name = 'publicationIDType' or name = 'publicationReplicationData';