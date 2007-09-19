<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">

<jsp:directive.page errorPage="/ExceptionHandler" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>       
   
<tiles:importAttribute name="pageTitle" scope="request"/>

<f:loadBundle basename="Bundle" var="bundle"/>
<f:loadBundle basename="BundleAnalysis" var="bundleAnalysis"/>
<f:view>
<ui:page id="layoutpage">
<ui:html>
      <ui:head title="#{pageTitle.value}">
         <h:outputText value="&#60;meta name='robots' content='nofollow' /&#62;" escape="false" rendered="#{pageTitle.value == 'DVN - Study' }" />
         <h:outputText value="&#60;meta name='robots' content='nofollow' /&#62;" escape="false" rendered="#{pageTitle.value == 'DVN - Variable Information' }" />
         
         <ui:link  id="link2" url="/resources/stylesheet.css"/>
         <ui:script url="/resources/EventCapture.js"/>
      </ui:head>
      <body>
              <f:subview id="banner">
                  <tiles:insert name="banner" flush="false"/>
              </f:subview>
             <div class="dvn_wrapper">
             <div class="dvn_content">               
                  <f:subview id="menubar">
                      <tiles:insert name="menubar" flush="false"/>
                  </f:subview>

                  <f:subview id="content">
                      <tiles:insert name="content" flush="false"/>
                  </f:subview>
                   </div>
                </div>   
              <f:subview id="footer">
                  <tiles:insert name="footer" flush="false"/>
              </f:subview>
              <!-- <script language="javascript" src="http://localhost:8080/DvnAwstats/cgi/awstats/js/awstats_misc_tracker.js"></script>
              <noscript>
                  <img src="http://localhost:8080/DvnAwstats/cgi/awstats/js/awstats_misc_tracker.js?nojs=y" height="0" width="0" border="0" style="display:block"/>
              </noscript> -->
          
      </body>
</ui:html>
</ui:page>
</f:view>
</jsp:root>
