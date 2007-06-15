<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.1" 
xmlns:t="/WEB-INF/tlds/scroller"
xmlns:f="http://java.sun.com/jsf/core" 
xmlns:h="http://java.sun.com/jsf/html" 
xmlns:jsp="http://java.sun.com/JSP/Page" 
xmlns:ui="http://www.sun.com/web/ui"
>

<!-- to change the content type or response encoding change the following line -->
<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
<jsp:directive.page import="java.util.*" />
<!-- scriptlet to check request header and parameters -->
<jsp:scriptlet>
<![CDATA[  
  //value=request.getParameter("form1:tabSet1:tabDwnld:dwnldBttn");

  List<String> vec = new ArrayList<String>();
  Enumeration em = request.getHeaderNames();
  while( em.hasMoreElements() ) {
      String name = (String)em.nextElement();
      //String[] values = request.getHeader( name );
      Enumeration emx = request.getHeaders( name );
      while (emx.hasMoreElements()) {
        vec.add( "<tr><td>"+name + "</td><td>" + (String)emx.nextElement() +"</td></tr>");
      }
  }

  Map hm = request.getParameterMap();
  Set es = hm.entrySet();
  String dataFileId = request.getParameter("dtId");
]]></jsp:scriptlet>


<!-- to be renamed to subview -->


<f:subview id="analysisPageView">
<!-- f:loadBundle basename="vdcnetwar.analysis.AnalysisResources" var="i18nres"/ -->

<!-- html top boiler-plate block -->    

    <h:form id="form1" >
    <![CDATA[<input type="hidden" name="dtId" value="]]><jsp:expression>dataFileId</jsp:expression><![CDATA["/>]]>

    <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>

     <ui:panelLayout id="plBelowForm1" panelLayout="flow" styleClass="vdcSectionMiddleNoBorder"> 
         
         <ui:panelGroup  block="true" styleClass="vdcStudyTitle">
             <h:outputText styleClass="vdcStudyTitleName"  value="#{AnalysisPage.studyTitle}"/>
             
        </ui:panelGroup>
        <ui:panelGroup  block="true" styleClass="vdcFileTitle" style="padding-bottom: 10px;">
            <h:outputText value="Date File: #{AnalysisPage.fileName}"/>
            <h:outputLink  style="margin-left: 2px;" value="/dvn#{VDCRequest.currentVDCURL}/faces/study/StudyPage.jsp?studyId=#{AnalysisPage.studyId}&amp;tab=files">
                      <h:outputText style="font-size: 0.9em; font-weight: bold;" value="Back to Study Files"/>                       
             </h:outputLink>
        </ui:panelGroup>

    <!--tab block starts here -->
  
            <h:panelGrid id="gridPanel1" binding="#{AnalysisPage.gridPanel1}" cellpadding="0" cellspacing="0" columns="1" width="100%">
            
<ui:tabSet binding="#{AnalysisPage.tabSet1}" id="tabSet1" lite="true" mini="true" selected="tabDwnld" styleClass="vdcStudyTabSet">
                
                
    <!-- 1. Download -->
    <ui:tab id="tabDwnld" text="#{bundleAnalysis['dwnld.tab.text']}" actionListener="#{AnalysisPage.resetVariableInLBox}">
        <ui:panelLayout id="layoutPanel3" panelLayout="flow" style="width: 100%;">

            <h:panelGrid cellpadding="0" cellspacing="0" columnClasses="vdcAnalysisCol1, vdcAnalysisCol2" columns="2" id="gridPanel4" width="100%">

                <ui:panelGroup id="groupPanel2" separator="&lt;br /&gt;">

                    <!-- ui tag solution -->
                    <ui:listbox id="mListboxDwnld" 
                        items="#{AnalysisPage.varSetAdvStat}" 
                        rows="10" 
                        labelLevel="3" 
                        monospace="true"  
                        multiple="true" 
                        label="#{bundleAnalysis['dwnld.selectedVarBox.title']}"
                        labelOnTop="true"/>



                    <ui:helpInline id="helpInline2" text="#{bundleAnalysis['dwnld.selectedvarBox.bttmHelpText']}" type="field" rendered="false"/>

                </ui:panelGroup>

                <ui:panelGroup block="true" id="groupPanel5">
                    <h:outputText id="outputText13" value="#{bundleAnalysis['dwnld.instruction']}"/>

                    <h:selectOneRadio binding="#{AnalysisPage.dwnldFileTypeSet}" id="dtdwnld" layout="pageDirection">

                        <f:selectItem id="dwnldFileTypeText"  itemLabel="#{bundleAnalysis['dwnld.fileType.text']}" itemValue="#{AnalysisPage.dwnldFileTypeItems[0]}"/>
                        <f:selectItem id="dwnldFileTypeRdata" itemLabel="#{bundleAnalysis['dwnld.fileType.Rdata']}" itemValue="#{AnalysisPage.dwnldFileTypeItems[1]}"/>
                        <f:selectItem id="dwnldFileTypeSplus" itemLabel="#{bundleAnalysis['dwnld.fileType.Splus']}" itemValue="#{AnalysisPage.dwnldFileTypeItems[2]}"/>
                        <f:selectItem id="dwnldFileTypeStata" itemLabel="#{bundleAnalysis['dwnld.fileType.stata']}" itemValue="#{AnalysisPage.dwnldFileTypeItems[3]}"/>
                    </h:selectOneRadio>
                    <h:commandButton id="dwnldBttn"
                       binding="#{AnalysisPage.dwnldButton}" 
                       type="submit" 
                       disabled="true" 
                       value="#{bundleAnalysis['dwnld.button.submit']}" 
                       action="#{AnalysisPage.dwnldAction}"/>
                </ui:panelGroup>

            </h:panelGrid>
        </ui:panelLayout>
    </ui:tab>
                
                
    <!-- 2. Recode -->

    <ui:tab  id="tabRecode" 
        text="#{bundleAnalysis['recode.tab.text']}" actionListener="#{AnalysisPage.resetVariableInLBox}">
        <ui:panelLayout id="layoutPanel2" panelLayout="flow" style="width: 100%;">
            <h:panelGrid  id="gridPanel7"
               cellpadding="0" cellspacing="0" columnClasses="vdcAnalysisCol1, vdcAnalysisCol2" columns="2" width="100%">
