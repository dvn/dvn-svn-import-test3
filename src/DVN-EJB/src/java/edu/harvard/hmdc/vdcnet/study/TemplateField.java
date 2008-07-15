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
 * TemplateField.java
 *
 * Created on August 2, 2006, 4:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.util.FieldInputLevelConstant;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class TemplateField implements java.io.Serializable {


    /**
     * Creates a new instance of TemplateField
     */
    public TemplateField() {
    }

    public TemplateField(FieldInputLevel inputLevel) {
        this.fieldInputLevel=inputLevel;
    }
    
    private String defaultValue;

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Holds value of property selectValues.
     */
    @OneToMany (mappedBy="templateField", cascade=CascadeType.PERSIST)
    private java.util.Collection<TemplateFieldSelectValue> selectValues;

    /**
     * Getter for property selectValues.
     * @return Value of property selectValues.
     */
    public java.util.Collection<TemplateFieldSelectValue> getSelectValues() {
        return this.selectValues;
    }

    /**
     * Setter for property selectValues.
     * @param selectValues New value of property selectValues.
     */
    public void setSelectValues(java.util.Collection<TemplateFieldSelectValue> selectValues) {
        this.selectValues = selectValues;
    }

    /**
     * Holds value of property fieldInputLevel.
     */
    @ManyToOne
    private FieldInputLevel fieldInputLevel;

    /**
     * Getter for property fieldInputLevel.
     * @return Value of property fieldInputLevel.
     */
    public FieldInputLevel getFieldInputLevel() {
        return this.fieldInputLevel;
    }

    /**
     * Setter for property fieldInputLevel.
     * @param fieldInputLevel New value of property fieldInputLevel.
     */
    public void setFieldInputLevel(FieldInputLevel fieldInputLevel) {
        this.fieldInputLevel = fieldInputLevel;
    }

    /**
     * Holds value of property template.
     */
    @ManyToOne
    private Template template;

    /**
     * Getter for property template.
     * @return Value of property template.
     */
    public Template getTemplate() {
    return null;}

    /**
     * Setter for property template.
     * @param template New value of property template.
     */
    public void setTemplate(Template template) {
    }

    /**
     * Holds value of property id.
     */
    @SequenceGenerator(name="templatefield_gen", sequenceName="templatefield_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="templatefield_gen")     
    private Long id;

    /**
     * Getter for property id.
     * @return Value of property id.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Setter for property id.
     * @param id New value of property id.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Holds value of property studyField.
     */
    @ManyToOne
    private StudyField studyField;

    /**
     * Getter for property studyField.
     * @return Value of property studyField.
     */
    public StudyField getStudyField() {
        return this.studyField;
    }

    /**
     * Setter for property studyField.
     * @param studyField New value of property studyField.
     */
    public void setStudyField(StudyField studyField) {
        this.studyField = studyField;
    }


  /**
     * Holds value of property version.
     */
    @Version
    private Long version; 

    /**
     * Getter for property version.
     * @return Value of property version.
     */
    public Long getVersion() {
        return this.version;
    }

    /**
     * Setter for property version.
     * @param version New value of property version.
     */
    public void setVersion(Long version) {
        this.version = version;
    }
    
    public boolean isRequired() {
        return fieldInputLevel.getName().equals(FieldInputLevelConstant.getRequired()); 
    }

    public boolean isRecommended() {
        return fieldInputLevel.getName().equals(FieldInputLevelConstant.getRecommended()); 
    }
    
    public boolean isOptional() {
        return fieldInputLevel.getName().equals(FieldInputLevelConstant.getOptional()); 
    }
    
    
   public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TemplateField)) {
            return false;
        }
        TemplateField other = (TemplateField)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }            

}
