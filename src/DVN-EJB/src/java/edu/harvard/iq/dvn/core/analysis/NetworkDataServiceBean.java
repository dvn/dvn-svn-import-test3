/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.analysis;


import edu.harvard.iq.dvn.core.study.DataTable;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.NetworkDataFile;
import edu.harvard.iq.dvn.core.study.StudyFileEditBean;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.util.FileUtil;
import edu.harvard.iq.dvn.ingest.dsb.impl.DvnRGraphServiceImpl;
import edu.harvard.iq.dvn.ingest.dsb.impl.DvnRJobRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author gdurand
 */
@Stateless
public class NetworkDataServiceBean implements NetworkDataServiceLocal, java.io.Serializable {
    private static Logger dbgLog = Logger.getLogger(NetworkDataServiceBean.class.getPackage().getName());
    @EJB VariableServiceLocal varService;

    public String initAnalysis(String fileLocation) {
        Map<String, String> resultInfo = new HashMap<String, String>();

        DvnRJobRequest rjr = new DvnRJobRequest(fileLocation, null);
        DvnRGraphServiceImpl dgs = new DvnRGraphServiceImpl();
        resultInfo = dgs.execute(rjr);

        checkForError(resultInfo);
        return resultInfo.get(DvnRGraphServiceImpl.SAVED_RWORK_SPACE);
    }

    public NetworkDataSubsetResult runManualQuery(String rWorkspace, String attributeSet, String query, boolean eliminateDisconnectedVertices) {

        Map<String, Object> subsetParameters = new HashMap<String, Object>();
        Map<String, String> resultInfo = new HashMap<String, String>();

        subsetParameters.put( DvnRGraphServiceImpl.SAVED_RWORK_SPACE, rWorkspace);
        subsetParameters.put( DvnRGraphServiceImpl.RSUBSETFUNCTION, DvnRGraphServiceImpl.MANUAL_QUERY_SUBSET );

        if (DataTable.TYPE_VERTEX.equals(attributeSet)) {
            subsetParameters.put( DvnRGraphServiceImpl.MANUAL_QUERY_TYPE, DvnRGraphServiceImpl.VERTEX_SUBSET );
        } else if (DataTable.TYPE_EDGE.equals(attributeSet)) {
             subsetParameters.put( DvnRGraphServiceImpl.MANUAL_QUERY_TYPE, DvnRGraphServiceImpl.EDGE_SUBSET );
        }

        subsetParameters.put( DvnRGraphServiceImpl.MANUAL_QUERY, query );

        if (eliminateDisconnectedVertices) {
            subsetParameters.put( DvnRGraphServiceImpl.ELIMINATE_DISCONNECTED, "TRUE" ); // default is false
        }

        DvnRJobRequest rjr = new DvnRJobRequest(null, subsetParameters);
        DvnRGraphServiceImpl dgs = new DvnRGraphServiceImpl();
        resultInfo = dgs.execute(rjr);

        checkForError(resultInfo);
        NetworkDataSubsetResult result = new NetworkDataSubsetResult();
        result.setVertices( Long.parseLong( resultInfo.get(DvnRGraphServiceImpl.NUMBER_OF_VERTICES) ) );
        result.setEdges( Long.parseLong( resultInfo.get(DvnRGraphServiceImpl.NUMBER_OF_EDGES) ) );
        return result;
    }

    public NetworkDataSubsetResult runAutomaticQuery(String rWorkspace, String automaticQuery, String nValue) {
        Map<String, Object> subsetParameters = new HashMap<String, Object>();
        Map<String, String> resultInfo = new HashMap<String, String>();

        subsetParameters.put(DvnRGraphServiceImpl.SAVED_RWORK_SPACE, rWorkspace);
        subsetParameters.put(DvnRGraphServiceImpl.RSUBSETFUNCTION, DvnRGraphServiceImpl.AUTOMATIC_QUERY_SUBSET);
        subsetParameters.put(DvnRGraphServiceImpl.AUTOMATIC_QUERY_TYPE, automaticQuery);
        subsetParameters.put(DvnRGraphServiceImpl.AUTOMATIC_QUERY_N_VALUE, nValue);

        DvnRJobRequest rjr = new DvnRJobRequest(rWorkspace, subsetParameters);
        DvnRGraphServiceImpl dgs = new DvnRGraphServiceImpl();
        resultInfo = dgs.execute(rjr);

        checkForError(resultInfo);
        NetworkDataSubsetResult result = new NetworkDataSubsetResult();
        result.setVertices( Long.parseLong( resultInfo.get(DvnRGraphServiceImpl.NUMBER_OF_VERTICES) ) );
        result.setEdges( Long.parseLong( resultInfo.get(DvnRGraphServiceImpl.NUMBER_OF_EDGES) ) );
        return result;
    }

