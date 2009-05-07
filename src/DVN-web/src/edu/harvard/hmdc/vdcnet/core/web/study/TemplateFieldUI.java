/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.core.web.study;

import edu.harvard.hmdc.vdcnet.core.study.TemplateField;
import edu.harvard.hmdc.vdcnet.core.study.TemplateServiceLocal;
import edu.harvard.hmdc.vdcnet.core.util.FieldInputLevelConstant;
import javax.naming.InitialContext;

/**
 *
 * @author Ellen Kraffmiller
 */
public class TemplateFieldUI {
    
    public TemplateFieldUI() {}
    
    public TemplateFieldUI(TemplateField tf) {
        templateField=tf;
    }

    private TemplateField templateField;

    public TemplateField getTemplateField() {
        return templateField;
    }

    public void setTemplateField(TemplateField templateField) {
        this.templateField = templateField;
    }
    
    public boolean isRecommended() {
        return templateField.isRecommended();
    }
    
    public void setRecommended(boolean isRecommended) {
       // finally, add types from db
        TemplateServiceLocal templateService = null;
        try {
            templateService = (TemplateServiceLocal) new InitialContext().lookup("java:comp/env/templateService");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isRecommended) {
              templateField.setFieldInputLevel(templateService.getFieldInputLevel(FieldInputLevelConstant.getRecommended()));
          } else {
              templateField.setFieldInputLevel(templateService.getFieldInputLevel(FieldInputLevelConstant.getOptional()));
          }    }
    
    

}
