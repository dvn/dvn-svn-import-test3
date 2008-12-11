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
 * FileDownloadServlet.java
 *
 * Created on October 12, 2006, 6:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.hmdc.vdcnet.web.servlet;

import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;

import edu.harvard.hmdc.vdcnet.study.FileCategory;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.study.RemoteAccessAuth;
import edu.harvard.hmdc.vdcnet.study.DataVariable;
import edu.harvard.hmdc.vdcnet.study.VariableCategory;

import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;

import edu.harvard.hmdc.vdcnet.web.study.StudyUI;
import edu.harvard.hmdc.vdcnet.web.study.FileCategoryUI;
import edu.harvard.hmdc.vdcnet.web.dvnremote.DvnTermsOfUseAccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.URLEncoder;
import javax.ejb.EJB;
import javax.ejb.EJBException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;


import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.apache.commons.lang.StringUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.*;

import edu.harvard.hmdc.vdcnet.dsb.*;
import edu.harvard.hmdc.vdcnet.dsb.impl.*;
import java.io.BufferedOutputStream;
import java.util.logging.Logger;

/**
 *
 * @author gdurand
 * @author landreev
 */
public class FileDownloadServlet extends HttpServlet {

    /** Creates a new instance of FileDownloadServlet */
    public FileDownloadServlet() {
    }

    private HttpClient getClient() {
        return new HttpClient();
    }
    
    private List<DataVariable> dataVariables = new ArrayList<DataVariable>();

    /**
     * The hash table (variable Id to variable name)
     * that stores the current selected variables
     */
    public Map<String, String> varCart = new LinkedHashMap<String, String>();

    /** Sets the logger (use the package name) */
    private static Logger dbgLog = Logger.getLogger(FileDownloadServlet.class.getPackage().getName());

    private String studyUIclassName = "edu.harvard.hmdc.vdcnet.web.study.StudyUI";
    
    
    /** The citation information as a String */
    private String citation;
    
    /** The title of the requested study */
    private String studyTitle;
    
    /** The ID of the requested study */
    private Long studyId;
    
    /** The URL of the requested study */
    private String studyURL;
    
    private static Map <String, String> formatRequestedMIMEtypeMap =
        new LinkedHashMap<String, String>();

    static {
        formatRequestedMIMEtypeMap.put("D00", "text/tab-separated-values");
        formatRequestedMIMEtypeMap.put("D01", "text/tab-separated-values");
        formatRequestedMIMEtypeMap.put("D02", "application/x-rlang-transport");
        formatRequestedMIMEtypeMap.put("D03", "application/x-stata-7");
        formatRequestedMIMEtypeMap.put("D04", "application/x-R-2");
    }



    private String generateDisseminateUrl() throws IOException {
        String dsbHost = System.getProperty("vdc.dsb.host");
        String dsbPort = System.getProperty("vdc.dsb.port");

        if (dsbHost != null) {
            if (dsbPort != null) {
                return "http://" + dsbHost + ":" + dsbPort + "/VDC/DSB/0.1/Disseminate";
            } else {
                return "http://" + dsbHost + "/VDC/DSB/0.1/Disseminate";
            }
        } else {
            // fall back to the old "vdc.dsb.url" option
            dsbHost = System.getProperty("vdc.dsb.url");

            if (dsbHost != null) {
                return "http://" + dsbHost + "/VDC/DSB/0.1/Disseminate";
            } else {
                throw new IOException("System property \"vdc.dsb.host\" has not been set.");
            }
        }

    }
    @EJB
    StudyServiceLocal studyService;
    @EJB
    VDCServiceLocal vdcService;