<!-- 1st column -->
                <ui:panelGroup id="groupPanel10" separator="&lt;br /&gt;">

                    <!-- ui tag solution -->
                    <ui:listbox id="mListboxRecode" 
                        items="#{AnalysisPage.varSetAdvStat}" 

                        selected="#{AnalysisPage.selectedRecodeVariable}"
                        rows="10" 
                        labelLevel="3" 
                        monospace="true"  
                        multiple="false" 
                        label="#{bundleAnalysis['advStat.selectedVarBox.title']}"
                        labelOnTop="true"/>

                    <ui:helpInline 
                        id="helpInline4" 
                        text="#{bundleAnalysis['recode.selectedvarBox.bttmHelpText']}" type="field" rendered="false" />
                </ui:panelGroup>
<!-- end: 1st column -->
                
<!-- 2nd column -->
                <ui:panelGroup id="groupPanel11" separator="&lt;br /&gt;">
                
                    <h:outputText id="outputText49" 
                       value="#{bundleAnalysis['recode.instruction']}"/>
                    <br />

                <ui:panelGroup id="recodeTableArea">
                    <ui:panelGroup id="groupPanel15">
                        <h:commandButton id="moveRecodeVarBttn"
                            binding="#{AnalysisPage.moveRecodeVarBttn}"
                            immediate="true"
                            actionListener="#{AnalysisPage.moveRecodeVariable}"
                            disabled="true"
                            style="vertical-align:top"
                            alt="#{bundleAnalysis['recode.button.moveVar.alt']}"  
                            value=" &gt; "   />
                        <h:outputText id="recodeNewVarName" 
                           value="#{bundleAnalysis['recode.newVar.name']}"/>
                        
                        <h:inputText id="recodeTargetVarName"
                           binding="#{AnalysisPage.recodeTargetVarName}"
                           />
                           <!-- value="#{AnalysisPage.recodeVariableName}"  -->

                        <h:outputText id="recodeNewVarLabel"
                           value="#{bundleAnalysis['recode.newVar.label']}"/>
                        
                        <h:inputText id="recodeTargetVarLabel"
                            binding="#{AnalysisPage.recodeTargetVarLabel}"
                            
                            size="40"/>
                            <!-- value="#{AnalysisPage.recodeVariableLabel}" -->

                    </ui:panelGroup>

                    <h:dataTable id="recodeTable" 
                       binding="#{AnalysisPage.recodeTable}"
                       var="rd"
                       value="#{AnalysisPage.recodeDataList}"
                       rendered="false"
                       cellpadding="0" cellspacing="0" columnClasses="vdcRecodesColA, vdcRecodesColB, vdcRecodesColB, vdcRecodesColB, vdcRecodesColB, vdcRecodesColB, vdcRecodesColB, vdcRecodesColB" 
                       headerClass="vdcRecodesHeader" 
                       style="margin-top: 20px" >

                        <h:column id="recodeClDropx">
                            <f:facet name="header">
                                <h:outputText id="recodeHdrDrop" 
                                   value="#{bundleAnalysis['recode.valueTable.drop.header']}"/>
                            </f:facet>

                            <ui:checkbox id="recodeDropValueCheckbox"
                                binding="#{AnalysisPage.recodeDropValueCheckbox}"
                                selected="#{rd[0]}"
                                toolTip="#{bundleAnalysis['recode.valueTable.drop.checkbox']}"
                                immediate="true"/>

                        </h:column>
                        <h:column id="recodeClValue">
                            <f:facet name="header">
                                <h:outputText id="recodeHdrValue" 
                                   value="#{bundleAnalysis['recode.valueTable.value.header']}"/>
                            </f:facet>

                            <h:inputText  id="recodeValueBox" 
                               
                               value="#{rd[1]}"
                               size="10"/>

                        </h:column>
                        <h:column id="recodeClValueLabel">
                            <f:facet name="header">
                                <h:outputText id="recodeHdrValueLabel" 
                                   value="#{bundleAnalysis['recode.valueTable.valuelabel.header']}"/>
                            </f:facet>

                            <h:inputText  id="recodeValueLabelBox"
                               value="#{rd[2]}" 
                               size="25"/>

                        </h:column>
                        <h:column id="recodeClVariable">
                            <f:facet name="header">
                                <h:outputText id="recodeHdrVariable" 
                                value="#{AnalysisPage.currentRecodeVariableName}"/>
                            </f:facet>

                            <h:inputText  id="recodeVariableNameBox"
                               value="#{rd[3]}" 
                               size="10"/>

                        </h:column>
                    </h:dataTable>
                    <br />
                    <h:commandButton id="addValueRangeBttn" 
                       binding="#{AnalysisPage.addValueRangeBttn}"
                       value="#{bundleAnalysis['recode.button.addValue']}"
                       actionListener="#{AnalysisPage.addValueRange}"
                       rendered ="false"/>

                    <ui:panelGroup id="groupPanel18" 
                        block="true" 
                        style="padding-left: 300px; white-space: nowrap">

                        <h:commandButton id="recodeBttn"
                           binding="#{AnalysisPage.recodeButton}" 
                           value="#{bundleAnalysis['recode.button.save']}"
                           actionListener="#{AnalysisPage.saveRecodedVariable}"
                           disabled="true" />
                           
                        <ui:staticText id="msgSaveRecodeBttn"
                            binding="#{AnalysisPage.msgSaveRecodeBttn}" 
                            rendered="false"
                            escape="false"
                            styleClass="errorMessage"/>


                    </ui:panelGroup>

            </ui:panelGroup><!-- recodeTableArea -->

                </ui:panelGroup><!--end 2nd column  -->
            </h:panelGrid>
        </ui:panelLayout>
    </ui:tab>
                
                
    <!-- 3. EDA -->
    <ui:tab id="tabEda" text="#{bundleAnalysis['eda.tab.text']}" actionListener="#{AnalysisPage.resetVariableInLBox}">
        <ui:panelLayout id="layoutPanel4" panelLayout="flow" style="width: 100%;">

            <h:panelGrid cellpadding="0" cellspacing="0" columnClasses="vdcAnalysisCol1, vdcAnalysisCol2" columns="2" id="gridPanel2" width="100%">

                <ui:panelGroup id="groupPanel3" separator="&lt;br /&gt;">
                    <!-- ui tag solution -->
                    <ui:listbox id="mListboxEda" 
                        items="#{AnalysisPage.varSetAdvStat}" 
                        rows="10" 
                        labelLevel="3" 
                        monospace="true"  
                        multiple="true" 
                        label="#{bundleAnalysis['eda.selectedVarBox.title']}"
                        labelOnTop="true"/>
                    <ui:helpInline id="helpInline3" text="#{bundleAnalysis['eda.selectedvarBox.bttmHelpText']}" type="field" rendered="false" />

                </ui:panelGroup>


                <ui:panelGroup block="true" id="groupPanel4">

                    <h:outputText id="outputText21" value="#{bundleAnalysis['eda.instruction']}"/>

                    <h:selectManyCheckbox binding="#{AnalysisPage.edaOptionSet}" id="analysis" layout="pageDirection">

                        <f:selectItem  id="edaOptionNumeric" 
                           itemLabel="#{bundleAnalysis['eda.option.numeric']}" 
                           itemValue="#{AnalysisPage.edaOptionItems[0]}"/>
                        <f:selectItem  id="edaOptionGraphic" 
                           itemLabel="#{bundleAnalysis['eda.option.graphic']}"  
                           itemValue="#{AnalysisPage.edaOptionItems[1]}"/>

                    </h:selectManyCheckbox>

                    <h:commandButton  id="edaBttn"
                       binding="#{AnalysisPage.edaButton}" 
                       disabled="true" 
                       value="#{bundleAnalysis['eda.button.submit']}" 
                       action="#{AnalysisPage.edaAction}"/>

                </ui:panelGroup>

            </h:panelGrid>

        </ui:panelLayout>
    </ui:tab>
                
                
    <!-- 5. advStat -->
    <ui:tab id="tabAdvStat" text="#{bundleAnalysis['advStat.tab.text']}">
        <ui:panelLayout id="layoutPanel5" panelLayout="flow" style="width: 100%;">

            <h:panelGrid id="gridPanel5"
               cellpadding="0" 
               cellspacing="0" 
               columnClasses="vdcAnalysisCol1, vdcAnalysisCol2" 
               columns="2"  
               width="100%">

                <ui:panelGroup id="groupPanel6" separator="&lt;br /&gt;">

                 <!-- ui tag solution -->

                  <ui:listbox id="mListboxAdvStat" 
                      items="#{AnalysisPage.varSetAdvStat}" 
                      binding="#{AnalysisPage.listboxAdvStat}"
                      selected="#{AnalysisPage.advStatSelectedVarLBox}"
                      rows="10" 
                      labelLevel="3" 
                      monospace="true"  
                      multiple="true" 
                      label="#{bundleAnalysis['advStat.selectedVarBox.title']}"
                      labelOnTop="true"/>

                    <ui:helpInline id="helpInline1" 
                        text="#{bundleAnalysis['advStat.selectedvarBox.bttmHelpText']}"
                        type="field" rendered="false" />
                </ui:panelGroup><!-- groupPanel6 -->
                <!-- end of LHS -->
                <!-- RHS section of the tab -->
                <ui:panelGroup id="groupPanel7" block="true" >

                    <!-- model menu list -->

                    <h:panelGrid id="gridPanel8out"
                       cellpadding="0" cellspacing="0" 
                       columnClasses="vdcAdvAnalysisCol1, vdcAdvAnalysisCol2" 
                       columns="1" >

                    <h:panelGrid id="gridPanel8above"
                       cellpadding="0" 
                       cellspacing="0" 
                       columnClasses="vdcAdvAnalysisCol1, vdcAdvAnalysisCol2" 
                       columns="1" >

                            <ui:dropDown id="dropDown1"
                                binding="#{AnalysisPage.dropDown1}"  
                                items="#{AnalysisApplicationBean.modelMenuOptions}"
                                onChange="common_timeoutSubmitForm(this.form, '#{AnalysisPage.dropDown1ClientId}');" 
                                valueChangeListener="#{AnalysisPage.dropDown1_processValueChange}"
                                submitForm="false"
                                immediate="true" />

                            <!-- 
                            </ui:panelGroup>
                            -->

                    </h:panelGrid><!-- end: gridPanel8above -->
                    <ui:panelGroup id="groupPanel8below" 
                        binding="#{AnalysisPage.groupPanel8below}" 
                        rendered="false">
                    <h:panelGrid id="gridPanel8"
                       cellpadding="0" 
                       cellspacing="0" 
                       columnClasses="vdcAdvAnalysisCol1, vdcAdvAnalysisCol2" 
                       columns="2" >

                        <!-- column of variable boxes-->
                        <ui:panelGroup id="groupPanel8" 

                            separator="&lt;br /&gt;">

                            <!-- 1st box panel: usually dependent -->
                            <ui:panelGroup id="groupPanel12"
                              binding="#{AnalysisPage.groupPanel12}" 
                              rendered="true" >

                                <!-- h:outputText id="moveVar1BttnLabel" 
                                  value="#{bundleAnalysis['advStat.modelVarbox.dependent.label']}"/ -->

                                <h:panelGrid columns="2" id="gridPanel12">

                                    <h:panelGrid columns="1" id="gridPanel12in">

                                    <h:commandButton id="moveVar1Bttn"
                                      binding="#{AnalysisPage.button4}"
                                      actionListener="#{AnalysisPage.addVarBoxR1}"
                                      value=" &gt; "/>
                                      <ui:staticText id="msgMoveVar1Bttn"
                                          binding="#{AnalysisPage.msgMoveVar1Bttn}" 
                                          rendered="false"
                                          escape="false"
                                          styleClass="errorMessage"/>
                                    <h:commandButton id="moveVar1Bttnb"
                                      binding="#{AnalysisPage.button4b}"
                                      actionListener="#{AnalysisPage.removeVarBoxR1}"
                                      value=" &lt; "/>

                                    </h:panelGrid>
                                    <h:panelGrid columns="1" id="gridPanel12r">
                                      <!--h:outputText id="moveVarListbox1lbl"
                                        binding="#{AnalysisPage.moveVarListbox1lbl}"
                                        value="1st box"/ -->
                                      <ui:label id="varListbox1Lbl" 
                                          binding="#{AnalysisPage.varListbox1Lbl}"
                                          for="moveVarListbox1"
                                          labelLevel="3" 
                                          text="1st box"/>

                                      <ui:listbox id="moveVarListbox1"
                                         binding="#{AnalysisPage.advStatVarListboxR1}" 
                                         items="#{AnalysisPage.advStatVarRBox1}"
                                         selected="#{AnalysisPage.advStatSelectedVarRBox1}"
                                         multiple="true"
                                         monospace="true"
                                         separators="false"
                                         immediate="true"

                                         rows="3" />
                                         <!-- label="#{bundleAnalysis['advStat.modelVarbox.dependent.label']}"-->
                                    </h:panelGrid>
                                </h:panelGrid>
                            </ui:panelGroup><!-- groupPanel12 -->

                            <!-- 2nd box panel: usually independent -->
                            <ui:panelGroup id="groupPanel13"

                              binding="#{AnalysisPage.groupPanel13}" 
                              rendered="false">

                                <!-- h:outputText id="outputText22" value="#{bundleAnalysis['advStat.modelVarbox.explanatory.label']}"/
                                -->
                                <h:panelGrid columns="2" id="gridPanel13">

                                    <h:panelGrid columns="1" id="gridPanel13in">

                                    <h:commandButton id="moveVar2Bttn"
                                      binding="#{AnalysisPage.button5}" 
                                      actionListener="#{AnalysisPage.addVarBoxR2}"
                                      value=" &gt; "/>
                                    
                                    <ui:staticText id="msgMoveVar2Bttn"
                                    binding="#{AnalysisPage.msgMoveVar2Bttn}" 
                                    rendered="false"
                                    escape="false"
                                    styleClass="errorMessage"/>
                                    
                                    <h:commandButton id="moveVar2Bttnb"
                                      binding="#{AnalysisPage.button5b}" 
                                      actionListener="#{AnalysisPage.removeVarBoxR2}"
                                      value=" &lt; "/>

                                    </h:panelGrid>
                                    <h:panelGrid columns="1" id="gridPanel13r">

                                    <ui:label id="varListbox2Lbl" 
                                        binding="#{AnalysisPage.varListbox2Lbl}"
                                        for="moveVarListbox2"
                                        labelLevel="3" 
                                        text="2nd box"/>

                                    <ui:listbox id="moveVarListbox2"
                                      binding="#{AnalysisPage.advStatVarListboxR2}" 
                                      items="#{AnalysisPage.advStatVarRBox2}"
                                      selected="#{AnalysisPage.advStatSelectedVarRBox2}"
                                      labelLevel="3" 
                                      multiple="true"
                                      monospace="true"
                                      immediate="true"

                                      rows="3"/>
                                     <!-- label="#{bundleAnalysis['advStat.modelVarbox.explanatory.label']}"-->
                                     </h:panelGrid>
                                </h:panelGrid>
                            </ui:panelGroup><!-- groupPanel13 -->

                            <!-- 3rd box panel: usually for models of factor anlysis-->

                            <ui:panelGroup id="groupPanel14"
                              binding="#{AnalysisPage.groupPanel14}"  
                              rendered="false">

                                <!-- h:outputText id="outputText44" 
                                value="#{bundleAnalysis['advStat.modelVarbox.observed.label']}"/
                                -->
                                <h:panelGrid columns="2" id="gridPanel14">

                                    <h:panelGrid columns="1" id="gridPanel14in">

                                    <h:commandButton id="moveVar3Bttn" 
                                      binding="#{AnalysisPage.button6}" 
                                      actionListener="#{AnalysisPage.addVarBoxR3}"
                                      value=" &gt; "/>
                                      
                                    <ui:staticText id="msgMoveVar3Bttn"
                                        binding="#{AnalysisPage.msgMoveVar3Bttn}" 
                                        rendered="false"
                                        escape="false"
                                        styleClass="errorMessage"/>
                                    
                                    <h:commandButton id="moveVar3Bttnb" 
                                      binding="#{AnalysisPage.button6b}" 
                                      actionListener="#{AnalysisPage.removeVarBoxR3}"
                                      value=" &lt; "/>

                                    </h:panelGrid>
                                    <h:panelGrid columns="1" id="gridPanel14r">


                                    <ui:label id="varListbox3Lbl" 
                                        binding="#{AnalysisPage.varListbox3Lbl}"
                                        for="moveVarListbox3"
                                        labelLevel="3" 
                                        text="3rd box"/>

                                    <ui:listbox id="moveVarListbox3"
                                      binding="#{AnalysisPage.advStatVarListboxR3}" 
                                      items="#{AnalysisPage.advStatVarRBox3}" 
                                      selected="#{AnalysisPage.advStatSelectedVarRBox3}"
                                      labelLevel="3"
                                      multiple="true"
                                      monospace="true"
                                      immediate="true"

                                      rows="3"/>
                                     <!-- label="#{bundleAnalysis['advStat.modelVarbox.observed.label']}"-->
                                     </h:panelGrid>
                                </h:panelGrid>
                            </ui:panelGroup><!-- groupPanel14 -->

                        <!-- end of column of var boxes -->
                        </ui:panelGroup><!-- groupPanel8 -->

                        <!-- column of option panes -->
                        <ui:panelGroup id="groupPanel9"
                            separator="&lt;br /&gt;">

                            <!-- output-opton block -->

                            <ui:panelGroup id="groupPanel25" 
                                block="true"
                                style="border: 1px groove #999999; padding: 2px; ">

                                <!-- Output-option block: checkbox set -->
                                <h:outputText id="outputText45" value="Output Options"/>
                                <ui:checkboxGroup id="checkboxGroup2"
                                  binding="#{AnalysisPage.checkboxGroup2}" 
                                  items="#{AnalysisPage.checkboxGroup2DefaultOptions.options}"
                                  labelLevel="3" 
                                  selected="#{AnalysisPage.checkboxGroup2DefaultOptions.selectedValue}"
                                  rendered="true"
                                  />

                                <ui:checkboxGroup id="checkboxGroupXtb"
                                  binding="#{AnalysisPage.checkboxGroupXtb}" 
                                  items="#{AnalysisPage.checkboxGroupXtbOptions.options}"
                                  labelLevel="3" 
                                  selected="#{AnalysisPage.checkboxGroupXtbOptions.selectedValue}"
                                  rendered="false"
                                  valueChangeListener="#{AnalysisPage.checkboxGroupXtbProcessValueChange}"
                                  />

                            </ui:panelGroup><!-- groupPanel25 -->

                            <!-- Analysis option block: complex -->

                            <ui:panelGroup id="analysisOptionPanel"
                                binding="#{AnalysisPage.analysisOptionPanel}" 
                                block="true" 
                                separator="&lt;br /&gt;" 
                                style="border: 1px groove #999999; padding: 2px; ">


                                <h:outputText id="outputText46" value="Analysis Options"/>

                            <ui:panelGroup id="groupPanelSetxOption"
                                binding="#{AnalysisPage.setxOptionPanel}" 
                                separator="&lt;br /&gt;"
                                rendered="true"
                                block="true" >
                                <!-- simulation: checkbox -->
                                <ui:checkbox id="checkbox3" 
                                    binding="#{AnalysisPage.checkbox3}" 
                                    immediate="true"
                                    valueChangeListener="#{AnalysisPage.showHideSimulationsOptPanel}"
                                    onClick="submit()"
                                    label="Simulations" />


                                <ui:panelGroup id="groupPanel20" 
                                    binding="#{AnalysisPage.groupPanel20}" 
                                    block="true" 
                                    rendered="false"
                                    style="border: 1px groove rgb(153, 153, 153); padding: 2px; " >

                                    <!-- contional simulation: radio selection -->
                                    <ui:label id="label4" 
                                        labelLevel="3" 
                                        text="#{bundleAnalysis['advStat.simulation.optionPanel']}"/>


                                    <ui:radioButtonGroup id="radioButtonGroup1"
                                        binding="#{AnalysisPage.radioButtonGroup1}" 
                                        items="#{AnalysisPage.radioButtonGroup1DefaultOptions.options}" 
                                        labelLevel="3" 
                                        selected="#{AnalysisPage.radioButtonGroup1DefaultOptions.selectedValue}"
                                        valueChangeListener="#{AnalysisPage.showHideSimCndtnOptPanel}"
                                        immediate="true"
                                        onClick="submit()" />

                                    <!-- gui when the option of select values is chosen-->
                                    <ui:panelGroup id="groupPanel22" 
                                        binding="#{AnalysisPage.groupPanel22}" 
                                        block="true" 

                                        style="border: 1px groove rgb(153, 153, 153); margin: 2px; padding: 2px; "  
                                        rendered="false">

                                        <!--1st option: Explnatory variable values -->

                                        <ui:label id="label1" 
                                            binding="#{AnalysisPage.label1}" 
                                            labelLevel="3" 
                                            text="#{bundleAnalysis['advStat.simulation.setValues.values']}"/>

                                        <h:panelGrid id="gridPanel11" 
                                           binding="#{AnalysisPage.gridPanel11}" columns="3">

                                           <ui:dropDown id="dropDown2"
                                               binding="#{AnalysisPage.dropDown2}"  
                                               items="#{AnalysisPage.setxDiffVarBox1}"
                                               submitForm="false"
                                               immediate="true" />
                                        <!--
                                            <h:selectOneMenu binding="#{AnalysisPage.dropdown3}" id="dropdown3">
                                                <f:selectItems 
                                                   binding="#{AnalysisPage.dropdown3SelectItems}"
                                                   id="dropdown3SelectItems" value="#{AnalysisPage.dropdown3DefaultItems}"/>
                                            </h:selectOneMenu>
                                        -->
                                            <ui:staticText id="staticText2" text=" = "/>
                                            <ui:textField binding="#{AnalysisPage.textField10}" columns="10" id="textField10"/>
                                        </h:panelGrid>

                                        <!-- 2nd option: Value for the first difference(optional)-->

                                        <ui:label id="label2"
                                            binding="#{AnalysisPage.label2}" labelLevel="3" 
                                            text="#{bundleAnalysis['advStat.simulation.setValues.diff']}"/>

                                        <h:panelGrid id="gridPanel10"
                                           binding="#{AnalysisPage.gridPanel10}" columns="3" >

                                           <ui:dropDown id="dropDown3" 
                                               binding="#{AnalysisPage.dropDown3}" 
                                               items="#{AnalysisPage.setxDiffVarBox2}"
                                               submitForm="false"
                                               immediate="true" />

                                            <!--
                                            <h:selectOneMenu id="dropdown2"
                                               binding="#{AnalysisPage.dropdown2}" >
                                                <f:selectItems id="dropdown2SelectItems"
                                                  binding="#{AnalysisPage.dropdown2SelectItems}"
                                                  value="#{AnalysisPage.dropdown2DefaultItems}"/>
                                            </h:selectOneMenu>
                                            -->
                                            <ui:staticText id="staticText1" text=" = "/>

                                            <ui:textField id="textField8" binding="#{AnalysisPage.textField8}" columns="10"/>
                                        </h:panelGrid>

                                    </ui:panelGroup><!-- groupPanel22 -->
                                </ui:panelGroup><!--groupPanel20 -->

                                <!-- checkbox: Sensitivity analysis -->
                                <!-- 
                                <ui:checkbox id="sensitivity"
                                    binding="#{AnalysisPage.sensitivity}" 
                                    label="#{bundleAnalysis['advStat.sensitivity.Option']}"
                                    valueChangeListener="#{AnalysisPage.showHideSensitivityOptPanel}" 
                                    immediate="true"
                                    rendered="false"
                                    visible="false"
                                    onClick="submit();"/>


                                <ui:panelGroup id="groupPanel23"
                                    binding="#{AnalysisPage.groupPanel23}" 
                                    block="true" 
                                    rendered="false"
                                    visible="false">
                                    <ui:label for="advStatSensitivityQ" id="label3" labelLevel="3" text="#{bundleAnalysis['advStat.sensitivity.LevelBox']}"/>
                                    <ui:textField columns="8" id="advStatSensitivityQ" text="0.05"/>
                                </ui:panelGroup>
                                -->
                                <!--groupPanel23 -->
                          </ui:panelGroup>
                                <!-- checkbox: Missing Values -->
                                <ui:checkbox id="advStatNaMethod" 
                                    binding="#{AnalysisPage.advStatNaMethod}"
                                    immediate="true"
                                    name="naMethod"
                                    label="Included Missing Values" 
                                    selectedValue="none"
                                    />
                            </ui:panelGroup><!-- analysisOptionPanel-->


                            <!--  -->
                            <h:commandButton id="advStatBttn" 
                               disabled="true" 
                               binding="#{AnalysisPage.advStatButton}" 
                               value="#{bundleAnalysis['advStat.button.submit']}"
                               action="#{AnalysisPage.advStatAction}"/>

                        <!-- end of  column of opion panes-->
                        </ui:panelGroup><!-- groupPanel9: option panes -->

                    </h:panelGrid><!-- gridPanel8: variable boxes and option panes -->
                    </ui:panelGroup><!-- groupPanel8below -->
                    </h:panelGrid><!-- gridPanel8out-->
                </ui:panelGroup><!-- groupPanel7--><!-- end of the RHS of the tab -->

            </h:panelGrid><!-- gridPanel5 -->
        </ui:panelLayout><!-- layoutPanel5 -->
    </ui:tab>
