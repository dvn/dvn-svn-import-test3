/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.hmdc.vdcnet.web.login;

import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import edu.harvard.hmdc.vdcnet.web.StudyListing;
import edu.harvard.hmdc.vdcnet.web.common.LoginBean;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import edu.harvard.hmdc.vdcnet.web.common.VDCSessionBean;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Ellen Kraffmiller
 */
public class LoginWorkflowBean extends VDCBaseBean {

    @EJB
    UserServiceLocal userService;
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
    private String workflowType;
    private VDCUser user;
    private Long studyId;

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
    
    
    public String beginCreatorWorkflow() {
        clearWorkflowState();
        workflowType = "creator";
        String nextPage = null;
        LoginBean loginBean = this.getVDCSessionBean().getLoginBean();
        if (loginBean != null) {
            grantWorkflowPermission();
            nextPage = "addSite";
        } else {
         
            nextPage = "addAccount";
        }
        return nextPage;
    }
    
    public String beginContributorWorkflow() {
        clearWorkflowState();
        workflowType = "contributor";
        String nextPage = null;
        LoginBean loginBean = this.getVDCSessionBean().getLoginBean();
        if (loginBean != null) {
            grantWorkflowPermission();
            nextPage = "myOptions";
        } else {   
            nextPage = "addAccount";
        }
        return nextPage;
    }
      

    public String beginLoginCreatorWorkflow() {
        clearWorkflowState();
        workflowType = "creator";
        String nextPage = "login";
        return nextPage;

    }
    
       public String beginLoginContributorWorkflow() {
        clearWorkflowState();
        workflowType = "contributor";
        String nextPage = "login";
        return nextPage;

    }


    public String processLogin(VDCUser user, Long studyId) {
        this.user = user;
        if (studyId!=null) {
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
      
            if (workflowType.equals("contributor")) {
                nextPage = "myOptions";
            } else if (workflowType.equals("creator")) {
                nextPage = "addSite";
            } else if (workflowType.equals("fileAccess")) {
               getRequestMap().put("studyId", studyId);
               nextPage = "fileRequest";
            }
            updateSessionForLogin();
      
        }

        getVDCSessionBean().setUserService(null);

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

        // celar the studylistings from prelogin
        StudyListing.clearStudyListingMap(sessionMap);
   

     }
    
     public void clearWorkflowState() {
         workflowType=null;
         user=null;
         studyId=null;
     }
    
     private void grantWorkflowPermission() {
        if (workflowType!=null) {
            if (workflowType.equals("creator")) {
                  userService.makeCreator(user.getId());
            } 
            else if (workflowType.equals("contributor")) {
                  userService.makeContributor(user.getId(),getVDCRequestBean().getCurrentVDCId());
                  // Update detached user object with updated user from cache
                  user = userService.find(user.getId());
            } else if  (workflowType.equals("fileAccess")) {
                // give study file permission
            }
        }
    }

    private void setLoginRedirect() {
        Map sessionMap = getSessionMap();
        String requestContextPath = this.getExternalContext().getRequestContextPath();
        VDC currentVDC = getVDCRequestBean().getCurrentVDC();
        if ("contributor".equals(workflowType)) {
            sessionMap.put("LOGIN_REDIRECT", requestContextPath + "/dv/" + currentVDC.getAlias() + "/faces/admin/OptionsPage.jsp");
        } else if ("creator".equals(workflowType)) {
            sessionMap.put("LOGIN_REDIRECT", requestContextPath + "/faces/site/AddSitePage.jsp");
        } else if ("fileAccess".equals(workflowType)) {
            if (currentVDC != null) {
                sessionMap.put("LOGIN_REDIRECT", requestContextPath + "/dv/" + currentVDC.getAlias() + "/faces/login/FileRequestPage.jsp?studyId=" + studyId);
            } else {
                sessionMap.put("LOGIN_REDIRECT", requestContextPath + "/faces/login/FileRequestPage.jsp");
            }
        } else {
            if (sessionMap.get("ORIGINAL_URL") != null) {
                sessionMap.put("LOGIN_REDIRECT", sessionMap.get("ORIGINAL_URL"));
                sessionMap.remove("ORIGINAL_URL");
            } else {
                //  HttpServletRequest request = this.getExternalContext().getRequestContextPath()
                if (currentVDC != null) {
                    sessionMap.put("LOGIN_REDIRECT", requestContextPath + "/dv/" + currentVDC.getAlias() + "/faces/HomePage.jsp");
                } else {
                    sessionMap.put("LOGIN_REDIRECT", requestContextPath + "/faces/HomePage.jsp");
                }
            }
        }
        workflowType=null;
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
}
