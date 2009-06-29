/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.ingest.dsb.impl;

import edu.harvard.iq.dvn.core.analysis.NetworkMeasureParameter; 

import edu.harvard.iq.dvn.core.util.StringUtil;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.lang.reflect.*;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.*;

import org.apache.commons.lang.*;
import org.apache.commons.lang.builder.*;

/**
 *
 * @author landreev
 */

public class DvnRGraphServiceImpl{

    // - static filelds
    
    private static Logger dbgLog = Logger.getLogger(DvnRGraphServiceImpl.class.getPackage().getName());

    private RConnection rc = null; 

    // - constants for defining the subset queries: 

    public static String RSUBSETFUNCTION = "RSUBSETFUNCTION";

    // - different kinds of subset functions: 

    public static String MANUAL_QUERY_SUBSET = "MANUAL_QUERY_SUBSET";
    public static String MANUAL_QUERY_TYPE = "MANUAL_QUERY_TYPE";
    public static String MANUAL_QUERY = "MANUAL_QUERY";
    public static String ELIMINATE_DISCONNECTED = "ELIMINATE_DISCONNECTED";
    public static String EDGE_SUBSET = "EDGE_SUBSET";
    public static String VERTEX_SUBSET = "VERTEX_SUBSET";

    public static String AUTOMATIC_QUERY_SUBSET = "AUTOMATIC_QUERY_SUBSET";
    public static String AUTOMATIC_QUERY_TYPE = "AUTOMATIC_QUERY_TYPE";
    public static String AUTOMATIC_QUERY_N_VALUE = "AUTOMATIC_QUERY_N_VALUE";


    public static String NETWORK_MEASURE = "NETWORK_MEASURE"; 
    public static String NETWORK_MEASURE_TYPE = "NETWORK_MEASURE_TYPE"; 
    public static String NETWORK_MEASURE_PARAMETER = "NETWORK_MEASURE_PARAMETER";

    public static String UNDO = "UNDO";
    // - return result fields:

    public static String SAVED_RWORK_SPACE = "SAVED_RWORK_SPACE";
    public static String NUMBER_OF_VERTICES = "NUMBER_OF_VERTICES"; 
    public static String NUMBER_OF_EDGES = "NUMBER_OF_EDGES"; 
    public static String NETWORK_MEASURE_NEW_COLUMN = "NETWORK_MEASURE_NEW_COLUMN";
    public static String GRAPHML_FILE_EXPORTED  = "GRAPHML_FILE_EXPORTED"; 

    public static String DVN_TMP_DIR=null;
    public static String DSB_TMP_DIR=null;

    private static String GRAPHML_FILE_NAME = "iGraph";
    public static String GRAPHML_FILE_EXT =".xml";

    private static String RDATA_FILE_NAME = "iGraph";
    public static String RDATA_FILE_EXT =".RData";

    private static String RSERVE_HOST = null;
    private static String RSERVE_USER = null;
    private static String RSERVE_PWD = null;    
    private static int RSERVE_PORT;
    private static String DSB_HOST_PORT= null;

    private static Map<String, Method> runMethods = new HashMap<String, Method>();
    private static String regexForRunMethods = "^run(\\w+)Request$" ;
    public static String TEMP_DIR = System.getProperty("java.io.tmpdir");
    
    static {
    
        DSB_TMP_DIR = System.getProperty("vdc.dsb.temp.dir");
        
        // fallout case: last resort
        if (DSB_TMP_DIR == null){
            
            DVN_TMP_DIR ="/tmp/VDC";
            DSB_TMP_DIR = DVN_TMP_DIR + "/DSB";
            
        }
        
        RSERVE_HOST = System.getProperty("vdc.dsb.host");
	DSB_HOST_PORT = System.getProperty("vdc.dsb.port");
        if (DSB_HOST_PORT == null){
            DSB_HOST_PORT= "80";
        }
                
        RSERVE_USER = System.getProperty("vdc.dsb.rserve.user");
        if (RSERVE_USER == null){
            RSERVE_USER= "rserve";
        }
        
        RSERVE_PWD = System.getProperty("vdc.dsb.rserve.pwrd");
        if (RSERVE_PWD == null){
            RSERVE_PWD= "rserve";
        }
        

        if (System.getProperty("vdc.dsb.rserve.port") == null ){
            RSERVE_PORT= 6311;
        } else {
            RSERVE_PORT = Integer.parseInt(System.getProperty("vdc.dsb.rserve.port"));
        }
        

    }

    static String librarySetup= "library('NetworkUtils');";
    boolean DEBUG = true;
    
    // ----------------------------------------------------- instance filelds
    public String IdSuffix = null;
    public String GraphMLfileNameRemote = null;    
    public String RDataFileName = null;
    public String wrkdir = null;
    public String requestdir = null;
    public List<String> historyEntry = new ArrayList<String>();
    public List<String> replicationFile = new LinkedList<String>();
    
    // ----------------------------------------------------- constructor
    public DvnRGraphServiceImpl(){
        
        // initialization
        IdSuffix = RandomStringUtils.randomNumeric(6);
                 
        requestdir = "Grph_" + IdSuffix;
        
        wrkdir = DSB_TMP_DIR + "/" + requestdir;
        
	RDataFileName = DSB_TMP_DIR + "/" + RDATA_FILE_NAME
                 +"." + IdSuffix + RDATA_FILE_EXT;

	GraphMLfileNameRemote = DSB_TMP_DIR + "/" + GRAPHML_FILE_NAME
                 + "." + IdSuffix + GRAPHML_FILE_EXT;
	
    }


    public void setupWorkingDirectories(RConnection c){
        try{

            // set up the working directory
            // parent dir;

	    // the 4 lines below are R code being sent over to Rserve;
	    // it looks kinda messy, true.

            String checkWrkDir = "if (file_test('-d', '"+DSB_TMP_DIR+"')) {Sys.chmod('"+
            DVN_TMP_DIR+"', mode = '0777'); Sys.chmod('"+DSB_TMP_DIR+"', mode = '0777');} else {dir.create('"+DSB_TMP_DIR+"', showWarnings = FALSE, recursive = TRUE);Sys.chmod('"+DVN_TMP_DIR+"', mode = '0777');Sys.chmod('"+
            DSB_TMP_DIR+"', mode = '0777');}";

            dbgLog.fine("w permission="+checkWrkDir);

            c.voidEval(checkWrkDir);

            // wrkdir
            String checkWrkDr = "if (file_test('-d', '"+wrkdir+"')) {Sys.chmod('"+
            wrkdir+"', mode = '0777'); } else {dir.create('"+wrkdir+"', showWarnings = FALSE, recursive = TRUE);Sys.chmod('"+wrkdir+"', mode = '0777');}";
            dbgLog.fine("w permission:wrkdir="+checkWrkDr);
            c.voidEval(checkWrkDr);

        } catch (RserveException rse) {
            rse.printStackTrace();
        }

    }
    
    
    /** *************************************************************
     * initialize the RServe connection and load the graph;
     * keep the open connection.
     *
     * @param sro    a DvnRJobRequest object that contains various parameters
     * @return    a Map that contains various information about the results
     */    
    