</ui:tabSet>  
            
        
        <!-- end of tab block -->
        
        
        <h:panelGrid  id="pgSubsettingInstruction"
          cellpadding="0" cellspacing="0"
          columns="1" 
          style="margin-left: 8px; margin-top: 10px" width="98%">
          
          <h:outputText id="txtSubsettingInstruction" 
          binding="#{AnalysisPage.txtSubsettingInstruction}" 
          value="#{bundleAnalysis['subsettingInstruction']}" />
          
          
          <ui:staticText id="msgVariableSelection"
              binding="#{AnalysisPage.msgVariableSelection}" 
              rendered="false"
              escape="false"
              styleClass="errorMessage"/>
        </h:panelGrid>
        
        
        <!-- search box -->
        
        <h:panelGrid  cellpadding="0" cellspacing="0"
          columnClasses="vdcVarHeaderCol1, vdcVarHeaderCol1, vdcVarHeaderCol2" 
          columns="3" id="gridPanel9"
          style="margin-left: 8px; margin-top: 10px" width="750px">
            <!--column 1 -->
            <ui:panelGroup id="groupPanel16">
            
                <h:outputText id="outputText47" rendered="false" styleClass="vdcSubHeaderColor" value="#{bundleAnalysis['varSearch.displaylabel.initial']}"/>
                
                <h:outputText escape="false" id="outputText3" styleClass="vdcSubHeaderColor" value="#{bundleAnalysis['varSearch.displaylabel.afterVarSearch']}" rendered="false" />
                
                <h:outputText id="outputText2" rendered="false" value="#{bundleAnalysis['varSearch.displaylabel.afterSelected']}"/>
                
                
                <h:commandLink id="linkAction3" rendered="false">
                    <h:outputText id="linkAction3Text" value="#{bundleAnalysis['varSearch.displayButton.inital']}"/>
                </h:commandLink>
                
                <h:commandLink  id="linkAction4" rendered="false">
                    <h:outputText id="linkAction4Text" value="#{bundleAnalysis['varSearch.displayButton.afterSearch']}"/>
                </h:commandLink>
                
                <h:commandLink id="linkAction5" rendered="false">
                    <h:outputText id="linkAction5Text" value="#{bundleAnalysis['varSearch.displayButton.afterSelected']}"/>
                </h:commandLink>
                
                
            </ui:panelGroup>
            <!-- column 2-->
            <ui:panelGroup id="groupPanel19">
                <h:inputText binding="#{AnalysisPage.textField4}" id="textField4" rendered="false"/>
                <h:commandButton id="button8" value="#{bundleAnalysis['varSearch.button.search']}" rendered="false"/>
            </ui:panelGroup>
            
            
            <!-- column 3-->
            <ui:panelGroup id="groupPanel17">
                <ui:dropDown id="howManyRows"
                    binding="#{AnalysisPage.howManyRows}"  
                    items="#{AnalysisPage.howManyRowsOptions.options}"
                    selected="#{AnalysisPage.howManyRowsOptions.selectedValue}"
                    onChange="common_timeoutSubmitForm(this.form, '#{AnalysisPage.howManyRowsClientId}');" 
                    valueChangeListener="#{AnalysisPage.howManyRows_processValueChange}"
                    submitForm="false"
                    label="Show "
                    labelLevel="3"
                    immediate="true" />

                    
            </ui:panelGroup>
            
        </h:panelGrid>
        
        
        <!-- data table -->
