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

<gui:param name="pageTitle" value="DVN - Request Access to File" />

  <gui:define name="body">


        <ui:form  id="fileRequestForm">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <h:inputHidden  value="test"/>
            <h:inputHidden  value="test"/>
         
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    
                        <h:outputText value="Request Access to Restricted File"/>
                   
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12">
                        
                        <ui:panelGroup rendered="#{ !FileRequestPage.alreadyRequested}">
                            <ui:panelGroup block="true"  style="padding-top: 10px" >
                                <h:outputText value="I would like to access restricted files in this study."/>
                            </ui:panelGroup>
                            <ui:panelGroup block="true"  style="padding-top: 10px; text-align: center;"  >                                      
                                <h:commandButton value="Submit Request" action="#{FileRequestPage.generateRequest}"/>
                                <h:commandButton value="Cancel" action="home"/>
                            </ui:panelGroup>
                        </ui:panelGroup>
                        <ui:panelGroup  block="true"  style="padding-top: 10px" rendered="#{FileRequestPage.alreadyRequested}">
                            <h:outputText value="You have already requested access to files in this study.  Please wait for approval from the administrator."/>
                            <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/HomePage.jsp" title="Go to Dataverse Home Page"/>
                        </ui:panelGroup>
                        
                    </div>
                </div>
            </div>
        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
