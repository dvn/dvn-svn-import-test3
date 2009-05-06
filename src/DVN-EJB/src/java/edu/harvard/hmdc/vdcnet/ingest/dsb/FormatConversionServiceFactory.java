/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.ingest.dsb;

/**
 *
 * @author asone
 */
public class FormatConversionServiceFactory {
    /**  */
    public static String FORMAT_CONVERSION_SERVICE_DEFAULT = 
        "edu.harvard.hmdc.vdcnet.ingest.dsb.impl.DvnFormatConversionServiceImpl";

    /**
     * 
     *
     * @param     
     * @return    
     */
    public static FormatConversionService getServiceInstance(String implClassName)
        throws ClassNotFoundException, 
            InstantiationException, IllegalAccessException{
            if (implClassName == null){
                // default fallout class
                return (FormatConversionService)
                    Class.forName("edu.harvard.hmdc.vdcnet.ingest.dsb.impl.MockDvnFormatConversionServiceImpl").newInstance();
            } else {
                return (FormatConversionService)
                    Class.forName(implClassName).newInstance();
            }
    }

}
