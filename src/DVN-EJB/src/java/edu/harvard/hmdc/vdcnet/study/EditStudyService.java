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

package edu.harvard.hmdc.vdcnet.study;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Local;

/**
 * This is the business interface for EditStudyService enterprise bean.
 */
@Local
public interface EditStudyService extends java.io.Serializable { 
    public void setStudy( Long studyId);
    public void newStudy(Long vdcId, Long userId, Long templateId);
    public void cancel();
    public void save(Long vdcId, Long userId);
    public Study getStudy();
    public void deleteStudy();
    public void removeCollectionElement(Collection coll, Object elem);
    public void removeCollectionElement(List list,int index);
    public void removeCollectionElement(Iterator iter, Object elem);
    
    java.util.List getCurrentFiles();

    void setCurrentFiles(List currentFiles);

    java.util.List getNewFiles();

    void setNewFiles(List newFiles);

  
    
    public HashMap getStudyMap();
    public void setStudyMap(HashMap studyMap);

    void setIngestEmail(String ingestEmail);
    
    public boolean isNewStudy();
    
    public void changeTemplate(Long templateId);


}
