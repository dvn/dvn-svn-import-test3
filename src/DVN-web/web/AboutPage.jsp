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

<gui:param name="pageTitle" value="DVN - About" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>

        <ui:form  id="aboutUsForm">
            <h:inputHidden rendered="#{VDCRequest.currentVDCId != null}" id="vdcId" value="#{VDCRequest.currentVDCId}"/>

            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <a name="about" title="">
                        <h:outputText  value="About #{VDCRequest.currentVDC.name} Dataverse" rendered="#{VDCRequest.currentVDCId != null}"/>
                        <h:outputText  value="About #{VDCRequest.vdcNetwork.name} Dataverse Network" rendered="#{VDCRequest.currentVDCId == null}"/>
                    </a>
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">
                        
                        <h:outputText  escape="false"  value="#{ (VDCRequest.currentVDCId == null) ? VDCRequest.vdcNetwork.aboutThisDataverseNetwork : VDCRequest.currentVDC.aboutThisDataverse }"/>
                        
                    </div>
                </div>
            </div>
            
        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
