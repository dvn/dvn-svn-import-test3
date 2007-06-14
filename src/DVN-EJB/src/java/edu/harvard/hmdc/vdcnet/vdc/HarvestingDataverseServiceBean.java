/*
 * HarvestingDataverseServiceBean.java
 *
 * Created on April 5, 2007, 10:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateless
public class HarvestingDataverseServiceBean implements edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverseServiceLocal {
    @EJB VDCServiceLocal vdcService;
   @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
   
    
    /**
     * Creates a new instance of HarvestingDataverseServiceBean
     */
    public HarvestingDataverseServiceBean() {
    }
    
    public List findAll() {
             return em.createQuery("select object(o) from HarvestingDataverse as o order by o.vdc.name").getResultList();
    }
    
    public HarvestingDataverse find(Long id) {
          HarvestingDataverse hd= em.find(HarvestingDataverse.class,id);
          em.refresh(hd);
          return hd;
    }
    
     public void edit(HarvestingDataverse harvestingDataverse) {
        em.merge(harvestingDataverse);
    }
    
    public void delete(Long hdId){
        HarvestingDataverse hd = em.find(HarvestingDataverse.class, hdId);
        vdcService.delete(hd.getVdc().getId());
        em.remove(hd);
     
    }
    
   @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setHarvestingNow(Long hdId, boolean harvestingNow) {
        HarvestingDataverse hd = em.find(HarvestingDataverse.class,hdId);
        em.refresh(hd);
        hd.setHarvestingNow(harvestingNow);
    }
    
        
    
}
