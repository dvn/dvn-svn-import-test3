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
 * StudyPage.java
 *
 * Created on September 5, 2006, 4:25 PM
 */
package edu.harvard.hmdc.vdcnet.web.study;


import com.sun.jsfcl.data.DefaultTableDataModel;
import com.sun.rave.web.ui.component.TabSet;
import edu.harvard.hmdc.vdcnet.study.EditStudyService;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyAbstract;
import edu.harvard.hmdc.vdcnet.study.StudyAuthor;
import edu.harvard.hmdc.vdcnet.study.StudyDistributor;
import edu.harvard.hmdc.vdcnet.study.StudyGeoBounding;
import edu.harvard.hmdc.vdcnet.study.StudyGrant;
import edu.harvard.hmdc.vdcnet.study.StudyKeyword;
import edu.harvard.hmdc.vdcnet.study.StudyNote;
import edu.harvard.hmdc.vdcnet.study.StudyOtherId;
import edu.harvard.hmdc.vdcnet.study.StudyOtherRef;
import edu.harvard.hmdc.vdcnet.study.StudyProducer;
import edu.harvard.hmdc.vdcnet.study.StudyRelMaterial;
import edu.harvard.hmdc.vdcnet.study.StudyRelPublication;
import edu.harvard.hmdc.vdcnet.study.StudyRelStudy;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.study.StudySoftware;
import edu.harvard.hmdc.vdcnet.study.StudyTopicClass;
import edu.harvard.hmdc.vdcnet.study.TemplateField;
import edu.harvard.hmdc.vdcnet.study.TemplateFileCategory;
import edu.harvard.hmdc.vdcnet.util.SessionCounter;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */

