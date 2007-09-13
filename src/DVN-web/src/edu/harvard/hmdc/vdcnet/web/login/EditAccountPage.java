/*
 * AddAccountPage.java
 *
 * Created on October 4, 2006, 1:04 PM
 * Copyright mcrosas
 */
package edu.harvard.hmdc.vdcnet.web.login;

import edu.harvard.hmdc.vdcnet.admin.EditUserService;
import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.util.CharacterValidator;
import edu.harvard.hmdc.vdcnet.web.common.StatusMessage;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.context.FacesContext;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class EditAccountPage extends VDCBaseBean {
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
      }
   
    
    // </editor-fold>
    @EJB EditUserService editUserService;
    @EJB UserServiceLocal userService;
    
    private HtmlSelectOneRadio resetPasswordRadio;
    
    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public EditAccountPage() {
    }
    
    
    
    /**
     * <p>Callback method that is called whenever a page is navigated to,
     * either directly via a URL, or indirectly via page navigation.
     * Customize this method to acquire resources that will be needed
     * for event handlers and lifecycle methods, whether or not this
     * page is performing post back processing.</p>
     *
     * <p>Note that, if the current request is a postback, the property
     * values of the components do <strong>not</strong> represent any
     * values submitted with this request.  Instead, they represent the
     * property values that were saved for this view when it was rendered.</p>
     */
    public void init() {
        super.init();
        if ( isFromPage("EditAccountPage")&& sessionGet(editUserService.getClass().getName())!=null ) {
            editUserService = (EditUserService) sessionGet(editUserService.getClass().getName());
            user = editUserService.getUser();
             
        } else {
        //    editUserService.setUser();
            sessionPut( editUserService.getClass().getName(), editUserService);
            //sessionPut( (studyService.getClass().getName() + "."  + studyId.toString()), studyService);
            editUserService.setUser(userId); 
            user = editUserService.getUser();
         
        }
        
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
    
    /**
     * Holds value of property user.
     */
    private VDCUser user;
    
    /**
     * Getter for property user.
     * @return Value of property user.
     */
    public VDCUser getUser() {
        return this.user;
    }
    
    /**
     * Setter for property user.
     * @param user New value of property user.
     */
    public void setUser(VDCUser user) {
        this.user = user;
    }
    
    public EditUserService getEditUserService() {
        return editUserService;
    }
    
    public void validateUserName(FacesContext context, 
                          UIComponent toValidate,
                          Object value) {
    CharacterValidator charactervalidator = new CharacterValidator();
    charactervalidator.validate(context, toValidate, value);
    String userName = (String) value;
    
    boolean userNameFound = false;
    VDCUser foundUser = userService.findByUserName(userName);
    if (foundUser!=null && foundUser.getId()!= user.getId()) {
        userNameFound=true;
    }
    
    if (userNameFound) {
       ((UIInput)toValidate).setValid(false);

        FacesMessage message = new FacesMessage("This Username is already taken.");
        context.addMessage(toValidate.getClientId(context), message);
    }

}
    
    public String save() {
        
       
        
        editUserService.save();
        // If the currently logged-in user is updating is account, reset the User object in the session
        if (getVDCSessionBean().getLoginBean().getUser().getId().equals(user.getId())) {
            this.getVDCSessionBean().getLoginBean().setUser(user);
        }
        // Save userId as requestAttribute so it can be used by AccountPage
        this.getRequestMap().put("userId",user.getId());
        StatusMessage msg = new StatusMessage();
        msg.setMessageText("User account updated successfully.");
        msg.setStyleClass("successMessage");
        getRequestMap().put("statusMessage",msg);
       
        
        return "result";
    }
    
    public String cancel() {
        editUserService.cancel();
        getVDCSessionBean().setUserService(null);
        return "myNetworkOptions";
    }

    /**
     * Holds value of property userId.
     */
    private Long userId;

    /**
     * Getter for property userId.
     * @return Value of property userId.
     */
    public Long getUserId() {
        return this.userId;
    }

    /**
     * Setter for property userId.
     * @param userId New value of property userId.
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public HtmlSelectOneRadio getResetPasswordRadio() {
        return resetPasswordRadio;
    }

    public void setResetPasswordRadio(HtmlSelectOneRadio resetPasswordRadio) {
        this.resetPasswordRadio = resetPasswordRadio;
    }

    /**
     * Holds value of property resetPassword.
     */
    private String resetPassword;

    /**
     * Getter for property resetPassword.
     * @return Value of property resetPassword.
     */
    public String getResetPassword() {
        return this.resetPassword;
    }

    /**
     * Setter for property resetPassword.
     * @param resetPassword New value of property resetPassword.
     */
    public void setResetPassword(String resetPassword) {
        this.resetPassword = resetPassword;
    }



}

