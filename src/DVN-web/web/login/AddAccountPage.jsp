<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="AddAccountPageView">
        <ui:form  id="form1">
           <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            <h:inputHidden id="studyId" value="#{AddAccountPage.studyId}"/>
            <h:inputHidden binding="#{AddAccountPage.hiddenWorkflow}" value="#{AddAccountPage.workflow}"/>

          <input type="hidden" name="pageName" value="AddAccountPage"/>
                                           
          <div class="dvn_section">
              <div class="dvn_sectionTitle">
                 
                      <h:outputText value="Create a New Account"/>
                 
              </div>            
              <div class="dvn_sectionBox">
                  <div class="dvn_margin12"> 
                      
                      <ui:panelGroup block="true" style="padding-bottom: 15px;">
                          <h:graphicImage value="/resources/icon_required.gif"/> <h:outputText style="vdcHelpText" value="Indicates a required field."/>
                      </ui:panelGroup>
                      <h:panelGrid  cellpadding="0" cellspacing="0"
                                    columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                          <ui:panelGroup >
                              <h:outputText   value="Username"/>
                              <h:graphicImage  id="image1" value="/resources/icon_required.gif"/>
                          </ui:panelGroup>
                          <ui:panelGroup>
                              <h:inputText id="inputUserName" validator="#{AddAccountPage.validateUserName}" value="#{AddAccountPage.user.userName}" required="true">
                              </h:inputText>
                              <h:message for="inputUserName"  styleClass="errorMessage"/>
                          </ui:panelGroup>
                          <ui:panelGroup >
                              <h:outputText  value="Password"/>
                              <h:graphicImage  value="/resources/icon_required.gif"/>
                          </ui:panelGroup>
                          <ui:panelGroup> 
                              <h:inputSecret  id="inputPassword" value="#{AddAccountPage.user.password}" required="true"/>
                              <h:message for="inputPassword"  styleClass="errorMessage"/>
                          </ui:panelGroup>
                          <ui:panelGroup >
                              <h:outputText  value="First Name"/>
                              <h:graphicImage  value="/resources/icon_required.gif"/>
                          </ui:panelGroup>
                          <ui:panelGroup>
                              <h:inputText id="inputFirstName" value="#{AddAccountPage.user.firstName}" required="true"/> 
                              <h:message for="inputFirstName"  styleClass="errorMessage"/>
                          </ui:panelGroup>
                          
                          
                          <ui:panelGroup >
                              <h:outputText  value="Last Name"/>
                              <h:graphicImage  value="/resources/icon_required.gif"/>
                          </ui:panelGroup>
                          <ui:panelGroup>
                              <h:inputText id="inputLastName" value="#{AddAccountPage.user.lastName}" required="true"/> 
                              <h:message for="inputLastName"  styleClass="errorMessage"/>
                          </ui:panelGroup>
                          
                          <ui:panelGroup >
                              <h:outputText  value="E-Mail"/>
                              <h:graphicImage  value="/resources/icon_required.gif"/>
                          </ui:panelGroup>
                          <ui:panelGroup>
                              <h:inputText  id="inputEmail" value="#{AddAccountPage.user.email}" size="40" required="true">
                                  <f:validator validatorId="EmailValidator"/>
                              </h:inputText>
                              <h:message for="inputEmail"  styleClass="errorMessage"/>
                          </ui:panelGroup>
                          
                          <ui:panelGroup>    
                              <h:outputText value="Institution"/>
                          </ui:panelGroup>
                          <h:inputText id="inputInstitution" value="#{AddAccountPage.user.institution}"  size="40"/>
                          
                          <ui:panelGroup >
                              <h:outputText  value="Position"/>
                          </ui:panelGroup>
                          
                          <h:selectOneMenu value="#{AddAccountPage.user.position}" >
                              <f:selectItem itemLabel="Student" itemValue="Student" />
                              <f:selectItem itemLabel="Faculty" itemValue="Faculty" />
                              <f:selectItem itemLabel="Staff" itemValue="Staff" />
                              <f:selectItem itemLabel="Other" itemValue="Other" />
                          </h:selectOneMenu>
                          
                          <ui:panelGroup >
                              <h:outputText  value="Phone Number"/>
                          </ui:panelGroup>
                          <h:inputText  id="inputPhoneNumber" value="#{AddAccountPage.user.phoneNumber}"  />
                          
                      </h:panelGrid>
                      <!--
                    <ui:panelGroup  block="true"  style="padding-left: 100px; padding-top: 20px" rendered="#{VDCRequest.currentVDCId!=null and VDCRequest.currentVDC.allowContributorRequests and AddAccountPage.studyId==null and VDCSession.loginBean.user==null}"> 
                        <h:selectBooleanCheckbox id="contributorCheckbox" value="#{AddAccountPage.contributorRequest}"/>
                        <h:outputLabel for="contributorCheckbox" value="I want to become a contributor to #{VDCRequest.currentVDC.name} dataverse. This will allow me to upload my own data in this archive."/>
                    </ui:panelGroup>
                    <ui:panelGroup  block="true" style="padding-left: 100px; padding-top: 20px" rendered="#{VDCRequest.vdcNetwork.allowCreateRequest and VDCRequest.currentVDCId==null and VDCSession.loginBean.user==null}">
                        <h:selectBooleanCheckbox id="creatorCheckbox" value="#{AddAccountPage.creatorRequest}" />
                        <h:outputLabel for="creatorCheckbox" value="I want to become a dataverse creator. This will allow me to create my virtual archive in the #{VDCRequest.vdcNetwork.name} Dataverse Network."/>
                    </ui:panelGroup>
                    <ui:panelGroup  block="true"  style="padding-left: 100px; padding-top: 20px" rendered="#{AddAccountPage.studyId!=null and VDCSession.loginBean.user==null}">
                        <h:selectBooleanCheckbox id="studyCheckbox" value="#{AddAccountPage.accessStudy}"  />
                        <h:outputLabel for="studyCheckbox" value="I want to access the restricted files of Study: #{AddAccountPage.study.globalId}"/>
                    </ui:panelGroup>
                    -->
                      <ui:panelGroup block="true"  style="padding-left: 100px; padding-top: 20px">
                          <h:commandButton  value="Create Account" action="#{AddAccountPage.createAccount}"/>
                          <h:commandButton  immediate="true" value="Cancel" action="#{AddAccountPage.cancel}"/>
                      </ui:panelGroup>
                      
                  </div>
              </div>
          </div>  
                                
       </ui:form>
   </f:subview>
</jsp:root>
