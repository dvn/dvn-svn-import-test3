<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
<f:subview id="OAISetsPageView">
    <ui:form id="form1">
        <div class="dvn_section">
            <div class="dvn_sectionTitle">
                 
                    <h:outputText value="OAI Sets defined in #{VDCRequest.vdcNetwork.name} Dataverse Network"/>
                
            </div>            
            <div class="dvn_sectionBox"> 
                <div class="dvn_margin12">
                    <div style="padding-bottom: 15px;">
                        <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                        <h:outputText  styleClass="vdcHelpText" value=" The sets defined here are used by the OAI server that comes with your DVN. Another DVN (or any other remote site) 
                        will be able to harvest the sets you define. If no sets are defined, the default OAI call (http://localhost/dvn/OAIHandler) will return all the studies owned by this DVN.
                        Once you've created a set, you can edit it by clicking on the set name."/>
                    </div>
                      
                      <h:outputLink value="EditOAISetPage.jsp">
                        <h:outputText value="Create a New Set"/>
                    </h:outputLink>
                    
                    <h:dataTable cellpadding="0" cellspacing="0" binding="#{OAISetsPage.dataTable}"
                                 columnClasses="vdcColPadded, vdcColPadded" headerClass="list-header-left" id="dataTable1"
                                 rowClasses="list-row-even,list-row-odd" value="#{OAISetsPage.oaiSets}" var="currentRow" width="100%">
                        <h:column id="column1">
                            <f:facet name="header">
                                <h:outputText id="column1Header" value="Set Name"/>
                            </f:facet>
                            <h:outputLink value="EditOAISetPage.jsp?oaiSetId=#{currentRow.id}">
                                <h:outputText value="#{currentRow.name}"/>
                            </h:outputLink>
                        </h:column>
                        <h:column id="column2">
                            <f:facet name="header">
                                <h:outputText id="column2Header" value="Spec"/>
                            </f:facet>
                                <h:outputText value="#{currentRow.spec}"/>
                         
                        </h:column>
                        <h:column id="column3">
                            <f:facet name="header">
                                <h:outputText id="column3Header" value="Query Definition"/>
                            </f:facet>
                            <h:outputText value="#{currentRow.definition}"/>
                        </h:column>
                       <h:column id="column4">
                            <f:facet name="header">
                                <h:outputText id="column4Header" value="Description"/>
                            </f:facet>
                            <h:outputText value="#{currentRow.description}"/>
                        </h:column>
                              
                        <h:column id="column5">
                            <f:facet name="header">
                                <h:outputText  value="Delete"/>
                            </f:facet>
                            <h:commandLink actionListener="#{OAISetsPage.deleteSet}">
                                <h:outputText value="Delete Set"/>
                            </h:commandLink>
                        </h:column>
                        
                    </h:dataTable>
                    
                </div>
            </div>
        </div>
    </ui:form>
    </f:subview>
</jsp:root>
