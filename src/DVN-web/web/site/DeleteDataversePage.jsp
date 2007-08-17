<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <f:subview id="deleteStudyPageView">
      
        <h:form  id="form1">

            <input type="hidden" name="pageName" value="DeleteStudyPage"/>
            <h:inputHidden id="deleteId" value="#{DeleteDataversePage.deleteId}"  binding="#{DeleteDataversePage.hiddenVdcId}" />                                    
            <h:inputHidden binding="#{DeleteDataversePage.hiddenVdcName}" id="vdcName" value="#{DeleteDataversePage.vdcName}"/>                                    

                    <div class="dvn_section">
                        <div class="dvn_sectionTitle">
                            <h3>
                                <h:outputText  value="Deleting a Study"/>
                            </h3>
                        </div>            
                        <div class="dvn_sectionBox"> 
                            <div class="dvn_margin12"> 
                                
                                <ui:panelGroup  block="true" id="groupPanel1">
                                    <h:outputText  styleClass="warnMessage" value="By deleting a dataverse, you are removing it permanently from the Archive. All studies and collections owned by this dataverse will also be deleted."/>
                                    <verbatim> <br /></verbatim>
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
               
    </f:subview>
</jsp:root>
