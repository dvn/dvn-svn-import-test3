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

<gui:param name="pageTitle" value="DVN - User Password" />

  <gui:define name="body">             
     

        <ui:form  id="form1">
           <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>
           <h:inputHidden id="hiddenUserId" binding="#{EditPasswordPage.hiddenUserId}" value="#{EditPasswordPage.userId}"/>

           <input type="hidden" name="pageName" value="EditPasswordPage"/>
           <div class="dvn_section">
               <div class="dvn_sectionTitle">   
                       <h:outputText value="Edit Password for "/> 
                       <h:outputText value="#{EditPasswordPage.user.userName}"/>
               </div>            
               <div class="dvn_sectionBox"> 
                   <div class="dvn_margin12"> 
                       <ui:panelGroup block="true" style="padding-bottom: 15px" rendered="#{VDCSession.loginBean.networkAdmin}">
                            <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                            <h:outputText styleClass="vdcHelpText" value="If you are a Network Administrator updating another user's password, enter your administrator password below."/>
                        </ui:panelGroup>   
                                  
                       <h:panelGrid  cellpadding="0" cellspacing="0"
                                     columnClasses="vdcColPadded, vdcColPadded" columns="2" id="gridPanel2">
                      
                           <ui:panelGroup >
                               <h:outputText   value="Administrator Password" rendered="#{VDCSession.loginBean.networkAdmin}"/>
                               <h:outputText   value="Current Password" rendered="#{!VDCSession.loginBean.networkAdmin}"/>
                               <h:graphicImage  id="image1" value="/resources/icon_required.gif"/>
                           </ui:panelGroup>
                           <ui:panelGroup>
                               <h:inputSecret id="inputCurrentPassword"  validator="#{EditPasswordPage.validateOldPassword}" value="#{EditPasswordPage.editUserService.currentPassword}" required="true" requiredMessage="This field is required.">
                               </h:inputSecret>
                               <h:message styleClass="errorMessage" for="inputCurrentPassword"/>
                           </ui:panelGroup>
                           <ui:panelGroup >
                               <h:outputText  value="New Password"/>
                               <h:graphicImage  value="/resources/icon_required.gif"/>
                           </ui:panelGroup>
                           <ui:panelGroup>
                               <h:inputSecret id="inputNewPassword1" binding="#{EditPasswordPage.inputNewPassword}"  value="#{EditPasswordPage.editUserService.newPassword1}" required="true" requiredMessage="This field is required."/> 
                               <h:message styleClass="errorMessage" for="inputNewPassword1"/>
                           </ui:panelGroup>
                           
                           
                           <ui:panelGroup >
                               <h:outputText  value="Retype New Password"/>
                               <h:graphicImage  value="/resources/icon_required.gif"/>
                           </ui:panelGroup>
                           <ui:panelGroup>
                               <h:inputSecret id="inputNewPassword2" validator="#{EditPasswordPage.validateConfirmPassword}" value="#{EditPasswordPage.editUserService.newPassword2}" required="true" requiredMessage="This field is required."/> 
                               <h:message styleClass="errorMessage" for="inputNewPassword2"/>
                           </ui:panelGroup>
                           
                             </h:panelGrid>
                      
                        
                       <ui:panelGroup block="true"  style="padding-left: 100px; padding-top: 20px">
                           <h:commandButton  value="Save" action="#{EditPasswordPage.save}"/>
                            
                       </ui:panelGroup>
                     
                   </div>
               </div>
           </div>
                        
        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
