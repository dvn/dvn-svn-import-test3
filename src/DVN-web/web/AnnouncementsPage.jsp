<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
                        xmlns:h="http://java.sun.com/jsf/html" 
                        xmlns:jsp="http://java.sun.com/JSP/Page" 
                        xmlns:ui="http://www.sun.com/web/ui"
                        xmlns:tiles="http://struts.apache.org/tags-tiles">
                            
   <f:subview id="AnnouncementsPageView">
        <ui:form  id="announcementsForm">  
            <h:inputHidden rendered="#{VDCRequest.currentVDCId != null}" id="vdcId" value="#{VDCRequest.currentVDCId}"/>
            
            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    <h3>
                        <h:outputText  value="#{ (VDCRequest.currentVDCId != null) ? 'Local' : 'Network'} #{bundle.announcementsHeading}"/>
                    </h3>
                </div>            
                <div class="dvn_sectionBox dvn_pad12"> 
                    <h:outputText  escape="false" id="pageContent" value="#{ (VDCRequest.currentVDCId == null) ? VDCRequest.vdcNetwork.announcements : VDCRequest.currentVDC.announcements }"/>
                </div>
            </div>
            
        </ui:form>
   </f:subview>
</jsp:root>
