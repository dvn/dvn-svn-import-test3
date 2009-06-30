/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.subsetting;

import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.context.Resource;
import edu.harvard.iq.dvn.core.analysis.NetworkDataServiceLocal;
import edu.harvard.iq.dvn.core.analysis.NetworkDataSubsetResult;
import edu.harvard.iq.dvn.core.analysis.NetworkMeasureParameter;
import edu.harvard.iq.dvn.core.study.DataTable;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.NetworkDataFile;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.util.FileUtil;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.ingest.dsb.impl.DvnRGraphServiceImpl.DvnRGraphException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author gdurand
 */
@EJB(name="networkData", beanInterface=edu.harvard.iq.dvn.core.analysis.NetworkDataServiceLocal.class)
public class NetworkDataAnalysisPage extends VDCBaseBean implements Serializable {

    public static String AUTOMATIC_QUERY_NTHLARGEST = "component";
    public static String AUTOMATIC_QUERY_BICONNECTED = "biconnected_component";
    public static String AUTOMATIC_QUERY_NEIGHBORHOOD = "add_neighborhood";

    public static String NETWORK_MEASURE_DEGREE = "add_degree";
    public static String NETWORK_MEASURE_UNIQUE_DEGREE = "add_unique_degree";
    public static String NETWORK_MEASURE_RANK = "add_pagerank";
    public static String NETWORK_MEASURE_IN_LARGEST = "add_in_largest_component";
    public static String NETWORK_MEASURE_BONACICH_CENTRALITY = "add_bonacich_centrality";

    @EJB
    StudyServiceLocal studyService;

    NetworkDataServiceLocal networkDataService;

    private Long fileId;
    private NetworkDataFile file;
    private String rWorkspace;

    private String actionType = "manualQuery";
    private String manualQueryType = DataTable.TYPE_VERTEX;
    private String manualQuery;
    private boolean eliminateDisconnectedVertices = false;
    private String automaticQueryType = AUTOMATIC_QUERY_NTHLARGEST;
    private String automaticQueryNthValue;
    private String networkMeasureType = NETWORK_MEASURE_RANK;

    private List<NetworkDataAnalysisEvent> events = new ArrayList();
    private List<NetworkMeasureParameter> networkMeasureParamterList = new ArrayList();
    private Map<String,String> friendlyNameMap;
    private Map<String,List> networkMeasureParameterMap;

    private List<SelectItem> vertexAttributeSelectItems;
    private List<SelectItem> edgeAttributeSelectItems;
    private List<SelectItem> automaticQuerySelectItems;
    private List<SelectItem> networkMeasureSelectItems;

    private boolean canUndo = false;

    // used for displaying errros
    private UIComponent manualQueryError;
    private UIComponent automaticQueryError;
    private UIComponent networkMeasureError;
    private HtmlDataTable eventTable;