<h:panelGrid id="pgDataTable">

        <h:dataTable id="dataTable1" 
           binding="#{AnalysisPage.data}"
           var="currentRow" 
           value="#{AnalysisPage.dt4Display}" 
           rows="20"
           cellpadding="0"
           cellspacing="0"
           columnClasses="vdcVariablesCol1, vdcVariablesCol2, vdcVariablesCol3, vdcVariablesCol4, vdcVariablesCol5, vdcVariablesCol6"
           style="margin: 10px 4px 10px 8px; "
           title="Variable Information"
           rowClasses="list-row-odd, list-row-even"  

           width="98%">
            
            <h:column id="column1">
                <f:facet name="header">
                    <!-- h:outputText id="outputText4" value="#{bundleAnalysis['varTable.colHdr.checkBox']}"/ -->
                      <ui:checkbox id="checkboxSelectUnselectAll"
                        binding="#{AnalysisPage.checkboxSelectUnselectAll}"
                        immediate="true"   
                        valueChangeListener="#{AnalysisPage.selectUnselectAllCheckbox}" 
                        onClick="submit();"
                        toolTip="select or unselect all rows in this view"
                        />
                </f:facet>
                
                <ui:checkbox id="varCheckbox"
                  binding="#{AnalysisPage.varCheckbox}"
                  immediate="true"   
                  selected="#{currentRow[0]}" 
                  name="varbl_#{currentRow[2]}"
                  valueChangeListener="#{AnalysisPage.updateCheckBoxState}" 
                  onClick="submit();" >
                </ui:checkbox> 
            </h:column>
            <h:column id="column2">
                <f:facet name="header">
                    <h:outputText id="outputText6" value="#{bundleAnalysis['varTable.colHdr.varType']}"/>
                </f:facet>
                <h:outputText id="outputText5" value="#{currentRow[1]}"/>
            </h:column>
            <h:column id="column3">
                <f:facet name="header"><!---->
                    <h:outputText  id="outputText8" value="#{bundleAnalysis['varTable.colHdr.Id']}" style="visibility:hidden;"/>
                    
                </f:facet><!---->
                <h:outputText id="outputText7" value="#{currentRow[2]}" style="visibility:hidden;"/>
            </h:column>
            <h:column id="column4">
                <f:facet name="header">
                    <h:outputText  id="outputText10" value="#{bundleAnalysis['varTable.colHdr.varName']}"/>
                </f:facet>
                <h:outputText id="outputText9" value="#{currentRow[3]}"/>
            </h:column>
            <h:column id="column5">
                <f:facet name="header">
                    <h:outputText  id="outputText12" value="#{bundleAnalysis['varTable.colHdr.varLabel']}"/>
                </f:facet>
                <h:outputText id="outputText11" value="#{currentRow[4]}"/>
            </h:column>
            <h:column id="column6">
                <f:facet name="header">
                    <h:outputText id="outputText14" value="#{bundleAnalysis['varTable.colHdr.quickSummary']}"/>
                </f:facet>
                
                <!--ui:checkbox id="quickSumStatChkbx" 
                   label="Show Summary"
                   immediate="true"   
                   selected="#{currentRow[6]}" 
                   valueChangeListener="#{AnalysisPage.showNoShowQuickSumStat}" 
                   onClick="submit();" >
                </ui:checkbox  -->
                
                <ui:imageHyperlink   id="showStatistics" 
                    actionListener="#{AnalysisPage.displayQuickSumStat}"
                    imageURL="/resources/icon_subsettable.gif"
                    styleClass="vdcNoBorders" 
                    alt="Click to show descriptive statistics"
                    toolTip="Click to show/hide descriptive statistics"
                    />
                <!--
      
      <ui:panelGroup binding="#{AnalysisPage.gPquickSumStat}" block="true" id="gPquickSumStat" visible="#{currentRow[6]}">  </ui:panelGroup>    
