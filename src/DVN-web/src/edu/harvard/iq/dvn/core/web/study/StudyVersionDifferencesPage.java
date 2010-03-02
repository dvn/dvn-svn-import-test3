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
 * StudyVersionDifferencesPage.java
 *
 * Created on February 18, 2010, 3:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.study;

import edu.harvard.iq.dvn.core.study.ReviewStateServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Leonid Andreev
 */
public class StudyVersionDifferencesPage extends VDCBaseBean implements java.io.Serializable  {

    @EJB private StudyServiceLocal studyService;
    @EJB private ReviewStateServiceLocal reviewStateService;


    public StudyVersionDifferencesPage() {
    }

    // params
    private String actionMode;
    private Long studyId;
    private String versionNumberList;

    public String getActionMode() {
        return actionMode;
    }

    public void setActionMode(String am) {
        this.actionMode = am;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public String getVersionNumberList() {
        return versionNumberList;
    }

    public void setVersionNumberList(String vnl) {
        this.versionNumberList = vnl;
    }


    // internals
    private Long versionNumber1 = null;
    private Long versionNumber2 = null;

    private StudyVersion studyVersion1 = null;
    private StudyVersion studyVersion2 = null;

    private StudyUI studyUI1;
    private StudyUI studyUI2;

    private List<catalogInfoDifferenceItem> citationDiffList;
    private List<catalogInfoDifferenceItem> abstractandscopeDiffList;
    private List<catalogInfoDifferenceItem> datacollectionDiffList;
    private List<catalogInfoDifferenceItem> dataavailDiffList;
    private List<catalogInfoDifferenceItem> termsofuseDiffList;
    private List<catalogInfoDifferenceItem> notesDiffList;


    public List<catalogInfoDifferenceItem> getCitationDiffList () {
        return citationDiffList;
    }
    
    public void setCitationDiffList (List<catalogInfoDifferenceItem> cidl) {
        this.citationDiffList = cidl;
    }

    public List<catalogInfoDifferenceItem> getAbstractandscopeDiffList () {
        return abstractandscopeDiffList;
    }

    public void setAbstractandscopeDiffList (List<catalogInfoDifferenceItem> cidl) {
        this.abstractandscopeDiffList = cidl;
    }

    public List<catalogInfoDifferenceItem> getDatacollectionDiffList () {
        return datacollectionDiffList;
    }

    public void setDatacollectionDiffList (List<catalogInfoDifferenceItem> cidl) {
        this.datacollectionDiffList = cidl;
    }

    public List<catalogInfoDifferenceItem> getDataavailDiffList () {
        return dataavailDiffList;
    }

    public void setDataavailDiffList (List<catalogInfoDifferenceItem> cidl) {
        this.dataavailDiffList = cidl;
    }


    public List<catalogInfoDifferenceItem> getTermsofuseDiffList () {
        return termsofuseDiffList;
    }

    public void setTermsofuseDiffList (List<catalogInfoDifferenceItem> cidl) {
        this.termsofuseDiffList = cidl;
    }

     public List<catalogInfoDifferenceItem> getNotesDiffList () {
        return notesDiffList;
    }

    public void setNotesDiffList (List<catalogInfoDifferenceItem> cidl) {
        this.notesDiffList = cidl;
    }

    public Long getVersionNumber1() {
        return versionNumber1;
    }

    public void setVersionNumber1(Long versionNumber) {
        this.versionNumber1 = versionNumber;
    }

    public Long getVersionNumber2() {
        return versionNumber2;
    }

    public void setVersionNumber2(Long versionNumber) {
        this.versionNumber2 = versionNumber;
    }

    public StudyVersion getStudyVersion1() {
        return studyVersion1;
    }

    public void setStudyVersion1(StudyVersion sv) {
        this.studyVersion1 = sv;
    }

    public StudyVersion getStudyVersion2() {
        return studyVersion2;
    }

    public void setStudyVersion2(StudyVersion sv) {
        this.studyVersion2 = sv;
    }

    public StudyUI getStudyUI1() {
        return studyUI1;
    }

    public void setStudyUI1(StudyUI studyUI) {
        this.studyUI1 = studyUI;
    }

    public StudyUI getStudyUI2() {
        return studyUI2;
    }

    public void setStudyUI2(StudyUI studyUI) {
        this.studyUI2 = studyUI;
    }


    public void init() {
        super.init();
   
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        if (studyId == null) {
            studyId = getVDCRequestBean().getStudyId();
        }

        if (actionMode == null) {
            actionMode = getVDCRequestBean().getActionMode();
        }

        if (versionNumberList == null || !versionNumberList.contains(",")) {
            versionNumberList = getVDCRequestBean().getStudyVersionNumberList();
        }

        if (studyId != null && versionNumberList != null) {
            String[] versionNumTokens = versionNumberList.split(",");

            Long[] versionNumberValues = new Long[2];

            for (int i=0; i<versionNumTokens.length && i<2; i++) {
                if (versionNumTokens[i] != null) {
                    try {
                        versionNumberValues[i] = new Long(versionNumTokens[i]);
                    } catch (Exception ex) {
                        // Means this token was not a parseable decimal long number.
                        versionNumberValues[i] = null;
                    }
                }
            }

            if ( versionNumberValues[0] != null && versionNumberValues[1] != null ) {
                // we want to show first the older version,
                // then the newer:
                if (versionNumberValues[0].compareTo(versionNumberValues[1]) < 0) {
                    versionNumber1 = versionNumberValues[0];
                    versionNumber2 = versionNumberValues[1];
                } else {
                    versionNumber1 = versionNumberValues[1];
                    versionNumber2 = versionNumberValues[0];
                }

                studyVersion1 = studyService.getStudyVersion(studyId, versionNumber1);
                studyVersion2 = studyService.getStudyVersion(studyId, versionNumber2);
            }

            if (studyVersion1 == null || studyVersion2 == null) {
                redirect("/faces/IdDoesNotExistPage.xhtml?type=Study%20Version");
                return;
            }

            studyUI1 = new StudyUI(studyVersion1, getVDCSessionBean().getUser());
            studyUI2 = new StudyUI(studyVersion2, getVDCSessionBean().getUser());

            //initPanelDisplay();

            initCatalogInfoDifferencesList();

        } else {
            // We must have a StudyId and the Version numbers; throw an error
            System.out.println("ERROR: in StudyVersionDifferencePage, without a studyId and/or version Ids");
        }
    }

    public boolean isReleaseConfrimInProgress () {
        return ("confirmRelease".equals(actionMode));
    }

    public void release (ActionEvent ae) {
        // First we archive the previously released version:
        studyService.setArchived(getStudyVersion1().getId());

        // Then we release this latest version:
        //studyUI2.getStudyVersion().setVersionState(StudyVersion.VersionState.RELEASED);
        studyService.setReleased(studyUI2.getStudy().getId());

        // the setReleased method in the StudyServiceBean, above,
        // also updates the study with the Indexer service.
        // every time a study is released.

        // And now we are redirecting back to the StudyPage;
        // no need to specify the version really, since the latest version is now released.
        redirect("/faces/study/StudyPage.xhtml?studyId="+studyId);
    }

    public String cancel() {

        // Do nothing and redirect back to the StudyPage;
        // Need to set the HTTP parameters:

        getVDCRequestBean().setStudyId(getStudyId());
        getVDCRequestBean().setStudyVersionNumber(getVersionNumber2());

        return "viewStudy";
    }


    private void initCatalogInfoDifferencesList () {
        initCitationDifferencesList();
        initAbstractandscopeDifferencesList();
        initDatacollectionDifferencesList();
        initDataavailDifferencesList();
        initTermsofuseDifferencesList();
        initNotesDifferencesList();
    }
    
    private void initCitationDifferencesList () {
        // Can't think of a better way of doing this than just going
        // through and comparing all the cataloging information entries.
        //
        // I also decided to the same exact items for these comparison
        // that we generate for the study view page, in the same order.
        //
        // The code below is largely automatically-generated using
        // the citation items as they are listed in the study view page.

        String value1;
        String value2;

        citationDiffList = new ArrayList<catalogInfoDifferenceItem>();
        
        catalogInfoDifferenceItem idi; 
        
        // insert auto-generated code here:
		value1 = getStudyUI1().getMetadata().getSubTitle();
		value2 = getStudyUI2().getMetadata().getSubTitle();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Subtitle");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				citationDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getStudy().getGlobalId();
		value2 = getStudyUI2().getStudy().getGlobalId();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Global ID");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				citationDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getOtherIds();
		value2 = getStudyUI2().getOtherIds();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Other ID");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				citationDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getAuthorAffiliations();
		value2 = getStudyUI2().getAuthorAffiliations();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Author");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				citationDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getProducers();
		value2 = getStudyUI2().getProducers();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Producer");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				citationDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getProductionDate();
		value2 = getStudyUI2().getProductionDate();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Production Date");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				citationDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getProductionPlace();
		value2 = getStudyUI2().getMetadata().getProductionPlace();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Production Place");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				citationDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getSoftware();
		value2 = getStudyUI2().getSoftware();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Software");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				citationDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getFundingAgency();
		value2 = getStudyUI2().getMetadata().getFundingAgency();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Funding Agency");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				citationDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getGrants();
		value2 = getStudyUI2().getGrants();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Grant Number");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				citationDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getDistributors();
		value2 = getStudyUI2().getDistributors();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Distributor");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				citationDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getDistributorContact();
		value2 = getStudyUI2().getDistributorContact();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Distributor Contact");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				citationDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getDistributionDate();
		value2 = getStudyUI2().getDistributionDate();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Distribution Date");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				citationDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getDepositor();
		value2 = getStudyUI2().getMetadata().getDepositor();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Depositor");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				citationDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getDateOfDeposit();
		value2 = getStudyUI2().getDateOfDeposit();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Deposit Date");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				citationDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getSeries();
		value2 = getStudyUI2().getSeries();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Series");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				citationDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getStudyVersionText();
		value2 = getStudyUI2().getStudyVersionText();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Version");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				citationDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getReplicationFor();
		value2 = getStudyUI2().getMetadata().getReplicationFor();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Replication For");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				citationDiffList.add(idi);

			}
		}

    }

    private void initAbstractandscopeDifferencesList () {
        String value1;
        String value2;

        abstractandscopeDiffList = new ArrayList<catalogInfoDifferenceItem>();

        catalogInfoDifferenceItem idi;

        // insert auto-generated code here:
        value1 = getStudyUI1().getAbstracts();
		value2 = getStudyUI2().getAbstracts();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Abstract");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				abstractandscopeDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getAbstractDates();
		value2 = getStudyUI2().getAbstractDates();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Abstract Date");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				abstractandscopeDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getKeywords();
		value2 = getStudyUI2().getKeywords();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Keyword");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				abstractandscopeDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getTopicClasses();
		value2 = getStudyUI2().getTopicClasses();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Topic Classification");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				abstractandscopeDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getRelPublications();
		value2 = getStudyUI2().getRelPublications();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Related Publications");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				abstractandscopeDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getTimePeriodCovered();
		value2 = getStudyUI2().getTimePeriodCovered();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Time Period Covered");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				abstractandscopeDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getDateOfCollection();
		value2 = getStudyUI2().getDateOfCollection();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Date of Collection");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				abstractandscopeDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getCountry();
		value2 = getStudyUI2().getMetadata().getCountry();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Country/Nation");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				abstractandscopeDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getGeographicCoverage();
		value2 = getStudyUI2().getMetadata().getGeographicCoverage();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Geographic Coverage");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				abstractandscopeDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getGeographicUnit();
		value2 = getStudyUI2().getMetadata().getGeographicUnit();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Geographic Unit");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				abstractandscopeDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getGeographicBoundings();
		value2 = getStudyUI2().getGeographicBoundings();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Geographic Bounding Box");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				abstractandscopeDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getUnitOfAnalysis();
		value2 = getStudyUI2().getMetadata().getUnitOfAnalysis();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Unit of Analysis");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				abstractandscopeDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getUniverse();
		value2 = getStudyUI2().getMetadata().getUniverse();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Universe");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				abstractandscopeDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getKindOfData();
		value2 = getStudyUI2().getMetadata().getKindOfData();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Kind of Data");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				abstractandscopeDiffList.add(idi);

			}
		}

    }

    private void initDatacollectionDifferencesList () {
        String value1;
        String value2;

        datacollectionDiffList = new ArrayList<catalogInfoDifferenceItem>();

        catalogInfoDifferenceItem idi;

        // insert auto-generated code here:
 		value1 = getStudyUI1().getMetadata().getTimeMethod();
		value2 = getStudyUI2().getMetadata().getTimeMethod();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Time Method");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getDataCollector();
		value2 = getStudyUI2().getMetadata().getDataCollector();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Data Collector");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getFrequencyOfDataCollection();
		value2 = getStudyUI2().getMetadata().getFrequencyOfDataCollection();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Frequency");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getSamplingProcedure();
		value2 = getStudyUI2().getMetadata().getSamplingProcedure();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Sampling Procedure");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getDeviationsFromSampleDesign();
		value2 = getStudyUI2().getMetadata().getDeviationsFromSampleDesign();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Major Deviations for Sample Design");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getCollectionMode();
		value2 = getStudyUI2().getMetadata().getCollectionMode();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Collection Mode");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getResearchInstrument();
		value2 = getStudyUI2().getMetadata().getResearchInstrument();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Type of Research Instrument");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getDataSources();
		value2 = getStudyUI2().getMetadata().getDataSources();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Data Sources");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getOriginOfSources();
		value2 = getStudyUI2().getMetadata().getOriginOfSources();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Origin of Sources");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getCharacteristicOfSources();
		value2 = getStudyUI2().getMetadata().getCharacteristicOfSources();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Characteristic of Sources Noted");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getAccessToSources();
		value2 = getStudyUI2().getMetadata().getAccessToSources();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Documentation and Access to Sources");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getDataCollectionSituation();
		value2 = getStudyUI2().getMetadata().getDataCollectionSituation();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Characteristics of Data Collection Situation");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getActionsToMinimizeLoss();
		value2 = getStudyUI2().getMetadata().getActionsToMinimizeLoss();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Actions to Minimize Losses");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getControlOperations();
		value2 = getStudyUI2().getMetadata().getControlOperations();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Control Operations");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getWeighting();
		value2 = getStudyUI2().getMetadata().getWeighting();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Weighting");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getCleaningOperations();
		value2 = getStudyUI2().getMetadata().getCleaningOperations();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Cleaning Operations");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getStudyLevelErrorNotes();
		value2 = getStudyUI2().getMetadata().getStudyLevelErrorNotes();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Study Level Error Notes");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getResponseRate();
		value2 = getStudyUI2().getMetadata().getResponseRate();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Response Rate");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getSamplingErrorEstimate();
		value2 = getStudyUI2().getMetadata().getSamplingErrorEstimate();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Estimates of Sampling Error ");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getOtherDataAppraisal();
		value2 = getStudyUI2().getMetadata().getOtherDataAppraisal();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Other Forms of Data Appraisal");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				datacollectionDiffList.add(idi);

			}
		}
   }

    private void initDataavailDifferencesList () {
        String value1;
        String value2;

        dataavailDiffList = new ArrayList<catalogInfoDifferenceItem>();

        catalogInfoDifferenceItem idi;

        // insert auto-generated code here:
		value1 = getStudyUI1().getMetadata().getPlaceOfAccess();
		value2 = getStudyUI2().getMetadata().getPlaceOfAccess();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Data Access Place");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				dataavailDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getOriginalArchive();
		value2 = getStudyUI2().getMetadata().getOriginalArchive();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Original Archive");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				dataavailDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getAvailabilityStatus();
		value2 = getStudyUI2().getMetadata().getAvailabilityStatus();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Availability Status");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				dataavailDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getCollectionSize();
		value2 = getStudyUI2().getMetadata().getCollectionSize();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Size of Collection");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				dataavailDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getStudyCompletion();
		value2 = getStudyUI2().getMetadata().getStudyCompletion();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Study Completion");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				dataavailDiffList.add(idi);

			}
		}
    }

    private void initTermsofuseDifferencesList () {
        String value1;
        String value2;

        termsofuseDiffList = new ArrayList<catalogInfoDifferenceItem>();

        catalogInfoDifferenceItem idi;

        // insert auto-generated code here:
		value1 = getStudyUI1().getMetadata().getConfidentialityDeclaration();
		value2 = getStudyUI2().getMetadata().getConfidentialityDeclaration();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Confidentiality Declaration");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				termsofuseDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getSpecialPermissions();
		value2 = getStudyUI2().getMetadata().getSpecialPermissions();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Special Permissions");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				termsofuseDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getRestrictions();
		value2 = getStudyUI2().getMetadata().getRestrictions();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Restrictions");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				termsofuseDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getContact();
		value2 = getStudyUI2().getMetadata().getContact();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Contact");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				termsofuseDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getCitationRequirements();
		value2 = getStudyUI2().getMetadata().getCitationRequirements();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Citation Requirements");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				termsofuseDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getDepositorRequirements();
		value2 = getStudyUI2().getMetadata().getDepositorRequirements();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Depositor Requirements");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				termsofuseDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getConditions();
		value2 = getStudyUI2().getMetadata().getConditions();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Conditions");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				termsofuseDiffList.add(idi);

			}
		}
		value1 = getStudyUI1().getMetadata().getDisclaimer();
		value2 = getStudyUI2().getMetadata().getDisclaimer();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Disclaimer");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				termsofuseDiffList.add(idi);

			}
		}
    }

     private void initNotesDifferencesList () {
        String value1;
        String value2;

        notesDiffList = new ArrayList<catalogInfoDifferenceItem>();

        catalogInfoDifferenceItem idi;

        // insert auto-generated code here:
		value1 = getStudyUI1().getMetadata().getTimeMethod();
		value2 = getStudyUI2().getMetadata().getTimeMethod();

		if (value1 != null || value2 != null) {
			if ((value1 != null && !value1.equals(value2)) ||
			    (value2 != null && !value2.equals(value1))) {

				if (value1 == null || value1.equals("")) {
					value1 = "[Empty]";
				} else if (value2 == null || value2.equals("")) {
					value2 = "[Empty]";
				}

				idi = new catalogInfoDifferenceItem();

				idi.setFieldName("Time Method");
				idi.setFieldValue1(value1);
				idi.setFieldValue2(value2);

				notesDiffList.add(idi);

			}
		}
    }


    public boolean isEmpty(String s) {
        if (s == null || s.trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }

   
    public class catalogInfoDifferenceItem {

        public catalogInfoDifferenceItem () {
        }

        private String fieldName;
        private String fieldValue1;
        private String fieldValue2;

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fn) {
            this.fieldName = fn;
        }

        public String getFieldValue1() {
            return fieldValue1;
        }

        public void setFieldValue1(String fv) {
            this.fieldValue1 = fv;
        }

        public String getFieldValue2() {
            return fieldValue2;
        }

        public void setFieldValue2(String fv) {
            this.fieldValue2 = fv;
        }

    }
    
}


