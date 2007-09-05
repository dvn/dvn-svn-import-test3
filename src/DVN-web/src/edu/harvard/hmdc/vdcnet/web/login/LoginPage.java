/*
 * LoginPage.java
 *
 * Created on October 2, 2006, 5:28 PM
 * Copyright mcrosas
 */
package edu.harvard.hmdc.vdcnet.web.login;

import edu.harvard.hmdc.vdcnet.admin.GroupServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.LoginAffiliate;
import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.web.common.LoginBean;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.FacesException;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class LoginPage extends VDCBaseBean {
     @EJB UserServiceLocal userService;
     @EJB GroupServiceLocal groupService;

    public void init() {
        super.init();
        setAffiliateNames();
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String refererUrl = new String("");
        String protocol = request.getProtocol().substring(0, request.getProtocol().indexOf("/")).toLowerCase();
        String defaultPage = new String("");
        String serverPort  = new String((request.getServerPort() != 80 ? ":" + request.getServerPort():""));
        if (getVDCRequestBean().getCurrentVDC() != null)
            defaultPage = protocol + "://" + request.getServerName() + serverPort + request.getContextPath() + "/dv/" + getVDCRequestBean().getCurrentVDC().getAlias() +  request.getServletPath() + "/HomePage.jsp";
        else
            defaultPage = protocol + "://" + request.getServerName() + serverPort + request.getContextPath() + request.getServletPath() + "/HomePage.jsp";
        // Set referer needed by loginAffiliate -- must be passed on the querystring to the EZProxy
        if (sessionGet("refererUrl") == null) {
            if (request.getHeader("referer") != null && !request.getHeader("referer").equals("") )  {
                    if ( request.getHeader("referer").indexOf("/login/") != -1 
                            || request.getHeader("referer").contains("/admin/")
                            || request.getHeader("referer").contains("/networkAdmin/") ) {
                        refererUrl = defaultPage;
                    } else { 
                        refererUrl = request.getHeader("referer");
                    }
            } else {
                refererUrl = defaultPage;
            }
            
            sessionPut("refererUrl", refererUrl);
        }
     }
    
    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public LoginPage() {
    }


    /** 
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    public void preprocess() {
    }

    /** 
     * <p>Callback method that is called just before rendering takes place.
     * This method will <strong>only</strong> be called for the page that
     * will actually be rendered (and not, for example, on a page that
     * handled a postback and then navigated to a different page).  Customize
     * this method to allocate resources that will be required for rendering
     * this page.</p>
     */
    public void prerender() {
    }

    /** 
     * <p>Callback method that is called after rendering is completed for
     * this request, if <code>init()</code> was called (regardless of whether
     * or not this was the page that was actually rendered).  Customize this
     * method to release resources acquired in the <code>init()</code>,
     * <code>preprocess()</code>, or <code>prerender()</code> methods (or
     * acquired during execution of an event handler).</p>
     */
    public void destroy() {
    }
    
    public String login() {
        if (sessionGet("refererUrl") != null) //remove loginAffiliate related session vars, wjb Sept 2007
            sessionRemove("refererUrl");
        boolean activeOnly=true;
        VDCUser user  = userService.findByUserName(userName,activeOnly);
        if (user==null || !user.getPassword().equals(password)) {
            loginFailed=true;
            return null;
        } else {
            //first remove any existing ipUserGroup info from the session
            if (getVDCSessionBean().getIpUserGroup() != null) {
                HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
                session.removeAttribute("ipUserGroup");
                session.removeAttribute("isIpGroupChecked");
            }
            LoginBean loginBean = new LoginBean();
            // copy all terms of use from session TermsOfUseMap
            loginBean.getTermsfUseMap().putAll( getVDCSessionBean().getTermsfUseMap() );
            // then clear the sessions version
            getVDCSessionBean().getTermsfUseMap().clear();
            loginBean.setUser(user);
            getVDCSessionBean().setLoginBean(loginBean);
            if (hiddenWorkflow.getValue().equals("contributor")) {
                   getSessionMap().put("LOGIN_REDIRECT", this.getExternalContext().getRequestContextPath()+"/dv/"+getVDCRequestBean().getCurrentVDC().getAlias()
                    + "/faces/login/ContributorRequestPage.jsp");               
             } else if (hiddenWorkflow.getValue().equals("creator")) {
                   getSessionMap().put("LOGIN_REDIRECT", this.getExternalContext().getRequestContextPath()
                   + "/faces/login/CreatorRequestPage.jsp");               
            } else if (hiddenWorkflow.getValue().equals("fileAccess")) {
                if (getVDCRequestBean().getCurrentVDC()!=null) {
                    getSessionMap().put("LOGIN_REDIRECT", this.getExternalContext().getRequestContextPath()+"/dv/"+getVDCRequestBean().getCurrentVDC().getAlias()
                    + "/faces/login/FileRequestPage.jsp?studyId="+studyId);               
                } else {
                   getSessionMap().put("LOGIN_REDIRECT", this.getExternalContext().getRequestContextPath()
                   + "/faces/login/FileRequestPage.jsp");     
                }
            } else {
                if (getSessionMap().get("ORIGINAL_URL")!=null ) {
                    getSessionMap().put("LOGIN_REDIRECT",getSessionMap().get("ORIGINAL_URL"));
                    getSessionMap().remove("ORIGINAL_URL");
                } else {
                  //  HttpServletRequest request = this.getExternalContext().getRequestContextPath()
                    if (getVDCRequestBean().getCurrentVDC()!=null) {
                        getSessionMap().put("LOGIN_REDIRECT", this.getExternalContext().getRequestContextPath()+"/dv/"+getVDCRequestBean().getCurrentVDC().getAlias()+"/faces/HomePage.jsp");
                    } else {
                       getSessionMap().put("LOGIN_REDIRECT", this.getExternalContext().getRequestContextPath()+"/faces/HomePage.jsp");          
                    }
                }
            }
            return "home";
            
        }
        
        
    }

    /**
     * Holds value of property userName.
     */
    private String userName;

    /**
     * Getter for property userName.
     * @return Value of property userName.
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Setter for property userName.
     * @param userName New value of property userName.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Holds value of property password.
     */
    private String password;

    /**
     * Getter for property password.
     * @return Value of property password.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Setter for property password.
     * @param password New value of property password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Holds value of property loginFailed.
     */
    private boolean loginFailed;

    /**
     * Getter for property loginFailed.
     * @return Value of property loginFailed.
     */
    public boolean isLoginFailed() {
        return this.loginFailed;
    }

    /**
     * Setter for property loginFailed.
     * @param loginFailed New value of property loginFailed.
     */
    public void setLoginFailed(boolean loginFailed) {
        this.loginFailed = loginFailed;
    }

    /**
     * Holds value of property redirect.
     */
    private String redirect;

    /**
     * Getter for property redirect.
     * @return Value of property redirect.
     */
    public String getRedirect() {
        return this.redirect;
    }

    /**
     * Setter for property redirect.
     * @param redirect New value of property redirect.
     */
    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    /* properties and methods to support
     * ezproxy login
     *
     *
     * @author wbossons
     */
    
    public void loginAffiliate() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(this.getAffiliateName() + "?url=" + sessionGet("refererUrl"));
        } catch (IOException ioe) {
            throw new FacesException(ioe);
        } finally {
            sessionRemove("refererUrl");
        }
    }

    /**
     * Holds value of property affiliateName.
     */
    private String affiliateName;

    /**
     * Getter for property affiliateName.
     * @return Value of property affiliateName.
     */
    public String getAffiliateName() {
        return this.affiliateName;
    }

    /**
     * Setter for property affiliateName.
     * @param affiliateName New value of property affiliateName.
     */
    public void setAffiliateName(String affiliateName) {
        this.affiliateName = affiliateName;
    }
    
    private SelectItem[] affiliateNames;
    
    private void setAffiliateNames() {
        List list = (List)groupService.findAllLoginAffiliates();
        Iterator iterator =  list.iterator();
        affiliateNames = new SelectItem[list.size()];
        int i = 0;
        while (iterator.hasNext()){
            //now populate this
            this.setIsAffiliates("true");
            LoginAffiliate loginaffiliate = (LoginAffiliate)iterator.next();
            SelectItem selectitem = new SelectItem();
            selectitem.setLabel((String)loginaffiliate.getName());
            selectitem.setValue((String)loginaffiliate.getUrl());
            affiliateNames[i] = selectitem;
            i++;
        }
    }

    
        public SelectItem[] getAffiliateNames() {
            return affiliateNames;
        } 
        
        public void changeAffiliateName(ValueChangeEvent event){
            String newValue = (String)event.getNewValue();
            setAffiliateName(newValue);
        } 

    /**
     * Holds value of property isAffiliates.
     */
    private String isAffiliates;

    /**
     * Getter for property isAffiliates.
     * @return Value of property isAffiliates.
     */
    public String getIsAffiliates() {
        return this.isAffiliates;
    }

    /**
     * Setter for property isAffiliates.
     * @param isAffiliates New value of property isAffiliates.
     */
    public void setIsAffiliates(String isAffiliates) {
        this.isAffiliates = isAffiliates;
    }

    /**
     * Holds value of property workflow.
     */
    private String workflow;

    /**
     * Getter for property workflow.
     * @return Value of property workflow.
     */
    public String getWorkflow() {
        return this.workflow;
    }

    /**
     * Setter for property workflow.
     * @param workflow New value of property workflow.
     */
    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    /**
     * Holds value of property hiddenWorkflow.
     */
    private HtmlInputHidden hiddenWorkflow;

    /**
     * Getter for property htmlHiddenWorkflow.
     * @return Value of property htmlHiddenWorkflow.
     */
    public javax.faces.component.html.HtmlInputHidden getHiddenWorkflow() {
        return this.hiddenWorkflow;
    }

    /**
     * Setter for property htmlHiddenWorkflow.
     * @param htmlHiddenWorkflow New value of property htmlHiddenWorkflow.
     */
    public void setHiddenWorkflow(javax.faces.component.html.HtmlInputHidden hiddenWorkflow) {
        this.hiddenWorkflow = hiddenWorkflow;
    }

    /**
     * Holds value of property studyId.
     */
    private Long studyId;

    /**
     * Getter for property studyId.
     * @return Value of property studyId.
     */
    public Long getStudyId() {
        return this.studyId;
    }

    /**
     * Setter for property studyId.
     * @param studyId New value of property studyId.
     */
    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    /**
     * Holds value of property hiddenStudyId.
     */
    private HtmlInputHidden hiddenStudyId;

    /**
     * Getter for property hiddenStudyId.
     * @return Value of property hiddenStudyId.
     */
    public HtmlInputHidden getHiddenStudyId() {
        return this.hiddenStudyId;
    }

    /**
     * Setter for property hiddenStudyId.
     * @param hiddenStudyId New value of property hiddenStudyId.
     */
    public void setHiddenStudyId(HtmlInputHidden hiddenStudyId) {
        this.hiddenStudyId = hiddenStudyId;
    }
    
}