    public Map<String, String> initializeConnection(DvnRJobRequest sro) {
    
        // set the return object
        Map<String, String> result = new HashMap<String, String>();
        
        try {

	    if ( sro != null ) {
		dbgLog.fine("sro dump:\n"+ToStringBuilder.reflectionToString(sro, ToStringStyle.MULTI_LINE_STYLE));
            } else {
		result.put("RexecError", "true");
		result.put("RexecErrorDescription", "NULL R JOB OBJECT"); 
		return result;
	    }

            // Set up an Rserve connection

            dbgLog.fine("RSERVE_USER="+RSERVE_USER+"[default=rserve]");
            dbgLog.fine("RSERVE_PWD="+RSERVE_PWD+"[default=rserve]");
            dbgLog.fine("RSERVE_PORT="+RSERVE_PORT+"[default=6311]");

            rc = new RConnection(RSERVE_HOST, RSERVE_PORT);
            dbgLog.fine("hostname="+RSERVE_HOST);

            rc.login(RSERVE_USER, RSERVE_PWD);
            dbgLog.fine(">" + rc.eval("R.version$version.string").asString() + "<");
            dbgLog.fine("wrkdir="+wrkdir);
            historyEntry.add(librarySetup);
            rc.voidEval(librarySetup);

	    String SavedRworkSpace = null;  
	    String CachedRworkSpace = sro.getCachedRworkSpace(); 

	    Map <String, Object> SubsetParameters = sro.getParametersForGraphSubset(); 

	    if ( SubsetParameters != null ) {
		SavedRworkSpace = (String) SubsetParameters.get(SAVED_RWORK_SPACE);
	    }

	    if ( SavedRworkSpace != null ) {
		RDataFileName = SavedRworkSpace; 
		dbgLog.fine("RDataFile="+RDataFileName);
		historyEntry.add("load('"+RDataFileName+"')");
		String cmdResponse = safeEval(rc, "load('"+RDataFileName+"')").asString();

	    } else if ( CachedRworkSpace != null ) {
		// send data file to the Rserve side 

		InputStream inb = new BufferedInputStream(new FileInputStream(CachedRworkSpace));
		int bufsize;
		byte[] bffr = new byte[1024];

		RFileOutputStream os = 
		    rc.createFile(RDataFileName);
		while ((bufsize = inb.read(bffr)) != -1) {
                    os.write(bffr, 0, bufsize);
		}
		os.close();
		inb.close();

		// Using "safeEval" for the next operation, loading 
		// the RData file, to catch any possible error messages
		// in case R refuses to load it:

		String cmdResponse = safeEval(rc,"load_and_clear('"+RDataFileName+"')").asString();

		// not sure if this extra "save.image" is still necessary;
		// will double-check with Alex. 

		String saveWS = "save.image(file='"+ RDataFileName +"')";
		dbgLog.fine("save the workspace="+saveWS);
		rc.voidEval(saveWS);

	    } else {
		result.put("RexecError", "true");
		result.put("RexecErrorDescription", "Initialize method called without either local or remote RData file"); 
		return result;
	    }

	    result.put(SAVED_RWORK_SPACE, RDataFileName);

	    result.put("dsbHost", RSERVE_HOST);
	    result.put("dsbPort", DSB_HOST_PORT);
	    result.put("IdSuffix", IdSuffix);
		

	} catch (RException re) {
	    result.put("IdSuffix", IdSuffix);
	    result.put("RCommandHistory",  StringUtils.join(historyEntry,"\n"));
	    result.put("RexecError", "true");
	    result.put("RexecErrorMessage", re.getMessage());
	    result.put("RexecErrorDescription", "init failed: R runtime Error");

	    dbgLog.info("rserve exception message: "+ re.getMessage());
	    dbgLog.info("rserve exception description: "+ "init failed: R runtime Error");
	    return result;

        } catch (RserveException rse) {
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            
            result.put("RexecError", "true");
            result.put("RexecErrorMessage", rse.getMessage());
            result.put("RexecErrorDescription", rse.getRequestErrorDescription());

            dbgLog.info("rserve exception message: "+rse.getMessage());
            dbgLog.info("rserve exception description: "+rse.getRequestErrorDescription());
            return result;

        } catch (REXPMismatchException mme) {
        
            // REXP mismatch exception (what we got differs from what we expected)
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            result.put("RexecError", "true");
            return result;

        } catch (IOException ie){
            
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            result.put("RexecError", "true");
            return result;
            
        } catch (Exception ex){
            
            result.put("IdSuffix", IdSuffix);

            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            result.put("RexecError", "true");
            return result;
        }
        
        return result;
        
    }


    /** *************************************************************
     * close RServe connection;
     * (should I remove the saved workspace(s) on the server side? 
     *
     * @return    a Map that contains diagnostics information 
     * in case of an error. 
     */    
    
    public Map<String, String> closeConnection() {
    
        // set the return object
        Map<String, String> result = new HashMap<String, String>();
        
	if ( rc == null ) {
	    dbgLog.fine("close method called on null connection!");
	    result.put("RexecError", "true");
	    result.put("RexecErrorDescription", "CLOSE CALLED ON NULL CONNECTION"); 
	} else {
	    rc.close(); 
	}
	
	return result;

    }

    /** *************************************************************
     * checks on the RServe connection status;
     *
     * @return  boolean
     */    
    
    public boolean isAlive() {
    
	if ( rc == null ) {
	    return false; 
	} 
	
	return rc.isConnected();

    }


    /** *************************************************************
     * Execute an R-based dvn analysis request on a Graph object
     * using an open connection created during the Initialize call. 
     *
     * @param sro    a DvnRJobRequest object that contains various parameters
     * @return    a Map that contains various information about the results
     */    
    
