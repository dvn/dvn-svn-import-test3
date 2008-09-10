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
 * StudyPermissionsPage.java
 *
 * Created on October 11, 2006, 2:03 PM
 *
 */
package edu.harvard.hmdc.vdcnet.web.study;

import edu.harvard.hmdc.vdcnet.admin.GroupServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceBean;
import edu.harvard.hmdc.vdcnet.admin.RoleServiceBean;
import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCRole;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.study.EditStudyPermissionsService;
import edu.harvard.hmdc.vdcnet.study.PermissionBean;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import com.icesoft.faces.component.ext.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class StudyPermissionsPage extends VDCBaseBean  implements java.io.Serializable {
    @EJB
    private EditStudyPermissionsService editStudyPermissions;
    @EJB
    private UserServiceLocal userService;
    @EJB GroupServiceLocal groupService;
    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public StudyPermissionsPage() {
    }
    
    
    public void init() {
        super.init();
        if ( isFromPage("StudyPermissionsPage") ) {
            setEditStudyPermissions((EditStudyPermissionsService) sessionGet(getEditStudyPermissions().getClass().getName()));
            System.out.println("Getting stateful session bean editStudyPermissions ="+getEditStudyPermissions());
            
            
        } else {
            editStudyPermissions.setStudy(getStudyId());
            System.out.println("Putting stateful session bean in request, editUserGroupService ="+getEditStudyPermissions());
            sessionPut(getEditStudyPermissions().getClass().getName(), getEditStudyPermissions());
            
            
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
     * Holds value of property users.
     */
    private Collection<String> users = new ArrayList();
    
    /**
     * Getter for property users.
     * @return Value of property users.
     */
    public Collection<String> getUsers() {
        users.clear();
        users.add("User1");
        users.add("User2");
        users.add("User3");
        return this.users;
    }
    
    /**
     * Setter for property users.
     * @param users New value of property users.
     */
    public void setUsers(Collection<String> users) {
        this.users = users;
    }
    
    public EditStudyPermissionsService getEditStudyPermissions() {
        return editStudyPermissions;
    }
    
    public void setEditStudyPermissions(EditStudyPermissionsService editStudyPermissions) {
        this.editStudyPermissions = editStudyPermissions;
    }
    
    /**
     * Holds value of property newStudyUser.
     */
    private String newStudyUser;
    
    /**
     * Getter for property newStudyUser.
     * @return Value of property newStudyUser.
     */
    public String getNewStudyUser() {
        return this.newStudyUser;
    }
    
    /**
     * Setter for property newStudyUser.
     * @param newStudyUser New value of property newStudyUser.
     */
    public void setNewStudyUser(String newStudyUser) {
        this.newStudyUser = newStudyUser;
    }
    
    /**
     * Holds value of property newFileUser.
     */
    private String newFileUser;
    
    /**
     * Getter for property newFileUser.
     * @return Value of property newFileUser.
     */
    public String getNewFileUser() {
        return this.newFileUser;
    }
    
    /**
     * Setter for property newFileUser.
     * @param newFileUser New value of property newFileUser.
     */
    public void setNewFileUser(String newFileUser) {
        this.newFileUser = newFileUser;
    }
    
    public void addStudyPermission(ActionEvent ae) {
        
        VDCUser user = userService.findByUserName(newStudyUser);
        UserGroup group = null;
        if (user==null) {
            group = groupService.findByName(newStudyUser);
        }
        if (user==null && group==null) {
            String msg = "Invalid user or group name.";
            FacesMessage message = new FacesMessage(msg);
            FacesContext.getCurrentInstance().addMessage(studyUserInputText.getClientId(FacesContext.getCurrentInstance()), message);
        } else {
            if (user!=null ) {
                if (validateStudyUserName(FacesContext.getCurrentInstance(),studyUserInputText, newStudyUser)) {
                    this.editStudyPermissions.addStudyUser(user.getId());
                    newStudyUser="";
                }
            } else {
                if (validateStudyGroupName(FacesContext.getCurrentInstance(),studyUserInputText, newStudyUser)) {
                    this.editStudyPermissions.addStudyGroup(group.getId());
                    newStudyUser="";
                }
            }
        }
    }
    
    
    
    
    public void addFilePermission(ActionEvent ae) {
        if (!StringUtil.isEmpty(newFileUser)) {
            VDCUser user = userService.findByUserName(newFileUser);
            UserGroup group = null;
            if (user==null) {
                group = groupService.findByName(newFileUser);
            }
            if (user==null && group==null) {
                String msg = "Invalid user or group name.";
                FacesMessage message = new FacesMessage(msg);
                FacesContext.getCurrentInstance().addMessage(fileUserInputText.getClientId(FacesContext.getCurrentInstance()), message);
            } else {
                if (user!=null ) {
                    if (validateFileUserName(FacesContext.getCurrentInstance(),fileUserInputText, newFileUser)) {
                        this.editStudyPermissions.addFileUser(user.getId());
                        newFileUser="";
                    }
                } else {
                     this.editStudyPermissions.addFileGroup(group.getId());
                    newFileUser="";
                }
            }
        }
        if (!StringUtil.isEmpty(this.selectFilePermission)) {
            editStudyPermissions.setFileRestriction(selectFilePermission.equals("Restricted"));
        }
        
    }
    
    public void removeStudyUserGroup(ActionEvent ae) {
        editStudyPermissions.removeStudyPermissions();
    }
    public void removeFilePermissions(ActionEvent ae) {
        editStudyPermissions.removeFilePermissions();
    }
    
    public void  updateRequests(ActionEvent ae) {
        HttpServletRequest request = (HttpServletRequest)this.getExternalContext().getRequest();
        String hostName=request.getLocalName();
        int port = request.getLocalPort();
        String portStr="";
        if (port!=80) {
            portStr=":"+port;
        }
        String studyUrl = "http://"+hostName+portStr+request.getContextPath()+"/dv/"+getVDCRequestBean().getCurrentVDC().getAlias()+"/faces/study/StudyPage.xhtml?studyId="+studyId+"&tab=files";
        
        
        editStudyPermissions.updateRequests(studyUrl);
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
    
    public String save() {
        this.getVDCRequestBean().setStudyId(editStudyPermissions.getStudy().getId());
        this.editStudyPermissions.save();
        return "viewStudy";
    }
    
    
    public boolean validateStudyUserName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String userNameStr = (String) value;
        String msg=null;
        boolean valid=true;
        
        VDCUser user = userService.findByUserName(userNameStr);
        if (user==null) {
            valid=false;
            msg = "User not found.";
        }
        if (valid) {
            for (Iterator it = this.editStudyPermissions.getStudyPermissions().iterator(); it.hasNext();) {
                PermissionBean pb = (PermissionBean)it.next();
                if (pb.getUser()!=null && pb.getUser().getId().equals(user.getId())) {
                    valid=false;
                    msg = "User already in study permissions list.";
                    break;
                }
                
            }
        }
        
        if (valid) {
            if ( !editStudyPermissions.getStudy().isUserRestricted(getVDCRequestBean().getCurrentVDC(),user)) {
                valid=false;
                msg= "This user already has a Network or Dataverse Role that allows access to the study.";
            }
        }
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage(msg);
            context.addMessage(toValidate.getClientId(context), message);
            
            
        }
        return valid;
        
    }
    
    
    public boolean validateStudyGroupName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String groupNameStr = (String) value;
        String msg=null;
        boolean valid=true;
        
        UserGroup group = this.groupService.findByName(groupNameStr);
        if (group==null) {
            valid=false;
            msg = "Group not found.";
        }
        if (valid) {
            for (Iterator it = this.editStudyPermissions.getStudyPermissions().iterator(); it.hasNext();) {
                PermissionBean pb = (PermissionBean)it.next();
                if (pb.getGroup()!=null && pb.getGroup().getId().equals(group.getId())) {
                    valid=false;
                    msg = "Group already in study permissions list.";
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
    
    public boolean validateFileUserName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String userNameStr = (String) value;
        String msg=null;
        boolean valid=true;
        
        VDCUser user = userService.findByUserName(userNameStr);
        if (user==null) {
            valid=false;
            msg = "User not found.";
        }
       
        if (valid) {
            if (user.getNetworkRole()!=null && user.getNetworkRole().getName().equals(NetworkRoleServiceBean.ADMIN)) {
                valid=false;
                msg= "User is a Network Administrator and already has all privileges to this dataverse.";
            }
        }
       if (valid) {
            VDCRole vdcRole = user.getVDCRole(this.getVDCRequestBean().getCurrentVDC());
            if ((vdcRole!=null && (vdcRole.getRole().getName().equals(RoleServiceBean.ADMIN) || vdcRole.getRole().getName().equals(RoleServiceBean.CURATOR)))
                 || editStudyPermissions.getStudy().getCreator().getId().equals(user.getId())) {
                valid=false;
                msg= "User already has a Network or Dataverse Role that allows access to the restricted files.";
            }
        }        
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage(msg);
            context.addMessage(toValidate.getClientId(context), message);
            
            
        }
        return valid;
        
    }
    
    
    public boolean validateFileGroupName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String groupNameStr = (String) value;
        String msg=null;
        boolean valid=true;
        
        UserGroup group = this.groupService.findByName(groupNameStr);
        if (group==null) {
            valid=false;
            msg = "Group not found.";
        }
    
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage(msg);
            context.addMessage(toValidate.getClientId(context), message);
        }
        return valid;
        
    }
    
    /**
     * Holds value of property studyUserInputText.
     */
    private HtmlInputText studyUserInputText;
    
    /**
     * Getter for property studyUserInputText.
     * @return Value of property studyUserInputText.
     */
    public HtmlInputText getStudyUserInputText() {
        return this.studyUserInputText;
    }
    
    /**
     * Setter for property studyUserInputText.
     * @param studyUserInputText New value of property studyUserInputText.
     */
    public void setStudyUserInputText(HtmlInputText studyUserInputText) {
        this.studyUserInputText = studyUserInputText;
    }
    
    /**
     * Holds value of property fileUserInputText.
     */
    private HtmlInputText fileUserInputText;
    
    /**
     * Getter for property fileUserInputText.
     * @return Value of property fileUserInputText.
     */
    public HtmlInputText getFileUserInputText() {
        return this.fileUserInputText;
    }
    
    /**
     * Setter for property fileUserInputText.
     * @param fileUserInputText New value of property fileUserInputText.
     */
    public void setFileUserInputText(HtmlInputText fileUserInputText) {
        this.fileUserInputText = fileUserInputText;
    }
    
    /**
     * Holds value of property selectFilePermission.
     */
    private String selectFilePermission;
    
    /**
     * Getter for property selectFilePermission.
     * @return Value of property selectFilePermission.
     */
    public String getSelectFilePermission() {
        return this.selectFilePermission;
    }
    
    /**
     * Setter for property selectFilePermission.
     * @param selectFilePermission New value of property selectFilePermission.
     */
    public void setSelectFilePermission(String selectFilePermission) {
        this.selectFilePermission = selectFilePermission;
    }
    
    
      public String cancel() {
        String forwardPage="viewStudy";
       
        editStudyPermissions.cancel();
        this.sessionRemove(editStudyPermissions.getClass().getName());
        getVDCRequestBean().setStudyId(studyId);
        getVDCSessionBean().setStudyService(null);
        
        return  forwardPage;
    }
}

