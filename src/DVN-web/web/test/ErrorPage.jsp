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

<gui:param name="pageTitle" value="DVN - Error" />

  <gui:define name="body">

            <ui:form  id="errorForm">
                <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                
                <div class="dvn_section">
                    <div class="dvn_sectionTitle">
                        
                            Error
                         
                    </div>            
                    <div class="dvn_sectionBox"> 
                        <div class="dvn_margin12">
                            
                            <h:outputText styleClass="errorMessage" value="We are sorry. An application error has occurred. "/>
                            <br /> <br />
                            <h:outputText value="Error Message:" rendered="#{param.errorMsg!=null}"/>
                            <h:outputText escape="false" styleClass="errorMessage" value="#{param.errorMsg}" rendered="#{param.errorMsg!=null}"/>
                            
                            <h:dataTable value="#{ErrorPage.messages}" var="currentRow">
                                <h:column>
                                    <h:outputText escape="false" styleClass="errorMessage" value="#{currentRow}"/>
                                </h:column>
                            </h:dataTable>
                            
                            <f:verbatim><br /><br /></f:verbatim>
                            <h:outputText styleClass="errorMessage" value="Event occurred at " rendered="#{param.time!=null}" />
                            <h:outputText styleClass="errorMessage" value="#{param.time}" rendered="#{param.time!=null}">
                                <f:convertDateTime dateStyle="full"/>
                            </h:outputText>
                            
                        </div>
                    </div>
                </div>
                 
            </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
