/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web;


import edu.harvard.iq.dvn.core.study.DataTable;
import edu.harvard.iq.dvn.core.visualization.VarGroup;
import edu.harvard.iq.dvn.core.visualization.VarGroupType;
import edu.harvard.iq.dvn.core.visualization.VarGrouping;
import edu.harvard.iq.dvn.core.visualization.VarGrouping.GroupingType;
import edu.harvard.iq.dvn.core.visualization.VisualizationServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import com.icesoft.faces.component.panelseries.PanelSeries;

import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlSelectBooleanCheckbox;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.context.Resource;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.visualization.DataVariableMapping;
import edu.harvard.iq.dvn.core.web.study.StudyUI;
import edu.harvard.iq.dvn.core.web.util.UrlValidator;
import edu.harvard.iq.dvn.ingest.dsb.FieldCutter;
import edu.harvard.iq.dvn.ingest.dsb.impl.DvnJavaFieldCutter;
import java.awt.image.BufferedImage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;




/**
 *
 * @author skraffmiller
 */
public class ExploreDataPage extends VDCBaseBean  implements Serializable {
    @EJB
    VariableServiceLocal varService;
    @EJB
    VisualizationServiceLocal      visualizationService;

    private String measureLabel;
    private String lineLabel;

    private String lineColor;
    private String dataTableId = "";
    private List <VarGrouping> varGroupings = new ArrayList();
    private List <VarGroupingUI> filterGroupings = new ArrayList();

    private List <String> filterStrings = new ArrayList();
    private List <SelectItem> selectMeasureItems = new ArrayList();
    private List <SelectItem> selectMeasureGroupTypes = new ArrayList();
    private List <SelectItem> selectBeginYears = new ArrayList();
    private List <SelectItem> selectIndexDate = new ArrayList();

    private List <SelectItem> selectEndYears = new ArrayList();    
    private List <SelectItem> selectView = new ArrayList();

    public List<SelectItem> getSelectView() {
        return selectView;
    }
    
    private List <VisualizationLineDefinition> vizLines = new ArrayList();
    private Long selectedMeasureId = new Long(0);
    private Long selectedFilterGroupId = new Long(0);
    private int groupTypeId = 0;
    private List <DataVariable> dvList;
    private List <DataVariableMapping> sourceList = new ArrayList();;
    private List <String> measureString= new ArrayList();
    private DataVariable xAxisVar;
    private DataTable dt = new DataTable();
    private DataVariable dataVariableSelected = new DataVariable();
    private String dtColumnString = new String();
    private String columnString = new String();
    private String csvColumnString = new String();
    private String imageColumnString = new String();
    
    private List <VarGroup> allMeasureGroups = new ArrayList();
    private List <VarGroup> allFilterGroups = new ArrayList();

    private Long numberOfColumns =  new Long(0);
    private String dataString = "";
    private String indexedDataString = "";
    private String csvString = "";
    private String indexDate = "";
    private String sources = "";
    private String imageSourceFooter = "";
    private String imageSourceFooterNoYLabel = "";
    private String displaySourceFooter = "";
    private String displaySourceFooterNoYLabel = "";
    private String imageAxisLabel = "";
    private String imageAxisLabelNoYLabel = "";   


    private boolean displayIndexes = false;
    private boolean includeImage = true;
    private boolean includePdf = true;
    private boolean includeExcel = true;
    private boolean includeCSV = true;
    private boolean imageAvailable = false;

    private boolean dataTableAvailable = false;           
    private boolean showFilterGroupTypes = false;

    
    private boolean titleEdited = false;
    private Long displayType = new Long(0);
    private String startYear = new String("0");
    private String endYear = new String("3000");
    private Study studyIn = new Study();
    private StudyUI studyUI;
    private String fileName = "";
    private String graphTitle = "";
    private String imageURL = "";
    private Long studyId = new Long(0);
    private Long versionNumber;
    private List filterGroupingMeasureAssociation = new ArrayList();
    private List groupingTypeAssociation = new ArrayList();
    private List filterGroupMeasureAssociation = new ArrayList();
    private List <VarGrouping> allVarGroupings = new ArrayList();
    private boolean showVariableInfoPopup = false;
    private String variableLabel = "";
    private boolean variableLabelLink = false;
    private String xAxisLabel = "";
    private String sourceLineLabel = "";
    private Long studyFileId;
    private String yAxisLabel = "";
    private boolean displayLegend = true;
    private int legendInt = 1;    
    private Integer defaultView = new Integer(2);



    public ExploreDataPage() {
        
    }

    @Override
    public void init() {
        super.init();        
        studyFileId = new Long( getVDCRequestBean().getRequestParam("fileId"));
        setUp();
     }
    
    private void setUp(){
        
        legendInt = 1;
        visualizationService.setDataTableFromStudyFileId(studyFileId);
        studyIn = visualizationService.getStudyFromStudyFileId(studyFileId);
        studyId = studyIn.getId();
        versionNumber = getVDCRequestBean().getStudyVersionNumber();
        dt = visualizationService.getDataTable();
        Study thisStudy = dt.getStudyFile().getStudy();
        studyUI = new StudyUI(thisStudy);
        StudyFile sf = dt.getStudyFile();
        fileName = sf.getFileName();
        dvList = dt.getDataVariables();
        allVarGroupings = dt.getVarGroupings();
        
        measureLabel = loadMeasureLabel();              
        selectMeasureGroupTypes = loadSelectMeasureGroupTypes();        
        selectMeasureItems = loadSelectMeasureItems(0);
        
        selectView = loadSelectViewItems();
        loadAllFilterGroupings();
        loadAllFilterGroupTypes();
        loadAllMeasureGroups();
        loadAllFilterGroups();
        if (!dt.getVisualizationDisplay().getSourceInfoLabel().isEmpty()){
            setSourceLineLabel(dt.getVisualizationDisplay().getSourceInfoLabel());
        } else {
            setSourceLineLabel("Source Info"); 
        }
        xAxisVar =  visualizationService.getXAxisVariable(dt.getId());
        for (DataVariableMapping mapping : xAxisVar.getDataVariableMappings()){
             if (mapping.isX_axis())xAxisLabel = mapping.getLabel();
        }
        
    }
    
    private void reInit(){
        setUp();
    }
    
    private void refreshSettings(){        
        titleEdited = false;
        displayType = new Long(0);
        startYear = "0";
        endYear = "3000";
        graphTitle = "";
        imageURL = "";
        showVariableInfoPopup = false;
        variableLabel = "";
        variableLabelLink = false;
        xAxisLabel = "";
        sourceLineLabel = "";
        yAxisLabel = "";
        displayLegend = true;
        legendInt = 1; 
        setDisplayIndexes(false);
        selectedMeasureId = new Long (0);
        lineLabel = "";
    }



    public List getVarGroupings() {
        return varGroupings;
    }

    public void setVarGroupings(List varGroupings) {
        this.varGroupings = varGroupings;
    }

    public List<VarGroupingUI> getFilterGroupings() {
        return filterGroupings;
    }

    public void setFilterGroupings(List<VarGroupingUI> filterGroupings) {
        this.filterGroupings = filterGroupings;
    }

    protected PanelSeries filterPanelGroup;

    public PanelSeries getFilterPanelGroup() {
        return filterPanelGroup;
    }

    public void setFilterPanelGroup(PanelSeries filterPanelGroup) {
        this.filterPanelGroup = filterPanelGroup;
    }

