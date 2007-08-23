<jsp:root version="2.0" xmlns:f="http://java.sun.com/jsf/core" 
    xmlns:h="http://java.sun.com/jsf/html" 
    xmlns:jsp="http://java.sun.com/JSP/Page" 
    xmlns:ui="http://www.sun.com/web/ui"
    xmlns:tiles="http://struts.apache.org/tags-tiles"
    xmlns:c="http://java.sun.com/jsp/jstl/core">
    
    <f:subview id="deleteStudyPageView">
      
        <h:form  id="form1">
            <input type="hidden" name="pageName" value="DeleteSuccessPage"/>

            <div class="dvn_section">
                <div class="dvn_sectionTitle">
                    
                    <h:outputText value="Delete Successful"/>
                 
                </div>            
                <div class="dvn_sectionBox"> 
                    <div class="dvn_margin12">
                        
                        <h:outputText  id="outputText2" value="The dataverse has been deleted successfully. "/> 
                        
                    </div>
                </div>
            </div>
        </h:form>   
    </f:subview>
</jsp:root>