    public Map<String, String> liveConnectionExecute(DvnRJobRequest sro) throws DvnRGraphException {
    
        // set the return object
        Map<String, String> result = new HashMap<String, String>();
        
        try {
            // Check if there's an Rserve connection: 
	    if ( rc == null ) {
		dbgLog.fine("LCE method called on null connection!");
		//result.put("RexecError", "true");
		//result.put("RexecErrorDescription", "EXECUTE CALLED ON NULL CONNECTION"); 
		//return result;

		throw new DvnRGraphException("execute method called on null connection");
	    }



	    if ( sro != null ) {
		dbgLog.fine("LCE sro dump:\n"+ToStringBuilder.reflectionToString(sro, ToStringStyle.MULTI_LINE_STYLE));
            } else {
		//result.put("RexecError", "true");
		//result.put("RexecErrorDescription", "LCE: NULL R JOB OBJECT"); 
		//return result;

		throw new DvnRGraphException("execute method called with a NULL job ob ject.");

	    }

	    // (if we have a non-null connection that's stale/closed, 
	    // Rserve will throw an exception below). 

	    String SavedRworkSpace = null;  

	    Map <String, Object> SubsetParameters = sro.getParametersForGraphSubset(); 
	    
	    if ( SubsetParameters != null ) {
		SavedRworkSpace = (String) SubsetParameters.get(SAVED_RWORK_SPACE);
	    } else {
		//result.put("RexecError", "true");
		//result.put("RexecErrorDescription", "LCE: NULL PARAMETERS OBJECT"); 
		//return result;
		throw new DvnRGraphException("execute method called with a null parameters object");

	    }

	    /*
	    if ( SavedRworkSpace != null ) {
		RDataFileName = SavedRworkSpace; 
	    } else {
		result.put("RexecError", "true");
		result.put("RexecErrorDescription", "LCE: NULL R JOB OBJECT"); 
		return result;
	    }		
	    */

            // subsetting 

	    String GraphSubsetType = (String) SubsetParameters.get(RSUBSETFUNCTION); 

	    if ( GraphSubsetType != null ) {
		
		if ( GraphSubsetType.equals(MANUAL_QUERY_SUBSET) ) {

		    String manualQueryType  = (String) SubsetParameters.get(MANUAL_QUERY_TYPE); 
		    String manualQuery = (String) SubsetParameters.get(MANUAL_QUERY); 

		    String subsetCommand = null; 

		    if ( manualQueryType != null ) {
			if (manualQueryType.equals(EDGE_SUBSET)) {
			    String dropDisconnected = (String) SubsetParameters.get(ELIMINATE_DISCONNECTED); 
			    if ( dropDisconnected != null ) {
				subsetCommand = "edge_subset(g, '"+manualQuery+"', TRUE)"; 
			    } else {
				subsetCommand = "edge_subset(g, '"+manualQuery+"')"; 
			    }

			} else if (manualQueryType.equals(VERTEX_SUBSET)){
			    subsetCommand = "vertex_subset(g, '"+manualQuery+"')"; 
			} else {
			    //result.put("RexecError", "true");
			    //return result;
			    throw new DvnRGraphException("execute: unsupported manual query subset");

			}		       

			dbgLog.fine("LCE: manualQuerySubset="+subsetCommand);
			historyEntry.add(subsetCommand);
			String cmdResponse = safeEval(rc,subsetCommand).asString();
			
		    }
		    
		} else if ( GraphSubsetType.equals(NETWORK_MEASURE) ) {
		    String networkMeasureType = (String) SubsetParameters.get(NETWORK_MEASURE_TYPE); 
		    String networkMeasureCommand = null; 
		    if ( networkMeasureType != null ) {
			    List<NetworkMeasureParameter> networkMeasureParameterList = (List<NetworkMeasureParameter>)SubsetParameters.get(NETWORK_MEASURE_PARAMETER);
                networkMeasureCommand = networkMeasureType + "(g" + buildParameterComponent(networkMeasureParameterList) + ")";
            /*
			if ( networkMeasureType.equals(NETWORK_MEASURE_DEGREE) ) {
			    networkMeasureCommand = "add_degree(g)"; 

			} else if ( networkMeasureType.equals(NETWORK_MEASURE_UNIQUE_DEGREE) ) {
			    networkMeasureCommand = "add_unique_degree(g)";

			} else if ( networkMeasureType.equals(NETWORK_MEASURE_IN_LARGEST) ) {
			    networkMeasureCommand = "add_in_largest_component(g)"; 

			} else if ( networkMeasureType.equals(NETWORK_MEASURE_RANK) ) {
			    String networkMeasureParam = null; 

			    List<NetworkMeasureParameter> networkMeasureParameterList = (List<NetworkMeasureParameter>)SubsetParameters.get(NETWORK_MEASURE_PARAMETER);
			    if ( networkMeasureParameterList != null ) {
								
				int i = 0; 

				// Page Rank takes one parameter, d (for "damping");
				// it is hard-coded below. We will add a better
				// system for keeping track of the parameters
				// that different functions take; either by
				// specifying them as constants in this class,
				// or, if it gets complicated, keeping an XML
				// config file somewhere. 

				while ( networkMeasureParam == null && networkMeasureParameterList.get(i) != null ) {
				    NetworkMeasureParameter nparameter = networkMeasureParameterList.get(i); 
				    if ( "d".equals(nparameter.getName()) ) {
					networkMeasureParam = nparameter.getValue();
				    }
				    i++; 
				}

				if ( networkMeasureParam != null ) {
				    networkMeasureCommand = "add_pagerank(g, "+networkMeasureParam+")";
				} else {
				    // this "damping" parameter isn't 
				    // mandatory; if the function is envoked 
				    // with one argument, the value of d=0.85
				    // is used. 
				    networkMeasureCommand = "add_pagerank(g)";

				}
			    }
			}
            */
		    }

		    if ( networkMeasureCommand == null ) {
			//result.put("RexecError", "true");
			//result.put("RexecErrorDescription", "ILLEGAL OR UNSUPPORTED NETWORK MEASURE QUERY"); 
			//return result;
			throw new DvnRGraphException("ILLEGAL OR UNSUPPORTED NETWORK MEASURE QUERY");

		    }

		    dbgLog.info("LCE: networkMeasureCommand="+networkMeasureCommand);
		    historyEntry.add(networkMeasureCommand);
		    String addedColumn = safeEval(rc,networkMeasureCommand).asString();
		    dbgLog.info("LCE: added column="+addedColumn);

		    if ( addedColumn != null ) {
			result.put(NETWORK_MEASURE_NEW_COLUMN, addedColumn);
		    } else {
			//result.put("RexecError", "true");
			//result.put("RexecErrorDescription", "FAILED TO READ ADDED COLUMN NAME"); 
			//return result;
			throw new DvnRGraphException("FAILED TO READ ADDED COLUMN NAME");

		    }
		    
		} else if ( GraphSubsetType.equals(AUTOMATIC_QUERY_SUBSET) ) {
		    String automaticQueryType = (String) SubsetParameters.get(AUTOMATIC_QUERY_TYPE); 
		    String autoQueryCommand = null; 
		    if ( automaticQueryType != null ) {
            String n = (String) SubsetParameters.get(AUTOMATIC_QUERY_N_VALUE);
            autoQueryCommand = automaticQueryType + "(g, " + n + ")";

		    }

		    if ( autoQueryCommand == null ) {
			//result.put("RexecError", "true");
			//result.put("RexecErrorDescription", "NULL OR UNSUPPORTED AUTO QUERY"); 
			//return result;
			throw new DvnRGraphException("NULL OR UNSUPPORTED AUTO QUERY");
		    }

		    dbgLog.info("LCE: autoQueryCommand="+autoQueryCommand);
		    historyEntry.add(autoQueryCommand);
		    String cEval = safeEval(rc, autoQueryCommand).asString();
		    dbgLog.info("LCE: auto query eval: "+cEval);

		} else if ( GraphSubsetType.equals(UNDO) ) {
		    String cEval = safeEval(rc, "undo()").asString();
		}
	    }

	    // get the vertices and edges counts: 

	    String countCommand = "vcount(g)";
	    int countResponse = safeEval(rc, countCommand).asInteger(); 
	    result.put(NUMBER_OF_VERTICES, Integer.toString(countResponse)); 

	    countCommand = "ecount(g)";
	    countResponse = safeEval(rc, countCommand).asInteger(); 
	    result.put(NUMBER_OF_EDGES, Integer.toString(countResponse)); 

            
            // save workspace:

            String saveWS = "save.image(file='"+ RDataFileName +"')";
            dbgLog.fine("LCE: save the workspace="+saveWS);
            rc.voidEval(saveWS);

	    result.put( SAVED_RWORK_SPACE, RDataFileName ); 

	    // we're done; let's add some potentially useful 
	    // information to the result and return: 

	    String RexecDate = rc.eval("as.character(as.POSIXct(Sys.time()))").asString();
	    String RversionLine = "R.Version()$version.string";
            String Rversion = rc.eval(RversionLine).asString();
            
            result.put("dsbHost", RSERVE_HOST);
            result.put("dsbPort", DSB_HOST_PORT);
            result.put("IdSuffix", IdSuffix);
            result.put("Rversion", Rversion);
            result.put("RexecDate", RexecDate);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            

            dbgLog.fine("LCE: result object (before closing the Rserve):\n"+result);
                    
	} catch (RException re) {
	    //result.put("IdSuffix", IdSuffix);
	    //result.put("RCommandHistory",  StringUtils.join(historyEntry,"\n"));
	    //result.put("RexecError", "true");
	    //result.put("RexecErrorMessage", re.getMessage());
	    //result.put("RexecErrorDescription", "R runtime Error");

	    dbgLog.info("LCE: rserve exception message: "+ re.getMessage());
	    dbgLog.info("LCE: rserve exception description: "+ "R runtime Error");
	    //return result;
	    throw new DvnRGraphException("R failed to process the input; Error  message: " +re.getMessage());

        } catch (RserveException rse) {
            //result.put("IdSuffix", IdSuffix);
            //result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            
            //result.put("RexecError", "true");
            //result.put("RexecErrorMessage", rse.getMessage());
            //result.put("RexecErrorDescription", rse.getRequestErrorDescription());

            dbgLog.info("LCE: rserve exception message: "+rse.getMessage());
            dbgLog.info("LCE: rserve exception description: "+rse.getRequestErrorDescription());
            //return result;
	    throw new DvnRGraphException("RServe failure; Error message: "+rse.getMessage());

        } catch (REXPMismatchException mme) {
        
            // REXP mismatch exception (what we got differs from what we expected)
            //result.put("IdSuffix", IdSuffix);
            //result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            //result.put("RexecError", "true");
            //return result;
	    throw new DvnRGraphException("REXPmismatchException occured");


        } catch (Exception ex){
            
            //result.put("IdSuffix", IdSuffix);

            //result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            //result.put("RexecError", "true");
            //return result;
	    throw new DvnRGraphException("Unknown exception occured: " +ex.getMessage());

        }
        
        return result;
        
    }

