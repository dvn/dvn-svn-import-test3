<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:t="/WEB-INF/tlds/scroller"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">
    
<f:subview id="searchPageView">
    <ui:form id="form1">
        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}" />
        <input type="hidden" name="pageName" value="SearchPage" />
        <script language="javascript">
           // workaround for issue where f:selectItems does not provide setStyle or setStyleClass wbossons
            function setSearchOptionsClass() {
                var table = document.getElementById('content:searchPageView:form1:radioButtonList1'); //content:searchPageView:form1:radioButtonList1
                var cells = table.getElementsByTagName('TD');
                for (var i = 0; i &lt; cells.length; i++) {
                    cells[i].className="vdcSearchOptions";
                }
            }
        </script>

        <h:inputHidden id="studyListingIndex" value="#{SearchPage.studyListingIndex}" />
      
      <!-- Main Section starts here -->
      <div id="dvn_mainSection">
          <div id="dvn_mainSectionTitle">
          <h:outputText value="Dataverses" rendered="#{VDCRequest.currentVDC == null}" />
          <h:outputText value="Studies" rendered="#{VDCRequest.currentVDC != null}" />
          </div>
              
      <!-- Search Section starts here -->
      <div class="dvn_searchblock">
        <div class="dvn_searchBoxContent">
            <div class="dvn_searchTitle">Search</div>
            <fieldset>
                <label id="dvn_searchMenuFilter" for="filter">
                    <h:selectOneMenu id="dropdown3" value="#{SearchPage.searchFilter}">
                            <f:selectItem itemLabel="All Collections" itemValue="allCollections" />
                            <f:selectItem itemLabel="This Collection" itemValue="thisCollection" />
                    </h:selectOneMenu>
                </label>
                <label for="options">
                    <h:selectOneMenu id="dropdown1" value="#{SearchPage.searchField}">
                        <f:selectItem itemLabel="Cataloging Information" itemValue="any" />
                        <f:selectItem itemLabel="- Author" itemValue="authorName" />
                        <f:selectItem itemLabel="- Title" itemValue="title" />
                        <f:selectItem itemLabel="- Study ID" itemValue="studyId" />
                        <f:selectItem itemLabel="Variable Information" itemValue="variable" />
                    </h:selectOneMenu>
                </label>
                <label for="search">
                    <span>for -</span><h:inputText onkeypress="if (window.event) return processEvent('', 'content:searchPageView:form1:search'); else return processEvent(event, 'content:searchPageView:form1:search');" id="textField2" value="#{SearchPage.searchValue}" />
                </label>
                <label for="button">
                    <h:commandButton id="search" value="Search" action="#{SearchPage.search_action}" />
                </label>
           </fieldset>
           <div class="dvn_searchLinks">
                <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/AdvSearchPage.jsp">
                    <h:outputText value="Advanced Search" />
                </h:outputLink> <h:outputText  value="&#160;" escape="false" />            
            </div>
        </div>
      </div>
  
    <script language="javascript">
        onLoad=setSearchOptionsClass();
    </script>
    <!-- Search Section ends here -->
                   
    <!-- Start Search Results (with side panel, if available) -->      
      <ui:panelLayout styleClass="#{ (SearchPage.renderTree) ? 'dvn_section dvn_overflow' : 'dvn_section'}" >  
           <ui:panelLayout styleClass="#{ (SearchPage.renderTree) ? 'dvn_sectionContent' : 'dvn_sectionContentNoPanels'}" >    	
              <div class="dvn_sectionTitle">
                  <a name="recent" title="">
                      <h:outputText value="#{SearchPage.listHeader}" />
                  </a>
              </div>            
                  <div class="dvn_sectionBox">  
                     <div class="dvn_margin12">
                       <h:outputText styleClass="vdcSubHeaderColor" value="#{SearchPage.subListHeader}" />
                       
                       <div style="padding-top: 1em; padding-bottom: 1em;">
                          <h:outputText id="listMessagePrefix" value="#{SearchPage.listMessagePrefix}" />
                          <h:outputText id="listMessageContent" styleClass="warnMessage" value="#{SearchPage.listMessageContent}" />
                          <h:outputText id="listMessageSuffix" value="#{SearchPage.listMessageSuffix}" />
                      </div>
                      
                      <h:panelGrid cellpadding="0" cellspacing="0" columns="2" id="gridPanel2" width="98%"
                                    styleClass="vdcSResultsTop" rendered="#{SearchPage.renderSort or SearchPage.renderScroller}">
                          <ui:panelGroup id="groupPanel5" rendered="#{SearchPage.renderSort}">
                              <h:selectOneMenu id="dropdown2" valueChangeListener="#{SearchPage.sort_action}" onchange="submit();">
                                  <f:selectItem itemLabel="Sort By:" itemValue="" />
                                  <f:selectItem itemLabel="- Study ID" itemValue="studyId" />
                                  <f:selectItem itemLabel="- Title" itemValue="title" />
                              </h:selectOneMenu>
                          </ui:panelGroup>
                          
                          <ui:panelGroup block="true" id="groupPanel4" style="text-align: right" rendered="#{SearchPage.renderScroller}">
                              <t:scroller binding="#{SearchPage.scroller}" for="dataTable1" actionListener="#{SearchPage.scroll_action}">
                                  <f:facet name="previous">
                                      <h:graphicImage id="scrollBack" value="/resources/arrow-left.gif" styleClass="vdcNoBorders" />
                                  </f:facet>
                                  <f:facet name="next">
                                      <h:graphicImage id="scrollNext" value="/resources/arrow-right.gif" styleClass="vdcNoBorders" />
                                  </f:facet>                                            
                              </t:scroller>
                          </ui:panelGroup>
                      </h:panelGrid>
                      
                      <h:dataTable binding="#{SearchPage.studyTable}" rows="10" headerClass="list-header" id="dataTable1" width="98%"
                                    rowClasses="list-row-odd, list-row-even" columnClasses="vdcSResultsList" value="#{SearchPage.studies}" var="studyUI">
                          <h:column id="column1">
                              <ui:panelGroup block="true" id="groupPanel1" style="padding-bottom: 5px">
                                  
                                  <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/study/StudyPage.jsp?studyId=#{studyUI.study.id}&amp;studyListingIndex=#{SearchPage.studyListingIndex}"  id="hyperlink3" styleClass="vdcSResultsStudyTitle">
                                      <h:outputText id="hyperlink3Text" value="#{studyUI.study.title}" />
                                  </h:outputLink>
                                  <h:outputText id="outputText13" value="by #{studyUI.authors}" rendered="#{studyUI.authors != null and studyUI.authors != ''}" />
                                  <h:outputText styleClass="dvn_SResultsGlobalId" value=" (#{studyUI.study.globalId})" />
                              </ui:panelGroup> 
                              <ui:panelGroup block="true" id="groupPanel1b">
                                  <h:graphicImage alt="This study contains no files." rendered="#{!studyUI.files}"
                                                  title="This study contains no files." value="/resources/icon_nofiles.gif" />
                                  <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/study/StudyPage.jsp?studyId=#{studyUI.study.id}&amp;tab=files&amp;studyListingIndex=#{SearchPage.studyListingIndex}" rendered="#{studyUI.files}">
                                      <h:graphicImage styleClass="vdcNoBorders" alt="This study contains subsettable files." rendered="#{studyUI.subsettable}"
                                                      title="This study contains subsettable files." value="/resources/icon_subsettable.gif" />
                                      <h:graphicImage styleClass="vdcNoBorders" alt="This study contains only non-subsettable files." rendered="#{!studyUI.subsettable}"
                                                      title="This study contains only non-subsettable files." value="/resources/icon_files.gif" />
                                  </h:outputLink>
                                  <h:outputText id="outputText18" value=" #{studyUI.numberOfDownloads}" rendered="#{studyUI.files}" /><h:outputText value=" downloads" rendered="#{studyUI.files and studyUI.numberOfDownloads != 1}" /><h:outputText value=" download" rendered="#{studyUI.files and studyUI.numberOfDownloads == 1}" />
                              </ui:panelGroup>
                              
                              <ui:panelGroup block="true" id="groupPanel2" rendered="#{studyUI.abstracts != null and studyUI.abstracts != ''}" >
                                  <h:outputText id="outputText10" styleClass="vdcSResultsField" value="Abstract:" />
                                  <ui:panelGroup block="true" styleClass="vdcAbstractSResults"> 
                                      <h:outputText  id="outputText14" escape="false" value="#{studyUI.truncatedAbstracts}" />
                                  </ui:panelGroup>
                              </ui:panelGroup>
                              <ui:panelGroup block="true" id="groupPanel7" rendered="#{studyUI.study.productionDate != null and studyUI.study.productionDate != ''}">
                                  <h:outputText id="outputText15" styleClass="vdcSResultsField" value="Production Date:" />
                                  <h:outputText id="outputText11" value="#{studyUI.study.productionDate}" />
                              </ui:panelGroup>
                              
                              <!-- Optional search fields  -->
                              <ui:panelGroup block="true" rendered="#{!empty SearchPage.studyFields[sfc.producerName]  and !empty studyUI.producers}">
                                  <h:outputText value="Producer:" styleClass="vdcSResultsField" />
                                  <h:outputText value="#{studyUI.producers}" escape="false" />
                              </ui:panelGroup>
                              <ui:panelGroup block="true" rendered="#{!empty SearchPage.studyFields[sfc.distributionDate]  and !empty studyUI.study.distributionDate}">
                                  <h:outputText value="Distribution Date:" styleClass="vdcSResultsField" />
                                  <h:outputText value="#{studyUI.study.distributionDate}" escape="false" />
                              </ui:panelGroup>
                              <ui:panelGroup block="true" rendered="#{!empty SearchPage.studyFields[sfc.distributorName]  and !empty studyUI.distributors}">
                                  <h:outputText value="Distributor:" styleClass="vdcSResultsField" />
                                  <h:outputText value="#{studyUI.distributors}" escape="false" />
                              </ui:panelGroup>
                              <ui:panelGroup block="true" rendered="#{!empty SearchPage.studyFields[sfc.replicationFor]  and !empty studyUI.study.replicationFor}">
                                  <h:outputText value="Replication For:" styleClass="vdcSResultsField" />
                                  <h:outputText value="#{studyUI.study.replicationFor}" escape="false" />
                              </ui:panelGroup>
                              <ui:panelGroup block="true" rendered="#{!empty SearchPage.studyFields[sfc.relatedPublications]  and !empty studyUI.relPublications}">
                                  <h:outputText value="Related Publications:" styleClass="vdcSResultsField" />
                                  <h:outputText value="#{studyUI.relPublications}" escape="false" />
                              </ui:panelGroup>
                              <ui:panelGroup block="true" rendered="#{!empty SearchPage.studyFields[sfc.relatedMaterial]  and !empty studyUI.relMaterials}">
                                  <h:outputText value="Related Material:" styleClass="vdcSResultsField" />
                                  <h:outputText value="#{studyUI.relMaterials}" escape="false" />
                              </ui:panelGroup>
                              <ui:panelGroup block="true" rendered="#{!empty SearchPage.studyFields[sfc.relatedStudies]  and !empty studyUI.relStudies}">
                                  <h:outputText value="Related Studies:" styleClass="vdcSResultsField" />
                                  <h:outputText value="#{studyUI.relStudies}" escape="false" />
                              </ui:panelGroup>
                              
                              <ui:panelGroup block="true" rendered="#{ !empty studyUI.foundInVariables}">
                                  <h:outputText value="Found In Variables:" styleClass="vdcSResultsField" />
                                  <h:dataTable id="dvDataTable" value="#{studyUI.foundInVariables}" var="dv" width="100%">
                                      <h:column>
                                          <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/study/VariablePage.jsp?dvId=#{dv.id}">
                                              <h:outputText value="#{dv.name}" />
                                          </h:outputLink>
                                      </h:column>
                                      <h:column>
                                          <h:outputText value="#{dv.label}" />
                                      </h:column>                                            
                                      <h:column>
                                          <h:outputText value="#{dv.dataTable.studyFile.fileName}" />
                                      </h:column>
                                  </h:dataTable>
                              </ui:panelGroup>                                    
                          
                          </h:column>
                      </h:dataTable>
                      
                  </div>
              </div>   
          </ui:panelLayout>
          
          <ui:panelGroup block="true" rendered="#{SearchPage.renderTree}" styleClass="dvn_sectionPanels">
              <div class="dvn_block"><h3><h:outputText value="#{SearchPage.treeHeader}"/></h3>
                <ui:tree binding="#{SearchPage.collectionTree}" id="collectionTree" text="" />
              </div>    
          </ui:panelGroup>
          
      </ui:panelLayout>   
    
      </div>
      <!-- Main Section ends here -->
      
        </ui:form>
    </f:subview>
</jsp:root>
