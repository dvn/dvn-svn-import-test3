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
import edu.harvard.iq.dvn.ingest.dsb.DSBWrapper;
import edu.harvard.iq.dvn.core.gnrs.GNRSServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJBException;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
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
 * @author skraffmiller
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EditStudyFilesServiceBean implements edu.harvard.iq.dvn.core.study.EditStudyFilesService {
    @EJB StudyServiceLocal studyService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB GNRSServiceLocal gnrsService;

    @PersistenceContext(type = PersistenceContextType.EXTENDED,unitName="VDCNet-ejbPU")
    EntityManager em;
    StudyVersion studyVersion;
    private boolean newStudy=false;
    private List currentFiles = new ArrayList();
    private List newFiles = new ArrayList();

    public void setStudyVersion(Long studyId ) {

        Study study = em.find(Study.class,studyId);
        if (study==null) {
            throw new IllegalArgumentException("Unknown study id: "+studyId);
        }

        studyVersion = study.getEditVersion();

        for (FileMetadata fm: studyVersion.getFileMetadatas()) {
            StudyFileEditBean fileBean = new StudyFileEditBean(fm);
            getCurrentFiles().add(fileBean);

        }


    }

    public void setStudyVersionByGlobalId(String globalId ) {
        Study searchStudy = getStudyByGlobalId(globalId);
        Long studyID = searchStudy.getId();
        Study study = em.find(Study.class,studyID);
        if (study==null) {
            throw new IllegalArgumentException("Unknown study id: "+studyID);
        }

        studyVersion = study.getEditVersion();

        for (FileMetadata fm: studyVersion.getFileMetadatas()) {
            StudyFileEditBean fileBean = new StudyFileEditBean(fm);
            getCurrentFiles().add(fileBean);

        }


    }


    public void cancel(){

    }

    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void save(Long vdcId, Long userId){
        VDCUser user = em.find(VDCUser.class,userId);
        try {

            editFiles();

            studyService.saveStudyVersion(studyVersion, userId);

            // if new, register the handle
            if ( isNewStudy() && vdcNetworkService.find().isHandleRegistration() ) {
                String handle = studyVersion.getStudy().getAuthority() + "/" + studyVersion.getStudy().getStudyId();
                gnrsService.createHandle(handle);

            }


            em.flush(); // Always call flush(), so that we can detect an OptimisticLockException


        } catch(EJBException e) {
            System.out.println("EJBException "+e.getMessage()+" saving studyVersion "+studyVersion.getId()+" edited by " + user.getUserName() + " at "+ new Date().toString());
            e.printStackTrace();
            throw e;

        }
    }

    public StudyVersion getStudyVersion(){
         return studyVersion;
    }

    public List getCurrentFiles(){
        return currentFiles;
    }

    public void setCurrentFiles(List currentFiles){
         this.currentFiles = currentFiles;
    }

    public List getNewFiles(){
        return newFiles;
    }

    public void setNewFiles(List newFiles){
         this.newFiles = newFiles;
    }


    private HashMap studyMap;
    public HashMap getStudyMap(){
        return studyMap;
    }

    public void setStudyMap(HashMap studyMap){
        this.studyMap = studyMap;
    }

    public void setIngestEmail(String ingestEmail){

    }

    public boolean isNewStudy(){
        return newStudy; 
    }


    private void editFiles() {
        boolean recalculateStudyUNF = false;
        List filesToBeDeleted = new ArrayList();
        em.flush();
        Iterator iter = currentFiles.iterator();
        while (iter.hasNext()) {
            StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();
            StudyFile f = em.find(StudyFile.class,fileBean.getStudyFile().getId());
            if (fileBean.isDeleteFlag()) {

                recalculateStudyUNF = f.isUNFable() ? true : recalculateStudyUNF;
                // If there is only one study version that points to the file,
                // delete the file metadata and the file.
                // Else, just delete the file metadata.
                if (f.getFileMetadatas().size()==1) {
                    f.getAllowedGroups().clear();
                    f.getAllowedUsers().clear();
                    filesToBeDeleted.add(f);
                }
                studyVersion.getFileMetadatas().remove(fileBean.getFileMetadata());
                em.remove(fileBean.getFileMetadata());
            }
        }


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

    public Study getStudyByGlobalId(String globalId) {
        return studyService.getStudyByGlobalId(globalId);
    }

    public void newStudy(Long vdcId, Long userId, Long templateId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}
