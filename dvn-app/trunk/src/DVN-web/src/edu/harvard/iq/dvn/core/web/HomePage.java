/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
/** Source File Name:   DvRecordsManager.java
 *
 * DvRecordsManager is the backing bean that supports
 * the network home page. It is through this class that the
 * DataverseGrouping parents and children are created and
 * passed onto the view.
 *
 *
 */
package edu.harvard.iq.dvn.core.web;

import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlInputHidden;
import com.icesoft.faces.component.datapaginator.DataPaginator;
import edu.harvard.iq.dvn.core.admin.NetworkRoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.RoleRequestServiceLocal;
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.vdc.*;
import edu.harvard.iq.dvn.core.web.common.StatusMessage;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.study.StudyUI;
import java.io.Serializable;
import java.lang.String;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;

@ViewScoped
@Named
public class HomePage extends VDCBaseBean implements Serializable {
    @EJB StudyServiceLocal      studyService;
    @EJB VDCGroupServiceLocal   vdcGroupService;
    @EJB VDCServiceLocal        vdcService;
    @EJB VariableServiceLocal   varService;
    @EJB IndexServiceLocal      indexService;
    @EJB NetworkRoleServiceLocal networkRoleService;
    @EJB RoleRequestServiceLocal roleRequestService;
    @EJB UserServiceLocal        userService;
    @EJB VDCNetworkStatsServiceLocal vdcNetworkStatsService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    
    

    //Primitives
    private int classificationsSize  = 0;

    

    //Objects
    private ArrayList accordionItemBeans;
    DataverseGrouping parentItem    = null;
    DataverseGrouping childItem     = null;
    private HtmlDataTable dataverseList = new HtmlDataTable();
    private HtmlInputHidden hiddenGroupId = new HtmlInputHidden();
    private HtmlInputHidden hiddenAlphaCharacter = new HtmlInputHidden();
    List descendants                = new ArrayList();
    private List recentStudies;
    private Long groupId;
    private String defaultVdcPath;
    private String groupName;
    private String parsedLocalAnnouncements     = null;
    private String parsedNetworkAnnouncements   = null;
    
    private String searchField;
    StatusMessage msg;
    private boolean isAlphaSort;




    private boolean hideRestricted = true; //show only restricted if set to false wjb

    
    public HomePage() {
    }

     @SuppressWarnings("unchecked")
    public void init() {
        super.init();
        String defaultDVSortColumn =  vdcNetworkService.find().getDefaultDVSortColumn();
        System.out.println("defaultDVSortColumn in HP init "+ defaultDVSortColumn);
        initChrome();
        initAccordionMenu();
        initAlphabeticFilter();
        populateVDCUIList(false);
        isAlphaSort = false;
     }

    public boolean isIsAlphaSort() {
        return isAlphaSort;
    }

