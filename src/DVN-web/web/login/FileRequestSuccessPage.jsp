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

            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    
                        <h:outputText value="Request Access to Restricted File"/>
                   
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">
                        
                        <h:outputText value="Your request has been received.  Please wait for approval from the administrator."/>
                        <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/HomePage.jsp" title="Go to Dataverse Home Page">
                            <h:outputText  value="Go to Dataverse Home Page"/>
                        </h:outputLink>    
                        
                    </div>
                </div>
            </div>     
        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
