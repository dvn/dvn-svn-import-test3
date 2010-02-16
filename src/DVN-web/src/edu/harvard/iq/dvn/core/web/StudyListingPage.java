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
 * StudyListingPage.java
 *
 * Created on September 19, 2006, 6:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web;

import com.icesoft.faces.component.datapaginator.DataPaginator;
import com.icesoft.faces.component.tree.IceUserObject;
import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.index.SearchTerm;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyField;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCCollection;
import edu.harvard.iq.dvn.core.vdc.VDCCollectionServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.collection.CollectionUI;
import edu.harvard.iq.dvn.core.web.common.LoginBean;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.component.VDCCollectionTree;
import edu.harvard.iq.dvn.core.web.site.VDCUI;
import edu.harvard.iq.dvn.core.web.study.StudyUI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.component.UIData;
import javax.faces.event.ValueChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author gdurand
 */
public class StudyListingPage extends VDCBaseBean implements java.io.Serializable {

    @EJB
    VDCServiceLocal vdcService;
    @EJB
    VDCCollectionServiceLocal vdcCollectionService;
    @EJB
    StudyServiceLocal studyService;
    @EJB
    IndexServiceLocal indexService;
    @EJB
    VariableServiceLocal varService;

    /** Creates a new instance of StudyListingPageBean */
    public StudyListingPage() {
    }

    // data members
    private StudyListing studyListing;
    private DefaultTreeModel collectionTree;
    private UIData studyTable;
    private DataPaginator paginator;
    private DataPaginator paginator2;
    private String searchField;
    private String searchValue;
    private Map studyFields;
    private String studyListingIndex;

    // display items
    boolean renderTree;
    boolean renderSearch;
    boolean renderSort;
    private boolean renderScroller;
    private boolean renderDescription;
    private boolean renderContributorLink;
    private boolean renderTruncatedDesc;
    private boolean descriptionTruncated;

    String listHeader;
    String listMessage;
    String truncatedDescription;

    public StudyListing getStudyListing() {
        return studyListing;
    }

    public void setStudyListing(StudyListing studyListing) {
        this.studyListing = studyListing;
    }

    public Collection getStudies() {
        List studyUIList = new ArrayList();
        VDCUser user = getVDCSessionBean().getUser();
        UserGroup usergroup = getVDCSessionBean().getIpUserGroup();

        if (studyListing != null && studyListing.getStudyIds() != null) {
            Iterator iter = studyListing.getStudyIds().iterator();
            while (iter.hasNext()) {
                Long sid = (Long) iter.next();
                StudyUI sui = new StudyUI(sid, getStudyFields(), user, usergroup);
                if (studyListing.getVariableMap() != null) {
                    List dvList = (List) studyListing.getVariableMap().get(sid);
                    sui.setFoundInVariables(dvList);
                }
                if (studyListing.getVersionMap() != null){
                    List versionList = (List) studyListing.getVersionMap().get(sid);
                    sui.setFoundInVersions(versionList);
                }
                studyUIList.add(sui);
            }
        }

        return studyUIList;
    }

    /**
     * Getter for property collectionTree.
     * @return Value of property collectionTree.
     */
    public DefaultTreeModel getCollectionTree() {
        return this.collectionTree;
    }

    public void setCollectionTree(DefaultTreeModel collectionTree) {
        this.collectionTree = collectionTree;
    }

    public UIData getStudyTable() {
        return studyTable;
    }

    public void setStudyTable(UIData studyTable) {
        this.studyTable = studyTable;
    }

    public String getStudyListingIndex() {
        return studyListingIndex;
    }

    public void setStudyListingIndex(String studyListingIndex) {
        this.studyListingIndex = studyListingIndex;
    }

    public String getListHeader() {
        return listHeader;
    }

    public String getListMessage() {
        return listMessage;
    }

    public String getTruncatedDescription() {
        return truncatedDescription;
    }

    public boolean isDescriptionTruncated() {
        return descriptionTruncated;
    }

    public boolean isRenderTree() {
        return renderTree;
    }

    public boolean isRenderSearch() {
        return renderSearch;
    }

    public boolean isRenderSort() {
        return renderSort;
    }