    public void service(HttpServletRequest req, HttpServletResponse res) {
        VDCUser user = null;
        if (LoginFilter.getLoginBean(req) != null) {
            user = LoginFilter.getLoginBean(req).getUser();
        }
        VDC vdc = vdcService.getVDCFromRequest(req);
        UserGroup ipUserGroup = null;
        if (req.getSession(true).getAttribute("ipUserGroup") != null) {
            ipUserGroup = (UserGroup) req.getSession().getAttribute("ipUserGroup");
        }
        String fileId = req.getParameter("fileId");
        String formatRequested = req.getParameter("format");
        String downloadOriginalFormat = req.getParameter("downloadOriginalFormat");
        String imageThumb = req.getParameter("imageThumb");
        String noVarHeader = req.getParameter("noVarHeader");

// v1.4 changes: start
        dbgLog.fine("*****  FileDownLoadServlet : service() starts here *****");
        dbgLog.fine("*****  v1.4 lines start here *****");

        /**
         * The name of StudyUI class that contains the citation-related 
         * information about the requested Study
         */
        Enumeration emso =  req.getSession().getAttributeNames();
        dbgLog.fine("session names="+emso);

        Map<String, Object> sessionMap = new HashMap<String, Object>();
        while(emso.hasMoreElements()){
            String nm = (String) emso.nextElement();
            sessionMap.put(nm, req.getSession().getAttribute(nm));
        }
        dbgLog.fine("sessionMap="+sessionMap);
        
        // Stores the title, ID, and citation data of the requested study
        if (sessionMap.containsKey(studyUIclassName)) {

            StudyUI sui = (StudyUI) sessionMap.get(studyUIclassName);

            // Stores the title, Id, and Citation of the requested study
            studyTitle = sui.getStudy().getTitle();
            studyId = sui.getStudy().getId();
            citation = sui.getStudy().getCitation(false);
            
            dbgLog.fine("StudyUIclassName was found in the session Map");
            dbgLog.fine("Study Title="+studyTitle);
            dbgLog.fine("Study Id="+studyId);
            dbgLog.fine("Citation="+citation);
        } else {
            dbgLog.fine("StudyUIclassName was not in the session Map");
        }

// v1.4 chagnes: end



        if (fileId != null && (!fileId.contains(","))) {

            StudyFile file = null;

            try {
                file = studyService.getStudyFile(new Long(fileId));

            } catch (Exception ex) {
                if (ex.getCause() instanceof IllegalArgumentException) {
                    createErrorResponse404(res);
                    return;
                }
            }

            if (file == null) {
                // this check is probably unnecessary, as a non-existing file
                // would already have produced the exception above (??)
                // -- not sure, gotta ask Gustavo

                createErrorResponse404(res);
                return;
            }

            // perform access authorization check:

            String dsbHost = System.getProperty("vdc.dsb.host");

            if (dsbHost == null) {
                // vdc.dsb.host isn't set;
                // fall back to the old-style option:
                dsbHost = System.getProperty("vdc.dsb.url");
            }

            boolean NOTaDSBrequest = true;

            String localHostByName = "localhost";
            String localHostNumeric = "127.0.0.1";

            if (dsbHost.equals(req.getRemoteHost()) ||
                localHostByName.equals(req.getRemoteHost()) ||
                localHostNumeric.equals(req.getRemoteHost())) {
                NOTaDSBrequest = false;
            } else {
                try {
                    String dsbHostIPAddress = InetAddress.getByName(dsbHost).getHostAddress();
                    if (dsbHostIPAddress.equals(req.getRemoteHost())) {
                        NOTaDSBrequest = false;
                    }
                } catch (UnknownHostException ex) {
                    // do nothing;
                    // the "vdc.dsb.host" setting is clearly misconfigured,
                    // so we just keep assuming this is NOT a DSB call
                }
            }

            if (NOTaDSBrequest && file.isFileRestrictedForUser(user, vdc, ipUserGroup)) {
                // generate a response with a correct 403/FORBIDDEN code
                createErrorResponse403(res);
                return;
            }


            // determine if the fileId represents a local object
            // or a remote URL

            if (file.isRemote()) {

// After v1.4, 
// the file-access part of the former non-format-conversion segment 
// becomes a common block for both conversion and non-conversion cases
                    dbgLog.fine(" ***** v1.4 code block : remote file case starts here *****");

                    DvnRJobRequest sro = null;
                    Map<String, List<String>> paramListToR = null;
                    Map<String, Map<String, String>> vls = null;
                    if (formatRequested != null) {
                        // remote file: case: ver1.4-addition starts here ########
                        dbgLog.fine(" ***** remote: set-up block for format conversion cases *****");

                        paramListToR = new HashMap<String, List<String>>();

                        paramListToR.put("dtdwnld", Arrays.asList(formatRequested));            
                        dbgLog.fine("remote: citation info to be sent:\n" + citation);

                        paramListToR.put("studytitle", Arrays.asList(studyTitle));
                        paramListToR.put("studyno", Arrays.asList(studyId.toString()));
                        paramListToR.put("studyURL", Arrays.asList(studyURL));
                        paramListToR.put("requestType", Arrays.asList("Download"));

                        dataVariables = file.getDataTable().getDataVariables();
                        vls = getValueTablesForAllRequestedVariables();
                        dbgLog.fine("remote: variables(getDataVariableForRequest())="+getDataVariableForRequest()+"\n");
                        dbgLog.fine("remote: value table(vls)="+vls+"\n");

                    }
                
                
                    // common(file-access) block for both cases
                
                    String remoteFileUrl = file.getFileSystemLocation();
                    
                    
                    GetMethod method = null;
                    int status = 200;

                    try {

                        if (remoteFileUrl != null) {
                            remoteFileUrl = remoteFileUrl.replaceAll(" ", "+");
                        }

                        // If it's another DVN from which we are getting
                        // the file, we need to pass the "noVarHeader"
                        // argument along:

                        if (noVarHeader != null) {
                            if (remoteFileUrl.matches(".*FileDownload.*")) {
                                remoteFileUrl = remoteFileUrl + "&noVarHeader=1";
                            }
                        }

                        // See if remote authentication is required;

                        String remoteHost = null;
                        String regexRemoteHost = "https*://([^/]*)/";
                        Pattern patternRemoteHost = Pattern.compile(regexRemoteHost);
                        Matcher hostMatcher = patternRemoteHost.matcher(remoteFileUrl);

                        if (hostMatcher.find()) {
                            remoteHost = hostMatcher.group(1);
                        }

                        String jsessionid = null;
                        String remoteAuthHeader = null;

                        String remoteAuthType = remoteAuthRequired(remoteHost);

                        if (remoteAuthType != null) {
                            if (remoteAuthType.equals("httpbasic")) {
                                // get the basic HTTP auth credentials
                                // (password and username) from the database:

                                remoteAuthHeader = getRemoteAuthCredentials(remoteHost);
                            } else if (remoteAuthType.equals("dvn")) {
                                // Authenticate with the remote DVN:

                                jsessionid = dvnRemoteAuth(remoteHost);
                            }
                        }

                        method = new GetMethod(remoteFileUrl);

                        if (jsessionid != null) {
                            method.addRequestHeader("Cookie", "JSESSIONID=" + jsessionid);
                        }

                        if (remoteAuthHeader != null) {
                            method.addRequestHeader("Authorization", remoteAuthHeader);
                        }

                        // normally, the HTTP client follows redirects
                        // automatically, so we need to explicitely tell it
                        // not to:

                        method.setFollowRedirects(false);
                        status = getClient().executeMethod(method);

                        // The code below is to enable the click through
                        // Terms-of-Use Agreement.
                        // We are assuming that if they have gotten here,
                        // they must have already clicked on all the
                        // licensing agreement forms (the terms-of-use
                        // agreements are preserved in the study DDIs as
                        // they are exported and harvested between DVNs).

                        // There are obvious dangers in this approach.
                        // We have to trust the DVN harvesting from us to display
                        // the agreements in question to their users. But since
                        // terms/restrictions cannot be disabled on harvested
                        // content through the normal DVN interface, so they
                        // would have to go directly to the database to do so,
                        // which would constitute an obvious "hacking" of the
                        // mechanism, (hopefully) making them and not us liable
                        // for it.

                        if (status == 302) {
                            // this is a redirect.

                            // let's see where it is redirecting us; if it looks like
                            // DVN TermsOfUse page, we'll "click" and submit the form,
                            // then we'll hopefully be able to download the file.
                            // If it's no the TOU page, we are just going to try to
                            // follow the redirect and hope for the best.
                            // (A good real life example is the Census archive: the
                            // URLs for their objects that they give us are actually
                            // aliases that are 302-redirected to the actual locations)

                            String redirectLocation = null;

                            for (int i = 0; i < method.getResponseHeaders().length; i++) {
                                String headerName = method.getResponseHeaders()[i].getName();
                                if (headerName.equals("Location")) {
                                    redirectLocation = method.getResponseHeaders()[i].getValue();
                                }
                            }

                            if (redirectLocation.matches(".*TermsOfUsePage.*")) {

                                // Accept the TOU agreement:

				method = remoteAccessTOU(redirectLocation, jsessionid, remoteFileUrl);

                                // If everything has worked right
                                // we should be redirected to the final
                                // download URL, and the method returned is
                                // an established download connection;

                                if (method != null) {
                                    status = method.getStatusCode();
                                } else {
                                    // but if something went wrong in the progress,
                                    // we just report that we couldn't find
                                    // the file:
                                    status = 404;
                                }

                            } else {
                                // just try again (and hope for the best!)
                                method = new GetMethod(redirectLocation);
                                status = getClient().executeMethod(method);
                            }
                        }

                    } catch (IOException ex) {
                        // return 404
                        // and generate a FILE NOT FOUND message

                        createErrorResponse404(res);
                        if (method != null) {
                            method.releaseConnection();
                        }
                        return;
                    }


                    if (status == 403) {
                        // generate an HTML-ized response with a correct
                        // 403/FORBIDDEN code

                        createErrorResponse403(res);
                        if (method != null) {
                            method.releaseConnection();
                        }
                        return;
                    }

                    if (status == 404) {
                        // generate an HTML-ized response with a correct
                        // 404/FILE NOT FOUND code

                        createErrorResponse404(res);
                        if (method != null) {
                            method.releaseConnection();
                        }
                        return;
                    }

                    // a generic response for all other failure cases:

                    if (status != 200) {
                        createErrorResponseGeneric(res, status, (method.getStatusLine() != null)
                            ? method.getStatusLine().toString()
                            : "Unknown HTTP Error has occured.");
                        if (method != null) {
                            method.releaseConnection();
                        }
                        return;
                    }

                    Boolean ExternalHTMLpage = false;

                    try {
                        // recycle the Content-* headers from the incoming HTTP stream:

                        for (int i = 0; i < method.getResponseHeaders().length; i++) {
                            String headerName = method.getResponseHeaders()[i].getName();
                            if (headerName.startsWith("Content")) {

                                // Special treatment case for remote
                                // HTML pages:
                                // if it looks like HTML, we redirect to
                                // that page, instead of trying to display it:
                                // (this is for cases like the harvested HGL
                                // documents which contain URLs pointing to
                                // dynamic content pages, not to static files.

                                if (headerName.equals("Content-Type") &&
                                    method.getResponseHeaders()[i].getValue() != null &&
                                    method.getResponseHeaders()[i].getValue().startsWith("text/html")) {
                                    //String remoteFileUrl = file.getFileSystemLocation();
                                    createRedirectResponse(res, remoteFileUrl);
                                    //studyService.incrementNumberOfDownloads(file.getFileCategory().getStudy().getId());
                                    studyService.incrementNumberOfDownloads(file.getId());
                                    method.releaseConnection();
                                    return;
                                }
                                res.setHeader(method.getResponseHeaders()[i].getName(), method.getResponseHeaders()[i].getValue());
                            }
                        }
                        
                        // non-converion/conversion-specific logic starts here
                        
                        if (formatRequested == null) {
                            // non-conversion case: start
                            try {
                                // send the incoming HTTP stream as the response body

                                InputStream in = method.getResponseBodyAsStream();
                                OutputStream out = res.getOutputStream();
                                //WritableByteChannel out = Channels.newChannel (res.getOutputStream());

                                byte[] dataReadBuffer = new byte[4 * 8192];
                                //ByteBuffer dataWriteBuffer = ByteBuffer.allocate ( 4 * 8192 );

                                int i = 0;
                                while ((i = in.read(dataReadBuffer)) > 0) {
                                    //dataWriteBuffer.put ( dataReadBuffer );
                                    //out.write(dataWriteBuffer);
                                    out.write(dataReadBuffer, 0, i);
                                    //dataWriteBuffer.rewind ();
                                    out.flush();
                                }

                                in.close();
                                out.close();

                                //studyService.incrementNumberOfDownloads(file.getFileCategory().getStudy().getId());
                                studyService.incrementNumberOfDownloads(file.getId());
                            
                            } catch (IOException ex) {
                                //ex.printStackTrace();
                                String errorMessage = "An unknown I/O error has occured while attempting to retreive a remote data file (i.e., a file that belongs to a harvested study). It is possible that it is temporarily unavailable from that location or perhaps a temporary network error has occured. Please try again later and if the problem persists, report it to your DVN technical support contact.";
                                createErrorResponseGeneric(res, 0, errorMessage);

                            }
                            
                        // end: non-conversion case
                        } else {
                        // start: conversion case

                            // save the incoming stream as a temp file
                            InputStream in = method.getResponseBodyAsStream();
                             // temp data file that stores incoming data
                            File inFile = File.createTempFile("tempTabfile.", ".tab");
                            OutputStream out = new BufferedOutputStream(new FileOutputStream(inFile));
                            
                            int bufsize;
                            byte [] bffr = new byte[4*8192];
                            while ((bufsize = in.read(bffr))!=-1) {
                                out.write(bffr, 0, bufsize);
                            }
                            
                            in.close();
                            out.close();

                            // Checks the resulting subset file 
                            if (inFile.exists()){
                                Long subsetFileSize = inFile.length();
                                dbgLog.fine("data file:Length="+subsetFileSize);
                                dbgLog.fine("data file:name="+inFile.getAbsolutePath());

                                if (subsetFileSize > 0){
                                    paramListToR.put("subsetFileName", Arrays.asList(inFile.getAbsolutePath()));
                                    paramListToR.put("subsetDataFileName",Arrays.asList(inFile.getName()));
                                } else {
                                    // subset file exists but it is empty
                                    dbgLog.warning("exiting service() due to a file error:"+
                                    "a source file is empty");
                                    return;
                                }
                            } else {
                                // subset file was not created
                                dbgLog.warning("exiting service() due to a file error:"+
                                "a source file was not created");
                                return;
                            }

                            dbgLog.fine("local: paramListToR="+paramListToR);

                            sro = new DvnRJobRequest(getDataVariableForRequest(), paramListToR, vls);
                            dbgLog.fine("sro dump:\n"+ToStringBuilder.reflectionToString(sro, ToStringStyle.MULTI_LINE_STYLE));

                            // create the service instance
                            DvnRforeignFileConversionServiceImpl dfcs = new DvnRforeignFileConversionServiceImpl();

                            // execute the service
                            Map<String, String> resultInfo = new HashMap<String, String>();
                            resultInfo = dfcs.execute(sro);
                            dbgLog.fine("resultInfo="+resultInfo+"\n");

                            File frmtCnvrtdFile = null;
                            // check whether a requested file is actually created
                            if (resultInfo.get("RexecError").equals("true")){
                                dbgLog.fine("exiting dwnldAction() due to an R-runtime error");
                            } else {
                                // (2)The format-converted subset data file
                                // get the path-name of the data-file to be delivered 
                                String wbDataFileName = resultInfo.get("wbDataFileName");
                                dbgLog.fine("wbDataFileName="+wbDataFileName);

                                frmtCnvrtdFile = new File(wbDataFileName);
                                if (frmtCnvrtdFile.exists()){
                                    dbgLog.fine("frmtCnvrtdFile:length="+frmtCnvrtdFile.length());
                                } else {
                                    // the data file was not created
                                    dbgLog.fine("frmtCnvrtdFile does not exist");
                                    dbgLog.warning("exiting service: format-converted data file was not transferred");
                                    return;
                                }
                            }
                            
                            try {
                                res.setContentType(formatRequestedMIMEtypeMap.get(formatRequested));
                                res.setHeader("content-disposition",
                                    "attachment; filename=" +
                                    frmtCnvrtdFile.getName());
                                InputStream infc = new FileInputStream(resultInfo.get("wbDataFileName"));
                                OutputStream outfc = res.getOutputStream();
                                
                                byte[] dataReadBuffer = new byte[8192 * 4];
                                
                                // One special case:
                                // With fixed-field files we support requesting the
                                // file in tab-delimited format. And for tab files
                                // we always want to add the variable names header
                                // line:

                                String varHeaderLine = null;

                                if (formatRequested.equals("D00") && noVarHeader == null) {
                                    List dataVariables = file.getDataTable().getDataVariables();
                                    varHeaderLine = generateVariableHeader(dataVariables);
                                    if (varHeaderLine != null) {
                                        byte[] varHeaderBuffer = null;
                                        varHeaderBuffer = varHeaderLine.getBytes();
                                        out.write(varHeaderBuffer);
                                        out.flush();
                                    }
                                }

                                int i = 0;
                                while ((i = infc.read(dataReadBuffer)) > 0) {
                                    outfc.write(dataReadBuffer, 0, i);
                                    outfc.flush();
                                }
                                
                                infc.close();
                                outfc.close();


                            } catch (IOException ex) {
                                //ex.printStackTrace();
                                String errorMessage = "An unknown I/O error has occured while trying to perform a format conversion on a locally-stored data file. Unfortunately, no extra diagnostic information on the nature of the problem is available to the application. It is possible that it was caused by a temporary network error as the Application was communicating to the Data Services Broker. Please try again later and if the problem persists, report it to your DVN technical support contact.";
                                createErrorResponseGeneric(res, 0, errorMessage);

                            }
                            // end: conversion case:
                        }


                    } catch (IOException ex) {
                        //ex.printStackTrace();
                        String errorMessage = "An unknown I/O error has occured while attempting to retreive a remote data file (i.e., a file that belongs to a harvested study). It is possible that it is temporarily unavailable from that location or perhaps a temporary network error has occured. Please try again later and if the problem persists, report it to your DVN technical support contact.";
                        createErrorResponseGeneric(res, 0, errorMessage);
                    }

                    method.releaseConnection();

// end of remote-file cases
            } else {
                // local object

                //studyService.incrementNumberOfDownloads(file.getFileCategory().getStudy().getId());

                if (formatRequested != null) {

                    // user requested the file in a non-default (i.e.,
                    // not tab-delimited) format.

                    // First, let's check if we have this file cached.

                    String cachedFileSystemLocation = file.getFileSystemLocation() + "." + formatRequested;

                    if (new File(cachedFileSystemLocation).exists()) {
                        try {
                            String cachedAltFormatType = generateAltFormat(formatRequested);
                            String cachedAltFormatFileName = generateAltFileName(formatRequested, "f" + file.getId().toString());

                            res.setHeader("Content-disposition",
                                "attachment; filename=\"" + cachedAltFormatFileName + "\"");
                            res.setHeader("Content-Type",
                                cachedAltFormatType + "; name=\"" + cachedAltFormatFileName + "\"; charset=ISO-8859-1");


                            // send the file as the response

                            FileChannel in = new FileInputStream(new File(cachedFileSystemLocation)).getChannel();
                            WritableByteChannel out = Channels.newChannel(res.getOutputStream());

                            long bytesPerIteration = 4 * 8192;
                            long start = 0;

                            while (start < in.size()) {
                                in.transferTo(start, bytesPerIteration, out);
                                start += bytesPerIteration;
                            }

                            in.close();
                            out.close();


                        } catch (IOException ex) {
                            //ex.printStackTrace();
                            String errorMessage = "An unknown I/O error has occured while trying to serve a locally stored data file in an alternative format. It. It could also be a result of a temporary network error. Please try again later, and if the problem persists report it to your DVN technical support contact.";
                            createErrorResponseGeneric(res, 0, errorMessage);

                        }

                    } else {
                        
// local file: format conversion case: ver1.4-changes start here ##########

                        dbgLog.fine("***** v1.4 code-block : local file: format conversion case starts here *****");
                        
                        DvnRJobRequest sro = null;
                        Map<String, List<String>> paramListToR = new HashMap<String, List<String>>();
                        
                        paramListToR.put("dtdwnld", Arrays.asList(formatRequested));            
                        dbgLog.fine("local: citation info to be sent:\n" + citation);

                        paramListToR.put("studytitle", Arrays.asList(studyTitle));
                        paramListToR.put("studyno", Arrays.asList(studyId.toString()));
                        paramListToR.put("studyURL", Arrays.asList(studyURL));
                        paramListToR.put("requestType", Arrays.asList("Download"));

                        dbgLog.fine("remote: paramListToR="+paramListToR);


/* pre-v1.4 code
                        // we are going to send a format conversion request to
                        // the DSB via HTTP.

                        Map parameters = new HashMap();
                        

                        parameters.put("dtdwnld", formatRequested);

                        String serverPrefix = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();

                        parameters.put("uri", generateUrlForDDI(serverPrefix, file.getId()));

                        parameters.put("URLdata", generateUrlForFile(serverPrefix, file.getId()));
                        parameters.put("fileid", "f" + file.getId().toString());

                        // We are requesting a conversion of the whole datafile.
                        // I was positive there was a simple way to ask univar (Disseminate)
                        // for "all" variables; but I'm not so sure anymore. So
                        // far I've only been able to do this by listing all the
                        // variables in the datafile and adding them to the request.
                        // :( -- L.A.

                        List variables = file.getDataTable().getDataVariables();
                        parameters.put("varbl", generateVariableListForDisseminate(variables));
                        parameters.put("wholefile", "true");                        
*/
                        
                        dataVariables = file.getDataTable().getDataVariables();
                        Map<String, Map<String, String>> vls = getValueTablesForAllRequestedVariables();
                        dbgLog.fine("local: variables(getDataVariableForRequest())="+getDataVariableForRequest()+"\n");
                        dbgLog.fine("local: value table(vls)="+vls+"\n");
                        
                        File inFile = new File(file.getFileSystemLocation());
                        dbgLog.fine("source file:name="+inFile.getAbsolutePath());
                        
                        // Checks the resulting subset file 
                        if (inFile.exists()){
                            Long subsetFileSize = inFile.length();
                            dbgLog.fine("data file:Length="+subsetFileSize);
                            dbgLog.fine("data file:name="+inFile.getAbsolutePath());

                            if (subsetFileSize > 0){
                                paramListToR.put("subsetFileName", Arrays.asList(inFile.getAbsolutePath()));
                                paramListToR.put("subsetDataFileName",Arrays.asList(inFile.getName()));
                            } else {
                                // subset file exists but it is empty
                                dbgLog.warning("exiting service() due to a file error:"+
                                "a source file is empty");
                                return;
                            }
                        } else {
                            // subset file was not created
                            dbgLog.warning("exiting service() due to a file error:"+
                            "a source file was not created");
                            return;
                        }
                        
                        dbgLog.fine("local: paramListToR="+paramListToR);
                        
                        sro = new DvnRJobRequest(getDataVariableForRequest(), paramListToR, vls);
                        dbgLog.fine("sro dump:\n"+ToStringBuilder.reflectionToString(sro, ToStringStyle.MULTI_LINE_STYLE));
                        
                        // create the service instance
                        DvnRforeignFileConversionServiceImpl dfcs = new DvnRforeignFileConversionServiceImpl();
                        
                        // execute the service
                        Map<String, String> resultInfo = new HashMap<String, String>();
                        resultInfo = dfcs.execute(sro);
                        dbgLog.fine("resultInfo="+resultInfo+"\n");
                        
                        File frmtCnvrtdFile = null;
                        // check whether a requested file is actually created
                        if (resultInfo.get("RexecError").equals("true")){
                            dbgLog.fine("exiting dwnldAction() due to an R-runtime error");
                        } else {
                            // (2)The format-converted subset data file
                            // get the path-name of the data-file to be delivered 
                            String wbDataFileName = resultInfo.get("wbDataFileName");
                            dbgLog.fine("wbDataFileName="+wbDataFileName);

                            frmtCnvrtdFile = new File(wbDataFileName);
                            if (frmtCnvrtdFile.exists()){
                                dbgLog.fine("frmtCnvrtdFile:length="+frmtCnvrtdFile.length());
//                                deleteTempFileList.add(frmtCnvrtdFile);
//                                zipFileList.add(frmtCnvrtdFile);

                            } else {
                                // the data file was not created
                                dbgLog.fine("frmtCnvrtdFile does not exist");
                                dbgLog.warning("exiting service: format-converted data file was not transferred");
                                return;
                            }
                        }
                       
                        try {

// v1.4 change: start
                            res.setContentType(formatRequestedMIMEtypeMap.get(formatRequested));
                            res.setHeader("content-disposition",
                                "attachment; filename=" +
                                frmtCnvrtdFile.getName());
                            InputStream in = new FileInputStream(resultInfo.get("wbDataFileName"));
// v1.4 change: end

                            // send the incoming HTTP stream as the response body
//                            InputStream in = method.getResponseBodyAsStream();
                            OutputStream out = res.getOutputStream();

                            // Also, we want to cache this file for future use:

                            FileOutputStream fileCachingStream = new FileOutputStream(cachedFileSystemLocation);

                            byte[] dataBuffer = new byte[8192];

                            int i = 0;
                            while ((i = in.read(dataBuffer)) > 0) {
                                out.write(dataBuffer, 0, i);
                                fileCachingStream.write(dataBuffer, 0, i);
                                out.flush();
                            }
                            in.close();
                            out.close();
                            fileCachingStream.flush();
                            fileCachingStream.close();

                        } catch (IOException ex) {
                            //ex.printStackTrace();
                            String errorMessage = "An unknown I/O error has occured while trying to perform a format conversion on a locally-stored data file. Unfortunately, no extra diagnostic information on the nature of the problem is available to the application. It is possible that it was caused by a temporary network error as the Application was communicating to the Data Services Broker. Please try again later and if the problem persists, report it to your DVN technical support contact.";
                            createErrorResponseGeneric(res, 0, errorMessage);

                        }

//                        method.releaseConnection();
                    }
// local file: format-conversion case: ver1.4-changes end here ###############

                } else {

                    // finally, the *true* local case, where we just
                    // read the file off disk and send the stream back.

                    try {
                        // set content type to that stored in the db,
                        // if available.

                        String dbContentType = null;

                        if (downloadOriginalFormat != null) {
                            dbContentType = file.getOriginalFileType();
                        } else {
                            dbContentType = file.getFileType();
                        }


                        // specify the file name, if available:
                        String dbFileName = file.getFileName();

                        if (dbFileName != null && downloadOriginalFormat != null) {
                            if (dbContentType != null) {
                                String origFileExtension = generateOriginalExtension(dbContentType);
                                dbFileName = dbFileName.replaceAll(".tab$", origFileExtension);
                            } else {
                                dbFileName = dbFileName.replaceAll(".tab$", "");
                            }
                        }

                        // open the appropriate physical file

                        File inFile = null;
                        String varHeaderLine = null;

                        // but first, see if they have requested a
                        // thumbnail for an image, or if it's a request
                        // for the datafile in the "original format":

                        if (imageThumb != null && dbContentType.substring(0, 6).equalsIgnoreCase("image/")) {
                            if (generateImageThumb(file.getFileSystemLocation())) {
                                inFile = new File(file.getFileSystemLocation() + ".thumb");
                                dbContentType = "image/png";
                            }
                        } else {
                            inFile = new File(file.getFileSystemLocation());

                            if (downloadOriginalFormat != null) {
                                inFile = new File(inFile.getParent(), "_" + file.getFileSystemName());
                            } else {
                                if (dbContentType != null && dbContentType.equals("text/tab-separated-values") && file.isSubsettable() && noVarHeader == null) {
                                    List datavariables = file.getDataTable().getDataVariables();
                                    varHeaderLine = generateVariableHeader(datavariables);
                                }
                            }
                        }

                        // send the file as the response

                        // InputStream in = new FileInputStream(inFile);

                        // we want to open the actual physical file *before*
                        // we set content-type headers; otherwise, if the file
                        // is corrupt/damaged and can't be open, the exception
                        // will be caught and the error message will be printed,
                        // but the browser will already be told to expect whatever
                        // content-type that file was supposed to be.

                        FileChannel in = new FileInputStream(inFile).getChannel();

                        if (dbFileName != null) {
                            if (dbContentType != null) {

                                // The "content-disposition" header is for the
                                // Mozilla family of browsers;

                                // about the commented out code below:
                                //
                                // the idea used to be that we should prompt
                                // for the content to open in the browser
                                // whenever possible; but lately it's been
                                // suggested by our users that all downloads
                                // should behave the same, i.e. prompt the
                                // user to "save as" the file.

                                //if ( dbContentType.equalsIgnoreCase("application/pdf") ||
                                //     dbContentType.equalsIgnoreCase("text/xml") ||
                                //     dbContentType.equalsIgnoreCase("text/plain"))  {
                                //    res.setHeader ( "Content-disposition",
                                //          "inline; filename=\"" + dbFileName + "\"" );
                                //
                                //} else {

                                res.setHeader("Content-disposition",
                                    "attachment; filename=\"" + dbFileName + "\"");
                                //}

                                // And this one is for MS Explorer:
                                res.setHeader("Content-Type",
                                    dbContentType + "; name=\"" + dbFileName + "\"; charset=ISO-8859-1");

                            } else {
                                // Have filename, but no content-type;
                                // All we can do is provide a Mozilla-friendly
                                // header:

                                res.setHeader("Content-disposition",
                                    "attachment; filename=\"" + dbFileName + "\"");
                            }
                        } else {
                            // no filename available;
                            // but if we have content-type in the database
                            // we'll just set that:

                            if (dbContentType != null) {
                                res.setContentType(dbContentType);
                            }
                        }

                        // OutputStream out = res.getOutputStream();

                        WritableByteChannel out = Channels.newChannel(res.getOutputStream());

                        if (varHeaderLine != null) {
                            //ByteBuffer varHeaderByteBuffer = ByteBuffer.allocate(varHeaderLine.length() * 2);
                            ByteBuffer varHeaderByteBuffer = ByteBuffer.wrap(varHeaderLine.getBytes());
                            //varHeaderByteBuffer.put (varHeaderLine.getBytes());
                            out.write(varHeaderByteBuffer);
                        }

                        long position = 0;
                        long howMany = 32 * 1024;

                        while (position < in.size()) {
                            in.transferTo(position, howMany, out);
                            position += howMany;
                        }

                        in.close();
                        out.close();

                        if (imageThumb == null || (!dbContentType.substring(0, 6).equalsIgnoreCase("image/"))) {
                            studyService.incrementNumberOfDownloads(file.getId());
                        }
                    } catch (IOException ex) {
                        //ex.printStackTrace();
                        String errorMessage = "An unknown I/O error has occured while attempting to serve a locally-stored data file. Unfortunately, no extra diagnostic information on the nature of the problem is available to the Application at this point. This could indicate that the data file in question is corrupt. It is also possible that it was caused by a temporary network error. Please try again later and if the problem persists, report it to your DVN technical support contact.";
                        createErrorResponseGeneric(res, 0, errorMessage);
                    }
                }
            }
        } else {
            // a request for a zip-packaged multiple
            // file archive.

            // first determine which files to archive.

            Study study = null;
            Collection files = new ArrayList();
            boolean createDirectoriesForCategories = false;
            String catId = req.getParameter("catId");
            String studyId = req.getParameter("studyId");

            String fileManifest = "";


            if (fileId != null) {
                String[] idTokens = fileId.split(",");

                for (String tok : idTokens) {
                    StudyFile sf;
                    try {
                        sf = studyService.getStudyFile(new Long(tok));
                        files.add(sf);
                    } catch (Exception ex) {
                        fileManifest = fileManifest + tok + " DOES NOT APPEAR TO BE A VALID FILE ID;\r\n";
                    }
                }
            } else if (catId != null) {
                try {
                    FileCategory cat = studyService.getFileCategory(new Long(catId));
                    study = cat.getStudy();
                    files = cat.getStudyFiles();
                } catch (Exception ex) {
                    if (ex.getCause() instanceof IllegalArgumentException) {
                        createErrorResponse404(res);
                        return;
                    }
                }
            } else if (studyId != null) {
                try {
                    study = studyService.getStudy(new Long(studyId));
                    files = study.getStudyFiles();
                    createDirectoriesForCategories = true;
                } catch (Exception ex) {
                    if (ex.getCause() instanceof IllegalArgumentException) {
                        createErrorResponse404(res);
                        return;
                    }
                }
            } else {
                createErrorResponse404(res);
                return;
            }

            // check for restricted files
            Iterator iter = files.iterator();
            while (iter.hasNext()) {
                StudyFile file = (StudyFile) iter.next();
                if (file.isFileRestrictedForUser(user, vdc, ipUserGroup)) {
                    fileManifest = fileManifest + file.getFileName() + " IS RESTRICTED AND CANNOT BE DOWNLOADED\r\n";
                    iter.remove();
                }
            }

            if (files.size() == 0) {
                createErrorResponse403(res);
                return;
            }

            // an HTTP GET method for remote files;
            // we want it defined here so that it can be closed
            // properly if an exception is caught.

            GetMethod method = null;

            // now create zip
            try {
                // set content type:
                res.setContentType("application/zip");

                // create zipped output stream:

                OutputStream out = res.getOutputStream();
                ZipOutputStream zout = new ZipOutputStream(out);


                // send the file as the response

                List nameList = new ArrayList(); // used to check for duplicates
                iter = files.iterator();

                while (iter.hasNext()) {
                    int fileSize = 0;
                    StudyFile file = (StudyFile) iter.next();

                    InputStream in = null;
                    //ReadableByteChannel in = null;


                    String varHeaderLine = null;
                    String dbContentType = file.getFileType();

                    if (dbContentType != null && dbContentType.equals("text/tab-separated-values") && file.isSubsettable()) {
                        List datavariables = file.getDataTable().getDataVariables();
                        varHeaderLine = generateVariableHeader(datavariables);
                    }

                    Boolean Success = true;

                    try {
                        if (file.isRemote()) {

                            // do the http magic

                            int status = 200;

                            method = new GetMethod(file.getFileSystemLocation());
                            status = getClient().executeMethod(method);

                            if (status != 200) {

                                if (method != null) {
                                    method.releaseConnection();
                                }

                            } else {

                                // the incoming HTTP stream is the source of
                                // the current chunk of the zip stream we are
                                // creating.

                                in = method.getResponseBodyAsStream();
                            }

                        // well, yes, the logic above will result in
                        // adding an empty file to the zip archive in
                        // case the remote object is not accessible.

                        // I can't think of a better solution right now,
                        // but it should work for now.

                        } else {
                            // local file.
                            in = new FileInputStream(new File(file.getFileSystemLocation()));
                        }
                    } catch (IOException ex) {
                        Success = false;

                        if (dbContentType == null) {
                            dbContentType = "unknown filetype;";
                        }

                        fileManifest = fileManifest + file.getFileName() + " (" + dbContentType + ") COULD NOT be downloaded because an I/O error has occured. \r\n";

                        if (in != null) {
                            in.close();
                        }

                        // if this was a remote stream, let's close
                        // the connection properly:

                        if (file.isRemote()) {
                            if (method != null) {
                                method.releaseConnection();
                            }
                        }
                    }

                    if (Success) {
                        String zipEntryName = file.getFileName();
                        if (createDirectoriesForCategories) {
                            String catName = new FileCategoryUI(file.getFileCategory()).getDownloadName();
                            zipEntryName = catName + "/" + zipEntryName;
                        }
                        zipEntryName = checkZipEntryName(zipEntryName, nameList);
                        ZipEntry e = new ZipEntry(zipEntryName);

                        zout.putNextEntry(e);

                        if (varHeaderLine != null) {
                            byte[] headerBuffer = varHeaderLine.getBytes();
                            zout.write(headerBuffer);
                            fileSize += (headerBuffer.length);
                        }

                        byte[] dataBuffer = new byte[8192];

                        int i = 0;
                        while ((i = in.read(dataBuffer)) > 0) {
                            zout.write(dataBuffer, 0, i);
                            fileSize += i;
                            out.flush();
                        }
                        in.close();
                        zout.closeEntry();

                        if (dbContentType == null) {
                            dbContentType = "unknown filetype;";
                        }

                        fileManifest = fileManifest + file.getFileName() + " (" + dbContentType + ") " + fileSize + " bytes.\r\n";

                        if (fileSize > 0) {
                            studyService.incrementNumberOfDownloads(file.getId());
                        }

                        // if this was a remote stream, let's close
                        // the connection properly:

                        if (file.isRemote()) {
                            if (method != null) {
                                method.releaseConnection();
                            }
                        }
                    }
                }

                // finally, let's create the manifest entry:

                ZipEntry e = new ZipEntry("MANIFEST.TXT");

                zout.putNextEntry(e);
                zout.write(fileManifest.getBytes());
                zout.closeEntry();

                zout.close();

            } catch (IOException ex) {
                // if we caught an exception *here*, it means something
                // catastrophic has happened while packaging the zip archive
                // itself (I/O errors on individual files would be caught
                // above); so there's not much we can do except print a
                // generic error message:

                //ex.printStackTrace();

                String errorMessage = "An unknown I/O error has occured while generating a Zip archive of multiple data files. Unfortunately, no further diagnostic information on the nature of the problem is avaiable to the Application at this point. It is possible that the problem was caused by a temporary network error. Please try again later and if the problem persists, report it to your DVN technical support contact.";
                createErrorResponseGeneric(res, 0, errorMessage);


                if (method != null) {
                    method.releaseConnection();
                }


            }
        }
    }

