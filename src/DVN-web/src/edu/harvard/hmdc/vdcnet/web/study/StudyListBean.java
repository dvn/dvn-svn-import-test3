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
 * StudyListBean.java
 *
 * Created on September 12, 2006, 4:21 PM
 *
 */

package edu.harvard.hmdc.vdcnet.web.study;
import edu.harvard.hmdc.vdcnet.study.EditStudyService;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.util.Collection;
import javax.ejb.EJB;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyListBean extends VDCBaseBean  implements java.io.Serializable {
    @EJB StudyServiceLocal studyService;
    @EJB EditStudyService editStudyService;
    @EJB VDCServiceLocal vdcService;
    private javax.faces.component.UIData studyTable;
    
    
    /** Creates a new instance of StudyListBean */
    public StudyListBean() {
    }
    
    /**
     * Holds value of property studies.
     */
    private Collection<Study> studies;
    
    /**
     * Getter for property studies.
     * @return Value of property studies.
     */
    public Collection<Study> getStudies() {
        
        if (this.studies==null) {
            studies = studyService.getStudies();
        }
        System.out.println("Getting studies, "+studies);
        return this.studies;
    }
    
    /**
     * Setter for property studies.
     * @param studies New value of property studies.
     */
    public void setStudies(Collection<Study> studies) {
        this.studies = studies;
    }
    
    public String goToDetails() {
        System.out.println("IN goto details............................");
        beginEditSession();
        return "view";
    }
    
    public String goToUpdate() {
        beginEditSession();
        return "edit";
    }
    public String goToAdd() {
        VDC currentVDC = getVDCRequestBean().getCurrentVDC();
       
        
        editStudyService=resetStudyService();
   //     editStudyService.newStudy(currentVDC.getId());
        return "add";
    }
    
    public String goToDelete() {
        beginEditSession();
        return "success";
    }
    
    private void beginEditSession() {
        
        Long id = getSelectedId();
        // Setup a stateful session bean to handle views/edits to the Study.
        // If a stateful bean currently exists in the session, remove it
        // before adding a new bean for the selected study.
        
       editStudyService = resetStudyService(); 
       editStudyService.setStudy(id);
        
    }
    
    private EditStudyService resetStudyService() {
         EditStudyService currService = getVDCSessionBean().getStudyService();
        if (currService!=null) {
            // Cancel operation also removes bean from EJB container.
            currService.cancel();
        }
        getVDCSessionBean().setStudyService(editStudyService);  
        return editStudyService;
    }
        
    private Long getSelectedId() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        
        UIViewRoot root = facesContext.getViewRoot();
        UIData table = (UIData)root.findComponent("studyListForm").findComponent("dataTable1");
        Study study = (Study) table.getRowData();
        return study.getId();
    }
    
    public javax.faces.component.UIData getStudyTable() {
        return studyTable;
    }
    
    public void setStudyTable(javax.faces.component.UIData studyTable) {
        this.studyTable = studyTable;
        
    }
    
}
