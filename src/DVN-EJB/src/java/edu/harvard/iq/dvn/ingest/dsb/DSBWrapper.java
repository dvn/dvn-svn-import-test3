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
 * DSBWrapper.java
 *
 * Created on November 1, 2006, 3:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.ingest.dsb;

import edu.harvard.iq.dvn.unf.UNF5Util;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.FileMetadata;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.StudyFileEditBean;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.study.TabularDataFile;
import edu.harvard.iq.dvn.core.util.WebStatisticsSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;


import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata.*;


import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import java.util.logging.Logger;

/**
 *
 * @author gdurand
 */
public class DSBWrapper implements java.io.Serializable  {
    
    private static Logger dbgLog = Logger.getLogger(DSBWrapper.class.getPackage().getName());
    private HttpClient client = null;
    
    public static final String DSB_ANALYZE = "Analyze";
    public static final String DSB_INGEST = "Ingest";
    public static final String DSB_CALCULATE_UNF = "CalculateUNF";
    public static final String DSB_GET_ZELIG_CONFIG = "GetZeligConfig";
    public static final String DSB_DISSEMINATE = "Disseminate";
    public static final String DSB_FILE_CONVERSION = "FileConversion";

    private static final String FORMAT_TYPE_TAB = "D01";
    private static final String FORMAT_TYPE_SPLUS = "D02";
    private static final String FORMAT_TYPE_STATA = "D03";
    private static final String FORMAT_TYPE_R = "D04";
    
    
    /** Creates a new instance of DSBWrapper */
    public DSBWrapper() {
    }

    public static boolean useNew(String verb) {
        String useNewProperty = System.getProperty("vdc.dsb.useNew");
        if (useNewProperty != null && verb != null) {
            StringTokenizer st = new StringTokenizer(useNewProperty, "|");
            while ( st.hasMoreTokens() ) {
                if ( st.nextToken().toUpperCase().equals( verb.toUpperCase() ) ) {
                    return true;
                }
            }
        }

        return false;    
    }
    
    private String generateUrl(String verb) throws IOException{
        String dsbHost = System.getProperty("vdc.dsb.host");
        String dsbPort = System.getProperty("vdc.dsb.port");
        
        if (dsbHost != null) {
            if ( dsbPort != null ) {
                return "http://" + dsbHost + ":" + dsbPort + "/VDC/DSB/0.1/" + verb;
            } else {
                return "http://" + dsbHost + "/VDC/DSB/0.1/" + verb;
            }
        } else {
            // fallback to the old-style option:
            dsbHost = System.getProperty("vdc.dsb.url");
            
            if (dsbHost != null) {
                return "http://" + dsbHost + "/VDC/DSB/0.1/" + verb;
            } else {
                throw new IOException("System property \"vdc.dsb.host\" has not been set.");
            }
        }
        
    }
    
    private HttpClient getClient() {
        if (client == null) {
            client = new HttpClient( new MultiThreadedHttpConnectionManager() );
        }
        return client;
    }
    