    private void createErrorResponseGeneric(HttpServletResponse res, int status, String statusLine) {
        res.setContentType("text/html");

        if (status == 0) {
            status = 200;
        }

        res.setStatus(status);
        PrintWriter out = null;
        try {
            out = res.getWriter();
            out.println("<HTML>");
            out.println("<HEAD><TITLE>File Download</TITLE></HEAD>");
            out.println("<BODY>");
            out.println("<BIG>" + statusLine + "</BIG>");

            if (status == res.SC_NOT_FOUND) {
                for (int i = 0; i < 10; i++) {
                    out.println("<!-- This line is filler to handle IE case for 404 errors   -->");
                }
            }

            out.println("</BODY></HTML>");
            out.flush();
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    private void createRedirectResponse(HttpServletResponse res, String remoteUrl) {
        try {
            res.sendRedirect(remoteUrl);
        } catch (IOException ex) {
            //ex.printStackTrace();
            String errorMessage = "An unknown I/O error has occured while the Application was attempting to issue a redirect to a remote resource (i.e., a URL pointing to a page on a remote server). Please try again later and if the problem persists, report it to your DVN technical support contact.";
            createErrorResponseGeneric(res, 0, errorMessage);

        }

    }

    private void createErrorResponse403(HttpServletResponse res) {
        createErrorResponseGeneric(res, res.SC_FORBIDDEN, "You do not have permission to download this file.");
    }

    private void createErrorResponse200(HttpServletResponse res) {
        createErrorResponseGeneric(res, 200, "You do not have permission to download this file.");
    }

    private void createErrorResponse404(HttpServletResponse res) {
        createErrorResponseGeneric(res, res.SC_NOT_FOUND, "Sorry. The file you are looking for could not be found.");
    }

    // private methods for generating parameters for the DSB 
    // conversion call;
    // borrowed from Gustavo's code in DSBWrapper (for now)
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

    public String generateVariableHeader(List dvs) {
        String varHeader = null;

        if (dvs != null) {
            Iterator iter = dvs.iterator();
            DataVariable dv;

            if (iter.hasNext()) {
                dv = (DataVariable) iter.next();
                varHeader = dv.getName();
            }

            while (iter.hasNext()) {
                dv = (DataVariable) iter.next();
                varHeader = varHeader + "\t" + dv.getName();
            }

            varHeader = varHeader + "\n";
        }

        return varHeader;
    }

    private String generateUrlForDDI(String serverPrefix, Long fileId) {
        String studyDDI = serverPrefix + "/ddi/?fileId=" + fileId;
        System.out.println(studyDDI);
        return studyDDI;
    }

    private String generateUrlForFile(String serverPrefix, Long fileId) {
        String file = serverPrefix + "/FileDownload/?fileId=" + fileId + "&isSSR=1";
        System.out.println(file);
        return file;
    }

    private String generateAltFormat(String formatRequested) {
        String altFormat;

        //  if ( formatRequested.equals("D02") ) {
        //    altFormat = "application/x-rlang-transport";
        // } else if ( formatRequested.equals("DO3") ) {
        //     altFormat = "application/x-stata-6";
        // } else {
        //     altFormat = "application/x-R-2";
        // }

        if (formatRequested.equals("D00")) {
            altFormat = "text/tab-separated-values";
        } else {
            altFormat = "application/zip";
        }

        return altFormat;
    }

    private String generateOriginalExtension(String fileType) {

        if (fileType.equalsIgnoreCase("application/x-spss-sav")) {
            return ".sav";
        } else if (fileType.equalsIgnoreCase("application/x-spss-por")) {
            return ".por";
        } else if (fileType.equalsIgnoreCase("application/x-stata")) {
            return ".dta";
        }

        return "";
    }

    private boolean generateImageThumb(String fileLocation) {

        String thumbFileLocation = fileLocation + ".thumb";

        // see if the thumb is already generated and saved:

        if (new File(thumbFileLocation).exists()) {
            return true;
        }

        // let's attempt to generate the thumb:

        // (I'm scaling all the images down to 64 pixels horizontally;
        // I picked the number 64 totally arbitrarily;
        // TODO: (?) make the default thumb size configurable
        // through a JVM option??


        if (new File("/usr/bin/convert").exists()) {

            String ImageMagick = "/usr/bin/convert -size 64x64 " + fileLocation + " -resize 64 -flatten png:" + thumbFileLocation;
            int exitValue = 1;

            try {
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec(ImageMagick);
                exitValue = process.waitFor();
            } catch (Exception e) {
                exitValue = 1;
            }

            if (exitValue == 0) {
                return true;
            }
        }

        // For whatever reason, creating the thumbnail with ImageMagick
        // has failed.
        // Let's try again, this time with Java's standard Image
        // library:

        try {
            BufferedImage fullSizeImage = ImageIO.read(new File(fileLocation));

            double scaleFactor = ((double) 64) / (double) fullSizeImage.getWidth(null);
            int thumbHeight = (int) (fullSizeImage.getHeight(null) * scaleFactor);

            java.awt.Image thumbImage = fullSizeImage.getScaledInstance(64, thumbHeight, java.awt.Image.SCALE_FAST);

            ImageWriter writer = null;
            Iterator iter = ImageIO.getImageWritersByFormatName("png");
            if (iter.hasNext()) {
                writer = (ImageWriter) iter.next();
            } else {
                return false;
            }

            BufferedImage lowRes = new BufferedImage(64, thumbHeight, BufferedImage.TYPE_INT_RGB);
            lowRes.getGraphics().drawImage(thumbImage, 0, 0, null);

            ImageOutputStream ios = ImageIO.createImageOutputStream(new File(thumbFileLocation));
            writer.setOutput(ios);

            // finally, save thumbnail image:
            writer.write(lowRes);
            writer.dispose();

            ios.close();
            thumbImage.flush();
            fullSizeImage.flush();
            lowRes.flush();
            return true;
        } catch (Exception e) {
            // something went wrong, returning "false":
            return false;
        }
    }

    private String generateAltFileName(String formatRequested, String xfileId) {
        String altFileName;

        //  if ( formatRequested.equals("D02") ) {
        //    altFileName = "data_" + xfileId + ".ssc";
        // } else if ( formatRequested.equals("DO3") ) {
        //    altFileName = "data_" + xfileId + ".dta";
        // } else {
        //     altFileName = "data_" + xfileId + ".RData";
        // }

        if (formatRequested.equals("D00")) {
            altFileName = "data_" + xfileId + ".tab";
        } else {
            altFileName = "data_" + xfileId + ".zip";
        }

        return altFileName;
    }

    private String checkZipEntryName(String originalName, List nameList) {
        String name = originalName;
        int fileSuffix = 1;
        int extensionIndex = originalName.lastIndexOf(".");

        while (nameList.contains(name)) {
            if (extensionIndex != -1) {
                name = originalName.substring(0, extensionIndex) + "_" + fileSuffix++ + originalName.substring(extensionIndex);
            } else {
                name = originalName + "_" + fileSuffix++;
            }
        }
        nameList.add(name);
        return name;
    }

    private String remoteAuthRequired(String remoteHost) {
        String remoteAuthType = null;

        if (remoteHost == null) {
            return null;
        }

        RemoteAccessAuth remoteAuth = studyService.lookupRemoteAuthByHost(remoteHost);
        if (remoteAuth != null) {
            remoteAuthType = remoteAuth.getType();
        }

        return remoteAuthType;
    }

    private String getRemoteAuthCredentials(String remoteHost) {
        String remoteAuthCreds = null;

        if (remoteHost == null) {
            return null;
        }

        remoteAuthCreds = studyService.lookupRemoteAuthByHost(remoteHost).getAuthCred1();

        if (remoteAuthCreds != null) {
            return "Basic " + remoteAuthCreds;
        }

        return null;
    }

    private String dvnRemoteAuth(String remoteHost) {
        // if successful, this method will return the JSESSION string
        // for the authenticated session on the remote DVN.

        String remoteJsessionid = null;
        String remoteDvnUser = null;
        String remoteDvnPw = null;

        GetMethod loginGetMethod = null;
        PostMethod loginPostMethod = null;


        RemoteAccessAuth remoteAuth = studyService.lookupRemoteAuthByHost(remoteHost);

        if (remoteAuth == null) {
            return null;
        }

        remoteDvnUser = remoteAuth.getAuthCred1();
        remoteDvnPw = remoteAuth.getAuthCred2();

        if (remoteDvnUser == null || remoteDvnPw == null) {
            return null;
        }

        int status = 0;

        try {

            String remoteAuthUrl = "http://" + remoteHost + "/dvn/faces/login/LoginPage.xhtml";
            loginGetMethod = new GetMethod(remoteAuthUrl);
            loginGetMethod.setFollowRedirects(false);
            status = getClient().executeMethod(loginGetMethod);

            InputStream in = loginGetMethod.getResponseBodyAsStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in));

            String line = null;

            String viewstate = null;


            String regexpJsession = "jsessionid=([^\"?&]*)";
            String regexpViewState = "ViewState\" value=\"([^\"]*)\"";

            Pattern patternJsession = Pattern.compile(regexpJsession);
            Pattern patternViewState = Pattern.compile(regexpViewState);

            Matcher matcher = null;

            while ((line = rd.readLine()) != null) {
                matcher = patternJsession.matcher(line);
                if (matcher.find()) {
                    remoteJsessionid = matcher.group(1);
                }
                matcher = patternViewState.matcher(line);
                if (matcher.find()) {
                    viewstate = matcher.group(1);
                }
            }

            rd.close();
            loginGetMethod.releaseConnection();

            if (remoteJsessionid != null) {

                // We have found Jsession;
                // now we can log in,
                // has to be a POST method:

                loginPostMethod = new PostMethod(remoteAuthUrl + ";jsessionid=" + remoteJsessionid);
                loginPostMethod.setFollowRedirects(false);

                Part[] parts = {
                    new StringPart("vanillaLoginForm:vdcId", ""),
                    new StringPart("vanillaLoginForm:username", remoteDvnUser),
                    new StringPart("vanillaLoginForm:password", remoteDvnPw),
                    new StringPart("vanillaLoginForm_hidden", "vanillaLoginForm_hidden"),
                    new StringPart("vanillaLoginForm:button1", "Log in"),
                    new StringPart("javax.faces.ViewState", viewstate)
                };


                loginPostMethod.setRequestEntity(new MultipartRequestEntity(parts, loginPostMethod.getParams()));
                loginPostMethod.addRequestHeader("Cookie", "JSESSIONID=" + remoteJsessionid);
                status = getClient().executeMethod(loginPostMethod);

                String redirectLocation = null;

                if (status == 302) {
                    for (int i = 0; i < loginPostMethod.getResponseHeaders().length; i++) {
                        String headerName = loginPostMethod.getResponseHeaders()[i].getName();
                        if (headerName.equals("Location")) {
                            redirectLocation = loginPostMethod.getResponseHeaders()[i].getValue();
                        }
                    }
                }

                loginPostMethod.releaseConnection();
                int counter = 0;

                while (status == 302 && counter < 10) {

                    if (counter > 0) {
                        for (int i = 0; i < loginGetMethod.getResponseHeaders().length; i++) {
                            String headerName = loginGetMethod.getResponseHeaders()[i].getName();
                            if (headerName.equals("Location")) {
                                redirectLocation = loginGetMethod.getResponseHeaders()[i].getValue();
                            }
                        }
                    }

                    // try following redirects until we get a static page,
                    // or until we exceed the hoop limit.
                    if (redirectLocation.matches(".*TermsOfUsePage.*")) {
                        loginGetMethod = remoteAccessTOU(redirectLocation + "&clicker=downloadServlet", remoteJsessionid, null);
                        if (loginGetMethod != null) {
                            status = loginGetMethod.getStatusCode();
                        }
                    } else {
                        loginGetMethod = new GetMethod(redirectLocation);
                        loginGetMethod.setFollowRedirects(false);
                        loginGetMethod.addRequestHeader("Cookie", "JSESSIONID=" + remoteJsessionid);
                        status = getClient().executeMethod(loginGetMethod);

                        //InputStream in = loginGetMethod.getResponseBodyAsStream();
                        //BufferedReader rd = new BufferedReader(new InputStreamReader(in));
                        //rd.close();
                        loginGetMethod.releaseConnection();
                        counter++;
                    }
                }
            }
        } catch (IOException ex) {
            if (loginGetMethod != null) {
                loginGetMethod.releaseConnection();
            }
            if (loginPostMethod != null) {
                loginPostMethod.releaseConnection();
            }
            return null;
        }

        return remoteJsessionid;
    }