    public void setIsAlphaSort(boolean isAlphaSort) {
        this.isAlphaSort = isAlphaSort;
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
    private void _init() {
    }

     

     private void initChrome() {
        msg = (StatusMessage) getRequestMap().get("statusMessage");
    }
    //DEBUG -- new way to get at VDCS
    private ArrayList vdcUI;
    private VDCUIList vdcUIList;

    public VDCUIList getVdcUIList() {
        return this.vdcUIList;
    }

    public DataPaginator getPaginator() {
        if (this.vdcUIList != null) {
            return this.vdcUIList.getPaginator();
        }
        return null; 
    }
    
    public void setPaginator (DataPaginator paginator) {
       if (this.vdcUIList != null) {
            this.vdcUIList.setPaginator(paginator);
        }
    }
    
    public HtmlInputHidden getHiddenGroupId() {
        return hiddenGroupId;
    }

    public void setHiddenGroupId(HtmlInputHidden hiddenGroupId) {
        this.hiddenGroupId = hiddenGroupId;
    }

    public HtmlInputHidden getHiddenAlphaCharacter() {
        return hiddenAlphaCharacter;
    }

    public void setHiddenAlphaCharacter(HtmlInputHidden hiddenAlphaCharacter) {
        this.hiddenAlphaCharacter = hiddenAlphaCharacter;
    }

    private HtmlInputHidden hiddenFilterType = new HtmlInputHidden();

    public HtmlInputHidden getHiddenFilterType() {
        return hiddenFilterType;
    }

    public void setHiddenFilterType(HtmlInputHidden hiddenFilterType) {
        this.hiddenFilterType = hiddenFilterType;
        if (hiddenFilterType.getValue() != null && hiddenFilterType.getValue().toString().equals("alphabetic")) {
            isAlphaSort = true;
        } else {
            isAlphaSort = false;
        }
    }

    public void changeGroupId(ValueChangeEvent event) {
        String changedValue = event.getNewValue().toString();
        if (!changedValue.equals("")) {
            Long newValue = new Long(changedValue);
            Long oldValue = null;
            if (event.getOldValue() != null && !event.getOldValue().equals("")) {
                oldValue = new Long(((Object)event.getOldValue()).toString());
            }
            if ( !newValue.toString().isEmpty()) {
                hiddenGroupId.setValue(newValue);
                groupId = newValue;
                if (oldValue != null && oldValue.equals(newValue) && hiddenFilterType.getValue().equals("alphabetic") && !hiddenAlphaCharacter.getValue().equals("All"))
                    populateVDCUIList(true);
                else
                    populateVDCUIList(false);
            }
        }
    }

    public void changeAlphaCharacter(ValueChangeEvent event) {
        String newValue = (String)event.getNewValue();
        String oldValue = (String)event.getOldValue();
        if (!newValue.isEmpty()) {
            if (newValue.equals("All")) {
                populateVDCUIList(false);
            } else {
                if (!(this.vdcUIList.getPaginator() == null)){
                    this.vdcUIList.getPaginator().gotoFirstPage();
                }
                
                hiddenAlphaCharacter.setValue(newValue);
                populateVDCUIList(true);

            }
        }
    }

    public void changeFilterType(ValueChangeEvent event) {
        String newValue = (String)event.getNewValue();
        if (newValue != null && newValue.equals("alphabetic")) {
            if (!(this.vdcUIList.getPaginator() == null)){
                this.vdcUIList.getPaginator().gotoFirstPage();
            }
            isAlphaSort = true;           
            populateVDCUIList(true);
        } else if (newValue != null && newValue.equals("all")) {
            if (!(this.vdcUIList.getPaginator() == null)){
                this.vdcUIList.getPaginator().gotoFirstPage();
            }
            isAlphaSort = false;
            populateVDCUIList(false);
        } else {
            isAlphaSort = false;
        }
    }

    private Long vdcUIListSize;
    private VDCGroup group;

    private void populateVDCUIList(boolean isAlphaSort) {
                
        String defaultDVSortColumn =  vdcNetworkService.find().getDefaultDVSortColumn();
        System.out.println("defaultDVSortColumn "+ defaultDVSortColumn);
        boolean isNewGroup = false;
        if ( hiddenGroupId.getValue() == null || (vdcUIList != null &&
                ( (vdcUIList.getVdcGroupId() != null &&
                        !vdcUIList.getVdcGroupId().equals(groupId)) ||
                                    vdcUIList.getVdcGroupId() == null)) ) {
            isNewGroup = true;
        }
        if (groupId == null || hiddenGroupId.getValue() == null || hiddenGroupId.getValue().equals("") || hiddenGroupId.getValue().toString().equals("-1")) {
            groupId = new Long("-1");
        }
        if (!isAlphaSort) {
            if (isNewGroup || vdcUIList != null && vdcUIList.getAlphaCharacter() != null && !vdcUIList.getAlphaCharacter().equals("")) {
                vdcUIList = new VDCUIList(groupId, hideRestricted, defaultDVSortColumn);
            } else {
                vdcUIList.setAlphaCharacter("");
                vdcUIList.getVdcUIList();
                vdcUIListSize = new Long(String.valueOf(vdcUIList.getVdcUIList().size()));
            }
        } else {
            vdcUIList = new VDCUIList(groupId, (String)hiddenAlphaCharacter.getValue(), hideRestricted, defaultDVSortColumn);
            if (!((String)hiddenAlphaCharacter.getValue()).equals(vdcUIList.getAlphaCharacter())) {
                vdcUIList.setAlphaCharacter((String)hiddenAlphaCharacter.getValue());
                vdcUIList.oldSort = "";
            }
            vdcUIList.setSortColumnName(vdcUIList.getNameColumnName());
        } 
        if (groupId == null || groupId.equals(new Long("-1")) ) {
            group = null;
            setGroupName("Released Dataverses");
        } else {
            group = vdcGroupService.findById(groupId);
            setGroupName(group.getName());
        }
        vdcUIList.getVdcUIList();
        vdcUIListSize = new Long(String.valueOf(vdcUIList.getVdcUIList().size()));
    }

    
    private ArrayList alphaCharacterList;
    private void initAlphabeticFilter() {
        if (alphaCharacterList == null) {
            alphaCharacterList = new ArrayList();
            alphaCharacterList.add(String.valueOf('#'));            
            for ( char ch = 'A';  ch <= 'Z';  ch++ ) {
              alphaCharacterList.add(String.valueOf(ch));
            }
        }
    }

    public ArrayList getAlphaCharacterList() {
        return this.alphaCharacterList;
    }

    public void setAlphaCharacterList(ArrayList list) {
        this.alphaCharacterList = list;
    }

    public Long getVdcUIListSize() {
        return vdcUIListSize;
    }

    public void setVdcUIListSize(Long vdcUIListSize) {
        this.vdcUIListSize = vdcUIListSize;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public VDCGroup getGroup() {
        return group;
    }

    public void setGroup(VDCGroup group) {
        this.group = group;
    }

    protected void initAccordionMenu() {
        if (accordionItemBeans != null) {
            accordionItemBeans.clear();
        } else {
            accordionItemBeans = new ArrayList();
        }

        List list = (List)vdcGroupService.findAll();
        //itemBeansSize = list.size();
        Iterator outeriterator = list.iterator();
        while(outeriterator.hasNext()) {
            classificationsSize++;
            VDCGroup vdcgroup = (VDCGroup)outeriterator.next();
                String indentStyle = (vdcgroup.getParent() == null) ? "groupRowIndentStyle" : "childRowIndentStyle";
                if (vdcgroup.getParent() == null) {
                    populateTopNode(vdcgroup, indentStyle);
                    // get all of the vdcs that belong to this group and add them to the parent
                    populateDescendants(vdcgroup, true);
                }
        }
    }

      //Manage classification
     protected void populateTopNode(VDCGroup vdcgroup, String indentStyle) {
         boolean isExpanded     = false;
         synchronized(accordionItemBeans) {
            parentItem  = new DataverseGrouping(vdcgroup.getId(), vdcgroup.getName(), "group", accordionItemBeans, isExpanded, "", "", new Long("-1"));
         }
         parentItem.setShortDescription(vdcgroup.getDescription());
         parentItem.setTextIndent(0);

         if (!indentStyle.equals(""))
             parentItem.setIndentStyleClass(indentStyle);
     }

      protected void populateDescendants(VDCGroup vdcgroup, boolean isExpanded) {
         Long parentId        = vdcgroup.getId();
         List list          = vdcGroupService.findByParentId(parentId);
         Iterator iterator  = list.iterator();
         DataverseGrouping childItem;
         while (iterator.hasNext()) {
            VDCGroup group = (VDCGroup)iterator.next();
            childItem = new DataverseGrouping(group.getId(), group.getName(), "subgroup", isExpanded, "", "", parentId);
            parentItem.addItem(childItem);
            parentItem.setIsAccordion(true);
            if (vdcGroupService.findByParentId(group.getId()) != null) {
                List innerlist       = vdcGroupService.findByParentId(group.getId());
                Iterator inneriterator  = innerlist.iterator();
                DataverseGrouping xtraItem;
                childItem.setXtraItems(new ArrayList());
                while (inneriterator.hasNext()) {
                    VDCGroup innergroup = (VDCGroup)inneriterator.next();
                    xtraItem = new DataverseGrouping(innergroup.getId(), innergroup.getName(), "subgroup", isExpanded, "", "", parentId);
                    childItem.addXtraItem(xtraItem);
                }
            }
         }
      }
      


     
  //getters

    public ArrayList getAccordionItemBeans() {
        return accordionItemBeans;
    }

    public int getClassificationsSize() {
        return this.classificationsSize;
    }

    public HtmlDataTable getDataverseList() {
       return this.dataverseList;
   }

     public String getDefaultVdcPath() {
        return defaultVdcPath;
    }

    public Long getGroupId() {
        return groupId;
    }

    public StatusMessage getMsg(){
        return msg;
    }

     public String getParsedLocalAnnouncements() {
         if (parsedLocalAnnouncements == null) {
             parsedLocalAnnouncements = parseAnnouncements((getVDCRequestBean().getCurrentVDC()!= null) ? getVDCRequestBean().getCurrentVDC().getAnnouncements(): "", true);

         }
        return this.parsedLocalAnnouncements;
    }

     public String getParsedNetworkAnnouncements() {
         if (parsedNetworkAnnouncements == null) {
             parsedNetworkAnnouncements = parseAnnouncements((getVDCRequestBean().getVdcNetwork() != null) ? getVDCRequestBean().getVdcNetwork().getAnnouncements(): "", false);
         }         
         
        return this.parsedNetworkAnnouncements;
    }

     public List getRecentStudies() {
        if (recentStudies == null) {
            recentStudies = new ArrayList();
            VDC vdc = getVDCRequestBean().getCurrentVDC();
            if (vdc != null) {
                VDCUser user = getVDCSessionBean().getUser();
                recentStudies = StudyUI.filterVisibleStudies( studyService.getRecentStudies(vdc.getId(), -1), vdc, user, getVDCSessionBean().getIpUserGroup(), 3 );
            }
        }
        return recentStudies;
    }

     public String getSearchField() {
        return searchField;
    }

    public String getStudyCount() {
        Long count = vdcNetworkStatsService.getVDCNetworkStats().getStudyCount();
        return NumberFormat.getIntegerInstance().format(count);
    }

      public String getFileCount() {
        Long count = vdcNetworkStatsService.getVDCNetworkStats().getFileCount();
        return NumberFormat.getIntegerInstance().format(count);
    }


   //setters
 
   public void setDataverseList(HtmlDataTable dataverselist) {
       this.dataverseList = dataverselist;
   }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public void setMsg(StatusMessage msg){
        this.msg = msg;
    }

    public void setParsedLocalAnnouncements(String announcements) {
        this.parsedLocalAnnouncements = announcements;
    }

    public void setParsedNetworkAnnouncements(String announcements) {
        this.parsedNetworkAnnouncements = announcements;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }




    //utils
     /** public String parseLocalAnnouncements
     *
     * @description This utility method checks a string
     * for a regexp pattern and then parses off the remainder.
     *
     *
     *@return parsed announcements
     *
     */
    public String parseAnnouncements(String announcements, boolean isLocal) {
        String truncatedAnnouncements = StringUtil.truncateString(announcements, 1000);
        if ( truncatedAnnouncements != null && !truncatedAnnouncements.equals(announcements) ) {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("Bundle");
            if (isLocal) {
                truncatedAnnouncements += "<a href=\"/dvn/faces/AnnouncementsPage.xhtml?vdcId=" + getVDCRequestBean().getCurrentVDC().getId() + "\" title=\"" + resourceBundle.getString("moreLocalAnnouncementsTip") + "\" class=\"dvn_more\" >more >></a>";
            } else {
                truncatedAnnouncements += "<a href=\"/dvn/faces/AnnouncementsPage.xhtml\" title=\"" + resourceBundle.getString("moreNetworkAnnouncementsTip") + "\" class=\"dvn_more\" >more >></a>";
            }
        }
        return truncatedAnnouncements;
    }


 
    
}