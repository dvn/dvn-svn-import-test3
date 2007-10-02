/*
 * OAISetServiceBean.java
 *
 * Created on Oct 2, 2007, 5:13:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateless
public class OAISetServiceBean implements OAISetServiceLocal {
   @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em; 
    
    
    public OAISet findBySpec(String spec) {
     String query="SELECT o from OAISelect o where o.spec = :fieldName";
       OAISet oaiSet=null;
       try {
           oaiSet=(OAISet)em.createQuery(query).setParameter("fieldName",spec).getSingleResult();
       } catch (javax.persistence.NoResultException e) {
           // Do nothing, just return null. 
       }
       return oaiSet;
    }
    public List findAll() {
        return em.createQuery("select object(o) from OAISet as o order by o.name").getResultList();
    }
    
}
