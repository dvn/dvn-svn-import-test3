<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
       <f:subview id="AllUsersPageView">
                    <ui:form id="form1">
                            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>

                            <ui:panelLayout id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddle">
                                <ui:panelLayout id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText value="All Users in #{VDCRequest.vdcNetwork.name} Dataverse Network"/>
                                </ui:panelLayout>
                                <ui:panelLayout id="layoutPanel3" panelLayout="flow" style="padding: 30px 40px 30px 40px; ">
                                    <ui:panelGroup block="true" style="padding-bottom: 15px">
                                        <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                        <h:outputText styleClass="vdcHelpText" value="The default network admin has admin privileges to all Dataverses (note that only Dataverses created by the network admin will be listed here under his/her name)"/>
                                    </ui:panelGroup>
                                    <h:dataTable binding="#{AllUsersPage.dataTable}" cellpadding="0" cellspacing="0"
                                        columnClasses="vdcColPadded, vdcColPadded, vdcColPadded, vdcColPadded" headerClass="list-header-left" id="dataTable1"
                                        rowClasses="list-row-even,list-row-odd" value="#{AllUsersPage.userData}" var="currentRow" width="100%">
                                        <h:column id="column1">
                                            <f:facet name="header">
                                                <h:outputText id="outputText2" value="Username"/>
                                            </f:facet>
                                            <h:outputLink id="hyperlink2"  value="../login/AccountPage.jsp?userId=#{currentRow.user.id}&amp;vdcId=#{VDCRequest.currentVDCId}" >
                                                <h:outputText id="hyperlink2Text1" value="#{currentRow.user.userName}"/>
                                            </h:outputLink>
                                        </h:column>
                                        <h:column id="column3">
                                            <f:facet name="header">
                                                <h:outputText id="outputText5" value="Full Name"/>
                                            </f:facet>
                                            <h:outputText id="outputText6" value="#{currentRow.user.firstName} "/>
                                            <h:outputText  value="#{currentRow.user.lastName}"/>
                                        </h:column>
                                        <h:column id="column2">
                                            <f:facet name="header">
                                                <h:outputText id="outputText3" value="Role/s"/>
                                            </f:facet>
                                            <h:outputText id="outputText4" value="#{currentRow.roles}"/>
                                        </h:column>
                                        <h:column>
                                                <f:facet name="header">
                                                    <h:outputText id="outputText10" value="Status"/>
                                                </f:facet>
                                                <h:outputText value="Active" rendered="#{currentRow.user.active}"/>
                                                <h:outputText value="Inactive" rendered="#{!currentRow.user.active}"/>
                                        </h:column>
                                        <h:column>
                                            <h:commandLink actionListener="#{AllUsersPage.deactivateUser}" rendered="#{currentRow.user.active and !currentRow.defaultNetworkAdmin}">
                                                <h:outputText value="Deactivate"/>
                                            </h:commandLink>
                                            <h:commandLink actionListener="#{AllUsersPage.activateUser}" rendered="#{!currentRow.user.active and !currentRow.defaultNetworkAdmin}">
                                                <h:outputText value="Activate"/>
                                            </h:commandLink>
                                            <h:outputText value="Cannot Deactivate Network Admin" rendered ="#{currentRow.defaultNetworkAdmin}"/>

                                        </h:column>
                                    </h:dataTable>
                                </ui:panelLayout>
                            </ui:panelLayout>
                    </ui:form>
         </f:subview>
</jsp:root>
