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

package edu.harvard.iq.dvn.core.admin;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Local;


/**
 * This is the business interface for EditUserService enterprise bean.
 */
@Local
public interface EditUserService extends java.io.Serializable  {
    public Long getRequestStudyId();
    public void setRequestStudyId(Long studyId);
    public String getNewPassword1();
    public void setNewPassword1(String password);
    public String getNewPassword2();
    public void setNewPassword2(String password);
    public String getCurrentPassword();
    public void setCurrentPassword(String password);    
    public void setUser(Long id);
    public void newUser();
    public void cancel();
    public void save();
    public void save(Long contributorRequestVdcId, boolean creatorRequest, Long studyRequestId);
    public VDCUser getUser();
    public void deleteUser();

    
}
