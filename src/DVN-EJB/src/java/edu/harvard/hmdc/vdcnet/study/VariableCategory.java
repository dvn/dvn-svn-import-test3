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
 * VariableRange.java
 *
 * Created on October 11, 2006, 11:01 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.util.AlphaNumericComparator;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class VariableCategory implements Comparable, java.io.Serializable {
    private static AlphaNumericComparator alphaNumericComparator = new AlphaNumericComparator();
    
    /** Creates a new instance of VariableRange */
    public VariableCategory() {
    }
    
       /**
     * Holds value of property id.
     */
   @SequenceGenerator(name="VariableCategory_gen", sequenceName="VariableCategory_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="VariableCategory_gen")     
    private Long id;

    /**
     * Holds value of property balue.
     */
    private String value;

    /**
     * Getter for property beginValue.
     * @return Value of property beginValue.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Setter for property beginValue.
     * @param beginValue New value of property beginValue.
     */
    public void setValue(String value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Holds value of property label.
     */
    private String label;

    /**
     * Getter for property label.
     * @return Value of property label.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Setter for property label.
     * @param label New value of property label.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Holds value of property missing.
     */
    private boolean missing;

    /**
     * Getter for property missing.
     * @return Value of property missing.
     */
    public boolean isMissing() {
        return this.missing;
    }

    /**
     * Setter for property missing.
     * @param missing New value of property missing.
     */
    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    /**
     * Holds value of property dataVariable.
     */
    @ManyToOne
    private DataVariable dataVariable;

    /**
     * Getter for property dataVariable.
     * @return Value of property dataVariable.
     */
    public DataVariable getDataVariable() {
        return this.dataVariable;
    }

    /**
     * Setter for property dataVariable.
     * @param dataVariable New value of property dataVariable.
     */
    public void setDataVariable(DataVariable dataVariable) {
        this.dataVariable = dataVariable;
    }

    /**
     * Holds value of property frequency.
     */
    private java.lang.Long frequency;

    /**
     * Getter for property frequency.
     * @return Value of property frequency.
     */
    public java.lang.Long getFrequency() {
        return this.frequency;
    }

    /**
     * Setter for property frequency.
     * @param frequency New value of property frequency.
     */
    public void setFrequency(java.lang.Long frequency) {
        this.frequency = frequency;
    }

   
 public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VariableCategory)) {
            return false;
        }
        VariableCategory other = (VariableCategory)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }             

 public int compareTo(Object obj) {
        VariableCategory ss = (VariableCategory)obj;     
        return alphaNumericComparator.compare(this.getValue(),ss.getValue());
        
    }    
}