    private GetMethod remoteAccessTOU(String TOUurl, String jsessionid, String downloadURL) {
	DvnTermsOfUseAccess dvnTOU = new DvnTermsOfUseAccess();

	jsessionid = dvnTOU.dvnAcceptRemoteTOU ( TOUurl, jsessionid, downloadURL ); 

	GetMethod finalGetMethod = null; 
	int status = 0; 

	try {
	    finalGetMethod = new GetMethod ( downloadURL );
	    finalGetMethod.setFollowRedirects(false);
	    finalGetMethod.addRequestHeader("Cookie", "JSESSIONID=" + jsessionid ); 
	    status = getClient().executeMethod(finalGetMethod);
	    
	} catch (IOException ex) {
	    if (finalGetMethod != null) { 
		finalGetMethod.releaseConnection(); 
	    }
	}

	if (status != 200) {
	    if (finalGetMethod != null) { 
		finalGetMethod.releaseConnection(); 
	    }
	    return null;
	}

	return finalGetMethod; 
    }
    
    
//  addtion (ver 1.4) starts here ---------------------------------------------






    /**
     * ArrayList object that stores major metadata of all variables in 
     * the requested data file and backs the value attribute of 
     * h:dataTable (id = dataTable1) in the variable table of the jsp page.
     */
    private List<Object> dt4Display = new ArrayList<Object>();
    


    
    /**
     * Adds major metadata of all variables in the requested data file into
     * dt4Display.  The six cells of each row are as follows:
     * 
     * boolean: checkbox state,
     * String: variable type,
     * Long:   variable Id,
     * String: variable name,
     * String: variable label,
     * String: blank cell for summary statistics
     * 
     * @see       #dt4Display
     */
    private void initDt4Display() {
        for (Iterator el = dataVariables.iterator(); el.hasNext();) {
            DataVariable dv = (DataVariable) el.next();

            List<Object> rw = new ArrayList<Object>();
            // 0-th: boolean (checked/unchecked)
            rw.add(new Boolean(false));
            
            // 1st: variable type
            if (dv.getVariableFormatType().getName().equals("numeric")) {
                if (dv.getVariableIntervalType() == null) {
                    rw.add("Continuous");
                } else {
                    if (dv.getVariableIntervalType().getId().intValue() == 2) {
                        rw.add("Continuous");
                    } else {
                        rw.add("Discrete");
                    }
                }
            } else {
                rw.add("Character");
            }

            // 2nd: ID
            rw.add(dv.getId().toString());
            
            // 3rd: Variable Name
            rw.add(dv.getName());
            
            // 4th: Variable Label
            if (dv.getLabel() != null) {
                rw.add(dv.getLabel());
            } else {
                rw.add("[label missing]");
            }
            
            // 5th: summary statistics(blank)
            // the content is generated by an AJAX call upon request
            rw.add("");
            /*
            // 6th: fileorder number
            rw.add(dv.getFileOrder());
             */ 
            // add a row
            dt4Display.add(rw);
        }
    }