    public String runNetworkMeasure(String rWorkspace, String networkMeasure, List<NetworkMeasureParameter> parameters) {
        Map<String, Object> subsetParameters = new HashMap<String, Object>();
        Map<String, String> resultInfo = new HashMap<String, String>();

        subsetParameters.put(DvnRGraphServiceImpl.SAVED_RWORK_SPACE, rWorkspace);
        subsetParameters.put(DvnRGraphServiceImpl.RSUBSETFUNCTION, DvnRGraphServiceImpl.NETWORK_MEASURE);
        subsetParameters.put(DvnRGraphServiceImpl.NETWORK_MEASURE_TYPE, networkMeasure);
        subsetParameters.put(DvnRGraphServiceImpl.NETWORK_MEASURE_PARAMETER, parameters);

        DvnRJobRequest rjr = new DvnRJobRequest(rWorkspace, subsetParameters);
        DvnRGraphServiceImpl dgs = new DvnRGraphServiceImpl();
        resultInfo = dgs.execute(rjr);

        checkForError(resultInfo);
        return resultInfo.get(DvnRGraphServiceImpl.NETWORK_MEASURE_NEW_COLUMN);
    }

    public File getSubsetExport(String rWorkspace) {
        Map<String, String> resultInfo = new HashMap<String, String>();

        DvnRGraphServiceImpl dgs = new DvnRGraphServiceImpl();
        resultInfo = dgs.exportAsGraphML(rWorkspace);

        checkForError(resultInfo);
        return new File( resultInfo.get(DvnRGraphServiceImpl.GRAPHML_FILE_EXPORTED) );
    }

    private void checkForError(Map<String, String> resultInfo) {
        if (resultInfo.get("RexecError") != null && resultInfo.get("RexecError").equals("true")){
            throw new EJBException(resultInfo.get("RexecErrorDescription") + ": " + resultInfo.get("RexecErrorMessage"));
            // resultInfo.get("RexecErrorDescription") -- error condition;
            // resultInfo.get("RexecErrorMessage") -- more detailed error
            //					  message, if available
        }
    }



    public void ingest(StudyFileEditBean editBean)  {
        dbgLog.fine("Begin ingest() ");
        // Initialize NetworkDataFile with new DataTable objects
        NetworkDataFile ndf = (NetworkDataFile)editBean.getStudyFile();
        
        DataTable vertexTable = new DataTable();
        vertexTable.setStudyFile(ndf);
        vertexTable.setDataVariables(new ArrayList<DataVariable>());
        ndf.setVertexDataTable(vertexTable);

        DataTable edgeTable = new DataTable();
        edgeTable.setStudyFile(ndf);
        edgeTable.setDataVariables(new ArrayList<DataVariable>());
        ndf.setEdgeDataTable(edgeTable);

        try {
            // Populate DataTables using GraphML file
            processXML(editBean.getTempSystemFileLocation(),ndf );

            //Copy GraphML file to "ingested" location
            copyFile(editBean);
        } catch(Exception e) {
            throw new EJBException(e);
        }

        // Convert the GraphML file to an RData File, and save it in "ingested" location so it can be loaded later for subsetting
        saveRDataFile(editBean);

    }

