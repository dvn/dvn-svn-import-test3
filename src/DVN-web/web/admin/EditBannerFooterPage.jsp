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
<title>Facelets: DVN Style</title>
</head>

<body>
<gui:composition template="/template.xhtml">

<gui:param name="pageTitle" value="DVN - Layout Branding" />

  <gui:define name="body">


    <ui:form id="editBannerFooterForm">
        <h:inputHidden rendered="#{VDCRequest.currentVDC != null}" id="vdcId" value="#{VDCRequest.currentVDCId}"/>
        
        <div class="dvn_section">
                <div class="dvn_sectionTitle">    
                         <h:outputText  value="#{bundle.editBannerFooterHeading}"/>              
                </div>            
                <div class="dvn_sectionBox">
                    <div class="dvn_margin12">

                        <h:messages layout="list" globalOnly="true" styleClass="successMessage" />
                        
                        <ui:panelGroup block="true" style="padding-bottom: 10px">
                            <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                            <h:outputText id="htmlHelp" styleClass="vdcHelpText" value="#{bundle.htmlHelpText}"/>
                        </ui:panelGroup>
                        <h:panelGrid cellpadding="0" cellspacing="0"
                                     columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2" style="margin-top: 10px">
                            <h:outputText  id="outputText2" styleClass="vdcFieldTitle" value="#{bundle.editCustomBannerLabel}"/>
                            <h:panelGroup>
                                <h:message for="combined" styleClass="errorMessage"/>   
                                <h:message  id="bannerMsg" 
                                            for="banner"
                                            styleClass="errorMessage"/>
                                <f:verbatim><br /><br /></f:verbatim>
                                <h:inputTextarea    id="banner" 
                                                    rows="15" 
                                                    value="#{EditBannerFooterPage.banner}"
                                                    styleClass="formHtmlEnabled">
                                    <f:validator validatorId="XhtmlValidator"/>
                                </h:inputTextarea>
                            </h:panelGroup>
                            <h:outputText  id="footerLabel" styleClass="vdcFieldTitle" value="#{bundle.editCustomFooterLabel}"/>
                            <h:panelGroup>
                                <h:message  id="footerMsg" 
                                            for="footer"
                                            styleClass="errorMessage"/>
                                
                                <f:verbatim><br /></f:verbatim>
                                <h:inputTextarea    id="footer" 
                                                    rows="7" 
                                                    value="#{EditBannerFooterPage.footer}"
                                                    styleClass="formHtmlEnabled">
                                    <f:validator validatorId="XhtmlValidator"/>
                                </h:inputTextarea> 
                                <h:inputHidden id="combined" value="combined"/>
                                
                            </h:panelGroup>
                            
                        </h:panelGrid>
                        <ui:panelGroup  block="true" id="groupPanel1" style="padding-left: 200px; padding-top: 20px">
                            <h:commandButton id="btnSave" value="#{bundle.saveButtonLabel}" action="#{EditBannerFooterPage.save_action}"/>
                            <h:commandButton id="btnCancel" immediate="true" style="margin-left: 30px" value="#{bundle.cancelButtonLabel}" action="#{EditBannerFooterPage.cancel_action}"/>
                        </ui:panelGroup>
                    </div>
               </div>
            </div>
        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
