/*
 * OAISetServiceLocal.java
 *
 * Created on Oct 2, 2007, 5:13:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import ORG.oclc.oai.server.verb.NoItemsMatchException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Ellen Kraffmiller
 */
@Local
public interface OAISetServiceLocal extends java.io.Serializable  {
   public OAISet findBySpec(String spec) throws NoItemsMatchException;   
   public void remove(Long id);
   public List findAll();
   public OAISet findById(Long id);
   public void update(OAISet oaiSet);
}