    private void processXML(String fileName, NetworkDataFile ndf) throws XMLStreamException, IOException{

        File file = new File(fileName);
        FileReader fileReader = new FileReader(file);
        javax.xml.stream.XMLInputFactory xmlif = javax.xml.stream.XMLInputFactory.newInstance();
        xmlif.setProperty("javax.xml.stream.isCoalescing", java.lang.Boolean.TRUE);

        XMLStreamReader xmlr = xmlif.createXMLStreamReader(fileReader);
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                
                if (xmlr.getLocalName().equals("key")) processKey(xmlr, ndf);
                else if (xmlr.getLocalName().equals("graph")) processGraph(xmlr, ndf);


            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("graphml")) return;
            }
        }

        // If #nodes and #edges is not set, then go thru list to count them
    }
    /**"
     * If this key element is for a node, add a DataVariable to the
     * vertexDataTable, else add a DataVariable to edgeDataTable
     * @param xmlr
     */
    private void processKey(XMLStreamReader xmlr,  NetworkDataFile ndf) {
        DataVariable dataVariable = new DataVariable();

        String attrName = xmlr.getAttributeValue(null, "attr.name");
        dataVariable.setName(attrName);
        dataVariable.setLabel(attrName);

        String attrType = xmlr.getAttributeValue(null, "attr.type");
        if (attrType.equals("string") || attrType.equals("boolean")) {
            dbgLog.fine("attrType = "+attrType);
            dataVariable.setVariableFormatType( varService.findVariableFormatTypeByName( "character" ) );
        } else {
            dataVariable.setVariableFormatType( varService.findVariableFormatTypeByName( "numeric" ) );

        }

        if (xmlr.getAttributeValue(null, "for").equals("node")) {
            ndf.getVertexDataTable().getDataVariables().add(dataVariable);
            dataVariable.setDataTable(ndf.getVertexDataTable());
        } else {
            ndf.getEdgeDataTable().getDataVariables().add(dataVariable);
            dataVariable.setDataTable(ndf.getEdgeDataTable());

        }
    }

    private void processGraph(XMLStreamReader xmlr,  NetworkDataFile ndf)throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("node")) {
                    Long caseQuantity = ndf.getVertexDataTable().getCaseQuantity();
                    if (caseQuantity == null) {
                        caseQuantity = new Long(0);
                    }
                    caseQuantity++;
                    ndf.getVertexDataTable().setCaseQuantity(caseQuantity);
                } else if (xmlr.getLocalName().equals("edge")) {
                    Long caseQuantity = ndf.getEdgeDataTable().getCaseQuantity();
                    if (caseQuantity == null) {
                        caseQuantity = new Long(0);
                    }
                    caseQuantity++;
                    ndf.getEdgeDataTable().setCaseQuantity(caseQuantity);
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("graph")) {
                    ndf.getEdgeDataTable().setVarQuantity(new Long(ndf.getEdgeDataTable().getDataVariables().size()));
                    ndf.getVertexDataTable().setVarQuantity(new Long(ndf.getVertexDataTable().getDataVariables().size()));
                    return;
                }
            }
        }
    }

    private void copyFile(StudyFileEditBean editBean) throws IOException {
        File tempFile = new File(editBean.getTempSystemFileLocation());

        // create a sub-directory "ingested"
        File newDir = new File(tempFile.getParentFile(), "ingested");

        if (!newDir.exists()) {
            newDir.mkdirs();
        }
        dbgLog.fine("newDir: abs path:\n" + newDir.getAbsolutePath());


        File newFile = new File(newDir, tempFile.getName());

        FileInputStream fis = new FileInputStream(tempFile);
        FileOutputStream fos = new FileOutputStream(newFile);
        FileChannel fcin = fis.getChannel();
        FileChannel fcout = fos.getChannel();
        fcin.transferTo(0, fcin.size(), fcout);
        fcin.close();
        fcout.close();
        fis.close();
        fos.close();

        dbgLog.fine("newFile: abs path:\n" + newFile.getAbsolutePath());

        // store the tab-file location
        editBean.setIngestedSystemFileLocation(newFile.getAbsolutePath());


    }


    private void saveRDataFile(StudyFileEditBean editBean) {
        DvnRGraphServiceImpl dgs = new DvnRGraphServiceImpl();


        Map<String, String> resultInfo = new HashMap<String, String>();
        File temploc  = new File(editBean.getTempSystemFileLocation());
        File tempDir = temploc.getParentFile();
        File ingestedDir = new File(tempDir, "ingested");
        if (!ingestedDir.exists()) {
            ingestedDir.mkdirs();
        }
        String rDataFileName = FileUtil.replaceExtension(temploc.getName(),"RData");
        File rDataFile = new File(ingestedDir,rDataFileName);
        resultInfo = dgs.ingestGraphML(editBean.getTempSystemFileLocation(), rDataFile.getAbsolutePath());

        // for cachedRDataFileName we may use the name of the graphML file in
        // the study directory, with the ".RData" extension?

        // error diagnostics:

        if (resultInfo!=null && resultInfo.get("RexecError")!=null && resultInfo.get("RexecError").equals("true")) {
            String err = resultInfo.get("RexecErrorDescription");// -- error condition;
            err += " "+ resultInfo.get("RexecErrorMessage"); //more detailed error message, if available
            throw new EJBException(err);
         
        }
    }

   public static void main(String args[]) throws Exception{


       NetworkDataFile ndf = new NetworkDataFile();
       StudyFileEditBean editBean = new StudyFileEditBean(ndf);

       editBean.setTempSystemFileLocation( "C:\\download\\network data\\alex examples\\boston_1.xml");
       NetworkDataServiceBean ndr = new NetworkDataServiceBean();
       ndr.ingest(editBean);
       DataTable nodeTable = ndf.getVertexDataTable();
       DataTable edgeTable = ndf.getEdgeDataTable();

       System.out.println("done!");

     /*
        // parse an XML document into a DOM tree
    DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document document = parser.parse(new File("instance.xml"));
    document.get

    // create a SchemaFactory capable of understanding WXS schemas
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

    // load a WXS schema, represented by a Schema instance
    Source schemaFile = new StreamSource(new File("mySchema.xsd"));
    Schema schema = factory.newSchema(schemaFile);

    // create a Validator instance, which can be used to validate an instance document
    Validator validator = schema.newValidator();

    // validate the DOM tree
    try {
        validator.validate(new DOMSource(document));
    } catch (SAXException e) {
        // instance document is invalid!
    }
*/

   }

}
