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

<gui:param name="pageTitle" value="DVN - Delete Dataverse" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>
    

      
        <h:form  id="form1">

            <input type="hidden" name="pageName" value="DeleteDataversePage"/>
            <h:inputHidden id="deleteId" value="#{DeleteDataversePage.deleteId}"  binding="#{DeleteDataversePage.hiddenVdcId}" />                                    
            <h:inputHidden binding="#{DeleteDataversePage.hiddenVdcName}" id="vdcName" value="#{DeleteDataversePage.vdcName}"/>                                    

                    <div class="dvn_section">
                        <div class="dvn_sectionTitle">
                             
                                <h:outputText  value="Deleting a Dataverse"/>
                             
                        </div>            
                        <div class="dvn_sectionBox"> 
                            <div class="dvn_margin12"> 
                                
                                <ui:panelGroup  block="true" id="groupPanel1">
                                    <h:outputText  styleClass="warnMessage" value="By deleting a dataverse, you are removing it permanently from the Archive. All studies and collections owned by this dataverse will also be deleted."/>
                                     <br />
                                    <h:outputText  styleClass="warnMessage" value="Are you sure you want to delete "/>
                                    <h:outputFormat  id="outputText3" styleClass="vdcTextStandOut" value="{0}">
                                        <f:param value="#{DeleteDataversePage.vdcName}"/>
                                        
                                    </h:outputFormat>
                                    <h:outputText  id="outputText4" value="?"/>
                                </ui:panelGroup>
                                <ui:panelGroup  block="true" id="groupPanel2" style="padding-top: 30px; text-align: center">
                                    <h:commandButton  id="button1" value="Delete" action="#{DeleteDataversePage.delete}"/>
                                    <h:commandButton  immediate="true" id="button2" style="margin-left: 30px" value="Cancel" action="#{DeleteDataversePage.cancel}"/>
                                </ui:panelGroup>
                                
                            </div>
                        </div>
                    </div>
                                
                    </h:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
