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

import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EditStudyPermissionsServiceBean implements EditStudyPermissionsService, java.io.Serializable  {
    @PersistenceContext(type = PersistenceContextType.EXTENDED,unitName="VDCNet-ejbPU")
    EntityManager em;
    @EJB StudyServiceLocal studyService;
    @EJB StudyFileServiceLocal studyFileService;
    @EJB MailServiceLocal mailService;

    Study study;
    /**
     *  Initialize the bean with a Study for editing
     */
    public void setStudy(Long id ) {
        study = em.find(Study.class,id);
        if (study==null) {
            throw new IllegalArgumentException("Unknown study id: "+id);
        }
        
        
        studyPermissions = new ArrayList<PermissionBean>();
        fileDetails = new ArrayList<FileDetailBean>();
        studyRequests = new ArrayList<StudyRequestBean>();
        
        
        
        for (Iterator it = study.getStudyRequests().iterator(); it.hasNext();) {
            StudyAccessRequest elem = (StudyAccessRequest) it.next();
            studyRequests.add(new  StudyRequestBean(elem));
            
        }
        
        if (study.isRestricted()) {
            setStudyRestriction("Restricted");
        } else {
            setStudyRestriction("Public");
        }
        
        for (Iterator it = study.getAllowedUsers().iterator(); it.hasNext();) {
            VDCUser user = (VDCUser) it.next();
            studyPermissions.add(new PermissionBean(user));
        }
        for (Iterator it2 = study.getAllowedGroups().iterator(); it2.hasNext();) {
            UserGroup group = (UserGroup) it2.next();
            studyPermissions.add(new PermissionBean(group));
        }
        
        // TODO: VERSION
        for (Iterator<FileMetadata>fileIter = studyFileService.getOrderedFilesByStudy(study.getId()).iterator(); fileIter.hasNext();) {
            FileMetadata elem = (FileMetadata)fileIter.next();
            FileDetailBean fd = new FileDetailBean();
            fd.setStudyFile(em.find(StudyFile.class,elem.getStudyFile().getId()));
            fd.setFilePermissions(new ArrayList<PermissionBean>());
            for (Iterator it4 = elem.getStudyFile().getAllowedUsers().iterator(); it4.hasNext();) {
                VDCUser elem2 = (VDCUser) it4.next();
                fd.getFilePermissions().add(new PermissionBean(elem2));
            }
            for (Iterator it5 = elem.getStudyFile().getAllowedGroups().iterator(); it5.hasNext();) {
                UserGroup elem2 = (UserGroup) it5.next();
                fd.getFilePermissions().add(new PermissionBean(elem2));
            }

            fileDetails.add(fd);

        }

        for (Iterator detailIter = fileDetails.iterator(); detailIter.hasNext();) {
            FileDetailBean elem = (FileDetailBean) detailIter.next();
            if (elem.getStudyFile().isRestricted()) {
                elem.setFileRestriction("Restricted");
            } else {
                elem.setFileRestriction("Public");
            }
            
        }
        
    }
    
    
    public void removeCollectionElement(Collection coll, Object elem) {
        coll.remove(elem);
        em.remove(elem);
    }
    
    public void removeCollectionElement(Iterator iter, Object elem) {
        iter.remove();
        em.remove(elem);
    }
    
    public  Study getStudy() {
        return study;
    }
    
    
    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteStudy() {
        for (Iterator<VDCCollection>it = study.getStudyColls().iterator(); it.hasNext();) {
            VDCCollection elem =  it.next();
            elem.getStudies().remove(study);
            
        }
        study.getStudyColls().clear();
        //TODO: Need to delete Files
        em.remove(study);
    }
    
    public void updateRequests(String studyUrl) {
        List removeBeans = new ArrayList();
        for (Iterator<StudyRequestBean> it =  studyRequests.iterator(); it.hasNext();) {
            StudyRequestBean elem = it.next();
            
            if (Boolean.TRUE.equals(elem.getAccept()) ){
                this.addFileUser(elem.getStudyRequest().getVdcUser().getId(),true);
                em.remove(elem.getStudyRequest());
                study.getStudyRequests().remove(elem.getStudyRequest());
                removeBeans.add(elem);
                // TODO: VERSION: change this to use a study version object
                //mailService.sendFileAccessApprovalNotification(elem.getStudyRequest().getVdcUser().getEmail(),study.getTitle(),study.getGlobalId(),studyUrl);
 
            } else if (Boolean.FALSE.equals(elem.getAccept()) ){
                em.remove(elem.getStudyRequest());
                study.getStudyRequests().remove(elem.getStudyRequest());
                // TODO: VERSION: change this to use a study version object
                //mailService.sendFileAccessRejectNotification(elem.getStudyRequest().getVdcUser().getEmail(),study.getTitle(),study.getGlobalId(),study.getOwner().getContactEmail());
                removeBeans.add(elem);
            }
            
        }
        for (Iterator it2 = removeBeans.iterator(); it2.hasNext();) {
            StudyRequestBean elem = (StudyRequestBean) it2.next();
            studyRequests.remove(elem);
        }
    }
    
    
    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save() {
        if (this.studyRestriction.equals("Restricted")) {
            study.setRestricted(true);
        } else {
            study.setRestricted(false);
        }
        
        for (Iterator it = fileDetails.iterator(); it.hasNext();) {
            FileDetailBean elem = (FileDetailBean) it.next();
            if (elem.getFileRestriction().equals("Restricted")) {
                elem.getStudyFile().setRestricted(true);
                int users = elem.getStudyFile().getAllowedUsers().size();
            } else {
                elem.getStudyFile().setRestricted(false);
            }
            
        }
        
        // Don't really need to do call flush(), because a method that
        // requires a transaction will automatically trigger a flush to the database,
        // but include this just to show what's happening here
        em.flush();
    }
    
    
    
    /**
     * Remove this Stateful Session bean from the EJB Container without
     * saving updates to the database.
     */
    @Remove
    public void cancel() {
        
    }
    /**
     * Creates a new instance of EditStudyServiceBean
     */
    public EditStudyPermissionsServiceBean() {
    }
    
    /**
     * Holds value of property studyPermissions.
     */
    private Collection<PermissionBean> studyPermissions;
    
    /**
     * Getter for property studyPermissions.
     * @return Value of property studyPermissions.
     */
    public Collection<PermissionBean> getStudyPermissions() {
        return this.studyPermissions;
    }
    
    /**
     * Setter for property studyPermissions.
     * @param studyPermissions New value of property studyPermissions.
     */
    public void setStudyPermissions(Collection<PermissionBean> studyPermissions) {
        this.studyPermissions = studyPermissions;
    }
    
    /**
     * Holds value of property fileDetails.
     */
    private Collection<FileDetailBean> fileDetails;
    
    /**
     * Getter for property filePermissions.
     * @return Value of property filePermissions.
     */
    public Collection<FileDetailBean> getFileDetails() {
        return this.fileDetails;
    }
    
    /**
     * Setter for property filePermissions.
     * @param filePermissions New value of property filePermissions.
     */
    public void setFileDetails(Collection<FileDetailBean> fileDetails) {
        this.fileDetails = fileDetails;
    }
    
    /**
     * Holds value of property studyRestriction.
     */
    private String studyRestriction;
    
    /**
     * Getter for property studyRestriction.
     * @return Value of property studyRestriction.
     */
    public String getStudyRestriction() {
        return this.studyRestriction;
    }
    
    /**
     * Setter for property studyRestriction.
     * @param studyRestriction New value of property studyRestriction.
     */
    public void setStudyRestriction(String studyRestriction) {
        this.studyRestriction = studyRestriction;
    }
    
    /**
     * Holds value of property studyRequests.
     */
    private Collection<StudyRequestBean> studyRequests;
    
    /**
     * Getter for property studyRequests.
     * @return Value of property studyRequests.
     */
    public Collection<StudyRequestBean> getStudyRequests() {
        return this.studyRequests;
    }
    
    /**
     * Setter for property studyRequests.
     * @param studyRequests New value of property studyRequests.
     */
    public void setStudyRequests(Collection<StudyRequestBean> studyRequests) {
        this.studyRequests = studyRequests;
    }
    
    public void addStudyUser(Long userId ) {
        VDCUser user = (VDCUser)em.find(VDCUser.class, userId);
        study.getAllowedUsers().add(user);
        studyPermissions.add(new PermissionBean(user));
    }
    
    public void addStudyGroup(Long groupId ) {
        UserGroup group = (UserGroup)em.find(UserGroup.class, groupId);
        study.getAllowedGroups().add(group);
        studyPermissions.add(new PermissionBean(group));
    }
    
   
    public void addFileUser(Long id) {
        boolean restrictedOnly=false;
        addFileUser(id, restrictedOnly);
    }
    
    public void addFileUser(Long id, boolean restrictedOnly) {
        VDCUser user = (VDCUser)em.find(VDCUser.class, id);
        for (Iterator it = fileDetails.iterator(); it.hasNext();) {
            FileDetailBean elem = (FileDetailBean) it.next();
            if ((!restrictedOnly && elem.isChecked()) || (restrictedOnly && elem.getStudyFile().isRestricted())) {
                StudyFile studyFile = elem.getStudyFile();
                if (!studyFile.isUserInAllowedUsers(user.getId())) {
                    studyFile.getAllowedUsers().add(user);
                    elem.getFilePermissions().add(new PermissionBean(user));
                }
            }
        }
    }
    
    public void addFileGroup(Long id) {
        UserGroup group = (UserGroup)em.find(UserGroup.class, id);
        for (Iterator it = fileDetails.iterator(); it.hasNext();) {
            FileDetailBean elem = (FileDetailBean) it.next();
            if (elem.isChecked()) {
                StudyFile studyFile = elem.getStudyFile();
                if (!studyFile.isGroupInAllowedGroups(id)) {
                    studyFile.getAllowedGroups().add(group);
                    elem.getFilePermissions().add(new PermissionBean(group));
                }
            }
        }
    }
    
    public void setFileRestriction( boolean restricted) {
          for (Iterator it = fileDetails.iterator(); it.hasNext();) {
            FileDetailBean elem = (FileDetailBean) it.next();
            if (elem.isChecked()) {
                StudyFile studyFile = elem.getStudyFile();
                studyFile.setRestricted(restricted);
                if (restricted) {
                    elem.setFileRestriction("Restricted");
                } else {
                    elem.setFileRestriction("Public");
                }
            }
        }
    }
    
    
    
    public void removeFilePermissions() {
        
        for (Iterator it = this.fileDetails.iterator(); it.hasNext();) {
            FileDetailBean fileDetail = (FileDetailBean) it.next();
            List checkedBeans = new ArrayList();
            for (Iterator it2 = fileDetail.getFilePermissions().iterator(); it2.hasNext();) {
                PermissionBean elem = (PermissionBean) it2.next();
                if (elem.isChecked()) {
                    if (elem.getUser()!=null) {
                        VDCUser foundUser = null;
                        // Note: need to do this extra step of finding the user by checking the ID,
                        // because this does not work:
                        // fileDetail.getStudyFile().getAllowedGroups().remove(elem.getUser());
                        // (Some wierdness in EJB where it doesn't think elem.getUser() is equivalent to the user in the list.)
                        for (Iterator userIt = fileDetail.getStudyFile().getAllowedUsers().iterator(); userIt.hasNext();) {
                            VDCUser user = (VDCUser) userIt.next();
                            if (user.getId().equals(elem.getUser().getId())) {
                                foundUser= user;
                            }
                        }
                        if (foundUser!=null) {
                            fileDetail.getStudyFile().getAllowedUsers().remove(foundUser);
                            foundUser.getStudyFiles().remove(fileDetail.getStudyFile());
                        }
                    } else if (elem.getGroup()!=null) {
                        // See above note for removing elem.getUser()
                        UserGroup foundGroup = null;
                        for (Iterator groupIt = fileDetail.getStudyFile().getAllowedGroups().iterator(); groupIt.hasNext();) {
                            UserGroup group = (UserGroup) groupIt.next();
                            if (group.getId().equals(elem.getGroup().getId())) {
                                foundGroup = group;
                            }
                        }
                        if (foundGroup!=null) {
                            fileDetail.getStudyFile().getAllowedGroups().remove(foundGroup);
                            foundGroup.getStudyFiles().remove(fileDetail.getStudyFile());
                        }
                    }
                    checkedBeans.add(elem);
                }
            }
            
            for (Iterator it3 = checkedBeans.iterator(); it3.hasNext();) {
                PermissionBean elem = (PermissionBean)it3.next();
                if (elem.isChecked()) {
                    fileDetail.getFilePermissions().remove(elem);
                }
            }
        }
    }
    
    public void removeStudyPermissions() {
        List checkedBeans = new ArrayList();
        
        for (Iterator<PermissionBean> it = studyPermissions.iterator(); it.hasNext();) {
            PermissionBean elem = it.next();
            if (elem.isChecked()) {
                if (elem.getUser()!=null) {
                    study.getAllowedUsers().remove(elem.getUser());
                    elem.getUser().getStudies().remove(study);
                } else if (elem.getGroup()!=null) {
                    study.getAllowedGroups().remove(elem.getGroup());
                    elem.getGroup().getStudies().remove(study);
                }
                checkedBeans.add(elem);
                
            }
            
        }
        for (Iterator it = checkedBeans.iterator(); it.hasNext();) {
            PermissionBean elem = (PermissionBean) it.next();
            if (elem.isChecked()) {
                studyPermissions.remove(elem);
            }
            
        }
    }
    
    
}
