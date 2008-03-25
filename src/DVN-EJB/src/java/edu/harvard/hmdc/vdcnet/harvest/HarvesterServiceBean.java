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
 * HarvesterServiceBean.java
 *
 * Created on February 12, 2007, 3:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.hmdc.vdcnet.harvest;

import ORG.oclc.oai.harvester2.verb.GetRecord;
import ORG.oclc.oai.harvester2.verb.ListIdentifiers;
import ORG.oclc.oai.harvester2.verb.ListMetadataFormats;
import ORG.oclc.oai.harvester2.verb.ListSets;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.jaxb.oai.HeaderType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.ListIdentifiersType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.ListMetadataFormatsType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.ListSetsType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.MetadataFormatType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.OAIPMHerrorType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.OAIPMHtype;
import edu.harvard.hmdc.vdcnet.jaxb.oai.ResumptionTokenType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.SetType;
import edu.harvard.hmdc.vdcnet.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.util.FileUtil;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverse;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverseServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateless(name = "harvesterService")
@EJB(name = "editStudyService", beanInterface = edu.harvard.hmdc.vdcnet.study.EditStudyService.class)
public class HarvesterServiceBean implements HarvesterServiceLocal {
    @PersistenceContext(unitName = "VDCNet-ejbPU")
    EntityManager em;
    @Resource
    javax.ejb.TimerService timerService;
    @Resource
    SessionContext ejbContext;
    @EJB
    StudyServiceLocal studyService;
    @EJB
    HarvesterServiceLocal harvesterService;
    @EJB
    HarvestingDataverseServiceLocal havestingDataverseService;
    @EJB
    IndexServiceLocal indexService;
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
    @EJB
    MailServiceLocal mailService;
    private static final Logger logger = Logger.getLogger("edu.harvard.hmdc.vdcnet.harvest.HarvesterServiceBean");
    private static final String HARVEST_TIMER = "HarvestTimer";
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat logFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");

    static {
        try {
            logger.addHandler(new FileHandler(FileUtil.getImportFileDir() + File.separator + "harvest.log"));
        } catch (IOException e) {


            throw new EJBException(e);
        }
    }
    private JAXBContext jaxbContext;
    private Unmarshaller unmarshaller;

