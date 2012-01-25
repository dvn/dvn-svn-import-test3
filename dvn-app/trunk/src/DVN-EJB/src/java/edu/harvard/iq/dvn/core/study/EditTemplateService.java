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

package edu.harvard.iq.dvn.core.study;

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
public interface EditTemplateService extends java.io.Serializable { 
    public void setTemplate( Long templateId);
    public void newTemplate(Long vdcId);
    public void newTemplate(Long vdcId,Long studyVersionId);
    public void cancel();
    public void save();
    public Template getTemplate();
    public void removeCollectionElement(Collection coll, Object elem);
    void removeCollectionElement(Iterator iter, Object elem);
    public void changeRecommend(TemplateField tf, boolean isRecommended);
    public void changeFieldInputLevel(TemplateField tf, String inputLevel);

    public void newNetworkTemplate();

    public void newClonedTemplate(Long vdcId, Template cloneSource);
   
 
}
