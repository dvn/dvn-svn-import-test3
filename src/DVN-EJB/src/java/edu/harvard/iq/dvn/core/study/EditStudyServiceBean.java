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
 * EditStudyServiceBean.java
 *
 * Created on September 29, 2006, 1:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.ingest.dsb.DSBWrapper;
import edu.harvard.iq.dvn.core.gnrs.GNRSServiceLocal;
import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion.VersionState;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EditStudyServiceBean implements edu.harvard.iq.dvn.core.study.EditStudyService, java.io.Serializable {
    @EJB IndexServiceLocal indexService;
    @EJB MailServiceLocal mailService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB StudyServiceLocal studyService;
    @EJB StudyFileServiceLocal studyFileService;
    @EJB GNRSServiceLocal gnrsService;
    
    @PersistenceContext(type = PersistenceContextType.EXTENDED,unitName="VDCNet-ejbPU")
    EntityManager em;
    @Resource(mappedName="jms/DSBIngest") Queue queue;
    @Resource(mappedName="jms/DSBQueueConnectionFactory") QueueConnectionFactory factory;
    StudyVersion studyVersion;
    private boolean newStudy=false;
    private List currentFiles = new ArrayList();
    private List newFiles = new ArrayList();
    private String ingestEmail;
    
    /**
     *  Initialize the bean with a studyVersion for editing
     */
    public void setStudyVersion(Long studyId ) {
        Study study = em.find(Study.class,studyId);
        if (study==null) {
            throw new IllegalArgumentException("Unknown study id: "+studyId);
        }
        StudyVersion latestVersion = study.getLatestVersion();
         if (latestVersion.isReleased())
            // if the latest version is released, create a new version for editing
            studyVersion = createNewStudyVersion(study,latestVersion);
        else if (latestVersion.isWorkingCopy()){
            // else, edit existing working copy
            studyVersion = latestVersion;
        } else {
            // if latest version is archived, we can't edit
            throw new IllegalArgumentException("Cannot edit deaccessioned study: "+studyId);
        }

        // now set the files
        /* TODO: VERSION:
        for (Iterator fileIt = studyFileService.getOrderedFilesByStudy(study.getId()).iterator(); fileIt.hasNext();) {
            StudyFile sf = (StudyFile) fileIt.next();
            StudyFileEditBean fileBean = new StudyFileEditBean(em.find(StudyFile.class,sf.getId()));
            fileBean.setFileCategoryName(sf.getFileCategory().getName());
            getCurrentFiles().add(fileBean);

        }
        */
   
    }
    
    private StudyVersion createNewStudyVersion(Study study, StudyVersion latestVersion) {
        StudyVersion sv = new StudyVersion();
        sv.setVersionState(VersionState.DRAFT);
        latestVersion.getMetadata().copyMetadata(sv.getMetadata());
        sv.setVersionNumber(latestVersion.getVersionNumber()+1);
        study.getStudyVersions().add(sv);
        sv.setStudy(study);
        return sv;
    }


    public void newStudy(Long vdcId, Long userId, Long templateId) {
        newStudy=true;
        
        VDC vdc = em.find(VDC.class, vdcId);
        VDCUser creator = em.find(VDCUser.class,userId);
       
      
        
        Study study = new Study(vdc, creator, StudyVersion.VersionState.DRAFT,  em.find(Template.class,templateId));
        em.persist(study);
        
        // set default protocol and authority
        VDCNetwork vdcNetwork = vdcNetworkService.find();
        study.setProtocol(vdcNetwork.getProtocol());
        study.setAuthority(vdcNetwork.getAuthority());

        studyVersion = study.getLatestVersion();
        
    }
    
    public void removeCollectionElement(Collection coll, Object elem) {
        coll.remove(elem);
        em.remove(elem);
    }
     public void removeCollectionElement(List list,int index) {
        System.out.println("index is "+index+", list size is "+list.size());
        em.remove(list.get(index));
        list.remove(index);
    }  
    public void removeCollectionElement(Iterator iter, Object elem) {
        iter.remove();
        em.remove(elem);
    }
    
    public  Study getStudy() {
        return studyVersion.getStudy();
    }
    
    
    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteStudy() {
        // TODO: VERSION:  check if this method is used anywhere
        studyService.deleteStudy(studyVersion.getStudy().getId());
        
    }
    
    private HashMap studyMap;
    public HashMap getStudyMap() {
        return studyMap;
    }
    
    public void setStudyMap(HashMap studyMap) {
        this.studyMap=studyMap;
    }
    
    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void save(Long vdcId, Long userId) {
        VDCUser user = em.find(VDCUser.class,userId);
        try {
            // If user is a Contributor and is editing a study with review state "Released", then
            // revert the review state back to In Review.

         // TODO: VERSION: review this commented logic
        //    if (!isNewStudy()
        //    && user.getVDCRole(study.getOwner())!=null
        //            && user.getVDCRole(study.getOwner()).getRole().getName().equals(RoleServiceLocal.CONTRIBUTOR)
        //            && study.getReviewState().getName().equals(ReviewStateServiceLocal.REVIEW_STATE_RELEASED)) {
        //        study.setReviewState(reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_IN_REVIEW));
        //    }
            
            
            // otherwise we are coming from edit; check current files for changes    
            editFiles();
            studyVersion.updateVersionContributors(user);


            studyService.saveStudy(studyVersion.getStudy(), userId);
            
            // if new, register the handle
            if ( isNewStudy() && vdcNetworkService.find().isHandleRegistration() ) {
              
                String handle = studyVersion.getStudy().getAuthority() + "/" + studyVersion.getStudy().getStudyId();
                gnrsService.createHandle(handle);
               
            }
            
            if (studyVersion.getId() == null) {
                // we need to flush to get the id for the indexer
                em.flush();
            }
            em.flush(); // Always call flush(), so that we can detect an OptimisticLockException
            indexService.updateStudy(studyVersion.getStudy().getId());
           
        } catch(EJBException e) {
            System.out.println("EJBException "+e.getMessage()+" saving studyVersion "+studyVersion.getId()+" edited by " + user.getUserName() + " at "+ new Date().toString());
            e.printStackTrace();
            throw e;
        
        }
            
      
    }
    
    
    /**
     * Remove this Stateful Session bean from the EJB Container without
     * saving updates to the database.
     */
    @Remove
    public void cancel() {
        
    }
    
    /*
    private void addFiles(VDCUser user)  {
        // step 0: start with some initialization
        File newDir = new File(FileUtil.getStudyFileDir(), study.getAuthority() + File.separator + study.getStudyId());
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
        
        // step 1: divide the files, based on subsettable or not
        List subsettableFiles = new ArrayList();
        List otherFiles = new ArrayList();
        
        Iterator iter = newFiles.iterator();
        while (iter.hasNext()) {
            StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();
            if ( fileBean.getStudyFile().isSubsettable() ) {
                subsettableFiles.add(fileBean);
            } else {
                otherFiles.add(fileBean);
                // also add to category, so that it will be flushed for the ids
                addFileToCategory(fileBean.getStudyFile(), fileBean.getFileCategoryName(), study);
            }
        }
        
        // step 2: iterate through nonsubsettable files, moving from temp to new location
        iter = otherFiles.iterator();
        while (iter.hasNext()) {
            StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();
            StudyFile f = fileBean.getStudyFile();
            File tempFile = new File(fileBean.getTempSystemFileLocation());
            File newLocationFile = new File(newDir, f.getFileSystemName());
            try {
                FileUtil.copyFile( tempFile, newLocationFile );
                tempFile.delete();
                f.setFileSystemLocation(newLocationFile.getAbsolutePath());
            } catch (IOException ex) {
                throw new EJBException(ex);
            }
        }
        
        // step 3: iterate through subsettable files, sending a message via JMS
        if (subsettableFiles.size() > 0) {
            QueueConnection conn = null;
            QueueSession session = null;
            QueueSender sender = null;
            try {
                conn = factory.createQueueConnection();
                session = conn.createQueueSession(false,0);
                sender = session.createSender(queue);
                
                DSBIngestMessage ingestMessage = new DSBIngestMessage();
                ingestMessage.setFileBeans(subsettableFiles);
                ingestMessage.setIngestEmail(ingestEmail);
                ingestMessage.setIngestUserId(user.getId());
                ingestMessage.setStudyId(study.getId());
                Message message = session.createObjectMessage(ingestMessage);
                
                String detail = "Ingest processing for " + subsettableFiles.size() + " file(s).";
                studyService.addStudyLock(study.getId(), user.getId(), detail);
                try {
                    sender.send(message);
                } catch(Exception ex) {
                    // If anything goes wrong, remove the study lock.
                    studyService.removeStudyLock(study.getId());
                    ex.printStackTrace();
                }
                 
                  
                // send an e-mail
                mailService.sendIngestRequestedNotification(ingestEmail, subsettableFiles);
                
                
            } catch (JMSException ex) {
                ex.printStackTrace();
            } finally {
                try {
              
                    if (sender != null) {sender.close();}
                    if (session != null) {session.close();}
                    if (conn != null) {conn.close();}
                } catch (JMSException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    */
    
    private void editFiles() {
        boolean recalculateStudyUNF = false;
        List filesToBeDeleted = new ArrayList();
        
        Iterator iter = currentFiles.iterator();
        while (iter.hasNext()) {
            StudyFileEditBean file = (StudyFileEditBean) iter.next();
            StudyFile f = em.find(StudyFile.class,file.getStudyFile().getId());
            if (file.isDeleteFlag()) {
                f.getAllowedGroups().clear();
                f.getAllowedUsers().clear();
                //removeCollectionElement(f.getFileCategory().getStudyFiles(),f);
                recalculateStudyUNF = f.isUNFable() ? true : recalculateStudyUNF;
                filesToBeDeleted.add(f);
                // TODO: VERSION
            } /*else {
                if (!f.getFileCategory().getName().equals(file.getFileCategoryName()) ) {
                    // move to new cat
                    f.getFileCategory().getStudyFiles().remove(f);
                    addFileToCategory(file.getStudyFile(), file.getFileCategoryName(), study);
                }
            }*/
        }

        // persist new categories and flush
        // this is done here, because, since a study file could change categories, we need the new category to get persisted
        // before the old categoy gets deleted (otherwise the persistence layer will attempt to set the category_id on the study file
        // to null when the delete happens throwing a SQL not null exception)
        for (FileCategory cat : studyVersion.getStudy().getFileCategories()) {
            if (cat.getId() == null) {
                em.persist(cat);
            }
        }
        em.flush();
        
        
        // and recalculate study UNF, if needed
        if (recalculateStudyUNF) {
            try {
                studyVersion.getMetadata().setUNF( new DSBWrapper().calculateUNF(studyVersion) );
            } catch (IOException e) {
                throw new EJBException("Could not calculate new study UNF");
            }
        }
        
        // finally delete the physical files
        Iterator tbdIter = filesToBeDeleted.iterator();
        while (tbdIter.hasNext()) {
            StudyFile f = (StudyFile) tbdIter.next();
            File physicalFile = new File(f.getFileSystemLocation());

            if ( f.isSubsettable() ) {
		// preserved original datafile, if available:
                //File originalPhysicalFile = new File(physicalFile.getParent(), "_" + f.getId().toString());
		File originalPhysicalFile = new File(physicalFile.getParent(), "_" + f.getFileSystemName());
		if ( originalPhysicalFile.exists() ) {
		    originalPhysicalFile.delete();
		}

               // TODO: Delete DataVariables and related data thru nativeSQL:
             
           
                 
		// and any cached copies of this file in formats other 
		// than tab-delimited: 
		
		for (DataFileFormatType type : studyService.getDataFileFormatTypes()) {
		    File cachedDataFile = new File(f.getFileSystemLocation() + "." + type.getValue());
		    if ( cachedDataFile.exists() ) {
			cachedDataFile.delete();
		    }
		}
            }

	    if ( physicalFile.exists() ) {
		physicalFile.delete();
	    }
        }
    }
    
    /**
     * Creates a new instance of EditStudyServiceBean
     */
    public EditStudyServiceBean() {
    }
    
    public List getCurrentFiles() {
        return currentFiles;
    }
    
    public void setCurrentFiles(List currentFiles) {
        this.currentFiles = currentFiles;
    }
    
    public List getNewFiles() {
        return newFiles;
    }
    
    public void setNewFiles(List newFiles) {
        this.newFiles = newFiles;
    }
    
    
   
    
    
    public String getIngestEmail() {
        return ingestEmail;
    }
    
    public void setIngestEmail(String ingestEmail) {
        this.ingestEmail = ingestEmail;
    }
    
    public boolean isNewStudy() {
        return newStudy;
    }
    
    public void changeTemplate(Long templateId) {
        Template newTemplate = em.find(Template.class, templateId);
        // Clear existing metadata from study version
     
        clearCollection(studyVersion.getMetadata().getStudyAbstracts());
        clearCollection(studyVersion.getMetadata().getStudyAuthors());
        clearCollection(studyVersion.getMetadata().getStudyDistributors());
        clearCollection(studyVersion.getMetadata().getStudyGeoBoundings());
        clearCollection(studyVersion.getMetadata().getStudyGrants());
        clearCollection(studyVersion.getMetadata().getStudyKeywords());
        clearCollection(studyVersion.getMetadata().getStudyNotes());
        clearCollection(studyVersion.getMetadata().getStudyOtherIds());
        clearCollection(studyVersion.getMetadata().getStudyOtherRefs());
        clearCollection(studyVersion.getMetadata().getStudyProducers());
        clearCollection(studyVersion.getMetadata().getStudyRelMaterials());
        clearCollection(studyVersion.getMetadata().getStudyRelPublications());
        clearCollection(studyVersion.getMetadata().getStudyRelStudies());
        clearCollection(studyVersion.getMetadata().getStudySoftware());
        clearCollection(studyVersion.getMetadata().getStudyTopicClasses());
        
        // Copy Template Metadata into Study Metadata
        newTemplate.getMetadata().copyMetadata(studyVersion.getMetadata());
        studyVersion.getStudy().setTemplate(newTemplate);

        // prefill date of deposit
        studyVersion.getMetadata().setDateOfDeposit(  new SimpleDateFormat("yyyy-MM-dd").format(studyVersion.getStudy().getCreateTime()) );
    }
    
    private void clearCollection(Collection collection) {
        if (collection!=null) {
            for (Iterator it = collection.iterator(); it.hasNext();) {
                Object elem =  it.next();          
                it.remove();
                em.remove(elem);
            }
        }
    }
    
}

