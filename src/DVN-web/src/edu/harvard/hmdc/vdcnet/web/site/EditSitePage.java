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
 * EditSitePage.java
 *
 * Created on September 19, 2006, 9:57 AM
 */
package edu.harvard.hmdc.vdcnet.web.site;

import com.icesoft.faces.component.ext.HtmlCommandButton;
import edu.harvard.hmdc.vdcnet.admin.RoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.study.StudyFieldServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollectionServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.StatusMessage;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.util.StringTokenizer;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlInputTextarea;
import com.icesoft.faces.component.ext.HtmlOutputLabel;
import com.icesoft.faces.component.ext.HtmlOutputText;
import edu.harvard.hmdc.vdcnet.util.CharacterValidator;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroupServiceLocal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class EditSitePage extends VDCBaseBean implements java.io.Serializable  {
    @EJB VDCServiceLocal           vdcService;
    @EJB VDCCollectionServiceLocal vdcCollectionService;
    @EJB VDCGroupServiceLocal      vdcGroupService;
    @EJB VDCNetworkServiceLocal    vdcNetworkService;
    @EJB StudyFieldServiceLocal    studyFieldService;
    @EJB UserServiceLocal          userService;
    @EJB RoleServiceLocal          roleService;

    private StatusMessage   msg;

    private String          affiliation;
    private HtmlInputText   dataverseAlias;
    private HtmlInputText   dataverseName;
    private HtmlInputTextarea shortDescriptionInput = new HtmlInputTextarea();
    private HtmlOutputText  shortDescriptionLabelText;
    private HtmlOutputLabel shortDescriptionLabel;
    private String          dataverseType = null;
    private String          firstName = new String("");
    private String          lastName;
    private String          shortDescription;


    private List<SelectItem> dataverseOptions = null;


    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init() {
        super.init();
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        //DEBUG
        //check to see if a dataverse type is in request

        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Iterator iterator = request.getParameterMap().keySet().iterator();
        while (iterator.hasNext()) {
            Object key = (Object) iterator.next();
            if ( key instanceof String && ((String) key).indexOf("dataverseType") != -1 && !request.getParameter((String)key).equals("")) {
                this.setDataverseType(request.getParameter((String)key));
            }
        }
        if (this.dataverseType == null && getVDCRequestBean().getCurrentVDC().getDtype() != null) {
            this.setDataverseType(getVDCRequestBean().getCurrentVDC().getDtype());
        }
        //what kind of vdc is this, basic or scholar
        try {
            if ( (this.dataverseType == null || this.dataverseType.equals("Scholar")) ) {
                //set the default values for the fields
                VDC scholardataverse = (VDC)vdcService.findScholarDataverseByAlias(thisVDC.getAlias());
                setDataverseType("Scholar");
                setFirstName(scholardataverse.getFirstName());
                setLastName(scholardataverse.getLastName());
                setAffiliation(scholardataverse.getAffiliation());
                HtmlInputText nameText = new HtmlInputText();
                nameText.setValue(scholardataverse.getName());
                setDataverseName(nameText);
                HtmlInputText aliasText = new HtmlInputText();
                aliasText.setValue(scholardataverse.getAlias());
                setShortDescription(scholardataverse.getDvnDescription());
            } else if (!this.dataverseType.equals("Scholar")) {
                setDataverseType("Basic");
                HtmlInputText nameText = new HtmlInputText();
                nameText.setValue(thisVDC.getName());
                setDataverseName(nameText);
                if (thisVDC.getAffiliation() != null)
                    this.setAffiliation(thisVDC.getAffiliation());
                else
                    this.setAffiliation(new String(""));
                HtmlInputText aliasText = new HtmlInputText();
                aliasText.setValue(thisVDC.getAlias());
                setShortDescription(thisVDC.getDvnDescription());
            }
        } catch (Exception nfe) {
            System.out.println("An error occurred " + nfe.toString());
        }

    }

    private HtmlOutputLabel componentLabel1 = new HtmlOutputLabel();

    public HtmlOutputLabel getComponentLabel1() {
        return componentLabel1;
    }

    public void setComponentLabel1(HtmlOutputLabel hol) {
        this.componentLabel1 = hol;
    }

    private HtmlOutputText componentLabel1Text = new HtmlOutputText();

    public HtmlOutputText getComponentLabel1Text() {
        return componentLabel1Text;
    }

    public void setComponentLabel1Text(HtmlOutputText hot) {
        this.componentLabel1Text = hot;
    }

    public HtmlInputText getDataverseName() {
        return dataverseName;
    }

    public void setDataverseName(HtmlInputText hit) {
        this.dataverseName = hit;
    }

    private HtmlOutputLabel componentLabel2 = new HtmlOutputLabel();

    public HtmlOutputLabel getComponentLabel2() {
        return componentLabel2;
    }

    public void setComponentLabel2(HtmlOutputLabel hol) {
        this.componentLabel2 = hol;
    }

    private HtmlOutputText componentLabel2Text = new HtmlOutputText();

    public HtmlOutputText getComponentLabel2Text() {
        return componentLabel2Text;
    }

    public void setComponentLabel2Text(HtmlOutputText hot) {
        this.componentLabel2Text = hot;
    }

    public HtmlInputText getDataverseAlias() {
        return dataverseAlias;
    }

    public void setDataverseAlias(HtmlInputText hit) {
        this.dataverseAlias = hit;
    }

    private HtmlCommandButton button1 = new HtmlCommandButton();

    public HtmlCommandButton getButton1() {
        return button1;
    }

    public void setButton1(HtmlCommandButton hcb) {
        this.button1 = hcb;
    }

    private HtmlCommandButton button2 = new HtmlCommandButton();

    public HtmlCommandButton getButton2() {
        return button2;
    }

    public void setButton2(HtmlCommandButton hcb) {
        this.button2 = hcb;
    }

    // </editor-fold>
    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public EditSitePage() {
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
    
    public String edit(){
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        String dataversetype = dataverseType;
        thisVDC.setDtype(dataversetype);
        thisVDC.setName((String)dataverseName.getValue());
        thisVDC.setAlias((String)dataverseAlias.getValue());
        thisVDC.setAffiliation(this.getAffiliation());
        thisVDC.setDvnDescription(shortDescription);
        if (dataverseType.equals("Scholar")) {
            thisVDC.setFirstName(this.firstName);
            thisVDC.setLastName(this.lastName);
            if (thisVDC.getVdcGroups().size() != 0) {
                //remove the group relationships.
                Iterator iterator = thisVDC.getVdcGroups().iterator();
                while (iterator.hasNext()) {
                    VDCGroup vdcgroup = (VDCGroup)iterator.next();
                    iterator.remove();
                    vdcGroupService.updateVdcGroup(vdcgroup);
                }
                
            }
        } else {
            thisVDC.setFirstName(null);
            thisVDC.setLastName(null);
        }

        vdcService.edit(thisVDC);
        getVDCRequestBean().setCurrentVDC(thisVDC);
        msg = new StatusMessage();
        msg.setMessageText("Update Successful!");
        msg.setStyleClass("successMessage");
        return "editSite";
    }
    
    public String editScholarDataverse(){
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        VDC scholardataverse = (VDC)thisVDC;
        String dataversetype = dataverseType;
        scholardataverse.setDtype(dataversetype);
        scholardataverse.setName((String)dataverseName.getValue());
        scholardataverse.setAlias((String)dataverseAlias.getValue());
        scholardataverse.setFirstName(this.firstName);
        scholardataverse.setLastName(this.lastName);
        scholardataverse.setAffiliation(this.affiliation);
        vdcService.edit(scholardataverse);
        getVDCRequestBean().setCurrentVDC(scholardataverse);
        msg = new StatusMessage();
        msg.setMessageText("Update Successful!");
        msg.setStyleClass("successMessage");
        return "editSite";
    }
    
    public String cancel(){
        return "myOptions";
    }
    
    public void validateName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String name = (String) value;
        if (name != null && name.trim().length() == 0) {
            FacesMessage message = new FacesMessage("The dataverse name field must have a value.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        if (!name.equals(thisVDC.getName())){
            boolean nameFound = false;
            VDC vdc = vdcService.findByName(name);
            if (vdc != null) {
                nameFound=true;
            }
            
            if (nameFound) {
                ((UIInput)toValidate).setValid(false);
                
                FacesMessage message = new FacesMessage("This name is already taken.");
                context.addMessage(toValidate.getClientId(context), message);
            }
        }
    }

    public void validateAlias(FacesContext context,
            UIComponent toValidate,
            Object value) {
        CharacterValidator charactervalidator = new CharacterValidator();
        charactervalidator.validate(context, toValidate, value);
        String alias = (String) value;
        StringTokenizer strTok = new StringTokenizer(alias);
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
            if (!alias.equals(thisVDC.getAlias())){
                boolean aliasFound = false;
                VDC vdc = vdcService.findByAlias(alias);
                if (vdc != null) {
                    aliasFound=true;
                }
                
                if (aliasFound) {
                    ((UIInput)toValidate).setValid(false);
                    
                    FacesMessage message = new FacesMessage("This alias is already taken.");
                    context.addMessage(toValidate.getClientId(context), message);
                }
            }
    }


    // ***************** GETTERS ********************
    public StatusMessage getMsg(){
        return msg;
    }

    /**
     * Returns the type of dataverse, basic or scholar in this case.
     *
     * @return
     */
    public String getDataverseType() {
        return dataverseType;
    }

    /**
     * set the possible options
     * please note, this was for 16a,
     * but is not used because of
     * issues with type casting - inheritance issues.
     * Keeping it pending a solution ...
     *
     * @author wbossons
     */
    public List<SelectItem> getDataverseOptions() {
        if (this.dataverseOptions == null) {
            dataverseOptions = new ArrayList();
            dataverseOptions.add(new SelectItem(new String("Scholar")));
            dataverseOptions.add(new SelectItem(new String("Basic")));
        }
        return dataverseOptions;
    }

    /**
     * Getter for property firstName.
     * @return Value of property firstName.
     */
    public String getFirstName() {
        return this.firstName;
    }

        /**
     * Getter for property lastName.
     * @return Value of property lastName.
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Getter for property affiliation.
     * @return Value of property affiliation.
     */
    public String getAffiliation() {
        if (affiliation == null)
            this.setAffiliation(getVDCRequestBean().getCurrentVDC().getAffiliation());
        return this.affiliation;
    }

    /**
     *
     *
     * @return Returns a string with the
     * short description of the dataverse.
     * It is viewable on the network page.
     *
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * Gets the HtmlInputTextarea that the short
     * description is bound to.
     *
     * @return HtmlInputTextarea
     */
    public HtmlInputTextarea getShortDescriptionInput() {
        return shortDescriptionInput;
    }

    /**
     * Gets the HtmlOutputLabel that the short
     * description label is bound to.
     *
     * @return HtmlOutputLabel
     */
    public HtmlOutputLabel getShortDescriptionLabel() {
        return shortDescriptionLabel;
    }

    /**
     * Gets the HtmlOutputText binding for the short
     * description label text.
     *
     * @return HtmlOutputText
     */
    public HtmlOutputText getShortDescriptionLabelText() {
        return shortDescriptionLabelText;
    }



    // ***************** SETTERS ********************
    public void setMsg(StatusMessage msg){
        this.msg = msg;
    }
    public void setDataverseType(String dataverseType) {
        this.dataverseType = dataverseType;
    }

    /**
     * Setter for property firstName.
     * @param firstName New value of property firstName.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Setter for property lastName.
     * @param lastName New value of property lastName.
     */
    public void setLastName(String lastname) {
        this.lastName = lastname;
    }

    /**
     * Setter for property affiliation.
     * @param affiliation New value of property affiliation.
     */
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    /**
     * Returns the value of the short description.
     *
     * @param shortDescription
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * Sets the HtmlInputTextarea that the short
     * description is bound to.
     *
     * @return void
     */
    public void setShortDescriptionInput(HtmlInputTextarea shortDescriptionInput) {
        this.shortDescriptionInput = shortDescriptionInput;
    }

    /**
     * Sets the HtmlOutputLabel binding for the short
     * description label.
     *
     * @return void
     */
    public void setShortDescriptionLabel(HtmlOutputLabel shortDescriptionLabel) {
        this.shortDescriptionLabel = shortDescriptionLabel;
    }

    /**
     * Sets the HtmlOutputText binding for the short
     * description label text.
     *
     * @return void
     */
    public void setShortDescriptionLabelText(HtmlOutputText shortDescriptionLabelText) {
        this.shortDescriptionLabelText = shortDescriptionLabelText;
    }




    
    /**
     * Captures value change event for the dataverse affiliation.
     *
     */
    public void changeAffiliation(ValueChangeEvent event) {
        String newValue = (String) event.getNewValue();
        this.setAffiliation(newValue);        
    }

    /**
     * Captures the selected option.
     *
     */
    public void changeDataverseOption(ValueChangeEvent event) {
        String newValue = (String) event.getNewValue();
        this.setDataverseType(newValue);  
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        request.setAttribute("dataverseType", newValue);
        //FacesContext.getCurrentInstance().renderResponse();
    }

    /**
     * Captures changes to the first name.
     *
     */
    public void changeFirstName(ValueChangeEvent event) {
        String newValue = (String) event.getNewValue();
        this.setFirstName(newValue);        
    }

    /**
     * Captures changes to the last name.
     *
     */
    public void changeLastName(ValueChangeEvent event) {
        String newValue = (String) event.getNewValue();
        this.setLastName(newValue);        
    }

    /**
     * Validation so that a required field is not left empty
     *
     * @param context
     * @param toValidate
     * @param value
     */
    public void validateIsEmpty(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String newValue = (String)value;
         if (newValue == null || newValue.trim().length() == 0)  {
            FacesMessage message = new FacesMessage("The field must have a value.");
            context.addMessage(toValidate.getClientId(context), message);
        }
    }

    public void validateShortDescription(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String newValue = (String)value;
        if (newValue != null && newValue.trim().length() > 0) {
            if (newValue.length() > 255) {
                FacesMessage message = new FacesMessage("The field cannot be more than 255 characters in length.");
                context.addMessage(toValidate.getClientId(context), message);
            }
        }
    }
}