/*
 * Dataverse Network - A web application to distribute, share and
 * analyze quantitative data.
 * Copyright (C) 2009
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
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

package edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata;

import java.util.*;

/**
 *
 * @author Akio Sone
 */
public final class SPSSConstants {

    private SPSSConstants() {
    }

    private static final int[] FORMAT_KEYS_SAV = {
      0,  1,  2,  3,  4,  5,  6,  7,  8,  9,
     10, 11, 12, 15, 16, 17,
     20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
     30, 31, 32, 33, 34, 35, 36, 37, 38, 39};
     
     
     private static final int[] FORMAT_KEYS_POR = {
          0,  1,  2,  3,  4,  5,  6,  7,  8,  9,
         10, 11, 12, 15, 16, 17,
        102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 
        112, 113, 114, 115, 116, 117, 118, 119, 120, 121};

    private static final String[] FORMAT_VALUES ={
    "CONTINUE","A", "AHEX","COMMA","DOLLAR","F","IB","PIBHEX","P","PIB",
    "PK", "RB", "RBHEX","Z","N","E", "DATE",
    "TIME", "DATETIME","ADATE","JDATE","DTIME","WKDAY","MONTH","MOYR","QYR",
    "WKYR","PCT","DOT","CCA","CCB","CCC","CCD","CCE","EDATE","SDATE"};

    private static final String[] FORMAT_CATEGORIES = {
    "other","other","other","other","currency","other","other","other","other","other",
    "other","other","other","other","other","other","date",
    "time","time","date","date","time","other","other","date","date","date",
    "other","other","currency","currency","currency","currency","currency","date","date"};

    public static Map<Integer, String> FORMAT_CODE_TABLE_SAV =
            new LinkedHashMap<Integer, String>();
            
    public static Map<Integer, String> FORMAT_CODE_TABLE_POR =
            new LinkedHashMap<Integer, String>();
            
    public static Map<String, String> FORMAT_CATEGORY_TABLE =
            new LinkedHashMap<String, String>();

    private static List<Integer> ORDINARY_FORMAT_CODE = Arrays.asList(0, 1, 5);

    public static final Set<Integer> ORDINARY_FORMAT_CODE_SET =
            new LinkedHashSet<Integer>(ORDINARY_FORMAT_CODE);

    static{
        for (int i=0; i< FORMAT_KEYS_SAV.length; i++){
            FORMAT_CODE_TABLE_SAV.put(FORMAT_KEYS_SAV[i], FORMAT_VALUES[i]);
            FORMAT_CODE_TABLE_POR.put(FORMAT_KEYS_POR[i], FORMAT_VALUES[i]);
            FORMAT_CATEGORY_TABLE.put(FORMAT_VALUES[i], FORMAT_CATEGORIES[i]);

        }
    }


}
