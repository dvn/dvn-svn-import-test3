/*
 * SearchFieldsPage.java
 *
 * Created on October 10, 2006, 10:23 AM
 * Copyright mcrosas
 */
package edu.harvard.hmdc.vdcnet.web.admin;

import com.sun.rave.web.ui.component.Body;
import com.sun.rave.web.ui.component.Form;
import com.sun.rave.web.ui.component.Head;
import com.sun.rave.web.ui.component.Html;
import com.sun.rave.web.ui.component.Link;
import com.sun.rave.web.ui.component.Page;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.study.StudyField;
import edu.harvard.hmdc.vdcnet.study.StudyFieldServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.StatusMessage;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.component.html.HtmlPanelGrid;
import com.sun.rave.web.ui.component.PanelLayout;
import javax.faces.component.html.HtmlOutputText;
import com.sun.rave.web.ui.component.PanelGroup;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.UIColumn;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import com.sun.rave.web.ui.component.HelpInline;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class SearchFieldsPage extends VDCBaseBean {
    @EJB VDCServiceLocal vdcService;
    @EJB StudyFieldServiceLocal studyFieldService;
    @EJB IndexServiceLocal indexService;
    DataModel studyFields;
    Collection <StudyField> searchResultsFields;
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init() {
        super.init();
        if (msg == null){
            msg =  (StatusMessage)getRequestMap().get("statusMessage");
        }
        searchResultsFields = getVDCRequestBean().getCurrentVDC().getSearchResultFields();
        for (Iterator it = searchResultsFields.iterator(); it.hasNext();) {
            StudyField elem = (StudyField) it.next();
            if (elem.getName().equals("producerName")){
                producerResults = true;
            }
            if (elem.getName().equals("distributionDate")){
                distributionDateResults = true;
            }
            if (elem.getName().equals("distributorName")){
                distributorResults = true;
            }
            if (elem.getName().equals("replicationFor")){
                replicationForResults = true;
            }
            if (elem.getName().equals("relatedPublications")){
                relatedPublicationsResults = true;
            }
            if (elem.getName().equals("relatedMaterial")){
                relatedMaterialResults = true;
            }
            if (elem.getName().equals("relatedStudies")){
                relatedStudiesResults = true;
            }
        }
    }
    
    StatusMessage msg;
    
    public StatusMessage getMsg(){
        return msg;
    }
    
    public void setMsg(StatusMessage msg){
        this.msg = msg;
    }
    
    private boolean producerResults;
    
    public boolean isProducerResults(){
        return producerResults;
    }
    
    public void setProducerResults(boolean checked){
        this.producerResults = checked;
    }
    
    private boolean distributionDateResults;
    
    public boolean isDistributionDateResults(){
        return distributionDateResults;
    }
    
    public void setDistributionDateResults(boolean checked){
        this.distributionDateResults = checked;
    }
    
    private boolean distributorResults;
    
    public boolean isDistributorResults(){
        return distributorResults;
    }
    
    public void setDistributorResults(boolean checked){
        this.distributorResults = checked;
    }
    
    private boolean replicationForResults;
    
    public boolean isReplicationForResults(){
        return replicationForResults;
    }
    
    public void setReplicationForResults(boolean checked){
        this.replicationForResults = checked;
    }
    
    private boolean relatedPublicationsResults;
    
    public boolean isRelatedPublicationsResults(){
        return relatedPublicationsResults;
    }
    
    public void setRelatedPublicationsResults(boolean checked){
        this.relatedPublicationsResults = checked;
    }
    
    private boolean relatedMaterialResults;
    
    public boolean isRelatedMaterialResults(){
        return relatedMaterialResults;
    }
    
    public void setRelatedMaterialResults(boolean checked){
        this.relatedMaterialResults = checked;
    }
    
    private boolean relatedStudiesResults;
    
    public boolean isRelatedStudiesResults(){
        return relatedStudiesResults;
    }
    
    public void setRelatedStudiesResults(boolean checked){
        this.relatedStudiesResults = checked;
    }
    
    private Page page1 = new Page();
    
    public Page getPage1() {
        return page1;
    }
    
    public void setPage1(Page p) {
        this.page1 = p;
    }
    
    private Html html1 = new Html();
    
    public Html getHtml1() {
        return html1;
    }
    
    public void setHtml1(Html h) {
        this.html1 = h;
    }
    
    private Head head1 = new Head();
    
    public Head getHead1() {
        return head1;
    }
    
    public void setHead1(Head h) {
        this.head1 = h;
    }
    
    private Link link1 = new Link();
    
    public Link getLink1() {
        return link1;
    }
    
    public void setLink1(Link l) {
        this.link1 = l;
    }
    
    private Body body1 = new Body();
    
    public Body getBody1() {
        return body1;
    }
    
    public void setBody1(Body b) {
        this.body1 = b;
    }
    
    private Form form1 = new Form();
    
    public Form getForm1() {
        return form1;
    }
    
    public void setForm1(Form f) {
        this.form1 = f;
    }

    private HtmlPanelGrid gridPanel1 = new HtmlPanelGrid();

    public HtmlPanelGrid getGridPanel1() {
        return gridPanel1;
    }

    public void setGridPanel1(HtmlPanelGrid hpg) {
        this.gridPanel1 = hpg;
    }

    private PanelLayout layoutPanel1 = new PanelLayout();

    public PanelLayout getLayoutPanel1() {
        return layoutPanel1;
    }

    public void setLayoutPanel1(PanelLayout pl) {
        this.layoutPanel1 = pl;
    }

    private PanelLayout layoutPanel2 = new PanelLayout();

    public PanelLayout getLayoutPanel2() {
        return layoutPanel2;
    }

    public void setLayoutPanel2(PanelLayout pl) {
        this.layoutPanel2 = pl;
    }

    private HtmlOutputText outputText1 = new HtmlOutputText();

    public HtmlOutputText getOutputText1() {
        return outputText1;
    }

    public void setOutputText1(HtmlOutputText hot) {
        this.outputText1 = hot;
    }

    private PanelLayout layoutPanel3 = new PanelLayout();

    public PanelLayout getLayoutPanel3() {
        return layoutPanel3;
    }

    public void setLayoutPanel3(PanelLayout pl) {
        this.layoutPanel3 = pl;
    }

    private PanelGroup groupPanel3 = new PanelGroup();

    public PanelGroup getGroupPanel3() {
        return groupPanel3;
    }

    public void setGroupPanel3(PanelGroup pg) {
        this.groupPanel3 = pg;
    }

    private HtmlCommandButton button3 = new HtmlCommandButton();

    public HtmlCommandButton getButton3() {
        return button3;
    }

    public void setButton3(HtmlCommandButton hcb) {
        this.button3 = hcb;
    }

    private HtmlDataTable dataTable1 = new HtmlDataTable();

    public HtmlDataTable getDataTable1() {
        return dataTable1;
    }

    public void setDataTable1(HtmlDataTable hdt) {
        this.dataTable1 = hdt;
    }

    private UIColumn column1 = new UIColumn();

    public UIColumn getColumn1() {
        return column1;
    }

    public void setColumn1(UIColumn uic) {
        this.column1 = uic;
    }

    private HtmlOutputText outputText3 = new HtmlOutputText();

    public HtmlOutputText getOutputText3() {
        return outputText3;
    }

    public void setOutputText3(HtmlOutputText hot) {
        this.outputText3 = hot;
    }

    private HtmlOutputLink hyperlink2 = new HtmlOutputLink();

    public HtmlOutputLink getHyperlink2() {
        return hyperlink2;
    }

    public void setHyperlink2(HtmlOutputLink hol) {
        this.hyperlink2 = hol;
    }

    private HtmlOutputText hyperlink2Text1 = new HtmlOutputText();

    public HtmlOutputText getHyperlink2Text1() {
        return hyperlink2Text1;
    }

    public void setHyperlink2Text1(HtmlOutputText hot) {
        this.hyperlink2Text1 = hot;
    }

    private UIColumn column2 = new UIColumn();

    public UIColumn getColumn2() {
        return column2;
    }

    public void setColumn2(UIColumn uic) {
        this.column2 = uic;
    }

    private HtmlOutputText outputText4 = new HtmlOutputText();

    public HtmlOutputText getOutputText4() {
        return outputText4;
    }

    public IndexServiceLocal getIndexService() {
        return indexService;
    }

    public StudyFieldServiceLocal getStudyFieldService() {
        return studyFieldService;
    }

    public void setOutputText4(HtmlOutputText hot) {
        this.outputText4 = hot;
    }
    
    private HtmlSelectBooleanCheckbox producerCheckbox = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getProducerCheckbox() {
        return producerCheckbox;
    }

    public void setProducerCheckbox(HtmlSelectBooleanCheckbox hsbc) {
        this.producerCheckbox = hsbc;
    }

    private UIColumn column3 = new UIColumn();

    public UIColumn getColumn3() {
        return column3;
    }

    public void setColumn3(UIColumn uic) {
        this.column3 = uic;
    }

    private HtmlSelectBooleanCheckbox distributionDateCheckbox = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getDistributionDateCheckbox() {
        return distributionDateCheckbox;
    }

    public void setDistributionDateCheckbox(HtmlSelectBooleanCheckbox hsbc) {
        this.distributionDateCheckbox = hsbc;
    }

    private HtmlOutputText outputText5 = new HtmlOutputText();

    public HtmlOutputText getOutputText5() {
        return outputText5;
    }

    public void setOutputText5(HtmlOutputText hot) {
        this.outputText5 = hot;
    }

    private UIColumn column4 = new UIColumn();

    public UIColumn getColumn4() {
        return column4;
    }

    public void setColumn4(UIColumn uic) {
        this.column4 = uic;
    }

    private HtmlOutputText outputText6 = new HtmlOutputText();

    public HtmlOutputText getOutputText6() {
        return outputText6;
    }

    public void setOutputText6(HtmlOutputText hot) {
        this.outputText6 = hot;
    }

    private HtmlSelectBooleanCheckbox distributorCheckbox = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getDistributorCheckbox() {
        return distributorCheckbox;
    }

    public void setDistributorCheckbox(HtmlSelectBooleanCheckbox hsbc) {
        this.distributorCheckbox = hsbc;
    }

    private HtmlSelectBooleanCheckbox replicationCheckbox = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getReplicationCheckbox() {
        return replicationCheckbox;
    }

    public void setReplicationCheckbox(HtmlSelectBooleanCheckbox hsbc) {
        this.replicationCheckbox = hsbc;
    }

    private HtmlSelectBooleanCheckbox relatedpubCheckbox = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getRelatedpubCheckbox() {
        return relatedpubCheckbox;
    }

    public void setRelatedpubCheckbox(HtmlSelectBooleanCheckbox hsbc) {
        this.relatedpubCheckbox = hsbc;
    }

    private HtmlSelectBooleanCheckbox relatedmatCheckbox = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getRelatedmatCheckbox() {
        return relatedmatCheckbox;
    }

    public void setRelatedmatCheckbox(HtmlSelectBooleanCheckbox hsbc) {
        this.relatedmatCheckbox = hsbc;
    }

    private HtmlSelectBooleanCheckbox relatedstudiesCheckbox = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getRelatedstudiesCheckbox() {
        return relatedstudiesCheckbox;
    }

    public void setRelatedstudiesCheckbox(HtmlSelectBooleanCheckbox hsbc) {
        this.relatedstudiesCheckbox = hsbc;
    }

    private PanelGroup groupPanel5 = new PanelGroup();

    public PanelGroup getGroupPanel5() {
        return groupPanel5;
    }

    public void setGroupPanel5(PanelGroup pg) {
        this.groupPanel5 = pg;
    }

    private HtmlCommandButton button4 = new HtmlCommandButton();

    public HtmlCommandButton getButton4() {
        return button4;
    }

    public void setButton4(HtmlCommandButton hcb) {
        this.button4 = hcb;
    }

    private HelpInline helpInline1 = new HelpInline();

    public HelpInline getHelpInline1() {
        return helpInline1;
    }

    public void setHelpInline1(HelpInline hi) {
        this.helpInline1 = hi;
    }
    
    // </editor-fold>


    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public SearchFieldsPage() {
    }



    /** 
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    public void preprocess() {
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
    
    /*
    public List <StudyField> getStudyFields(){
        List studyFields = studyFieldService.findAll();
        return studyFields;
    }
     */
    
    public DataModel getStudyFields(){
        VDC thisVDC = vdcService.find(new Long(1));
        Collection resultFields = thisVDC.getSearchResultFields();
        Collection advSearchFields = thisVDC.getAdvSearchFields();
        boolean searchResultsFieldsEmpty = resultFields.isEmpty();
        List displayFields = new ArrayList();
        List postgresStudyFields = studyFieldService.findAll();
        for (Iterator it = postgresStudyFields.iterator(); it.hasNext();) {
            StudyField elem = (StudyField) it.next();
            DisplayStudyField displayField = new DisplayStudyField();
            displayField.setName(elem.getName());
            if (searchResultsFieldsEmpty){
                displayField.setDisplayAdvancedSearch(elem.isAdvancedSearchField());
                displayField.setDisplaySearchResults(elem.isSearchResultField());
            }
            else{
                displayField.setDisplayAdvancedSearch(searchCollectionForStudyField(advSearchFields, elem));
                displayField.setDisplaySearchResults(searchCollectionForStudyField(resultFields, elem));
            }
            displayField.setDisplaySearchResultsDisabled(elem.isSearchResultField());
            displayFields.add(displayField);
            
        }
        studyFields = new ListDataModel(displayFields);
        return studyFields;
    }
    
    private boolean searchCollectionForStudyField(Collection collection, StudyField sf){
        boolean retVal = false;
        for (Iterator it = collection.iterator(); it.hasNext();) {
            StudyField elem = (StudyField) it.next();
            if (elem.getId().equals(sf.getId())){
                retVal = true;
            }
        }
        return retVal;
    }
            
    public String update(){
        ArrayList advancedSearchFields = new ArrayList();
        ArrayList searchResultsFields = new ArrayList();
        List fields = (List<DisplayStudyField>) studyFields.getWrappedData();
        for (Iterator it = fields.iterator(); it.hasNext();) {
            DisplayStudyField elem = (DisplayStudyField) it.next();
            if (elem.isDisplayAdvancedSearch()){
                StudyField advancedSearchField = studyFieldService.findByName(elem.getName());
                advancedSearchFields.add(advancedSearchField);
            }
            if (elem.isDisplaySearchResults()){
                StudyField searchResultsField = studyFieldService.findByName(elem.getName());
                searchResultsFields.add(searchResultsField);
            }
            
        }
//        VDC thisVDC = vdcService.find(new Long(1));
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        thisVDC.setAdvSearchFields(advancedSearchFields);
        thisVDC.setSearchResultFields(searchResultsFields);
        vdcService.edit(thisVDC);
        
        return "success";
    }
    
    public String save(){
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        List <StudyField> newSearchResultsFields = getDefaultSearchResultsFields();
        if (producerCheckbox.isSelected()){
            StudyField producerResultsField = studyFieldService.findByName("producerName");
            newSearchResultsFields.add(producerResultsField);
        }
        if (distributionDateCheckbox.isSelected()){
            StudyField distributionDateResultsField = studyFieldService.findByName("distributionDate");
            newSearchResultsFields.add(distributionDateResultsField);
        }
        if (distributorCheckbox.isSelected()){
            StudyField distributorResultsField = studyFieldService.findByName("distributorName");
            newSearchResultsFields.add(distributorResultsField);
        }
        if (replicationCheckbox.isSelected()){
            StudyField replicationResultsField = studyFieldService.findByName("replicationFor");
            newSearchResultsFields.add(replicationResultsField);
        }
        if (relatedpubCheckbox.isSelected()){
            StudyField relatedpubResultsField = studyFieldService.findByName("relatedPublications");
            newSearchResultsFields.add(relatedpubResultsField);
        }
        if (relatedmatCheckbox.isSelected()){
            StudyField relatedmatResultsField = studyFieldService.findByName("relatedMaterial");
            newSearchResultsFields.add(relatedmatResultsField);
        }
        if (relatedstudiesCheckbox.isSelected()){
            StudyField relatedstudiesResultsField = studyFieldService.findByName("relatedStudies");
            newSearchResultsFields.add(relatedstudiesResultsField);
        }
        if (!newSearchResultsFields.isEmpty()){
            thisVDC.setSearchResultFields(newSearchResultsFields);
            vdcService.edit(thisVDC);
        }
        StatusMessage newMsg = new StatusMessage();
        String msgText;
        msgText = "Search Results updated.";
        newMsg.setStyleClass("successMessage");
        newMsg.setMessageText(msgText);
        Map m = getRequestMap();
        m.put("statusMessage",newMsg);
        msg = newMsg;
        return "searchFields";
    }
    
    private List <StudyField> getDefaultSearchResultsFields(){
        ArrayList searchResultsFields = new ArrayList();
        List allStudyFields = studyFieldService.findAll();
        for (Iterator it = allStudyFields.iterator(); it.hasNext();) {
            StudyField elem = (StudyField) it.next();
            if (elem.isSearchResultField()){
                searchResultsFields.add(elem);
            }
        }
        return searchResultsFields;        
    }
    
    public String cancel(){
//s        indexService.indexAll();
        return "myOptions";
    }
    
}

