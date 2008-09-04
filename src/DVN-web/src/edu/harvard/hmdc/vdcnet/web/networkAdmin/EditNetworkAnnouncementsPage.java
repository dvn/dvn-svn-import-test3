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

/*
 * EditNetworkAnnouncementsPage.java
 *
 * Created on October 19, 2006, 4:40 PM
 * 
 */
package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.component.html.HtmlInputTextarea;
import edu.harvard.hmdc.vdcnet.util.ExceptionMessageWriter;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetwork;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class EditNetworkAnnouncementsPage extends VDCBaseBean implements java.io.Serializable  {
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }
    
    private HtmlPanelGrid gridPanel1 = new HtmlPanelGrid();

    public HtmlPanelGrid getGridPanel1() {
        return gridPanel1;
    }

    public void setGridPanel1(HtmlPanelGrid hpg) {
        this.gridPanel1 = hpg;
    }
    
    private HtmlOutputText outputText1 = new HtmlOutputText();

    public HtmlOutputText getOutputText1() {
        return outputText1;
    }

    public void setOutputText1(HtmlOutputText hot) {
        this.outputText1 = hot;
    }


    private HtmlOutputText outputText4 = new HtmlOutputText();

    public HtmlOutputText getOutputText4() {
        return outputText4;
    }

    public void setOutputText4(HtmlOutputText hot) {
        this.outputText4 = hot;
    }

    private HtmlSelectBooleanCheckbox checkbox2 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox2() {
        return checkbox2;
    }

    public void setCheckbox2(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox2 = hsbc;
    }


    private HtmlInputTextarea textArea1 = new HtmlInputTextarea();

    public HtmlInputTextarea getTextArea1() {
        return textArea1;
    }

    public void setTextArea1(HtmlInputTextarea hit) {
        this.textArea1 = hit;
    }

    private HtmlCommandButton btnSave = new HtmlCommandButton();

    public HtmlCommandButton getBtnSave() {
        return btnSave;
    }

    public void setBtnSave(HtmlCommandButton hcb) {
        this.btnSave = hcb;
    }

    private HtmlCommandButton btnCancel = new HtmlCommandButton();

    public HtmlCommandButton getBtnCancel() {
        return btnCancel;
    }

    public void setBtnCancel(HtmlCommandButton hcb) {
        this.btnCancel = hcb;
    }
    
    // </editor-fold>


    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public EditNetworkAnnouncementsPage() {
    }


     /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init() {
        super.init();
        success = false;
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
    
    private String networkAnnouncements;
    
    public String getNetworkAnnouncements() {
        return networkAnnouncements;
    }
    
    public void setNetworkAnnouncements(String networkAnnouncements) {
        this.networkAnnouncements = networkAnnouncements;
    }
    
    private boolean chkEnableNetworkAnnouncements;
    
    public boolean isChkEnableNetworkAnnouncements() {
        return chkEnableNetworkAnnouncements;
    }
    
    public void setChkEnableNetworkAnnouncements(boolean chkEnableNetworkAnnouncements) {
        this.chkEnableNetworkAnnouncements = chkEnableNetworkAnnouncements;
    }
    
    public String save_action() {
        String msg = SUCCESS_MESSAGE;
        success    = true;
        try {
            if (validateAnnouncementsText()) {
                setChkEnableNetworkAnnouncements(chkEnableNetworkAnnouncements);
                setNetworkAnnouncements(networkAnnouncements);
                // Get the Network
                VDCNetwork vdcnetwork = getVDCRequestBean().getVdcNetwork();
                vdcnetwork.setDisplayAnnouncements(this.isChkEnableNetworkAnnouncements());
                vdcnetwork.setAnnouncements(this.getNetworkAnnouncements());
                vdcNetworkService.edit(vdcnetwork);
                FacesContext.getCurrentInstance().addMessage("editNetworkAnnouncementsForm:btnSave", new FacesMessage(msg));
            } else {
                ExceptionMessageWriter.removeGlobalMessage(SUCCESS_MESSAGE);
                success = false;
            }
        } catch (Exception e) {
            msg = "An error occurred: " + e.getCause().toString();
            System.out.println(msg);
        } finally {
            return "result";
        }
    }
    
    public String cancel_action(){
        if (getVDCRequestBean().getCurrentVDCId() == null)
            return "cancelNetwork";
        else
            return "cancelVDC";
    }
    
    //UTILITY METHODS
    
    /** validateAnnouncementsText
     *
     *
     * @author wbossons
     *
     */
    
    private String SUCCESS_MESSAGE = new String("Update Successful! Go to Home to see your changes.");
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
    
    public boolean validateAnnouncementsText() {
        boolean isAnnouncements = true;
        String elementValue = networkAnnouncements;
        if ( (elementValue == null || elementValue.equals("")) && (isChkEnableNetworkAnnouncements()) ) {
            isAnnouncements = false;
            success = false;
            FacesMessage message = new FacesMessage("To enable announcements, you must also enter announcements in the field below.  Please enter announcements as either plain text or html.");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("editNetworkAnnouncementsForm:networkAnnouncements", message);
            context.renderResponse();
        }
        return isAnnouncements;
    }
}

