/*
 * DVNVersionServiceLocal.java
 * 
 * Created on Sep 20, 2007, 10:24:41 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import javax.ejb.Local;

/**
 *
 * @author roberttreacy
 */
@Local
public interface DVNVersionServiceLocal {

    public edu.harvard.hmdc.vdcnet.admin.DVNVersion getLatestVersion();
    
}
