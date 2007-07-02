/*
 * StudyKeyword.java
 *
 * Created on August 7, 2006, 9:54 AM
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
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class StudySoftware {
    
    /** Creates a new instance of StudySoftware */
    public StudySoftware() {
    }

    /**
     * Holds value of property id.
     */
   @SequenceGenerator(name="studysoftware_gen", sequenceName="studysoftware_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="studysoftware_gen")     
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
     * Holds value of property name.
     */
    private String name;

    /**
     * Getter for property value.
     * @return Value of property value.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for property value.
     * @param value New value of property value.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    @ManyToOne
    private Study study;
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
     * Holds value of property softwareVersion.
     */
    private String softwareVersion;

    /**
     * Getter for property softwareVersion.
     * @return Value of property softwareVersion.
     */
    public String getSoftwareVersion() {
        return this.softwareVersion;
    }

    /**
     * Setter for property softwareVersion.
     * @param softwareVersion New value of property softwareVersion.
     */
    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }
    
      public boolean isEmpty() {
        return ((name==null || name.trim().equals(""))
            && (softwareVersion==null || softwareVersion.trim().equals("")));
    }
  public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudySoftware)) {
            return false;
        }
        StudySoftware other = (StudySoftware)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
}
