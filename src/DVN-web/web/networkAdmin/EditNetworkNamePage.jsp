<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
   <f:subview id="EditNetworkNamePageView">

        <ui:form binding="#{EditNetworkNamePage.form1}" id="form1">
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                     
                        <h:outputText binding="#{EditNetworkNamePage.outputText1}" value="Edit Network Name"/>
                     
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">
                        
                        <ui:panelLayout styleClass="#{EditNetworkNamePage.msg.styleClass}"  rendered="#{!empty EditNetworkNamePage.msg.messageText}">
                            <h:outputText id="statusMessage"  value="#{EditNetworkNamePage.msg.messageText}" /> 
                        </ui:panelLayout>
                        
                        <ui:panelGroup block="true" style="padding-bottom: 20px">
                            <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                            <h:outputText styleClass="vdcHelpText" value="Your Dataverse Network name is used in the &quot;Connected to&quot; banner displayed in each dataverse added to this Network. Please use a short name."/>
                        </ui:panelGroup>
                        
                        <h:panelGrid binding="#{EditNetworkNamePage.gridPanel2}" cellpadding="0" cellspacing="0"
                                     columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                            <ui:panelGroup binding="#{EditNetworkNamePage.groupPanel1}" id="groupPanel1">
                                <h:outputText binding="#{EditNetworkNamePage.outputText2}" id="outputText2" value="Network Name"/>
                                <h:graphicImage binding="#{EditNetworkNamePage.image1}" id="image1" value="/resources/icon_required.gif"/>
                            </ui:panelGroup>
                            <h:inputText binding="#{EditNetworkNamePage.textField1}" id="textField1" size="20" value="#{EditNetworkNamePage.networkName}" onkeypress="if (window.event) return processEvent('', 'content:EditNetworkNamePageView:form1:button1'); else return processEvent(event, 'content:EditNetworkNamePageView:form1:button1');"/>
                        </h:panelGrid>
                        <ui:panelGroup binding="#{EditNetworkNamePage.groupPanel2}" block="true" id="groupPanel2" style="padding-left: 110px; padding-top: 20px">
                            <h:commandButton binding="#{EditNetworkNamePage.button1}" id="button1" value="Save" action="#{EditNetworkNamePage.saveNetworkName}"/>
                            <h:commandButton binding="#{EditNetworkNamePage.button2}" id="button2" style="margin-left: 30px" value="Cancel" action="#{EditNetworkNamePage.cancel}"/>
                        </ui:panelGroup>
                        
                    </div>
                </div>
            </div>
        </ui:form>            
    </f:subview>
</jsp:root>