    public void ejbCreate() {
        try {

            jaxbContext = JAXBContext.newInstance("edu.harvard.hmdc.vdcnet.jaxb.oai");
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates a new instance of HarvesterServiceBean
     */
    public HarvesterServiceBean() {

    }
    /**
     * Called to run an "On Demand" harvest.  
     * This method creates a timer that will go off immediately,
     * which will start an immediate asynchronous harvest.
     * @param dataverse
     */
    public void doAsyncHarvest(HarvestingDataverse dataverse) {
        Calendar cal = Calendar.getInstance();

        timerService.createTimer(cal.getTime(), new HarvestTimerInfo(dataverse.getId(), dataverse.getVdc().getName(), dataverse.getSchedulePeriod(), dataverse.getScheduleHourOfDay(), dataverse.getScheduleDayOfWeek()));
    }

    public void createScheduledHarvestTimers() {
        // First clear all previous Harvesting timers 
        for (Iterator it = timerService.getTimers().iterator(); it.hasNext();) {
            Timer timer = (Timer) it.next();
            if (timer.getInfo() instanceof HarvestTimerInfo) {
                timer.cancel();
            }

        }

        List dataverses = havestingDataverseService.findAll();
        for (Iterator it = dataverses.iterator(); it.hasNext();) {
            HarvestingDataverse dataverse = (HarvestingDataverse) it.next();
            if (dataverse.isScheduled()) {
                createHarvestTimer(dataverse);
            }
        }
 

    }

    public void removeHarvestTimer(HarvestingDataverse dataverse) {
        // Clear dataverse timer, if one exists 
        for (Iterator it = timerService.getTimers().iterator(); it.hasNext();) {
            Timer timer = (Timer) it.next();
            if (timer.getInfo() instanceof HarvestTimerInfo) {
                HarvestTimerInfo info = (HarvestTimerInfo) timer.getInfo();
                if (info.getHarvestingDataverseId().equals(dataverse.getId())) {
                    timer.cancel();
                }
            }
        }
    }

    public void updateHarvestTimer(HarvestingDataverse dataverse) {
        removeHarvestTimer(dataverse);
        createHarvestTimer(dataverse);
    }

    private void createHarvestTimer(HarvestingDataverse dataverse) {
        if (dataverse.isScheduled()) {
            long intervalDuration = 0;
            Calendar initExpiration = Calendar.getInstance();
            initExpiration.set(Calendar.MINUTE, 0);
            initExpiration.set(Calendar.SECOND, 0);
            if (dataverse.getSchedulePeriod().equals(dataverse.SCHEDULE_PERIOD_DAILY)) {
                intervalDuration = 1000 * 60 * 60 * 24;
                initExpiration.set(Calendar.HOUR_OF_DAY, dataverse.getScheduleHourOfDay());

            } else if (dataverse.getSchedulePeriod().equals(dataverse.SCHEDULE_PERIOD_WEEKLY)) {
                intervalDuration = 1000 * 60 * 60 * 24 * 7;
                initExpiration.set(Calendar.HOUR_OF_DAY, dataverse.getScheduleHourOfDay());
                initExpiration.set(Calendar.DAY_OF_WEEK, dataverse.getScheduleDayOfWeek());

            } else {
                logger.log(Level.WARNING, "Could not set timer for dataverse id, " + dataverse.getId() + ", unknown schedule period: " + dataverse.getSchedulePeriod());
                return;
            }
            Date initExpirationDate = initExpiration.getTime();
            Date currTime = new Date();
            if (initExpirationDate.before(currTime)) {
                initExpirationDate.setTime(initExpiration.getTimeInMillis() + intervalDuration);
            }
            logger.log(Level.INFO, "Setting timer for dataverse " + dataverse.getVdc().getName() + ", initial expiration: " + initExpirationDate);
            timerService.createTimer(initExpirationDate, intervalDuration, new HarvestTimerInfo(dataverse.getId(), dataverse.getVdc().getName(), dataverse.getSchedulePeriod(), dataverse.getScheduleHourOfDay(), dataverse.getScheduleDayOfWeek()));
        }
    }
    
    /**
     * This method is called whenever an EJB Timer goes off.
     * Check to see if this is a Harvest Timer, and if it is
     * Run the harvest for the given (scheduled) dataverse
     * @param timer
     */
    @Timeout
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void handleTimeout(javax.ejb.Timer timer) {
        // We have to put all the code in a try/catch block because
        // if an exception is thrown from this method, Glassfish will automatically
        // call the method a second time. (The minimum number of re-tries for a Timer method is 1)
        try {
            if (timer.getInfo() instanceof HarvestTimerInfo) {
                HarvestTimerInfo info = (HarvestTimerInfo) timer.getInfo();
                logger.log(Level.INFO, "DO HARVESTING of dataverse " + info.getHarvestingDataverseId());
                doHarvesting(info.getHarvestingDataverseId());
            }


        } catch (Throwable e) {
            mailService.sendHarvestErrorNotification(vdcNetworkService.find().getContactEmail(), vdcNetworkService.find().getName());
            logException(e, logger);
        }
    }

    /**
     * Harvest an individual Dataverse
     * @param dataverseId
     */
    private void doHarvesting(Long dataverseId) throws IOException {
        HarvestingDataverse dataverse = em.find(HarvestingDataverse.class, dataverseId);
        MutableBoolean harvestErrorOccurred = new MutableBoolean(false);
        String logTimestamp = logFormatter.format(new Date());
        Logger hdLogger = Logger.getLogger("edu.harvard.hmdc.vdcnet.harvest.HarvesterServiceBean." + dataverse.getVdc().getAlias() + logTimestamp);
        String logFileName = FileUtil.getImportFileDir() + File.separator + "harvest_" + dataverse.getVdc().getAlias() + logTimestamp + ".log ";
      
        hdLogger.addHandler(new FileHandler(logFileName));
 
        boolean harvestingNow = havestingDataverseService.getHarvestingNow(dataverse.getId());
        int harvestedStudyCount=0;
        List<String> failedIdentifiers = new ArrayList<String>();
       if (harvestingNow) {
            harvestErrorOccurred.setValue(true);
             hdLogger.log(Level.SEVERE,"Cannot begin harvesting, Dataverse " + dataverse.getVdc().getName() + " is currently being harvested.");
            
        } else {

            String until = null;  // If we don't set until date, we will get all the changes since the last harvest.
            String from = null;
            Date lastHarvestTime = havestingDataverseService.getLastHarvestTime(dataverse.getId());
            if (lastHarvestTime != null) {
                from = formatter.format(lastHarvestTime);
            }
            harvestedStudyCount = harvest(dataverse, hdLogger, from, until, harvestErrorOccurred, failedIdentifiers );
        }      
        mailService.sendHarvestNotification(vdcNetworkService.find().getContactEmail(), dataverse.getVdc().getName(), logFileName, harvestErrorOccurred.booleanValue(), harvestedStudyCount, failedIdentifiers);  

    }
 
    /**
     * 
     * @param dataverse  the dataverse to harvest into
     * @param from       get updated studies from this beginning date
     * @param until      get updated studies until this end date
     * @param initialHarvest  are we harvesting this dataverse for the first time?
     * @param harvestErrorOccurred  have we encountered any errors during harvest?
     * @param failedIdentifiers     Study Identifiers for failed "GetRecord" requests
     */
    private int harvest(HarvestingDataverse dataverse, Logger hdLogger, String from, String until, MutableBoolean harvestErrorOccurred, List<String> failedIdentifiers) {
   
        List<Long> harvestedStudyIds = new ArrayList<Long>();
   
        try {

            havestingDataverseService.setHarvestingNow(dataverse.getId(), true);
            Date lastHarvestTime = new Date();

            hdLogger.log(Level.INFO, "BEGIN HARVEST..., oaiUrl=" + dataverse.getOaiServer() + ",set=" + dataverse.getHarvestingSet() + ", metadataPrefix=" + dataverse.getHarvestFormatType().getMetadataPrefix() + ", from=" + from + ", until=" + until);
            ResumptionTokenType resumptionToken = null;

            do {
                resumptionToken = harvesterService.harvestFromIdentifiers(hdLogger, resumptionToken, dataverse, from, until, harvestedStudyIds, failedIdentifiers, harvestErrorOccurred);
            } while (resumptionToken != null && !resumptionToken.equals(""));

            hdLogger.log(Level.INFO, "COMPLETED HARVEST, oaiUrl=" + dataverse.getOaiServer() + ",set=" + dataverse.getHarvestingSet() + ", metadataPrefix=" + dataverse.getHarvestFormatType().getMetadataPrefix() + ", from=" + from + ", until=" + until);
            havestingDataverseService.setLastHarvestTime(dataverse.getId(), lastHarvestTime);
            
            // now index all studies (need to modify for update)
            hdLogger.log(Level.INFO, "POST HARVEST, start calls to index.");
            indexService.updateIndexList(harvestedStudyIds);
            hdLogger.log(Level.INFO, "POST HARVEST, calls to index finished.");
        } catch (Throwable e) {
            harvestErrorOccurred.setValue(true);
            String message = "Exception processing harvest, oaiServer= " + dataverse.getOaiServer() + ",from=" + from + ",until=" + until + ",encodedSet=" + dataverse.getHarvestingSet() + ",format=" + dataverse.getHarvestFormatType().getMetadataPrefix() + " " + e.getClass().getName() + " " + e.getMessage();
            hdLogger.log(Level.SEVERE, message);
            logException(e, hdLogger);
            hdLogger.log(Level.INFO, "HARVEST NOT COMPLETED DUE TO UNEXPECTED ERROR.");
          
        } finally {
            havestingDataverseService.setHarvestingNow(dataverse.getId(), false);
        }

        return harvestedStudyIds.size();
     
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ResumptionTokenType harvestFromIdentifiers(Logger hdLogger, ResumptionTokenType resumptionToken, HarvestingDataverse dataverse, String from, String until, List<Long> harvestedStudyIds, List<String> failedIdentifiers, MutableBoolean harvestErrorOccurred)
            throws java.io.IOException, ParserConfigurationException, SAXException, TransformerException, JAXBException {
        String encodedSet = dataverse.getHarvestingSet() == null ? null : URLEncoder.encode(dataverse.getHarvestingSet(), "UTF-8");
        ListIdentifiers listIdentifiers = null;

        if (resumptionToken == null) {
            listIdentifiers = new ListIdentifiers(dataverse.getOaiServer(),
                    from,
                    until,
                    encodedSet,
                    URLEncoder.encode(dataverse.getHarvestFormatType().getMetadataPrefix(), "UTF-8"));
        } else {
            hdLogger.log(Level.INFO, "harvestFromIdentifiers(), resumptionToken=" + resumptionToken.getValue());
            listIdentifiers = new ListIdentifiers(dataverse.getOaiServer(), resumptionToken.getValue());
        }
        Document doc = listIdentifiers.getDocument();

        //       JAXBContext jc = JAXBContext.newInstance("edu.harvard.hmdc.vdcnet.jaxb.oai");
        //       Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBElement unmarshalObj = (JAXBElement) unmarshaller.unmarshal(doc);
        OAIPMHtype oaiObj = (OAIPMHtype) unmarshalObj.getValue();

        if (oaiObj.getError() != null && oaiObj.getError().size() > 0) {
            handleOAIError(hdLogger, oaiObj, "calling listIdentifiers, oaiServer= " + dataverse.getOaiServer() + ",from=" + from + ",until=" + until + ",encodedSet=" + encodedSet + ",format=" + dataverse.getHarvestFormatType().getMetadataPrefix());
            throw new EJBException("Received OAI Error response calling ListIdentifiers");
        } else {
            ListIdentifiersType listIdentifiersType = oaiObj.getListIdentifiers();
            if (listIdentifiersType != null) {
                resumptionToken = listIdentifiersType.getResumptionToken();
                for (Iterator it = listIdentifiersType.getHeader().iterator(); it.hasNext();) {
                    HeaderType header = (HeaderType) it.next();
                    MutableBoolean getRecordErrorOccurred = new MutableBoolean(false);
                    Long studyId = harvesterService.getRecord(hdLogger, dataverse, header.getIdentifier(), dataverse.getHarvestFormatType().getMetadataPrefix(), getRecordErrorOccurred);
                    if (studyId != null) {
                        harvestedStudyIds.add(studyId);
                    }
                    if (getRecordErrorOccurred.booleanValue()==true) {
                        harvestErrorOccurred.setValue(true);
                        failedIdentifiers.add(header.getIdentifier());
                    }
                    
                }

            }
        }
        String logMsg = "Returning from harvestFromIdentifiers";

        if (resumptionToken == null) {
            logMsg += " resumptionToken is null";
        } else if (!StringUtil.isEmpty(resumptionToken.getValue())) {
            logMsg += " resumptionToken is " + resumptionToken.getValue();
        } else {
            // Some OAIServers return an empty resumptionToken element when all
            // the identifiers have been sent, so need to check  for this, and 
            // treat it as if resumptiontoken is null.
            logMsg += " resumptionToken is empty, setting return value to null.";
            resumptionToken = null;
        }
        hdLogger.info(logMsg);
        return resumptionToken;
    }

    private void handleOAIError(Logger hdLogger, OAIPMHtype oaiObj, String message) {
        for (Iterator it = oaiObj.getError().iterator(); it.hasNext();) {
            OAIPMHerrorType error = (OAIPMHerrorType) it.next();
            message += ", error code: " + error.getCode();
            message += ", error value: " + error.getValue();
            hdLogger.log(Level.SEVERE, message);

        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Long getRecord(HarvestingDataverse dataverse, String identifier, String metadataPrefix) {
        return getRecord(logger, dataverse, identifier, metadataPrefix, null);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Long getRecord(Logger hdLogger, HarvestingDataverse dataverse, String identifier, String metadataPrefix, MutableBoolean errorOccurred) {

        String errMessage = null;;
        Study harvestedStudy = null;
        String oaiUrl = dataverse.getOaiServer();
        try {
            hdLogger.log(Level.INFO, "Calling GetRecord: oaiUrl =" + oaiUrl + "?verb=GetRecord&identifier=" + identifier + "&metadataPrefix=" + metadataPrefix);

            GetRecord record = new GetRecord(oaiUrl, identifier, metadataPrefix);
            errMessage = record.getErrorMessage();
            if (errMessage != null) {
                hdLogger.log(Level.SEVERE, "Error calling GetRecord - " + errMessage);
            } else if (record.isDeleted()) {
                hdLogger.log(Level.INFO, "Received 'deleted' status from OAI Server.");
                Study study = studyService.getStudyByHarvestInfo(dataverse.getVdc(), identifier);
                if (study != null) {
                    hdLogger.log(Level.INFO, "Deleting study " + study.getGlobalId());
                    studyService.deleteStudy(study.getId());
                } else {
                    hdLogger.log(Level.INFO, "No study found for this record, skipping delete. ");
                }

            } else {
                VDCUser networkAdmin = vdcNetworkService.find().getDefaultNetworkAdmin();
                harvestedStudy = studyService.importHarvestStudy(record.getMetadataFile(), dataverse.getVdc().getId(), networkAdmin.getId(), identifier);
                hdLogger.log(Level.INFO, "Harvest Successful for identifier " + identifier);
            }

        } catch (Throwable e) {
            errMessage = "Exception processing getRecord(), oaiUrl=" + oaiUrl + ",identifier=" + identifier + " " + e.getClass().getName() + " " + e.getMessage();
            hdLogger.log(Level.SEVERE, errMessage);
            logException(e, hdLogger);
        }

        // determine how to handle the error, if any
        if (errMessage != null) {
            if (errorOccurred  != null) {
                errorOccurred.setValue(true);           
            } else {
                throw new EJBException(errMessage);
            }
        }

        return harvestedStudy != null ? harvestedStudy.getId() : null;
    }
    
    public List<String> getMetadataFormats(String oaiUrl) {
        JAXBElement unmarshalObj;
        try {

            Document doc = new ListMetadataFormats(oaiUrl).getDocument();
            JAXBContext jc = JAXBContext.newInstance("edu.harvard.hmdc.vdcnet.jaxb.oai");
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            unmarshalObj = (JAXBElement) unmarshaller.unmarshal(doc);
        } catch (TransformerException ex) {
            throw new EJBException(ex);
        } catch (ParserConfigurationException ex) {
            throw new EJBException(ex);
        } catch (JAXBException ex) {
            throw new EJBException(ex);
        } catch (SAXException ex) {
            throw new EJBException(ex);
        } catch (IOException ex) {
            throw new EJBException(ex);
        }

        OAIPMHtype OAIObj = (OAIPMHtype) unmarshalObj.getValue();
        ListMetadataFormatsType listMetadataFormats = OAIObj.getListMetadataFormats();
        List<String> formats = null;
        if (listMetadataFormats != null) {
            formats = new ArrayList<String>();
            for (Iterator it = listMetadataFormats.getMetadataFormat().iterator(); it.hasNext();) {
                //  Object elem = it.next();
                MetadataFormatType elem = (MetadataFormatType) it.next();
                formats.add(elem.getMetadataPrefix());
            }
        }
        return formats;
    }

    /**
     *
     *  SetDetailBean returned rather than the ListSetsType because we get strange errors when trying
     *  to refer to JAXB generated classes in both Web and EJB tiers.
     */
    public List<SetDetailBean> getSets(String oaiUrl) {
        JAXBElement unmarshalObj = null;

        try {
            ListSets listSets = new ListSets(oaiUrl);
            int nodeListLength = listSets.getErrors().getLength();
            if (nodeListLength==1) {
                 System.out.println("err Node: "+ listSets.getErrors().item(0));
            }
           
            
            Document doc = new ListSets(oaiUrl).getDocument();
            JAXBContext jc = JAXBContext.newInstance("edu.harvard.hmdc.vdcnet.jaxb.oai");
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            unmarshalObj = (JAXBElement) unmarshaller.unmarshal(doc);
        } catch (ParserConfigurationException ex) {
            throw new EJBException(ex);
        } catch (SAXException ex) {
            throw new EJBException(ex);
        } catch (TransformerException ex) {
            throw new EJBException(ex);
        } catch (IOException ex) {
            throw new EJBException(ex);
        } catch (JAXBException ex) {
            throw new EJBException(ex);
        }
        List<SetDetailBean> sets = null;
        Object value = unmarshalObj.getValue();

        Package valPackage = value.getClass().getPackage();
        if (value instanceof edu.harvard.hmdc.vdcnet.jaxb.oai.OAIPMHtype) {
            OAIPMHtype OAIObj = (OAIPMHtype) value;
            //      OAIObj.getError()
            ListSetsType listSetsType = OAIObj.getListSets();
            if (listSetsType != null) {
                sets = new ArrayList<SetDetailBean>();
                for (Iterator it = listSetsType.getSet().iterator(); it.hasNext();) {
                    SetType elem = (SetType) it.next();
                    SetDetailBean setDetail = new SetDetailBean();
                    setDetail.setName(elem.getSetName());
                    setDetail.setSpec(elem.getSetSpec());
                    sets.add(setDetail);
                }
            }
        }
        return sets;
    }

    private void logException(Throwable e, Logger logger) {

        boolean cause = false;
        String fullMessage = "";
        do {
            String message = e.getClass().getName() + " " + e.getMessage();
            if (cause) {
                message = "\nCaused By Exception.................... " + e.getClass().getName() + " " + e.getMessage();
            }
            StackTraceElement[] ste = e.getStackTrace();
            message += "\nStackTrace: \n";
            for (int m = 0; m < ste.length; m++) {
                message += ste[m].toString() + "\n";
            }
            fullMessage += message;
            cause = true;
        } while ((e = e.getCause()) != null);
        logger.severe(fullMessage);
    }

    public HarvestFormatType findHarvestFormatTypeByMetadataPrefix(String metadataPrefix) {
        String queryStr = "SELECT f FROM HarvestFormatType f WHERE f.metadataPrefix = '" + metadataPrefix + "'";
        Query query = em.createQuery(queryStr);
        List resultList = query.getResultList();
        HarvestFormatType hft = null;
        if (resultList.size() > 1) {
            throw new EJBException("More than one HarvestFormatType found with metadata Prefix= '" + metadataPrefix + "'");
        }
        if (resultList.size() == 1) {
            hft = (HarvestFormatType) resultList.get(0);
        }
        return hft;
    }
}
