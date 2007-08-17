
package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.ejb.Local;
import javax.xml.bind.JAXBException;

/**
 * This is the business interface for NewSession enterprise bean.
 */
@Local
public interface StudyServiceLocal {
   
    /**
     * Add given Study to persistent storage.
     */
    
    public void exportStudyFiles(String lastUpdateTime, String authority) throws IOException, JAXBException;
    public void addStudy(Study study);
        
    public Study getStudy(Long studyId);
    
    public Study getStudyByGlobalId(String globalId);
   
    public Study getStudyByHarvestInfo(String authority, String harvestIdentifier);
   
    public Study getStudyDetail(Long studyId);
    
    public void updateStudy(Study study);
    
    public void deleteStudy(Long studyId) ;
   
    public List getStudies();
        
    java.util.List getStudies(List studyIdList, String orderBy);

    edu.harvard.hmdc.vdcnet.study.StudyFile getStudyFile(Long fileId);

    edu.harvard.hmdc.vdcnet.study.FileCategory getFileCategory(Long fileCategoryId);

    java.util.List getRecentStudies(Long vdcId, int numResults);

    java.util.List <Study> getContributorStudies(VDCUser contributor);

    java.util.List<edu.harvard.hmdc.vdcnet.study.Study> getReviewerStudies(Long vdcId);
   java.util.List<edu.harvard.hmdc.vdcnet.study.Study> getNewStudies(Long vdcId);

    void incrementNumberOfDownloads(Long studyId);
    
    List<DataFileFormatType> getDataFileFormatTypes();

    void addIngestedFiles(Long studyId, List fileBeans, Long userId);
    
    java.util.List<FileCategory> getOrderedFileCategories(Long studyId);
    
    java.util.List<StudyFile> getOrderedFilesByCategory(Long fileCategoryId);

    java.util.List<StudyFile> getOrderedFilesByStudy(Long studyId);

    String generateStudyIdSequence(String protocol, String authority);
    
    boolean isUniqueStudyId(String  userStudyId,String protocol,String authority);

    java.lang.String generateFileSystemNameSequence();

    boolean isUniqueFileSystemName(String fileSystemName);

    void removeStudyLock(Study study);

    void addStudyLock(Study study, VDCUser user, String detail);

    void deleteDataVariables(Long dataTableId);

    edu.harvard.hmdc.vdcnet.study.Study saveStudy(Study study, Long userId);

    edu.harvard.hmdc.vdcnet.study.Study importLegacyStudy(File xmlFile, Long vdcId, Long userId);
    edu.harvard.hmdc.vdcnet.study.Study importHarvestStudy(File xmlFile, Long vdcId, Long userId, String harvestIdentifier);
    edu.harvard.hmdc.vdcnet.study.Study importStudy(File xmlFile,  int xmFileFormatId, Long vdcId, Long userId, boolean registerHandle, boolean generateHandle, boolean allowUpdates, boolean checkRestrictions, boolean retrieveFiles, String harvestIdentifier);

    edu.harvard.hmdc.vdcnet.study.Study doImportStudy(File xmlFile, int xmFileFormatId, Long vdcId, Long userId, boolean registerHandle, boolean generateHandle, boolean allowUpdates, boolean checkRestrictions, boolean retrieveFiles, String harvestIdentifier);

    List getVisibleStudies(List studyIds, Long vdcId);
    List getViewableStudies(List<Long> studyIds, Long userId, Long ipUserGroupId);

   
}
