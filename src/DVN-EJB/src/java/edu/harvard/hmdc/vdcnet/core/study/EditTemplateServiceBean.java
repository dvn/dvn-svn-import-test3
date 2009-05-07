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
 * EditStudyServiceBean.java
 *
 * Created on September 29, 2006, 1:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.core.study;

import edu.harvard.hmdc.vdcnet.core.util.FieldInputLevelConstant;
import edu.harvard.hmdc.vdcnet.core.vdc.VDC;
import edu.harvard.hmdc.vdcnet.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.hmdc.vdcnet.core.vdc.VDCServiceLocal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EditTemplateServiceBean implements edu.harvard.hmdc.vdcnet.core.study.EditTemplateService, java.io.Serializable {
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB TemplateServiceLocal templateService;
    @PersistenceContext(type = PersistenceContextType.EXTENDED,unitName="VDCNet-ejbPU")
    EntityManager em;
    Template template;
    private boolean newTemplate=false;
    private Long createdFromStudyId;
  
    
    /**
     *  Initialize the bean with a Template for editing
     */
    public void setTemplate(Long id ) {
        template = em.find(Template.class,id);
        if (template==null) {
            throw new IllegalArgumentException("Unknown template id: "+id);
        }
        
      
   
    }
    
    public void newTemplate(Long vdcId ) {
        newTemplate=true;   
         
        template = new Template();
        em.persist(template);
       System.out.println("Persist after init, member template");    
    
 
        
       initTemplate( vdcId);
      
     
    }
    public void  newTemplate(Long vdcId, Long studyId) {
        newTemplate=true;
        template = new Template();

        initTemplate( vdcId);
        Study study = em.find(Study.class, studyId);
        study.getMetadata().copyMetadata(template.getMetadata());
        template.getMetadata().setDateOfDeposit("");

        em.persist(template);
        createdFromStudyId=studyId;
      
    }
    
    public void removeCollectionElement(Collection coll, Object elem) {
        coll.remove(elem);
        em.remove(elem);
    }
    
    public void removeCollectionElement(Iterator iter, Object elem) {
        iter.remove();
        em.remove(elem);
    }
    
    public  Template getTemplate() {
        return template;
    }
    
    
    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteStudy() {
       em.remove(template);
        
    }
    
    private HashMap studyMap;
    public HashMap getStudyMap() {
        return studyMap;
    }
    
    public void setStudyMap(HashMap studyMap) {
        this.studyMap=studyMap;
    }
    
    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void save() {
      
      // Don't need to do anything specific, just commit the transaction 
  
      
    }
    
    
    /**
     * Remove this Stateful Session bean from the EJB Container without
     * saving updates to the database.
     */
    @Remove
    public void cancel() {
        
    }
    
  
   
    
    /**
     * Creates a new instance of EditStudyServiceBean
     */
    public EditTemplateServiceBean() {
    }
    
 
    
    
   
    
 
    
    public boolean isNewTemplate() {
        return newTemplate;
    }
    
    public void setCreatedFromStudy(Long createdFromStudyId) {
        this.createdFromStudyId=createdFromStudyId;
    }
    
    public Long getCreatedFromStudyId() {
        return createdFromStudyId;
    }
   
    /**
     * Get the default template for the given dataverse, and add fields to the 
     *  new template based on the default template. 
     */
    private void addFields(Long vdcId) {
        
        VDC vdc = em.find(VDC.class, vdcId);
        Collection<TemplateField> defaultFields = vdc.getDefaultTemplate().getTemplateFields();
       
        template.setTemplateFields(new ArrayList());
        for( TemplateField defaultField: defaultFields) {
            TemplateField tf = new TemplateField();
            tf.setDefaultValue(defaultField.getDefaultValue());
            tf.setFieldInputLevel(defaultField.getFieldInputLevel());
            tf.setStudyField(defaultField.getStudyField());
            tf.setTemplate(template);
            template.getTemplateFields().add(tf);
        }
        
       
   
        
   
    }
     
   
    private void initTemplate( Long vdcId) {
       // Template template = new Template();
           
        VDC vdc = em.find(VDC.class, vdcId);      
        template.setVdc(vdc);
        vdc.getTemplates().add(template);
        addFields(vdcId);
     
    }
    
     public void changeRecommend(TemplateField tf, boolean isRecommended) {
          if (isRecommended) {
              tf.setFieldInputLevel(templateService.getFieldInputLevel(FieldInputLevelConstant.getRecommended()));
          } else {
              tf.setFieldInputLevel(templateService.getFieldInputLevel(FieldInputLevelConstant.getOptional()));
          }
     }
    
}

