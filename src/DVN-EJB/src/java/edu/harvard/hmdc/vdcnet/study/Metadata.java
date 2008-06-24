/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/*
 * Metadata.java
 *
 * Created on June 1, 2008 2:44 PM
 *
 */

package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.util.StringUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class Metadata implements java.io.Serializable {
    /**
     * Holds value of property id.
     */
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="metadata_gen")
    @SequenceGenerator(name="metadata_gen", sequenceName="metadata_id_seq")
    
    private Long id;
 
  
    /**
     * Getter for property id.
     * @return Value of property id.
     */
    public Long getId() {
        return this.id;
    }
    
    /**
     * Setter for property id.
     * @param id New value of property id.
     */
    public void setId(Long id) {
        this.id = id;
    }
    @OneToOne(mappedBy="metadata")
    private Study study;

 /*   public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }
*/
    
    @OneToOne(mappedBy="metadata")
    private Template template;

  

    
    public Metadata () {
    }    
        
    public void copyMetadata(Metadata copyTarget ) {
     
        copyTarget.setUNF(this.UNF);
        copyTarget.setAccessToSources(this.accessToSources);
        copyTarget.setActionsToMinimizeLoss(this.actionsToMinimizeLoss);
        copyTarget.setAvailabilityStatus(this.availabilityStatus);
        copyTarget.setCharacteristicOfSources(this.characteristicOfSources);
        copyTarget.setCitationRequirements(this.citationRequirements);
        copyTarget.setCleaningOperations(this.cleaningOperations);
        copyTarget.setCollectionMode(this.collectionMode);
        copyTarget.setCollectionSize(this.collectionSize);
        copyTarget.setConditions(this.conditions);
        copyTarget.setConfidentialityDeclaration(this.confidentialityDeclaration);
        copyTarget.setContact(contact);
        copyTarget.setControlOperations(controlOperations);
        copyTarget.setCountry(country);
        copyTarget.setDataCollectionSituation(dataCollectionSituation);
        copyTarget.setDataCollector(dataCollector);
        copyTarget.setDataSources(dataSources);
        copyTarget.setDateOfCollectionEnd(dateOfCollectionEnd);
        copyTarget.setDateOfCollectionStart(dateOfCollectionStart);
        copyTarget.setDateOfDeposit(dateOfDeposit);
        copyTarget.setDepositor(depositor);
        copyTarget.setDepositorRequirements(depositorRequirements);
        copyTarget.setDeviationsFromSampleDesign(deviationsFromSampleDesign);
        copyTarget.setDisclaimer(disclaimer);
        copyTarget.setDistributionDate(distributionDate);
        copyTarget.setDistributorContact(distributorContact);
        copyTarget.setDistributorContactAffiliation(distributorContactAffiliation);
        copyTarget.setDistributorContactEmail(distributorContactEmail);
        copyTarget.setFrequencyOfDataCollection(frequencyOfDataCollection);
        copyTarget.setFundingAgency(fundingAgency);
        copyTarget.setGeographicCoverage(geographicCoverage);
        copyTarget.setGeographicUnit(geographicUnit);
        copyTarget.setHarvestDVNTermsOfUse(harvestDVNTermsOfUse);
        copyTarget.setHarvestDVTermsOfUse(harvestDVTermsOfUse);
        copyTarget.setHarvestHoldings(harvestHoldings);
        copyTarget.setHarvestIdentifier(harvestIdentifier);
        copyTarget.setKindOfData(kindOfData);
        copyTarget.setOriginOfSources(originOfSources);
        copyTarget.setOriginalArchive(originalArchive);
        copyTarget.setOtherDataAppraisal(otherDataAppraisal);
        copyTarget.setPlaceOfAccess(placeOfAccess);
        copyTarget.setProductionDate(productionDate);
        copyTarget.setProductionPlace(productionPlace);
        copyTarget.setReplicationFor(replicationFor);
        copyTarget.setResearchInstrument(researchInstrument);
        copyTarget.setResponseRate(responseRate);
        copyTarget.setRestrictions(restrictions);
        copyTarget.setSamplingErrorEstimate(samplingErrorEstimate);
        copyTarget.setSamplingProcedure(samplingProcedure);
        copyTarget.setSeriesInformation(seriesInformation);
        copyTarget.setSeriesName(seriesName);
        copyTarget.setSpecialPermissions(specialPermissions);
        copyTarget.setStudyVersion(studyVersion);
        copyTarget.setSubTitle(subTitle);
        copyTarget.setTimeMethod(timeMethod);
        copyTarget.setTimePeriodCoveredEnd(timePeriodCoveredEnd);
        copyTarget.setTimePeriodCoveredStart(timePeriodCoveredStart);
        copyTarget.setTitle(title);
        copyTarget.setUnitOfAnalysis(unitOfAnalysis);
        copyTarget.setUniverse(universe);
        
        
        
     
        copyTarget.setStudyAbstracts(new ArrayList<StudyAbstract>());
        for(StudyAbstract sa: studyAbstracts) {
            StudyAbstract cloneAbstract = new StudyAbstract();
            cloneAbstract.setDate(sa.getDate());
            cloneAbstract.setDisplayOrder(sa.getDisplayOrder());
            cloneAbstract.setMetadata(copyTarget);
            cloneAbstract.setText(sa.getText());
            copyTarget.getStudyAbstracts().add(cloneAbstract);            
        }
        copyTarget.setStudyAuthors(new ArrayList<StudyAuthor>());
        for (StudyAuthor author: studyAuthors) {
            StudyAuthor cloneAuthor = new StudyAuthor();
            cloneAuthor.setAffiliation(author.getAffiliation());
            cloneAuthor.setDisplayOrder(author.getDisplayOrder());
            cloneAuthor.setMetadata(copyTarget);
            cloneAuthor.setName(author.getName());
            copyTarget.getStudyAuthors().add(cloneAuthor);
        }
        copyTarget.setStudyDistributors(new ArrayList<StudyDistributor>());        
        for (StudyDistributor dist: studyDistributors){
            StudyDistributor cloneDist = new StudyDistributor();
            cloneDist.setAbbreviation(dist.getAbbreviation());
            cloneDist.setAffiliation(dist.getAffiliation());
            cloneDist.setDisplayOrder(dist.getDisplayOrder());
            cloneDist.setMetadata(copyTarget);
            cloneDist.setLogo(dist.getLogo());
            cloneDist.setName(dist.getName());
            cloneDist.setUrl(dist.getUrl());
            copyTarget.getStudyDistributors().add(cloneDist);
        }
        copyTarget.setStudyGeoBoundings(new ArrayList<StudyGeoBounding>());        
        for(StudyGeoBounding geo: studyGeoBoundings) {
            StudyGeoBounding cloneGeo = new StudyGeoBounding();
            cloneGeo.setDisplayOrder(geo.getDisplayOrder());
            cloneGeo.setMetadata(copyTarget);
            cloneGeo.setEastLongitude(geo.getEastLongitude());
            cloneGeo.setNorthLatitude(geo.getNorthLatitude());
            cloneGeo.setSouthLatitude(geo.getSouthLatitude());
            cloneGeo.setWestLongitude(geo.getWestLongitude());
            copyTarget.getStudyGeoBoundings().add(geo);
        }
        copyTarget.setStudyGrants(new ArrayList<StudyGrant>());        
        for(StudyGrant grant: studyGrants) {
            StudyGrant cloneGrant = new StudyGrant();
            cloneGrant.setAgency(grant.getAgency());
            cloneGrant.setDisplayOrder(grant.getDisplayOrder());
            cloneGrant.setMetadata(copyTarget);
            cloneGrant.setNumber(grant.getNumber());
            copyTarget.getStudyGrants().add(cloneGrant);
        }
        copyTarget.setStudyKeywords(new ArrayList<StudyKeyword>());        
        for(StudyKeyword key: studyKeywords) {
            StudyKeyword cloneKey = new StudyKeyword();
            cloneKey.setDisplayOrder(key.getDisplayOrder());
            cloneKey.setMetadata(copyTarget);
            cloneKey.setValue(key.getValue());
            cloneKey.setVocab(key.getVocab());
            cloneKey.setVocabURI(key.getVocabURI());
            copyTarget.getStudyKeywords().add(key);
        }
       copyTarget.setStudyNotes(new ArrayList<StudyNote>());        
       for(StudyNote note: studyNotes) {
            StudyNote cloneNote = new StudyNote();
            cloneNote.setDisplayOrder(note.getDisplayOrder());
            cloneNote.setMetadata(copyTarget);
            cloneNote.setSubject(note.getSubject());
            cloneNote.setText(note.getText());
            cloneNote.setType(note.getType());
            copyTarget.getStudyNotes().add(note);
        }
        copyTarget.setStudyOtherIds(new ArrayList<StudyOtherId>());        
        for(StudyOtherId id: studyOtherIds) {
            StudyOtherId cloneId = new StudyOtherId();
            cloneId.setAgency(id.getAgency());
            cloneId.setDisplayOrder(id.getDisplayOrder());
            cloneId.setMetadata(copyTarget);
            cloneId.setOtherId(id.getOtherId());
            copyTarget.getStudyOtherIds().add(id);
        }
        copyTarget.setStudyOtherRefs(new ArrayList<StudyOtherRef>());        
        for(StudyOtherRef ref: studyOtherRefs) {
            StudyOtherRef cloneRef = new StudyOtherRef();
            cloneRef.setDisplayOrder(ref.getDisplayOrder());
            cloneRef.setMetadata(copyTarget);
            cloneRef.setText(ref.getText());
            copyTarget.getStudyOtherRefs().add(ref);
        }
        copyTarget.setStudyProducers(new ArrayList<StudyProducer>());        
        for(StudyProducer prod: studyProducers) {
            StudyProducer cloneProd = new StudyProducer();
            cloneProd.setAbbreviation(prod.getAbbreviation());
            cloneProd.setAffiliation(prod.getAffiliation());
            cloneProd.setDisplayOrder(prod.getDisplayOrder());
            cloneProd.setLogo(prod.getLogo());
            cloneProd.setMetadata(copyTarget);
            cloneProd.setName(prod.getName());
            cloneProd.setUrl(prod.getUrl());
        }
       copyTarget.setStudyRelMaterials(new ArrayList<StudyRelMaterial>());        
       for(StudyRelMaterial rel: studyRelMaterials) {
            StudyRelMaterial cloneRel = new StudyRelMaterial();
            cloneRel.setDisplayOrder(rel.getDisplayOrder());
            cloneRel.setMetadata(copyTarget);
            cloneRel.setText(rel.getText());
            copyTarget.getStudyRelMaterials().add(cloneRel);
        }
       copyTarget.setStudyRelPublications(new ArrayList<StudyRelPublication>());        
        for(StudyRelPublication rel: studyRelPublications){
            StudyRelStudy cloneRel = new StudyRelStudy();
            cloneRel.setDisplayOrder(rel.getDisplayOrder());
            cloneRel.setMetadata(copyTarget);
            cloneRel.setText(rel.getText());
            copyTarget.getStudyRelPublications().add(rel);
        }
        copyTarget.setStudyRelStudies(new ArrayList<StudyRelStudy>());        
        for(StudyRelStudy rel: studyRelStudies){
            StudyRelStudy cloneRel = new StudyRelStudy();
            cloneRel.setDisplayOrder(rel.getDisplayOrder());
            cloneRel.setMetadata(copyTarget);
            cloneRel.setText(rel.getText());
            copyTarget.getStudyRelStudies().add(rel);
        }
        copyTarget.setStudySoftware(new ArrayList<StudySoftware>());
        for(StudySoftware soft: studySoftware){
            StudySoftware cloneSoft = new StudySoftware();
            cloneSoft.setDisplayOrder(soft.getDisplayOrder());
            cloneSoft.setMetadata(copyTarget);
            cloneSoft.setName(soft.getName());
            cloneSoft.setSoftwareVersion(soft.getSoftwareVersion());
            copyTarget.getStudySoftware().add(soft);
        }
        copyTarget.setStudyTopicClasses(new ArrayList<StudyTopicClass>());
        for (StudyTopicClass topic: studyTopicClasses){
            StudyTopicClass cloneTopic = new StudyTopicClass();
            cloneTopic.setDisplayOrder(topic.getDisplayOrder());
            cloneTopic.setMetadata(copyTarget);
            cloneTopic.setValue(topic.getValue());
            cloneTopic.setVocab(topic.getVocab());
            cloneTopic.setVocabURI(topic.getVocabURI());
            copyTarget.getStudyTopicClasses().add(topic);
        }
       
    }
     
    public String getAuthorsStr() {
        return getAuthorsStr(true);
    }
    
    public String getAuthorsStr(boolean affiliation) {
        String str="";
        for (Iterator<StudyAuthor> it = getStudyAuthors().iterator(); it.hasNext();) {
            StudyAuthor sa =  it.next();
            if (str.trim().length()>1) {
                str+="; ";
            }
            str += sa.getName();
            if (affiliation) {
                if (!StringUtil.isEmpty(sa.getAffiliation())) {
                    str+=" ("+sa.getAffiliation()+")";
                }
            }
            
        }
        return str;
        
    }
    public String getDistributorNames() {
        String str="";
        for (Iterator<StudyDistributor> it = this.getStudyDistributors().iterator(); it.hasNext();) {
            StudyDistributor sd =  it.next();
            if (str.trim().length()>1) {
                str+=";";
            }
            str += sd.getName();
            
            
        }
        return str;
        
    }
   
    @Column(columnDefinition="TEXT")
    private String UNF;
  
    public String getUNF() {
        return UNF;
    }
    
    public void setUNF(String UNF) {
        this.UNF=UNF;
    }
    
    @Column(columnDefinition="TEXT")
    private String title;
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
 
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private java.util.List<StudyAuthor> studyAuthors;
 
    public java.util.List<StudyAuthor> getStudyAuthors() {
        return studyAuthors;
    }
    
    public void setStudyAuthors(java.util.List<StudyAuthor> studyAuthors) {
        this.studyAuthors = studyAuthors;
    }
    

    
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private java.util.List<StudyKeyword> studyKeywords;
    
    public java.util.List<StudyKeyword> getStudyKeywords() {
        return studyKeywords;
    }
    
    public void setStudyKeywords(java.util.List<StudyKeyword> studyKeywords) {
        this.studyKeywords = studyKeywords;
    }
    

    
    
    
    
   
   
    
    /**
     * Holds value of property version.
     */
    @Version
    private Long version;
    
    /**
     * Getter for property version.
     * @return Value of property version.
     */
    public Long getVersion() {
        return this.version;
    }
    
    /**
     * Setter for property version.
     * @param version New value of property version.
     */
    public void setVersion(Long version) {
        this.version = version;
    }
 
    
    /**
     * Holds value of property studyProducers.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyProducer> studyProducers;
    
    /**
     * Getter for property studyProducers.
     * @return Value of property studyProducers.
     */
    public List<StudyProducer> getStudyProducers() {
        return this.studyProducers;
    }
    
    /**
     * Setter for property studyProducers.
     * @param studyProducers New value of property studyProducers.
     */
    public void setStudyProducers(List<StudyProducer> studyProducers) {
        this.studyProducers = studyProducers;
    }
    
    /**
     * Holds value of property studySoftware.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudySoftware> studySoftware;
    
    /**
     * Getter for property studySoftware.
     * @return Value of property studySoftware.
     */
    public List<StudySoftware> getStudySoftware() {
        return this.studySoftware;
    }
    
    /**
     * Setter for property studySoftware.
     * @param studySoftware New value of property studySoftware.
     */
    public void setStudySoftware(List<StudySoftware> studySoftware) {
        this.studySoftware = studySoftware;
    }
    
    /**
     * Holds value of property studyDistributors.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyDistributor> studyDistributors;
    
    /**
     * Getter for property studyDistributors.
     * @return Value of property studyDistributors.
     */
    public List<StudyDistributor> getStudyDistributors() {
        return this.studyDistributors;
    }
    
    /**
     * Setter for property studyDistributors.
     * @param studyDistributors New value of property studyDistributors.
     */
    public void setStudyDistributors(List<StudyDistributor> studyDistributors) {
        this.studyDistributors = studyDistributors;
    }
    
    /**
     * Holds value of property studyTopicClasses.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyTopicClass> studyTopicClasses;
    
    /**
     * Getter for property studyTopicClasses.
     * @return Value of property studyTopicClasses.
     */
    public List<StudyTopicClass> getStudyTopicClasses() {
        return this.studyTopicClasses;
    }
    
    /**
     * Setter for property studyTopicClasses.
     * @param studyTopicClasses New value of property studyTopicClasses.
     */
    public void setStudyTopicClasses(List<StudyTopicClass> studyTopicClasses) {
        this.studyTopicClasses = studyTopicClasses;
    }
    
    /**
     * Holds value of property studyAbstracts.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyAbstract> studyAbstracts;
    
    /**
     * Getter for property studyAbstracts.
     * @return Value of property studyAbstracts.
     */
    public List<StudyAbstract> getStudyAbstracts() {
        return this.studyAbstracts;
    }
    
    /**
     * Setter for property studyAbstracts.
     * @param studyAbstracts New value of property studyAbstracts.
     */
    public void setStudyAbstracts(List<StudyAbstract> studyAbstracts) {
        this.studyAbstracts = studyAbstracts;
    }
    
    /**
     * Holds value of property studyNotes.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyNote> studyNotes;
    
    /**
     * Getter for property studyNotes.
     * @return Value of property studyNotes.
     */
    public List<StudyNote> getStudyNotes() {
        return this.studyNotes;
    }
    
    /**
     * Setter for property studyNotes.
     * @param studyNotes New value of property studyNotes.
     */
    public void setStudyNotes(List<StudyNote> studyNotes) {
        this.studyNotes = studyNotes;
    }
    
    /**
     * Holds value of property fundingAgency.
     */
    @Column(columnDefinition="TEXT")    
    private String fundingAgency;
    
    /**
     * Getter for property fundingAgency.
     * @return Value of property fundingAgency.
     */
    public String getFundingAgency() {
        return this.fundingAgency;
    }
    
    /**
     * Setter for property fundingAgency.
     * @param fundingAgency New value of property fundingAgency.
     */
    public void setFundingAgency(String fundingAgency) {
        this.fundingAgency = fundingAgency;
    }
    
    /**
     * Holds value of property seriesName.
     */
    @Column(columnDefinition="TEXT")   
    private String seriesName;
    
    /**
     * Getter for property seriesName.
     * @return Value of property seriesName.
     */
    public String getSeriesName() {
        return this.seriesName;
    }
    
    /**
     * Setter for property seriesName.
     * @param seriesName New value of property seriesName.
     */
    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }
    
    /**
     * Holds value of property seriesInformation.
     */
    @Column(columnDefinition="TEXT")
    private String seriesInformation;
    
    /**
     * Getter for property seriesInformation.
     * @return Value of property seriesInformation.
     */
    public String getSeriesInformation() {
        return this.seriesInformation;
    }
    
    /**
     * Setter for property seriesInformation.
     * @param seriesInformation New value of property seriesInformation.
     */
    public void setSeriesInformation(String seriesInformation) {
        this.seriesInformation = seriesInformation;
    }
    
    /**
     * Holds value of property studyVersion.
     */
    @Column(columnDefinition="TEXT")
    private String studyVersion;
    
    /**
     * Getter for property studyVersion.
     * @return Value of property studyVersion.
     */
    public String getStudyVersion() {
        return this.studyVersion;
    }
    
    /**
     * Setter for property studyVersion.
     * @param studyVersion New value of property studyVersion.
     */
    public void setStudyVersion(String studyVersion) {
        this.studyVersion = studyVersion;
    }
    
    /**
     * Holds value of property timePeriodCoveredStart.
     */
    @Column(columnDefinition="TEXT")
    private String timePeriodCoveredStart;
    
    /**
     * Getter for property timePeriodCoveredStart.
     * @return Value of property timePeriodCoveredStart.
     */
    public String getTimePeriodCoveredStart() {
        return this.timePeriodCoveredStart;
    }
    
    /**
     * Setter for property timePeriodCoveredStart.
     * @param timePeriodCoveredStart New value of property timePeriodCoveredStart.
     */
    public void setTimePeriodCoveredStart(String timePeriodCoveredStart) {
        this.timePeriodCoveredStart = timePeriodCoveredStart;
    }
    
    /**
     * Holds value of property timePeriodCoveredEnd.
     */
    @Column(columnDefinition="TEXT")
    private String timePeriodCoveredEnd;
    
    /**
     * Getter for property timePeriodCoveredEnd.
     * @return Value of property timePeriodCoveredEnd.
     */
    public String getTimePeriodCoveredEnd() {
        return this.timePeriodCoveredEnd;
    }
    
    /**
     * Setter for property timePeriodCoveredEnd.
     * @param timePeriodCoveredEnd New value of property timePeriodCoveredEnd.
     */
    public void setTimePeriodCoveredEnd(String timePeriodCoveredEnd) {
        this.timePeriodCoveredEnd = timePeriodCoveredEnd;
    }
    
    /**
     * Holds value of property dateOfCollectionStart.
     */
    @Column(columnDefinition="TEXT")
    private String dateOfCollectionStart;
    
    /**
     * Getter for property dateOfCollectionStart.
     * @return Value of property dateOfCollectionStart.
     */
    public String getDateOfCollectionStart() {
        return this.dateOfCollectionStart;
    }
    
    /**
     * Setter for property dateOfCollectionStart.
     * @param dateOfCollectionStart New value of property dateOfCollectionStart.
     */
    public void setDateOfCollectionStart(String dateOfCollectionStart) {
        this.dateOfCollectionStart = dateOfCollectionStart;
    }
    
    /**
     * Holds value of property dateOfCollectionEnd.
     */
    @Column(columnDefinition="TEXT")
    private String dateOfCollectionEnd;
    
    /**
     * Getter for property dateOfCollectionEnd.
     * @return Value of property dateOfCollectionEnd.
     */
    public String getDateOfCollectionEnd() {
        return this.dateOfCollectionEnd;
    }
    
    /**
     * Setter for property dateOfCollectionEnd.
     * @param dateOfCollectionEnd New value of property dateOfCollectionEnd.
     */
    public void setDateOfCollectionEnd(String dateOfCollectionEnd) {
        this.dateOfCollectionEnd = dateOfCollectionEnd;
    }
    
    /**
     * Holds value of property country.
     */
    @Column(columnDefinition="TEXT")
    private String country;
    
    /**
     * Getter for property country.
     * @return Value of property country.
     */
    public String getCountry() {
        return this.country;
    }
    
    /**
     * Setter for property country.
     * @param country New value of property country.
     */
    public void setCountry(String country) {
        this.country = country;
    }
    
    /**
     * Holds value of property geographicCoverage.
     */
    @Column(columnDefinition="TEXT")
    private String geographicCoverage;
    
    /**
     * Getter for property geographicCoverage.
     * @return Value of property geographicCoverage.
     */
    public String getGeographicCoverage() {
        return this.geographicCoverage;
    }
    
    /**
     * Setter for property geographicCoverage.
     * @param geographicCoverage New value of property geographicCoverage.
     */
    public void setGeographicCoverage(String geographicCoverage) {
        this.geographicCoverage = geographicCoverage;
    }
    
    /**
     * Holds value of property geographicUnit.
     */
    @Column(columnDefinition="TEXT")
    private String geographicUnit;
    
    /**
     * Getter for property geographicUnit.
     * @return Value of property geographicUnit.
     */
    public String getGeographicUnit() {
        return this.geographicUnit;
    }
    
    /**
     * Setter for property geographicUnit.
     * @param geographicUnit New value of property geographicUnit.
     */
    public void setGeographicUnit(String geographicUnit) {
        this.geographicUnit = geographicUnit;
    }
    
    /**
     * Holds value of property unitOfAnalysis.
     */
    @Column(columnDefinition="TEXT")
    private String unitOfAnalysis;
    
    /**
     * Getter for property unitOfAnalysis.
     * @return Value of property unitOfAnalysis.
     */
    public String getUnitOfAnalysis() {
        return this.unitOfAnalysis;
    }
    
    /**
     * Setter for property unitOfAnalysis.
     * @param unitOfAnalysis New value of property unitOfAnalysis.
     */
    public void setUnitOfAnalysis(String unitOfAnalysis) {
        this.unitOfAnalysis = unitOfAnalysis;
    }
    
    /**
     * Holds value of property universe.
     */
    @Column(columnDefinition="TEXT")
    private String universe;
    
    /**
     * Getter for property universe.
     * @return Value of property universe.
     */
    public String getUniverse() {
        return this.universe;
    }
    
    /**
     * Setter for property universe.
     * @param universe New value of property universe.
     */
    public void setUniverse(String universe) {
        this.universe = universe;
    }
    
    /**
     * Holds value of property kindOfData.
     */
    @Column(columnDefinition="TEXT")
    private String kindOfData;
    
    /**
     * Getter for property kindOfData.
     * @return Value of property kindOfData.
     */
    public String getKindOfData() {
        return this.kindOfData;
    }
    
    /**
     * Setter for property kindOfData.
     * @param kindOfData New value of property kindOfData.
     */
    public void setKindOfData(String kindOfData) {
        this.kindOfData = kindOfData;
    }
    
    /**
     * Holds value of property timeMethod.
     */
    @Column(columnDefinition="TEXT")
    private String timeMethod;
    
    /**
     * Getter for property timeMethod.
     * @return Value of property timeMethod.
     */
    public String getTimeMethod() {
        return this.timeMethod;
    }
    
    /**
     * Setter for property timeMethod.
     * @param timeMethod New value of property timeMethod.
     */
    public void setTimeMethod(String timeMethod) {
        this.timeMethod = timeMethod;
    }
    
    /**
     * Holds value of property dataCollector.
     */
    @Column(columnDefinition="TEXT")
    private String dataCollector;
    
    /**
     * Getter for property dataCollector.
     * @return Value of property dataCollector.
     */
    public String getDataCollector() {
        return this.dataCollector;
    }
    
    /**
     * Setter for property dataCollector.
     * @param dataCollector New value of property dataCollector.
     */
    public void setDataCollector(String dataCollector) {
        this.dataCollector = dataCollector;
    }
    
    /**
     * Holds value of property frequencyOfDataCollection.
     */
    @Column(columnDefinition="TEXT")
    private String frequencyOfDataCollection;
    
    /**
     * Getter for property frequencyOfDataCollection.
     * @return Value of property frequencyOfDataCollection.
     */
    public String getFrequencyOfDataCollection() {
        return this.frequencyOfDataCollection;
    }
    
    /**
     * Setter for property frequencyOfDataCollection.
     * @param frequencyOfDataCollection New value of property frequencyOfDataCollection.
     */
    public void setFrequencyOfDataCollection(String frequencyOfDataCollection) {
        this.frequencyOfDataCollection = frequencyOfDataCollection;
    }
    
    /**
     * Holds value of property samplingProcedure.
     */
    @Column(columnDefinition="TEXT")
    private String samplingProcedure;
    
    /**
     * Getter for property samplingProdedure.
     * @return Value of property samplingProdedure.
     */
    public String getSamplingProcedure() {
        return this.samplingProcedure;
    }
    
    /**
     * Setter for property samplingProdedure.
     * @param samplingProdedure New value of property samplingProdedure.
     */
    public void setSamplingProcedure(String samplingProcedure) {
        this.samplingProcedure = samplingProcedure;
    }
    
    /**
     * Holds value of property deviationsFromSampleDesign.
     */
    @Column(columnDefinition="TEXT")
    private String deviationsFromSampleDesign;
    
    /**
     * Getter for property deviationsFromSampleDesign.
     * @return Value of property deviationsFromSampleDesign.
     */
    public String getDeviationsFromSampleDesign() {
        return this.deviationsFromSampleDesign;
    }
    
    /**
     * Setter for property deviationsFromSampleDesign.
     * @param deviationsFromSampleDesign New value of property deviationsFromSampleDesign.
     */
    public void setDeviationsFromSampleDesign(String deviationsFromSampleDesign) {
        this.deviationsFromSampleDesign = deviationsFromSampleDesign;
    }
    
    /**
     * Holds value of property collectionMode.
     */
    @Column(columnDefinition="TEXT")
    private String collectionMode;
    
    /**
     * Getter for property collectionMode.
     * @return Value of property collectionMode.
     */
    public String getCollectionMode() {
        return this.collectionMode;
    }
    
    /**
     * Setter for property collectionMode.
     * @param collectionMode New value of property collectionMode.
     */
    public void setCollectionMode(String collectionMode) {
        this.collectionMode = collectionMode;
    }
    
    /**
     * Holds value of property researchInstrument.
     */
    @Column(columnDefinition="TEXT")
    private String researchInstrument;
    
    /**
     * Getter for property researchInstrument.
     * @return Value of property researchInstrument.
     */
    public String getResearchInstrument() {
        return this.researchInstrument;
    }
    
    /**
     * Setter for property researchInstrument.
     * @param researchInstrument New value of property researchInstrument.
     */
    public void setResearchInstrument(String researchInstrument) {
        this.researchInstrument = researchInstrument;
    }
    
    /**
     * Holds value of property dataSources.
     */
    @Column(columnDefinition="TEXT")
    private String dataSources;
    
    /**
     * Getter for property dataSources.
     * @return Value of property dataSources.
     */
    public String getDataSources() {
        return this.dataSources;
    }
    
    /**
     * Setter for property dataSources.
     * @param dataSources New value of property dataSources.
     */
    public void setDataSources(String dataSources) {
        this.dataSources = dataSources;
    }
    
    /**
     * Holds value of property originOfSources.
     */
    @Column(columnDefinition="TEXT")
    private String originOfSources;
    
    /**
     * Getter for property originOfSources.
     * @return Value of property originOfSources.
     */
    public String getOriginOfSources() {
        return this.originOfSources;
    }
    
    /**
     * Setter for property originOfSources.
     * @param originOfSources New value of property originOfSources.
     */
    public void setOriginOfSources(String originOfSources) {
        this.originOfSources = originOfSources;
    }
    
    /**
     * Holds value of property characteristicOfSources.
     */
    @Column(columnDefinition="TEXT")
    private String characteristicOfSources;
    
    /**
     * Getter for property characteristicOfSources.
     * @return Value of property characteristicOfSources.
     */
    public String getCharacteristicOfSources() {
        return this.characteristicOfSources;
    }
    
    /**
     * Setter for property characteristicOfSources.
     * @param characteristicOfSources New value of property characteristicOfSources.
     */
    public void setCharacteristicOfSources(String characteristicOfSources) {
        this.characteristicOfSources = characteristicOfSources;
    }
    
    /**
     * Holds value of property accessToSources.
     */
    @Column(columnDefinition="TEXT")
    private String accessToSources;
    
    /**
     * Getter for property accessToSources.
     * @return Value of property accessToSources.
     */
    public String getAccessToSources() {
        return this.accessToSources;
    }
    
    /**
     * Setter for property accessToSources.
     * @param accessToSources New value of property accessToSources.
     */
    public void setAccessToSources(String accessToSources) {
        this.accessToSources = accessToSources;
    }
    
    /**
     * Holds value of property dataCollectionSituation.
     */
    @Column(columnDefinition="TEXT")
    private String dataCollectionSituation;
    
    /**
     * Getter for property dataCollectionSituation.
     * @return Value of property dataCollectionSituation.
     */
    public String getDataCollectionSituation() {
        return this.dataCollectionSituation;
    }
    
    /**
     * Setter for property dataCollectionSituation.
     * @param dataCollectionSituation New value of property dataCollectionSituation.
     */
    public void setDataCollectionSituation(String dataCollectionSituation) {
        this.dataCollectionSituation = dataCollectionSituation;
    }
    
    /**
     * Holds value of property actionsToMinimizeLoss.
     */
    @Column(columnDefinition="TEXT")
    private String actionsToMinimizeLoss;
    
    /**
     * Getter for property actionsToMinimizeLoss.
     * @return Value of property actionsToMinimizeLoss.
     */
    public String getActionsToMinimizeLoss() {
        return this.actionsToMinimizeLoss;
    }
    
    /**
     * Setter for property actionsToMinimizeLoss.
     * @param actionsToMinimizeLoss New value of property actionsToMinimizeLoss.
     */
    public void setActionsToMinimizeLoss(String actionsToMinimizeLoss) {
        this.actionsToMinimizeLoss = actionsToMinimizeLoss;
    }
    
    /**
     * Holds value of property controlOperations.
     */
    @Column(columnDefinition="TEXT")
    private String controlOperations;
    
    
    /**
     * Getter for property controlOperations.
     * @return Value of property controlOperations.
     */
    public String getControlOperations() {
        return this.controlOperations;
    }
    
    /**
     * Setter for property controlOperations.
     * @param controlOperations New value of property controlOperations.
     */
    public void setControlOperations(String controlOperations) {
        this.controlOperations = controlOperations;
    }
    
    /**
     * Holds value of property weighting.
     */
    @Column(columnDefinition="TEXT")
    private String weighting;
    
    /**
     * Getter for property weighting.
     * @return Value of property weighting.
     */
    public String getWeighting() {
        return this.weighting;
    }
    
    /**
     * Setter for property weighting.
     * @param weighting New value of property weighting.
     */
    public void setWeighting(String weighting) {
        this.weighting = weighting;
    }
    
    /**
     * Holds value of property cleaningOperations.
     */
    @Column(columnDefinition="TEXT")
    private String cleaningOperations;
    
    /**
     * Getter for property cleaningOperations.
     * @return Value of property cleaningOperations.
     */
    public String getCleaningOperations() {
        return this.cleaningOperations;
    }
    
    /**
     * Setter for property cleaningOperations.
     * @param cleaningOperations New value of property cleaningOperations.
     */
    public void setCleaningOperations(String cleaningOperations) {
        this.cleaningOperations = cleaningOperations;
    }
    
    /**
     * Holds value of property studyLevelErrorNotes.
     */
    @Column(columnDefinition="TEXT")
    private String studyLevelErrorNotes;
    
    /**
     * Getter for property studyLevelErrorNotes.
     * @return Value of property studyLevelErrorNotes.
     */
    public String getStudyLevelErrorNotes() {
        return this.studyLevelErrorNotes;
    }
    
    /**
     * Setter for property studyLevelErrorNotes.
     * @param studyLevelErrorNotes New value of property studyLevelErrorNotes.
     */
    public void setStudyLevelErrorNotes(String studyLevelErrorNotes) {
        this.studyLevelErrorNotes = studyLevelErrorNotes;
    }
    
    /**
     * Holds value of property responseRate.
     */
    @Column(columnDefinition="TEXT")
    private String responseRate;
    
    /**
     * Getter for property responseRate.
     * @return Value of property responseRate.
     */
    public String getResponseRate() {
        return this.responseRate;
    }
    
    /**
     * Setter for property responseRate.
     * @param responseRate New value of property responseRate.
     */
    public void setResponseRate(String responseRate) {
        this.responseRate = responseRate;
    }
    
    /**
     * Holds value of property samplingErrorEstimate.
     */
    @Column(columnDefinition="TEXT")
    private String samplingErrorEstimate;
    
    /**
     * Getter for property samplingErrorEstimate.
     * @return Value of property samplingErrorEstimate.
     */
    public String getSamplingErrorEstimate() {
        return this.samplingErrorEstimate;
    }
    
    /**
     * Setter for property samplingErrorEstimate.
     * @param samplingErrorEstimate New value of property samplingErrorEstimate.
     */
    public void setSamplingErrorEstimate(String samplingErrorEstimate) {
        this.samplingErrorEstimate = samplingErrorEstimate;
    }
    
    /**
     * Holds value of property otherDataAppraisal.
     */
    @Column(columnDefinition="TEXT")
    private String otherDataAppraisal;
    
    /**
     * Getter for property dataAppraisal.
     * @return Value of property dataAppraisal.
     */
    public String getOtherDataAppraisal() {
        return this.otherDataAppraisal;
    }
    
    /**
     * Setter for property dataAppraisal.
     * @param dataAppraisal New value of property dataAppraisal.
     */
    public void setOtherDataAppraisal(String otherDataAppraisal) {
        this.otherDataAppraisal = otherDataAppraisal;
    }
    
    /**
     * Holds value of property placeOfAccess.
     */
    @Column(columnDefinition="TEXT")
    private String placeOfAccess;
    
    /**
     * Getter for property placeOfAccess.
     * @return Value of property placeOfAccess.
     */
    public String getPlaceOfAccess() {
        return this.placeOfAccess;
    }
    
    /**
     * Setter for property placeOfAccess.
     * @param placeOfAccess New value of property placeOfAccess.
     */
    public void setPlaceOfAccess(String placeOfAccess) {
        this.placeOfAccess = placeOfAccess;
    }
    
    /**
     * Holds value of property originalArchive.
     */
    @Column(columnDefinition="TEXT")
    private String originalArchive;
    
    /**
     * Getter for property originalArchive.
     * @return Value of property originalArchive.
     */
    public String getOriginalArchive() {
        return this.originalArchive;
    }
    
    /**
     * Setter for property originalArchive.
     * @param originalArchive New value of property originalArchive.
     */
    public void setOriginalArchive(String originalArchive) {
        this.originalArchive = originalArchive;
    }
    
    /**
     * Holds value of property availabilityStatus.
     */
    @Column(columnDefinition="TEXT")
    private String availabilityStatus;
    
    /**
     * Getter for property availabilityStatus.
     * @return Value of property availabilityStatus.
     */
    public String getAvailabilityStatus() {
        return this.availabilityStatus;
    }
    
    /**
     * Setter for property availabilityStatus.
     * @param availabilityStatus New value of property availabilityStatus.
     */
    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
    
    /**
     * Holds value of property collectionSize.
     */
    @Column(columnDefinition="TEXT")
    private String collectionSize;
    
    /**
     * Getter for property collectionSize.
     * @return Value of property collectionSize.
     */
    public String getCollectionSize() {
        return this.collectionSize;
    }
    
    /**
     * Setter for property collectionSize.
     * @param collectionSize New value of property collectionSize.
     */
    public void setCollectionSize(String collectionSize) {
        this.collectionSize = collectionSize;
    }
    
    /**
     * Holds value of property studyCompletion.
     */
    @Column(columnDefinition="TEXT")
    private String studyCompletion;
    
    /**
     * Getter for property studyCompletion.
     * @return Value of property studyCompletion.
     */
    public String getStudyCompletion() {
        return this.studyCompletion;
    }
    
    /**
     * Setter for property studyCompletion.
     * @param studyCompletion New value of property studyCompletion.
     */
    public void setStudyCompletion(String studyCompletion) {
        this.studyCompletion = studyCompletion;
    }
    
   /**
     * Holds value of property restrictions.
     */
    @Column(columnDefinition="TEXT")
    private String specialPermissions;
    
    /**
     * Getter for property specialPermissions.
     * @return Value of property specialPermissions.
     */
    public String getSpecialPermissions() {
        return this.specialPermissions;
    }
    
    /**
     * Setter for property specialPermissions.
     * @param specialPermissions New value of property specialPermissions.
     */
    public void setSpecialPermissions(String specialPermissions) {
        this.specialPermissions = specialPermissions;
    }
    
    /**
     * Holds value of property restrictions.
     */
    @Column(columnDefinition="TEXT")
    private String restrictions;
    
    /**
     * Getter for property restrictions.
     * @return Value of property restrictions.
     */
    public String getRestrictions() {
        return this.restrictions;
    }
    
    /**
     * Setter for property restrictions.
     * @param restrictions New value of property restrictions.
     */
    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }
    
    /**
     * Holds value of property contact.
     */
    @Column(columnDefinition="TEXT")
    private String contact;
    
    /**
     * Getter for property contact.
     * @return Value of property contact.
     */
    public String getContact() {
        return this.contact;
    }
    
    /**
     * Setter for property contact.
     * @param contact New value of property contact.
     */
    public void setContact(String contact) {
        this.contact = contact;
    }
    
    /**
     * Holds value of property citationRequirements.
     */
    @Column(columnDefinition="TEXT")
    private String citationRequirements;
    
    /**
     * Getter for property citationRequirements.
     * @return Value of property citationRequirements.
     */
    public String getCitationRequirements() {
        return this.citationRequirements;
    }
    
    /**
     * Setter for property citationRequirements.
     * @param citationRequirements New value of property citationRequirements.
     */
    public void setCitationRequirements(String citationRequirements) {
        this.citationRequirements = citationRequirements;
    }
    
    /**
     * Holds value of property depositorRequirements.
     */
    @Column(columnDefinition="TEXT")
    private String depositorRequirements;
    
    /**
     * Getter for property dipositorRequirements.
     * @return Value of property dipositorRequirements.
     */
    public String getDepositorRequirements() {
        return this.depositorRequirements;
    }
    
    /**
     * Setter for property dipositorRequirements.
     * @param dipositorRequirements New value of property dipositorRequirements.
     */
    public void setDepositorRequirements(String depositorRequirements) {
        this.depositorRequirements = depositorRequirements;
    }
    
    /**
     * Holds value of property conditions.
     */
    @Column(columnDefinition="TEXT")
    private String conditions;
    
    /**
     * Getter for property conditions.
     * @return Value of property conditions.
     */
    public String getConditions() {
        return this.conditions;
    }
    
    /**
     * Setter for property conditions.
     * @param conditions New value of property conditions.
     */
    public void setConditions(String conditions) {
        this.conditions = conditions;
    }
    
    /**
     * Holds value of property disclaimer.
     */
    @Column(columnDefinition="TEXT")
    private String disclaimer;
    
    /**
     * Getter for property disclaimer.
     * @return Value of property disclaimer.
     */
    public String getDisclaimer() {
        return this.disclaimer;
    }
    
    /**
     * Setter for property disclaimer.
     * @param disclaimer New value of property disclaimer.
     */
    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
    
    /**
     * Holds value of property productionDate.
     */
    @Column(columnDefinition="TEXT")
    private String productionDate;
    
    /**
     * Getter for property productionDate.
     * @return Value of property productionDate.
     */
    public String getProductionDate() {
        return this.productionDate;
    }
    
    /**
     * Setter for property productionDate.
     * @param productionDate New value of property productionDate.
     */
    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }
    
    /**
     * Holds value of property productionPlace.
     */
    @Column(columnDefinition="TEXT")
    private String productionPlace;
    
    /**
     * Getter for property productionPlace.
     * @return Value of property productionPlace.
     */
    public String getProductionPlace() {
        return this.productionPlace;
    }
    
    /**
     * Setter for property productionPlace.
     * @param productionPlace New value of property productionPlace.
     */
    public void setProductionPlace(String productionPlace) {
        this.productionPlace = productionPlace;
    }
    
    /**
     * Holds value of property confidentialityDeclaration.
     */
    @Column(columnDefinition="TEXT")
    private String confidentialityDeclaration;
    
    /**
     * Getter for property confidentialityDeclaration.
     * @return Value of property confidentialityDeclaration.
     */
    public String getConfidentialityDeclaration() {
        return this.confidentialityDeclaration;
    }
    
    /**
     * Setter for property confidentialityDeclaration.
     * @param confidentialityDeclaration New value of property confidentialityDeclaration.
     */
    public void setConfidentialityDeclaration(String confidentialityDeclaration) {
        this.confidentialityDeclaration = confidentialityDeclaration;
    }
    
    /**
     * Holds value of property studyGrants.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyGrant> studyGrants;
    
    /**
     * Getter for property studyGrants.
     * @return Value of property studyGrants.
     */
    public List<StudyGrant> getStudyGrants() {
        return this.studyGrants;
    }
    
    /**
     * Setter for property studyGrants.
     * @param studyGrants New value of property studyGrants.
     */
    public void setStudyGrants(List<StudyGrant> studyGrants) {
        this.studyGrants = studyGrants;
    }
    
    
    /**
     * Holds value of property distributionDate.
     */
    @Column(columnDefinition="TEXT")
    private String distributionDate;
    
    /**
     * Getter for property distributionDate.
     * @return Value of property distributionDate.
     */
    public String getDistributionDate() {
        return this.distributionDate;
    }
    
    /**
     * Setter for property distributionDate.
     * @param distributionDate New value of property distributionDate.
     */
    public void setDistributionDate(String distributionDate) {
        this.distributionDate = distributionDate;
    }
    
    /**
     * Holds value of property distributorContact.
     */
    @Column(columnDefinition="TEXT")
    private String distributorContact;
    
    /**
     * Getter for property distributorContact.
     * @return Value of property distributorContact.
     */
    public String getDistributorContact() {
        return this.distributorContact;
    }
    
    /**
     * Setter for property distributorContact.
     * @param distributorContact New value of property distributorContact.
     */
    public void setDistributorContact(String distributorContact) {
        this.distributorContact = distributorContact;
    }
    
    /**
     * Holds value of property distributorContactAffiliation.
     */
    @Column(columnDefinition="TEXT")
    private String distributorContactAffiliation;
    
    /**
     * Getter for property distributorContactAffiliation.
     * @return Value of property distributorContactAffiliation.
     */
    public String getDistributorContactAffiliation() {
        return this.distributorContactAffiliation;
    }
    
    /**
     * Setter for property distributorContactAffiliation.
     * @param distributorContactAffiliation New value of property distributorContactAffiliation.
     */
    public void setDistributorContactAffiliation(String distributorContactAffiliation) {
        this.distributorContactAffiliation = distributorContactAffiliation;
    }
    
    /**
     * Holds value of property distributorContactEmail.
     */
    @Column(columnDefinition="TEXT")
    private String distributorContactEmail;
    
    /**
     * Getter for property distributorContactEmail.
     * @return Value of property distributorContactEmail.
     */
    public String getDistributorContactEmail() {
        return this.distributorContactEmail;
    }
    
    /**
     * Setter for property distributorContactEmail.
     * @param distributorContactEmail New value of property distributorContactEmail.
     */
    public void setDistributorContactEmail(String distributorContactEmail) {
        this.distributorContactEmail = distributorContactEmail;
    }
    
    /**
     * Holds value of property depositor.
     */
    @Column(columnDefinition="TEXT")
    private String depositor;
    
    /**
     * Getter for property depositor.
     * @return Value of property depositor.
     */
    public String getDepositor() {
        return this.depositor;
    }
    
    /**
     * Setter for property depositor.
     * @param depositor New value of property depositor.
     */
    public void setDepositor(String depositor) {
        this.depositor = depositor;
    }
    
    /**
     * Holds value of property dateOfDeposit.
     */
    @Column(columnDefinition="TEXT")
    private String dateOfDeposit;
    
    /**
     * Getter for property dateOfDeposit.
     * @return Value of property dateOfDeposit.
     */
    public String getDateOfDeposit() {
        return this.dateOfDeposit;
    }
    
    /**
     * Setter for property dateOfDeposit.
     * @param dateOfDeposit New value of property dateOfDeposit.
     */
    public void setDateOfDeposit(String dateOfDeposit) {
        this.dateOfDeposit = dateOfDeposit;
    }
    
    /**
     * Holds value of property studyOtherIds.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyOtherId> studyOtherIds;
    
    /**
     * Getter for property studyOtherIds.
     * @return Value of property studyOtherIds.
     */
    public List<StudyOtherId> getStudyOtherIds() {
        return this.studyOtherIds;
    }
    
    /**
     * Setter for property studyOtherIds.
     * @param studyOtherIds New value of property studyOtherIds.
     */
    public void setStudyOtherIds(List<StudyOtherId> studyOtherIds) {
        this.studyOtherIds = studyOtherIds;
    }
    
  
    
    
    /**
     * Holds value of property versionDate.
     */
    @Column(columnDefinition="TEXT")
    private String versionDate;
    
    /**
     * Getter for property versionDate.
     * @return Value of property versionDate.
     */
    public String getVersionDate() {
        return this.versionDate;
    }
    
    /**
     * Setter for property versionDate.
     * @param versionDate New value of property versionDate.
     */
    public void setVersionDate(String versionDate) {
        this.versionDate = versionDate;
    }
    
 
    
    /**
     * Holds value of property replicationFor.
     */
    @Column(columnDefinition="TEXT")
    private String replicationFor;
    
    /**
     * Getter for property replicationFor.
     * @return Value of property replicationFor.
     */
    
    public String getReplicationFor() {
        return this.replicationFor;
    }
    
    /**
     * Setter for property replicationFor.
     * @param replicationFor New value of property replicationFor.
     */
    public void setReplicationFor(String replicationFor) {
        this.replicationFor = replicationFor;
    }
    
    /**
     * Holds value of property subTitle.
     */
    @Column(columnDefinition="TEXT")
    private String subTitle;
    
    /**
     * Getter for property subTitle.
     * @return Value of property subTitle.
     */
    public String getSubTitle() {
        return this.subTitle;
    }
    
    /**
     * Setter for property subTitle.
     * @param subTitle New value of property subTitle.
     */
    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
    
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private java.util.List<StudyGeoBounding> studyGeoBoundings;
    
    public java.util.List<StudyGeoBounding> getStudyGeoBoundings() {
        return studyGeoBoundings;
    }
    
    public void setStudyGeoBoundings(java.util.List<StudyGeoBounding> studyGeoBoundings) {
        this.studyGeoBoundings = studyGeoBoundings;
    }
    
  
    /**
     * Holds value of property studyOtherRefs.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyOtherRef> studyOtherRefs;
    
    /**
     * Getter for property studyOtherRefs.
     * @return Value of property studyOtherRefs.
     */
    public List<StudyOtherRef> getStudyOtherRefs() {
        return this.studyOtherRefs;
    }
    
    /**
     * Setter for property studyOtherRefs.
     * @param studyOtherRefs New value of property studyOtherRefs.
     */
    public void setStudyOtherRefs(List<StudyOtherRef> studyOtherRefs) {
        this.studyOtherRefs = studyOtherRefs;
    }
    
    /**
     * Holds value of property studyRelMaterials.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyRelMaterial> studyRelMaterials;
    
    /**
     * Getter for property studyRelMaterial.
     * @return Value of property studyRelMaterial.
     */
    public List<StudyRelMaterial> getStudyRelMaterials() {
        return this.studyRelMaterials;
    }
    
    /**
     * Setter for property studyRelMaterial.
     * @param studyRelMaterial New value of property studyRelMaterial.
     */
    public void setStudyRelMaterials(List<StudyRelMaterial> studyRelMaterials) {
        this.studyRelMaterials = studyRelMaterials;
    }
    
    /**
     * Holds value of property studyRelPublications.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyRelPublication> studyRelPublications;
    
    /**
     * Getter for property studyRelPublications.
     * @return Value of property studyRelPublications.
     */
    public List<StudyRelPublication> getStudyRelPublications() {
        return this.studyRelPublications;
    }
    
    /**
     * Setter for property studyRelPublications.
     * @param studyRelPublications New value of property studyRelPublications.
     */
    public void setStudyRelPublications(List<StudyRelPublication> studyRelPublications) {
        this.studyRelPublications = studyRelPublications;
    }
    
    /**
     * Holds value of property studyRelStudies.
     */
    @OneToMany(mappedBy="metadata", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<StudyRelStudy> studyRelStudies;
    
    /**
     * Getter for property studyRelStudies.
     * @return Value of property studyRelStudies.
     */
    public List<StudyRelStudy> getStudyRelStudies() {
        return this.studyRelStudies;
    }
    
    /**
     * Setter for property studyRelStudies.
     * @param studyRelStudies New value of property studyRelStudies.
     */
    public void setStudyRelStudies(List<StudyRelStudy> studyRelStudies) {
        this.studyRelStudies = studyRelStudies;
    }
    
   

  
    
   

     public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Metadata)) {
            return false;
        }
        Metadata other = (Metadata)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    private String harvestIdentifier;
    
    public String getHarvestIdentifier() {
        return harvestIdentifier;
    }

    public void setHarvestIdentifier(String harvestIdentifier) {
        this.harvestIdentifier = harvestIdentifier;
    }   
    
    private String harvestHoldings;

    public String getHarvestHoldings() {
        return harvestHoldings;
    }

    public void setHarvestHoldings(String harvestHoldings) {
        this.harvestHoldings = harvestHoldings;
    }



    @Column(columnDefinition="TEXT")
    private String harvestDVTermsOfUse;

    @Column(columnDefinition="TEXT")
    private String harvestDVNTermsOfUse;

    public String getHarvestDVTermsOfUse() {
        return harvestDVTermsOfUse;
    }

    public void setHarvestDVTermsOfUse(String harvestDVTermsOfUse) {
        this.harvestDVTermsOfUse = harvestDVTermsOfUse;
    }

    public String getHarvestDVNTermsOfUse() {
        return harvestDVNTermsOfUse;
    }

    public void setHarvestDVNTermsOfUse(String harvestDVNTermsOfUse) {
        this.harvestDVNTermsOfUse = harvestDVNTermsOfUse;
    }


}
