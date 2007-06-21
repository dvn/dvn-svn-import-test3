<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
       <f:subview id="HarvestSitesPageView">
                    <ui:form  id="form1">
                        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                             <ui:panelLayout id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddleNoBorder">
                                <h:panelGrid cellpadding="0" cellspacing="0" columns="1" width="100%">
                                <ui:panelLayout  id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText value="Manage Harvest Dataverses"/>
                                </ui:panelLayout>
                                <ui:panelLayout id="layoutPanel3" panelLayout="flow" style="padding: 40px 40px 30px 40px; border: 1px solid #cccccc; ">
                                    <ui:panelGroup block="true" style="padding-bottom: 10px">
                                                 <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                                 <h:outputText  styleClass="vdcHelpText" escape="false" value="Help Text" />
                                    </ui:panelGroup>

                                    <h:dataTable cellpadding="0" cellspacing="0"
                                                binding="#{HarvestSitesPage.harvestDataTable}"
                                                 headerClass="list-header-left vdcColPadded" id="dataTable1" 
                                                 rowClasses="list-row-even vdcColPadded, list-row-odd vdcColPadded" 
                                                 value="#{HarvestSitesPage.harvestSiteList}" var="currentRow" width="100%">
                                        <h:column >
                                            <f:facet name="header">
                                                <h:outputText id="outputText2" value="Harvest Dataverse"/>
                                            </f:facet>
                                            <h:outputText  value="#{currentRow.vdc.name}" rendered="#{currentRow.harvestingNow}" />
                                            <h:outputLink rendered="#{!currentRow.harvestingNow}" id="hyperlink1" value="EditHarvestSitePage.jsp?harvestId=#{currentRow.id}">
                                                <h:outputText id="hyperlink1Text1" value="#{currentRow.vdc.name}"/>
                                            </h:outputLink>
                                        </h:column>
                                        <h:column >
                                            <f:facet name="header">
                                                <h:outputText  id="outputText4" value="Status"/>
                                            </f:facet>
                                            <h:outputText  id="outputText4b" value="Scheduled" rendered="#{currentRow.scheduled}"/>
                                           <h:outputText  id="outputText4c" value="Not Scheduled" rendered="#{!currentRow.scheduled}"/>
                                        </h:column>
                                         <h:column  >
                                            <f:facet name="header">
                                                <h:outputText value=""/>
                                            </f:facet>
                                            <h:commandButton value="Schedule Harvesting" rendered="#{!currentRow.scheduled}" actionListener="#{HarvestSitesPage.doSchedule}"/>
                                            <h:commandButton value="Unschedule Harvesting" rendered="#{currentRow.scheduled}" actionListener="#{HarvestSitesPage.doUnschedule}"/>
                                         
                                        </h:column>
                                         <h:column  >
                                            <f:facet name="header">
                                                <h:outputText  value=""/>
                                            </f:facet>
                                            <h:commandButton value="Run Harvester Now"  rendered="#{!currentRow.harvestingNow}"   actionListener="#{HarvestSitesPage.doRunNow}"/>
                                            <h:outputText  value="Harvesting Currently Running" rendered="#{currentRow.harvestingNow}" />   
                                         
                                        </h:column>
                                        <h:column >
                                            <f:facet name="header">
                                                <h:outputText id="outputText3" value="Remove"/>
                                            </f:facet>
                                            <h:commandButton  value="Remove"  disabled="#{currentRow.harvestingNow}" actionListener="#{HarvestSitesPage.doRemoveHarvestDataverse}"/>
                                        </h:column>
                                        
                                    </h:dataTable>
                                   
                                </ui:panelLayout>
                                 <ui:panelLayout  panelLayout="flow" style="padding: 40px 40px 30px 40px; border: 1px solid #cccccc; ">
                                    <ui:panelGroup block="true" style="padding-bottom: 10px">
                                                 <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                                 <h:outputText  styleClass="vdcHelpText" escape="false" value="Help Text" />
                                    </ui:panelGroup>

                                    <h:dataTable cellpadding="0" cellspacing="0"
                                                binding="#{HarvestSitesPage.dataverseDataTable}"
                                                 headerClass="list-header-left vdcColPadded" 
                                                 rowClasses="list-row-even vdcColPadded, list-row-odd vdcColPadded" 
                                                 value="#{HarvestSitesPage.dataverseSiteList}" var="currentRow" width="100%">
                                        <h:column >
                                            <f:facet name="header">
                                                <h:outputText  value="Dataverse"/>
                                            </f:facet>
                                            <h:outputLink value="/dvn/faces/admin/OptionsPage.jsp?currentVDCId=#{currentRow.id}">
                                                <h:outputText id="hyperlink1Text1" value="#{currentRow.name}"/>
                                            </h:outputLink>
                                        </h:column>
                                       
                                        
                                        <h:column >
                                            <f:facet name="header">
                                                <h:outputText  value="Remove"/>
                                            </f:facet>
                                            <h:commandButton  value="Remove" actionListener="#{HarvestSitesPage.doRemoveDataverse}"/>
                                        </h:column>
                                        
                                    </h:dataTable>
                                   
                                </ui:panelLayout>
                              </h:panelGrid>
                            </ui:panelLayout>
                    </ui:form>          
    </f:subview>
</jsp:root>
