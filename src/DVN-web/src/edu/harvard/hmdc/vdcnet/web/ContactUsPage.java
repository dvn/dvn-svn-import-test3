/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package edu.harvard.hmdc.vdcnet.web;

import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.model.SelectItem;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.webapp.http.common.Request;
import edu.harvard.hmdc.vdcnet.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.util.ExceptionMessageWriter;
import edu.harvard.hmdc.vdcnet.vdc.Captcha;
import edu.harvard.hmdc.vdcnet.vdc.CaptchaServiceLocal;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

/*
 * ContactUsPage.java
 *
 * Created on October 18, 2006, 4:24 PM
 */
import javax.servlet.http.HttpServletRequest;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import net.tanesha.recaptcha.http.SimpleHttpLoader;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class ContactUsPage extends VDCBaseBean implements java.io.Serializable {
    @EJB MailServiceLocal mailService;
    @EJB CaptchaServiceLocal captchService;
    
    private String ERROR_MESSAGE   = new String("An Error Occurred.");
    private String SUCCESS_MESSAGE = new String("An e-mail has been sent successfully!");
    private String EMAIL_ERROR_MESSAGE = new String("An error occurred. The message was not sent.");
    private Captcha c;
    private ReCaptchaImpl r;
    private SimpleHttpLoader l;
    
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    
    
    // </editor-fold>


    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public ContactUsPage() {
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
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init() {
        super.init();
         this.setFullName((getVDCSessionBean().getLoginBean() != null) ? getVDCSessionBean().getLoginBean().getUser().getFirstName() + " " + getVDCSessionBean().getLoginBean().getUser().getLastName() : "");
        this.setEmailAddress((getVDCSessionBean().getLoginBean() != null) ? getVDCSessionBean().getLoginBean().getUser().getEmail() : "");
        this.setEmailBody((this.getEmailBody() != null) ? this.getEmailBody() : "");
        this.setSelectedSubject((this.getSelectedSubject() != null) ? this.getSelectedSubject() : "");
        success = false;
        exception = false;
    }

    private HtmlCommandButton btnSend = new HtmlCommandButton();

    public HtmlCommandButton getBtnSend() {
        return btnSend;
    }

    public void setBtnSend(HtmlCommandButton hcb) {
        this.btnSend = hcb;
    }

    private HtmlCommandButton btnCancel = new HtmlCommandButton();

    public HtmlCommandButton getButtonCancel() {
        return btnCancel;
    }

    public void setBtnCancel(HtmlCommandButton hcb) {
        this.btnCancel = hcb;
    }
    
    private String fullName = "";
    
    public String getFullName() {
        return this.fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    private String emailAddress;
    
    public String getEmailAddress() {
        return this.emailAddress;
    }
    
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    private String selectedSubject;
    
    public String getSelectedSubject () {
        return selectedSubject;
    }
    
    public void setSelectedSubject(String selectedSubject) {
        this.selectedSubject = selectedSubject;
    }
    
    private HtmlSelectOneMenu listSubjects = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getListSubjects() {
        return listSubjects;
    }

    public void setListSubjects(HtmlSelectOneMenu hsom) {
        this.listSubjects = hsom;
    }
    
  
    public ArrayList<SelectItem> getListSubjectItems() {
        ArrayList<SelectItem> arr = new ArrayList<SelectItem>();
        arr.add(new SelectItem("-  Select a subject -"));
        arr.add(new SelectItem("Issue accessing a study or studies"));
        arr.add(new SelectItem("Issue downloading a file or files"));
        arr.add(new SelectItem("Issue logging in or creating an account"));
        arr.add(new SelectItem("Issues with Analysis and Subsetting"));
        arr.add(new SelectItem("Curator/Administration Issues"));
        arr.add(new SelectItem("Issues with Analysis and Subsetting"));
        arr.add(new SelectItem("Need more information about a topic"));
        arr.add(new SelectItem("Other question or issue"));
        return arr;

    }

   
   
    
    //email body
    private String emailBody;

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }
    
     /**
     * Holds value of property success.
     */
    private boolean success;

    /**
     * Getter for property success.
     * @return Value of property success.
     */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * Setter for property success.
     * @param success New value of property success.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    /**
     * Holds value of property success.
     */
    private boolean exception;

    /**
     * Getter for property success.
     * @return Value of property success.
     */
    public boolean isException() {
        return this.exception;
    }

    /**
     * Setter for property success.
     * @param success New value of property success.
     */
    public void setException(boolean exception) {
        this.exception = exception;
    }
    
    //ACTION METHODS
    public String send_action() {
        String msg  = SUCCESS_MESSAGE;
        Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String challenge = map.get("recaptcha_challenge_field").toString();
        String response = map.get("recaptcha_response_field").toString();
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        success = true;
        ReCaptchaResponse resp = null;
        try {
            resp = r.checkAnswer("127.0.0.1", challenge, response);
            if (resp.isValid()){
                msg = resp.getErrorMessage();
            String fromAddress = "\"" + fullName + "\"<" + emailAddress.trim() + ">";
            mailService.sendMail(fromAddress, (getVDCRequestBean().getCurrentVDCId() == null) ? getVDCRequestBean().getVdcNetwork().getContactEmail() : getVDCRequestBean().getCurrentVDC().getContactEmail(), (getVDCRequestBean().getCurrentVDCId()==null) ? getVDCRequestBean().getVdcNetwork().getName()  + " Dataverse Network: " + selectedSubject.trim() : getVDCRequestBean().getCurrentVDC().getName() + " dataverse: " + selectedSubject.trim(), emailBody.trim());
            } else {
                FacesMessage message = new FacesMessage("This field is required.");
                FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage("XXXXXX", message);
                return "invalidResponse";
            }
        } catch (Exception e) {
            success     = false;
            exception   = true;
            msg = EMAIL_ERROR_MESSAGE;
            ExceptionMessageWriter.logException(e);
        } finally {
            System.out.println("oooo "+response+" k "+ resp.isValid());
            ExceptionMessageWriter.addGlobalMessage(msg);
            if (success) return "success"; else return "result";
        }
        
    }
    
    public String cancel_action(){
        if (getVDCRequestBean().getCurrentVDCId() == null)
            return "cancelNetwork";
        else
            return "cancelVDC";
    }
      
        /* validateSubject
     *
     **<p> Utility method to validate the Subject
     * selected.</p>
     *
     * @author Wendy Bossons
     */
    public void validateSubject(FacesContext context, 
                          UIComponent toValidate,
                          Object value) {
        String subject = (String) value;
        if (subject.indexOf("Select a subject") != -1) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Please select a subject.");
            context.addMessage(toValidate.getClientId(context), message);
        }
    }
    
    public void validateCaptcha(FacesContext context,
            UIComponent toValidate,
            Object value) {
        if (c != null) {
            Map map = context.getExternalContext().getRequestParameterMap();
            String challenge = map.get("recaptcha_challenge_field").toString();
            String response = map.get("recaptcha_response_field").toString();
            HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            try {
                System.out.println("Rmote Addr: " + InetAddress.getLocalHost().getCanonicalHostName());
            } catch (UnknownHostException ex) {
                Logger.getLogger(ContactUsPage.class.getName()).log(Level.SEVERE, null, ex);
            }
            ReCaptchaResponse resp = r.checkAnswer(c.getHost(), challenge, response);
            if (!resp.isValid()) {
                ((UIInput) toValidate).setValid(false);
                FacesMessage message = new FacesMessage("Please fill in the reCAPTCHA form.");
                context.addMessage(toValidate.getClientId(context), message);
            }
        }
    }

    public String getCaptcha() {
        c = captchService.findCaptcha();
        String retVal = null;
        if (c != null) {
            r = new ReCaptchaImpl();
            l = new SimpleHttpLoader();

            r.setIncludeNoscript(true);
            r.setRecaptchaServer(ReCaptchaImpl.HTTPS_SERVER);
            r.setHttpLoader(l);
            r.setPrivateKey(c.getPrivateKey());
            r.setPublicKey(c.getPublicKey());
            retVal = r.createRecaptchaHtml(null, null);
        } else {
            retVal = "";
        }
        return retVal;
    }
}

