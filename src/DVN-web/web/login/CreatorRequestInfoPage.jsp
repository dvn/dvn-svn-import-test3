<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
    <f:subview id="CreatorRequestInfoPageView">
        <ui:form  id="creatorRequestForm">
          <input type="hidden" name="pageName" value="CreatorRequestPage"/>
            <h:inputHidden id="vdcId" value="#{VDCRequest.currentVDCId}"/>

            <div id="dvn_createDvRequest" class="dvn_section dvn_overflow">
    	<div id="requestHeader" class="dvn_overflow">
        	<div id="requestContentTitle">Create Your Own Dataverse</div>
        	<div id="requestContentDescRight">Create a dataverse to upload your own data sets and create collections of data. Archiving with a dataverse extends the useful life of data, preserving it in perpetuity against the failure of computers systems and the obsolescence of data formats.</div>
        </div>
        
        <div style="margin-bottom: 12px; background-image: none; background-color: #ccc;" id="requestTimeline">
            <div style="left: 87px;" class="requestTimelinePoint"><img alt="" border="0" src="/dvn/resources/lrg-grey-bullet.gif" /></div>
            <div style="left: 301px;" class="requestTimelinePoint"><img alt="" border="0" src="/dvn/resources/lrg-grey-bullet.gif" /></div>
            <div style="left: 513px;" class="requestTimelinePoint"><img alt="" border="0" src="/dvn/resources/lrg-grey-bullet.gif" /></div>
            <div style="left: 727px;" class="requestTimelinePoint"><img alt="" border="0" src="/dvn/resources/lrg-grey-bullet.gif" /></div>
        </div>

        <div><div class="dvn_margin12 dvn_overflow" id="requestIntroSteps">
        	<div class="requestIntroStepCard" style="margin-right: 24px; background: #f4ebc7 url(/dvn/resources/request-intro-rndtop1.png) no-repeat;">
                    <h3>Create<br/>Account</h3>
                    <p>Register with our network, by creating an account...</p>
                    <div class="stepCardBtm">
                     <h:outputText value="&#160;" escape="false"/>
                    </div>
		</div>
        	<div class="requestIntroStepCard" style="margin-right: 24px; background: #f4ebc7 url(/dvn/resources/request-intro-rndtop2.png) no-repeat;">
                    <h3>Terms<br/>of Use</h3>
                    <p>Read and agree to the terms of use...</p>
                    <div class="stepCardBtm">
                     <h:outputText value="&#160;" escape="false"/>
                    </div>
		</div>
                <div class="requestIntroStepCard" style="margin-right: 24px; background: #f4ebc7 url(/dvn/resources/request-intro-rndtop3.png) no-repeat;">
                    <h3>Name<br/>Dataverse</h3>
                    <p>Enter the title, type and affiliation...</p>
                    <p>click save...</p>
                    <div class="stepCardBtm">
                     <h:outputText value="&#160;" escape="false"/>
                    </div>
		</div>
                <div class="requestIntroStepCard" style="background: #f4ebc7 url(/dvn/resources/request-intro-rndtop4.png) no-repeat;">
                    <h3 style="line-height:1.85;">Success!</h3>
                    <p>You have created your own dataverse and are now ready to start adding data.</p>
                    <div class="stepCardBtm">
                     <h:outputText value="&#160;" escape="false"/>
                    </div>
		</div>
        </div>
        <div id="requestIntroCont">
                  <h:commandLink styleClass="requestIntroContLink" action="#{LoginWorkflowBean.beginCreatorWorkflow}">
                                <h:outputText value="Continue" escape="false"/>
                  </h:commandLink>
                  <br/>
                  <h:commandLink styleClass="requestIntroContLinkNote" rendered="#{VDCSession.loginBean==null }" action="#{LoginWorkflowBean.beginLoginCreatorWorkflow}">
                                <h:outputText value="Already have an account? Log In" escape="false"/>
                  </h:commandLink>
            </div>
        </div>
    </div>

        </ui:form>
    </f:subview>
</jsp:root>
