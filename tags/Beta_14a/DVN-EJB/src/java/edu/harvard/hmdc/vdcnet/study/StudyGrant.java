/*
 * StudyAuthor.java
 *
 * Created on August 7, 2006, 9:49 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class StudyGrant {
    
    /** Creates a new instance of StudyGrant */
    public StudyGrant() {
    }

    /**
     * Holds value of property id.
     */

   
    
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="studygrant_gen") 
    @SequenceGenerator(name="studygrant_gen", sequenceName="studygrant_id_seq")
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
     * Holds value of property agency.
     */
    private String agency;

    /**
     * Getter for property value.
     * @return Value of property value.
     */
    public String getAgency() {
        return this.agency;
    }

    /**
     * Setter for property value.
     * @param value New value of property value.
     */
    public void setAgency(String agency) {
        this.agency = agency;
    }

    /**
     * Holds value of property displayOrder.
     */
    private int displayOrder;

    /**
     * Getter for property order.
     * @return Value of property order.
     */
    public int getDisplayOrder() {
        return this.displayOrder;
    }

    /**
     * Setter for property order.
     * @param order New value of property order.
     */
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
     * Holds value of property study.
     */
    @ManyToOne 
    @JoinColumn(nullable=false)
    private Study study;

    /**
     * Getter for property study.
     * @return Value of property study.
     */
    public Study getStudy() {
        return this.study;
    }

    /**
     * Setter for property study.
     * @param study New value of property study.
     */
    public void setStudy(Study study) {
        this.study = study;
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

    /**
     * Holds value of property number.
     */
    private String number;

    /**
     * Getter for property affiliation.
     * @return Value of property affiliation.
     */
    public String getNumber() {
        return this.number;
    }

    /**
     * Setter for property affiliation.
     * @param affiliation New value of property affiliation.
     */
    public void setNumber(String number) {
        this.number = number;
    }  
    
    public boolean isEmpty() {
        return ((agency==null || agency.trim().equals(""))
            && (number==null || number.trim().equals("")));
    }
  public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyGrant)) {
            return false;
        }
        StudyGrant other = (StudyGrant)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }    
}
