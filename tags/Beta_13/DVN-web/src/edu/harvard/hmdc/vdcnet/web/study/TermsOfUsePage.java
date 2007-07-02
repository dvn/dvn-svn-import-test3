/*
 * StudyPage.java
 *
 * Created on September 19, 2006, 2:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.study;

import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.io.IOException;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Gustavo Durand
 */
public class TermsOfUsePage extends VDCBaseBean {
    @EJB  private StudyServiceLocal studyService;
    
    public TermsOfUsePage() {}
    
  
    private Long studyId;
    private edu.harvard.hmdc.vdcnet.study.Study study;
    private String redirectPage;

    public edu.harvard.hmdc.vdcnet.study.Study getStudy() {
        return study;
    }

    public void setStudy(edu.harvard.hmdc.vdcnet.study.Study study) {
        this.study = study;
    }

    public Long getStudyId() {
        return this.studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }
    
    public String getRedirectPage() {
        return redirectPage;
    }

    public void setRedirectPage(String redirectPage) {
        this.redirectPage = redirectPage;
    }

    public void init() {
        super.init();
        // first check if the parameter ha sbeen set as query String parameters
        try {
            studyId = new Long(getRequestParam("studyId"));
        } catch (NumberFormatException ex) {}
        
        if (studyId == null) {
            // now check specific JSF post parameters
            try {
                if ( isFromPage("TermsOfUsePage") ) {
                    studyId = new Long(getRequestParam("content:termsOfUsePageView:form1:studyId"));    
                } else if ( isFromPage("StudyPage") ) {
                    studyId = new Long(getRequestParam("content:studyPageView:form1:studyId"));
                } else { //check the requestBean; if coming from some other page
                    studyId = getVDCRequestBean().getStudyId();
                }
            } catch (NumberFormatException ex) {}
        }
        if (studyId != null) {
            setStudy(studyService.getStudyDetail(studyId));
        } else {
            // WE SHOULD HAVE A STUDY ID, throw an error
            System.out.println("ERROR: in editStudyPage, without a serviceBean or a studyId");
        }               
    }       
    
    
    private boolean vdcTermsAccepted;
    private boolean studyTermsAccepted;

    public boolean isTermsAcceptanceRequired() {
        return isVdcTermsRequired() || isStudyTermsRequired();
    }
    
    public void setTermsAcceptanceRequired(boolean termsAcceptanceRequired) {} // dummy method since the get is just a wrapper
    
    public boolean isVdcTermsRequired() {
        boolean vdcTermsRequired = study.getOwner().isTermsOfUseEnabled();
        if (vdcTermsRequired) {
            return getTermsOfUseMap().get("vdc_" + study.getOwner().getId() ) == null;
        }
        
        return false;
    }

    public boolean isStudyTermsRequired() {
        boolean studyTermsRequired = study.isTermsOfUseEnabled();
        if (studyTermsRequired) {
            return getTermsOfUseMap().get("study_" + study.getId() ) == null;
        }
        
        return false;
    }     
    
    public boolean isVdcTermsAccepted() {
        return vdcTermsAccepted;
    }

    public void setVdcTermsAccepted(boolean vdcTermsAccepted) {
        this.vdcTermsAccepted = vdcTermsAccepted;
    }

    public boolean isStudyTermsAccepted() {
        return studyTermsAccepted;
    }

    public void setStudyTermsAccepted(boolean studyTermsAccepted) {
        this.studyTermsAccepted = studyTermsAccepted;
    }

    public String acceptTerms_action () {
        Map termsOfUseMap = getTermsOfUseMap();
        if ( studyTermsAccepted )  {         
            termsOfUseMap.put( "study_" + study.getId(), "accepted" );
        }
        if ( vdcTermsAccepted ) { 
            termsOfUseMap.put( "vdc_" + study.getOwner().getId(), "accepted" );
        }
        if (redirectPage != null) {
            // piggy back on the login redirect logic for now
            String loginRedirect = this.getExternalContext().getRequestContextPath() + getVDCRequestBean().getCurrentVDCURL() + redirectPage;
            getSessionMap().put("LOGIN_REDIRECT", loginRedirect);
            
            // we don't actually want to go to the home page, but we need to fake JSF,
            // so that the redirect happens
            return "home";            
        }
        
        return null;
    }    

    private Map getTermsOfUseMap() {
        if (getVDCSessionBean().getLoginBean() != null) {
            return getVDCSessionBean().getLoginBean().getTermsfUseMap();
        } else {
            return getVDCSessionBean().getTermsfUseMap();
        }        
    }

}
