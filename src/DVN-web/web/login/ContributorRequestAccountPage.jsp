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

<gui:param name="pageTitle" value="DVN - Request to Contribute" />

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>
                            
     

        <ui:form  id="contributorRequestForm">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>

                       
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <a name="request" title="">
                        <h:outputText value="Request to Become a Dataverse Contributor"/>
                    </a>
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12"> 
                        
                        <h:outputText value="In order to request to become a Contributor to this dataverse, please create an account. If you already have an account, please log in."/>
                        
                        <ui:panelGroup  block="true"  style="padding-top: 10px; text-align: center;" >
                            <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/login/LoginPage.jsp?workflow=contributor">
                                <h:outputText value="Log in"/>
                            </h:outputLink>
                            <h:outputText value=" | " />
                            <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/login/AddAccountPage.jsp?workflow=contributor">
                                <h:outputText value="Create Account"/>
                            </h:outputLink>
                        </ui:panelGroup>
                        
                    </div>
                </div>
            </div>
        </ui:form>
</body>
</html>
