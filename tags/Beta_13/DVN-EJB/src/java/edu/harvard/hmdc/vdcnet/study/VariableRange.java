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

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class VariableRange {
    
    /** Creates a new instance of VariableRange */
    public VariableRange() {
    }
    
       /**
     * Holds value of property id.
     */
   @SequenceGenerator(name="variablerange_gen", sequenceName="variablerange_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="variablerange_gen")     
    private Long id;

    /**
     * Holds value of property beginValue.
     */
    private String beginValue;

    /**
     * Getter for property beginValue.
     * @return Value of property beginValue.
     */
    public String getBeginValue() {
        return this.beginValue;
    }

    /**
     * Setter for property beginValue.
     * @param beginValue New value of property beginValue.
     */
    public void setBeginValue(String beginValue) {
        this.beginValue = beginValue;
    }

    /**
     * Holds value of property endValue.
     */
    private String endValue;

    /**
     * Getter for property endValue.
     * @return Value of property endValue.
     */
    public String getEndValue() {
        return this.endValue;
    }

    /**
     * Setter for property endValue.
     * @param endValue New value of property endValue.
     */
    public void setEndValue(String endValue) {
        this.endValue = endValue;
    }

    /**
     * Holds value of property beginValueType.
     */
    @ManyToOne
    private VariableRangeType beginValueType;

    /**
     * Getter for property beginValueType.
     * @return Value of property beginValueType.
     */
    public VariableRangeType getBeginValueType() {
        return this.beginValueType;
    }

    /**
     * Setter for property beginValueType.
     * @param beginValueType New value of property beginValueType.
     */
    public void setBeginValueType(VariableRangeType beginValueType) {
        this.beginValueType = beginValueType;
    }

    /**
     * Holds value of property endValueType.
     */
    @ManyToOne
    private VariableRangeType endValueType;

    /**
     * Getter for property endValueType.
     * @return Value of property endValueType.
     */
    public VariableRangeType getEndValueType() {
        return this.endValueType;
    }

    /**
     * Setter for property endValueType.
     * @param endValueType New value of property endValueType.
     */
    public void setEndValueType(VariableRangeType endValueType) {
        this.endValueType = endValueType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof VariableRange)) {
            return false;
        }
        VariableRange other = (VariableRange)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
   
}