-->
      
                <h:outputText escape="false" id="varSummaryTable" value="#{currentRow[5]}"  rendered="#{currentRow[6]}"/>
                
                
            </h:column>
        </h:dataTable>    
        
        <f:facet name="footer">
            <h:panelGroup id="pgFooterDataTable1">
                <t:scroller navFacetOrientation="NORTH" for="dataTable1" actionListener="#{AnalysisPage.processScrollEvent}">
                    <f:facet name="next">
                        <h:panelGroup>
                            <h:outputText value="Next"/>
                            <h:graphicImage url="/resources/arrow-right.gif" styleClass="vdcNoBorders"/>
                        </h:panelGroup>
                    </f:facet>
                    
                    <f:facet name="previous">
                        <h:panelGroup>
                            <h:outputText value="Previous"/>
                            <h:graphicImage url="/resources/arrow-left.gif" styleClass="vdcNoBorders"/>
                        </h:panelGroup>
                    </f:facet>
                    
                    <f:facet name="number">
                    </f:facet>
                    
                    <f:facet name="current">
                    </f:facet>
                    
                </t:scroller>
            </h:panelGroup>
        </f:facet>
        
</h:panelGrid>
<!---->
<h:panelGrid id="belowDT">

<ui:panelGroup id="pgRecodedVarTable"
  binding="#{AnalysisPage.pgRecodedVarTable}" >

