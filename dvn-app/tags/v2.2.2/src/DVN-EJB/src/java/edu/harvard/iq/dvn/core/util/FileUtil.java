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
 * FileUtil.java
 *
 * Created on February 12, 2007, 5:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.util;

import edu.harvard.iq.dvn.core.study.FileMetadata;
import edu.harvard.iq.dvn.ingest.dsb.JhoveWrapper;
import edu.harvard.iq.dvn.ingest.dsb.SubsettableFileChecker;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.TabularDataFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.activation.MimetypesFileTypeMap;
import javax.ejb.EJBException;

import java.util.logging.*;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author Ellen Kraffmiller
 */
public class FileUtil implements java.io.Serializable  {
    
    private static Logger dbgLog = Logger.getLogger(FileUtil.class.getCanonicalName());

    private static final String[] SUBSETTABLE_FORMAT_SET = {"POR", "SAV", "DTA"};

    private static Map<String, String> STATISTICAL_SYNTAX_FILE_EXTENSION = new HashMap<String, String>();
    static {
        STATISTICAL_SYNTAX_FILE_EXTENSION.put("do",  "x-stata-syntax");
        STATISTICAL_SYNTAX_FILE_EXTENSION.put("sas", "x-sas-syntax");
        STATISTICAL_SYNTAX_FILE_EXTENSION.put("sps", "x-spss-syntax");
    }
    
    private static MimetypesFileTypeMap MIME_TYPE_MAP = new MimetypesFileTypeMap();

    /**
     * The number of bytes in a kilobyte.
     */
    public static final long ONE_KB = 1024;

    /**
     * The number of bytes in a megabyte.
     */
    public static final long ONE_MB = ONE_KB * ONE_KB;

    /**
     * The number of bytes in a gigabyte.
     */
    public static final long ONE_GB = ONE_KB * ONE_MB;

    /** Creates a new instance of FileUtil */
    public FileUtil() {
    }
    
      public static void copyFile(File inputFile, File outputFile) throws IOException {
        FileChannel in = null;
        WritableByteChannel out = null;
        
        try {
            in = new FileInputStream(inputFile).getChannel();
            out = new FileOutputStream(outputFile).getChannel();
            long bytesPerIteration = 50000;
            long start = 0;
            while ( start < in.size() ) {
                in.transferTo(start, bytesPerIteration, out);
                start += bytesPerIteration;
            }
            
        } finally {
            if (in != null) { in.close(); }
            if (out != null) { out.close(); }
        }
    }


    public static String determineFileType(File f) throws IOException{
        return determineFileType( f, f.getName()) ;    
    }      
    
    public static String determineFileType(FileMetadata fm) throws IOException{
        //TODO: networkDataFile
        StudyFile sf = fm.getStudyFile();
      if (sf instanceof TabularDataFile) {
            return determineTabularDataFileType((TabularDataFile) sf);
        } else {            
            if ( sf.isRemote() ) {
                return FileUtil.determineFileType( fm.getLabel() );
            } else {
                return FileUtil.determineFileType( new File( sf.getFileSystemLocation() ), fm.getLabel() );
            }              
        }
    }
    
    public static String determineTabularDataFileType(TabularDataFile tdf) {
            if ( tdf.getDataTable().getRecordsPerCase() != null )  {
                return "text/x-fixed-field";
            } else {
                return "text/tab-separated-values";
            }        
    }  
    