    /** *************************************************************
     * Export a saved RData file as a GraphML file, using the existing
     * open connection
     *
     * @param savedRDatafile;
     * @return    a Map that contains various information about the results
     */    
    
     public Map<String, String> liveConnectionExport (String savedRDataFile) {

        Map<String, String> result = new HashMap<String, String>();
        
	try {
            // Check if there's an Rserve connection: 
	    if ( rc == null ) {
		dbgLog.fine("LC export method called on null connection!");
		result.put("RexecError", "true");
		result.put("RexecErrorDescription", "EXPOPRT CALLED ON NULL CONNECTION"); 
		return result;
	    }

	    // Check if the connection is alive: 
	    if ( !rc.isConnected() ) {
		dbgLog.fine("LC export method called on a closed connection!");
		result.put("RexecError", "true");
		result.put("RexecErrorDescription", "EXPOPRT CALLED ON CLOSED CONNECTION"); 
		return result;
	    }

	    String exportCommand = "dump_graphml(g, '" + GraphMLfileNameRemote + "')";
	    dbgLog.fine(exportCommand);
	    historyEntry.add(exportCommand);
	    String cmdResponse = safeEval(rc, exportCommand).asString(); 

	    exportCommand = "dump_tab(g, '" + DSB_TMP_DIR + "/temp_" + IdSuffix + ".tab')";
	    dbgLog.fine(exportCommand);
	    historyEntry.add(exportCommand);
	    cmdResponse = safeEval(rc, exportCommand).asString();


	    File zipFile  = new File(TEMP_DIR, "subset_" + IdSuffix + ".zip");
	    FileOutputStream zipFileStream = new FileOutputStream(zipFile);
	    ZipOutputStream zout = new ZipOutputStream( new FileOutputStream(zipFile) );

	    addZipEntry(rc, zout, GraphMLfileNameRemote, "data/subset.xml");
	    addZipEntry(rc, zout, DSB_TMP_DIR + "/temp_" + IdSuffix + "_verts.tab", "data/vertices.tab");
	    addZipEntry(rc, zout, DSB_TMP_DIR + "/temp_" + IdSuffix + "_edges.tab", "data/edges.tab");

	    zout.close();
	    zipFileStream.close();

	    result.put(GRAPHML_FILE_EXPORTED, zipFile.getAbsolutePath());

	    String RexecDate = rc.eval("as.character(as.POSIXct(Sys.time()))").asString();
	    String RversionLine = "R.Version()$version.string";
            String Rversion = rc.eval(RversionLine).asString();
            
            result.put("dsbHost", RSERVE_HOST);
            result.put("dsbPort", DSB_HOST_PORT);
            result.put("IdSuffix", IdSuffix);

            result.put("Rversion", Rversion);
            result.put("RexecDate", RexecDate);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            
            dbgLog.fine("result object (before closing the Rserve):\n"+result);
        
	} catch (RException re) {
	    result.put("IdSuffix", IdSuffix);
	    result.put("RCommandHistory",  StringUtils.join(historyEntry,"\n"));
	    result.put("RexecError", "true");
	    result.put("RexecErrorMessage", re.getMessage());
	    result.put("RexecErrorDescription", "R runtime Error");

	    dbgLog.info("rserve exception message: "+ re.getMessage());
	    dbgLog.info("rserve exception description: "+ "R runtime Error");
	    return result;
        } catch (RserveException rse) {
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            
            result.put("RexecError", "true");
	    result.put("RexecErrorMessage", rse.getMessage()); 
	    result.put("RexecErrorDescription", rse.getRequestErrorDescription()); 
            return result;

        } catch (REXPMismatchException mme) {
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            result.put("RexecError", "true");
            return result;

        } catch (FileNotFoundException fe){
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            result.put("RexecError", "true");
	    result.put("RexecErrorDescription", "File Not Found"); 
            return result;

	} catch (IOException ie){
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            result.put("RexecError", "true");
            return result;
            
        } catch (Exception ex){
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            result.put("RexecError", "true");
            return result;
        }
        
        return result;
        
    }

    
    /** *************************************************************
     * Execute an R-based dvn analysis request on a Graph object
     *
     * @param sro    a DvnRJobRequest object that contains various parameters
     * @return    a Map that contains various information about the results
     */    
    