<h:outputText id="recodedVarTableTitle" 
  binding="#{AnalysisPage.recodedVarTableTitle}"
  value="#{bundleAnalysis['recodedVarTable.title']}"/>
<!--
<br />

<h:outputText id="recodedVarTableState" 
  binding="#{AnalysisPage.recodedVarTableState}"
  value="#{bundleAnalysis['recodedVarTable.state']}"/>
-->
<h:dataTable id="recodedVarTable"
  binding="#{AnalysisPage.recodedVarTable}" 
  var="rcw"
  value="#{AnalysisPage.recodedVarSet}"
  headerClass="list-header"  rowClasses="list-row-even,list-row-odd"
  cellpadding="0"
  cellspacing="0"
  columnClasses="vdcVariablesCol3, vdcVariablesCol4, vdcVariablesCol5, vdcVariablesCol2"
  style="margin: 10px 4px 10px 8px;">
  
  <!-- chekcbox: remove --><!---->
  <h:column id="RVTcolumn0">
    <f:facet name="header">
      <h:outputText id="RVThdr0" value="#{bundleAnalysis['recodedVarTable.colHdr.remove']}"/>
    </f:facet>
<!--
        <ui:checkbox id="removeRecodedVarCheckbox" 
          immediate="true"   
          selected="#{rcw[0]}" 
          valueChangeListener="#{AnalysisPage.removeRecodedVar}" 
          onClick="submit();" >
        </ui:checkbox>