    public static String determineFileType(String fileName) {
        return MIME_TYPE_MAP.getContentType(fileName);
    }  
       
    
    private static String determineFileType(File f, String fileName) throws IOException{
        String fileType = null;
        dbgLog.fine("***** within FileUtil: determineFileType(File, String) ******");
        
        // step 1: check whether the file is subsettable
        dbgLog.fine("before SubsettableFileChecker constructor");
        SubsettableFileChecker sfchk = new SubsettableFileChecker(SUBSETTABLE_FORMAT_SET);
        
        dbgLog.fine("f: abs path="+f.getAbsolutePath());
        fileType = sfchk.detectSubsettableFormat(f);
        
        // step 2: If not found, check if graphml
        if (fileType==null) {
            if (isGraphMLFile(f))  {
                fileType = "text/xml-graphml";
            }
        }
        dbgLog.fine("before jhove");
        // step 3: check the mime type of this file with Jhove
        if (fileType == null){
            JhoveWrapper jw = new JhoveWrapper();
            fileType = jw.getFileMimeType(f);
        }
        dbgLog.fine("after jhove");
        // step 3: handle Jhove fileType (if we have an extension)
        // if text/plain and syntax file, replace the "plain" part
        // if application/octet-stream, check for mime type by extension
        String fileExtension = getFileExtension(fileName);
        dbgLog.fine("fileExtension="+fileExtension);
        
        if ( fileExtension != null) {
            if (fileType.startsWith("text/plain")){
                if (( fileExtension != null) && (STATISTICAL_SYNTAX_FILE_EXTENSION.containsKey(fileExtension))) {
                    // replace the mime type with the value of the HashMap
                    fileType = fileType.replace("plain",STATISTICAL_SYNTAX_FILE_EXTENSION.get(fileExtension));
                }
            } else if (fileType.equals("application/octet-stream")) {
                fileType = determineFileType(fileName);
            }
            dbgLog.fine("non-null fileType="+fileType);
        } else {
            dbgLog.fine("fileExtension is null");
        }
        dbgLog.fine("returning fileType "+fileType);
        return fileType;
    }
        
   public static String getFileExtension(String fileName){
        String ext = null;
        if ( fileName.lastIndexOf(".") != -1){
            ext = (fileName.substring( fileName.lastIndexOf(".") + 1 )).toLowerCase();
        }
        return ext;
    } 

    public static String replaceExtension(String originalName) {
       return replaceExtension(originalName, "tab");
    }   
    
    public static String replaceExtension(String originalName, String newExtension) {
        int extensionIndex = originalName.lastIndexOf(".");
        if (extensionIndex != -1 ) {
            return originalName.substring(0, extensionIndex) + "."+newExtension ;
        } else {
            return originalName +"."+newExtension ;
        }
    }
     public static String getUserFriendlyFileType(StudyFile sf) {
        String tempFileType = sf.getFileType();
        
        if (tempFileType != null) {
            if ( tempFileType.indexOf(";") != -1 ) {
                tempFileType = tempFileType.substring( 0, tempFileType.indexOf(";") );
            }
        
            try {
                return ResourceBundle.getBundle("FileTypeBundle").getString( tempFileType );
            } catch (MissingResourceException e) {
                return tempFileType;
            }
        }
        
        return tempFileType;
     }     
    
      
    public static String getStudyFileDir() {
        String studyFileDir = System.getProperty("vdc.study.file.dir");
        if (studyFileDir != null) {
            return studyFileDir;
        } else {
            throw new EJBException("System property \"vdc.study.file.dir\" has not been set.");
        }
    }     
    
       public static String getLegacyFileDir() {
        String studyFileDir = System.getProperty("vdc.legacy.file.dir");
        if (studyFileDir != null) {
            return studyFileDir;
        } else {
            throw new EJBException("System property \"vdc.legacy.file.dir\" has not been set.");
        }
    }     
       
       public static String getImportFileDir() {
        String importFileDir = System.getProperty("vdc.import.log.dir");
        if (importFileDir != null) {
            File importLogDir = new File(importFileDir);
            if (!importLogDir.exists()) {
                importLogDir.mkdirs();
            }            
            return importFileDir;
        } else {
            throw new EJBException("System property \"vdc.import.log.dir\" has not been set.");
        }
    }
       