    public Map<String, String> execute(DvnRJobRequest sro) {
    
        // set the return object
        Map<String, String> result = new HashMap<String, String>();
        
        try {
	    if ( sro != null ) {
		dbgLog.fine("sro dump:\n"+ToStringBuilder.reflectionToString(sro, ToStringStyle.MULTI_LINE_STYLE));
            } else {
		result.put("RexecError", "true");
		result.put("RexecErrorDescription", "NULL R JOB OBJECT"); 
		return result;
	    }

            // Set up an Rserve connection

            dbgLog.fine("RSERVE_USER="+RSERVE_USER+"[default=rserve]");
            dbgLog.fine("RSERVE_PWD="+RSERVE_PWD+"[default=rserve]");
            dbgLog.fine("RSERVE_PORT="+RSERVE_PORT+"[default=6311]");

            RConnection c = new RConnection(RSERVE_HOST, RSERVE_PORT);
            dbgLog.fine("hostname="+RSERVE_HOST);

            c.login(RSERVE_USER, RSERVE_PWD);
            dbgLog.fine(">" + c.eval("R.version$version.string").asString() + "<");
            dbgLog.fine("wrkdir="+wrkdir);
            historyEntry.add(librarySetup);
            c.voidEval(librarySetup);

	    String SavedRworkSpace = null;  

	    String CachedRworkSpace = sro.getCachedRworkSpace(); 

	    Map <String, Object> SubsetParameters = sro.getParametersForGraphSubset(); 
	    
	    if ( SubsetParameters != null ) {
		SavedRworkSpace = (String) SubsetParameters.get(SAVED_RWORK_SPACE);
	    }

	    if ( SavedRworkSpace != null ) {
		RDataFileName = SavedRworkSpace; 

	    } else if ( CachedRworkSpace != null ) {
		// send data file to the Rserve side 

		InputStream inb = new BufferedInputStream(new FileInputStream(CachedRworkSpace));
		int bufsize;
		byte[] bffr = new byte[1024];

		RFileOutputStream os = 
		    c.createFile(RDataFileName);
		while ((bufsize = inb.read(bffr)) != -1) {
                    os.write(bffr, 0, bufsize);
		}
		os.close();
		inb.close();

		c.voidEval("load_and_clear('"+RDataFileName+"')");

        String saveWS = "save.image(file='"+ RDataFileName +"')";
        dbgLog.fine("save the workspace="+saveWS);
        c.voidEval(saveWS);

		result.put(SAVED_RWORK_SPACE, RDataFileName);

		result.put("dsbHost", RSERVE_HOST);
		result.put("dsbPort", DSB_HOST_PORT);
		result.put("IdSuffix", IdSuffix);
		
		c.close();

		return result; 

	    } 

            dbgLog.fine("RDataFile="+RDataFileName);
            historyEntry.add("load('"+RDataFileName+"')");
            c.voidEval("load('"+RDataFileName+"')");

            // check working directories
            setupWorkingDirectories(c);
            
            // subsetting 

	    String GraphSubsetType = (String) SubsetParameters.get(RSUBSETFUNCTION); 

	    if ( GraphSubsetType != null ) {
		
		if ( GraphSubsetType.equals(MANUAL_QUERY_SUBSET) ) {

		    String manualQueryType  = (String) SubsetParameters.get(MANUAL_QUERY_TYPE); 
		    String manualQuery = (String) SubsetParameters.get(MANUAL_QUERY); 

		    String subsetCommand = null; 

		    if ( manualQueryType != null ) {
			if (manualQueryType.equals(EDGE_SUBSET)) {
			    String dropDisconnected = (String) SubsetParameters.get(ELIMINATE_DISCONNECTED); 
			    if ( dropDisconnected != null ) {
				subsetCommand = "edge_subset(g, '"+manualQuery+"', TRUE)"; 
			    } else {
				subsetCommand = "edge_subset(g, '"+manualQuery+"')"; 
			    }

			} else if (manualQueryType.equals(VERTEX_SUBSET)){
			    subsetCommand = "vertex_subset(g, '"+manualQuery+"')"; 
			} else {
			    result.put("RexecError", "true");
			    return result;
			}		       

			dbgLog.fine("manualQuerySubset="+subsetCommand);
			historyEntry.add(subsetCommand);
			c.voidEval(subsetCommand);
			
		    }
		    
		} else if ( GraphSubsetType.equals(NETWORK_MEASURE) ) {
		    String networkMeasureType = (String) SubsetParameters.get(NETWORK_MEASURE_TYPE); 
		    String networkMeasureCommand = null; 
		    if ( networkMeasureType != null ) {
			    List<NetworkMeasureParameter> networkMeasureParameterList = (List<NetworkMeasureParameter>)SubsetParameters.get(NETWORK_MEASURE_PARAMETER);
                networkMeasureCommand = networkMeasureType + "(g" + buildParameterComponent(networkMeasureParameterList) + ")";
            /*
			if ( networkMeasureType.equals(NETWORK_MEASURE_DEGREE) ) {
			    networkMeasureCommand = "add_degree(g)"; 

			} else if ( networkMeasureType.equals(NETWORK_MEASURE_UNIQUE_DEGREE) ) {
			    networkMeasureCommand = "add_unique_degree(g)";

			} else if ( networkMeasureType.equals(NETWORK_MEASURE_IN_LARGEST) ) {
			    networkMeasureCommand = "add_in_largest_component(g)"; 

			} else if ( networkMeasureType.equals(NETWORK_MEASURE_RANK) ) {
			    String networkMeasureParam = null; 

			    List<NetworkMeasureParameter> networkMeasureParameterList = (List<NetworkMeasureParameter>)SubsetParameters.get(NETWORK_MEASURE_PARAMETER);
			    if ( networkMeasureParameterList != null ) {
								
				int i = 0; 

				// Page Rank takes one parameter, d (for "damping");
				// it is hard-coded below. We will add a better
				// system for keeping track of the parameters
				// that different functions take; either by
				// specifying them as constants in this class,
				// or, if it gets complicated, keeping an XML
				// config file somewhere. 

				while ( networkMeasureParam == null && networkMeasureParameterList.get(i) != null ) {
				    NetworkMeasureParameter nparameter = networkMeasureParameterList.get(i); 
				    if ( "d".equals(nparameter.getName()) ) {
					networkMeasureParam = nparameter.getValue();
				    }
				    i++; 
				}

				if ( networkMeasureParam != null ) {
				    networkMeasureCommand = "add_pagerank(g, "+networkMeasureParam+")";
				} else {
				    // this "damping" parameter isn't 
				    // mandatory; if the function is envoked 
				    // with one argument, the value of d=0.85
				    // is used. 
				    networkMeasureCommand = "add_pagerank(g)";

				}
			    }
			}
            */
		    }

		    if ( networkMeasureCommand == null ) {
			result.put("RexecError", "true");
			result.put("RexecErrorDescription", "ILLEGAL OR UNSUPPORTED NETWORK MEASURE QUERY"); 
			return result;
		    }


		    dbgLog.info("networkMeasureCommand="+networkMeasureCommand);
		    historyEntry.add(networkMeasureCommand);
		    String addedColumn = safeEval(c,networkMeasureCommand).asString();
		    dbgLog.info("added column="+addedColumn);

		    if ( addedColumn != null ) {
			result.put(NETWORK_MEASURE_NEW_COLUMN, addedColumn);
		    } else {
			result.put("RexecError", "true");
			result.put("RexecErrorDescription", "FAILED TO READ ADDED COLUMN NAME"); 
			return result;
		    }
		    
		} else if ( GraphSubsetType.equals(AUTOMATIC_QUERY_SUBSET) ) {
		    String automaticQueryType = (String) SubsetParameters.get(AUTOMATIC_QUERY_TYPE); 
		    String autoQueryCommand = null; 
		    if ( automaticQueryType != null ) {
            String n = (String) SubsetParameters.get(AUTOMATIC_QUERY_N_VALUE);
            autoQueryCommand = automaticQueryType + "(g, " + n + ")";
            /*
			if ( automaticQueryType.equals(AUTOMATIC_QUERY_NTHLARGEST) ) {
			    int n = Integer.parseInt((String) SubsetParameters.get(AUTOMATIC_QUERY_N_VALUE));
			    autoQueryCommand = "component(g, " + n + ")";

			} else if ( automaticQueryType.equals(AUTOMATIC_QUERY_BICONNECTED) ) {
			    int n = Integer.parseInt((String) SubsetParameters.get(AUTOMATIC_QUERY_N_VALUE));
			    autoQueryCommand = "biconnected_component(g, " + n + ")";

			} else if ( automaticQueryType.equals(AUTOMATIC_QUERY_NEIGHBORHOOD) ) {
			    int n = Integer.parseInt((String) SubsetParameters.get(AUTOMATIC_QUERY_N_VALUE));
			    autoQueryCommand = "add_neighborhood(g, " + n + ")";

			}
            */
		    }

		    if ( autoQueryCommand == null ) {
			result.put("RexecError", "true");
			result.put("RexecErrorDescription", "NULL OR UNSUPPORTED AUTO QUERY"); 
			return result;

		    }

		    dbgLog.info("autoQueryCommand="+autoQueryCommand);
		    historyEntry.add(autoQueryCommand);
		    //c.voidEval(autoQueryCommand);
		    String cEval = safeEval(c, autoQueryCommand).asString();
		    dbgLog.info("auto query eval: "+cEval);

		} else if ( GraphSubsetType.equals(UNDO) ) {
            c.voidEval("undo()");
        }

	    }

	    // get the vertices and edges counts: 

	    String countCommand = "vcount(g)";
	    int countResponse = safeEval(c, countCommand).asInteger(); 
	    result.put(NUMBER_OF_VERTICES, Integer.toString(countResponse)); 

	    countCommand = "ecount(g)";
	    countResponse = safeEval(c, countCommand).asInteger(); 
	    result.put(NUMBER_OF_EDGES, Integer.toString(countResponse)); 

            
            // save workspace:

            String saveWS = "save.image(file='"+ RDataFileName +"')";
            dbgLog.fine("save the workspace="+saveWS);
            c.voidEval(saveWS);


	    result.put( SAVED_RWORK_SPACE, RDataFileName ); 

	    // we're done; let's add some potentially useful 
	    // information to the result and return: 

	    String RexecDate = c.eval("as.character(as.POSIXct(Sys.time()))").asString();
	    String RversionLine = "R.Version()$version.string";
            String Rversion = c.eval(RversionLine).asString();
            
            result.put("dsbHost", RSERVE_HOST);
            result.put("dsbPort", DSB_HOST_PORT);
            result.put("IdSuffix", IdSuffix);
            result.put("Rversion", Rversion);
            result.put("RexecDate", RexecDate);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            

            dbgLog.fine("result object (before closing the Rserve):\n"+result);
                    
            c.close();

	} catch (RException re) {
	    result.put("IdSuffix", IdSuffix);
	    result.put("RCommandHistory",  StringUtils.join(historyEntry,"\n"));
	    result.put("RexecError", "true");
	    result.put("RexecErrorMessage", re.getMessage());
	    result.put("RexecErrorDescription", "R runtime Error");

	    dbgLog.info("rserve exception message: "+ re.getMessage());
	    dbgLog.info("rserve exception description: "+ "R runtime Error");
	    return result;

        } catch (RserveException rse) {
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            
            result.put("RexecError", "true");
            result.put("RexecErrorMessage", rse.getMessage());
            result.put("RexecErrorDescription", rse.getRequestErrorDescription());

            dbgLog.info("rserve exception message: "+rse.getMessage());
            dbgLog.info("rserve exception description: "+rse.getRequestErrorDescription());
            return result;

        } catch (REXPMismatchException mme) {
        
            // REXP mismatch exception (what we got differs from what we expected)
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            result.put("RexecError", "true");
            return result;

        } catch (IOException ie){
            
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            result.put("RexecError", "true");
            return result;
            
        } catch (Exception ex){
            
            result.put("IdSuffix", IdSuffix);

            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            result.put("RexecError", "true");
            return result;
        }
        
        return result;
        
}