-->

    <ui:hyperlink id="removeRecodedVariable" 
      actionListener="#{AnalysisPage.removeRecodedVariable}"
      immediate="true" 
      text="#{bundleAnalysis['recodedVarTable.hyperLink.title.remove']}" 
      toolTip="#{bundleAnalysis['recodedVarTable.tooltip.remove']}" /> 
        
  </h:column>

  <!-- variable name --><!---->
  <h:column id="RVTcolumn1">
    <f:facet name="header">
      <h:outputText id="RVThdr1" value="#{bundleAnalysis['recodedVarTable.colHdr.varName']}"/>
    </f:facet>
    <h:outputText id="RVTcolv1" value="#{rcw[0]}"/>
  </h:column>

  <!-- variable label --><!---->
  <h:column id="RVTcolumn2">
    <f:facet name="header">
      <h:outputText id="RVThdr2" value="#{bundleAnalysis['recodedVarTable.colHdr.varLabel']}"/>
    </f:facet>
    <h:outputText id="RVTcolv2" value="#{rcw[1]}"/>
  </h:column>

  <!-- checkbox: modify --><!---->
  <h:column id="RVTcolumn3">
    <f:facet name="header">
      <ui:staticText  id="RVThdr3" text="#{bundleAnalysis['recodedVarTable.colHdr.modify']}"  visible="false" />
    </f:facet>
    <!-- h:outputText id="RVTcolv3" value="#{rcw[3]}"/ -->
    <ui:hyperlink id="RVTcolv3" 
      actionListener="#{AnalysisPage.editRecodedVariable}"
      immediate="true" 
      text="#{bundleAnalysis['recodedVarTable.hyperLink.title.modify']}" 
      toolTip="#{bundleAnalysis['recodedVarTable.tooltip.modify']}"
       visible="false"
      /> 
    
  </h:column>
  <!-- variable Id  --><!---->
  <h:column id="RVTcolumn4" >
    <f:facet name="header">
      <h:outputText id="RVThdr4" value=""/>
    </f:facet>
    <ui:staticText id="RVTcolv4" text="#{rcw[2]}" visible="false"/> 
    <!-- h:outputText id="RVTcolv4" value="#{rcw[2]}" / -->
  </h:column>

