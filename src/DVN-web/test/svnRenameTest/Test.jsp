<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:gui="http://java.sun.com/jsf/facelets"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core" 
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:ui="http://www.sun.com/web/ui"
      xmlns:dvn="/WEB-INF/tlds/dvn-components"
      xmlns:a4j="https://ajax4jsf.dev.java.net/ajax">
<!-- 
    Document   : Test
    Created on : Jul 23, 2008, 4:15:23 PM
    Author     : wbossons
-->

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>TODO supply a title</title>
    </head>
    <body>
        <gui:composition template="/template.xhtml">

<gui:param name="pageTitle" value="DVN - Home" />

  <gui:define name="body">
        <span  jsfc="h:outputText" value="#{TestPage.testBeforeString}"></span>
        <a jsfc="h:outputLink" value="#{TestPage.bogusUrl}">
            <span jsfc="h:outputText" value="#{TestPage.bogusUrlText}"></span>
        </a>.
        </gui:define>
        </gui:composition>
    </body>
</html>
