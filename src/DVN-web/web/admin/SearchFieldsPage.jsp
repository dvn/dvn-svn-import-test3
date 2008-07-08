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
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />

</head>

<body>
<gui:composition template="/template.xhtml">


<gui:param name="pageTitle" value="DVN - Search Results Fields" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>


    <ui:form  id="form1">
        <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
        
        <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    
                        <h:outputText value="Additional Fields in Search Results"/>
                    
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">  
                        <ui:panelLayout styleClass="#{SearchFieldsPage.msg.styleClass}" rendered="#{!empty SearchFieldsPage.msg.messageText}">
                            <h:outputText id="statusMessage" value="#{SearchFieldsPage.msg.messageText}" />
                        </ui:panelLayout>    
                        
                        <h:outputText value="Select any of the cataloging information fields below to be displayed in the search results page (or studies listing). If selected, these fields will be displayed below the default information (Study ID, title, authors, production date, abstract), and will only be displayed if they are available in the study cataloging information. These settings apply to this Dataverse only."  />
                        
                        <ui:panelGroup  block="true" style="padding-top: 10px;">
                            <h:selectBooleanCheckbox binding="#{SearchFieldsPage.producerCheckbox}" id="producercheckbox" value="#{SearchFieldsPage.producerResults}" /> 
                            <h:outputLabel for="producercheckbox" value="Producer"/>
                            <ui:panelGroup  block="true" style="padding-left: 20px;">
                                <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />  
                                <h:outputText value="The Producer name, logo and URL will be displayed, if they are available." styleClass="vdcHelpText" />
                            </ui:panelGroup>
                        </ui:panelGroup>
                        
                        <ui:panelGroup  block="true" style="padding-top: 10px;">
                            <h:selectBooleanCheckbox binding="#{SearchFieldsPage.distributionDateCheckbox}" id="distributiondatecheckbox"  value="#{SearchFieldsPage.distributionDateResults}"/> 
                            <h:outputLabel for="distributiondatecheckbox" value="Distribution Date"/>
                        </ui:panelGroup>
                        
                        <ui:panelGroup  block="true" style="padding-top: 10px;">
                            <h:selectBooleanCheckbox binding="#{SearchFieldsPage.distributorCheckbox}" id="distributorcheckbox" value="#{SearchFieldsPage.distributorResults}"/> 
                            <h:outputLabel for="distributorcheckbox" value="Distributor"/>
                            <ui:panelGroup  block="true" style="padding-left: 20px;">
                                <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />  
                                <h:outputText value="The Distributor name, logo and URL will be displayed, if they are available." styleClass="vdcHelpText" />
                            </ui:panelGroup>
                        </ui:panelGroup>
                        
                        <ui:panelGroup  block="true" style="padding-top: 10px;">
                            <h:selectBooleanCheckbox binding="#{SearchFieldsPage.replicationCheckbox}" id="replicationcheckbox"  value="#{SearchFieldsPage.replicationForResults}"/> 
                            <h:outputLabel for="replicationcheckbox" value="Replication For"/>
                        </ui:panelGroup>
                        
                        <ui:panelGroup  block="true" style="padding-top: 10px;">
                            <h:selectBooleanCheckbox binding="#{SearchFieldsPage.relatedpubCheckbox}" id="relatedpubcheckbox" value="#{SearchFieldsPage.relatedPublicationsResults}"/> 
                            <h:outputLabel for="relatedpubcheckbox" value="Related Publications"/>
                        </ui:panelGroup>
                        
                        <ui:panelGroup  block="true" style="padding-top: 10px;">
                            <h:selectBooleanCheckbox binding="#{SearchFieldsPage.relatedmatCheckbox}" id="relatedmatcheckbox"  value="#{SearchFieldsPage.relatedMaterialResults}"/> 
                            <h:outputLabel for="relatedmatcheckbox" value="Related Material"/>
                        </ui:panelGroup>
                        
                        <ui:panelGroup  block="true" style="padding-top: 10px;">
                            <h:selectBooleanCheckbox binding="#{SearchFieldsPage.relatedstudiesCheckbox}" id="relatedstudiescheckbox" value="#{SearchFieldsPage.relatedStudiesResults}"/> 
                            <h:outputLabel for="relatedstudiescheckbox" value="Related Studies"/>
                        </ui:panelGroup>
                        
                        <ui:panelGroup  block="true"  style="padding-top: 20px" >
                            <h:commandButton  id="saveButton" value="Save"  action="#{SearchFieldsPage.save}"/>
                            <h:commandButton  id="cancelButton" value="Cancel" style="margin-left: 30px" action="#{SearchFieldsPage.cancel}"/>
                        </ui:panelGroup>                                
                    </div>
                </div>
            </div>
    </ui:form>
</gui:define>
  </gui:composition>
 </body>
</html>