    public void init() {
        super.init();

        try {
            fileId = Long.parseLong( getRequestParam("fileId") );
            file = (NetworkDataFile) studyService.getStudyFile(fileId);
        } catch (Exception e) { // id not a long, or file is not a NetworkDataFile (TODO: redirect to a different page if not network data file)
            redirect("/faces/IdDoesNotExistPage.xhtml?type=File");
            return;
        }

        //init workspace and page components
        Context ctx;
        try {
            ctx = new InitialContext();
            networkDataService = (NetworkDataServiceLocal) ctx.lookup("java:comp/env/networkData");
            rWorkspace = networkDataService.initAnalysis(file.getFileSystemLocation() + ".RData");
        } catch (NamingException ex) {
            Logger.getLogger(NetworkDataAnalysisPage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DvnRGraphException ex) {
            Logger.getLogger(NetworkDataAnalysisPage.class.getName()).log(Level.SEVERE, null, ex);
        }
        initComponents();
    }

    private void initComponents() {
        vertexAttributeSelectItems =  new ArrayList();
        edgeAttributeSelectItems = new ArrayList();
        automaticQuerySelectItems = new ArrayList();
        networkMeasureSelectItems = new ArrayList();
        friendlyNameMap = new HashMap();
        networkMeasureParameterMap = new HashMap();


        // start with manual query atribute lists
        for (DataVariable dv : file.getVertexDataTable().getDataVariables()) {
            vertexAttributeSelectItems.add(new SelectItem(dv.getName()) );
        }

        for (DataVariable dv : file.getEdgeDataTable().getDataVariables()) {
            edgeAttributeSelectItems.add(new SelectItem(dv.getName()) );
        }
        
        // TODO: we will eventually have to read all the queries and network measures from xml
        // add automatic queries
        friendlyNameMap.put(AUTOMATIC_QUERY_NTHLARGEST, "Largest Graph");
        automaticQuerySelectItems.add(new SelectItem(AUTOMATIC_QUERY_NTHLARGEST, friendlyNameMap.get(AUTOMATIC_QUERY_NTHLARGEST)));

        friendlyNameMap.put(AUTOMATIC_QUERY_BICONNECTED, "Biconnected Graph");
        automaticQuerySelectItems.add(new SelectItem(AUTOMATIC_QUERY_BICONNECTED, friendlyNameMap.get(AUTOMATIC_QUERY_BICONNECTED)));

        friendlyNameMap.put(AUTOMATIC_QUERY_NEIGHBORHOOD, "Neighborhood");
        automaticQuerySelectItems.add(new SelectItem(AUTOMATIC_QUERY_NEIGHBORHOOD, friendlyNameMap.get(AUTOMATIC_QUERY_NEIGHBORHOOD)));

        // and network measures
        friendlyNameMap.put(NETWORK_MEASURE_RANK, "Page Rank");
        networkMeasureSelectItems.add(new SelectItem(NETWORK_MEASURE_RANK, friendlyNameMap.get(NETWORK_MEASURE_RANK)));
        List parameters = new ArrayList();
        NetworkMeasureParameter d = new NetworkMeasureParameter();
        d.setName("d");
        d.setDefaultValue(".85");
        parameters.add(d);
        networkMeasureParameterMap.put(NETWORK_MEASURE_RANK, parameters);
                
        friendlyNameMap.put(NETWORK_MEASURE_DEGREE, "Degree");
        networkMeasureSelectItems.add(new SelectItem(NETWORK_MEASURE_DEGREE, friendlyNameMap.get(NETWORK_MEASURE_DEGREE)));

        friendlyNameMap.put(NETWORK_MEASURE_UNIQUE_DEGREE, "Unique Degree");
        networkMeasureSelectItems.add(new SelectItem(NETWORK_MEASURE_UNIQUE_DEGREE, friendlyNameMap.get(NETWORK_MEASURE_UNIQUE_DEGREE)));

        friendlyNameMap.put(NETWORK_MEASURE_IN_LARGEST, "In Largest Component");
        networkMeasureSelectItems.add(new SelectItem(NETWORK_MEASURE_IN_LARGEST, friendlyNameMap.get(NETWORK_MEASURE_IN_LARGEST)));

        friendlyNameMap.put(NETWORK_MEASURE_BONACICH_CENTRALITY, "Bonacich Centrality");
        networkMeasureSelectItems.add(new SelectItem(NETWORK_MEASURE_BONACICH_CENTRALITY, friendlyNameMap.get(NETWORK_MEASURE_BONACICH_CENTRALITY)));
        parameters = new ArrayList();
        NetworkMeasureParameter p1 = new NetworkMeasureParameter();
        p1.setName("alpha");
        p1.setDefaultValue("1");
        parameters.add(p1);

        NetworkMeasureParameter p2 = new NetworkMeasureParameter();
        p2.setName("exo");
        p2.setDefaultValue("1");
        parameters.add(p2);
        networkMeasureParameterMap.put(NETWORK_MEASURE_BONACICH_CENTRALITY, parameters);

        networkMeasureParamterList = networkMeasureParameterMap.get(networkMeasureType);

        // and finally, add the initial event
        events.add(getInitialEvent());
    }

    private NetworkDataAnalysisEvent getInitialEvent() {
        NetworkDataAnalysisEvent initialEvent = new NetworkDataAnalysisEvent();
        initialEvent.setLabel("Initial State");
        initialEvent.setVertices(file.getVertexDataTable().getCaseQuantity());
        initialEvent.setEdges(file.getEdgeDataTable().getCaseQuantity());
        return initialEvent;
    }

    public NetworkDataFile getFile() {
        return file;
    }

    public void setFile(NetworkDataFile file) {
        this.file = file;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    // helper methods for checking action type
    public boolean isManualQueryAction() {
        return "manualQuery".equals(actionType);
    }

    public boolean isAutomaticQueryAction() {
        return "automaticQuery".equals(actionType);
    }

    public boolean isNetworkMeasureAction() {
        return "networkMeasure".equals(actionType);
    }

    public String getManualQueryType() {
        return manualQueryType;
    }

    public void setManualQueryType(String manualQueryType) {
        this.manualQueryType = manualQueryType;
    }

    public String getManualQuery() {
        return manualQuery;
    }

    public void setManualQuery(String manualQuery) {
        this.manualQuery = manualQuery;
    }

    public boolean isEliminateDisconnectedVertices() {
        return eliminateDisconnectedVertices;
    }

    public void setEliminateDisconnectedVertices(boolean eliminateDisconnectedVertices) {
        this.eliminateDisconnectedVertices = eliminateDisconnectedVertices;
    }

    public String getAutomaticQueryType() {
        return automaticQueryType;
    }

    public void setAutomaticQueryType(String automaticQueryType) {
        this.automaticQueryType = automaticQueryType;
    }

    public String getAutomaticQueryNthValue() {
        return automaticQueryNthValue;
    }

    public void setAutomaticQueryNthValue(String automaticQueryNthValue) {
        this.automaticQueryNthValue = automaticQueryNthValue;
    }

    

    public String getNetworkMeasureType() {
        return networkMeasureType;
    }

    public void setNetworkMeasureType(String networkMeasureType) {
        this.networkMeasureType = networkMeasureType;
    }

    public boolean isCanUndo() {
        return canUndo;
    }

    public void setCanUndo(boolean canUndo) {
        this.canUndo = canUndo;
    }



    public UIComponent getAutomaticQueryError() {
        return automaticQueryError;
    }

    public void setAutomaticQueryError(UIComponent automaticQueryError) {
        this.automaticQueryError = automaticQueryError;
    }

    public UIComponent getManualQueryError() {
        return manualQueryError;
    }

    public void setManualQueryError(UIComponent manualQueryError) {
        this.manualQueryError = manualQueryError;
    }

    public UIComponent getNetworkMeasureError() {
        return networkMeasureError;
    }

    public void setNetworkMeasureError(UIComponent networkMeasureError) {
        this.networkMeasureError = networkMeasureError;
    }

    public HtmlDataTable getEventTable() {
        return eventTable;
    }

    public void setEventTable(HtmlDataTable eventTable) {
        this.eventTable = eventTable;
    }


 

    public List<NetworkDataAnalysisEvent> getEvents() {
        return events;
    }

    public void setEvents(List<NetworkDataAnalysisEvent> events) {
        this.events = events;
    }

    


    public List<SelectItem> getAttributeSelectItems() {
        if (DataTable.TYPE_VERTEX.equals(manualQueryType)) {
            return vertexAttributeSelectItems;
        } else if (DataTable.TYPE_EDGE.equals(manualQueryType)) {
            return edgeAttributeSelectItems;
        }

        return new ArrayList();
    }

    public List<SelectItem> getAutomaticQuerySelectItem() {
        return automaticQuerySelectItems;
    }

    public List<SelectItem> getNetworkMeasureSelectItems() {
        return networkMeasureSelectItems;
    }

    public List<NetworkMeasureParameter> getNetworkMeasureParameterList() {
        return networkMeasureParamterList;
    }


    public void networkMeasureSelect_action(ValueChangeEvent e) {
        networkMeasureParamterList = networkMeasureParameterMap.get(e.getNewValue().toString());
    }

    public String manualQuery_action() {
        try {
            NetworkDataSubsetResult result = networkDataService.runManualQuery(rWorkspace, manualQueryType, manualQuery, eliminateDisconnectedVertices );

            NetworkDataAnalysisEvent event = new NetworkDataAnalysisEvent();
            event.setLabel("Manual Query");
            event.setAttributeSet(manualQueryType);
            event.setQuery(manualQuery);
            event.setVertices( result.getVertices() );
            event.setEdges( result.getEdges() );
            events.add(event);
            canUndo=true;

        } catch (DvnRGraphException e) {
            FacesMessage message = new FacesMessage(e.getMessage());
            getFacesContext().addMessage(manualQueryError.getClientId(getFacesContext()), message);
        }

        return null;
    }

    public String automaticQuery_action() {
        try {
            NetworkDataSubsetResult result = networkDataService.runAutomaticQuery(rWorkspace, automaticQueryType, automaticQueryNthValue);

            NetworkDataAnalysisEvent event = new NetworkDataAnalysisEvent();
            event.setLabel("Automatic Query");
            event.setAttributeSet("N/A");
            event.setQuery(friendlyNameMap.get(automaticQueryType) + " (" + (StringUtil.isEmpty(automaticQueryNthValue) ? "1" : automaticQueryNthValue) + ")");
            event.setVertices( result.getVertices() );
            event.setEdges( result.getEdges() );
            events.add(event);
            canUndo = true;
            
        } catch (DvnRGraphException e) {
                FacesMessage message = new FacesMessage(e.getMessage());
                getFacesContext().addMessage(automaticQueryError.getClientId(getFacesContext()), message);
        }


        return null;
    }

    public String networkMeasure_action() {
        try {
            String result = networkDataService.runNetworkMeasure(rWorkspace, networkMeasureType, networkMeasureParamterList);

            NetworkDataAnalysisEvent event = new NetworkDataAnalysisEvent();
            event.setLabel("Network Measure");
            event.setAttributeSet("N/A");
            event.setQuery(friendlyNameMap.get(networkMeasureType) + " ("+ getNetworkMeasureParametersAsString(networkMeasureParamterList) + ")");
            event.setVertices( getLastEvent().getVertices() );
            event.setEdges( getLastEvent().getEdges() );
            event.setAddedAttribute(result); // in case we need to undo later
            events.add(event);

            // add measure to attributeList
            vertexAttributeSelectItems.add(new SelectItem(result));
            canUndo = true;

        } catch (DvnRGraphException e) {
                FacesMessage message = new FacesMessage(e.getMessage());
                getFacesContext().addMessage(networkMeasureError.getClientId(getFacesContext()), message);
        }

        return null;
    }

    public String restart_action() throws Exception {
        //reinit workspace and clear events
        rWorkspace = networkDataService.initAnalysis(file.getFileSystemLocation() + ".RData");
        events.clear();
        events.add(getInitialEvent());
        canUndo = false;

        return null;
    }

    public String undo_action() throws Exception {
        networkDataService.undoLastEvent(rWorkspace);
        NetworkDataAnalysisEvent lastEvent = getLastEvent();
        if (lastEvent.getAddedAttribute() != null) {
            for (SelectItem selectItem : vertexAttributeSelectItems) {
                if (lastEvent.getAddedAttribute().equals( selectItem.getValue() ) ) {
                    vertexAttributeSelectItems.remove(selectItem);
                    break;
                }

            }
        }

        events.remove( getLastEvent() );
        canUndo = false;

        return null;
    }

    public Resource getSubsetResource() {
        return new RFileResource( getVDCRequestBean().getCurrentVDCId() );
    }

    public String getSubsetFileName() {
        return "subset_" + FileUtil.replaceExtension(file.getFileName(),"zip");
    }

    private String getNetworkMeasureParametersAsString(List<NetworkMeasureParameter> parameterLists) {
        String returnString = "";
        if (parameterLists != null) {
            for (NetworkMeasureParameter parameter : parameterLists) {
                if (!"".equals(returnString)) {
                    returnString += "; ";
                }
                returnString += parameter.getName() + " = ";
                returnString += !StringUtil.isEmpty(parameter.getValue()) ? parameter.getValue() : parameter.getDefaultValue();
            }
        }
        return returnString;
    }

    private NetworkDataAnalysisEvent getLastEvent() {
        return events.get( events.size() - 1);
    }



    public class NetworkDataAnalysisEvent {

        private String label;
        private String attributeSet;
        private String query;
        private long edges;
        private long vertices;
        private String addedAttribute;

        public String getAttributeSet() {
            return attributeSet;
        }

        public void setAttributeSet(String attributeSet) {
            this.attributeSet = attributeSet;
        }

        public long getEdges() {
            return edges;
        }

        public void setEdges(long edges) {
            this.edges = edges;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public long getVertices() {
            return vertices;
        }

        public void setVertices(long vertices) {
            this.vertices = vertices;
        }

        public String getAddedAttribute() {
            return addedAttribute;
        }

        public void setAddedAttribute(String addedAttribute) {
            this.addedAttribute = addedAttribute;
        }

        
    }

    // resource class which doesn't create the file (called via R) until the open method is called (ie the download button is pressed)
    class RFileResource implements Resource, Serializable{
        File file;
        Long vdcId;

        public RFileResource(Long vdcId) {
            this.vdcId = vdcId;
        }

        public String calculateDigest() {
            return file != null ? file.getPath() : null;
        }

        public Date lastModified() {
            return file != null ? new Date(file.lastModified()) : null;
        }

        public InputStream open() throws IOException {
            try {
                file = networkDataService.getSubsetExport(rWorkspace);
            } catch (DvnRGraphException ex) {
                Logger.getLogger(NetworkDataAnalysisPage.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOException("There was a problem attempting to get the export file");
            }

            studyService.incrementNumberOfDownloads(fileId, vdcId);

            return new FileInputStream(file);  
        }

        public void withOptions(Options arg0) throws IOException {
        }
    }
}