    public boolean isRenderScroller() {
        return renderScroller;
    }

    public boolean isRenderContributorLink() {
        return renderContributorLink;
    }

    public boolean isRenderDescription() {
        return renderDescription;
    }

    public boolean isRenderTruncatedDesc() {
        return renderTruncatedDesc;
    }

    public String search_action() {
        List searchTerms = new ArrayList();
        SearchTerm st = new SearchTerm();
        st.setFieldName(searchField);
        st.setValue(searchValue);
        searchTerms.add(st);

        List studyIDList = new ArrayList();
        Map variableMap = new HashMap();
        Map versionMap = new HashMap();

        // currently search filter is determined from a set of boolean checkboxes
        int searchFilter = 0;
        if (renderSearchResultsFilter && searchResultsFilter) {
            searchFilter = 2;
        }
        if (renderSearchCollectionFilter && searchCollectionFilter) {
            searchFilter = 1;
        }

        if (searchField.equals("variable")) {
            List variables = null;
            if (searchFilter == 1) {
                // just this collection
                List collections = new ArrayList();
                collections.add(vdcCollectionService.find(studyListing.getCollectionId()));
                variables = indexService.searchVariables(getVDCRequestBean().getCurrentVDC(), collections, st);
            } else if (searchFilter == 2) {
                // subsearch
                variables = indexService.searchVariables(studyListing.getStudyIds(), st);
            } else {
                variables = indexService.searchVariables(getVDCRequestBean().getCurrentVDC(), st);
            }

            varService.determineStudiesFromVariables(variables, studyIDList, variableMap);
        } else {
            if (searchFilter == 1) {
                // just this collection
                List collections = new ArrayList();
                collections.add(vdcCollectionService.find(studyListing.getCollectionId()));
                studyIDList = indexService.search(getVDCRequestBean().getCurrentVDC(), collections, searchTerms);
            } else if (searchFilter == 2) {
                // subsearch
                studyIDList = indexService.search(studyListing.getStudyIds(), searchTerms);
            } else {
                studyIDList = indexService.search(getVDCRequestBean().getCurrentVDC(), searchTerms);
            }
            if (searchField.equals("any")) {
                List<Long> versionIds = indexService.searchVersionUnf(searchValue);
                Iterator iter = versionIds.iterator();
                Long studyId = null;
                List<StudyVersion> svList = new ArrayList<StudyVersion>();
                while (iter.hasNext()) {
                    Long vId = (Long) iter.next();
                    StudyVersion sv = null;
                    try {
                        sv = studyService.getStudyVersionById(vId);
                        studyId = sv.getStudy().getId();
                        svList.add(sv); 
                        if (!studyIDList.contains(studyId)) {
                            studyIDList.add(studyId);
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("StudyVersion (ID=" + vId + ") was found in index, but is not in DB.");
                    }
                }
                versionMap.put(studyId, svList);

            }
        }


        // now we handle the display of the page
        // first get the bound collection tree
        collectionTree = studyListing.getCollectionTree();

        // now create the new StudyListing
        studyListing = new StudyListing(StudyListing.SEARCH);
        studyListing.setVdcId(getVDCRequestBean().getCurrentVDCId());
        studyListing.setStudyIds(studyIDList);
        studyListing.setSearchTerms(searchTerms);
        studyListing.setVariableMap(variableMap);
        studyListing.setVersionMap(versionMap);
        studyListing.setCollectionTree(collectionTree);
        setStudyListingIndex(addToStudyListingMap(studyListing));

        // finally reinit!
        initStudies();
        initPageComponents(StudyListing.SEARCH);
        if (renderTree) {
            initCollectionTree();
        }

        resetScroller();
        return "search";
    }

    public void sort_action(ValueChangeEvent event) {
        String sortBy = (String) event.getNewValue();
        if (sortBy == null || sortBy.equals("")) {
            return;
        }

        if (studyListing.getStudyIds() != null && studyListing.getStudyIds().size() > 0) {
            List sortedStudies = studyService.getOrderedStudies(studyListing.getStudyIds(), sortBy);
            studyListing.setStudyIds(sortedStudies);
            resetScroller();
        }
    }

    public String toggleDescription_action() {
        renderTruncatedDesc = !renderTruncatedDesc;
        return null;
    }

    private void resetScroller() {
        if (paginator != null) {
            paginator.gotoFirstPage();
        }
        if (paginator2 != null) {
            paginator2.gotoFirstPage();
        }
    }

    public void init() {
        super.init();
        if (isFromPage("StudyListingPage")) { // this is a post, so get the studyListing and let actions handle the rest
            String slIndex = getRequestParam("form1:studyListingIndex");
            if (slIndex != null) {
                studyListing = getStudyListingFromMap(slIndex);
                initPageComponents(studyListing.getMode());
            }
        } else {
            // first check for slIndex
            String slIndex = getRequestParam("studyListingIndex");
            if (slIndex != null) {
                studyListing = getStudyListingFromMap(slIndex);
                setStudyListingIndex(slIndex);

            } else {
                // we need to create a new studyListing
                initNewStudyListing();
            }
            
            initStudies();
            initPageComponents(studyListing.getMode());
            if (renderTree) {
                initCollectionTree();
            }

        }
    }

    private void initStudies() {
        if (studyListing.getStudyIds() != null) {
            VDC vdc = getVDCRequestBean().getCurrentVDC();
            
            // first filter the visible studies; visible studies are those that are released
            // and not from a restricted VDC (unless you are in that VDC)
            studyListing.getStudyIds().retainAll(studyService.getVisibleStudies(
                    studyListing.getStudyIds(),
                    vdc != null ? vdc.getId() : null));


            /*
            // next determine user role:
            // if networkAdmin, skip viewable filter (all studies are viewable)
            // if vdc admin or curator, allow vdc's owned studies to pass through filter (by sending vdcId)

            VDCUser user = getVDCSessionBean().getUser();
            UserGroup usergroup = getVDCSessionBean().getIpUserGroup();
            Long passThroughVdcId = null;

             if (user != null) {
                if (user.getNetworkRole() != null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN)) {
                    return;

                } else if (vdc != null) {
                    VDCRole userRole = user.getVDCRole(vdc);
                    String userRoleName = userRole != null ? userRole.getRole().getName() : null;
                    if (RoleServiceLocal.ADMIN.equals(userRoleName) || RoleServiceLocal.CURATOR.equals(userRoleName)) {
                        passThroughVdcId = vdc.getId();
                    }
                }
            }

            studyListing.getStudyIds().retainAll(studyService.getViewableStudies(
                    studyListing.getStudyIds(),
                    (user != null ? user.getId() : null),
                    (usergroup != null ? usergroup.getId() : null),
                    passThroughVdcId ) );
            */
        }
    }

    private void initPageComponents(int mode) {
        int matches = studyListing.getStudyIds() != null ? studyListing.getStudyIds().size() : 0;
        renderSort = matches == 0 ? false : true;
        renderScroller = matches == 0 ? false : true;

        // default the following to false; will likely change after checking mode
        renderSearch = true;
        renderSearchResultsFilter = false;
        renderSearchCollectionFilter = false;
        renderDescription = false;
        renderContributorLink = false;
        renderTree = false;

        
        if (mode == StudyListing.SEARCH) {
            listHeader = "Search Results";
            listMessage = "";

            if (studyListing.getSearchTerms() != null) {
                Iterator iter = studyListing.getSearchTerms().iterator();
                while (iter.hasNext()) {
                    SearchTerm st = (SearchTerm) iter.next();
                    listMessage += getUserFriendlySearchField(st.getFieldName()) + " " + st.getOperator() + " \"" + st.getValue() + "\"";
                    if (iter.hasNext()) {
                        listMessage += " AND ";
                    }
                }
            }

            if (matches == 0) {
                listMessage = "Your search for " + listMessage + " returned no results.";
            } else {
                listMessage = "for " + listMessage;
            }

            renderSearchResultsFilter = matches == 0 ? false : true;

        } else if (mode == StudyListing.COLLECTION_STUDIES) {
            listHeader = vdcCollectionService.find(studyListing.getCollectionId()).getName();

            VDC currentVDC = getVDCRequestBean().getCurrentVDC();
            if (currentVDC == null ||
                    (currentVDC.getRootCollection().getSubCollections().size() == 0 &&
                    currentVDC.getLinkedCollections().size() == 0)) {
                renderTree = false;
            } else {
                renderTree = true;
            }

            renderSearchCollectionFilter = renderTree;
            
            // check description rendering and size
            renderDescription = getVDCRequestBean().getCurrentVDC().isDisplayAnnouncements();
            if (renderDescription) {
                truncatedDescription = StringUtil.truncateString(getVDCRequestBean().getCurrentVDC().getAnnouncements(), 1000);
                if ( truncatedDescription != null && !truncatedDescription.equals(getVDCRequestBean().getCurrentVDC().getAnnouncements()) ) {
                    descriptionTruncated = true;
                    renderTruncatedDesc = true;
                }
            }

            LoginBean loginBean = getVDCSessionBean().getLoginBean();
            renderContributorLink =
                getVDCRequestBean().getCurrentVDC() != null &&
                getVDCRequestBean().getCurrentVDC().isAllowContributorRequests() &&
                (loginBean == null || (loginBean != null && loginBean.isBasicUser() ) );

            if (matches == 0) {
                listMessage = "There are no studies in this " + (renderTree ? "collection." : "dataverse.");
            }

        } else if (mode == StudyListing.VDC_RECENT_STUDIES) {
            listHeader = "Studies Uploaded and Released to This Dataverse";
            renderSearch = true;
            renderSearchResultsFilter = matches == 0 ? false : true;

        } else if (mode == StudyListing.GENERIC_LIST) {
            // this needs to be fleshed out if it's ever used
            listHeader = "Studies";

        } else {
            // in this case we have an invalid list
            if (mode == StudyListing.GENERIC_ERROR) {
                listHeader = "Error";
                listMessage = "Sorry. You must specify a valid mode (and corresponding parameters) for this page.";
            } else if (mode == StudyListing.EXPIRED_LIST) {
                listHeader = "Expired Listing";
                listMessage = "The results for this listing have expired.";
            } else if (mode == StudyListing.INCORRECT_VDC) {
                listHeader = "Invalid Listing";
                listMessage = "The results for this listing were generated while searching or browsing a different dataverse.";
            }
        }

    }

    private String getUserFriendlySearchField(String searchField) {
        try {
            return ResourceBundle.getBundle("SearchFieldBundle").getString(searchField);
        } catch (MissingResourceException e) {
            return searchField;
        }
    }

    private void initCollectionTree() {
        VDCCollectionTree vdcTree = null;

        if (studyListing.getCollectionTree() == null) {
            vdcTree = new VDCCollectionTree();
        } else {
            vdcTree = new VDCCollectionTree(studyListing.getCollectionTree());
        }

        vdcTree.setCollectionUrl("/faces/StudyListingPage.xhtml?mode=1");

        if (studyListing.getCollectionId() != null) {
            vdcTree.setCollectionToBeExpanded(new Long(studyListing.getCollectionId()));
        }

        collectionTree = vdcTree.populate(getVDCRequestBean().getCurrentVDC());
        studyListing.setCollectionTree(collectionTree);

    /* OLD code which sets up different type of tree depending on mode
    if (studyListing.getMode() == StudyListing.SEARCH) {
    // performace of filtering the tree is slow, so for now just show entire tree
    //vdcTree.setStudyFilter(studies);
    //vdcTree.setIncludeCount(true);
    vdcTree.setCollectionUrl("/faces/StudyListingPage.xhtml?mode=3&oslIndex=" + studyListingIndex);
    } else {
    vdcTree.setCollectionUrl("/faces/StudyListingPage.xhtml?mode=1");
    }
     */

    }

    private void initNewStudyListing() {
        StudyListing sl = null;
        int mode = -1;
        try {
            mode = Integer.parseInt(getRequestParam("mode"));
        } catch (Exception e) {
        }    // mode is -1


        if (mode == StudyListing.COLLECTION_STUDIES) {
            String collectionId = getRequestParam("collectionId");
            if (collectionId != null) {
                sl = new StudyListing(StudyListing.COLLECTION_STUDIES);
                sl.setCollectionId(new Long(collectionId));
                CollectionUI collUI = new CollectionUI(vdcCollectionService.find(new Long(sl.getCollectionId())));
                sl.setStudyIds(collUI.getStudyIds());
                setStudyListingIndex(addToStudyListingMap(sl));
            }

        } else if (mode == StudyListing.SEARCH) {
            String searchValue = getRequestParam("searchValue");
            if (searchValue != null) {
                String searchField = getRequestParam("searchField");
                if (searchField == null) {
                    searchField = "any"; // set a default searchField
                }

                sl = search(searchField, searchValue);
                setStudyListingIndex(addToStudyListingMap(sl));
            }

        } else if (mode == StudyListing.COLLECTION_FILTER) {
            String oslIndex = getRequestParam("oslIndex");
            String collectionId = getRequestParam("collectionId");
            if (oslIndex != null && collectionId != null) {
                StudyListing osl = getStudyListingFromMap(oslIndex);
                if (osl.getMode() > 0) { // all study listings <= 0 are error type listings
                    List newStudyIds = new ArrayList();

                    // so we create a new studyListing based on the old one
                    sl = new StudyListing(osl.getMode());
                    sl.setSearchTerms(osl.getSearchTerms());
                    sl.setVariableMap(osl.getVariableMap());
                    sl.setCollectionId(new Long(collectionId));
                    VDCCollection narrowingColl = vdcCollectionService.find(sl.getCollectionId());
                    Iterator iter = osl.getStudyIds().iterator();
                    while (iter.hasNext()) {
                        Study study = studyService.getStudy((Long) iter.next());
                        if (VDCCollectionTree.isStudyInCollection(study, narrowingColl, true)) {
                            newStudyIds.add(study.getId());
                        }
                    }
                    sl.setStudyIds(newStudyIds);
                    setStudyListingIndex(addToStudyListingMap(sl));
                }
            }

        } else if (mode == StudyListing.VDC_RECENT_STUDIES) {
            int numResults = 100;
            try {
                numResults = Integer.parseInt(getRequestParam("numResults"));
            } catch (Exception e) {
            } // numResults remains 100

            sl = new StudyListing(StudyListing.VDC_RECENT_STUDIES);
            VDC vdc = getVDCRequestBean().getCurrentVDC();
            if (vdc != null) {
                VDCUser user = getVDCSessionBean().getUser();
                // TODO: change filter method to only return studyIds
                List studies = StudyUI.filterVisibleStudies(studyService.getRecentStudies(vdc.getId(), -1), vdc, user, getVDCSessionBean().getIpUserGroup(), numResults);
                List studyIds = new ArrayList();
                Iterator iter = studies.iterator();
                while (iter.hasNext()) {
                    Study study = (Study) iter.next();
                    studyIds.add(study.getId());
                }
                sl.setStudyIds(studyIds);
            } else {
                sl.setStudyIds(new ArrayList());
            }

            setStudyListingIndex(addToStudyListingMap(sl));

        } else {
            // in this case we don't have a mode so we check to see if we
            // have a studyListing passed via the request
            sl = getVDCRequestBean().getStudyListing();
            if (sl != null) {
                setStudyListingIndex(addToStudyListingMap(sl));
            }
        }

        // no params; default behavior, show root collection (or first linked); for dvn, show error version
        if (sl == null) {
            VDC currentVDC = getVDCRequestBean().getCurrentVDC();
            if (currentVDC != null) {
                sl = new StudyListing(StudyListing.COLLECTION_STUDIES);
                if ( new VDCUI(currentVDC).containsOnlyLinkedCollections() ) {
                    sl.setCollectionId(new Long(getVDCRequestBean().getCurrentVDC().getLinkedCollections().get(0).getId()));
                } else {
                    sl.setCollectionId(new Long(getVDCRequestBean().getCurrentVDC().getRootCollection().getId()));
                }

                CollectionUI collUI = new CollectionUI(vdcCollectionService.find(new Long(sl.getCollectionId())));
                sl.setStudyIds(collUI.getStudyIds());
                setStudyListingIndex(addToStudyListingMap(sl));
            } else {
                sl = new StudyListing(StudyListing.GENERIC_ERROR);
            }
        }

        studyListing = sl;
        studyListing.setVdcId(getVDCRequestBean().getCurrentVDCId());

    }

    private String addToStudyListingMap(StudyListing sl) {
        return StudyListing.addToStudyListingMap(sl, getSessionMap());
    }

    private StudyListing getStudyListingFromMap(String slIndex) {
        return StudyListing.getStudyListingFromMap(slIndex, getSessionMap(), getVDCRequestBean().getCurrentVDCId());
    }

    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public DataPaginator getPaginator() {
        return paginator;
    }

    public void setPaginator(DataPaginator paginator) {
        this.paginator = paginator;
    }

    public DataPaginator getPaginator2() {
        return paginator2;
    }

    public void setPaginator2(DataPaginator paginator2) {
        this.paginator2 = paginator2;
    }

    public Map getStudyFields() {
        if (studyFields == null) {
            studyFields = new HashMap();
            VDC vdc = getVDCRequestBean().getCurrentVDC();
            if (vdc != null) {
                Iterator iter = vdc.getSearchResultFields().iterator();
                while (iter.hasNext()) {
                    StudyField sf = (StudyField) iter.next();
                    studyFields.put(sf.getName(), sf.getName());
                }
            }
        }

        return studyFields;
    }

    // booleans for search filters
    boolean searchResultsFilter;
    boolean searchCollectionFilter;
    boolean renderSearchResultsFilter;
    boolean renderSearchCollectionFilter;

    public boolean isSearchResultsFilter() {
        return searchResultsFilter;
    }

    public void setSearchResultsFilter(boolean searchResultsFilter) {
        this.searchResultsFilter = searchResultsFilter;
    }

    public boolean isSearchCollectionFilter() {
        return searchCollectionFilter;
    }

    public void setSearchCollectionFilter(boolean searchCollectionFilter) {
        this.searchCollectionFilter = searchCollectionFilter;
    }

    public boolean isRenderSearchResultsFilter() {
        return renderSearchResultsFilter;
    }

    public void setRenderSearchResultsFilter(boolean renderSearchResultsFilter) {
        this.renderSearchResultsFilter = renderSearchResultsFilter;
    }

    public boolean isRenderSearchCollectionFilter() {
        return renderSearchCollectionFilter;
    }

    public void setRenderSearchCollectionFilter(boolean renderSearchCollectionFilter) {
        this.renderSearchCollectionFilter = renderSearchCollectionFilter;
    }

    private StudyListing search(String searchField, String searchValue) {
        // TODO: combine the search logic from the search_action (which has more
        // flexibility) with this one for one unified search code section
        List searchTerms = new ArrayList();
        SearchTerm st = new SearchTerm();
        st.setFieldName(searchField);
        st.setValue(searchValue);
        searchTerms.add(st);

        List studies = new ArrayList();
        Map variableMap = new HashMap();

        if (searchField.equals("variable")) {
            List variables = indexService.searchVariables(getVDCRequestBean().getCurrentVDC(), st);
            varService.determineStudiesFromVariables(variables, studies, variableMap);

        } else {
            studies = indexService.search(getVDCRequestBean().getCurrentVDC(), searchTerms);
        }


        StudyListing sl = new StudyListing(StudyListing.SEARCH);
        sl.setStudyIds(studies);
        sl.setSearchTerms(searchTerms);
        sl.setVariableMap(variableMap);

        sl.setVdcId(getVDCRequestBean().getCurrentVDCId());
        sl.setCollectionTree(collectionTree);

        return sl;
    }

    public int getStudyCount() {
        return studyListing.getStudyIds().size();
    }

    public int getCollectionTreeVisibleNodeCount() {
        int visibleNodeCount = 0;
        if (renderTree) {
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) collectionTree.getRoot();
            visibleNodeCount = countVisibleNodes(rootNode) - 1; // subtract 1 to remove the hidden root from the count
        }

        return visibleNodeCount;
    }

    private int countVisibleNodes(DefaultMutableTreeNode node) {
        int count = 1;

        if ( ((IceUserObject) node.getUserObject()).isExpanded()) {
            Enumeration childrenEnum = node.children();
            while (childrenEnum.hasMoreElements()) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) childrenEnum.nextElement();
                count += countVisibleNodes(childNode);
            }
        }

        return count;
    }

}
