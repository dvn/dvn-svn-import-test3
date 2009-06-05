/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.networkAdmin;
import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.ext.HtmlDataTable;
import edu.harvard.iq.dvn.core.study.StudyComment;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.study.StudyCommentService;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.study.StudyCommentUI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 *
 * @author wbossons
 */
public class CommentReviewPage extends VDCBaseBean implements java.io.Serializable  {
    @EJB
    StudyCommentService studyCommentService;
    VDCServiceLocal vdcService;

    protected List<StudyCommentUI> commentsForReview = null;
    protected Long flaggedCommentId;

    @Override
    public void init() {
        super.init();
        getCommentsForReview();
     }

     public void deleteFlaggedComment(ActionEvent event) {
         if (deleteCommentLink.getAttributes().get("commentId") != null) {
             flaggedCommentId = new Long(deleteCommentLink.getAttributes().get("commentId").toString());
         }
         String deletedMessage = "You reported as abusive a comment in the study titled, " +
                                getFlaggedStudyTitle() + ". " + "\n" +
                                "The comment was, \"" + getFlaggedStudyComment() + "\". " + "\n" +
                               "This comment was deleted in accordance with the " +
                                "study comments terms of use.";
         studyCommentService.deleteComment(flaggedCommentId, deletedMessage);
         String truncatedComment = (getFlaggedStudyComment().length() <= 25) ? getFlaggedStudyComment() : getFlaggedStudyComment().substring(0, 25);
         truncatedComment += "...";
         FacesContext context = FacesContext.getCurrentInstance();
         context.addMessage(event.getComponent().getClientId(context), new FacesMessage("Success!  The comment, " + truncatedComment + ", was deleted."));
         //cleanup
         flaggedCommentId  = new Long("0");
         commentsForReview = null;
         actionComplete    = true;
     }

     public void ignoreCommentFlag(ActionEvent event) {
         if (ignoreCommentFlagLink.getAttributes().get("commentId") != null) {
             flaggedCommentId = new Long(ignoreCommentFlagLink.getAttributes().get("commentId").toString());
         }
         String okMessage = "You reported as abusive a comment in the study titled, " +
                                getFlaggedStudyTitle() + ". " + "\n" +
                                "The comment was, \"" + getFlaggedStudyComment() + "\". " + "\n" +
                                "According to the terms of use of this study, the " +
                                "reported comment is not an abuse. This comment will remain posted, and will " +
                                "no longer appear to you as reported.";
         studyCommentService.okComment(flaggedCommentId, okMessage);
         String truncatedComment = (getFlaggedStudyComment().length() <= 25) ? getFlaggedStudyComment() : getFlaggedStudyComment().substring(0, 25);
         truncatedComment += "...";
         FacesContext context = FacesContext.getCurrentInstance();
         context.addMessage(event.getComponent().getClientId(context), new FacesMessage("Success!  The comment, " + truncatedComment + ", was reset to OK."));
         //cleanup
         flaggedCommentId  = new Long("0");
         commentsForReview = null;
         actionComplete    = true;
     }

     public void goToCommentsTab(ActionEvent event){
         if (goToCommentsLink.getAttributes().get("commentId") != null) {
                 flaggedCommentId = new Long(goToCommentsLink.getAttributes().get("commentId").toString());
         }
         Iterator iterator = commentsForReview.iterator();
         String theUrl = new String();
         while (iterator.hasNext()) {
            StudyCommentUI studyCommentUI     = (StudyCommentUI)iterator.next();
            if (studyCommentUI.getStudyComment().getId().equals(flaggedCommentId)) {
                theUrl = studyCommentUI.getCommentsTabLink();
                getVDCRequestBean().setSelectedTab("comments");
                getVDCRequestBean().setStudyId(studyCommentUI.getStudyComment().getStudy().getId());
                getVDCRequestBean().setCurrentVDC(studyCommentUI.getStudyComment().getStudy().getOwner());

                break;
            }
          }
         FacesContext fc = FacesContext.getCurrentInstance();
         ExternalContext context = fc.getExternalContext();
         NavigationHandler navigationHandler = fc.getApplication().getNavigationHandler();
         navigationHandler.handleNavigation(fc, null, "goToComments");
     }

    protected HtmlCommandLink goToCommentsLink;

    /**
     * Get the value of goToCommentsLink
     *
     * @return the value of goToCommentsLink
     */
    public HtmlCommandLink getGoToCommentsLink() {
        return goToCommentsLink;
    }