    private String buildParameterComponent(List<NetworkMeasureParameter> parameters) {
        String returnString = "";
        if (parameters != null) {
            for (NetworkMeasureParameter param : parameters) {
                if ( !StringUtil.isEmpty(param.getValue()) ) {
                    returnString += ", " + param.getName() + "=" + param.getValue();
                }
            }
        }
        return returnString;
    }
    
    /** *************************************************************
     * Execute an R-based "ingest" of a GraphML file
     *
     * @param graphMLfileName;
     * @param cachedRDatafileName;
     * @return    a Map that contains various information about the results
     */    
    
     public Map<String, String> ingestGraphML (String graphMLfileName, 
					      String cachedRDatafileName) {

        Map<String, String> result = new HashMap<String, String>();
        
	try {

            // Set up an Rserve connection
            
            dbgLog.fine("RSERVE_USER="+RSERVE_USER+"[default=rserve]");
            dbgLog.fine("RSERVE_PWD="+RSERVE_PWD+"[default=rserve]");
            dbgLog.fine("RSERVE_PORT="+RSERVE_PORT+"[default=6311]");

            RConnection c = new RConnection(RSERVE_HOST, RSERVE_PORT);
            dbgLog.fine("hostname="+RSERVE_HOST);

            c.login(RSERVE_USER, RSERVE_PWD);

            dbgLog.fine(">" + c.eval("R.version$version.string").asString() + "<");

            // send the graphML to the Rserve side

            InputStream inb = new BufferedInputStream(new FileInputStream(graphMLfileName));

            int bufsize;
            byte[] bffr = new byte[1024];

            RFileOutputStream os = c.createFile(GraphMLfileNameRemote);

            while ((bufsize = inb.read(bffr)) != -1) {
                    os.write(bffr, 0, bufsize);
            }
            os.close();
            inb.close();
            
            historyEntry.add(librarySetup);
            c.voidEval(librarySetup);

            this.setupWorkingDirectories(c);

            // ingest itself:

	    String ingestCommand = "ingest_graphml('" + GraphMLfileNameRemote + "')";
	    dbgLog.fine(ingestCommand);
	    historyEntry.add(ingestCommand);
	    String responseVoid = safeEval(c,ingestCommand).asString();
	     
            int fileSize = getFileSize(c,RDataFileName);
            
	    OutputStream outbr = new BufferedOutputStream(new FileOutputStream(new File(cachedRDatafileName)));
	    RFileInputStream ris = c.openFile(RDataFileName);

	    if (fileSize < 64*1024*1024){
		bufsize = fileSize;
	    } else {
		bufsize = 64*1024*1024; 
	    }

	    byte[] obuf = new byte[bufsize];

	    while ( ris.read(obuf) != -1 ) {
		outbr.write(obuf, 0, bufsize);
	    }

	    ris.close();
	    outbr.close();


	    String RexecDate = c.eval("as.character(as.POSIXct(Sys.time()))").asString();
	    String RversionLine = "R.Version()$version.string";
            String Rversion = c.eval(RversionLine).asString();
            
            result.put("dsbHost", RSERVE_HOST);
            result.put("dsbPort", DSB_HOST_PORT);
            result.put("IdSuffix", IdSuffix);

            result.put("Rversion", Rversion);
            result.put("RexecDate", RexecDate);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            
            dbgLog.fine("result object (before closing the Rserve):\n"+result);
                    
            c.close();
        
	} catch (RException re) {
	    result.put("IdSuffix", IdSuffix);
	    result.put("RCommandHistory",  StringUtils.join(historyEntry,"\n"));
	    result.put("RexecError", "true");
	    result.put("RexecErrorMessage", re.getMessage());
	    result.put("RexecErrorDescription", "R runtime Error");

	    dbgLog.info("rserve exception message: "+ re.getMessage());
	    dbgLog.info("rserve exception description: "+ "R runtime Error");
	    return result;
        } catch (RserveException rse) {
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            
            result.put("RexecError", "true");
	    result.put("RexecErrorMessage", rse.getMessage()); 
	    result.put("RexecErrorDescription", rse.getRequestErrorDescription()); 
            return result;

        } catch (REXPMismatchException mme) {
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            result.put("RexecError", "true");
            return result;

        } catch (FileNotFoundException fe){
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            result.put("RexecError", "true");
	    result.put("RexecErrorDescription", "File Not Found"); 
            return result;

	} catch (IOException ie){
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            result.put("RexecError", "true");
            return result;
            
        } catch (Exception ex){
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            result.put("RexecError", "true");
            return result;
        }
        
        return result;
        
    }
    
