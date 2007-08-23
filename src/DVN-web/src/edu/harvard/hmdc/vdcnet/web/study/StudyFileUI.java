/*
 * StudyFileUI.java
 *
 * Created on January 25, 2007, 2:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.study;

import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import edu.harvard.hmdc.vdcnet.util.WebStatisticsSupport;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyFileUI {
    
    /** Creates a new instance of StudyFileUI */
    public StudyFileUI() {
    }
    
    public StudyFileUI(StudyFile studyFile, VDC vdc, VDCUser user, UserGroup ipUserGroup) {
        this.studyFile=studyFile;
        this.restrictedForUser = studyFile.isFileRestrictedForUser(user,vdc, ipUserGroup);
    }

    /**
     * Holds value of property studyFile.
     */
    private StudyFile studyFile;

    /**
     * Getter for property studyFile.
     * @return Value of property studyFile.
     */
    public StudyFile getStudyFile() {
        return this.studyFile;
    }

    /**
     * Setter for property studyFile.
     * @param studyFile New value of property studyFile.
     */
    public void setStudyFile(StudyFile studyFile) {
        this.studyFile = studyFile;
    }

    /**
     * Holds value of property restrictedForUser.
     */
    private boolean restrictedForUser;

    /**
     * Getter for property restrictedForUser.
     * @return Value of property restrictedForUser.
     */
    public boolean isRestrictedForUser() {
        return this.restrictedForUser;
    }

    /**
     * Setter for property restrictedForUser.
     * @param restrictedForUser New value of property restrictedForUser.
     */
    public void setRestrictedForUser(boolean restrictedForUser) {
        this.restrictedForUser = restrictedForUser;
    }
    
    
    private String format;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
    
     public String fileDownload_action () {
        try {
            //get the isMIT arg
            WebStatisticsSupport webstatistics = new WebStatisticsSupport();
            int headerValue = webstatistics.getParameterFromHeader("X-Forwarded-For");
            String isMIT = webstatistics.getQSArgument("isMIT", headerValue);
            String fileDownloadURL = "/dvn/FileDownload/" + "?fileId=" + this.studyFile.getId() + isMIT;
            if (!StringUtil.isEmpty(format)) {
                fileDownloadURL += "&format=" + this.format;
            }
            System.out.println("This is the project class file ... ");
            FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();
            HttpServletResponse response = (javax.servlet.http.HttpServletResponse) fc.getExternalContext().getResponse();
            response.sendRedirect(fileDownloadURL);
            fc.responseComplete();
        } catch (IOException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        }
        
        return null;
     }    
}