    public String loadMeasureLabel() {
        Iterator iterator = allVarGroupings.iterator();
        while (iterator.hasNext() ){
            VarGrouping varGrouping = (VarGrouping) iterator.next();

            if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)){
                return varGrouping.getName();
            }

        }
        return "Measure";
    }

    private void loadFilterGroupings(){
        filterStrings.clear();
        filterGroupings.clear();
        List <VarGrouping> localVGList = new ArrayList();

        Iterator i = filterGroupingMeasureAssociation.listIterator();
        int count = 0;
        while (i.hasNext()){
            Object test = i.next();
            if ( count % 2 == 0 ){
                Long id = (Long) test;
                if (id.equals(selectedMeasureId)){
                    localVGList.add((VarGrouping) i.next());
                    count++;
                }
            }
            count++;
        }


        for (VarGrouping varGrouping: localVGList){

            if (varGrouping.getGroupingType().equals(GroupingType.FILTER)){
                List <VarGroup> filterGroups = new ArrayList();
                List <VarGroupTypeUI> filterGroupTypes = new ArrayList();
                VarGroupingUI vgUI = new VarGroupingUI();
                vgUI.setVarGrouping(varGrouping);
                loadFilterGroups(filterGroups);
                loadFilterGroupTypes(filterGroupTypes, varGrouping.getId());
                vgUI.setVarGroupTypesUI(filterGroupTypes);
                filterGroupings.add(vgUI);
            }

        }
    }

    private void loadAllFilterGroupings(){
        filterGroupingMeasureAssociation.clear();
        
        for (VarGrouping varGroupingTest: allVarGroupings){
            if (varGroupingTest.getGroupingType().equals(GroupingType.MEASURE)){
                for (VarGroup varGroup : varGroupingTest.getVarGroups() ){
                     List <VarGrouping> varGroupingsBack = visualizationService.getFilterGroupingsFromMeasureId(varGroup.getId());
                     for(VarGrouping varGroupingBack: varGroupingsBack){
                         filterGroupingMeasureAssociation.add(varGroup.getId());
                         filterGroupingMeasureAssociation.add(varGroupingBack);
                     }
                     List <VarGroup> varGroupsBack = visualizationService.getFilterGroupsFromMeasureId(varGroup.getId());
                     for(VarGroup varGroupBack: varGroupsBack){
                         filterGroupMeasureAssociation.add(varGroup.getId());
                         filterGroupMeasureAssociation.add(varGroupBack);
                     }
                }
            }

        }
    }
    
    private void loadAllFilterGroups(){

        allFilterGroups.clear();
        for (VarGrouping varGroupingTest: allVarGroupings){
            if (varGroupingTest.getGroupingType().equals(GroupingType.FILTER)){
                for (VarGroup varGroup : varGroupingTest.getVarGroups() ){
                    allFilterGroups.add(varGroup);
                }
            }

        }
    }
    
    private void loadAllMeasureGroups(){
        allMeasureGroups.clear();
        
        for (VarGrouping varGroupingTest: allVarGroupings){
            if (varGroupingTest.getGroupingType().equals(GroupingType.MEASURE)){
                for (VarGroup varGroup : varGroupingTest.getVarGroups() ){
                    allMeasureGroups.add(varGroup);
                }
            }

        }
    }

    private void loadAllFilterGroupTypes(){


        for (VarGrouping varGroupingTest: allVarGroupings){
            if (varGroupingTest.getGroupingType().equals(GroupingType.FILTER)){
                     List <VarGroupType> varGroupTypesBack = visualizationService.getGroupTypesFromGroupingId(varGroupingTest.getId());

                     for(VarGroupType varGroupTypeBack: varGroupTypesBack){
                         groupingTypeAssociation.add(varGroupingTest.getId());
                         groupingTypeAssociation.add(varGroupTypeBack);
                     }

            }

        }
    }


    private void loadFilterGroups(List <VarGroup> inList){
        inList.clear();
         List <VarGroup> localVGList = new ArrayList();

        Iterator i = filterGroupMeasureAssociation.listIterator();
        int count = 0;
        while (i.hasNext()){
            Object test = i.next();
            if ( count % 2 == 0 ){
                Long id = (Long) test;
                if (id.equals(selectedMeasureId)){
                    localVGList.add((VarGroup) i.next());
                    count++;
                }
            }

            count++;
        }

        for (VarGroup varGroup: localVGList ){
            inList.add(varGroup);
        }
    }

    private List <VarGroup> getFilterGroupsFromMeasureId(Long MeasureId){
        List returnList = new ArrayList();
         List <VarGroup> localVGList = new ArrayList();

        Iterator i = filterGroupMeasureAssociation.listIterator();
        int count = 0;
        while (i.hasNext()){
            Object test = i.next();
            if ( count % 2 == 0 ){
                Long id = (Long) test;
                if (id.equals(MeasureId)){
                    localVGList.add((VarGroup) i.next());
                    count++;
                }
            }

            count++;
        }

        for (VarGroup varGroup: localVGList ){
            returnList.add(varGroup);
        }

       return returnList;
    }


    private void loadFilterGroupTypes(List <VarGroupTypeUI> inList, Long groupingId){
        

        List <VarGroupType> localVGList = new ArrayList();

        Iterator i = groupingTypeAssociation.listIterator();
        int count = 0;
        while (i.hasNext()){
            Object test = i.next();
            if ( count % 2 == 0 ){
                Long id = (Long) test;
                if (id.equals(groupingId)){
                    localVGList.add((VarGroupType) i.next());
                    count++;
                }
            }

            count++;
        }
        
        List <VarGroup> varGroupsAll = (List<VarGroup>)  getFilterGroupsFromMeasureId(selectedMeasureId);
            List <VarGroupType> varGroupTypesAll = new ArrayList();
            for (VarGroup varGroupFilter:varGroupsAll){
                ;
                for (VarGroupType vgt: varGroupFilter.getGroupTypes()){
                    varGroupTypesAll.add(vgt);
                }
            }


        for (VarGroupType varGroupType: localVGList ){
            if (varGroupTypesAll.contains(varGroupType) ){
                VarGroupTypeUI varGroupTypeUI = new VarGroupTypeUI();
                varGroupTypeUI.setVarGroupType(varGroupType);
                varGroupTypeUI.setEnabled(false);
                inList.add(varGroupTypeUI);
            }
        }
    }


    public void setMeasureLabel(String measureLabel) {
        this.measureLabel = measureLabel;
    }

    public String getMeasureLabel() {
        return measureLabel;
    }

    public List<String> getFilterStrings() {
        return filterStrings;
    }

    public void setFilterStrings(List<String> filterStrings) {
        this.filterStrings = filterStrings;
    }

    public List<String> getMeasureString() {
        return measureString;
    }

    public void setMeasureString(List<String> measureString) {
        this.measureString = measureString;
    }

    public List<SelectItem> getSelectMeasureItems() {
        return this.selectMeasureItems;
    }

    public void resetFiltersForMeasure(ValueChangeEvent ae){
        selectedMeasureId = (Long) ae.getNewValue();
        
        if (selectedMeasureId == null ){
            selectedMeasureId = new Long(0);
        }
        if (selectedMeasureId != null) {
            loadFilterGroupings();

            
        }
       
    }
    
    
    public void updateLineLabelForFilter(ValueChangeEvent ae){
       Long filterId =  (Long) ae.getNewValue();
       
       String tmpLineLabel = "";          
       if (filterId > 0){
           tmpLineLabel += getFilterGroupFromId(filterId).getName();    
       }       
       getInputLineLabel().setValue(tmpLineLabel);                      
    }
    
    public List<SelectItem> loadSelectMeasureItems(int grouptype_id) {
        boolean resetSelected = true;

        List selectItems = new ArrayList<SelectItem>();
        List <VarGroup> varGroups = new ArrayList();
        varGroupings = dt.getVarGroupings();
        Iterator iterator = varGroupings.iterator();
        while (iterator.hasNext() ){
            VarGrouping varGrouping = (VarGrouping) iterator.next();
            if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)){

                if (grouptype_id == 0) {
                    varGroups = (List<VarGroup>) varGrouping.getVarGroups();
                } else {
                    List <VarGroup> varGroupsTest = (List<VarGroup>) varGrouping.getVarGroups();
                    for (VarGroup vgTest: varGroupsTest){
                       List <VarGroupType> varGroupTypes = vgTest.getGroupTypes();
                       for (VarGroupType vgTypeTest: varGroupTypes ){
                           if(vgTypeTest.getId().intValue() == grouptype_id ){
                               varGroups.add(vgTest);
                           }
                       }
                    }
                }
 
                for(VarGroup varGroup: varGroups) {
                    boolean added = false;
                    Long testId  = varGroup.getId();
                    for (Object testItem:  selectItems){
                         SelectItem testListSI = (SelectItem) testItem;
                         if(testId.equals(testListSI.getValue())){
                             added = true;
                         }
                    }
                    if (!added ){
                        selectItems.add(new SelectItem(varGroup.getId(), varGroup.getName()));
                        if (varGroup.getId().equals(selectedMeasureId)){
                            resetSelected = false;
                        }
                        added = true;
                    }

                }
            }
        }
        if (resetSelected){
            selectedMeasureId = new Long(0);
        }
        return selectItems;
    }

    public List<SelectItem> loadSelectViewItems() {
        defaultView = 0;
        boolean image = false;
        boolean flash = false;
        boolean datatable = false;
        
        List selectItems = new ArrayList<SelectItem>();


        
        int defaultViewDisplay = dt.getVisualizationDisplay().getDefaultDisplay();
        

        switch (defaultViewDisplay) {
            case 0:  defaultView = 2;  
                        selectItems.add(new SelectItem(2, "Image Graph"));
                        imageAvailable = true;
                        image = true;
                        break;
            case 1:  defaultView = 1;   
                        selectItems.add(new SelectItem(1, "Flash Graph"));  
                        flash = true;
                        break;
            case 2:  defaultView = 3;   
                        selectItems.add(new SelectItem(3, "Data Table"));
                        dataTableAvailable = true; 
                        datatable = true;
                        break;
            default:    break;
        }
        
        if ( !image && dt.getVisualizationDisplay().isShowImageGraph()) {
            selectItems.add(new SelectItem(2, "Image Graph"));
            imageAvailable = true;
        }
        if (!flash && dt.getVisualizationDisplay().isShowFlashGraph()) {
            selectItems.add(new SelectItem(1, "Flash Graph"));  
        }
        if (!datatable && dt.getVisualizationDisplay().isShowDataTable()) {
            selectItems.add(new SelectItem(3, "Data Table"));
            dataTableAvailable = true;  
        }

        if (!dt.getVisualizationDisplay().isShowImageGraph()) {
            includeImage = false;
            includePdf = false;
        }

        if (!dt.getVisualizationDisplay().isShowDataTable()) {
            includeExcel = false;
        }
        
        return selectItems;
    }
    
    public List<SelectItem> loadSelectMeasureGroupTypes() {
        boolean allAdded = false;
        List selectItems = new ArrayList<SelectItem>();
        for(VarGrouping varGrouping: allVarGroupings) {           
            if (varGrouping.getGroupingType().equals(GroupingType.MEASURE)){
                List <VarGroupType> varGroupTypes = (List<VarGroupType>) varGrouping.getVarGroupTypes();
                for(VarGroupType varGroupType: varGroupTypes) {
                    if(!allAdded){
                        selectItems.add(new SelectItem(0, "All"));
                        allAdded = true;
                    }
                    if (varGroupType.getGroups().size() > 0) {
                        selectItems.add(new SelectItem(varGroupType.getId(), varGroupType.getName()));
                    }
                }
            }
        }
        return selectItems;
    }

    public List<SelectItem> loadSelectFilterGroupTypes() {
        List selectItems = new ArrayList<SelectItem>();

        Iterator iterator = varGroupings.iterator();
        while (iterator.hasNext() ){
            VarGrouping varGrouping = (VarGrouping) iterator.next();
            if (varGrouping.getGroupingType().equals(GroupingType.FILTER)){
                List <VarGroupType> varGroupTypes = (List<VarGroupType>) varGrouping.getVarGroupTypes();
                for(VarGroupType varGroupType: varGroupTypes) {
                    selectItems.add(new SelectItem(varGroupType.getId(), varGroupType.getName()));
                }
            }
        }
        return selectItems;
    }
    
    public List<SelectItem> getSelectFilterGroups() {
        if (!selectedMeasureId.equals(new Long(0))){
          
            return getFilterGroupsWithMeasure();
        }
        return null;
    }


    public List<SelectItem> getSelectFilterGroupTypes() {
        Long groupingId = new Long(0);
        showFilterGroupTypes = false;
        List selectItems = new ArrayList<SelectItem>();
        if (!selectedMeasureId.equals(new Long(0))){
            groupingId = new Long(filterPanelGroup.getAttributes().get("groupingId").toString());

            List <VarGroup> varGroupsAll = (List<VarGroup>)  getFilterGroupsFromMeasureId(selectedMeasureId);
            List <VarGroupType> varGroupTypesAll = new ArrayList();
            for (VarGroup varGroupFilter:varGroupsAll){
                ;
                for (VarGroupType vgt: varGroupFilter.getGroupTypes()){
                    varGroupTypesAll.add(vgt);
                }
            }
            
            List <VarGroupType> varGroupTypes = (List<VarGroupType>)  visualizationService.getGroupTypesFromGroupingId(new Long(groupingId));
                for(VarGroupType varGroupType: varGroupTypes) {
                    if (varGroupTypesAll.contains(varGroupType) ){
                        selectItems.add(new SelectItem(varGroupType.getId(), varGroupType.getName()));
                        showFilterGroupTypes = true;
                    }
                    
                }
            
        }
        return selectItems;
    }

    public List<VarGroupType> loadFilterGroupTypes() {
        Long groupingId = new Long(0);

        List selectItems = new ArrayList<VarGroupType>();

                List <VarGroupType> varGroupTypes = (List<VarGroupType>)  visualizationService.getGroupTypesFromGroupingId(new Long(groupingId));
                for(VarGroupType varGroupType: varGroupTypes) {
                    selectItems.add(new SelectItem(varGroupType.getId(), varGroupType.getName()));
                }

        return selectItems;
    }


    private List<SelectItem> getFilterGroupsWithMeasure(){

         List selectItems = new ArrayList<SelectItem>();
          Long groupingId = new Long(filterPanelGroup.getAttributes().get("groupingId").toString());
          boolean addAll = false;
          List <VarGroup> multipleSelections = new ArrayList <VarGroup>();
          List <VarGroup> varGroupsAll = (List<VarGroup>)  getFilterGroupsFromMeasureId(selectedMeasureId);
              for(VarGroup varGroup: varGroupsAll) {
                  boolean added = false;
                  for (VarGroupingUI varGroupingUI :filterGroupings){
                      if (varGroupingUI.getVarGrouping().getId().equals(groupingId)){
                        List <VarGroupTypeUI> varGroupTypesUI = varGroupingUI.getVarGroupTypesUI();
                        if (!varGroupTypesUI.isEmpty()){
                            boolean allFalse = true;
                            for (VarGroupTypeUI varGroupTypeUI: varGroupTypesUI){
                                allFalse &= !varGroupTypeUI.isEnabled();
                            }
                            if (allFalse){
                                
                                       for (VarGroup varGroupTest: varGroupsAll) {
                                            if (!added && varGroupTest.getId().equals(varGroup.getId())  && varGroup.getGroupAssociation().equals(varGroupingUI.getVarGrouping()) ){
                                                selectItems.add(new SelectItem(varGroup.getId(), varGroup.getName()));
                                                added = true;
                                            }
                                        }


                            }
                            if (!allFalse){
                                int countEnabled = 0;
                                for (VarGroupTypeUI varGroupTypeUI: varGroupTypesUI){
                                    if (varGroupTypeUI.isEnabled()){
                                        countEnabled++;
                                    }
                                }
                                if( countEnabled == 1){
                                    for (VarGroupTypeUI varGroupTypeUI: varGroupTypesUI){
                                    if (varGroupTypeUI.isEnabled()){
                                        List <VarGroup> varGroups = (List) varGroupTypeUI.getVarGroupType().getGroups();
                                            for (VarGroup varGroupTest: varGroups) {
                                                if (!added && varGroupTest.getId().equals(varGroup.getId())  && varGroup.getGroupAssociation().equals(varGroupingUI.getVarGrouping()) ){
                                                    selectItems.add(new SelectItem(varGroup.getId(), varGroup.getName()));
                                                    added = true;
                                                }
                                            }

                                        }
                                      }
                                }
                                if( countEnabled > 1){
                                    int counter = countEnabled;
                                    List[] varGroupArrayList = new  List [countEnabled];

                                    
                                    for (VarGroupTypeUI varGroupTypeUI: varGroupTypesUI){
                                    if (varGroupTypeUI.isEnabled()){
                                        List <VarGroup> varGroups = (List) varGroupTypeUI.getVarGroupType().getGroups();
                                        varGroupArrayList[counter-1] = varGroups;
                                        counter--;
                                        }
                                    }
                                    List <VarGroup> varGroupsSaveGet = new ArrayList((List) varGroupArrayList[0]);
                                    List <VarGroup> varGroupsTempGet = new ArrayList((List) varGroupArrayList[0]);
                                    List <VarGroup> varGroupsSave = new ArrayList(varGroupsSaveGet);
                                    List <VarGroup> varGroupsTemp = new ArrayList(varGroupsTempGet);
                                    List <VarGroup> vsrGroupRemove = new ArrayList();
                                    for (int i=1; i<=countEnabled-1; i++){
                                        List <VarGroup> varGroupsTest = (List) varGroupArrayList[i];
                                        for (VarGroup vgs: varGroupsTemp){
                                            boolean save = false;
                                            for(VarGroup vgt : varGroupsTest){
                                               if (vgt.getId().equals(vgs.getId())){
                                                   save=true;
                                               }
                                                
                                            }
                                            if (!save){
                                                vsrGroupRemove.add(vgs);
                                            }
                                        }

                                        
                                    }


                                       for(VarGroup vgr : vsrGroupRemove){
                                            varGroupsSave.remove(vgr);
                                       }
                                       multipleSelections = varGroupsSave;
                                    
                                }                                
                            }


                        } else {
                               if (!added && varGroup.getGroupAssociation().equals(varGroupingUI.getVarGrouping()) ){
                                    selectItems.add(new SelectItem(varGroup.getId(), varGroup.getName()));
                                    added = true;
                               }
                        }
                      }
                  }                   
              }
              if (multipleSelections.size()>0){
                  for (VarGroup vgs: multipleSelections){
                      selectItems.add(new SelectItem(vgs.getId(), vgs.getName()));
                  }

              }
          return selectItems;

    }


    public List<SelectItem> getSelectMeasureGroupTypes() {

        return this.selectMeasureGroupTypes;
    }

    public void reset_DisplayType(){
        Object value= this.selectGraphType.getValue();
        this.displayType = new Long((String) value );
           FacesContext fc = FacesContext.getCurrentInstance();
           JavascriptContext.addJavascriptCall(fc, "drawVisualization();");
           JavascriptContext.addJavascriptCall(fc, "initLineDetails");
    }

    public void update_StartYear(){
        Object value= this.selectStartYear.getValue();
        this.startYear = (String) value ;
    }

    public void update_EndYear(){
        Object value= this.selectEndYear.getValue();
        this.endYear = (String) value ;
    }

    public void update_IndexYear(){
        Object value= this.selectIndexYear.getValue();
        this.indexDate= (String) value ;
        getDataTable();
        resetLineBorder();
    }

    public void updateUseIndex(){
        Object value= this.useIndicesCheckBox.getValue();

        this.displayIndexes = (Boolean) value ;
        FacesContext fc = FacesContext.getCurrentInstance();
        JavascriptContext.addJavascriptCall(fc, "drawVisualization();");
        JavascriptContext.addJavascriptCall(fc, "initLineDetails");
    }

    public void reset_MeasureItems(ValueChangeEvent ae){
        int i = (Integer) ae.getNewValue();
        this.selectMeasureItems = loadSelectMeasureItems(i);
        loadFilterGroupings();
    }
    
    private VarGroup getMeasureGroupFromId(Long Id) {
        for (VarGroup varGroupTest: allMeasureGroups){
            if (varGroupTest.getId().equals(Id)){
                return varGroupTest;
            }
        }
        return null;        
    }
    
    private VarGroup getFilterGroupFromId(Long Id) {
        
        for (VarGroup varGroupTest: allFilterGroups){
            if (varGroupTest.getId().equals(Id)){
                return varGroupTest;
            }
        }
        return null;
        
    }
    
    private void updateGraphTitleForMeasure(){
        String tmpLineLabel = "";       
        Set<String> set = new HashSet(); 
        for (VisualizationLineDefinition vld: vizLines){
            
            String checkName = vld.getMeasureGroup().getName();
            if (!checkName.isEmpty()){
                 if (tmpLineLabel.isEmpty()){
                        
                        tmpLineLabel = checkName;
                        set.add(checkName);
                 } else if (!set.contains(checkName)){
                     
                        tmpLineLabel += ", ";
                        tmpLineLabel += checkName;
                       set.add(checkName);
                 }
            }
        }

           setGraphTitle(tmpLineLabel);
           getInputGraphTitle().setValue(tmpLineLabel);                
    }

    public void reset_FilterItems(){
        
        this.getSelectFilterGroups();
    }

    public int getGroupTypeId() {
        return groupTypeId;
    }

    public void setGroupTypeId(int groupTypeId) {
        this.groupTypeId = groupTypeId;
    }


    public void addLine(ActionEvent ae){
        
        if ( lineLabel.isEmpty() || lineLabel.trim().equals("") ) {
            FacesMessage message = new FacesMessage("Please enter a label");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(addLineButton.getClientId(fc), message);
            return;
        }

        if (vizLines.size() >= 8){
            FacesMessage message = new FacesMessage("A maximum of 8 lines may be displayed in a single graph");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(addLineButton.getClientId(fc), message);
            return;
        }

        if (validateSelections()){
            for (VisualizationLineDefinition vl :vizLines)
                if (vl.getVariableId().equals(dataVariableSelected.getId())){
                   FacesMessage message = new FacesMessage("This data has already been selected");
                    FacesContext fc = FacesContext.getCurrentInstance();
                    fc.addMessage(addLineButton.getClientId(fc), message);
                    return;
                }
        }

        if (validateSelections()){
           setLegendInt(1);
           FacesContext.getCurrentInstance().renderResponse();
           VisualizationLineDefinition vizLine = new VisualizationLineDefinition();

           vizLine.setMeasureGroup(visualizationService.getGroupFromId(selectedMeasureId));

           List <VarGroup> selectedFilterGroups = new ArrayList();
           for(VarGroupingUI varGrouping: filterGroupings) {
               if (!(varGrouping.getSelectedGroupId() == 0)){
                    VarGroup filterGroup = visualizationService.getGroupFromId(varGrouping.getSelectedGroupId());
                    selectedFilterGroups.add(filterGroup);
               }
           }
           vizLine.setFilterGroups(selectedFilterGroups);
           vizLine.setLabel(lineLabel);
           vizLine.setMeasureLabel(measureLabel);
           vizLine.setColor(lineColor);
           vizLine.setBorder("border:1px solid black;");
           vizLine.setVariableId(dataVariableSelected.getId());
           vizLine.setVariableName(dataVariableSelected.getName());
           vizLine.setVariableLabel(dataVariableSelected.getLabel());
           
           vizLines.add(vizLine);
           this.numberOfColumns = new Long(vizLines.size());
           getDataTable();
           resetLineBorder();
           getSourceList();
           checkUnits();
           updateImageFooters();
           if (!titleEdited){
                updateGraphTitleForMeasure();
           }

           FacesContext fc = FacesContext.getCurrentInstance();
           JavascriptContext.addJavascriptCall(fc, "drawVisualization();");           
           JavascriptContext.addJavascriptCall(fc, "initLineDetails");

           return;
        } else {
            FacesContext fc = FacesContext.getCurrentInstance();
            JavascriptContext.addJavascriptCall(fc, "jQuery(\"div.dvnMsgBlockRound\").corner(\"10px\");");
        }

    }

    public void removeAllLines(ActionEvent ae){
    
        List <VisualizationLineDefinition> vizLinesRemove = new ArrayList();
        for (VisualizationLineDefinition vld: vizLines){
            vizLinesRemove.add(vld);
        }
        
        for (VisualizationLineDefinition vld: vizLinesRemove){
            vizLines.remove(vld);
        }        

        getDataTable();
        resetLineBorder();
        getSourceList();
        updateImageFooters();
        FacesContext fc = FacesContext.getCurrentInstance();
        JavascriptContext.addJavascriptCall(fc, "drawVisualization();");           
        JavascriptContext.addJavascriptCall(fc, "initLineDetails");
        refreshSettings();
    }
   
    private void resetLineBorder(){
        int i = 0;
        if (vizLines.size()>=1){
            for (VisualizationLineDefinition vl: vizLines){
                if (i==0){
                    vl.setBorder("border:1px solid #4684EE;");
                }
                if (i==1){
                    vl.setBorder("border:1px solid #DC3912;");
                }
                if (i==2){
                    vl.setBorder("border:1px solid #FF9900;");
                }
                if (i==3){
                    vl.setBorder("border:1px solid #008000;");
                }
                if (i==4){
                    vl.setBorder("border:1px solid #4942CC;");
                }
                if (i==5){
                    vl.setBorder("border:1px solid #990099;");
                }
                if (i==6){
                    vl.setBorder("border:1px solid #FF80F2;");
                }
                if (i==7){
                    vl.setBorder("border:1px solid #7FD127;");
                }
                i++;
            }
        }
    }

    public void deleteLine(ActionEvent ae){

        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VisualizationLineDefinition vizLine = (VisualizationLineDefinition) tempTable.getRowData();
        

        List <VisualizationLineDefinition> removeList = new ArrayList();

        List <VisualizationLineDefinition> tempList = new ArrayList(vizLines);
           for(VisualizationLineDefinition vizLineCheck: tempList){
                if (vizLineCheck.equals(vizLine)){
                    removeList.add(vizLineCheck);

                }
             }

           for(VisualizationLineDefinition vizLineRem : removeList){

                        vizLines.remove(vizLineRem);
           }
           
           this.numberOfColumns = new Long(vizLines.size());
           getDataTable();
           resetLineBorder();
           getSourceList();
           checkUnits();
           updateImageFooters();
           
           if (!titleEdited){
                updateGraphTitleForMeasure();
           }

           FacesContext fc = FacesContext.getCurrentInstance();
           JavascriptContext.addJavascriptCall(fc, "drawVisualization();");
           JavascriptContext.addJavascriptCall(fc, "jQuery(\"div.dvnMsgBlockRound\").corner(\"10px\");");

    }
    private boolean validateSelections(){
        boolean valid  = true;
        int count = 0;
        if (selectedMeasureId == 0){
            FacesMessage message = new FacesMessage("You must select a measure.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(addLineButton.getClientId(fc), message);
            return false;
        }


           
           List <DataVariable> resultList = new ArrayList();

           Iterator varIter = dvList.iterator();
           while (varIter.hasNext()) {
                DataVariable dv = (DataVariable) varIter.next();
                Collection <DataVariableMapping> dvMappings = dv.getDataVariableMappings();
                for(DataVariableMapping dvMapping:dvMappings ) {

                    if (!dvMapping.isX_axis() && dvMapping.getGroup().getId().equals(selectedMeasureId)){
                        resultList.add(dv);
                         count++;
                    }
                }
           }
           List <ArrayList> filterGroupingList = new ArrayList();

           List <DataVariable> resultListFilter = new ArrayList();

           for(VarGroupingUI varGrouping: filterGroupings) {
               if(varGrouping.getSelectedGroupId()!=0){
                   ArrayList <DataVariable> tempList = new ArrayList();
                   Iterator varIterb = resultList.iterator();
                   while (varIterb.hasNext()) {
                        DataVariable dv = (DataVariable) varIterb.next();
                        Collection <DataVariableMapping> dvMappings = dv.getDataVariableMappings();
                        for(DataVariableMapping dvMapping:dvMappings ) {
                            if (!dvMapping.isX_axis() && dvMapping.getGroup().getId().equals(varGrouping.getSelectedGroupId())){
                                resultListFilter.add(dv);
                                tempList.add(dv);
                                count++;
                            }
                        }
                   }
                   filterGroupingList.add(tempList);
               }
           }

           List <DataVariable> testCounts = resultListFilter;
           int maxCount = 0;

           for(DataVariable resultDV:resultListFilter ) {
                    int maxCountTest = 0;
                    for (DataVariable testDV:testCounts ){
                        if (resultDV.equals(testDV)){
                            maxCountTest++;
                        }
                    }

                    if (maxCountTest > maxCount) maxCount = maxCountTest;

            }

        List <DataVariable> finalList = new ArrayList();
        finalList = resultList;

        List <DataVariable> removeList = new ArrayList();
        for (DataVariable dv : finalList){
            boolean remove = true;
            for (ArrayList arrayList : filterGroupingList){
                for (Object dvO: arrayList){
                    DataVariable dvF = (DataVariable) dvO;
                        if (dvF.equals(dv)){
                            remove = false;
                        }
                }
                if (remove) removeList.add(dv);
            }
        }

        for(DataVariable dataVarRemove : removeList){
              finalList.remove(dataVarRemove);
        }


        if(finalList.size() == 1) {
            dataVariableSelected = finalList.get(0);


        } else {
            FacesMessage message = new FacesMessage("Your selections do not match any in the data table.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(addLineButton.getClientId(fc), message);
            return false;
        }



        if (!valid){
            FacesMessage message = new FacesMessage("You must select a filter for each group.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(addLineButton.getClientId(fc), message);

        }

        return valid;
    }

    public List<VisualizationLineDefinition> getVizLines() {
        return vizLines;
    }

    public void setVizLines(List<VisualizationLineDefinition> vizLines) {
        this.vizLines = vizLines;
    }
    public Long getSelectedFilterGroupId() {
        return selectedFilterGroupId;
    }

    public void setSelectedFilterGroupId(Long selectedFilterGroupId) {
        this.selectedFilterGroupId = selectedFilterGroupId;
    }

    public Long getSelectedMeasureId() {
        return selectedMeasureId;
    }

    public void setSelectedMeasureId(Long selectedMeasureId) {
        this.selectedMeasureId = selectedMeasureId;
    }

    public String getLineLabel() {
        return lineLabel;
    }

    public void setLineLabel(String lineLabel) {
        this.lineLabel = lineLabel;
    }
    public String getLineColor() {
        return lineColor;
    }

    public void setLineColor(String lineColor) {
        this.lineColor = lineColor;
    }
    
    public boolean isTitleEdited() {
        return titleEdited;
    }

    public void setTitleEdited(boolean titleEdited) {
        this.titleEdited = titleEdited;
    }

    public boolean isShowFilterGroupTypes() {
        return showFilterGroupTypes;
    }

    public void setShowFilterGroupTypes(boolean showFilterGroupTypes) {
        this.showFilterGroupTypes = showFilterGroupTypes;
    }
    
    public String getDisplaySourceFooter() {
        return displaySourceFooter;
    }

    public void setDisplaySourceFooter(String displaySourceFooter) {
        this.displaySourceFooter = displaySourceFooter;
    }

    public String getDisplaySourceFooterNoYLabel() {
        return displaySourceFooterNoYLabel;
    }

    public void setDisplaySourceFooterNoYLabel(String displaySourceFooterNoYLabel) {
        this.displaySourceFooterNoYLabel = displaySourceFooterNoYLabel;
    }

    public String getDataTableId() {
        return dataTableId;
    }

    public void setDataTableId(String dataTableId) {
        this.dataTableId = dataTableId;
    }
    
    public String getDtColumnString() {
        return dtColumnString;
    }

    public void setDtColumnString(String dtColumnString) {
        this.dtColumnString = dtColumnString;
    }

    private HtmlCommandButton addLineButton = new HtmlCommandButton();

    public HtmlCommandButton getAddLineButton() {
        return addLineButton;
    }

    public void setAddLineButton(HtmlCommandButton hit) {
        this.addLineButton = hit;
    }

    private HtmlCommandButton deleteLineButton = new HtmlCommandButton();

    public HtmlCommandButton getDeleteLineButton() {
        return deleteLineButton;
    }

    public void setDeleteLineButton(HtmlCommandButton hit) {
        this.deleteLineButton = hit;
    }

    public String getDataTable(){

    StudyFile sf = dt.getStudyFile();
    csvColumnString = "";
    dtColumnString = "";
    columnString = "";
    imageColumnString = "";
    try {
        File tmpSubsetFile = File.createTempFile("tempsubsetfile.", ".tab");
        List <DataVariable> dvsIn = new ArrayList();
        dvsIn.add(xAxisVar);
        columnString += xAxisVar.getName();
        csvColumnString += xAxisVar.getName();
        dtColumnString += xAxisVar.getName();
        for (VisualizationLineDefinition vld: vizLines){
            if (vld.getLabel().length() > 6){
                setLegendInt(2);
            }
            Long testId = vld.getVariableId();
            for (DataVariable dv : dt.getDataVariables()){
                if (dv.getId().equals(testId)){
                     dvsIn.add(dv);
                     columnString = columnString + "^" + vld.getLabel();
                     dtColumnString = dtColumnString + "^" + vld.getLabel() + " (" + vld.getMeasureGroup().getUnits() + ")";
                     csvColumnString = csvColumnString + "," + vld.getLabel();
                      try {
                           imageColumnString= imageColumnString + "^" +  URLEncoder.encode(vld.getLabel(), "UTF-8");
                        }    catch (Exception e){
                           imageColumnString= imageColumnString + "^" + vld.getLabel();
                     }
                }
            }

        }

        Set<Integer> fields = new LinkedHashSet<Integer>();
        List<DataVariable> dvs = dvsIn;
        sourceList.clear();
        List <DataVariableMapping> variableMappings = new ArrayList();
        for (Iterator el = dvs.iterator(); el.hasNext();) {
            DataVariable dv = (DataVariable) el.next();
            variableMappings = visualizationService.getSourceMappings((List)  dv.getDataVariableMappings());
                for (DataVariableMapping dvm: variableMappings ){
                sourceList.add(dvm);
            }
            fields.add(dv.getFileOrder());
        }

        // Execute the subsetting request:
        FieldCutter fc = new DvnJavaFieldCutter();


        fc.subsetFile(
            sf.getFileSystemLocation(),
            tmpSubsetFile.getAbsolutePath(),
            fields,
            dt.getCaseQuantity() );

        if (tmpSubsetFile.exists()) {


            Long subsetFileSize = tmpSubsetFile.length();
            List <String>  fileList = new ArrayList();
            BufferedReader reader = new BufferedReader(new FileReader(tmpSubsetFile));
            String line = null;
            while ((line=reader.readLine()) != null) {
               String check =  line.toString();
               fileList.add(check);
            }

            loadDataTableData(fileList);

            int countRows = 0;
            
            return subsetFileSize.toString();

        }
        return "";

        } catch  (IOException e) {
            e.printStackTrace();
            return "failure";
        }


    }

    private void loadDataTableData(List inStr){
    selectBeginYears = new ArrayList();
    selectEndYears = new ArrayList();
    selectIndexDate = new ArrayList();
    selectEndYears.add(new SelectItem(3000, "Max"));
    boolean firstYearSet = false;
    String maxYear = "";
    String output = "";
    String indexedOutput = "";
    String csvOutput = csvColumnString + "\n";
    boolean indexSet = false;
    boolean addIndexDate = false;
    boolean[] getIndexes = new boolean[9];
    Float lowValStandard = new Float(0);
    Float lowValIndex = new Float (0);
    Float highValStandard = new Float (0);
    Float highValIndex = new Float(0);    
    
    for (int i = 1; i<9; i++){
        getIndexes[i] = false;
    }
    String[] indexVals = new String[9];
    int maxLength = 0;
    for (Object inObj: inStr ){
        String nextStr = (String) inObj;
        String[] columnDetail = nextStr.split("\t");
        String[] test = columnDetail;

        if (test.length -1 > maxLength)
            {
                maxLength = test.length -1;
           }
    }
    boolean indexesDone = false;
    for (Object inObj: inStr ){
        String nextStr = (String) inObj;
        String[] columnDetail = nextStr.split("\t");
        String[] test = columnDetail;

        String col = "";
        String csvCol = "";
        boolean alreadyAdded = false;
        if (test.length > 1)
            {
                for (int i=0; i<test.length; i++){
                if (i == 0) {
                    col =  test[i];
                    csvCol  = test[i];

                    if (!firstYearSet){
                         selectBeginYears.add(new SelectItem(col, "Min"));
                         firstYearSet = true;
                    }
                    if (maxLength == test.length -1){
                         addIndexDate = true;
                    }
                    if (!indexSet && maxLength == test.length -1 &&  (indexDate.isEmpty()  ||  indexDate.equals(col)) ){
                         for (int k = 1; k<maxLength+1; k++){
                             getIndexes[k] = true;
                         }
                         indexSet = true;                       
                    }
                    maxYear = col;
                    selectBeginYears.add(new SelectItem( col, col));
                    selectEndYears.add(new SelectItem(col, col));

                } else {
                        col = col + ", " +  test[i];
                        csvCol = csvCol + ", " +  test[i];
                        /*
                        if (lowValStandard.equals(new Float  (0))  || lowValStandard.compareTo(new Float (test[i])) > 0 ){
                            lowValStandard = new Float(test[i]);
                        }
                        if (highValStandard.equals(new Float (0))  || highValStandard.compareTo(new Float (test[i])) < 0 ){
                            highValStandard = new Float(test[i]);
                        }
                         
                         */
                        Double testIndexVal = new Double (0);
                        if (!test[i].isEmpty()){
                            testIndexVal =  new Double (test[i]);
                        }
                        if (getIndexes[i] && testIndexVal > 0){
                            indexVals[i] = test[i];
                            getIndexes[i] = false;
                        }
                        boolean allfalse = true;
                        for (int j = 1; j<9; j++){
                            if( getIndexes[j] == true){
                                allfalse = false;
                            }
                        }

                        if (addIndexDate && allfalse && !alreadyAdded){
                            selectIndexDate.add(new SelectItem(test[0], test[0]));
                            alreadyAdded = true;
                        }

                        if (allfalse && !indexesDone  ){
                            for (int q = 1; q<test.length; q++){
                                    indexVals[q] = test[q];
                                    indexesDone = true;
                            }
                        }
                    }

                }
                  col = col + ";";
                  csvCol = csvCol + "\n";
           }

           output = output + col;
           csvOutput = csvOutput + csvCol;
    }

    for (Object inObj: inStr ){
        String nextStr = (String) inObj;
        String[] columnDetail = nextStr.split("\t");
        String[] test = columnDetail;
        String indexCol = "";         
        if (test.length > 1)
            {
                for (int i=0; i<test.length; i++){
                    if (i == 0) {
                        indexCol = test[i];
                    } else {

                            Double numerator = new Double(0);
                            if (!test[i].isEmpty()){
                                numerator = new Double (test[i]);
                            }
                            Double denominator = new Double (0);
                            if (!indexVals[i].isEmpty()){
                                denominator = new Double (indexVals[i]);
                            }

                            Object result = new Double(0);
                            if (!denominator.equals(new Double (0))  && !numerator.equals(new Double (0))){
                                result = (numerator / denominator) *  new Double (100);
                            } else {
                                result = "";
                            }
                            indexCol = indexCol + ", " +  result.toString();

                    }
                }
                  indexCol = indexCol + ";";
           }


           indexedOutput = indexedOutput + indexCol;
    }

          SelectItem setSI = selectEndYears.get(0);
          setSI.setValue(maxYear);
          selectEndYears.set(0, setSI);
          csvString = csvOutput;
          dataString = output;
          indexedDataString = indexedOutput;
    }

    public File getZipFileExport() {
        File zipOutputFile;
        ZipOutputStream zout;

        String exportTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss").format(new Date());

        try {
            zipOutputFile = File.createTempFile("dataDownload","zip");
            zout = new ZipOutputStream((OutputStream) new FileOutputStream(zipOutputFile));

            if (includeCSV) {
                File csvFile = File.createTempFile("dataDownload_","csv");
                writeFile(csvFile, csvString.toString().toCharArray(), csvString.toString().length());
                addZipEntry(zout, csvFile.getAbsolutePath(), "csvData_" + exportTimestamp + ".txt");

            }
            if (includeImage || includePdf) {
                File imageUrlFile = includeImage ? File.createTempFile("dataDownload","png") : null;
                File imagePdfFile = includePdf ? File.createTempFile("dataDownload","pdf") : null;
                writeImageFile(imageUrlFile, imagePdfFile);
                if (includeImage) {
                    addZipEntry(zout, imageUrlFile.getAbsolutePath(), "imageGraph_" + exportTimestamp + ".png");
                }
                if (includePdf) {
                    addZipEntry(zout, imagePdfFile.getAbsolutePath(), "imagePdf_" + exportTimestamp + ".pdf");
                }
            }
            if (includeExcel) {
                File excelDataFile = File.createTempFile("dataDownload","xls");
                writeExcelFile(excelDataFile);
                addZipEntry(zout, excelDataFile.getAbsolutePath(), "excelData_" + exportTimestamp + ".xls");
            }

            zout.close();
        } catch (IOException e) {
            throw new EJBException(e);
        } catch (Exception ie) {
            zipOutputFile = null;
        }


        return zipOutputFile;
    }


    private void writeFile(File fileIn, char[] charArrayIn, int bufSize){
            try {

            FileOutputStream outputFile = null;
            outputFile = new FileOutputStream(fileIn, true);
            FileChannel outChannel = outputFile.getChannel();
            ByteBuffer buf = ByteBuffer.allocate((bufSize * 2) + 1000);
            for (char ch : charArrayIn) {
              buf.putChar(ch);
            }

            buf.flip();

            try {
              outChannel.write(buf);
              outputFile.close();
            } catch (IOException e) {
              e.printStackTrace(System.err);
            }



        } catch (IOException e) {
            throw new EJBException(e);
        }


    }

    private void writeImageFile(File fileIn, File pdfFileIn) {

        try {

            String decoded = URLDecoder.decode(imageURL, "UTF-8");

            if (!graphTitle.isEmpty()){
                String graphTitleOut = "";
                if (graphTitle.length() > 80  && graphTitle.indexOf("|") == -1 ){
                    int strLen = graphTitle.length();
                    int half = graphTitle.length()/2;
                    int nextSpace = graphTitle.indexOf(" ",  half);
                    graphTitleOut = graphTitle.substring(0, nextSpace) + "|" + graphTitle.substring(nextSpace + 1, strLen);
                    graphTitle = graphTitleOut;
                } else {
                    graphTitleOut = graphTitle;
                }
                String encodedTitle = URLEncoder.encode(graphTitleOut, "UTF-8");
                decoded = decoded + "&chtt=" + encodedTitle;
            }
            /*
            if (!sources.isEmpty()){
                String xAxisDecoded = "0:" + getXaxisString() + URLEncoder.encode(getImageSourceFooter(), "UTF-8") ;
                decoded = decoded + "&chxl=" + xAxisDecoded;
            }
*/
       
        if (!decoded.isEmpty()){
                URL imageURLnew = new URL(decoded);

                try{
                    BufferedImage image =     ImageIO.read(imageURLnew);
                    if (fileIn != null) {                         
                        ImageIO.write(image, "png", fileIn);                         
                    }

                    if (pdfFileIn != null) {
                        Document document = new Document();
                        PdfWriter.getInstance(document, new FileOutputStream(pdfFileIn, true));
                        document.open();
                        java.awt.Image awtImg = image;
                        com.itextpdf.text.Image image2 =   com.itextpdf.text.Image.getInstance(awtImg, null);
                        document.add(image2);
                        document.close();
                    }

                } catch (IIOException io){
                    System.out.println(io.getMessage().toString());
                     System.out.println(io.getCause().toString());
                     System.out.println("IIOException " + imageURLnew);
                    
                } 
            }

        } catch (UnsupportedEncodingException uee){
              System.out.println("UnsupportedEncodingException ");

        } catch (MalformedURLException mue){
            System.out.println("MalformedURLException ");
        } catch (IOException io){
            System.out.println("IOException - outer ");
        } catch (DocumentException io){
            System.out.println("IOException - document ");
            System.out.println(io.getMessage());
        }
    }

    

    private void addZipEntry(ZipOutputStream zout, String inputFileName, String outputFileName) throws IOException{
        FileInputStream tmpin = new FileInputStream(inputFileName);
        byte[] dataBuffer = new byte[8192];
        int i = 0;

        ZipEntry e = new ZipEntry(outputFileName);
        zout.putNextEntry(e);

        while ((i = tmpin.read(dataBuffer)) > 0) {
            zout.write(dataBuffer, 0, i);
            zout.flush();
        }
        tmpin.close();
        zout.closeEntry();
     }

  private void writeExcelFile   (File fileIn) throws IOException {
      String parseString = new String(dataString);
      List list = Arrays.asList(parseString.split(";"));
      String parseColumn = new String(columnString);
      
      try {
            WorkbookSettings ws = new WorkbookSettings();
            ws.setLocale(new Locale("en", "EN"));
            WritableWorkbook w =
            Workbook.createWorkbook(fileIn, ws);

            WritableSheet s = w.createSheet("Data", 0);
            
            int rowCounter = 0;
            
            if (!graphTitle.isEmpty()){
                Label h = new Label (0, 0,  graphTitle.toString());
                s.addCell(h);
                rowCounter++;
            }
            
            List columnHeads = Arrays.asList(parseColumn.split("\\^"));
            
            if(!sources.isEmpty()){
                
                Label h = new Label (0, rowCounter,  "Source(s): " + sources.toString());
                s.addCell(h);
                rowCounter++;
                
            }
            
            
            int ccounter = 0;
            for (Object c: columnHeads){
                 Label h = new Label (ccounter, rowCounter,  c.toString());
                 s.addCell(h);
                 ccounter++;
            }
            rowCounter++;
            
            for (Object o: list){
                List dataFields = Arrays.asList(o.toString().split(","));
                int dcounter = 0;
                for (Object d: dataFields){
                    if (dcounter == 0){
                      Label l = new Label (dcounter, rowCounter,  d.toString());
                      s.addCell(l);

                    } else {
                        if (!d.toString().isEmpty()){
                            jxl.write.Number n = new jxl.write.Number(dcounter, rowCounter,  new Double(d.toString()));
                            s.addCell(n);
                        } else {
                            Label m = new Label  (dcounter, rowCounter,  "N/A");
                            s.addCell(m);
                        }
                    }
                    
                    dcounter++;
                }
                rowCounter++;
            }
            w.write();
            w.close();
        }
            catch (Exception e){

        }

  }

    private void getSourceList(  ) {
        String returnString = "";

        Set<String> set = new HashSet();
        if (sourceList != null  && !sourceList.isEmpty()){
            for (DataVariableMapping dvm: sourceList){
            String checkSource = dvm.getGroup().getName();
            if (!checkSource.isEmpty()){
                if (set.contains(checkSource)){

                    } else {
                        set.add(checkSource);
                        if (returnString.isEmpty()){
                            returnString = returnString + checkSource;
                        } else {
                            returnString = returnString + ", " + checkSource;
                        }
                    }
                }
            }

        }
        sources = returnString;
    }
    
    private void checkUnits() {        
        Set<String> set = new HashSet(); 
        int countLines = 0;

            for (VisualizationLineDefinition vld: vizLines){
                countLines++;
            String checkUnit = vld.getMeasureGroup().getUnits();
            if (!checkUnit.isEmpty()){
                if (!set.isEmpty() && !set.contains(checkUnit) && countLines > 1){
                        setDisplayIndexes(true);
                        yAxisLabel = "";
                    } else {
                        set.add(checkUnit);
                        yAxisLabel = checkUnit;
                    }
                }
            }
      
    }

    private void updateImageFooters(  ) {
        String sourcesWLabel = "";
        String returnString = "";
        boolean done = false;
        boolean sources = false;
        String footerNotes = "";
        String footerNotesNoY = "";
        String displayFooterNotes = "";
        String displayFooterNotesNoY = "";
        
        Set<String> set = new HashSet();
        String axisLabelTemp = "x,y";
        String axisLabelTempNoY = "x,y";
          Integer lineNum = 2;
          Integer lineNumNoY = 2;
          footerNotesNoY = "|";
          footerNotes = "|";
          displayFooterNotesNoY = "|";
          displayFooterNotes = "|";
        if (!yAxisLabel.isEmpty()){
               axisLabelTemp += ",y";
              String codedYAxisLabel = "";
              try {
                  codedYAxisLabel = URLEncoder.encode(yAxisLabel, "UTF-8");
              } catch (Exception e) {
                  codedYAxisLabel = yAxisLabel;
              }
               footerNotes += "|" + lineNum + ":||"+ codedYAxisLabel +"|";
               displayFooterNotes += "|" + lineNum + ":||"+ yAxisLabel +"|";
               lineNum++;
        }
        
        if (!xAxisLabel.isEmpty()){
               axisLabelTemp += ",x";
               String codedXAxisLabel = "";
              try {
                  codedXAxisLabel = URLEncoder.encode(xAxisLabel, "UTF-8");
              } catch (Exception e) {                
                  codedXAxisLabel = xAxisLabel;
              }
               footerNotes += "|" + lineNum + ":||"+ codedXAxisLabel +"|";
               footerNotesNoY += "|" + lineNumNoY + ":||"+ codedXAxisLabel +"|";
               displayFooterNotes += "|" + lineNum + ":||"+ xAxisLabel +"|";
               displayFooterNotesNoY += "|" + lineNumNoY + ":||"+ xAxisLabel +"|";
               lineNum++;
               lineNumNoY++;
        }
        
        if (sourceList != null  && !sourceList.isEmpty()){
            sources = true;
            for (DataVariableMapping dvm: sourceList){
            String checkSource = dvm.getGroup().getName();
            if (!checkSource.isEmpty()){
                if (set.contains(checkSource)){

                    } else {
                        set.add(checkSource);
                        if (returnString.isEmpty()){
                            returnString = returnString + "Source:  " + checkSource;
                        } else {
                            returnString = returnString + ", " + checkSource;
                        }
                    }
                }
            }
            
                    sourcesWLabel = "" + returnString;

          int numberOfLines = sourcesWLabel.length() / 72;
          numberOfLines = numberOfLines + 2;
          String[] sourceLines = new String[numberOfLines];
          int begOfLine = 0;
          int endOfLine = Math.min(sourcesWLabel.length(), 72);
          for (int i = 0; i < numberOfLines; i++){

              int previousSpace = sourcesWLabel.lastIndexOf(" ",  endOfLine);

              if (previousSpace > begOfLine  && (endOfLine - begOfLine) > 71 && !done){
                  sourceLines[i] = sourcesWLabel.substring(begOfLine, previousSpace);
              } else if (!done) {
                  sourceLines[i] = sourcesWLabel.substring(begOfLine, endOfLine);
                  done = true;
              } else {
                  sourceLines[i] = "";
              }

              begOfLine =  previousSpace + 1;
              endOfLine = Math.min(sourcesWLabel.length(), previousSpace + 73);
          }

          

          for (String sourceLine : sourceLines){
              axisLabelTemp += ",x";
              axisLabelTempNoY += ",x";
              String codedSourceLine = "";
              try {
                  codedSourceLine = URLEncoder.encode(sourceLine, "UTF-8");
              } catch (Exception e) {
                  codedSourceLine = sourceLine;
              }
              footerNotes += "|" + lineNum + ":|"+  codedSourceLine +"|";
              footerNotesNoY += "|" + lineNumNoY + ":|"+ codedSourceLine +"|";
              displayFooterNotes += "|" + lineNum + ":|"+  sourceLine +"|";
              displayFooterNotesNoY += "|" + lineNumNoY + ":|"+ sourceLine +"|";
              lineNum++;
              lineNumNoY++;
          }

        }
          String outputSourceFooter =  "";
          try {
                outputSourceFooter = URLEncoder.encode(footerNotes, "UTF-8");
          } catch (Exception e){
                outputSourceFooter= footerNotes;
          }
          
          setImageAxisLabel(axisLabelTemp);
          setImageSourceFooter(footerNotes);
          setImageAxisLabelNoYLabel(axisLabelTempNoY);
          setImageSourceFooterNoYLabel(footerNotesNoY);
          setDisplaySourceFooter(displayFooterNotes);
          setDisplaySourceFooterNoYLabel(displayFooterNotesNoY);          

    }
    
    private String getXaxisString()
{
    int startRange = 0;
    int endRange = 0;
    int yearLabelInt = 0;
    String myRowList[] = getDataString().split(";");
    String retString = "|";
    if (new Integer( startYear.toString()).intValue() != 0 && new Integer( endYear.toString()).intValue() !=3000)
        
    { yearLabelInt = Math.round((new Integer( endYear.toString()).intValue()-new Integer( startYear.toString()).intValue())/5); }
    else {
        for (int i=0; i< myRowList.length -1 ;i++)
        { String myRow = myRowList[i];
            String myRowParse[] = myRow.split ( "," );
            int yearNum = new Integer( myRowParse[0].toString()).intValue();
            if (i==0)
            { startRange = yearNum; } endRange = yearNum; }
        yearLabelInt = Math.round((endRange-startRange)/5); }
    int counter = yearLabelInt;
    for (int i=0; i< myRowList.length -1 ;i++)
    { boolean yearPrint = false; String myRow = myRowList[i];
        String myRowParse[] = myRow.split ( "," );
        int yearNum = new Integer( myRowParse[0].toString());
        if (i== 0 || counter == 0 )
        { counter = yearLabelInt; yearPrint = true; }
        counter--;
        if (yearNum >= new Integer( startYear.toString()).intValue() && yearNum <= new Integer( endYear.toString()).intValue() )
        { if (yearPrint){ retString= retString + yearNum + '|'; }
            else { retString = retString + '|'; } } }
    return retString;
}

    public String getTitleOut() {
        return "";
    }

    public void openVariableInfoPopup(ActionEvent ae){

        UIComponent uiComponent = ae.getComponent().getParent();
        while (!(uiComponent instanceof HtmlDataTable)){
            uiComponent = uiComponent.getParent();
        }
        HtmlDataTable tempTable = (HtmlDataTable) uiComponent;
        VisualizationLineDefinition vizLine = (VisualizationLineDefinition) tempTable.getRowData();

        variableLabel = vizLine.getVariableLabel();
        UrlValidator localValidator = new UrlValidator();
        variableLabelLink = false;
        try {
               variableLabelLink= localValidator.validateUrl(variableLabel);
        }
            catch (java.net.MalformedURLException mue){
                  variableLabelLink = false;
        }
        showVariableInfoPopup = true;
    }

    public void closeVariableInfoPopup(ActionEvent ae){
        variableLabel = "";
        showVariableInfoPopup = false;
    }
    private HtmlDataTable dataTableVizLines;

    public HtmlDataTable getDataTableVizLines() {
        return this.dataTableVizLines;
    }

    public void setDataTableVizLines(HtmlDataTable dataTableVizLines) {
        this.dataTableVizLines = dataTableVizLines;
    }


    public String getColumnString() {

        return columnString;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;
    }

    public String getDataString() {
        return  this.dataString;
    }

    public String getDataColumns() {
        return  this.numberOfColumns.toString();
    }

    HtmlSelectOneMenu selectGraphType;

    public HtmlSelectOneMenu getSelectGraphType() {
        return selectGraphType;
    }

    public void setSelectGraphType(HtmlSelectOneMenu selectGraphType) {
        this.selectGraphType = selectGraphType;
    }


    HtmlSelectOneMenu selectStartYear;

    public HtmlSelectOneMenu getSelectStartYear() {
        return selectStartYear;
    }

    public void setSelectStartYear(HtmlSelectOneMenu selectStartYear) {
        this.selectStartYear = selectStartYear;
    }

    HtmlSelectOneMenu selectEndYear;

    public HtmlSelectOneMenu getSelectEndYear() {
        return selectEndYear;
    }

    public void setSelectEndYear(HtmlSelectOneMenu selectEndYear) {
        this.selectEndYear = selectEndYear;
    }

    public String getImageAxisLabel() {
        return imageAxisLabel;
    }

    public String getImageSourceFooter() {
        return imageSourceFooter;
    }

    public void setImageAxisLabel(String imageAxisLabel) {
        this.imageAxisLabel = imageAxisLabel;
    }

    public void setImageSourceFooter(String imageSourceFooter) {
        this.imageSourceFooter = imageSourceFooter;
    }
    HtmlSelectOneMenu selectIndexYear;

    public HtmlSelectOneMenu getSelectIndexYear() {
        return selectIndexYear;
    }

    public void setSelectIndexYear(HtmlSelectOneMenu selectIndexYear) {
        this.selectIndexYear = selectIndexYear;
    }
    public Long getDisplayType() {
        return displayType;
    }

    public void setDisplayType(Long displayType) {
        this.displayType = displayType;
    }
    
    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public StudyUI getStudyUI() {
        return studyUI;
    }

    public void setStudyUI(StudyUI studyUI) {
        this.studyUI = studyUI;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    private HtmlInputText inputGraphTitle;

    public HtmlInputText getInputGraphTitle() {
        return this.inputGraphTitle;
    }
    public void setInputGraphTitle(HtmlInputText inputGraphTitle) {
        this.inputGraphTitle = inputGraphTitle;
    }
    private HtmlInputText inputDownloadFileName;

    public HtmlInputText getInputDownloadFileName() {
        return this.inputDownloadFileName;
    }
    public void setInputDownloadFileName(HtmlInputText inputDownloadFileName) {
        this.inputDownloadFileName = inputDownloadFileName;
    }
    private HtmlInputText inputImageURL;

    public HtmlInputText getInputImageURL() {
        return this.inputImageURL;
    }
    public void setInputImageURL(HtmlInputText inputImageURL) {
        this.inputImageURL = inputImageURL;
    }
    
    private HtmlInputText inputLineLabel;

    public HtmlInputText getInputLineLabel() {
        return this.inputLineLabel;
    }
    public void setInputLineLabel(HtmlInputText inputLineLabel) {
        this.inputLineLabel = inputLineLabel;
    }

    public String getGraphTitle() {
        return graphTitle;
    }

    public void setGraphTitle(String graphTitle) {
        this.graphTitle = graphTitle;
    }

    public String updateGraphTitle(){
        titleEdited = true;
        String graphTitleIn = (String) getInputGraphTitle().getValue();
        setGraphTitle(graphTitleIn);        
        FacesContext fc = FacesContext.getCurrentInstance();
        JavascriptContext.addJavascriptCall(fc, "drawVisualization();");
        JavascriptContext.addJavascriptCall(fc, "initLineDetails");
        return "";
    }

    public String updateImageURL(){
        String imageURLIn = (String) getInputImageURL().getValue();   
        setImageURL(imageURLIn);               
        return "";
    }

    public List<SelectItem> getSelectBeginYears() {
        return selectBeginYears;
    }

    public void setSelectBeginYears(List<SelectItem> selectBeginYears) {
        this.selectBeginYears = selectBeginYears;
    }

    public List<SelectItem> getSelectEndYears() {
        return selectEndYears;
    }

    public void setSelectEndYears(List<SelectItem> selectEndYears) {
        this.selectEndYears = selectEndYears;
    }
    public String getEndYear() {
        return endYear;
    }

    public void setEndYear( String endYear) {
        this.endYear = endYear;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getIndexedDataString() {
        return indexedDataString;
    }

    public void setIndexedDataString(String indexedDataString) {
        this.indexedDataString = indexedDataString;
    }

    public String getIndexDate() {
        return indexDate;
    }

    public void setIndexDate(String indexDate) {
        this.indexDate = indexDate;
    }


    public List<SelectItem> getSelectIndexDate() {
        return selectIndexDate;
    }

    public void setSelectIndexDate(List<SelectItem> selectIndexDate) {
        this.selectIndexDate = selectIndexDate;
    }


    public boolean isDisplayIndexes() {
        return displayIndexes;
    }

    public void setDisplayIndexes(boolean displayIndexes) {
        this.displayIndexes = displayIndexes;
    }

    public boolean isDisplayLegend() {
        return displayLegend;
    }

    public void setDisplayLegend(boolean displayLegend) {
        this.displayLegend = displayLegend;
    }
    
    
    public String getImageAxisLabelNoYLabel() {
        return imageAxisLabelNoYLabel;
    }

    public void setImageAxisLabelNoYLabel(String imageAxisLabelNoYLabel) {
        this.imageAxisLabelNoYLabel = imageAxisLabelNoYLabel;
    }


    public String getSources() {
        getSourceList();
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public boolean isShowVariableInfoPopup() {
        return showVariableInfoPopup;
    }

    public void setShowVariableInfoPopup(boolean showVariableInfoPopup) {
        this.showVariableInfoPopup = showVariableInfoPopup;
    }

    public String getVariableLabel() {
        return variableLabel;
    }

    public void setVariableLabel(String variableLabel) {
        this.variableLabel = variableLabel;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    
    public boolean isVariableLabelLink() {
        
        return variableLabelLink;
    }

    public String getImageColumnString() {
        return imageColumnString;
    }

    public void setImageColumnString(String imageColumnString) {
        this.imageColumnString = imageColumnString;
    }
    
    public String getImageSourceFooterNoYLabel() {
        return imageSourceFooterNoYLabel;
    }
    
    
    public boolean isDataTableAvailable() {
        return dataTableAvailable;
    }

    public void setDataTableAvailable(boolean dataTableAvailable) {
        this.dataTableAvailable = dataTableAvailable;
    }

    public boolean isImageAvailable() {
        return imageAvailable;
    }

    public void setImageAvailable(boolean imageAvailable) {
        this.imageAvailable = imageAvailable;
    }
    
    public String getSourceLineLabel() {
        return sourceLineLabel;
    }

    public void setSourceLineLabel(String sourceLineLabel) {
        this.sourceLineLabel = sourceLineLabel;
    }
    
    public Integer getDefaultView() {

        return defaultView;
    }


    public void setImageSourceFooterNoYLabel(String imageSourceFooterNoYLabel) {
        this.imageSourceFooterNoYLabel = imageSourceFooterNoYLabel;
    }

    private HtmlSelectBooleanCheckbox dataExcelCheckBox;

    public HtmlSelectBooleanCheckbox getDataExcelCheckBox() {
        return dataExcelCheckBox;
    }

    public void setDataExcelCheckBox(HtmlSelectBooleanCheckbox dataExcelCheckBox) {
        this.dataExcelCheckBox = dataExcelCheckBox;
    }

    
    private HtmlSelectBooleanCheckbox useIndicesCheckBox;

    public HtmlSelectBooleanCheckbox getUseIndicesCheckBox() {
        return useIndicesCheckBox;
    }

    public void setUseIndicesCheckBox(HtmlSelectBooleanCheckbox useIndicesCheckBox) {
        this.useIndicesCheckBox = useIndicesCheckBox;
    }


    public boolean isIncludeCSV() {
        return includeCSV;
    }

    public void setIncludeCSV(boolean includeCSV) {
        this.includeCSV = includeCSV;
    }

    public boolean isIncludeExcel() {
        return includeExcel;
    }

    public void setIncludeExcel(boolean includeExcel) {
        this.includeExcel = includeExcel;
    }

    public boolean isIncludeImage() {
        return includeImage;
    }

    public void setIncludeImage(boolean includeImage) {
        this.includeImage = includeImage;
    }


    public boolean isIncludePdf() {
        return includePdf;
    }

    public void setIncludePdf(boolean includePdf) {
        this.includePdf = includePdf;
    }

    public Resource getDownloadImage() {
        return new ExportFileResource("png");
    }

    public Resource getDownloadPDF() {
        return new ExportFileResource("pdf");
    }

    public Resource getDownloadExcel() {
        return new ExportFileResource("xls");
    }

    public Resource getDownloadCSV() {
        return new ExportFileResource("csv");
    }

    public Resource getDownloadZip() {
        return new ExportFileResource("zip");
    }


    public Integer getLegendInt() {
        return legendInt;
    }

    public void setLegendInt(Integer legendInt) {
        this.legendInt = legendInt;
    }
    
    public class ExportFileResource implements Resource, Serializable{
        File file;
        String fileType;



        public ExportFileResource(String fileType) {
            this.fileType = fileType;
        }

        public String calculateDigest() {
            return file != null ? file.getPath() : null;
        }

        public Date lastModified() {
            return file != null ? new Date(file.lastModified()) : null;
        }

        public InputStream open() throws IOException {
            file = File.createTempFile("downloadFile","tmp");
            if ("png".equals(fileType) ) {
                 writeImageFile(file, null);
            } else if ("pdf".equals(fileType) ) {
                 writeImageFile(null, file);
            } else if ("xls".equals(fileType) ) {
                writeExcelFile(file);
            } else if ("csv".equals(fileType) ) {
                writeFile(file, csvString.toString().toCharArray(), csvString.toString().length() );
            } else if ("zip".equals(fileType) ) {
                file = getZipFileExport();
            } else {
                throw new IOException("Invalid file type selected.");
            }

            return new FileInputStream(file);
        }

        public void withOptions(Options options) throws IOException {
            String filePrefix = "dataDownload_" + new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss").format(new Date());
            options.setFileName(filePrefix + "." + fileType);

        }

        public File getFile() {
            return file;
        }

    }
}
