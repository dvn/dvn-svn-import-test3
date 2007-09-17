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
 * StringUtil.java
 *
 * Created on October 4, 2006, 11:07 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Ellen Kraffmiller
 */
public final class StringUtil {
    
   
    public static final boolean isEmpty(String str) {
        if (str==null || str.trim().equals("")) {
            return true;
        } else {
            return false;
        }        
    }

    public static String truncateString(String originalString, int maxLength) {
        maxLength = Math.max( 0, maxLength);
        String finalString = originalString;
        if (finalString != null && finalString.length() > maxLength ) {
             try {
                String regexp = "[A-Za-z0-9][\\p{Space}]";
                Pattern pattern = Pattern.compile(regexp);
                String startParsedString = finalString.substring(0, maxLength);
                String endParsedString   = finalString.substring(maxLength, finalString.length());
                Matcher matcher          = pattern.matcher(endParsedString);
                boolean found            = matcher.find();
                endParsedString          = endParsedString.substring(0, matcher.end());
                finalString              = "<div>" + startParsedString + endParsedString + "<span class='dvn_threedots'>...</span></div>";

             } catch (Exception e) {
                System.out.println("An issue occurred truncating the following String: " + originalString);
            }
        } 
        
        return finalString;             
    }    
    
}
