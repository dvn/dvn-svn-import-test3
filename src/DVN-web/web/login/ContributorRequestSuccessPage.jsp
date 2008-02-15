<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="ContributorRequestSuccessPageView">
        <ui:form  id="contributorRequestForm">
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>

            <ui:panelLayout styleClass="dvn_createDvRequest dvn_section dvn_overflow">
              <ui:panelLayout styleClass="requestHeader dvn_overflow">
                      <h:outputText value="Success! &lt;span&gt;&gt; Become a Contributor&lt;/span&gt;" escape="false"/>
              </ui:panelLayout>
              <ui:panelLayout rendered="#{LoginWorkflowBean.contributorWorkflow}" styleClass="requestTimeline" style="background-position: 0 -12px;">
                        <div class="requestTimelinePoint" style="left: 53px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899;">Create Account</strong></div>
                        <div class="requestTimelinePoint" style="left: 372px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899;">Terms of Use</strong></div>
                        <div class="requestTimelinePoint" style="left: 709px;"><img src="/dvn/resources/lrg-blue-bullet.gif" class="vdcNoBorder" alt=""/><br/><strong style="color:#035899; font-weight:bold;">Success!</strong></div>
              </ui:panelLayout>
              <ui:panelLayout styleClass="requestContent">
                  
                  <ui:panelLayout styleClass="requestContentDescLeft requestContentSucessH4">
                      <h4>Your are now a contributor to the <h:outputText value="#{VDCRequest.currentVDC.name}"/> dataverse!</h4>
                  </ui:panelLayout>
                  
                  <ui:panelLayout styleClass="requestContentDescRight requestContentSucess">
                  <ui:panelLayout styleClass="dvn_margin12"> 
                      
                        <p>As a contributor, you can: </p>
                            <ul>
                            <li>
                            <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/study/EditStudyPage.jsp">
                                <h:outputText value="Add new studies and upload files"/>
                            </h:outputLink>
                            </li>
                            <li> 
                            <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/study/MyStudiesPage.jsp">
                                <h:outputText value="View studies you have uploaded"/>
                            </h:outputLink>
                            </li>
                            </ul>
                        <p>You can get to these links from <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/admin/OptionsPage.jsp">
                                <h:outputText value="My Options"/>
                            </h:outputLink> (displayed in menubar when you are logged in to this dataverse). </p>
                            <p>Be sure to click <em>Ready for Review</em> after you have completed adding a study, to notify the Dataverse Administrator that your study is ready to be released. 
                        After the study is sent for Review, you cannot edit it or add files to it anymore (use 
                        <h:outputLink value="/dvn#{VDCRequest.currentVDCURL}/faces/ContactUsPage.jsp">
                                <h:outputText value="Contact Us"/>
                            </h:outputLink>
                        to ask any questions or report any concerns to the Administrator of this Dataverse). </p>
                      
                  </ui:panelLayout>
                  </ui:panelLayout>
                  
              </ui:panelLayout>
          </ui:panelLayout>
          
        </ui:form>
    </f:subview>
</jsp:root>
