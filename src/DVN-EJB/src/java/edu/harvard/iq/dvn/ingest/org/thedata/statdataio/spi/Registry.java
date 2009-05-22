/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2009
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
 *  along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi;

import java.util.*;
import static java.lang.System.*;
/**
 *
 * @author akio sone
 */
public class Registry {
    

    /**
     * 
     */
    public static Registry REGISTRY = new Registry();

   private static Map<String, Object> map = new HashMap<String, Object>();

   private Registry() {
       //out.println("Registry constructor is called");
   }

   /**
    *
    * @param classname
    * @return
    */
   public static synchronized Object getInstance(String classname) {

      Object singleton = map.get(classname);

      if(singleton != null) {
         return singleton;
      }
      try {
         singleton = Class.forName(classname).newInstance();
      } catch(ClassNotFoundException cnf) {
          cnf.printStackTrace();
      } catch(InstantiationException ie) {
          ie.printStackTrace();
      } catch(IllegalAccessException ia) {
          ia.printStackTrace();
      } catch(Exception e) {
         e.printStackTrace();
      }
      map.put(classname, singleton);
      //out.println("passing Registry.getInstance()");
      return singleton;
   }

   /**
    *
    * @param classname
    * @return
    */
   public static boolean isThisClassRegistered(String classname){
        //out.println("classname="+classname);
        Object singleton = map.get(classname);
        if (singleton != null){
           return true;
        } else {
           return false;
        }
    }
}
