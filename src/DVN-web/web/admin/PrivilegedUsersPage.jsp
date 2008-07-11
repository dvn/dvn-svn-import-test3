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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

</head>

<body>
<gui:composition template="/template.xhtml">


<gui:param name="pageTitle" value="DVN - Dataverse Privileged Users" />

  <gui:define name="body">


<ui:form id="privilegedUsersForm">
       <input type="hidden" name="pageName" value="PrivilegedUsersPage"/>
       <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/> 
           
        <div class="dvn_section">
            <div class="dvn_sectionTitle">
                
                    <h:outputText value="Site Restrictions and Users' Privileges"/>
                
            </div>            
            <div class="dvn_sectionBox"> 
                <div class="dvn_margin12">
                    <ui:panelLayout styleClass="successMessage" rendered="#{PrivilegedUsersPage.success}">
                        <h:outputText value="Update Successful!" />
                    </ui:panelLayout>
                    
                    <h:outputText id="outputText13" styleClass="vdcSubHeaderColor" value="- Dataverse Release Settings:"/>
                    <ui:panelGroup block="true" id="groupPanel6" style="padding-top: 10px; padding-bottom: 10px">
                        <h:outputText id="outputText12" value="This dataverse is set as: "/>
                        <h:selectOneMenu value="#{PrivilegedUsersPage.siteRestriction}">
                            <f:selectItem itemLabel="Not Released" itemValue="Restricted"/>
                            <f:selectItem itemLabel="Released" itemValue="Public"/>
                        </h:selectOneMenu>
                        <ui:panelGroup block="true" >
                            <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                            <h:outputText styleClass="vdcHelpText" value="By default, a new dataverse is set to 'Not Released' and only appropriate privileges can access it. Change it to 'Released' once is ready for others to view, and click Save."/>
                        </ui:panelGroup>  
                    </ui:panelGroup>
                    
                    <h:outputText id="outputText14" styleClass="vdcSubHeaderColor" value="- Contributions Settings:"/>
                    <ui:panelGroup block="true" id="groupPanel7" style="padding-top: 10px; padding-bottom: 10px">
                        <h:outputText id="outputText15" value="Allow users to request to be contributors when they create an account: "/>
                        <h:selectBooleanCheckbox id="checkbox5" value="#{PrivilegedUsersPage.vdc.allowContributorRequests}"/>
                        <ui:panelGroup block="true" >
                            <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                            <h:outputText styleClass="vdcHelpText" value="By checking this option, your dataverse homepage will display an invitation to become a contributor  (only end-users without the permission to contribute will see that invitation.)"/>
                        </ui:panelGroup>
                    </ui:panelGroup>
                    
                    <ui:panelGroup block="true" style="padding-top: 10px; padding-bottom: 10px">
                        <h:outputText styleClass="vdcSubHeaderColor" value="- Edit Privileged Users:"/>  
                    </ui:panelGroup>
                    <ui:panelGroup block="true" id="groupPanel3" style="padding-bottom: 10px">
                        <h:outputText id="outputText2" value="Enter the Username of the user you want to add: "/>
                        <h:inputText  binding="#{PrivilegedUsersPage.userInputText}" id="userName" value="#{PrivilegedUsersPage.userName}" onkeypress="if (window.event) return processEvent('', 'privilegedUsersForm:addUserButton'); else return processEvent(event, 'privilegedUsersForm:addUserButton');"/>
                        <h:commandButton  id="addUserButton" value="Add" actionListener="#{PrivilegedUsersPage.addUser}" />
                        <h:message styleClass="errorMessage" for="userName"/> 
                    </ui:panelGroup>
                    <ui:panelGroup block="true" style="padding-bottom: 10px;">
                        <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                        <h:outputText styleClass="vdcHelpText" value="You can add a privileged user to be a contributor, curator or admin for this dataverse, or to gain access to this dataverse, if it's not released. "/>
                    </ui:panelGroup>
                    <h:dataTable binding="#{PrivilegedUsersPage.userTable}" cellpadding="0" cellspacing="0"
                                 columnClasses="vdcColPadded, vdcColPadded, vdcColPadded, vdcColPadded" headerClass="list-header-left" id="dataTable1"
                                 rowClasses="list-row-even,list-row-odd" value="#{PrivilegedUsersPage.privilegedUsers}" var="currentRow" width="98%">
                        <h:column id="column1">
                            <f:facet name="header">
                                <h:outputText id="outputText3" value="Username"/>
                            </f:facet>
                            <h:outputLink id="hyperlink2" value="../login/AccountPage.jsp?userId=#{currentRow[0].vdcUser.id}">
                                <h:outputText id="hyperlink2Text" value="#{currentRow[0].vdcUser.userName}"/>
                            </h:outputLink>
                        </h:column>
                        <h:column id="column9" >
                            <f:facet name="header">
                                <ui:panelGroup  block="true"  >
                                    <h:outputText id="outputText16" value="Privileged Role"/>
                                    <h:outputText styleClass="vdcHelpText" style="padding-left: 30px;" value="('Access To Site' is only applicable if dataverse is not released.)"/>
                                </ui:panelGroup>
                            </f:facet>
                            <h:selectOneRadio  disabled="#{currentRow[0].vdcUser.id==PrivilegedUsersPage.vdc.creator.id}" id="roleSelectMenu" value="#{currentRow[1]}">
                                <h:message for="roleSelectMenu"  styleClass="errorMessage"/>
                                <f:selectItems id="roleSelectItems" value="#{PrivilegedUsersPage.roleSelectItems}"/>
                                
                            </h:selectOneRadio>
                            
                        </h:column>
                        <h:column >
                            <h:commandButton  id="removeRoleButton" value="Remove Role" rendered="#{currentRow[0].vdcUser.id!=PrivilegedUsersPage.vdc.creator.id}" actionListener="#{PrivilegedUsersPage.removeRole}" />    
                            <h:outputText value="(Dataverse Creator - cannot modify Role)" rendered="#{currentRow[0].vdcUser.id==PrivilegedUsersPage.vdc.creator.id}"  />    
                            
                        </h:column>
                        
                    </h:dataTable>
                    
                    <ui:panelGroup block="true" style="padding-top: 20px; padding-bottom: 10px">
                        <h:outputText styleClass="vdcSubHeaderColor" value="- Edit Privileged Groups:"/>
                    </ui:panelGroup>
                    
                    <ui:panelGroup block="true"  style="padding-bottom: 10px">
                        <h:outputText  value="Enter the name of the Group you want to add: "/>
                        <h:inputText id="groupName" binding="#{PrivilegedUsersPage.groupInputText}" value="#{PrivilegedUsersPage.groupName}" onkeypress="if (window.event) return processEvent('', 'privilegedUsersForm:addGroupButton'); else return processEvent(event, 'privilegedUsersForm:addGroupButton');"/>
                        <h:commandButton id="addGroupButton" value="Add" actionListener="#{PrivilegedUsersPage.addGroup}" />
                        <h:message styleClass="errorMessage" for="groupName"/> 
                    </ui:panelGroup>
                    <ui:panelGroup block="true" style="padding-bottom: 10px;">
                        <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                        <h:outputText styleClass="vdcHelpText" value="You can add a privileged group to gain access to this dataverse, if it is not released. "/>
                    </ui:panelGroup>
                    <h:dataTable binding="#{PrivilegedUsersPage.groupTable}" cellpadding="0" cellspacing="0"
                                 columnClasses="vdcColPadded, vdcColPadded, vdcColPadded, vdcColPadded" headerClass="list-header-left" 
                                 rowClasses="list-row-even,list-row-odd" value="#{PrivilegedUsersPage.vdc.allowedGroups}" var="currentRow" width="98%">
                        <h:column >
                            <f:facet name="header">
                                <h:outputText id="groups_tcol1" value="Group Name"/>
                            </f:facet>
                            <h:outputLink  value="../networkAdmin/ViewUserGroupPage.jsp?userGroupId=#{currentRow.id}">
                                <h:outputText  value="#{currentRow.name}"/>
                            </h:outputLink>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText id="roleHeader" value="Privileged Role"/>
                            </f:facet>
                            
                            <h:outputText value="Access To Site"/>
                            
                        </h:column>                                  
                        <h:column>
                            <h:commandButton  id="removeGroupButton" value="Remove Group" actionListener="#{PrivilegedUsersPage.removeGroup}" />                                            
                        </h:column>
                    </h:dataTable>
                    <ui:panelGroup block="true"  style="padding-bottom: 10px; padding-top: 10px; padding-right: 5px;" styleClass="vdcTextRight">
                        <h:outputText  styleClass="vdcHelpText" value="(Nothing in this page will be saved until you click  Save Changes)"/>
                        <h:commandButton  id = "saveChangesButton" value="Save Changes" action="#{PrivilegedUsersPage.saveChanges}"/>   
                        <h:commandButton immediate="true" id = "cancelButton" value="Cancel" action="#{PrivilegedUsersPage.cancel}"/>   
                    </ui:panelGroup>
                </div> 
            </div>
        </div>
    </ui:form>
 </gui:define>
        </gui:composition>
    </body>
</html>

