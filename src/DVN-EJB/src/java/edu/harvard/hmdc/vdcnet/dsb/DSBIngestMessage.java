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
 * DSBIngestMessage.java
 *
 * Created on March 22, 2007, 4:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.dsb;

import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author gdurand
 */
public class DSBIngestMessage implements Serializable{
    
    /** Creates a new instance of DSBIngestMessage */
    public DSBIngestMessage()  {
    }
    
    private String ingestEmail;
    private Long ingestUserId;
    
    private Long studyId;
    private List fileBeans;

    public String getIngestEmail() {
        return ingestEmail;
    }

    public void setIngestEmail(String ingestEmail) {
        this.ingestEmail = ingestEmail;
    }

    public Long getIngestUserId() {
        return ingestUserId;
    }

    public void setIngestUserId(Long ingestUserId) {
        this.ingestUserId = ingestUserId;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public List getFileBeans() {
        return fileBeans;
    }

    public void setFileBeans(List fileBeans) {
        this.fileBeans = fileBeans;
    }
}