    /** *************************************************************
     * Export a saved RData file as a GraphML file
     *
     * @param savedRDatafile;
     * @return    a Map that contains various information about the results
     */    
    
     public Map<String, String> exportAsGraphML (String savedRDataFile) {

        Map<String, String> result = new HashMap<String, String>();
        
	try {

            // Set up an Rserve connection
            
            dbgLog.fine("RSERVE_USER="+RSERVE_USER+"[default=rserve]");
            dbgLog.fine("RSERVE_PWD="+RSERVE_PWD+"[default=rserve]");
            dbgLog.fine("RSERVE_PORT="+RSERVE_PORT+"[default=6311]");

            RConnection c = new RConnection(RSERVE_HOST, RSERVE_PORT);
            dbgLog.fine("hostname="+RSERVE_HOST);

            c.login(RSERVE_USER, RSERVE_PWD);

            dbgLog.fine(">" + c.eval("R.version$version.string").asString() + "<");

	    historyEntry.add(librarySetup);
            c.voidEval(librarySetup);
	    historyEntry.add("load_and_clear('"+savedRDataFile+"')");
	    c.voidEval("load_and_clear('"+savedRDataFile+"')");

        // export: GraphML
	    String exportCommand = "dump_graphml(g, '" + GraphMLfileNameRemote + "')";
	    dbgLog.fine(exportCommand);
	    historyEntry.add(exportCommand);
	    c.voidEval(exportCommand);            

        // export: tab files for vertices and edges
	    exportCommand = "dump_tab(g, '" + DSB_TMP_DIR + "/temp_" + IdSuffix + ".tab')";
	    dbgLog.fine(exportCommand);
	    historyEntry.add(exportCommand);
	    String responseVoid = safeEval(c, exportCommand).asString();


	    File zipFile  = new File(TEMP_DIR, "subset_" + IdSuffix + ".zip");
	    FileOutputStream zipFileStream = new FileOutputStream(zipFile);
	    ZipOutputStream zout = new ZipOutputStream( new FileOutputStream(zipFile) );

	    addZipEntry(c, zout, GraphMLfileNameRemote, "data/subset.xml");
	    addZipEntry(c, zout, DSB_TMP_DIR + "/temp_" + IdSuffix + "_verts.tab", "data/vertices.tab");
	    addZipEntry(c, zout, DSB_TMP_DIR + "/temp_" + IdSuffix + "_edges.tab", "data/edges.tab");

	    zout.close();
	    zipFileStream.close();

	    result.put(GRAPHML_FILE_EXPORTED, zipFile.getAbsolutePath());
	    
	    String RexecDate = c.eval("as.character(as.POSIXct(Sys.time()))").asString();
	    String RversionLine = "R.Version()$version.string";
            String Rversion = c.eval(RversionLine).asString();
            
            result.put("dsbHost", RSERVE_HOST);
            result.put("dsbPort", DSB_HOST_PORT);
            result.put("IdSuffix", IdSuffix);

            result.put("Rversion", Rversion);
            result.put("RexecDate", RexecDate);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            
            dbgLog.fine("result object (before closing the Rserve):\n"+result);
                    
            c.close();
        
	} catch (RException re) {
	    result.put("IdSuffix", IdSuffix);
	    result.put("RCommandHistory",  StringUtils.join(historyEntry,"\n"));
	    result.put("RexecError", "true");
	    result.put("RexecErrorMessage", re.getMessage());
	    result.put("RexecErrorDescription", "R runtime Error");

	    dbgLog.info("rserve exception message: "+ re.getMessage());
	    dbgLog.info("rserve exception description: "+ "R runtime Error");
	    return result;
        } catch (RserveException rse) {
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            
            result.put("RexecError", "true");
	    result.put("RexecErrorMessage", rse.getMessage()); 
	    result.put("RexecErrorDescription", rse.getRequestErrorDescription()); 
            return result;

        } catch (REXPMismatchException mme) {
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            result.put("RexecError", "true");
            return result;

        } catch (FileNotFoundException fe){
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));
            result.put("RexecError", "true");
	    result.put("RexecErrorDescription", "File Not Found"); 
            return result;

	} catch (IOException ie){
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            result.put("RexecError", "true");
            return result;
            
        } catch (Exception ex){
            result.put("IdSuffix", IdSuffix);
            result.put("RCommandHistory", StringUtils.join(historyEntry,"\n"));

            result.put("RexecError", "true");
            return result;
        }
        
        return result;
        
    }

     private void addZipEntry(RConnection c, ZipOutputStream zout, String inputFileName, String outputFileName) throws IOException{
        RFileInputStream tmpin = c.openFile(inputFileName);
        byte[] dataBuffer = new byte[8192];
        int i = 0;

        ZipEntry e = new ZipEntry(outputFileName);
        zout.putNextEntry(e);

        while ((i = tmpin.read(dataBuffer)) > 0) {
            zout.write(dataBuffer, 0, i);
            zout.flush();
        }
        tmpin.close();
        zout.closeEntry();
     }
    

    // -- utilitiy methods
    
    /**
     * Returns the array of values that corresponds the order of 
     * provided keys
     *
     * @param     
     * @return    
     */

    public static String[] getValueSet(Map<String, String> mp, String[] keys) {
        
        List<String> tmpvl = new ArrayList<String>();
        for (int i=0; i< keys.length; i++){
            tmpvl.add(mp.get(keys[i]));
        }
        String[] tmpv = (String[])tmpvl.toArray(new String[tmpvl.size()]);
        return tmpv;
    }
    
    
    /** *************************************************************
     * 
     *
     * @param     
     * @return    
     */
    public String joinNelementsPerLine(String[] vn, int divisor){
        String vnl = null;
        if (vn.length < divisor){
            vnl = StringUtils.join(vn, ", ");
        } else {
            StringBuilder sb = new StringBuilder();
            
            int iter =  vn.length / divisor;
            int lastN = vn.length % divisor;
            if (lastN != 0){
                iter++;
            }
            int iterm = iter - 1;
            for (int i= 0; i<iter; i++){
                int terminalN = divisor;
                if ((i == iterm )  && (lastN != 0)){
                    terminalN = lastN;
                }                
                for (int j = 0; j< terminalN; j++){
                    if ( (divisor*i +j +1) == vn.length){ 
                        sb.append(vn[j + i*divisor]);
                    } else {
                        
                        sb.append(vn[j + i*divisor] + ", ");
                    }
                }
                sb.append("\n");
            }
            vnl = sb.toString();
            dbgLog.fine(vnl);
        }
        return vnl;
    }
    
    /** *************************************************************
     * 
     *
     * @param     
     * @return    
     */
    public String joinNelementsPerLine(String[] vn, int divisor, String sp, 
        boolean quote, String qm, String lnsp){
        if (!(divisor >= 1)){
            divisor = 1;
        } else if ( divisor > vn.length) {
            divisor = vn.length;
        }
        String sep = null;
        if (sp != null){
            sep = sp;
        } else {
            sep = ", ";
        }
        String qmrk = null;
        if (quote){
            if (qm == null){
                qmrk = ",";
            } else {
                if (qm.equals("\"")){
                    qmrk = "\"";
                } else {
                    qmrk = qm;
                }
            }
        } else {
            qmrk = "";
        }
        String lineSep = null;
        if (lnsp == null){
            lineSep = "\n";
        } else {
            lineSep = lnsp;
        }
        
        String vnl = null;
        if (vn.length < divisor){
            vnl = StringUtils.join(vn, sep);
        } else {
            StringBuilder sb = new StringBuilder();
            
            int iter =  vn.length / divisor;
            int lastN = vn.length % divisor;
            if (lastN != 0){
                iter++;
            }
            int iterm = iter - 1;
            for (int i= 0; i<iter; i++){
                int terminalN = divisor;
                if ((i == iterm )  && (lastN != 0)){
                    terminalN = lastN;
                }                
                for (int j = 0; j< terminalN; j++){
                    if ( (divisor*i +j +1) == vn.length){ 
                        
                        sb.append(qmrk + vn[j + i*divisor] + qmrk);
                        
                    } else {
                        
                        sb.append(qmrk + vn[j + i*divisor] + qmrk + sep);
                        
                    }
                }
                if (i < (iter-1)){
                sb.append(lineSep);
                }
            }
            vnl = sb.toString();
            dbgLog.fine("results:\n"+vnl);
        }
        return vnl;
    }
    
    /** *************************************************************
     * 
     *
     * @param     
     * @return    
     */
    public int getFileSize(RConnection c, String targetFilename){
        dbgLog.fine("targetFilename="+targetFilename);
        int fileSize = 0;
        try {
            String fileSizeLine = "round(file.info('"+targetFilename+"')$size)";
            fileSize = c.eval(fileSizeLine).asInteger();
        } catch (RserveException rse) {
            rse.printStackTrace();
        } catch (REXPMismatchException mme) {
            mme.printStackTrace();
        }
        return fileSize;
    }

    public class RException extends Exception {

	public RException(String msg) {
	    super("\"" + msg + "\"");
	}
    }


    private REXP safeEval(RConnection c, String s) throws  
RserveException,
	RException, REXPMismatchException {
	REXP r = c.eval("try({" + s + "}, silent=TRUE)");
	if (r.inherits("try-error")) {
	    throw new RException(r.asString());
	}
	return r;
    }

    public class DvnRGraphException extends Exception {

	public DvnRGraphException(String msg) {
	    super("DVN RGraph Communication exception: \"" + msg + "\"");
	}
    }



}