    public static String getExportFileDir() {
        String exportFileDir = System.getProperty("vdc.export.log.dir");
        if (exportFileDir != null) {
            File exportLogDir = new File(exportFileDir);
            if (!exportLogDir.exists()) {
                exportLogDir.mkdirs();
            }            
            return exportFileDir;
        } else {
            throw new EJBException("System property \"vdc.export.log.dir\" has not been set.");
        }
    }    
    public static File getStudyFileDir(Study study) {
        
        File file = new File(FileUtil.getStudyFileDir(), study.getAuthority() + File.separator + study.getStudyId());
        if (!file.exists()) {
             file.mkdirs();
        }
        return file;
    }

    public static File getStudyFileDir(String authority, String studyId) {

        File file = new File(FileUtil.getStudyFileDir(), authority + File.separator + studyId.toUpperCase());
        if (!file.exists()) {
             file.mkdirs();
        }
        return file;
    }
    
    
     public static File createTempFile(String sessionId, String originalFileName) throws Exception{ 
        String filePathDir = System.getProperty("vdc.temp.file.dir");
        if (filePathDir != null) {
            File tempDir = new File(filePathDir, sessionId);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            return createTempFile(tempDir, originalFileName);
           
        } else {
            throw new Exception("System property \"vdc.temp.file.dir\" has not been set.");
        }

     }

     public static File createTempFile(File dir, String originalFileName) throws Exception{ 
        // now create the file
        String tempFileName = originalFileName;
        File file = new File(dir, tempFileName);
        int fileSuffix = 1;

        while (!file.createNewFile()) {
            int extensionIndex = originalFileName.lastIndexOf(".");
            if (extensionIndex != -1 ) {
                tempFileName = originalFileName.substring(0, extensionIndex) + "_" + fileSuffix++ + originalFileName.substring(extensionIndex);
            }  else {
                tempFileName = originalFileName + "_" + fileSuffix++;
            }
            file = new File(dir, tempFileName);
        }

        return file;
     }

       private static boolean isGraphMLFile(File file) {
        boolean isGraphML = false;
        dbgLog.fine("begin isGraphMLFile()");
        try{
            FileReader fileReader = new FileReader(file);
            javax.xml.stream.XMLInputFactory xmlif = javax.xml.stream.XMLInputFactory.newInstance();
            xmlif.setProperty("javax.xml.stream.isCoalescing", java.lang.Boolean.TRUE);

            XMLStreamReader xmlr = xmlif.createXMLStreamReader(fileReader);
            for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if (xmlr.getLocalName().equals("graphml")) {
                        String schema = xmlr.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
                        dbgLog.fine("schema = "+schema);
                        if (schema!=null && schema.indexOf("http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd")!=-1){
                            dbgLog.fine("graphML is true");
                            isGraphML = true;
                        }
                    }
                    break;
                }
            }
        } catch(XMLStreamException e) {
            dbgLog.fine("XML error - this is not a valid graphML file.");
            isGraphML = false;
        } catch(IOException e) {
            throw new EJBException(e);
        }
        dbgLog.fine("end isGraphML()");
        return isGraphML;
    }
    
    //-----------------------------------------------------------------------
    /**
     * This borrows from Apache commons io FileUtils, with a slightly finer
     * grained display of file sizes greater than 1 GB
     * Returns a human-readable version of the file size, where the input
     * represents a specific number of bytes.
     *
     * @param size  the number of bytes
     * @return a human-readable display value (includes units)
     */
    public static String byteCountToDisplaySize(long size) {
        String displaySize;

        if (size / ONE_GB > 0) {
            displaySize = String.valueOf(size / ONE_GB) + "." + String.valueOf((size % ONE_GB)/ (100*ONE_MB)) + " GB";
        } else if (size / ONE_MB > 0) {
            displaySize = String.valueOf(size / ONE_MB) + " MB";
        } else if (size / ONE_KB > 0) {
            displaySize = String.valueOf(size / ONE_KB) + " KB";
        } else {
            displaySize = String.valueOf(size) + " bytes";
        }
        return displaySize;
    }

}