</h:dataTable>

</ui:panelGroup>
</h:panelGrid>

        
        
        
        </h:panelGrid><!--gridPanel1 -->
      </ui:panelLayout><!--PLbelowForm1 -->
        
    </h:form>
<!-- hr / -->

  <script type="text/javascript">
  <![CDATA[
  function testJs(){
    alert("js test");
  }
  
  function checkParameters(){
    alert("request is submitted");
  }

// for a checkbox
  function showHideMonitorBlock(id, shw){
    var trgt = document.getElementById(id);
    if (shw) {
      trgt.style.display="";
    } else {
      trgt.style.display="none";
    }
  }
 
// show-no show button with a state-dependent button label
function shwNshwLnk(id, srcId, label) {
  // id     whose block to be shown/hidden
  // srcId  button id
  // label  button lable
  var srcEl = document.getElementById(id);
  
  if (srcEl.style.display == "none") {
    srcEl.style.display = "";
    // this part depends on the design of a button
    document.getElementById(srcId).value="Hide " +label;
  } else if (srcEl.style.display == "") {
    srcEl.style.display = "none";
    // this part depends on the design of a button
    document.getElementById(srcId).value ="Show " +label;
  }
}
 
  //]]>
  </script>
  
<input type="button" id="monitorBttn" style="display:none" value="show monitor block" onclick="shwNshwLnk('monitorBlock', 'monitorBttn', 'monitor block');"/>
<div id="monitorBlock" style="display:none">

<h4>requested parameters</h4> 
<table border="1">
  <jsp:scriptlet>
<![CDATA[  
    for(Iterator itr = es.iterator();itr.hasNext();){
    Map.Entry entry = (Map.Entry)itr.next();
    StringBuffer sb = new StringBuffer();
    sb.append("<tr><td>"+ entry.getKey() + "</td><td>");
    String[] arr = (String[]) entry.getValue();
    for(int i=0;i<arr.length;i++){
      sb.append(arr[i]);
    }
    sb.append("</td></tr>");
    out.println(sb.toString());
    } 
]]>
</jsp:scriptlet>

</table>

<h4>Request Header</h4>
<table border="1">
<jsp:scriptlet>
<![CDATA[  
  
    for( int i = 0; i < vec.size(); i++ ) {
]]>
</jsp:scriptlet>
<jsp:expression>vec.get(i)</jsp:expression>
<jsp:scriptlet>
    }
</jsp:scriptlet>
</table>
<hr />
</div>


<!-- to be renamed to subview -->
</f:subview>

</jsp:root>

