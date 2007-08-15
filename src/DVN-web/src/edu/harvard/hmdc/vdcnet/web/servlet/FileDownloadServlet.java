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
import edu.harvard.hmdc.vdcnet.study.DataVariable;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.study.FileCategoryUI;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map; 
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.ejb.EJB;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod; 
import org.apache.commons.httpclient.methods.PostMethod; 

/**
 *
 * @author gdurand
 */
public class FileDownloadServlet extends HttpServlet{

    private HttpClient client = null;
    
    /** Creates a new instance of FileDownloadServlet */
    public FileDownloadServlet() {
    }

    private HttpClient getClient() {
        if (client == null) {
            client = new HttpClient( new MultiThreadedHttpConnectionManager() );
        }
        return client;
    }

    private String generateDisseminateUrl() throws IOException{
        String dsbUrl = System.getProperty("vdc.dsb.url");
        
        if (dsbUrl != null) {
            return "http://" + dsbUrl + "/VDC/DSB/0.1/Disseminate";
        } else {
            throw new IOException("System property \"vdc.dsb.url\" has not been set.");
        }
        
    }

    @EJB StudyServiceLocal studyService;
    @EJB VDCServiceLocal vdcService;
  
    public void service(HttpServletRequest req, HttpServletResponse res)  {
        VDCUser user = null;
        if ( LoginFilter.getLoginBean(req) != null ) {
            user= LoginFilter.getLoginBean(req).getUser();
        }
        VDC vdc = vdcService.getVDCFromRequest(req);
        UserGroup ipUserGroup= null;
        if (req.getSession(true).getAttribute("ipUserGroup") != null) {
            ipUserGroup= (UserGroup)req.getSession().getAttribute("ipUserGroup");
        }
        String fileId = req.getParameter("fileId");
	String formatRequested = req.getParameter("format");


        if (fileId != null) {

	    StudyFile file = null; 

	    try {
		file = studyService.getStudyFile( new Long(fileId));

	    } catch (IllegalArgumentException ex) {
		createErrorResponse404(res);
		return;
	     }
	    
	    if ( file == null ) {
	        // this check is probably unnecessary, as a non-existing file
		// would already have produced the exception above (??)
		// -- not sure, gotta ask Gustavo 

		createErrorResponse404(res);
		return;
	     }		    

	    // determine if the fileId represents a local object 
	    // or a remote URL

	    if ( file.isRemote() ) {

		// do the http magic

		GetMethod method = null; 
		int status = 200;

		try { 
		    method = new GetMethod ( file.getFileSystemLocation() );
		    status = getClient().executeMethod(method);
		} catch (IOException ex) {
		    // return 404 
		    // and generate a FILE NOT FOUND message

		    createErrorResponse404(res);
		    if (method != null) { method.releaseConnection(); }
		    return;
		}


		if ( status == 403 ) {
		    // generate an HTML-ized response with a correct 
		    // 403/FORBIDDEN code   

		    createErrorResponse403(res);
		    if (method != null) { method.releaseConnection(); }
		    return;
		} 

		if ( status == 404 ) {
		    // generate an HTML-ized response with a correct 
		    // 404/FILE NOT FOUND code   

		    createErrorResponse404(res);
		    if (method != null) { method.releaseConnection(); }
		    return;
		} 

		// a generic response for all other failure cases:

		if ( status != 200 ) {
		    createErrorResponseGeneric( res, status, (method.getStatusLine() != null)
						? method.getStatusLine().toString()
						: "Unknown HTTP Error");
		    if (method != null) { method.releaseConnection(); }
		    return;
		}

		try {
		    // recycle all the incoming headers 
		    for (int i = 0; i < method.getResponseHeaders().length; i++) {
			res.setHeader(method.getResponseHeaders()[i].getName(), method.getResponseHeaders()[i].getValue());
		    }

		    
		    // send the incoming HTTP stream as the response body

		    InputStream in = method.getResponseBodyAsStream(); 
		    OutputStream out = res.getOutputStream();

		    int i = in.read();
    		    while (i != -1 ) {
			out.write(i);
			i = in.read();
		    }
		    in.close();
		    out.close();
                
                
		} catch (IOException ex) {
		    ex.printStackTrace();
		}

		method.releaseConnection();
	    } else {
		// local object
		
		String dsbHost = System.getProperty("vdc.dsb.url");

		boolean NOTaDSBrequest = true;
  
		if ( dsbHost.equals(req.getRemoteHost()) ) {
		    NOTaDSBrequest = false; 
		} else { 
		    try {
			String dsbHostIPAddress = InetAddress.getByName(dsbHost).getHostAddress(); 
			if ( dsbHostIPAddress.equals(req.getRemoteHost()) ) {
			    NOTaDSBrequest = false;
			}
		    } catch ( UnknownHostException ex ) {
			// do nothing; 
			// the "vdc.dsb.url" setting is clearly misconfigured,
			// so we just keep assuming this is NOT a DSB call
		    }
		}
		
		if ( NOTaDSBrequest && file.isRestricted() && (user == null || file.isFileRestrictedForUser(user, vdc,ipUserGroup)) ) {
		    // generate a response with a correct 403/FORBIDDEN code   

		    createErrorResponse403(res);
		    return;
		}

		studyService.incrementNumberOfDownloads(file.getFileCategory().getStudy().getId());

		if (formatRequested != null) {

		    // user requested the file in a non-default (i.e.,
		    // not tab-delimited) format.
		    // we are going to send a format conversion request to 
		    // the DSB via HTTP.
		    
		    Map parameters = new HashMap();		    

		    parameters.put("dtdwnld", formatRequested); 

		    String serverPrefix = req.getScheme() +"://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();

		    parameters.put("uri", generateUrlForDDI(serverPrefix, file.getFileCategory().getStudy().getId()));
		    parameters.put("URLdata", generateUrlForFile(serverPrefix, file.getId()));
		    parameters.put("fileid", "f" + file.getId().toString());

		    // We are requesting a conversion of the whole datafile. 
		    // I was positive there was a simple way to ask univar (Disseminate)
		    // for "all" variables; but I'm not so sure anymore. So 
		    // far I've only been able to do this by listing all the 
		    // variables in the datafile and adding them to the request.
		    // :( -- L.A.

		    List variables = file.getDataTable().getDataVariables();
		    parameters.put("varbl", generateVariableListForDisseminate( variables ) );

		    PostMethod method = null; 
		    int status = 200;		    

		    try { 
			method = new PostMethod (generateDisseminateUrl());

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

			status = getClient().executeMethod(method);
			
		    } catch (IOException ex) {
			// return 404 and
			// generate generic error messag:

			status = 404; 
			createErrorResponseGeneric( res, status, (method.getStatusLine() != null)
						    ? method.getStatusLine().toString()
						    : "DSB conversion failure");
			if (method != null) { method.releaseConnection(); }
			return;
		    }

		    if ( status != 200 ) {
			createErrorResponseGeneric( res, status, (method.getStatusLine() != null)
						    ? method.getStatusLine().toString()
						    : "DSB conversion failure");
			if (method != null) { method.releaseConnection(); }
			return;
		    }

		    try {
			// recycle all the incoming headers 
			for (int i = 0; i < method.getResponseHeaders().length; i++) {
			    res.setHeader(method.getResponseHeaders()[i].getName(), method.getResponseHeaders()[i].getValue());
			}

		    
			// send the incoming HTTP stream as the response body

			InputStream in = method.getResponseBodyAsStream(); 
			OutputStream out = res.getOutputStream();

			int i = in.read();
			while (i != -1 ) {
			    out.write(i);
			    i = in.read();
			}
			in.close();
			out.close();
                
                
		    } catch (IOException ex) {
			ex.printStackTrace();
		    }
		    
		    method.releaseConnection();
		    
		} else {

		    // finally, the *true* local case, where we just 
		    // read the file off disk and send the stream back.
		   
		    try {
			res.setContentType(file.getFileType());
			// send the file as the response
			InputStream in = new FileInputStream(new File(file.getFileSystemLocation()));
			OutputStream out = res.getOutputStream();
			
			int i = in.read();
			while (i != -1 ) {
			    out.write(i);
			    i = in.read();
			}
			in.close();
			out.close();
                
			
		    } catch (IOException ex) {
			ex.printStackTrace();
		    }
		}
	    }
	} else {
            // first determine which files for zip file

            Study study = null;
            Collection files = new ArrayList();
            boolean createDirectoriesForCategories = false;
            
            String catId = req.getParameter("catId");
            String studyId = req.getParameter("studyId");
            
            if (catId != null) {
                FileCategory cat = studyService.getFileCategory( new Long(catId)); 
                study = cat.getStudy();
                files = cat.getStudyFiles();
            } else if (studyId != null) {
                study = studyService.getStudy( new Long(studyId));
                files = study.getStudyFiles();
                createDirectoriesForCategories = true;
            }
 
            // check for restricted files
            Iterator iter = files.iterator();
            while (iter.hasNext()) {
                StudyFile file = (StudyFile) iter.next();
                if (file.isRestricted() && (user == null || file.isFileRestrictedForUser(user, vdc,ipUserGroup)) ) {
                    iter.remove();
                }  
            }
            if (files.size() == 0) {
                createErrorResponse403(res); 
                return;
            }
            studyService.incrementNumberOfDownloads(study.getId());            
            
            // now create zip
            try {
                // send the file as the response
                res.setContentType("application/zip");
                ZipOutputStream zout = new ZipOutputStream(res.getOutputStream());
                List nameList = new ArrayList(); // used to check for duplicates
                iter = files.iterator();
                while (iter.hasNext()) {
                    StudyFile file = (StudyFile) iter.next();
                    InputStream in = new FileInputStream(new File(file.getFileSystemLocation()));
                    
                    String zipEntryName = file.getFileName();
                    if (createDirectoriesForCategories) {
                        String catName = new FileCategoryUI( file.getFileCategory() ).getDownloadName();    
                        zipEntryName = catName + "/" + zipEntryName;
                    }
                    zipEntryName = checkZipEntryName( zipEntryName, nameList );
                    ZipEntry e = new ZipEntry(zipEntryName);
                    
                    zout.putNextEntry(e);
                    int i = in.read();
                    while (i != -1 ) {
			zout.write(i);
                        i = in.read();
                    }
                    in.close();
                    zout.closeEntry();
                }
                zout.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void createErrorResponseGeneric(HttpServletResponse res, int status, String statusLine) {
        res.setContentType("text/html");
	res.setStatus ( status ); 
        try {
            PrintWriter out = res.getWriter();
            out.println("<HTML>");
            out.println("<HEAD><TITLE>File Download</TITLE></HEAD>");
            out.println("<BODY>");
            out.println("<BIG>" + statusLine + "</BIG>");
            out.println("</BODY></HTML>");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    
    private void createErrorResponse403(HttpServletResponse res) {
        res.setContentType("text/html");
	res.setStatus ( res.SC_FORBIDDEN ); 
        try {
            PrintWriter out = res.getWriter();
            out.println("<HTML>");
            out.println("<HEAD><TITLE>File Download</TITLE></HEAD>");
            out.println("<BODY>");
            out.println("<BIG>You do not have permission to download this file.</BIG>");
            out.println("</BODY></HTML>");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void createErrorResponse404(HttpServletResponse res) {
        res.setContentType("text/html");
	res.setStatus ( res.SC_NOT_FOUND ); 
        try {
            PrintWriter out = res.getWriter();
            out.println("<HTML>");
            out.println("<HEAD><TITLE>File Download</TITLE></HEAD>");
            out.println("<BODY>");
            out.println("<BIG>No such object found for the File Id supplied.</BIG>");
            out.println("</BODY></HTML>");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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

    

    private String generateUrlForDDI(String serverPrefix, Long studyId) {
        String studyDDI = serverPrefix + "/ddi/?studyId=" + studyId;
        return studyDDI;
    }
    
    private String generateUrlForFile(String serverPrefix, Long fileId) {
        String file = serverPrefix + "/FileDownload/?fileId=" + fileId + "&isSSR=1";
        System.out.println(file);
        return file;
    }
   
    private String checkZipEntryName(String originalName, List nameList) {
        String name = originalName;
        int fileSuffix = 1;
        int extensionIndex = originalName.lastIndexOf(".");
        
        while (nameList.contains(name)) {
            if (extensionIndex != -1 ) {
                name = originalName.substring(0, extensionIndex) + "_" + fileSuffix++ + originalName.substring(extensionIndex);
            }  else {
                name = originalName + "_" + fileSuffix++;
            }    
        }
        nameList.add(name);
        return name;            
    }
}

