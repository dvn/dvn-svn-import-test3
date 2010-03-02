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

package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.vdc.VDC;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;
import javax.xml.bind.JAXBException;

/**
 * This is the business interface for NewSession enterprise bean.
 */
@Local
public interface StudyServiceLocal extends java.io.Serializable {
   
    /**
     * Add given Study to persistent storage.
     */

    //   Comments for breaking this into multiple classes
    //
    // 1= Retrieval
    // 2= Update
    // 3= Import/Export
    // 4= File related
    // ?= not sure if this is needed
    // x= no longer needed

    //?
    public void exportStudyFilesToLegacySystem(String lastUpdateTime, String authority) throws IOException, JAXBException;
// comment out - not used    public void addStudy(Study study); 
        
    // 1
    public Study getStudy(Long studyId);

    // 1
    public Study getStudyByGlobalId(String globalId);

    // 1 (new to this version)
    public StudyVersion getStudyVersion(String globalId, Long versionNumber);
    public StudyVersion getStudyVersion(Long studyId, Long versionNumber);
    public StudyVersion getStudyVersionById(Long versionId);

    //1
    public Study getStudyByHarvestInfo(VDC vdc, String harvestIdentifier);

    //1
    public Study getStudyDetail(Long studyId);

    //1
    public Study getStudyForSearch(Long studyId, Map studyFields);

    //2
    public void updateStudy(Study study);
    public void updateStudyVersion(StudyVersion studyVersion);

    //2
    public void deleteStudy(Long studyId);
    
    //2
    public void deleteStudyInNewTransaction(Long studyId, boolean deleteFromIndex);
     
    //2
    public void deleteStudyList(List<Long> studyIds);
   
    //?
    public List getStudies();

    //1
    public List<Long> getAllNonHarvestedStudyIds();

    //1
    java.util.List getOrderedStudies(List studyIdList, String orderBy);

    //?
    java.util.List getRecentStudies(Long vdcId, int numResults);

    //1
    java.util.List <Study> getContributorStudies(VDCUser contributor, VDC vdc);

    //1
    java.util.List<edu.harvard.iq.dvn.core.study.Study> getReviewerStudies(Long vdcId);

    //1
    java.util.List<edu.harvard.iq.dvn.core.study.Study> getNewStudies(Long vdcId);

    //?1
    public List getDvOrderedStudyIds(Long vdcId, String orderBy, boolean ascending );

    //?1
    public List getDvOrderedStudyIdsByCreator(Long vdcId, Long creatorId, String orderBy, boolean ascending );

    //4
    public void incrementNumberOfDownloads(Long studyFileId, Long currentVDCId);

    //4
    public void incrementNumberOfDownloads(Long studyFileId, Long currentVDCId, Date lastDownloadTime);

    //4
    public RemoteAccessAuth lookupRemoteAuthByHost(String remoteHost);

    //4
    public List<DataFileFormatType> getDataFileFormatTypes();

    //4
    void addIngestedFiles(Long studyId, List fileBeans, Long userId);
    

    //2
    String generateStudyIdSequence(String protocol, String authority);
    
    //2
    boolean isUniqueStudyId(String  userStudyId,String protocol,String authority);

    //4
    java.lang.String generateFileSystemNameSequence();

    //4
    boolean isUniqueFileSystemName(String fileSystemName);

    //2
    List<StudyLock> getStudyLocks();
    
    //2
    void removeStudyLock(Long studyId);

    //2
    void addStudyLock(Long studyId, Long userId, String detail);

    //2
    void deleteDataVariables(Long dataTableId);

    //2
    edu.harvard.iq.dvn.core.study.Study saveStudy(Study study, Long userId);

    //3
    edu.harvard.iq.dvn.core.study.Study importHarvestStudy(File xmlFile, Long vdcId, Long userId, String harvestIdentifier);
    
    //3
    edu.harvard.iq.dvn.core.study.Study importStudy(File xmlFile,  Long harvestFormatTypeId, Long vdcId, Long userId);
    
    //3
    edu.harvard.iq.dvn.core.study.Study importStudy(File xmlFile,  Long harvestFormatTypeId, Long vdcId, Long userId, List<StudyFileEditBean> filesToUpload);
    
    //3
    edu.harvard.iq.dvn.core.study.Study importStudy(File xmlFile,  Long harvestFormatTypeId, Long vdcId, Long userId, String harvestIdentifier, List<StudyFileEditBean> filesToUpload);

    //1
    List getVisibleStudies(List studyIds, Long vdcId);

    //1
    List getViewableStudies(List<Long> studyIds);
    
    //1
    List getViewableStudies(List<Long> studyIds, Long userId, Long ipUserGroupId, Long vdcId);

    //3
    List getStudyIdsForExport();

    //?
    public List<Long> getAllStudyIds();

    //3
    public void exportStudy(Long studyId);
    public void exportStudy(Study study);
    public void exportStudyToFormat(Long studyId, String exportFormat);
    public void exportStudyToFormat(Study study, String exportFormat);
    public void exportUpdatedStudies();
    public void exportStudies(List<Long> studyIds);
    public void exportStudies(List<Long> studyIds, String exportFormat);

    //4
    public void addFiles(Study study, List<StudyFileEditBean> newFiles, VDCUser user);
    public void addFiles(Study study, List<StudyFileEditBean> newFiles, VDCUser user, String ingestEmail);

    //2
    public boolean isValidStudyIdString(String studyId);
    public void setIndexTime(Long studyId, Date indexTime);
    public Timestamp getLastUpdatedTime(Long vdcId);
   
    //1
    public long getStudyDownloadCount(Long studyId);
    public Long getActivityCount(Long vdcId);
    public Long getTotalActivityCount();


    //2
    public void setReadyForReview(Long studyId);
    public void setReadyForReview(StudyVersion sv);
    public void saveVersionNote(Long studyId, Long versionNumber, String newVersionNote);
    public void saveVersionNote(Long studyVersionId, String newVersionNote);
    public void setReleased(Long studyId);
    public void setArchived(Long studyVersionId);

}
