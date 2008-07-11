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

<gui:param name="pageTitle" value="DVN - Study Locked" />

  <gui:define name="body">             
     

        <ui:form  id="studyLockedForm">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    
                        <h:outputText value="File Upload In Progress"/>   
                    
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">
                        
                        <h:outputText  style="warnMessage" value="We are sorry; this study is currently unavailable for editing because files are being uploaded.   When the file upload is completed the study will be editable again. "/>
                        <p>
                            <h:outputText   style="warnMessage" value="#{param.message}"/>   
                        </p>
                        
                    </div>
                </div>
            </div>

        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