    /**
     * Returns a List object that stores major metadata for all variables 
     * selected by an end-user
     *
     * @return    List of DataVariable objects that stores metadata
     */
    public List<DataVariable> getDataVariableForRequest() {
        List<DataVariable> dvs = new ArrayList<DataVariable>();
        for (Iterator el = dataVariables.iterator(); el.hasNext();) {
            DataVariable dv = (DataVariable) el.next();
            String keyS = dv.getId().toString();
            //if (varCart.containsKey(keyS)) {
                dvs.add(dv);
            //}
        }
        return dvs;
    }

    /**
     * Returns the name of a given variable whose id is known.
     * Because dt4Display is not a HashMap but a List, 
     * loop-through is necessary
     *
     * @param varId    the id of a given variable
     * @return    the name of a given variable
     */
    public String getVariableNamefromId(String varId) {

        for (int i = 0; i < dt4Display.size(); i++) {
            if (((String) ((ArrayList) dt4Display.get(i)).get(2)).equals(varId)) {
                return (String) ((ArrayList) dt4Display.get(i)).get(3);

            }
        }
        return null;
    }


    /**
     * Returns the label of a given variable whose id is known.
     * Because dt4Display is not a HashMap but a List, 
     * loop-through is necessary
     *
     * @param varId    the id of a given variable
     * @return    the label of a given variable
     */
    public String getVariableLabelfromId(String varId) {

        for (int i = 0; i < dt4Display.size(); i++) {
            if (((String) ((ArrayList) dt4Display.get(i)).get(2)).equals(varId)) {
                return (String) ((ArrayList) dt4Display.get(i)).get(4);
            }
        }
        return null;
    }

