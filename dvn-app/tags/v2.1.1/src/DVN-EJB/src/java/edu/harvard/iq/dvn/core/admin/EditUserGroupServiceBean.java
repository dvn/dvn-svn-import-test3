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
 * EditUserServiceBean.java
 *
 * Created on September 29, 2006, 1:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

  

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EditUserGroupServiceBean implements  EditUserGroupService {
    @PersistenceContext(type = PersistenceContextType.EXTENDED,unitName="VDCNet-ejbPU")
    private EntityManager em;
    @EJB private UserServiceLocal userService;
    private UserGroup userGroup;
    
    public void newUserGroup() {
        userGroup = new UserGroup();
        
        LoginDomain loginDomain =new LoginDomain();
        loginDomain.setUserGroup(userGroup);
        userGroup.setLoginDomains(new ArrayList<LoginDomain>());
        userGroup.getLoginDomains().add(loginDomain);
        
        //Wendy's add
        LoginAffiliate loginaffiliate = new LoginAffiliate();
        loginaffiliate.setUserGroup(userGroup);
        userGroup.setLoginAffiliates(new ArrayList<LoginAffiliate>());
        userGroup.getLoginAffiliates().add(loginaffiliate);
        
        
        userDetailBeans = new ArrayList();
        UserDetailBean udb = new UserDetailBean();
        udb.setUserName("");
        userDetailBeans.add(udb);
        
        
        em.persist(userGroup);
    }
    
    /**
     *  Initialize the bean with a Study for editing
     */
    public void setUserGroup(Long id ) {
        userGroup = em.find(UserGroup.class,id);
        if (userGroup==null) {
            throw new IllegalArgumentException("Unknown userGroup id: "+id);
        }
        userDetailBeans = new ArrayList();
        for (Iterator it = userGroup.getUsers().iterator(); it.hasNext();) {
            VDCUser elem = (VDCUser) it.next();
            UserDetailBean udb = new UserDetailBean();
            udb.setUserName(elem.getUserName());
            userDetailBeans.add(udb);
        }
        
        
    }
    
    public void newUser() {
        
        userGroup = new UserGroup();
        em.persist(userGroup);
        
    }
    
    
    
    public  UserGroup getUserGroup() {
        return userGroup;
    }
    
    
    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteUserGroup() {
        
        em.remove(userGroup);
    }
    
    
    

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save( ){
        // First clear existing userGroup relationships
        for (Iterator it = userGroup.getUsers().iterator(); it.hasNext();) {
            VDCUser elem = (VDCUser) it.next();
            elem.getUserGroups().remove(userGroup);
        }
        userGroup.getUsers().clear();
        for (Iterator it = userDetailBeans.iterator(); it.hasNext();) {
                UserDetailBean elem = (UserDetailBean) it.next();
                if (elem.getUserName()!=null && !elem.getUserName().trim().equals("")) {
                    VDCUser user = userService.findByUserName(elem.getUserName());
                        user.getUserGroups().add(userGroup);
                        userGroup.getUsers().add(user);
                }        
        
        }
        List emptyLogins = new ArrayList();
        for (Iterator it = userGroup.getLoginDomains().iterator(); it.hasNext();) {
            LoginDomain elem = (LoginDomain) it.next();
            if (elem.getIpAddress() == null || elem.getIpAddress().trim().equals("")) {
                emptyLogins.add(elem);
            }
            
        }
        for (Iterator it2 = emptyLogins.iterator(); it2.hasNext();) {
            LoginDomain elem = (LoginDomain) it2.next();
            userGroup.getLoginDomains().remove(elem);
            em.remove(elem);
        }
        
        //remove any empty or null affiliate rows
        List emptyAffiliates = new ArrayList();
        Iterator iterator = userGroup.getLoginAffiliates().iterator();
        while (iterator.hasNext()) {
            LoginAffiliate loginaffiliate = (LoginAffiliate)iterator.next();
            if (loginaffiliate.getName() == null || loginaffiliate.getName().trim().equals("")) 
                emptyAffiliates.add(loginaffiliate);
        }
        
        iterator = emptyAffiliates.iterator();
        while (iterator.hasNext()) {
            LoginAffiliate loginaffiliate = (LoginAffiliate)iterator.next();
            userGroup.getLoginAffiliates().remove(loginaffiliate);
            em.remove(loginaffiliate);  
        }
        
        
        // Don't really need to do call flush(), because a method that
        // requires a transaction will automatically trigger a flush to the database,
        // but include this just to show what's happening here
        em.flush();
    }
    
    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }
    
    
    public void removeCollectionElement(Collection coll, Object elem) {
        coll.remove(elem);
        em.remove(elem);
    }
    
    /**
     * Remove this Stateful Session bean from the EJB Container without
     * saving updates to the database.
     */
    @Remove
    public void cancel() {
        
    }
    /**
     * Creates a new instance of EditUserServiceBean
     */
    public EditUserGroupServiceBean() {
    }
    
    /**
     * Holds value of property userDetailBeans.
     */
    private List<UserDetailBean> userDetailBeans;
    
    /**
     * Getter for property userDetailBeans.
     * @return Value of property userDetailBeans.
     */
    public List<UserDetailBean> getUserDetailBeans() {
        return this.userDetailBeans;
    }
    
    /**
     * Setter for property userDetailBeans.
     * @param userDetailBeans New value of property userDetailBeans.
     */
    public void setUserDetailBeans(List<UserDetailBean> userDetailBeans) {
        this.userDetailBeans = userDetailBeans;
    }

    public void removeLoginDomains() {
        Iterator iterator = userGroup.getLoginDomains().iterator();
        while (iterator.hasNext()) {
            LoginDomain elem = (LoginDomain)iterator.next();
            em.remove(elem);
            iterator.remove();
        }
       
    }

    public UserGroup findById(Long id) {
        UserGroup o = (UserGroup) em.find(UserGroup.class, id);
        return o;
    }
    
}