    /**
     * Set the value of goToCommentsLink
     *
     * @param goToCommentsLink new value of goToCommentsLink
     */
    public void setGoToCommentsLink(HtmlCommandLink goToCommentsLink) {
        this.goToCommentsLink = goToCommentsLink;
    }


    /**
     * Get the value of commentsForReview
     *
     * @return the value of commentsForReview
     */
    public List<StudyCommentUI> getCommentsForReview() {
        if (commentsForReview == null) {
            commentsForReview = new ArrayList();
            List<StudyComment> tempCommentsForReview = studyCommentService.getAbusiveStudyComments();
            Iterator iterator = tempCommentsForReview.iterator();
            while (iterator.hasNext()) {
                StudyComment studyComment     = (StudyComment)iterator.next();
                StudyCommentUI studyCommentUI = new StudyCommentUI(studyComment);
                commentsForReview.add(studyCommentUI);
            }
            totalNotifications = new Long(Integer.toString(commentsForReview.size()));
        }
        return commentsForReview;
    }

    /**
     * Set the value of commentsForReview
     *
     * @param commentsForReview new value of commentsForReview
     */
    public void setCommentsForReview(List<StudyCommentUI> commentsForReview) {
        this.commentsForReview = commentsForReview;
    }

    protected HtmlCommandLink deleteCommentLink;

    /**
     * Get the value of deleteCommentLink
     *
     * @return the value of deleteCommentLink
     */
    public HtmlCommandLink getDeleteCommentLink() {
        return deleteCommentLink;
    }

    /**
     * Set the value of deleteCommentLink
     *
     * @param deleteCommentLink new value of deleteCommentLink
     */
    public void setDeleteCommentLink(HtmlCommandLink deleteCommentLink) {
        this.deleteCommentLink = deleteCommentLink;
    }

    protected HtmlCommandLink ignoreCommentFlagLink;

    /**
     * Get the value of ignoreCommentFlagLink
     *
     * @return the value of ignoreCommentFlagLink
     */
    public HtmlCommandLink getIgnoreCommentFlagLink() {
        return ignoreCommentFlagLink;
    }

    /**
     * Set the value of ignoreCommentFlagLink
     *
     * @param ignoreCommentFlagLink new value of ignoreCommentFlagLink
     */
    public void setIgnoreCommentFlagLink(HtmlCommandLink ignoreCommentFlagLink) {
        this.ignoreCommentFlagLink = ignoreCommentFlagLink;
    }

    protected Long totalNotifications;

    /**
     * Get the value of totalNotifications
     *
     * @return the value of totalNotifications
     */
    public Long getTotalNotifications() {
        return totalNotifications;
    }

    /**
     * Set the value of totalNotifications
     *
     * @param totalNotifications new value of totalNotifications
     */
    public void setTotalNotifications(Long totalNotifications) {
        this.totalNotifications = totalNotifications;
    }

    // getters and setters
     protected String getFlaggedStudyComment() {
         String comment = new String("");
         Iterator iterator = commentsForReview.iterator();
         while (iterator.hasNext()) {
             StudyCommentUI studycommentui = (StudyCommentUI)iterator.next();
             //debug remove this

             if (studycommentui.getStudyComment().getId().equals(flaggedCommentId)) {
                 comment = studycommentui.getStudyComment().getComment();
                 break;
             }
         }
         return comment;
     }

     // getters and setters
     protected String getFlaggedStudyTitle() {
         String title = new String("");
         Iterator iterator = commentsForReview.iterator();
         while (iterator.hasNext()) {
             StudyCommentUI studycommentui = (StudyCommentUI)iterator.next();
             if (studycommentui.getStudyComment().getId().equals(flaggedCommentId)) {
                 title = studycommentui.getStudyComment().getStudy().getTitle();
                 break;
             }
         }
         return title;
     }

     

    protected boolean actionComplete = false;

    /**
     * Get the value of actionComplete
     *
     * @return the value of actionComplete
     */
    public boolean isActionComplete() {
        return actionComplete;
    }

    /**
     * Set the value of actionComplete
     *
     * @param actionComplete new value of actionComplete
     */
    public void setActionComplete(boolean actionComplete) {
        this.actionComplete = actionComplete;
    }

    protected HtmlDataTable mainDataTable;

    /**
     * Get the value of mainDataTable
     *
     * @return the value of mainDataTable
     */
    public HtmlDataTable getMainDataTable() {
        return mainDataTable;
    }

    /**
     * Set the value of mainDataTable
     *
     * @param mainDataTable new value of mainDataTable
     */
    public void setMainDataTable(HtmlDataTable mainDataTable) {
        this.mainDataTable = mainDataTable;
    }
}