    /**
     * Returns the file-order of a given variable whose id is known.
     * Because dt4Display is not a HashMap but a List, 
     * loop-through is necessary
     *
     * @param varId    the id of a given variable
     * @return    the file-order number of a given variable
     */
    /*
    public String getVariableFileOderfromId(String varId) {

        for (int i = 0; i < dt4Display.size(); i++) {
            if (((String) ((ArrayList) dt4Display.get(i)).get(2)).equals(varId)) {
                return (String) ((ArrayList) dt4Display.get(i)).get(6);

            }
        }
        return null;
    }
    */
    /**
     * Gets the row of metadata of a variable whose Id is given by a String
     * object
     *
     * @param varId    a given variable's ID as a String object
     * @return    a DataVariable instance that contains major metadata of the
     *            requested variable
     */
    public DataVariable getVariableById(String varId) {

        DataVariable dv = null;
        for (Iterator el = dataVariables.iterator(); el.hasNext();) {
            dv = (DataVariable) el.next();
            // Id is Long
            if (dv.getId().toString().equals(varId)) {
                return dv;
            }
        }
        return dv;
    }
    
    
    /**
     * 
     *
     * @param dvs   
     * @return      
     */
    public List<String> generateVariableIdList(List<String> dvs) {
        List<String> variableIdList = new ArrayList<String>();
        if (dvs != null) {
            for (String el : dvs){
                variableIdList.add("v" + el);
            }
        }
        return variableIdList;
    }

