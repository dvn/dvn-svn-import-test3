/*
 * EditHarvestSitePage.java
 *
 * Created on April 2, 2007, 3:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.site;


import edu.harvard.hmdc.vdcnet.admin.EditHarvestSiteService;
import edu.harvard.hmdc.vdcnet.admin.GroupServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.harvest.HarvesterServiceLocal;
import edu.harvard.hmdc.vdcnet.harvest.SetDetailBean;
import edu.harvard.hmdc.vdcnet.util.CharacterValidator;
import edu.harvard.hmdc.vdcnet.util.SessionCounter;
import edu.harvard.hmdc.vdcnet.vdc.HandlePrefix;
import edu.harvard.hmdc.vdcnet.vdc.HandlePrefixServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverse;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Ellen Kraffmiller
 */
@EJB(name="editHarvestSite", beanInterface=edu.harvard.hmdc.vdcnet.admin.EditHarvestSiteService.class)
public class EditHarvestSitePage extends VDCBaseBean {
    @EJB UserServiceLocal userService;
    @EJB GroupServiceLocal groupService;
    @EJB VDCServiceLocal vdcService;
    @EJB HarvesterServiceLocal harvesterService;
    @EJB HandlePrefixServiceLocal handlePrefixService;
    
    
    private HarvestingDataverse harvestingDataverse;
    private EditHarvestSiteService editHarvestSiteService;
    public void init() {
        super.init();
        String harvestIdParam = getParamFromRequestOrComponent("harvestId");
        if (harvestIdParam!=null) {
            this.harvestId =new Long(Long.parseLong(harvestIdParam));
        }
        if ( isFromPage("EditHarvestSitePage")) {
            if ( sessionGet(EditHarvestSiteService.class.getName()+harvestId)!=null) {
                editHarvestSiteService = (EditHarvestSiteService) sessionGet(EditHarvestSiteService.class.getName()+harvestId);
                harvestingDataverse= editHarvestSiteService.getHarvestingDataverse();
                
            } else {
                FacesContext context = FacesContext.getCurrentInstance();
                FacesMessage errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "The form you are trying to save contains stale data. Please re-load the form and try again.","");
                context.addMessage(null,errMessage);
            }
        } else {
            // we need to create the editHarvestSiteService bean
            try {
                Context ctx = new InitialContext();
                editHarvestSiteService = (EditHarvestSiteService) ctx.lookup("java:comp/env/editHarvestSite");
            } catch(NamingException e) {
                e.printStackTrace();
                FacesContext context = FacesContext.getCurrentInstance();
                FacesMessage errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),null);
                context.addMessage(null,errMessage);
                
            }
            if (this.harvestId != null) {
                editHarvestSiteService.setHarvestingDataverse(this.harvestId);
                sessionPut( EditHarvestSiteService.class.getName()+this.harvestId, editHarvestSiteService);
                harvestingDataverse = editHarvestSiteService.getHarvestingDataverse();
                if (harvestingDataverse.getVdc()!=null) {
                    dataverseName = harvestingDataverse.getVdc().getName();
                    dataverseAlias = harvestingDataverse.getVdc().getAlias();
                }
                try {
                    assignHarvestingSets(editHarvestSiteService.getHarvestingDataverse().getOaiServer());        
                    assignMetadataFormats(editHarvestSiteService.getHarvestingDataverse().getOaiServer());       
                } catch(Exception e) {
                    e.printStackTrace();
                    FacesContext context = FacesContext.getCurrentInstance();
                    FacesMessage errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),null);
                    context.addMessage(null,errMessage);
                }
                
            } else {
                editHarvestSiteService.newHarvestingDataverse();
                harvestingDataverse = editHarvestSiteService.getHarvestingDataverse();
                this.harvestId = SessionCounter.getNext();
                sessionPut( EditHarvestSiteService.class.getName()+this.harvestId, editHarvestSiteService);
              
                
            }
            
        }
    }
    /**
     * Creates a new instance of EditHarvestSitePage
     */
    public EditHarvestSitePage() {
    }
    
    public void validateAction(ActionEvent ae) {
        // do nothing, everthing should be done in validateOAIServer
        System.out.println("in validateAction");
    }
    
    public boolean validateOAIServer(FacesContext context,
            UIComponent toValidate,Object value) {
     
        boolean valid=true;            

       valid= assignHarvestingSets(((String)value).trim());
       if (valid) {
            assignMetadataFormats(((String)value).trim());
       }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage( "Invalid OAI Server Url");
            context.addMessage(toValidate.getClientId(context),message);
        }
        return valid;
        
    }
    
    public void getOAISets(ActionEvent ea) {
        
    }
    /**
     * Holds value of property harvestId.
     */
    private Long harvestId;
    
    /**
     * Getter for property harvestId.
     *
     * @return Value of property harvestId.
     */
    public Long getHarvestId() {
        return this.harvestId;
    }
    
    /**
     * Setter for property harvestId.
     *
     * @param harvestId New value of property harvestId.
     */
    public void setHarvestId(Long harvestId) {
        this.harvestId = harvestId;
    }
    
    public HarvestingDataverse getHarvestingDataverse() {
        return harvestingDataverse;
    }
    
    public void setHarvestingDataverse(HarvestingDataverse harvestingDataverse) {
        this.harvestingDataverse = harvestingDataverse;
    }
    
    public EditHarvestSiteService getEditHarvestSiteService() {
        return editHarvestSiteService;
    }
    
    public void setEditHarvestSiteService(EditHarvestSiteService editHarvestSiteService) {
        this.editHarvestSiteService = editHarvestSiteService;
    }
    
    
    public String save() {
        Long userId = getVDCSessionBean().getLoginBean().getUser().getId();
        editHarvestSiteService.save(dataverseName,dataverseAlias,userId);
        success=true;
        return "success";
        
    }
    
    public String cancel() {
        editHarvestSiteService.cancel();
        return "myNetworkOptions";
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
     * Holds value of property dataverseName.
     */
    private String dataverseName;
    
    /**
     * Getter for property dataverseName.
     * @return Value of property dataverseName.
     */
    public String getDataverseName() {
        return this.dataverseName;
    }
    
    /**
     * Setter for property dataverseName.
     * @param dataverseName New value of property dataverseName.
     */
    public void setDataverseName(String dataverseName) {
        this.dataverseName = dataverseName;
    }
    
    /**
     * Holds value of property dataverseAlias.
     */
    private String dataverseAlias;
    
    /**
     * Getter for property dataverseAlias.
     * @return Value of property dataverseAlias.
     */
    public String getDataverseAlias() {
        return this.dataverseAlias;
    }
    
    /**
     * Setter for property dataverseAlias.
     * @param dataverseAlias New value of property dataverseAlias.
     */
    public void setDataverseAlias(String dataverseAlias) {
        this.dataverseAlias = dataverseAlias;
    }
    
    /**
     * Holds value of property groupTable.
     */
    private HtmlDataTable groupTable;
    
    /**
     * Getter for property groupTable.
     * @return Value of property groupTable.
     */
    public HtmlDataTable getGroupTable() {
        return this.groupTable;
    }
    
    /**
     * Setter for property groupTable.
     * @param groupTable New value of property groupTable.
     */
    public void setGroupTable(HtmlDataTable groupTable) {
        this.groupTable = groupTable;
    }
    
    /**
     * Holds value of property userTable.
     */
    private HtmlDataTable userTable;
    
    /**
     * Getter for property userTable.
     * @return Value of property userTable.
     */
    public HtmlDataTable getUserTable() {
        return this.userTable;
    }
    
    /**
     * Setter for property userTable.
     * @param userTable New value of property userTable.
     */
    public void setUserTable(HtmlDataTable userTable) {
        this.userTable = userTable;
    }
    
    /**
     * Holds value of property addUserName.
     */
    private String addUserName;
    
    /**
     * Getter for property addUserName.
     * @return Value of property addUserName.
     */
    public String getAddUserName() {
        return this.addUserName;
    }
    
    /**
     * Setter for property addUserName.
     * @param addUserName New value of property addUserName.
     */
    public void setAddUserName(String addUserName) {
        this.addUserName = addUserName;
    }
    
    /**
     * Holds value of property addGroupName.
     */
    private String addGroupName;
    
    /**
     * Getter for property addGroupName.
     * @return Value of property addGroupName.
     */
    public String getAddGroupName() {
        return this.addGroupName;
    }
    
    /**
     * Setter for property addGroupName.
     * @param addGroupName New value of property addGroupName.
     */
    public void setAddGroupName(String addGroupName) {
        this.addGroupName = addGroupName;
    }
    
    public void removeGroup(ActionEvent ae) {
        this.editHarvestSiteService.removeAllowedFileGroup(((UserGroup)groupTable.getRowData()).getId());
        
    }
    
    public void removeUser(ActionEvent ae) {
        this.editHarvestSiteService.removeAllowedFileUser(((VDCUser)userTable.getRowData()).getId());
        
    }
    
    public void addUser(ActionEvent ae) {
        
        if (validateUserName(FacesContext.getCurrentInstance(),userInputText, addUserName)) {
            VDCUser   user = userService.findByUserName(addUserName);
            this.editHarvestSiteService.addAllowedFileUser(user.getId());
            
            addUserName="";
        }
        
    }
    
    
    public void addGroup(ActionEvent ae) {
        if (validateGroupName(FacesContext.getCurrentInstance(), groupInputText, addGroupName)) {
            UserGroup group = groupService.findByName(addGroupName);
            this.editHarvestSiteService.addAllowedFileGroup(group.getId());
            addGroupName="";
        }
        
    }
    public boolean validateUserName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String userNameStr = (String) value;
        String msg=null;
        boolean valid=true;
        VDCUser user = null;
        
        user = userService.findByUserName(userNameStr);
        if (user==null) {
            valid=false;
            msg = "User not found.";
        }
        if (valid) {
            for (Iterator it = harvestingDataverse.getAllowedFileUsers().iterator(); it.hasNext();) {
                VDCUser elem = (VDCUser) it.next();
                if (elem.getId().equals(user.getId())) {
                    valid=false;
                    msg = "User already in allowed users list.";
                    break;
                }
                
            }
        }
        
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage(msg);
            context.addMessage(toValidate.getClientId(context), message);
            
            
        }
        return valid;
        
    }
    
    
    public boolean validateGroupName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String groupNameStr = (String) value;
        String msg=null;
        boolean valid=true;
        UserGroup group = null;
        
        group = this.groupService.findByName(groupNameStr);
        if (group==null) {
            valid=false;
            msg = "Group not found.";
        }
        
        if (valid) {
            for (Iterator it = harvestingDataverse.getAllowedFileGroups().iterator(); it.hasNext();) {
                UserGroup elem = (UserGroup) it.next();
                if (elem.getId().equals(group.getId())) {
                    valid=false;
                    msg = "Group already in allowed groups list.";
                    break;
                }
                
            }
        }
        
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage(msg);
            context.addMessage(toValidate.getClientId(context), message);
        }
        return valid;
        
    }
    
    /**
     * Holds value of property userInputText.
     */
    private HtmlInputText userInputText;
    
    /**
     * Getter for property userInputText.
     * @return Value of property userInputText.
     */
    public HtmlInputText getUserInputText() {
        return this.userInputText;
    }
    
    /**
     * Setter for property userInputText.
     * @param userInputText New value of property userInputText.
     */
    public void setUserInputText(HtmlInputText userInputText) {
        this.userInputText = userInputText;
    }
    
    /**
     * Holds value of property groupInputText.
     */
    private HtmlInputText groupInputText;
    
    /**
     * Getter for property groupInputText.
     * @return Value of property groupInputText.
     */
    public HtmlInputText getGroupInputText() {
        return this.groupInputText;
    }
    
    /**
     * Setter for property groupInputText.
     * @param groupInputText New value of property groupInputText.
     */
    public void setGroupInputText(HtmlInputText groupInputText) {
        this.groupInputText = groupInputText;
    }
    
   
    
    private void assignMetadataFormats(String oaiUrl) {
        if (oaiUrl!=null) {          
            editHarvestSiteService.setMetadataFormats(harvesterService.getMetadataFormats(oaiUrl));
        } else {
            editHarvestSiteService.setMetadataFormats(null);
        }
        
    }
    
    private boolean assignHarvestingSets(String oaiUrl)   {
        
        boolean valid=true;
        if (oaiUrl!=null) {
            try {
            editHarvestSiteService.setHarvestingSets(harvesterService.getSets(oaiUrl));
            } catch (EJBException e) {
                valid=false;
            }
        } else {
            editHarvestSiteService.setHarvestingSets(null);
        }
        return valid;
    }
    
    
    /**
     * Getter for property harvestingSetsSelect.
     * @return Value of property harvestingSetsSelect.
     */
    public List<SelectItem> getHarvestingSetsSelect() {
        List<SelectItem> harvestingSetsSelect = new ArrayList<SelectItem>();
        if (this.editHarvestSiteService.getHarvestingSets()!=null) {
            for (Iterator it = this.editHarvestSiteService.getHarvestingSets().iterator(); it.hasNext();) {
                SetDetailBean elem = (SetDetailBean) it.next();
                harvestingSetsSelect.add(new SelectItem(elem.getSpec(),elem.getName()));
              
            }
        }
        return harvestingSetsSelect;
    }
    
    public List<SelectItem> getMetadataFormatsSelect() {
        List<SelectItem> metadataFormatsSelect = new ArrayList<SelectItem>();
        if (this.editHarvestSiteService.getMetadataFormats()!=null) {
            for (Iterator it = this.editHarvestSiteService.getMetadataFormats().iterator(); it.hasNext();) {
                String elem = (String) it.next();
                metadataFormatsSelect.add(new SelectItem(elem,elem));
            }
        }
        return metadataFormatsSelect;
    }
    
    
   public List<SelectItem> getHandlePrefixSelect() {
        List<SelectItem> handlePrefixSelect = new ArrayList<SelectItem>();
        List<HandlePrefix> prefixList = handlePrefixService.findAll();
        for (Iterator it = prefixList.iterator(); it.hasNext();) {
            HandlePrefix prefix = (HandlePrefix) it.next();
            handlePrefixSelect.add(new SelectItem(prefix.getId(),"Register harvested studies with prefix "+prefix.getPrefix()));
        }
        
        return handlePrefixSelect;
    }    
    
    public void validateAlias(FacesContext context,
            UIComponent toValidate,
            Object value) {
        CharacterValidator charactervalidator = new CharacterValidator();
        charactervalidator.validate(context, toValidate, value);
        String alias = (String) value;
        
        boolean aliasFound = false;
        VDC vdc = vdcService.findByAlias(alias);
        if (vdc != null && (harvestingDataverse.getVdc()== null || vdc.getId()!=harvestingDataverse.getVdc().getId())) {
            aliasFound=true;
        }
        
        if (aliasFound) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage("This alias is already taken.");
            context.addMessage(toValidate.getClientId(context), message);
        }
    }
  public void validateName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String name = (String) value;
        
        boolean nameFound = false;
        VDC vdc = vdcService.findByName(name);
        
        if (vdc != null && (harvestingDataverse.getVdc()==null || !harvestingDataverse.getVdc().equals(vdc))) {
            nameFound=true;
        }
        
        if (nameFound) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage("This name is already taken.");
            context.addMessage(toValidate.getClientId(context), message);
        }
    }

    /**
     * Holds value of property handlePrefixId.
     */
    private Long handlePrefixId;

    /**
     * Getter for property handlePrefixId.
     * @return Value of property handlePrefixId.
     */
    public Long getHandlePrefixId() {
        Long id=null;
        if (harvestingDataverse.getHandlePrefix()!=null) {
            id = harvestingDataverse.getHandlePrefix().getId();
        }
        return id;
    }

    /**
     * Setter for property handlePrefixId.
     * @param handlePrefixId New value of property handlePrefixId.
     */
    public void setHandlePrefixId(Long handlePrefixId) {
        this.handlePrefixId = handlePrefixId;
    }

    /**
     * Holds value of property handlePrefixSelectOneMenu.
     */
    private HtmlSelectOneMenu handlePrefixSelectOneMenu;

    /**
     * Getter for property handlePrefixSelectOneMenu.
     * @return Value of property handlePrefixSelectOneMenu.
     */
    public HtmlSelectOneMenu getHandlePrefixSelectOneMenu() {
        return this.handlePrefixSelectOneMenu;
    }

    /**
     * Setter for property handlePrefixSelectOneMenu.
     * @param handlePrefixSelectOneMenu New value of property handlePrefixSelectOneMenu.
     */
    public void setHandlePrefixSelectOneMenu(HtmlSelectOneMenu handlePrefixSelectOneMenu) {
        this.handlePrefixSelectOneMenu = handlePrefixSelectOneMenu;
    }


    public Boolean getSubsetRestrictedWrapper() {
        return harvestingDataverse.isSubsetRestricted();
    }

    public void setSubsetRestrictedWrapper(Boolean subsetRestrictedWrapper) {
        if (subsetRestrictedWrapper != null) {
            harvestingDataverse.setSubsetRestricted(subsetRestrictedWrapper);
        } else {
            harvestingDataverse.setSubsetRestricted(false);
        }
    }    
    
}
