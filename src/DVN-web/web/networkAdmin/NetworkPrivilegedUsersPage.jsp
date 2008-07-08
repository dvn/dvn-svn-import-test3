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


This text will also not be displayed.

  <gui:define name="body">
      <f:loadBundle basename="Bundle" var="bundle"/>
     <f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>

        <ui:form id="NetworkPrivilegedUsersPageView">
               <input type="hidden" name="pageName" value="NetworkPrivilegedUsersPage"/>
               
               <div class="dvn_section">
                   <div class="dvn_sectionTitle">
                       
                           <h:outputText value="Dataverse Creation and Network Users' Privileges"/>
                       
                   </div>            
                   <div class="dvn_sectionBox"> 
                       <div class="dvn_margin12">
                           
                           <ui:panelLayout  styleClass="successMessage" rendered="#{NetworkPrivilegedUsersPage.success}" >
                               <h:outputText value="Update Successful!" />
                           </ui:panelLayout>
                           
                           <h:outputText id="outputText4"
                                         styleClass="vdcSubHeaderColor" value="- Creating a New Dataverse:"/>
                           <ui:panelGroup block="true" id="groupPanel2" style="padding-top: 10px; padding-bottom: 10px">
                               <h:outputText id="outputText5" value="Allow users to request to create a new Dataverse when they create an account: "/>
                               <h:selectBooleanCheckbox id="checkbox1" value="#{NetworkPrivilegedUsersPage.privileges.network.allowCreateRequest}"/>
                               <ui:panelGroup block="true" >
                                   <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                                   <h:outputText styleClass="vdcHelpText" value="By checking this option, your Dataverse Network homepage will display an invitation to create a dataverse (only users without Dataverse creator privileges will see the invitation)."/>
                               </ui:panelGroup>  

                           </ui:panelGroup>
                           
                           <ui:panelGroup block="true" style="padding-top: 10px; padding-bottom: 10px">
                               <h:outputText id="outputText2" styleClass="vdcSubHeaderColor" value="- Network Privileged Users:"/>
                           </ui:panelGroup>
                           <ui:panelGroup block="true"  style="padding-bottom: 5px">
                               <h:outputText id="outputText7" value="Enter the Username of the user you want to add: "/>
                               <h:inputText  onkeypress="if (window.event) return processEvent('', 'content:NetworkPrivilegedUsersPage:NetworkPrivilegedUsersPageView:addUserbutton'); else return processEvent(event, 'content:NetworkPrivilegedUsersPage:NetworkPrivilegedUsersPageView:addUserbutton');" id="textField1" value="#{NetworkPrivilegedUsersPage.userName}"/>
                               <h:commandLink  id="addUserbutton" value="Add" actionListener="#{NetworkPrivilegedUsersPage.addUser}"/>
                               <h:outputText styleClass="errorMessage" value="User Not Found." rendered="#{NetworkPrivilegedUsersPage.userNotFound}"/>
                           </ui:panelGroup>
                           <ui:panelGroup block="true" style="padding-bottom: 10px;">
                               <h:graphicImage alt="Information" title="Information" styleClass="vdcNoBorders" style="vertical-align: bottom" value="/resources/icon_info.gif" />
                               <h:outputText styleClass="vdcHelpText" value="You can add a network privileged user to be another network admin or a Dataverse creator. After privileged users are added to this list, if you want to remove a user, click 'Clear Role' for that user."/>
                           </ui:panelGroup>
                           <h:dataTable binding="#{NetworkPrivilegedUsersPage.userTable}" cellpadding="0" cellspacing="0"
                                        columnClasses="vdcColPadded, vdcColPadded, vdcColPadded, vdcColPadded" headerClass="list-header-left" 
                                        rowClasses="list-row-even,list-row-odd" value="#{NetworkPrivilegedUsersPage.privileges.privilegedUsers}" var="currentRow" width="98%">
                               <h:column id="column1" rendered="#{currentRow.user.id!=NetworkPrivilegedUsersPage.privileges.network.defaultNetworkAdmin.id}">
                                   <f:facet name="header">
                                       <h:outputText id="outputText3" value="Username"/>
                                   </f:facet>
                                   <h:outputLink id="hyperlink2" value="../login/AccountPage.jsp?userId=#{currentRow.user.id}">
                                       <h:outputText id="hyperlink2Text" value="#{currentRow.user.userName}"/>
                                   </h:outputLink>
                               </h:column>
                               <h:column id="column9" rendered="#{currentRow.user.id!=NetworkPrivilegedUsersPage.privileges.network.defaultNetworkAdmin.id}">
                                   <f:facet name="header">
                                       <h:outputText id="outputText16" value="Privileged Role"/>
                                   </f:facet>
                                   <h:selectOneRadio  id="roleSelectMenu" value="#{currentRow.networkRoleId}"> 
                                       <f:selectItems id="roleSelectItems" value="#{NetworkPrivilegedUsersPage.roleSelectItems}"/>
                                       <h:message for="roleSelectMenu"  styleClass="errorMessage"/>
                                   </h:selectOneRadio>
                                   
                               </h:column>
                               <h:column rendered="#{currentRow.user.id!=NetworkPrivilegedUsersPage.privileges.network.defaultNetworkAdmin.id}">
                                   <h:commandButton  value="Clear Role" actionListener="#{NetworkPrivilegedUsersPage.clearRole}" />                                            
                               </h:column>
                           </h:dataTable>
                           
                           <ui:panelGroup block="true" id="groupPanel6"
                                          style="padding-bottom: 10px; padding-top: 10px; padding-right: 5px;" styleClass="vdcTextRight">
                               <h:outputText  styleClass="vdcHelpText" value="(Nothing in this page will be saved until you click  Save Changes)"/>  
                               <h:commandButton id="button5" value="Save Changes" action="#{NetworkPrivilegedUsersPage.save}"/>
                               <h:commandButton immediate="true" id = "cancelButton" value="Cancel" action="#{NetworkPrivilegedUsersPage.cancel}"/>   
                           </ui:panelGroup>
                           
                       </div>
                   </div>
               </div>
                          
        </ui:form>
            </gui:define>
        </gui:composition>
    </body>
</html>