    /**
     * 
     *
     * @param     
     * @return    
     */
    
    public List<String> getVariableListForRequest(){
        List<String> variableList = new ArrayList();
        if (getDataVariableForRequest() != null) {
            Iterator iter = getDataVariableForRequest().iterator();
            while (iter.hasNext()) {
                DataVariable dv = (DataVariable) iter.next();
                variableList.add(dv.getId().toString());
            }
        }
        return variableList;
    }
    
    public List<String> getVariableIdListForRequest(){
        List<String> ids = generateVariableIdList(getVariableListForRequest());
        return ids;
    }

    /**
     * 
     *
     * @param     
     * @return    
     */
    
    public List<String> getVariableOrderForRequest(){
        List<String> variableOrder = new ArrayList();
        if (getDataVariableForRequest() != null) {
            Iterator iter = getDataVariableForRequest().iterator();
            while (iter.hasNext()) {
                DataVariable dv = (DataVariable) iter.next();
                // the susbsetting parameter starts from 1 not 0,
                // add 1 to the number
                variableOrder.add( Integer.toString(dv.getFileOrder()+1) );
            }
        }
        return variableOrder;
    }

    /**
     * 
     *
     * @return    A List of file-order numbers
     */
    public List<Integer> getFileOrderForRequest() {
        List<DataVariable> dvs = new ArrayList<DataVariable>();
        List<Integer> fileOrderForRequest = new ArrayList<Integer>();
        for (Iterator el = dataVariables.iterator(); el.hasNext();) {
            DataVariable dv = (DataVariable) el.next();
            String keyS = dv.getId().toString();
            //if (varCart.containsKey(keyS)) {
                fileOrderForRequest.add(dv.getFileOrder());
            //}
        }
        Collections.sort(fileOrderForRequest);
        return fileOrderForRequest;
    }

