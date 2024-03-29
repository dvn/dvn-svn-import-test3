/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.login;

import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.util.PropertyUtil;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.web.StudyListing;
import edu.harvard.iq.dvn.core.web.common.LoginBean;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.common.VDCSessionBean;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Ellen Kraffmiller
 */
public class LoginWorkflowBean extends VDCBaseBean implements java.io.Serializable  {

    @EJB
    UserServiceLocal userService;
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
    private String workflowType;
    private VDCUser user;
    private Long studyId;
    public static String WORKFLOW_TYPE_CONTRIBUTOR = "contributor";
    public static String WORKFLOW_TYPE_CREATOR = "creator";
    public static String WORKFLOW_TYPE_FILE_ACCESS = "fileAccess";
    public static String WORKFLOW_TYPE_COMMENTS = "comments";

    /** Creates a new instance of LoginWorkflowBean */
    public LoginWorkflowBean() {
    }

    public String beginLoginWorkflow() {
        clearWorkflowState();
        return "login";
    }
    
    public String beginFileAccessWorkflow(Long studyId) {
        clearWorkflowState();
        workflowType="fileAccess"; 
        this.studyId=studyId;
          String nextPage =  null;
          LoginBean loginBean = this.getVDCSessionBean().getLoginBean();
        if (loginBean == null) {
            nextPage = "fileRequestAccount";
        } else {
              nextPage = "fileRequest"; 
        }
  
        return nextPage;
    }

    public String beginCommentsWorkflow(Long studyId) {
        clearWorkflowState();
        workflowType = WORKFLOW_TYPE_COMMENTS;
        this.studyId=studyId;
        String nextPage = "addAccount";
        return nextPage;
    }
    
    
    public String beginCreatorWorkflow() {
        clearWorkflowState();
        workflowType = WORKFLOW_TYPE_CREATOR;
        String nextPage = null;
        LoginBean loginBean = this.getVDCSessionBean().getLoginBean();
        if (loginBean != null) {
            user = loginBean.getUser();
            grantWorkflowPermission();
            nextPage = "addSite";
        } else {
         
            nextPage = "addAccount";
        }
        return nextPage;
    }
    
    public String beginContributorWorkflow() {
        clearWorkflowState();
        workflowType = this.WORKFLOW_TYPE_CONTRIBUTOR;
        String nextPage = null;
        LoginBean loginBean = this.getVDCSessionBean().getLoginBean();
        if (loginBean != null) {
            grantWorkflowPermission();
            nextPage = "contributorSuccess";
        } else {   
            nextPage = "addAccount";
        }
        return nextPage;
    }
      

    public String beginLoginCreatorWorkflow() {
        clearWorkflowState();
        workflowType =WORKFLOW_TYPE_CREATOR;
        String nextPage = "login";
        return nextPage;

    }
    
       public String beginLoginContributorWorkflow() {
        clearWorkflowState();
        workflowType =   WORKFLOW_TYPE_CONTRIBUTOR;
 
        String nextPage = "login";
        return nextPage;

    }


    public String processLogin(VDCUser user, Long studyId) {
        this.user = user;
        if (studyId != null) {
            this.studyId = studyId;
        }
        String nextPage = null;
        if (user.isAgreedTermsOfUse() || !vdcNetworkService.find().isTermsOfUseEnabled()) {
            updateSessionAndRedirect();
            nextPage = "home";
        } else {
            
            nextPage = "accountTermsOfUse";
        }
        return nextPage;
    }

  public String processAddAccount(VDCUser newUser) {
        user = newUser;
        String nextPage = null;
        if (workflowType == null) {
            getRequestMap().put("fromPage", "AddAccountPage");
            getRequestMap().put("userId", user.getId());
            nextPage = "viewAccount";
        } else if (vdcNetworkService.find().isTermsOfUseEnabled()) {
            nextPage = "accountTermsOfUse";
        } else {
            if (workflowType.equals(WORKFLOW_TYPE_CONTRIBUTOR)) {
                nextPage = "contributorSuccess";
            } else if (workflowType.equals(WORKFLOW_TYPE_CREATOR)) {
                nextPage = "addSite";
            } else if (workflowType.equals(WORKFLOW_TYPE_FILE_ACCESS)) {
               getRequestMap().put("studyId", studyId);
               nextPage = "fileRequest";
            } else if (workflowType.equals(WORKFLOW_TYPE_COMMENTS)) {
               getRequestMap().put("studyId", studyId);
               nextPage = "studyPage";
            }
            updateSessionForLogin();
        }
        return nextPage;
    }

    public String processTermsOfUse(boolean termsAccepted) {
        String forward = null;
        if (user != null) {
            if (termsAccepted) {
                userService.setAgreedTermsOfUse(user.getId(), termsAccepted);
                user.setAgreedTermsOfUse(termsAccepted);  // update detached object because it will be added to the loginBean
                updateSessionAndRedirect();
            }

        }
        forward = "home";
        return forward;

    }

