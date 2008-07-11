<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:gui="http://java.sun.com/jsf/facelets"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core" 
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:ui="http://www.sun.com/web/ui"
      >
<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

</head>

<body>
<gui:composition template="/template.xhtml">

<gui:param name="pageTitle" value="DVN - Manage Collections" />

  <gui:define name="body">
                            
        <ui:form binding="#{ManageCollectionsPage.form1}" id="form1">   
         <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
         
         <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    
                        <h:outputText binding="#{ManageCollectionsPage.outputText1}" value="Manage Collections"/>
                    
                </div>            
                <div class="dvn_sectionBox"> 
                        <div class="dvn_margin12"> 
                            
                            <ui:panelLayout styleClass="#{ManageCollectionsPage.msg.styleClass}" rendered="#{!empty ManageCollectionsPage.msg.messageText}">
                                <h:outputText id="statusMessage" value="#{ManageCollectionsPage.msg.messageText}" />
                            </ui:panelLayout>
                            
                            <ui:panelGroup block="true" style="padding-bottom:10px">
                                <h:outputLink id="hyperlinkAddStudiesCollection" value="/dvn#{VDCRequest.currentVDCURL}/faces/collection/AddCollectionStudiesPage.jsp">
                                    <h:outputText  id="hyperlink3Text2" value="Add Collection by Assigning Studies"/>
                                </h:outputLink>
                               | 
                                <h:outputLink id="hyperlinkAddQueryCollection" value="/dvn#{VDCRequest.currentVDCURL}/faces/collection/AddCollectionQueryPage.jsp">
                                    <h:outputText  id="hyperlink3Text1" value="Add Collection as a Query"/>
                                </h:outputLink>
                                |
                                <h:outputLink  value="/dvn#{VDCRequest.currentVDCURL}/faces/collection/AddLinkPage.jsp">
                                    <h:outputText value="Add Collection Link"/>
                                </h:outputLink>
                            </ui:panelGroup>
                            <ui:panelGroup block="true">    
                                <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                <h:outputText  styleClass="vdcHelpText" value="To edit a collection, click the collection name. Collection Links (links to collection from other Dataverses) are only editable in their original Dataverse."/>
                            </ui:panelGroup>
                            
                            <ui:panelGroup binding="#{ManageCollectionsPage.groupPanel1}" block="true" id="groupPanel1" style="background-color: rgb(226, 226, 226); font-size: 1em; margin-right: 10px; margin-top: 20px; margin-bottom: 5px; padding-top: 10px; padding-bottom: 5px">
                               <!--
                                <h:outputText binding="#{ManageCollectionsPage.outputText2}" id="outputText2" style="padding-left: 30px" value="Change Status to: "/>
                                <h:selectOneMenu binding="#{ManageCollectionsPage.dropdown1}" id="dropdown1">
                                    <f:selectItems binding="#{ManageCollectionsPage.dropdown1SelectItems}" id="dropdown1SelectItems" value="#{ManageCollectionsPage.dropdown1DefaultItems}"/>
                                </h:selectOneMenu>
                                <h:commandButton binding="#{ManageCollectionsPage.changeStatusButton}" action="#{ManageCollectionsPage.changeStatusButton_action}" id="changeStatusButton" value="Change Status"/>
                                -->
                                <h:commandButton binding="#{ManageCollectionsPage.removeButton}" action="#{ManageCollectionsPage.removeButton_action}" id="removeButton" style="margin-left: 30px"
                                                 value="Remove"/>
                                <h:outputText  styleClass="vdcHelpText" value="Check collection(s) you wish to remove from your dataverse and then click the Remove button."/>                 
                            </ui:panelGroup>
                            <h:dataTable binding="#{ManageCollectionsPage.dataTable1}" cellpadding="0" cellspacing="0" headerClass="" id="dataTable1"
                                         style="padding-left: 10px; margin-top: 20px" columnClasses="vdcColPadded" value="#{ManageCollectionsPage.dataTable1Model}" var="currentRow" width="100%">
                                <h:column binding="#{ManageCollectionsPage.column1}" id="column1">
                                    <h:selectBooleanCheckbox  id="checkbox1" style="margin-left: 10px; margin-right:  #{currentRow['level']}0px;"  value="#{currentRow['selected']}"/>
                                    <h:outputLink disabled="#{currentRow['link']}" id="hyperlink3" value="/dvn#{VDCRequest.currentVDCURL}/faces/collection/#{currentRow['queryType'] ? 'AddCollectionQueryPage' : 'AddCollectionStudiesPage' }.jsp?collectionId=#{currentRow['id']}&amp;parentId=#{currentRow['parentId']}">
                                        <h:outputText binding="#{ManageCollectionsPage.outputText11}" id="outputText11" value="#{currentRow['name']}"/>
                                    </h:outputLink>
                                </h:column>
                            </h:dataTable>
                            
                        </div>
                    </div>
                </div>
        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
