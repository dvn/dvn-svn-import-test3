/*
 * DDIServiceLocal.java
 *
 * Created on Jan 11, 2008, 3:08:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.ddi;

import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyExporter;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import java.io.File;
import java.io.OutputStream;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author Gustavo
 */
@Local
public interface DDIServiceLocal extends StudyExporter, java.io.Serializable {
    void mapDDI(String xmlToParse, Study study);
    void mapDDI(File ddiFile, Study study);
    Map determineId(File ddiFile);
    void exportDataFile(StudyFile sf, OutputStream out);
}