    private void updateSessionAndRedirect() {
        updateSessionForLogin();
        setLoginRedirect();
     
    }
    
   
    /**
     *  Create loginBean and add it to the Session, do other session-related updates.
     * @param session
     * @param sessionMap
     * @param vdcSessionBean
     * @param user
     */
    
    private void updateSessionForLogin() {
        //first remove any existing ipUserGroup info from the session
        ExternalContext externalContext = getExternalContext();
        Map sessionMap = getSessionMap();
        VDCSessionBean vdcSessionBean = getVDCSessionBean();
        HttpSession session = (HttpSession) externalContext.getSession(true);
        if (vdcSessionBean.getIpUserGroup() != null) {
            session.removeAttribute("ipUserGroup");
            session.removeAttribute("isIpGroupChecked");
        }
        grantWorkflowPermission();
        LoginBean loginBean = new LoginBean();
        loginBean.setUser(user);
        vdcSessionBean.setLoginBean(loginBean);

        // copy all terms of use from session TermsOfUseMap
        loginBean.getTermsfUseMap().putAll(vdcSessionBean.getTermsfUseMap());
        // then clear the sessions version
        vdcSessionBean.getTermsfUseMap().clear();
        // clear the studylistings from prelogin
        StudyListing.clearStudyListingMap(sessionMap);
     }
    
     public void clearWorkflowState() {
         workflowType=null;
         user=null;
         studyId=null;
     }
    
     private void grantWorkflowPermission() {
        if (workflowType!=null) {
            if (workflowType.equals(WORKFLOW_TYPE_CREATOR)) {
                  userService.makeCreator(user.getId());
            } 
            else if (workflowType.equals(WORKFLOW_TYPE_CONTRIBUTOR)) {
                // this workflow no longer makes the contributor immediately (it is used to create an account);
                // instead the user will become a contributor when they make an actual contribution
                  
            } else if  (workflowType.equals(WORKFLOW_TYPE_FILE_ACCESS)) {
                // give study file permission
            }
            // Update detached user object with updated user from database
            user = userService.find(user.getId());
        }
    }

    private void setLoginRedirect() {
        Map sessionMap = getSessionMap();
        String requestContextPath = this.getExternalContext().getRequestContextPath();
        VDC currentVDC = getVDCRequestBean().getCurrentVDC();
        if (WORKFLOW_TYPE_CONTRIBUTOR.equals(workflowType)) {
            sessionMap.put("LOGIN_REDIRECT", requestContextPath + getVDCRequestBean().getCurrentVDCURL() + "/faces/login/ContributorRequestSuccessPage.xhtml");
        } else if (WORKFLOW_TYPE_CREATOR.equals(workflowType)) {
            sessionMap.put("LOGIN_REDIRECT", requestContextPath + "/faces/site/AddSitePage.xhtml");
        } else if (WORKFLOW_TYPE_FILE_ACCESS.equals(workflowType)) {
            if (currentVDC != null) {
                sessionMap.put("LOGIN_REDIRECT", requestContextPath + "/dv/" + currentVDC.getAlias() + "/faces/login/FileRequestPage.xhtml?studyId=" + studyId);
            } else {
                sessionMap.put("LOGIN_REDIRECT", requestContextPath + "/faces/login/FileRequestPage.xhtml");
            }
        } else {
            if (sessionMap.get("ORIGINAL_URL") != null) {
                sessionMap.put("LOGIN_REDIRECT", sessionMap.get("ORIGINAL_URL"));
                sessionMap.remove("ORIGINAL_URL");
            } else {
                //  HttpServletRequest request = this.getExternalContext().getRequestContextPath()
                if (currentVDC != null) {
                    sessionMap.put("LOGIN_REDIRECT", requestContextPath + "/dv/" + currentVDC.getAlias() + "/faces/StudyListingPage.xhtml");
                } else {
                    sessionMap.put("LOGIN_REDIRECT", requestContextPath + "/faces/HomePage.xhtml");
                }
            }
        }
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public VDCUser getUser() {
        return user;
    }

    public void setUser(VDCUser user) {
        this.user = user;
    }
    
    public String getWorkflowType() {
        return workflowType;
    }
    
    /**
     * Used in the Creator Workflow Success page - for url of dataverse home page.
     * @return hostUrl (based on inet Address)
     */
    public String getHostUrl() {
        return PropertyUtil.getHostUrl();
    }
    
    public boolean isContributorWorkflow() {
        return workflowType!=null && workflowType.equals(WORKFLOW_TYPE_CONTRIBUTOR);
    }
    public boolean isCreatorWorkflow() {
        return workflowType!=null && workflowType.equals(WORKFLOW_TYPE_CREATOR);
    }
    public boolean isFileAccessWorkflow() {
        return workflowType!=null && workflowType.equals(WORKFLOW_TYPE_FILE_ACCESS);
    }
    public boolean isPlainWorkflow() {
        return workflowType==null;
    }
   
}