@EJB(name="editStudy", beanInterface=edu.harvard.hmdc.vdcnet.study.EditStudyService.class)
public class EditStudyPage extends VDCBaseBean implements java.io.Serializable  {
    EditStudyService editStudyService;
    @EJB StudyServiceLocal studyService;
    
    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public EditStudyPage() {
        
    }
    
    
    public void init() {
        super.init();
        // set tab if it was it was sent as pamameter
        if (tab != null) {
            tabSet1.setSelected(tab);
        }
        token = this.getRequestParam("content:editStudyPageView:studyForm:token" );
        if ( token!=null) {
            if ( sessionGet(token)!=null) {
                editStudyService = (EditStudyService) sessionGet(token);
                study = editStudyService.getStudy();
                setFiles(editStudyService.getCurrentFiles());
            } else {
                FacesContext context = FacesContext.getCurrentInstance();
                FacesMessage errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "The Study form you are trying to save contains stale data. Please re-load the study and try again.","");
                context.addMessage(null,errMessage);
           }
        } else {
            
            // we need to create the token and the editStudyService bean
            token=java.util.UUID.randomUUID().toString();
            try {
                Context ctx = new InitialContext();
                editStudyService = (EditStudyService) ctx.lookup("java:comp/env/editStudy");
                sessionPut( token, editStudyService);
            } catch(NamingException e) {
                e.printStackTrace();
                FacesContext context = FacesContext.getCurrentInstance();
                FacesMessage errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),null);
                context.addMessage(null,errMessage);
                
            }
            if (getStudyId() != null) {
                editStudyService.setStudy(studyId);            
                study = editStudyService.getStudy();
                setFiles(editStudyService.getCurrentFiles());
            } else {
                Long vdcId = getVDCRequestBean().getCurrentVDC().getId();
                editStudyService.newStudy(vdcId, getVDCSessionBean().getLoginBean().getUser().getId());
                study = editStudyService.getStudy();
                studyId = SessionCounter.getNext();
               

                 study.setStudyId(studyService.generateStudyIdSequence(study.getProtocol(),study.getAuthority()));
                // prefill date of deposit
                study.setDateOfDeposit(  new SimpleDateFormat("yyyy-MM-dd").format(study.getCreateTime()) );
                setFiles(editStudyService.getCurrentFiles());
            }
            // Add empty first element to subcollections, so the input text fields will be visible
            initStudyMap();
            initCollections();
            
        }
        
        
        
        
    }
    
    private String getStudyIdFromRequest() {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String studyIdParam=request.getParameter("studyId");
        if (studyIdParam==null) {
            Iterator iter = request.getParameterMap().keySet().iterator();
            while (iter.hasNext()) {
                Object key = (Object) iter.next();
                if ( key instanceof String && ((String) key).indexOf("studyId") != -1 ) {
                    studyIdParam = request.getParameter((String)key);
                    break;
                }
            }
        }
        return studyIdParam;
        
    }    
    private void initCollections() {
        if ( study.getStudyOtherIds()==null || study.getStudyOtherIds().size()==0) {
            StudyOtherId elem = new StudyOtherId();
            elem.setStudy(study);
            List otherIds = new ArrayList();
            otherIds.add(elem);
            study.setStudyOtherIds(otherIds);
        }
        if ( study.getStudyAuthors()==null || study.getStudyAuthors().size()==0) {
            List authors = new ArrayList();
            StudyAuthor anAuthor = new StudyAuthor();
            anAuthor.setStudy(study);
            authors.add(anAuthor);
            study.setStudyAuthors(authors);
        }
        
        if ( study.getStudyAbstracts()==null || study.getStudyAbstracts().size()==0) {
            List abstracts = new ArrayList();
            StudyAbstract elem = new StudyAbstract();
            elem.setStudy(study);
            abstracts.add(elem);
            study.setStudyAbstracts(abstracts);
        }
        
        if (study.getStudyDistributors()==null || study.getStudyDistributors().size()==0) {
            List distributors = new ArrayList();
            StudyDistributor elem = new StudyDistributor();
            elem.setStudy(study);
            distributors.add(elem);
            study.setStudyDistributors(distributors);
        }
        if (study.getStudyGrants()==null || study.getStudyGrants().size()==0) {
            List grants = new ArrayList();
            StudyGrant elem = new StudyGrant();
            elem.setStudy(study);
            grants.add(elem);
            study.setStudyGrants(grants);
        }
        
        if (study.getStudyKeywords()==null || study.getStudyKeywords().size()==0 ) {
            List keywords = new ArrayList();
            StudyKeyword elem = new StudyKeyword();
            elem.setStudy(study);
            keywords.add(elem);
            study.setStudyKeywords(keywords);
        }
        
        if (study.getStudyTopicClasses()==null || study.getStudyTopicClasses().size()==0 ) {
            List topicClasses = new ArrayList();
            StudyTopicClass elem = new StudyTopicClass();
            elem.setStudy(study);
            topicClasses.add(elem);
            study.setStudyTopicClasses(topicClasses);
        }
        
        if (study.getStudyNotes()==null || study.getStudyNotes().size()==0) {
            List notes = new ArrayList();
            StudyNote elem = new StudyNote();
            elem.setStudy(study);
            notes.add(elem);
            study.setStudyNotes(notes);
        }
        
        if (study.getStudyProducers()==null || study.getStudyProducers().size()==0) {
            List producers = new ArrayList();
            StudyProducer elem = new StudyProducer();
            elem.setStudy(study);
            producers.add(elem);
            study.setStudyProducers(producers);
        }
        
        if (study.getStudySoftware()==null || study.getStudySoftware().size()==0) {
            List software = new ArrayList();
            StudySoftware elem = new StudySoftware();
            elem.setStudy(study);
            software.add(elem);
            study.setStudySoftware(software);
        }
        if (study.getStudyGeoBoundings()==null || study.getStudyGeoBoundings().size()==0) {
            List boundings = new ArrayList();
            StudyGeoBounding elem = new StudyGeoBounding();
            elem.setStudy(study);
            boundings.add(elem);
            study.setStudyGeoBoundings(boundings);
        }
        if (study.getStudyRelMaterials()==null || study.getStudyRelMaterials().size()==0) {
            List mats = new ArrayList();
            StudyRelMaterial elem = new StudyRelMaterial();
            elem.setStudy(study);
            mats.add(elem);
            study.setStudyRelMaterials(mats);
        }
        if (study.getStudyRelPublications()==null || study.getStudyRelPublications().size()==0) {
            List list = new ArrayList();
            StudyRelPublication elem = new StudyRelPublication();
            elem.setStudy(study);
            list.add(elem);
            study.setStudyRelPublications(list);
        }
        if (study.getStudyRelStudies()==null || study.getStudyRelStudies().size()==0) {
            List list = new ArrayList();
            StudyRelStudy elem = new StudyRelStudy();
            elem.setStudy(study);
            list.add(elem);
            study.setStudyRelStudies(list);
        }
        if (study.getStudyOtherRefs()==null || study.getStudyOtherRefs().size()==0) {
            List list = new ArrayList();
            StudyOtherRef elem = new StudyOtherRef();
            elem.setStudy(study);
            list.add(elem);
            study.setStudyOtherRefs(list);
        }
        
        
    }
    
    /**
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    public void preprocess() {
        System.out.println("in preprocess");
    }
    
    /**
     * <p>Callback method that is called just before rendering takes place.
     * This method will <strong>only</strong> be called for the page that
     * will actually be rendered (and not, for example, on a page that
     * handled a postback and then navigated to a different page).  Customize
     * this method to allocate resources that will be required for rendering
     * this page.</p>
     */
    public void prerender() {
        System.out.println("in prerender");
    }
    
    /**
     * <p>Callback method that is called after rendering is completed for
     * this request, if <code>init()</code> was called (regardless of whether
     * or not this was the page that was actually rendered).  Customize this
     * method to release resources acquired in the <code>init()</code>,
     * <code>preprocess()</code>, or <code>prerender()</code> methods (or
     * acquired during execution of an event handler).</p>
     */
    public void destroy() {
    }
    
    private TabSet tabSet1 = new TabSet();
    
    public TabSet getTabSet1() {
        return tabSet1;
    }
    
    public void setTabSet1(TabSet ts) {
        this.tabSet1 = ts;
    }
    
    public String tab1_action() {
        // TODO: Replace with your code
        
        return null;
    }
    
    
    public String tab2_action() {
        // TODO: Replace with your code
        
        return null;
    }
    
    /**
     * Holds value of property study.
     */
    private Study study;
    
    /**
     * Getter for property study.
     * @return Value of property study.
     */
    public Study getStudy() {
        
        return this.study;
    }
    
    /**
     * Setter for property study.
     * @param study New value of property study.
     */
    public void setStudy(Study study) {
        System.out.println("Set Study is called");
        this.study = study;
    }
    
    private DefaultTableDataModel dataTable5Model = new DefaultTableDataModel();
    
    public DefaultTableDataModel getDataTable5Model() {
        return dataTable5Model;
    }
    
    public void setDataTable5Model(DefaultTableDataModel dtdm) {
        this.dataTable5Model = dtdm;
    }
    
    
    
    
    
    public void initStudyMap() {
        editStudyService.setStudyMap(new HashMap());
        for (Iterator<TemplateField> it = study.getTemplate().getTemplateFields().iterator(); it.hasNext();) {
            TemplateField tf = it.next();
            StudyMapValue smv = new StudyMapValue();
            smv.setTemplateField(tf);
            smv.setRendered(allFieldsShowing || !tf.isOptional());
            editStudyService.getStudyMap().put(tf.getStudyField().getName(),smv);
        }
        
    }
    
    public void updateStudyMap() {
        for (Iterator<StudyMapValue> it = editStudyService.getStudyMap().values().iterator(); it.hasNext();) {
            StudyMapValue smv = it.next();
            boolean rendered = allFieldsShowing || !smv.getTemplateField().isOptional();
            smv.setRendered(rendered);
            
        }
    }
    
    public Map getStudyMap() {
        return editStudyService.getStudyMap();
        
    }
    public boolean isTitleRequired() {
        TemplateField tf = (TemplateField)( editStudyService.getStudyMap().get("title"));
        return tf.isRequired();
        
    }
    public void addRow(ActionEvent ae) {
        
        //      UIComponent dataTable = ae.getComponent().getParent().getParent().getParent();
        HtmlDataTable dataTable = (HtmlDataTable)ae.getComponent().getParent().getParent();
        
        if (dataTable.equals(dataTableOtherIds)) {
            StudyOtherId newElem = new StudyOtherId();
            newElem.setStudy(study);
            study.getStudyOtherIds().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(dataTableAuthors)) {
            StudyAuthor newElem = new StudyAuthor();
            newElem.setStudy(study);
            study.getStudyAuthors().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(dataTableAbstracts)) {
            StudyAbstract newElem = new StudyAbstract();
            newElem.setStudy(study);
            study.getStudyAbstracts().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(dataTableDistributors)) {
            StudyDistributor newElem = new StudyDistributor();
            newElem.setStudy(study);
            study.getStudyDistributors().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(dataTableGrants)) {
            StudyGrant newElem = new StudyGrant();
            newElem.setStudy(study);
            study.getStudyGrants().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(dataTableKeywords)) {
            StudyKeyword newElem = new StudyKeyword();
            newElem.setStudy(study);
            study.getStudyKeywords().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(dataTableNotes)) {
            StudyNote newElem = new StudyNote();
            newElem.setStudy(study);
            study.getStudyNotes().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(dataTableProducers)) {
            StudyProducer newElem = new StudyProducer();
            newElem.setStudy(study);
            study.getStudyProducers().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(dataTableSoftware)) {
            StudySoftware newElem = new StudySoftware();
            newElem.setStudy(study);
            study.getStudySoftware().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(dataTableTopicClass)) {
            StudyTopicClass newElem = new StudyTopicClass();
            newElem.setStudy(study);
            study.getStudyTopicClasses().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(this.dataTableGeoBoundings)) {
            StudyGeoBounding newElem = new StudyGeoBounding();
            newElem.setStudy(study);
            study.getStudyGeoBoundings().add(dataTable.getRowIndex()+1,newElem);
        }  else  if (dataTable.equals(this.dataTableRelPublications)) {
            StudyRelPublication newElem = new StudyRelPublication();
            newElem.setStudy(study);
            study.getStudyRelPublications().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(this.dataTableRelMaterials)) {
            StudyRelMaterial newElem = new StudyRelMaterial();
            newElem.setStudy(study);
            study.getStudyRelMaterials().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(this.dataTableRelStudies)) {
            StudyRelStudy newElem = new StudyRelStudy();
            newElem.setStudy(study);
            study.getStudyRelStudies().add(dataTable.getRowIndex()+1,newElem);
        } else  if (dataTable.equals(this.dataTableOtherReferences)) {
            StudyOtherRef newElem = new StudyOtherRef();
            newElem.setStudy(study);
            study.getStudyOtherRefs().add(dataTable.getRowIndex()+1,newElem);
        }
        
        
        
        
    }
    
    public void removeRow(ActionEvent ae) {
        
        HtmlDataTable dataTable = (HtmlDataTable)ae.getComponent().getParent().getParent();
        if (dataTable.getRowCount()>1) {
            List data = (List)dataTable.getValue();
            editStudyService.removeCollectionElement(data,dataTable.getRowData());
        }
    }
    
    public void toggleInlineHelp(ActionEvent ae) {
        String id =ae.getComponent().getId();
        String studyField = id.substring(5, id.length());
        System.out.println("studyField is"+studyField);
        UIComponent helpText = FacesContext.getCurrentInstance().getViewRoot().findComponent("help_text_"+studyField);
        helpText.setRendered(!helpText.isRendered());
    }
    /**
     * Holds value of property dataTableOtherIds.
     */
    private HtmlDataTable dataTableOtherIds;
    
    /**
     * Getter for property dataTableOtherIds.
     * @return Value of property dataTableOtherIds.
     */
    public HtmlDataTable getDataTableOtherIds() {
        return this.dataTableOtherIds;
    }
    
    /**
     * Setter for property dataTableOtherIds.
     * @param dataTableOtherIds New value of property dataTableOtherIds.
     */
    public void setDataTableOtherIds(HtmlDataTable dataTableOtherIds) {
        this.dataTableOtherIds = dataTableOtherIds;
    }
    
    private void removeEmptyRows() {
        // Remove empty collection rows
        
        // StudyAuthor
        for (Iterator<StudyAuthor> it = study.getStudyAuthors().iterator(); it.hasNext();) {
            StudyAuthor elem =  it.next();
            if (elem.isEmpty()) {
                  editStudyService.removeCollectionElement(it,elem);
            }
        }
        
        // StudyAbstract
        for (Iterator<StudyAbstract> it = study.getStudyAbstracts().iterator(); it.hasNext();) {
            StudyAbstract elem =  it.next();
            if (elem.isEmpty()) {
                   editStudyService.removeCollectionElement(it,elem);
            }
        }
        
        // StudyDistributor
        for (Iterator<StudyDistributor> it = study.getStudyDistributors().iterator(); it.hasNext();) {
            StudyDistributor elem =  it.next();
            if (elem.isEmpty()) {
                   editStudyService.removeCollectionElement(it,elem);
           }
        }
        
        // StudyGrant
        for (Iterator<StudyGrant> it = study.getStudyGrants().iterator(); it.hasNext();) {
            StudyGrant elem =  it.next();
            if (elem.isEmpty()) {
                    editStudyService.removeCollectionElement(it,elem);
           }
        }
        // StudyGeobounding
        for (Iterator<StudyGeoBounding> it = study.getStudyGeoBoundings().iterator(); it.hasNext();) {
            StudyGeoBounding elem =  it.next();
            if (elem.isEmpty()) {
                  editStudyService.removeCollectionElement(it,elem);
            }
        }
        // StudyKeyword
        for (Iterator<StudyKeyword> it = study.getStudyKeywords().iterator(); it.hasNext();) {
            StudyKeyword elem =  it.next();
            if (elem.isEmpty()) {
                   editStudyService.removeCollectionElement(it,elem);
            }
        }
        // StudyNote
        for (Iterator<StudyNote> it = study.getStudyNotes().iterator(); it.hasNext();) {
            StudyNote elem =  it.next();
            if (elem.isEmpty()) {
                  editStudyService.removeCollectionElement(it,elem);
            }
        }
        // StudyOtherId
        for (Iterator<StudyOtherId> it = study.getStudyOtherIds().iterator(); it.hasNext();) {
            StudyOtherId elem =  it.next();
            if (elem.isEmpty()) {
                  editStudyService.removeCollectionElement(it,elem);
            }
        }
        // StudyProducer
        for (Iterator<StudyProducer> it = study.getStudyProducers().iterator(); it.hasNext();) {
            StudyProducer elem =  it.next();
            if ( elem.isEmpty()) {
                  editStudyService.removeCollectionElement(it,elem);
            }
        }
        
        // StudySoftware
        for (Iterator<StudySoftware> it = study.getStudySoftware().iterator(); it.hasNext();) {
            StudySoftware elem =  it.next();
            if (elem.isEmpty()) {
                   editStudyService.removeCollectionElement(it,elem);
           }
        }
        
        // StudyTopicClass
        for (Iterator<StudyTopicClass> it = study.getStudyTopicClasses().iterator(); it.hasNext();) {
            StudyTopicClass elem =  it.next();
            if (elem.isEmpty()) {
                   editStudyService.removeCollectionElement(it,elem);
           }
        }
        // StudyRelMaterial
        for (Iterator<StudyRelMaterial> it = study.getStudyRelMaterials().iterator(); it.hasNext();) {
            StudyRelMaterial elem =  it.next();
            if (elem.isEmpty()) {
                   editStudyService.removeCollectionElement(it,elem);
           }
        }
        // StudyRelPublication
        for (Iterator<StudyRelPublication> it = study.getStudyRelPublications().iterator(); it.hasNext();) {
            StudyRelPublication elem =  it.next();
            if (elem.isEmpty()) {
                   editStudyService.removeCollectionElement(it,elem);
           }
        }
        // StudyRelStudy
        for (Iterator<StudyRelStudy> it = study.getStudyRelStudies().iterator(); it.hasNext();) {
            StudyRelStudy elem =  it.next();
            if (elem.isEmpty()) {
                   editStudyService.removeCollectionElement(it,elem);
           }
        }
        // StudyOtherRef
        for (Iterator<StudyOtherRef> it = study.getStudyOtherRefs().iterator(); it.hasNext();) {
            StudyOtherRef elem =  it.next();
            if (elem.isEmpty()) {
                   editStudyService.removeCollectionElement(it,elem);
           }
        }
        
        
    }
    
    
    
    
    public String cancel() {
        String forwardPage="viewStudy";
        if (study.getId()==null) {
            forwardPage="myOptions";
        }
        editStudyService.cancel();
        this.sessionRemove(token);
        getVDCRequestBean().setStudyId(study.getId());
        getVDCRequestBean().setSelectedTab(tabSet1.getSelected());
        getVDCSessionBean().setStudyService(null);
        
        return  forwardPage;
    }
    
    
    
    /**
     * Holds value of property editMode.
     */
    private String editMode;
    
    /**
     * Getter for property editMode.
     * @return Value of property editMode.
     */
    public String getEditMode() {
        return this.editMode;
    }
    
    /**
     * Setter for property editMode.
     * @param editMode New value of property editMode.
     */
    public void setEditMode(String editMode) {
        this.editMode = editMode;
    }
    
    /**
     * Holds value of property dataTableAuthors.
     */
    private HtmlDataTable dataTableAuthors;
    
    /**
     * Getter for property dataTableAuthors.
     * @return Value of property dataTableAuthors.
     */
    public HtmlDataTable getDataTableAuthors() {
        return this.dataTableAuthors;
    }
    
    /**
     * Setter for property dataTableAuthors.
     * @param dataTableAuthors New value of property dataTableAuthors.
     */
    public void setDataTableAuthors(HtmlDataTable dataTableAuthors) {
        this.dataTableAuthors = dataTableAuthors;
    }
    
    /**
     * Holds value of property dataTableAbstracts.
     */
    private HtmlDataTable dataTableAbstracts;
    
    /**
     * Getter for property dataTableAbstract.
     * @return Value of property dataTableAbstract.
     */
    public HtmlDataTable getDataTableAbstracts() {
        return this.dataTableAbstracts;
    }
    
    /**
     * Setter for property dataTableAbstract.
     * @param dataTableAbstract New value of property dataTableAbstract.
     */
    public void setDataTableAbstracts(HtmlDataTable dataTableAbstracts) {
        this.dataTableAbstracts = dataTableAbstracts;
    }
    
    /**
     * Holds value of property dataTableDistributors.
     */
    private HtmlDataTable dataTableDistributors;
    
    /**
     * Getter for property dataTableDistributor.
     * @return Value of property dataTableDistributor.
     */
    public HtmlDataTable getDataTableDistributors() {
        return this.dataTableDistributors;
    }
    
    /**
     * Setter for property dataTableDistributor.
     * @param dataTableDistributor New value of property dataTableDistributor.
     */
    public void setDataTableDistributors(HtmlDataTable dataTableDistributors) {
        this.dataTableDistributors = dataTableDistributors;
    }
    
    /**
     * Holds value of property dataTableGrants.
     */
    private HtmlDataTable dataTableGrants;
    
    /**
     * Getter for property dataTableGrant.
     * @return Value of property dataTableGrant.
     */
    public HtmlDataTable getDataTableGrants() {
        return this.dataTableGrants;
    }
    
    /**
     * Setter for property dataTableGrant.
     * @param dataTableGrant New value of property dataTableGrant.
     */
    public void setDataTableGrants(HtmlDataTable dataTableGrants) {
        this.dataTableGrants = dataTableGrants;
    }
    
    /**
     * Holds value of property dataTableKeywords.
     */
    private HtmlDataTable dataTableKeywords;
    
    /**
     * Getter for property dataTableKeyword.
     * @return Value of property dataTableKeyword.
     */
    public HtmlDataTable getDataTableKeywords() {
        return this.dataTableKeywords;
    }
    
    /**
     * Setter for property dataTableKeyword.
     * @param dataTableKeyword New value of property dataTableKeyword.
     */
    public void setDataTableKeywords(HtmlDataTable dataTableKeywords) {
        this.dataTableKeywords = dataTableKeywords;
    }
    
    /**
     * Holds value of property dataTableNotes.
     */
    private HtmlDataTable dataTableNotes;
    
    /**
     * Getter for property dataTableNotes.
     * @return Value of property dataTableNotes.
     */
    public HtmlDataTable getDataTableNotes() {
        return this.dataTableNotes;
    }
    
    /**
     * Setter for property dataTableNotes.
     * @param dataTableNotes New value of property dataTableNotes.
     */
    public void setDataTableNotes(HtmlDataTable dataTableNotes) {
        this.dataTableNotes = dataTableNotes;
    }
    
    /**
     * Holds value of property dataTableProducers.
     */
    private HtmlDataTable dataTableProducers;
    
    /**
     * Getter for property dataTableProducers.
     * @return Value of property dataTableProducers.
     */
    public HtmlDataTable getDataTableProducers() {
        return this.dataTableProducers;
    }
    
    /**
     * Setter for property dataTableProducers.
     * @param dataTableProducers New value of property dataTableProducers.
     */
    public void setDataTableProducers(HtmlDataTable dataTableProducers) {
        this.dataTableProducers = dataTableProducers;
    }
    
    /**
     * Holds value of property dataTableSoftware.
     */
    private HtmlDataTable dataTableSoftware;
    
    /**
     * Getter for property dataTableSoftware.
     * @return Value of property dataTableSoftware.
     */
    public HtmlDataTable getDataTableSoftware() {
        return this.dataTableSoftware;
    }
    
    /**
     * Setter for property dataTableSoftware.
     * @param dataTableSoftware New value of property dataTableSoftware.
     */
    public void setDataTableSoftware(HtmlDataTable dataTableSoftware) {
        this.dataTableSoftware = dataTableSoftware;
    }
    
    /**
     * Holds value of property dataTableTopicClass.
     */
    private HtmlDataTable dataTableTopicClass;
    
    /**
     * Getter for property dataTableTopicClass.
     * @return Value of property dataTableTopicClass.
     */
    public HtmlDataTable getDataTableTopicClass() {
        return this.dataTableTopicClass;
    }
    
    /**
     * Setter for property dataTableTopicClass.
     * @param dataTableTopicClass New value of property dataTableTopicClass.
     */
    public void setDataTableTopicClass(HtmlDataTable dataTableTopicClass) {
        this.dataTableTopicClass = dataTableTopicClass;
    }
    
    
    private HtmlCommandButton commandButtonShowFields;
    
    private HtmlCommandButton commandButtonHideFields;
    
    public HtmlCommandButton getCommandButtonShowFields() {
        return commandButtonShowFields;
    }
    
    public void setCommandButtonShowFields(HtmlCommandButton commandButtonShowFields) {
        this.commandButtonShowFields = commandButtonShowFields;
    }
    
    public HtmlCommandButton getCommandButtonHideFields() {
        return commandButtonHideFields;
    }
    
    public void setCommandButtonHideFields(HtmlCommandButton commandButtonHideFields) {
        this.commandButtonHideFields = commandButtonHideFields;
    }
    
    public void showFields(ActionEvent ev) {
        allFieldsShowing=true;
        updateStudyMap();
        commandButtonShowFields.setRendered(false);
        commandButtonHideFields.setRendered(true);
        FacesContext.getCurrentInstance().renderResponse();
    }
    
    public String showFields() {
        Iterator<FacesMessage> it = FacesContext.getCurrentInstance().getMessages();
        while( it.hasNext()) {
            FacesMessage elem = it.next();
            System.out.println("Found FacesMessage: "+elem.toString());
            
        }
        allFieldsShowing=true;
        updateStudyMap();
        commandButtonShowFields.setRendered(false);
        commandButtonHideFields.setRendered(true);
        FacesContext.getCurrentInstance().renderResponse();
        return null;
    }
    public void hideFields(ActionEvent ev) {
        allFieldsShowing=false;
        updateStudyMap();
        commandButtonShowFields.setRendered(true);
        commandButtonHideFields.setRendered(false);
        FacesContext.getCurrentInstance().renderResponse();
        
    }
    private boolean allFieldsShowing;
    
    public boolean isAllFieldsShowing() {
        return allFieldsShowing;
    }
    
    public void setAllFieldsShowing(boolean allFieldsShowing) {
        this.allFieldsShowing = allFieldsShowing;
    }
    
    private Long studyId;
    
    public Long getStudyId() {
        return studyId;
    }
    
    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
        
    private List files;
    
    public List getFiles() {
        return files;
    }
    
    public void setFiles(List files) {
        this.files = files;
    }
    
    
    public String save() {
        if (this.getStudy()==null) {
            return "home";
        }
        removeEmptyRows();
        if (!StringUtil.isEmpty(study.getReplicationFor())  ) {
            if (!study.getTitle().startsWith("Replication data for:")) {
                study.setTitle("Replication data for: "+study.getTitle());
            }
        }
       
        editStudyService.save(getVDCRequestBean().getCurrentVDCId(),getVDCSessionBean().getLoginBean().getUser().getId());
       
        getVDCRequestBean().setStudyId(study.getId());
        getVDCRequestBean().setSelectedTab(tabSet1.getSelected());
        getVDCSessionBean().setStudyService(null);
        this.sessionRemove(token);
        return "viewStudy";
    }
    
    
    
    
    
    private String tab;
    
    public String getTab() {
        return tab;
    }
    
    public void setTab(String tab) {
        if ( tab == null || tab.equals("files") || tab.equals("catalog") ) {
            this.tab = tab;
        }
    }
    
    
    public List getTemplateFileCategories() {
        List tfc = new ArrayList();
        Iterator iter = study.getTemplate().getTemplateFileCategories().iterator();
        while (iter.hasNext()) {
            tfc.add( new SelectItem( ((TemplateFileCategory) iter.next()).getName() ) );
        }
        return tfc;
    }
    
    
    
    public void validateLongitude(FacesContext context,
            UIComponent toValidate,
            Object value) {
        boolean valid=true;
        
        Double longitude = new Double(value.toString().trim());
        BigDecimal decimalLongitude = new BigDecimal(value.toString().trim());
        BigDecimal maxLongitude = new BigDecimal("180");
        BigDecimal minLongitude = new BigDecimal("-180");
        
        // To be valid longitude must be between 180 and -180
        if (decimalLongitude.compareTo(maxLongitude)==1 || decimalLongitude.compareTo(minLongitude)==-1) {
            valid=false;
        }
        
        // decimalLongitude must have at most 3 digits to the right of the decimal place
        if (decimalLongitude.scale()>3) {
            valid=false;
        }
        
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage("Invalid Longitude.  Value must be between -180 and 180. (Unit is decimal degrees.)");
            context.addMessage(toValidate.getClientId(context), message);
           
        }
        
    }
    
    public void validateLatitude(FacesContext context,
            UIComponent toValidate,
            Object value) {
        boolean valid=true;
        
        Double latitude = new Double(value.toString().trim());
        BigDecimal decimalLatitude = new BigDecimal(value.toString().trim());
        BigDecimal maxLatitude = new BigDecimal("90");
        BigDecimal minLatitude = new BigDecimal("-90");
        
        // To be valid latitude must be between 90 and -90
        if (decimalLatitude.compareTo(maxLatitude)==1 || decimalLatitude.compareTo(minLatitude)==-1) {
            valid=false;
        }
        
        // Latitude must have at most 3 digits to the right of the decimal place
        if (decimalLatitude.scale()>3) {
            valid=false;
        }
        
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Invalid Latitude.  Value must be between -90 and 90. (Unit is decimal degrees.)");
            context.addMessage(toValidate.getClientId(context), message);
        }
    }
    
    public void validateStudyAuthor(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
        // StudyAuthor
        String name = (String)inputAuthorName.getLocalValue();
        String affiliation = value.toString();
        
        if (StringUtil.isEmpty(name) && !StringUtil.isEmpty(affiliation)) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Author name is required if Affiliation is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
    
     public void validateStudyOtherId(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputOtherId.getLocalValue())
        && !StringUtil.isEmpty((String)value)  ) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Other ID  is required if Agency is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
     
      public void validateSeries(FacesContext context,
            UIComponent toValidate, Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputSeries.getLocalValue())
        && !StringUtil.isEmpty((String)value))   {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Series is required if Series Information is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
     
 public void validateVersion(FacesContext context,
            UIComponent toValidate, Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputVersion.getLocalValue())
        && !StringUtil.isEmpty((String)value)) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Version is required if Version Date is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }     
    public void validateStudyAbstract(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputAbstractText.getLocalValue())
        && !StringUtil.isEmpty((String)value)  ) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Abstract text  is required if Date is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }   
         public void validateStudyNote(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)this.inputNoteType.getLocalValue())
        && (!StringUtil.isEmpty((String)this.inputNoteText.getLocalValue())
            || !StringUtil.isEmpty((String)this.inputNoteSubject.getLocalValue())
            || !StringUtil.isEmpty((String)value))  ) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Note type is required if other note data is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }   
     
     public void validateStudySoftware(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)this.inputSoftwareName.getLocalValue())
        && !StringUtil.isEmpty((String)value)  ) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Software Name is required if Version is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }  
    }     
    
        public void validateStudyGrant(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputGrantNumber.getLocalValue())
        && !StringUtil.isEmpty((String)value)  ) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Grant Number is required if Grant Number Agency is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }  
    }       
     
     
    public void validateStudyDistributor(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputDistributorName.getLocalValue())
        && (!StringUtil.isEmpty((String)inputDistributorAbbreviation.getLocalValue()) 
             || !StringUtil.isEmpty((String)inputDistributorAffiliation.getLocalValue())
             || !StringUtil.isEmpty((String)inputDistributorLogo.getLocalValue())
             || !StringUtil.isEmpty((String)value) )) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Distributor name is required if other distributor data is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
    
       public void validateDistributorContact(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputDistributorContact.getLocalValue())
        && (!StringUtil.isEmpty((String)inputDistributorContactAffiliation.getLocalValue()) 
             || !StringUtil.isEmpty((String)inputDistributorContactEmail.getLocalValue())
             || !StringUtil.isEmpty((String)value) )) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Distributor contact name is required if distributor contact affiliation is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
    
    public void validateStudyKeyword(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputKeywordValue.getLocalValue())
        && (!StringUtil.isEmpty((String)inputKeywordVocab.getLocalValue()) 
             || !StringUtil.isEmpty((String)value) )) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Keyword value is required if other keyword data is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
    
   public void validateStudyTopicClass(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputTopicClassValue.getLocalValue())
        && (!StringUtil.isEmpty((String)inputTopicClassVocab.getLocalValue()) 
             || !StringUtil.isEmpty((String)value) )) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Topic Classification value is required if other topic classification data is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
           
   public void validateGeographicBounding(FacesContext context,
            UIComponent toValidate,
            Object value) {
     boolean valid=true;
     // if any geographic values are filled, then they all must be filled
     if (!StringUtil.isEmpty((String)inputWestLongitude.getLocalValue())
        ||  !StringUtil.isEmpty((String)inputEastLongitude.getLocalValue())
        ||  !StringUtil.isEmpty((String)inputNorthLatitude.getLocalValue())
        || !StringUtil.isEmpty((String)inputSouthLatitude.getLocalValue())) {
         if ( StringUtil.isEmpty((String)inputWestLongitude.getLocalValue())
            ||  StringUtil.isEmpty((String)inputEastLongitude.getLocalValue())
            ||  StringUtil.isEmpty((String)inputNorthLatitude.getLocalValue())
            || StringUtil.isEmpty((String)inputSouthLatitude.getLocalValue())) {
             
             valid=false;
         }
         
    }
     
     
        
        if (!valid) {
            inputSouthLatitude.setValid(false);
            FacesMessage message = new FacesMessage("If any geographic field is filled, then all geographic fields must be filled.");
            context.addMessage(inputSouthLatitude.getClientId(context), message);
        }
   }
   
    public void validateStudyProducer(FacesContext context,
            UIComponent toValidate,
            Object value) {
        
        boolean valid=true;
       
         
        if (StringUtil.isEmpty((String)inputProducerName.getLocalValue())
        && (!StringUtil.isEmpty((String)inputProducerAbbreviation.getLocalValue()) 
             || !StringUtil.isEmpty((String)inputProducerAffiliation.getLocalValue())
             || !StringUtil.isEmpty((String)inputProducerLogo.getLocalValue())
             || !StringUtil.isEmpty((String)value) )) {
            valid=false;
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Producer name is required if other producer data is entered.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
      
  
    public void validateStudyId(FacesContext context,
            UIComponent toValidate,
            Object value) {
        boolean valid=true;
        
        
        
        String studyId = (String)value;
        FacesMessage message=null;
        if (!studyService.isUniqueStudyId(studyId, study.getProtocol(),study.getAuthority())) {
            valid=false;
            message = new FacesMessage("Study ID is already used in this dataverse.");
        }
        if (valid) {
            if (!StringUtil.isAlphaNumeric(studyId)) {
                valid = false;
                message = new FacesMessage("Study ID can only contain characters a-z, A-Z or 0-9 (no spaces allowed)");
            }
        }
        
        if (!valid) {
            ((UIInput)toValidate).setValid(false);           
            context.addMessage(toValidate.getClientId(context), message);
        }
    }
    
    public void validateFileName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String fileName = (String) value;
        String errorMessage = null;
        
        // check invalid characters
        if (    fileName.contains("\\") ||
                fileName.contains("/") ||
                fileName.contains(":") ||
                fileName.contains("*") ||
                fileName.contains("?") ||
                fileName.contains("\"") ||
                fileName.contains("<") ||
                fileName.contains(">") ||
                fileName.contains("|") ||
                fileName.contains(";") ||
                fileName.contains("#")) {
            errorMessage = "cannot contain any of the following characters: \\ / : * ? \" < > | ; #";
        }
        
        
        // now check unique filename against other file names
        Iterator iter = getValidationFileNames().iterator();
        while (iter.hasNext()) {
            
            if ( fileName.equals( (String) iter.next() ) ) {
                errorMessage = "must be unique.";
                break;
            }
            
        }
        
        
        // now add this name to the validation list
        getValidationFileNames().add(fileName);
        
        if (errorMessage != null) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage("Invalid File Name - " + errorMessage);
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
    
    /**
     * Holds value of property dataTableGeoBoundings.
     */
    private HtmlDataTable dataTableGeoBoundings;
    
    /**
     * Getter for property dataTableGeoBoundings.
     * @return Value of property dataTableGeoBoundings.
     */
    public HtmlDataTable getDataTableGeoBoundings() {
        return this.dataTableGeoBoundings;
    }
    
    /**
     * Setter for property dataTableGeoBoundings.
     * @param dataTableGeoBoundings New value of property dataTableGeoBoundings.
     */
    public void setDataTableGeoBoundings(HtmlDataTable dataTableGeoBoundings) {
        this.dataTableGeoBoundings = dataTableGeoBoundings;
    }
    
    private List validationFileNames = new ArrayList();
    
    public List getValidationFileNames() {
        return validationFileNames;
    }
    
    /**
     * Holds value of property dataTableRelMaterials.
     */
    private HtmlDataTable dataTableRelMaterials;
    
    /**
     * Getter for property dataTableRelMaterials.
     * @return Value of property dataTableRelMaterials.
     */
    public HtmlDataTable getDataTableRelMaterials() {
        return this.dataTableRelMaterials;
    }
    
    /**
     * Setter for property dataTableRelMaterials.
     * @param dataTableRelMaterials New value of property dataTableRelMaterials.
     */
    public void setDataTableRelMaterials(HtmlDataTable dataTableRelMaterials) {
        this.dataTableRelMaterials = dataTableRelMaterials;
    }
    
    /**
     * Holds value of property dataTableRelStudies.
     */
    private HtmlDataTable dataTableRelStudies;
    
    /**
     * Getter for property dataTableRelStudies.
     * @return Value of property dataTableRelStudies.
     */
    public HtmlDataTable getDataTableRelStudies() {
        return this.dataTableRelStudies;
    }
    
    /**
     * Setter for property dataTableRelStudies.
     * @param dataTableRelStudies New value of property dataTableRelStudies.
     */
    public void setDataTableRelStudies(HtmlDataTable dataTableRelStudies) {
        this.dataTableRelStudies = dataTableRelStudies;
    }
    
    /**
     * Holds value of property dataTableRelPublications.
     */
    private HtmlDataTable dataTableRelPublications;
    
    /**
     * Getter for property dataTableRelPublications.
     * @return Value of property dataTableRelPublications.
     */
    public HtmlDataTable getDataTableRelPublications() {
        return this.dataTableRelPublications;
    }
    
    /**
     * Setter for property dataTableRelPublications.
     * @param dataTableRelPublications New value of property dataTableRelPublications.
     */
    public void setDataTableRelPublications(HtmlDataTable dataTableRelPublications) {
        this.dataTableRelPublications = dataTableRelPublications;
    }
    
    /**
     * Holds value of property dataTableOtherReferences.
     */
    private HtmlDataTable dataTableOtherReferences;
    
    /**
     * Getter for property dataTableOtherReferences.
     * @return Value of property dataTableOtherReferences.
     */
    public HtmlDataTable getDataTableOtherReferences() {
        return this.dataTableOtherReferences;
    }
    
    /**
     * Setter for property dataTableOtherReferences.
     * @param dataTableOtherReferences New value of property dataTableOtherReferences.
     */
    public void setDataTableOtherReferences(HtmlDataTable dataTableOtherReferences) {
        this.dataTableOtherReferences = dataTableOtherReferences;
    }

    /**
     * Holds value of property inputAuthorName.
     */
    private HtmlInputText inputAuthorName;

    /**
     * Getter for property studyAuthorName.
     * @return Value of property studyAuthorName.
     */
    public HtmlInputText getInputAuthorName() {
        return this.inputAuthorName;
    }

    /**
     * Setter for property studyAuthorName.
     * @param studyAuthorName New value of property studyAuthorName.
     */
    public void setInputAuthorName(HtmlInputText inputAuthorName) {
        this.inputAuthorName = inputAuthorName;
    }

    /**
     * Holds value of property inputAuthorAffiliation.
     */
    private HtmlInputText inputAuthorAffiliation;

    /**
     * Getter for property studyAuthorAffiliation.
     * @return Value of property studyAuthorAffiliation.
     */
    public HtmlInputText getInputAuthorAffiliation() {
        return this.inputAuthorAffiliation;
    }

    /**
     * Setter for property studyAuthorAffiliation.
     * @param studyAuthorAffiliation New value of property studyAuthorAffiliation.
     */
    public void setInputAuthorAffiliation(HtmlInputText inputAuthorAffiliation) {
        this.inputAuthorAffiliation = inputAuthorAffiliation;
    }

    /**
     * Holds value of property sessionCounter.
     */
    private int sessionCounter;

    /**
     * Getter for property sessionCounter.
     * @return Value of property sessionCounter.
     */
    public int getSessionCounter() {
        return this.sessionCounter;
    }

    /**
     * Setter for property sessionCounter.
     * @param sessionCounter New value of property sessionCounter.
     */
    public void setSessionCounter(int sessionCounter) {
        this.sessionCounter = sessionCounter;
    }

    /**
     * Holds value of property inputProducerName.
     */
    private HtmlInputText inputProducerName;

    /**
     * Getter for property inputStudyProducerName.
     * @return Value of property inputStudyProducerName.
     */
    public HtmlInputText getInputProducerName() {
        return this.inputProducerName;
    }

    /**
     * Setter for property inputStudyProducerName.
     * @param inputStudyProducerName New value of property inputStudyProducerName.
     */
    public void setInputProducerName(HtmlInputText inputProducerName) {
        this.inputProducerName = inputProducerName;
    }

    /**
     * Holds value of property inputProducerAffiliation.
     */
    private HtmlInputText inputProducerAffiliation;

    /**
     * Getter for property inputProducerAffiliation.
     * @return Value of property inputProducerAffiliation.
     */
    public HtmlInputText getInputProducerAffiliation() {
        return this.inputProducerAffiliation;
    }

    /**
     * Setter for property inputProducerAffiliation.
     * @param inputProducerAffiliation New value of property inputProducerAffiliation.
     */
    public void setInputProducerAffiliation(HtmlInputText inputProducerAffiliation) {
        this.inputProducerAffiliation = inputProducerAffiliation;
    }

    /**
     * Holds value of property inputProducerAbbreviation.
     */
    private HtmlInputText inputProducerAbbreviation;

    /**
     * Getter for property inputProducerAbbreviation.
     * @return Value of property inputProducerAbbreviation.
     */
    public HtmlInputText getInputProducerAbbreviation() {
        return this.inputProducerAbbreviation;
    }

    /**
     * Setter for property inputProducerAbbreviation.
     * @param inputProducerAbbreviation New value of property inputProducerAbbreviation.
     */
    public void setInputProducerAbbreviation(HtmlInputText inputProducerAbbreviation) {
        this.inputProducerAbbreviation = inputProducerAbbreviation;
    }

    /**
     * Holds value of property inputProducerUrl.
     */
    private HtmlInputText inputProducerUrl;

    /**
     * Getter for property inputProducerUrl.
     * @return Value of property inputProducerUrl.
     */
    public HtmlInputText getInputProducerUrl() {
        return this.inputProducerUrl;
    }

    /**
     * Setter for property inputProducerUrl.
     * @param inputProducerUrl New value of property inputProducerUrl.
     */
    public void setInputProducerUrl(HtmlInputText inputProducerUrl) {
        this.inputProducerUrl = inputProducerUrl;
    }

    /**
     * Holds value of property inputProducerLogo.
     */
    private HtmlInputText inputProducerLogo;

    /**
     * Getter for property inputProducerLogo.
     * @return Value of property inputProducerLogo.
     */
    public HtmlInputText getInputProducerLogo() {
        return this.inputProducerLogo;
    }

    /**
     * Setter for property inputProducerLogo.
     * @param inputProducerLogo New value of property inputProducerLogo.
     */
    public void setInputProducerLogo(HtmlInputText inputProducerLogo) {
        this.inputProducerLogo = inputProducerLogo;
    }

    /**
     * Holds value of property inputSoftwareVersion.
     */
    private HtmlInputText inputSoftwareVersion;

    /**
     * Getter for property inputSoftwareVersion.
     * @return Value of property inputSoftwareVersion.
     */
    public HtmlInputText getInputSoftwareVersion() {
        return this.inputSoftwareVersion;
    }

    /**
     * Setter for property inputSoftwareVersion.
     * @param inputSoftwareVersion New value of property inputSoftwareVersion.
     */
    public void setInputSoftwareVersion(HtmlInputText inputSoftwareVersion) {
        this.inputSoftwareVersion = inputSoftwareVersion;
    }

    /**
     * Holds value of property inputSoftwareName.
     */
    private HtmlInputText inputSoftwareName;

    /**
     * Getter for property inputSoftwareName.
     * @return Value of property inputSoftwareName.
     */
    public HtmlInputText getInputSoftwareName() {
        return this.inputSoftwareName;
    }

    /**
     * Setter for property inputSoftwareName.
     * @param inputSoftwareName New value of property inputSoftwareName.
     */
    public void setInputSoftwareName(HtmlInputText inputSoftwareName) {
        this.inputSoftwareName = inputSoftwareName;
    }

    /**
     * Holds value of property inputGrantNumber.
     */
    private HtmlInputText inputGrantNumber;

    /**
     * Getter for property inputGrantNumber.
     * @return Value of property inputGrantNumber.
     */
    public HtmlInputText getInputGrantNumber() {
        return this.inputGrantNumber;
    }

    /**
     * Setter for property inputGrantNumber.
     * @param inputGrantNumber New value of property inputGrantNumber.
     */
    public void setInputGrantNumber(HtmlInputText inputGrantNumber) {
        this.inputGrantNumber = inputGrantNumber;
    }

    /**
     * Holds value of property inputDistributorName.
     */
    private HtmlInputText inputDistributorName;

    /**
     * Getter for property inputDistributorName.
     * @return Value of property inputDistributorName.
     */
    public HtmlInputText getInputDistributorName() {
        return this.inputDistributorName;
    }

    /**
     * Setter for property inputDistributorName.
     * @param inputDistributorName New value of property inputDistributorName.
     */
    public void setInputDistributorName(HtmlInputText inputDistributorName) {
        this.inputDistributorName = inputDistributorName;
    }

    /**
     * Holds value of property inputDistributorAffiliation.
     */
    private HtmlInputText inputDistributorAffiliation;

    /**
     * Getter for property inputDistributorAffiliation.
     * @return Value of property inputDistributorAffiliation.
     */
    public HtmlInputText getInputDistributorAffiliation() {
        return this.inputDistributorAffiliation;
    }

    /**
     * Setter for property inputDistributorAffiliation.
     * @param inputDistributorAffiliation New value of property inputDistributorAffiliation.
     */
    public void setInputDistributorAffiliation(HtmlInputText inputDistributorAffiliation) {
        this.inputDistributorAffiliation = inputDistributorAffiliation;
    }

    /**
     * Holds value of property inputDistributorAbbreviation.
     */
    private HtmlInputText inputDistributorAbbreviation;

    /**
     * Getter for property inputDistributorAbbreviation.
     * @return Value of property inputDistributorAbbreviation.
     */
    public HtmlInputText getInputDistributorAbbreviation() {
        return this.inputDistributorAbbreviation;
    }

    /**
     * Setter for property inputDistributorAbbreviation.
     * @param inputDistributorAbbreviation New value of property inputDistributorAbbreviation.
     */
    public void setInputDistributorAbbreviation(HtmlInputText inputDistributorAbbreviation) {
        this.inputDistributorAbbreviation = inputDistributorAbbreviation;
    }

    /**
     * Holds value of property inputDistributorUrl.
     */
    private HtmlInputText inputDistributorUrl;

    /**
     * Getter for property inputDistributorUrl.
     * @return Value of property inputDistributorUrl.
     */
    public HtmlInputText getInputDistributorUrl() {
        return this.inputDistributorUrl;
    }

    /**
     * Setter for property inputDistributorUrl.
     * @param inputDistributorUrl New value of property inputDistributorUrl.
     */
    public void setInputDistributorUrl(HtmlInputText inputDistributorUrl) {
        this.inputDistributorUrl = inputDistributorUrl;
    }

    /**
     * Holds value of property inputDistributorLogo.
     */
    private HtmlInputText inputDistributorLogo;

    /**
     * Getter for property inputDistributorLogo.
     * @return Value of property inputDistributorLogo.
     */
    public HtmlInputText getInputDistributorLogo() {
        return this.inputDistributorLogo;
    }

    /**
     * Setter for property inputDistributorLogo.
     * @param inputDistributorLogo New value of property inputDistributorLogo.
     */
    public void setInputDistributorLogo(HtmlInputText inputDistributorLogo) {
        this.inputDistributorLogo = inputDistributorLogo;
    }

    /**
     * Holds value of property inputRelPublicationName.
     */
    private HtmlInputTextarea inputRelPublicationName;

    /**
     * Getter for property inputRelPublicationName.
     * @return Value of property inputRelPublicationName.
     */
    public HtmlInputTextarea getInputRelPublicationName() {
        return this.inputRelPublicationName;
    }

    /**
     * Setter for property inputRelPublicationName.
     * @param inputRelPublicationName New value of property inputRelPublicationName.
     */
    public void setInputRelPublicationName(HtmlInputTextarea inputRelPublicationName) {
        this.inputRelPublicationName = inputRelPublicationName;
    }

    /**
     * Holds value of property inputRelMaterial.
     */
    private HtmlInputTextarea inputRelMaterial;

    /**
     * Getter for property inputRelMaterial.
     * @return Value of property inputRelMaterial.
     */
    public HtmlInputTextarea getInputRelMaterial() {
        return this.inputRelMaterial;
    }

    /**
     * Setter for property inputRelMaterial.
     * @param inputRelMaterial New value of property inputRelMaterial.
     */
    public void setInputRelMaterial(HtmlInputTextarea inputRelMaterial) {
        this.inputRelMaterial = inputRelMaterial;
    }

    /**
     * Holds value of property inputRelStudy.
     */
    private HtmlInputTextarea inputRelStudy;

    /**
     * Getter for property inputRelStudy.
     * @return Value of property inputRelStudy.
     */
    public HtmlInputTextarea getInputRelStudy() {
        return this.inputRelStudy;
    }

    /**
     * Setter for property inputRelStudy.
     * @param inputRelStudy New value of property inputRelStudy.
     */
    public void setInputRelStudy(HtmlInputTextarea inputRelStudy) {
        this.inputRelStudy = inputRelStudy;
    }

    /**
     * Holds value of property inputOtherReference.
     */
    private HtmlInputText inputOtherReference;

    /**
     * Getter for property inputOtherReference.
     * @return Value of property inputOtherReference.
     */
    public HtmlInputText getInputOtherReference() {
        return this.inputOtherReference;
    }

    /**
     * Setter for property inputOtherReference.
     * @param inputOtherReference New value of property inputOtherReference.
     */
    public void setInputOtherReference(HtmlInputText inputOtherReference) {
        this.inputOtherReference = inputOtherReference;
    }

    /**
     * Holds value of property inputKeywordValue.
     */
    private HtmlInputText inputKeywordValue;

    /**
     * Getter for property inputKeywordValue.
     * @return Value of property inputKeywordValue.
     */
    public HtmlInputText getInputKeywordValue() {
        return this.inputKeywordValue;
    }

    /**
     * Setter for property inputKeywordValue.
     * @param inputKeywordValue New value of property inputKeywordValue.
     */
    public void setInputKeywordValue(HtmlInputText inputKeywordValue) {
        this.inputKeywordValue = inputKeywordValue;
    }

    /**
     * Holds value of property inputKeywordVocab.
     */
    private HtmlInputText inputKeywordVocab;

    /**
     * Getter for property inputKeywordVocab.
     * @return Value of property inputKeywordVocab.
     */
    public HtmlInputText getInputKeywordVocab() {
        return this.inputKeywordVocab;
    }

    /**
     * Setter for property inputKeywordVocab.
     * @param inputKeywordVocab New value of property inputKeywordVocab.
     */
    public void setInputKeywordVocab(HtmlInputText inputKeywordVocab) {
        this.inputKeywordVocab = inputKeywordVocab;
    }

    /**
     * Holds value of property inputKeywordVocabUri.
     */
    private HtmlInputText inputKeywordVocabUri;

    /**
     * Getter for property inputKeywordVocabUri.
     * @return Value of property inputKeywordVocabUri.
     */
    public HtmlInputText getInputKeywordVocabUri() {
        return this.inputKeywordVocabUri;
    }

    /**
     * Setter for property inputKeywordVocabUri.
     * @param inputKeywordVocabUri New value of property inputKeywordVocabUri.
     */
    public void setInputKeywordVocabUri(HtmlInputText inputKeywordVocabUri) {
        this.inputKeywordVocabUri = inputKeywordVocabUri;
    }

    /**
     * Holds value of property inputTopicClassValue.
     */
    private HtmlInputText inputTopicClassValue;

    /**
     * Getter for property inputTopicClassValue.
     * @return Value of property inputTopicClassValue.
     */
    public HtmlInputText getInputTopicClassValue() {
        return this.inputTopicClassValue;
    }

    /**
     * Setter for property inputTopicClassValue.
     * @param inputTopicClassValue New value of property inputTopicClassValue.
     */
    public void setInputTopicClassValue(HtmlInputText inputTopicClassValue) {
        this.inputTopicClassValue = inputTopicClassValue;
    }

    /**
     * Holds value of property inputTopicClassVocab.
     */
    private HtmlInputText inputTopicClassVocab;

    /**
     * Getter for property inputTopicClassVocab.
     * @return Value of property inputTopicClassVocab.
     */
    public HtmlInputText getInputTopicClassVocab() {
        return this.inputTopicClassVocab;
    }

    /**
     * Setter for property inputTopicClassVocab.
     * @param inputTopicClassVocab New value of property inputTopicClassVocab.
     */
    public void setInputTopicClassVocab(HtmlInputText inputTopicClassVocab) {
        this.inputTopicClassVocab = inputTopicClassVocab;
    }

    /**
     * Holds value of property inputTopicClassVocabUri.
     */
    private HtmlInputText inputTopicClassVocabUri;

    /**
     * Getter for property inputTopicVocabUri.
     * @return Value of property inputTopicVocabUri.
     */
    public HtmlInputText getInputTopicClassVocabUri() {
        return this.inputTopicClassVocabUri;
    }

    /**
     * Setter for property inputTopicVocabUri.
     * @param inputTopicVocabUri New value of property inputTopicVocabUri.
     */
    public void setInputTopicClassVocabUri(HtmlInputText inputTopicClassVocabUri) {
        this.inputTopicClassVocabUri = inputTopicClassVocabUri;
    }

    /**
     * Holds value of property inputAbstractText.
     */
    private javax.faces.component.html.HtmlInputTextarea inputAbstractText;

    /**
     * Getter for property inputAbstractText.
     * @return Value of property inputAbstractText.
     */
    public javax.faces.component.html.HtmlInputTextarea getInputAbstractText() {
        return this.inputAbstractText;
    }

    /**
     * Setter for property inputAbstractText.
     * @param inputAbstractText New value of property inputAbstractText.
     */
    public void setInputAbstractText(javax.faces.component.html.HtmlInputTextarea inputAbstractText) {
        this.inputAbstractText = inputAbstractText;
    }

    /**
     * Holds value of property inputAbstractDate.
     */
    private HtmlInputText inputAbstractDate;

    /**
     * Getter for property inputAbstractDate.
     * @return Value of property inputAbstractDate.
     */
    public HtmlInputText getInputAbstractDate() {
        return this.inputAbstractDate;
    }

    /**
     * Setter for property inputAbstractDate.
     * @param inputAbstractDate New value of property inputAbstractDate.
     */
    public void setInputAbstractDate(HtmlInputText inputAbstractDate) {
        this.inputAbstractDate = inputAbstractDate;
    }

    /**
     * Holds value of property inputNoteText.
     */
    private HtmlInputText inputNoteText;

    /**
     * Getter for property inputNoteText.
     * @return Value of property inputNoteText.
     */
    public HtmlInputText getInputNoteText() {
        return this.inputNoteText;
    }

    /**
     * Setter for property inputNoteText.
     * @param inputNoteText New value of property inputNoteText.
     */
    public void setInputNoteText(HtmlInputText inputNoteText) {
        this.inputNoteText = inputNoteText;
    }

    /**
     * Holds value of property inputNoteSubject.
     */
    private HtmlInputText inputNoteSubject;

    /**
     * Getter for property inputNoteSubject.
     * @return Value of property inputNoteSubject.
     */
    public HtmlInputText getInputNoteSubject() {
        return this.inputNoteSubject;
    }

    /**
     * Setter for property inputNoteSubject.
     * @param inputNoteSubject New value of property inputNoteSubject.
     */
    public void setInputNoteSubject(HtmlInputText inputNoteSubject) {
        this.inputNoteSubject = inputNoteSubject;
    }

    /**
     * Holds value of property inputNoteType.
     */
    private HtmlInputText inputNoteType;

    /**
     * Getter for property inputNoteType.
     * @return Value of property inputNoteType.
     */
    public HtmlInputText getInputNoteType() {
        return this.inputNoteType;
    }

    /**
     * Setter for property inputNoteType.
     * @param inputNoteType New value of property inputNoteType.
     */
    public void setInputNoteType(HtmlInputText inputNoteType) {
        this.inputNoteType = inputNoteType;
    }

    /**
     * Holds value of property inputOtherId.
     */
    private HtmlInputText inputOtherId;

    /**
     * Getter for property inputOtherId.
     * @return Value of property inputOtherId.
     */
    public HtmlInputText getInputOtherId() {
        return this.inputOtherId;
    }

    /**
     * Setter for property inputOtherId.
     * @param inputOtherId New value of property inputOtherId.
     */
    public void setInputOtherId(HtmlInputText inputOtherId) {
        this.inputOtherId = inputOtherId;
    }

    /**
     * Holds value of property inputOtherIdAgency.
     */
    private HtmlInputText inputOtherIdAgency;

    /**
     * Getter for property inputOtherIdAgency.
     * @return Value of property inputOtherIdAgency.
     */
    public HtmlInputText getInputOtherIdAgency() {
        return this.inputOtherIdAgency;
    }

    /**
     * Setter for property inputOtherIdAgency.
     * @param inputOtherIdAgency New value of property inputOtherIdAgency.
     */
    public void setInputOtherIdAgency(HtmlInputText inputOtherIdAgency) {
        this.inputOtherIdAgency = inputOtherIdAgency;
    }

    /**
     * Holds value of property inputGrantAgency.
     */
    private HtmlInputText inputGrantAgency;

    /**
     * Getter for property inputGrantAgency.
     * @return Value of property inputGrantAgency.
     */
    public HtmlInputText getInputGrantAgency() {
        return this.inputGrantAgency;
    }

    /**
     * Setter for property inputGrantAgency.
     * @param inputGrantAgency New value of property inputGrantAgency.
     */
    public void setInputGrantAgency(HtmlInputText inputGrantAgency) {
        this.inputGrantAgency = inputGrantAgency;
    }

    /**
     * Holds value of property inputSeries.
     */
    private HtmlInputText inputSeries;

    /**
     * Getter for property inputSeries.
     * @return Value of property inputSeries.
     */
    public HtmlInputText getInputSeries() {
        return this.inputSeries;
    }

    /**
     * Setter for property inputSeries.
     * @param inputSeries New value of property inputSeries.
     */
    public void setInputSeries(HtmlInputText inputSeries) {
        this.inputSeries = inputSeries;
    }

    /**
     * Holds value of property inputSeriesInformation.
     */
    private HtmlInputText inputSeriesInformation;

    /**
     * Getter for property inputSeriesInformation.
     * @return Value of property inputSeriesInformation.
     */
    public HtmlInputText getInputSeriesInformation() {
        return this.inputSeriesInformation;
    }

    /**
     * Setter for property inputSeriesInformation.
     * @param inputSeriesInformation New value of property inputSeriesInformation.
     */
    public void setInputSeriesInformation(HtmlInputText inputSeriesInformation) {
        this.inputSeriesInformation = inputSeriesInformation;
    }

    /**
     * Holds value of property inputVersion.
     */
    private HtmlInputText inputVersion;

    /**
     * Getter for property inputVersion.
     * @return Value of property inputVersion.
     */
    public HtmlInputText getInputVersion() {
        return this.inputVersion;
    }

    /**
     * Setter for property inputVersion.
     * @param inputVersion New value of property inputVersion.
     */
    public void setInputVersion(HtmlInputText inputVersion) {
        this.inputVersion = inputVersion;
    }

    /**
     * Holds value of property inputVersionDate.
     */
    private HtmlInputText inputVersionDate;

    /**
     * Getter for property inputVersionDate.
     * @return Value of property inputVersionDate.
     */
    public HtmlInputText getInputVersionDate() {
        return this.inputVersionDate;
    }

    /**
     * Setter for property inputVersionDate.
     * @param inputVersionDate New value of property inputVersionDate.
     */
    public void setInputVersionDate(HtmlInputText inputVersionDate) {
        this.inputVersionDate = inputVersionDate;
    }

    /**
     * Holds value of property inputDistributorContact.
     */
    private HtmlInputText inputDistributorContact;

    /**
     * Getter for property inputDistributorContact.
     * @return Value of property inputDistributorContact.
     */
    public HtmlInputText getInputDistributorContact() {
        return this.inputDistributorContact;
    }

    /**
     * Setter for property inputDistributorContact.
     * @param inputDistributorContact New value of property inputDistributorContact.
     */
    public void setInputDistributorContact(HtmlInputText inputDistributorContact) {
        this.inputDistributorContact = inputDistributorContact;
    }

    /**
     * Holds value of property inputDistributorContactAffiliation.
     */
    private HtmlInputText inputDistributorContactAffiliation;

    /**
     * Getter for property inputDistributorContactAffiliation.
     * @return Value of property inputDistributorContactAffiliation.
     */
    public HtmlInputText getInputDistributorContactAffiliation() {
        return this.inputDistributorContactAffiliation;
    }

    /**
     * Setter for property inputDistributorContactAffiliation.
     * @param inputDistributorContactAffiliation New value of property inputDistributorContactAffiliation.
     */
    public void setInputDistributorContactAffiliation(HtmlInputText inputDistributorContactAffiliation) {
        this.inputDistributorContactAffiliation = inputDistributorContactAffiliation;
    }

    /**
     * Holds value of property inputDistributorContactEmail.
     */
    private HtmlInputText inputDistributorContactEmail;

    /**
     * Getter for property inputDistributorContactEmail.
     * @return Value of property inputDistributorContactEmail.
     */
    public HtmlInputText getInputDistributorContactEmail() {
        return this.inputDistributorContactEmail;
    }

    /**
     * Setter for property inputDistributorContactEmail.
     * @param inputDistributorContactEmail New value of property inputDistributorContactEmail.
     */
    public void setInputDistributorContactEmail(HtmlInputText inputDistributorContactEmail) {
        this.inputDistributorContactEmail = inputDistributorContactEmail;
    }

    /**
     * Holds value of property inputWestLongitude.
     */
    private HtmlInputText inputWestLongitude;

    /**
     * Getter for property inputWestLongitude.
     * @return Value of property inputWestLongitude.
     */
    public HtmlInputText getInputWestLongitude() {
        return this.inputWestLongitude;
    }

    /**
     * Setter for property inputWestLongitude.
     * @param inputWestLongitude New value of property inputWestLongitude.
     */
    public void setInputWestLongitude(HtmlInputText inputWestLongitude) {
        this.inputWestLongitude = inputWestLongitude;
    }

    /**
     * Holds value of property inputEastLongitude.
     */
    private HtmlInputText inputEastLongitude;

    /**
     * Getter for property inputEastLongitude.
     * @return Value of property inputEastLongitude.
     */
    public HtmlInputText getInputEastLongitude() {
        return this.inputEastLongitude;
    }

    /**
     * Setter for property inputEastLongitude.
     * @param inputEastLongitude New value of property inputEastLongitude.
     */
    public void setInputEastLongitude(HtmlInputText inputEastLongitude) {
        this.inputEastLongitude = inputEastLongitude;
    }

    /**
     * Holds value of property inputNorthLatitude.
     */
    private HtmlInputText inputNorthLatitude;

    /**
     * Getter for property inputNorthLatitude.
     * @return Value of property inputNorthLatitude.
     */
    public HtmlInputText getInputNorthLatitude() {
        return this.inputNorthLatitude;
    }

    /**
     * Setter for property inputNorthLatitude.
     * @param inputNorthLatitude New value of property inputNorthLatitude.
     */
    public void setInputNorthLatitude(HtmlInputText inputNorthLatitude) {
        this.inputNorthLatitude = inputNorthLatitude;
    }

    /**
     * Holds value of property inputSouthLatitude.
     */
    private HtmlInputText inputSouthLatitude;

    /**
     * Getter for property inputSouthLatitude.
     * @return Value of property inputSouthLatitude.
     */
    public HtmlInputText getInputSouthLatitude() {
        return this.inputSouthLatitude;
    }

    /**
     * Setter for property inputSouthLatitude.
     * @param inputSouthLatitude New value of property inputSouthLatitude.
     */
    public void setInputSouthLatitude(HtmlInputText inputSouthLatitude) {
        this.inputSouthLatitude = inputSouthLatitude;
    }
    
    
  
    
}