    /**
     * 
     *
     * @return    
     */
    public Map<Long, List<List<Integer>>> getSubsettingMetaData(Long noRecords){

        Map<Long, List<List<Integer>>> varMetaSet = new LinkedHashMap<Long, List<List<Integer>>>();

        List<DataVariable> dvs = getDataVariableForRequest();

    //populate the initial, empty varMetaSet: 

    for (Long count = new Long((long)0); count < noRecords; count++){
        List<List<Integer>> cardVarMetaSet = new LinkedList<List<Integer>>();
        varMetaSet.put((count+1), cardVarMetaSet); 
    }

        if (dvs != null) {
            for (int i = 0 ; i < dvs.size();i++  ){

                DataVariable dv = dvs.get(i);

                List<Integer> varMeta = new ArrayList<Integer>();

                varMeta.add( Integer.valueOf(dv.getFileStartPosition().toString()) );
                varMeta.add( Integer.valueOf(dv.getFileEndPosition().toString()) );
                // raw data: 1: numeric 2: character=> 0: numeric; 1 character
                varMeta.add( Integer.valueOf( (int)(dv.getVariableFormatType().getId()-1)  ) ); 
                
                if ( dv.getNumberOfDecimalPoints() == null ) {
                    varMeta.add ( 0 ); 
                } else if ( dv.getNumberOfDecimalPoints().toString().equals ("") ) {
                    varMeta.add ( 0 ); 
                } else {
                    varMeta.add( Integer.valueOf(dv.getNumberOfDecimalPoints().toString()) ); 
                }

                Long recordSegmentNumber = dv.getRecordSegmentNumber(); 

                //if ( varMetaSet.get(recordSegmentNumber) == null ) {
                //    List<List<Integer>> cardVarMetaSet = new LinkedList<List<Integer>>();
                //    varMetaSet.put(recordSegmentNumber, cardVarMetaSet); 
                //}

                varMetaSet.get(recordSegmentNumber).add(varMeta); 
            
            }
        }

        return varMetaSet; 
    }
        
    public Map<String, String> getValueTableForRequestedVariable(DataVariable dv){
        List<VariableCategory> varCat = new ArrayList<VariableCategory>();
        varCat.addAll(dv.getCategories());
        Map<String, String> vl = new HashMap<String, String>();
        for (VariableCategory vc : varCat){
            vl.put(vc.getValue(), vc.getLabel());
        }
        return vl;
    }
    
    
    public Map<String, Map<String, String>> getValueTableForRequestedVariables(List<DataVariable> dvs){
        Map<String, Map<String, String>> vls = new LinkedHashMap<String, Map<String, String>>();
        for (DataVariable dv : dvs){
            List<VariableCategory> varCat = new ArrayList<VariableCategory>();
            varCat.addAll(dv.getCategories());
            Map<String, String> vl = new HashMap<String, String>();
            for (VariableCategory vc : varCat){
                if (vc.getLabel() != null){
                    vl.put(vc.getValue(), vc.getLabel());
                }
            }
            if (vl.size() > 0){
                vls.put("v"+dv.getId(), vl);
            }
        }
        return vls;
    }

    public Map<String, Map<String, String>> getValueTablesForAllRequestedVariables(){
        Map<String, Map<String, String>> vls = getValueTableForRequestedVariables(getDataVariableForRequest());
        //Map<String, Map<String, String>> vln = getValueTablesOfRecodedVariables();
        //vls.putAll(vln);
        return vls;
    }

    public String getVariableHeaderForSubset() {
        String varHeader = null; 
        List<DataVariable> dvs = getDataVariableForRequest();
        List<String> vn = new ArrayList<String>();
        if (dvs != null) {
            for (Iterator el = dvs.iterator(); el.hasNext();) {
                DataVariable dv = (DataVariable) el.next();
                vn.add(dv.getName());
            }
            varHeader = StringUtils.join(vn, "\t");
            varHeader = varHeader + "\n";
        }
        return varHeader;
    }
    
    
    public Set<Integer> getFieldNumbersForSubsetting(){
        // create var ids for subsetting
        // data(int) are taken from DB's studyfile table -- FileOrder column
        Set<Integer> fields = new LinkedHashSet<Integer>();
        List<DataVariable> dvs = getDataVariableForRequest();

        if (dvs != null) {
            for (Iterator el = dvs.iterator(); el.hasNext();) {
                DataVariable dv = (DataVariable) el.next();
                fields.add(dv.getFileOrder());
            }
        }
        return fields;
    }
    
//  addtion (ver 1.4) ends here -----------------------------------------------
    
}
