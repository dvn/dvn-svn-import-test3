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
 * VDCTestServlet.java
 *
 * Created on December 14, 2006, 4:41 PM
 */

package edu.harvard.hmdc.vdcnet.web.servlet;

import edu.harvard.hmdc.vdcnet.ddi.DDI20ServiceLocal;
import edu.harvard.hmdc.vdcnet.ddi.MappingException;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.study.EditStudyService;
import edu.harvard.hmdc.vdcnet.study.RepositoryWrapper;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.util.FileUtil;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;

import javax.servlet.*;
import javax.servlet.http.*;
import org.xml.sax.SAXException;

/**
 *
 * @author gdurand
 * @version
 */
@EJB(name="editStudy", beanInterface=edu.harvard.hmdc.vdcnet.study.EditStudyService.class)
public class VDCImportServlet extends HttpServlet {
    
    @EJB DDI20ServiceLocal ddiService;
    @EJB StudyServiceLocal studyService;
    @EJB IndexServiceLocal indexService;
    
    
    
    
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        Logger logger = null;
        try {
            String importLogDirStr = FileUtil.getImportFileDir();
            
            
            // Create a file handler that write log record to a file
            FileHandler handler = new FileHandler(importLogDirStr+ File.separator+ "import.log");
            
            // Add to the desired logger
            logger = Logger.getLogger("edu.harvard.hmdc.vdcnet.web.servlet.VDCImportServlet");
            logger.addHandler(handler);
            
            doImportAllStudies(req,res, logger);
            
        } catch (Exception e) {
            if (logger!=null) {
                logger.severe("Exception: "+e.getMessage());
                String stackTrace= "Stack Trace: \n";
                StackTraceElement[] ste = e.getStackTrace();
                for(int i=0;i<ste.length;i++) {
                    stackTrace+=ste[i]+"\n";
                }
                logger.severe(stackTrace);
            } else {
                e.printStackTrace();
            }
            
        }
        
    }
    
    private void doImportAllStudies(HttpServletRequest req, HttpServletResponse res, Logger logger)throws IOException,SAXException{
        String legacyFileDirStr = System.getProperty("vdc.legacy.file.dir");
        if (legacyFileDirStr==null) {
            System.out.println("Missing system property:  vdc.legacy.file.dir.  Please add to JVM options");
            return;
        }
        
        
        File legacyFileDir = new File(legacyFileDirStr);
        
        
        //
        // Set Request Parameters
        //
        String authStr = req.getParameter("authority");
        if (authStr==null) {
            logger.severe("Import failed: missing parameter 'authority' in URL");
            return;
        }
        
        Long vdcId=null;
        String vdcIdStr = req.getParameter("vdcId");
        if (vdcIdStr!=null) {
            vdcId = new Long(vdcIdStr);
        } else {
            vdcId = new Long(1);
        }
        
        Long userId=null;
        String userIdStr=req.getParameter("userId");
        if (userIdStr!=null) {
            userId= new Long(userIdStr);
        } else
            userId=new Long(1);
        
        
        logger.info("Begin import, time="+new Date()+".\n");
        
        if (!legacyFileDir.exists()) {
            logger.severe("Error: "+legacyFileDir+" not found.\n");
            return;
        }
        if (!legacyFileDir.isDirectory()) {
            logger.severe("Error: "+legacyFileDir+" not a directory.\n");
            return;
            
        }

        List successfulIds = new ArrayList();
        List<File> files = getDDIFilesFromRepository(logger,legacyFileDir,authStr);
        for (Iterator it = files.iterator(); it.hasNext();) {
            File file = (File) it.next();
            Long studyId = doImportStudy(req,res,logger,  file, vdcId,userId);
            if ( studyId != null) {
                successfulIds.add(studyId);
            }
        }
        
        // now index
        indexService.updateIndexList(successfulIds);
        
        logger.info("Import completed, imported "+ successfulIds.size() +" studies, time= "+new Date()+". \n\n");
        
    }
    
    
    private List<File> getDDIFilesFromRepository(Logger logger, File legacyFileDir,String authority)throws IOException,SAXException {
        List files= new ArrayList<File>();
        RepositoryWrapper repositoryWrapper = new RepositoryWrapper();
        
        List<String> filePaths = repositoryWrapper.getStudyDDIPaths(authority);
        for (Iterator it = filePaths.iterator(); it.hasNext();) {
            String filePath = (String) it.next();
            File ddiFile = new File(legacyFileDir,filePath);
            if (ddiFile.exists()) {
                files.add(ddiFile);
            } else {
                logger.severe("File "+filePath+" does not exist!");
            }
        }
        return files;
    }
    
    private Long doImportStudy(HttpServletRequest req, HttpServletResponse res, Logger logger,  File studyFile,Long vdcId,Long userId ) {
        String ddiFilePath = studyFile.getParent()+studyFile.separatorChar+studyFile.getName();
        logger.info("Importing study "+ddiFilePath+"\n");
        Long studyId = null;
        try {
            // PLEASE NOTE: this servlet no longer coies files from legacy system; that logic no longer
            // exists (more than likely we should just removwe this servlet)
            //studyId = studyService.importLegacyStudy(studyFile,vdcId,userId).getId();
            studyId = studyService.importStudy(studyFile, new Long(0), vdcId, userId).getId();
            logger.info("Successfully imported study "+ddiFilePath+"\n");
            
        } catch(Exception e) {
            if (e.getCause()!=null && e.getCause() instanceof MappingException) {
                logger.info("Study  "+ddiFilePath+" already exists, not imported.");
            } else {
                logger.severe("Import of study "+ddiFilePath+" failed, Exception = "+e.getClass().getName()+" "+e.getMessage()+"\n");
                if (e.getCause()!=null) {
                    String stackTrace = "StackTrace: \n";
                    logger.severe("Exception caused by: "+e.getCause()+"\n");
                    StackTraceElement[] ste = e.getCause().getStackTrace();
                    for(int m=0;m<ste.length;m++) {
                        stackTrace+=ste[m].toString()+"\n";
                    }
                    logger.severe(stackTrace);
                }
            }
        }
        return studyId;
    }
    
    
    
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