    private void executeMethod(PostMethod method) throws IOException {
        int state = getClient().executeMethod(method);
        
        if (state != 200) {
            throw new IOException(
                    (method.getStatusLine() != null)
                    ? method.getStatusLine().toString()
                    : "DSB Error");
        }
    }
    
    
    public String analyze(File f) throws IOException{
        BufferedReader rd = null;
        PostMethod method = null;
        
        try {
            String fileType = null;
            
            // create method
            method = new PostMethod(generateUrl(DSB_ANALYZE));
            method.addParameter("file_name", f.getName());
            method.addParameter("file_header", new String(Base64.encodeBase64(getHeaderFromFile(f))) );
            
            // execute
            executeMethod(method);
            
            // parse the response
            rd = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()  ));
            String line;
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
                int startIndex = line.indexOf("<mime>");
                if (startIndex != -1) {
                    int endIndex = line.indexOf("</mime>");
                    fileType = line.substring( startIndex+6,endIndex );
                    //break;
                }
            }
            
            return fileType;
            
        } finally {
            if (method != null) { method.releaseConnection(); }
            try {
                if (rd != null) { rd.close(); }
            } catch (IOException ex) {
            }
        }
    }
    
    private static byte[] getHeaderFromFile(File f) throws IOException{
        FileInputStream fin = null;
        try {
            byte[] header = new byte[1024];
            fin = new FileInputStream(f);
            
            // Get the first 'headerLength' bytes from the file
            if (f.length() > 1024) {
                fin.read(header, 0, 1024);
            } else {
                //byte[] b = new byte[(int) f.length()];
                header = new byte[(int) f.length()];
                //fin.read(b);
                fin.read(header);
            }
            
            return header;
            
        } finally {
            try {
                if (fin != null) { fin.close(); }
            } catch (IOException ex) {
            }
        }
    }
    
    public String ingest(StudyFileEditBean file)throws IOException{
        dbgLog.fine("***** DSBWrapper: ingest(): start *****\n");

       String mime_type = file.getStudyFile().getFileType(); 
       String ddi = null;

       BufferedInputStream infile = null;

   

            // ingest-source file
            File tempFile = new File(file.getTempSystemFileLocation()); // 

            infile = new BufferedInputStream(new FileInputStream(tempFile));

            dbgLog.fine("\nfile mimeType="+mime_type+"\n\n");

            // get available FileReaders for this MIME-type
            Iterator<StatDataFileReader> itr =
                StatDataIO.getStatDataFileReadersByMIMEType(mime_type);

            if (!itr.hasNext()){
                throw new IllegalArgumentException("No FileReader Class found" +
                    " for this mime type="+ mime_type);
            }
            // use the first reader
            StatDataFileReader sdioReader = itr.next();

            dbgLog.fine("reader class name="+sdioReader.getClass().getName());

            SDIOData sd = null;
            if (mime_type != null){
                sd = sdioReader.read(infile);
            } else {
                // fail-safe block if mime_type is null
                // check the format type again and then read the file
                dbgLog.info("mime-type was null: use the back-up method");
                sd = StatDataIO.read(infile);
            }
            
            SDIOMetadata smd = sd.getMetadata();

            // tab-file: source file
            String tabDelimitedDataFileLocation =
                    smd.getFileInformation().get("tabDelimitedDataFileLocation").toString();

            dbgLog.fine("tabDelimitedDataFileLocation="+tabDelimitedDataFileLocation);

            dbgLog.fine("data file(tempFile): abs path:\n"+file.getTempSystemFileLocation());
            dbgLog.fine("mimeType :\n"+file.getStudyFile().getFileType());


            if (infile != null){
                infile.close();
            }

            // parse the response
            StudyFile f = file.getStudyFile();

            // first, check dir
            // create a sub-directory "ingested"
            File newDir = new File(tempFile.getParentFile(), "ingested");

            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            dbgLog.fine("newDir: abs path:\n"+newDir.getAbsolutePath());

            // tab-file case: destination
            File newFile = new File( newDir, tempFile.getName() );

            // nio-based file-copying idiom
            FileInputStream fis = new FileInputStream(tabDelimitedDataFileLocation);
            FileOutputStream fos = new FileOutputStream(newFile);
            FileChannel fcin = fis.getChannel();
            FileChannel fcout = fos.getChannel();
            fcin.transferTo(0, fcin.size(), fcout);
            fcin.close();
            fcout.close();
            fis.close();
            fos.close();

            dbgLog.fine("newFile: abs path:\n"+newFile.getAbsolutePath());

            // store the tab-file location
            file.setIngestedSystemFileLocation(newFile.getAbsolutePath());

            // return xmlToParse;
            DDIWriter dw = new DDIWriter(smd);
            ddi = dw.generateDDI();

       

        return ddi;



    }
    
    public String calculateUNF(StudyVersion sv) throws IOException {
        //TODO: VERSION: (check this)
        List unfs = new ArrayList();
        for (FileMetadata fmd : sv.getFileMetadatas()) {
            StudyFile temp = fmd.getStudyFile();
            if (temp.isUNFable()) {
                unfs.add(temp.getUnf());
            }
        }
        
        if (unfs.size() == 0) {
            // no subsettable files
            return null;
        } else {
            String fileUNF=null;
          
            fileUNF = UNF5Util.calculateUNF(unfs);
            //return calculateUNF(unfs);
          
            return fileUNF;
        }
    }
    
    public String calculateUNF(List unfs) throws IOException{
        PostMethod method = null;
        
        try {
            // create method
            method = new PostMethod(generateUrl(DSB_CALCULATE_UNF));
            Iterator iter = unfs.iterator();
            while (iter.hasNext()) {
                String unf = (String) iter.next();
                method.addParameter("unf", unf);
            }
            
            // execute
            executeMethod(method);
            
            // parse the response
            String unf = method.getResponseBodyAsString();
            if (unf != null) {
                unf = unf.trim();
            }
            return unf;
            
        } finally {
            if (method != null) { method.releaseConnection(); }
        }
    }
    
    public String getZeligConfig() throws IOException{
        PostMethod method = null;
        
        try {
            // create method
            method = new PostMethod(generateUrl(DSB_GET_ZELIG_CONFIG));
            
            // execute
            executeMethod(method);
            
            // parse the response
            String zeligConfig = method.getResponseBodyAsString();
            return zeligConfig;
            
        } finally {
            if (method != null) { method.releaseConnection(); }
        }
    }
    
    public void disseminate(HttpServletResponse res, TabularDataFile tdf, String serverPrefix, String formatType) throws IOException{
        Map parameters = new HashMap();
        List variables = tdf.getDataTable().getDataVariables();
        disseminate(res, parameters, tdf, serverPrefix, variables, formatType );
    }
    
    public void disseminate(HttpServletResponse res, Map parameters, StudyFile sf, String serverPrefix, List variables, String formatType) throws IOException{
        if (parameters == null) {
            parameters = new HashMap();
        }
        parameters.put("dtdwnld", formatType);
        disseminate(res, parameters, sf, serverPrefix, variables);
    }
    
    public void disseminate(HttpServletResponse res, Map parameters, StudyFile sf, String serverPrefix, List variables) throws IOException{
        // add paramters for the file
        if (parameters == null) {
            parameters = new HashMap();
        }
        
        parameters.put("uri", generateUrlForDDI(serverPrefix, sf.getId()));
        parameters.put("URLdata", generateUrlForFile(serverPrefix, sf.getId()));
        parameters.put("fileid", "f" + sf.getId().toString());
        parameters.put("varbl", generateVariableListForDisseminate( variables ) );
        
        disseminate(res, parameters);
    }
    
    public void disseminate(HttpServletResponse res, Map parameters) throws IOException{
        PostMethod method = null;
        InputStream in = null;
        OutputStream out = null;
        
        try {
            // create method
            method = new PostMethod(generateUrl(DSB_DISSEMINATE));
            
            // generate parameters
            Iterator iter = parameters.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                Object value = parameters.get(key);
                
                if (value instanceof String) {
                    method.addParameter(key, (String) value);
                } else if (value instanceof List) {
                    Iterator valueIter = ((List) value).iterator();
                    while (valueIter.hasNext()) {
                        String item = (String) valueIter.next();
                        method.addParameter(key, (String) item);
                    }
                }
            }
            
            
            String debug = "Disseminate - Method Parameters:\n";
            for (int i = 0; i < method.getParameters().length; i++) {
                debug += "\n" + method.getParameters()[i].getName() + " = '";
                debug += method.getParameters()[i].getValue() + "'";
            }
            System.out.println(debug);
            HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
            if (request.getHeader("X-Forwarded-For") != null)
                method.addRequestHeader("X-Forwarded-For", request.getHeader("X-Forwarded-For"));
            else
                method.addRequestHeader("X-Forwarded-For", "NULL-HEADER-X-FORWARDED-FOR");
            // execute
            executeMethod(method);
            
            debug = "Disseminate - Recycled response headers:\n";
            // set headers on the response
            for (int i = 0; i < method.getResponseHeaders().length; i++) {
                String headerName = method.getResponseHeaders()[i].getName();
                if (headerName.startsWith("Content")) {
                    String headerValue = method.getResponseHeaders()[i].getValue();
                    debug += "\n" + headerName + " = '" + headerValue + "'";
                    res.setHeader(headerName, headerValue);
                }
            }
            System.out.println(debug);
            
            in = method.getResponseBodyAsStream();
            out = res.getOutputStream();
            
            
            byte[] dataBuffer = new byte[8192];
            
            int i = 0;
            while ( ( i = in.read(dataBuffer) ) > 0 ) {
                out.write(dataBuffer,0,i);
                out.flush();
            }
            
            
            
        } finally {
            if (method != null) { method.releaseConnection(); }
            if (in != null) { in.close(); }
            if (out != null) { out.close(); }
        }
    }
    
    public List generateVariableListForDisseminate(List dvs) {
        List variableList = new ArrayList();
        if (dvs != null) {
            Iterator iter = dvs.iterator();
            while (iter.hasNext()) {
                DataVariable dv = (DataVariable) iter.next();
                variableList.add("v" + dv.getId());
            }
        }
        return variableList;
    }
    
    private String generateUrlForDDI(String serverPrefix, Long fileId) {
        String studyDDI = serverPrefix + "/ddi/?fileId=" + fileId;
        System.out.println(studyDDI);
        return studyDDI;
    }
    
    private String generateUrlForFile(String serverPrefix, Long fileId) {
        //get the xFF arg -- used by the web stats txt report
        WebStatisticsSupport webstatistics = new WebStatisticsSupport();
        int headerValue = webstatistics.getParameterFromHeader("X-Forwarded-For");
        String xff = webstatistics.getQSArgument("xff", headerValue);
        String file = serverPrefix + "/FileDownload/?fileId=" + fileId + "&isSSR=1" + xff;
        System.out.println(file);
        return file;
    }

    public static boolean isDSBRequest(HttpServletRequest req) {
        boolean dsbRequest = false;
        
        String dsbHost = System.getProperty("vdc.dsb.host");

        if ( dsbHost == null ) {
            // vdc.dsb.host isn't set; 
            // fall back to the old-style option: 
            dsbHost = System.getProperty("vdc.dsb.url");
        }          


        if ( dsbHost.equals(req.getRemoteHost()) ) {
            dsbRequest = true; 
        } else { 
            try {
                String dsbHostIPAddress = InetAddress.getByName(dsbHost).getHostAddress(); 
                if ( dsbHostIPAddress.equals(req.getRemoteHost()) ) {
                    dsbRequest = true;
                }
            } catch ( UnknownHostException ex ) {
                // do nothing; 
                // the "vdc.dsb.host" setting is clearly misconfigured,
                // so we just keep assuming this is NOT a DSB call
            }
        }

        return dsbRequest;
       
    }
}
