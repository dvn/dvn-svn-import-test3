<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">                            
       <f:subview id="contactConfirmPageView">
                    <ui:form  id="contactConfirmForm">  
                      <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
                            <ui:panelLayout  id="layoutPanel1" panelLayout="flow" styleClass="vdcSectionMiddleFixed" style="width: 750px">
                                <ui:panelLayout  id="layoutPanel2" panelLayout="flow" styleClass="vdcSectionHeader">
                                    <h:outputText value="#{bundle.contactUsHeading}"/>
                                </ui:panelLayout>
                                
                                <ui:panelLayout  id="layoutPanel3" panelLayout="flow" style="padding: 40px 30px 30px 30px; ">
                                    <ui:panelLayout panelLayout="flow" styleClass="vdcSectionMiddleMessage" style="width: 300px;" rendered="#{ContactUsPage.success}">
                                 <ui:panelGroup block="true"  styleClass="successMessage" >
                                     <h:messages layout="table" globalOnly="true" />
                                 </ui:panelGroup>
                                </ui:panelLayout>
                                    
                                    <ui:panelGroup  block="true" id="groupPanel2" style="padding-bottom: 20px; padding-top: 20px">
                                        <h:outputText  id="outputText2" value="#{bundle.contactUsMessageSummary}"/>
                                    </ui:panelGroup>
                                    <h:panelGrid  cellpadding="0" cellspacing="0"
                                        columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                                        <h:outputText  id="outputText7" style="font-weight: bold" value="#{bundle.contactUsToLabel}"/>
                                        <h:outputFormat value="#{bundle.contactUsTo}">
                                            <f:param value="#{(VDCRequest.currentVDCId == null) ? VDCRequest.vdcNetwork.name : VDCRequest.currentVDC.name }" />
                                            <f:param value="#{ (VDCRequest.currentVDCId == null) ? ('Network') : ('')}"/>
                                        </h:outputFormat>
                                        
                                        <ui:panelGroup  id="groupPanel3">
                                            <h:outputText id="outputText4" style="font-weight: bold" value="#{bundle.contactUsEmailLabel}"/>
                                        </ui:panelGroup>
                                        <ui:panelGroup id="groupPanel13a" block="true" separator="&lt;br /&gt;">
                                            <h:outputText id="fullName" escape="true" 
                                                         value = "#{ContactUsPage.fullName} (#{ContactUsPage.emailAddress})"
                                                         />
                                        </ui:panelGroup>
                                        <ui:panelGroup  id="groupPanel4">
                                            <h:outputText  id="outputText5" style="font-weight: bold" value="#{bundle.contactUsSubjectLabel}"/>
                                        </ui:panelGroup>
                                        <ui:panelGroup block="true" separator="&lt;br /&gt;"> 
                                         <h:outputText id="listSubjects"
                                                        value="#{ContactUsPage.selectedSubject}"
                                                        />
                                        </ui:panelGroup>
                                        <ui:panelGroup  id="groupPanel5" styleClass="vdcTextNoWrap">
                                            <h:outputText  id="outputText6" style="font-weight: bold" value="#{bundle.contactUsBodyLabel}"/>
                                        </ui:panelGroup>
                                        <ui:panelGroup block="true" separator="&lt;br /&gt;"> 
                                            <h:outputText id="emailBody" 
                                                        value="#{ContactUsPage.emailBody}" 
                                                        />
                                        </ui:panelGroup>
                                    </h:panelGrid>
                                </ui:panelLayout>
                            </ui:panelLayout>                       
                    </ui:form>
          </f:subview>
</jsp:root>
