/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author roberttreacy
 */
public class IndexLockFileNameFilter implements FilenameFilter{

    public boolean accept(File dir, String name) {
        return (name.endsWith("write.lock"));
    }

}
