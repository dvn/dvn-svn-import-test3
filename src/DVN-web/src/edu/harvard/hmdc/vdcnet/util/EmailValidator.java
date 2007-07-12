/*
 * EmailValidator.java
 *
 * Created on February 2, 2007, 5:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author wbossons
 */
public class EmailValidator implements Validator {
    
    private String msg = new String("Email Address Validation Error(s):  Please enter a valid email.");
    
    Logger logger = Logger.getLogger("validationLogger");
    
        /** Creates a new instance of EmailValidator */
    public EmailValidator() {
    }
    
    public void validate(FacesContext context, UIComponent component, 
                        Object value) {
        if (value == null) return;
        String email = (String) value;
            if (!validateEmail(email) ) {
                logger.info("Validation Error:  An invalid email was found.");
                FacesMessage message = new FacesMessage(msg);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                ((UIInput)component).setValid(false);
                context.addMessage(component.getClientId(context), message);
                throw new ValidatorException(message);
            }
    }
    
    

    public boolean validateEmail (String email) {
        boolean isValid = true;
        String input = email;
      if (email.indexOf('@') == -1) {
          isValid = false;
          msg += "  A valid email address must contain \"@\".";
          return isValid;
      } 
      //Checks for email addresses starting with
      //inappropriate symbols like dots or @ signs.
      Pattern p = Pattern.compile("^\\.|^\\@");
      Matcher m = p.matcher(input);
      if (m.find()) {
          isValid = false;
          msg += "  An email address cannot start" +
                            " with \".\" or \"@\".";
          return isValid;
      }
      //check to see if there is something after the @
      p = Pattern.compile("\\@$");
      m = p.matcher(input);
      if (m.find()) {
          isValid = false;
          msg += " An email address cannot end with @.";
          return isValid;
      }
      
      //check to see if the domain part of the email address matches the rfc for email addresses
      p = Pattern.compile("\\@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+");
      m = p.matcher(input);
      if (!m.find()) {
          isValid = false;
          msg += "Please check the email address domain. It appears to be incorrect.";
          return isValid;
      }
      //Checks for email addresses that start with
      //www. and prints a message if it does.
      p = Pattern.compile("^www\\.");
      m = p.matcher(input);
      if (m.find()) {
          isValid = false;
          msg += "  An email address cannot start with " +
                " with \"www.\".";
          return isValid;
      }
      //now check for invalid chars in the local part of the email address
      p = Pattern.compile("[^A-Za-z0-9\\.\\@_\\-~#]+");
      m = p.matcher(input);
      StringBuffer sb = new StringBuffer();
      boolean result = m.find();
      boolean deletedIllegalChars = false;

      while(result) {
         deletedIllegalChars = true;
         m.appendReplacement(sb, "");
         result = m.find();
      }
      // Add the last segment of input to the new String
      m.appendTail(sb);
      input = sb.toString();

      if (deletedIllegalChars) {
          isValid = false;
          msg += "  Invalid characters were found" +
                           ".  Check for invalid characters or spaces at the end of the email address.";
      }
      return isValid;
    }
}
