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

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import org.apache.commons.lang.builder.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class SummaryStatistic implements java.io.Serializable {
    
    /** Creates a new instance of VariableRange */
    public SummaryStatistic() {
    }
    
       /**
     * Holds value of property id.
     */
   @SequenceGenerator(name="summarystatistic_gen", sequenceName="summarystatistic_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="summarystatistic_gen")     
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
     * Holds value of property type.
     */
    @ManyToOne
    private SummaryStatisticType type;

    /**
     * Getter for property type.
     * @return Value of property type.
     */
    public SummaryStatisticType getType() {
        return this.type;
    }

    /**
     * Setter for property type.
     * @param type New value of property type.
     */
    public void setType(SummaryStatisticType type) {
        this.type = type;
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

  public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SummaryStatistic)) {
            return false;
        }
        SummaryStatistic other = (SummaryStatistic)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
            ToStringStyle.MULTI_LINE_STYLE);
    }
}
