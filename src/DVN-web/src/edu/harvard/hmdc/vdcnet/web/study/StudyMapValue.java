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
 * StudyMapValue.java
 *
 * Created on October 6, 2006, 2:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.study;

import edu.harvard.hmdc.vdcnet.study.TemplateField;

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyMapValue implements java.io.Serializable  {
    
    /**
     * Creates a new instance of StudyMapValue
     */
    public StudyMapValue() {
    }
    
   

    /**
     * Holds value of property rendered.
     */
    private boolean rendered;

    /**
     * Getter for property rendered.
     * @return Value of property rendered.
     */
    public boolean isRendered() {
        return this.rendered;
    }

    /**
     * Setter for property rendered.
     * @param rendered New value of property rendered.
     */
    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    /**
     * Holds value of property templateField.
     */
    private TemplateField templateField;

    /**
     * Getter for property templateField.
     * @return Value of property templateField.
     */
    public TemplateField getTemplateField() {
        return this.templateField;
    }

    /**
     * Setter for property templateField.
     * @param templateField New value of property templateField.
     */
    public void setTemplateField(TemplateField templateField) {
        this.templateField = templateField;
    }
    
    public boolean isRequired() {
        return templateField.isRequired();
    }
    
    public boolean isRecommended() {
        return templateField.isRecommended();
    }
      public boolean isOptional() {
        return templateField.isOptional();
    }
   
}
