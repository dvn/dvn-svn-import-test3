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
 * ReportConstants.java
 *
 * Created on Jul 23, 2007, 10:06:19 AM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.hmdc.vdcnet.web.networkAdmin;


/**
 *
 * @author wbossons
 */
public class ReportConstants implements java.io.Serializable  {

 public ReportConstants() {

 }

 public static void main(String[] args)
 			throws java.io.IOException {
    System.out.println("in the ReportConstants class");
 }

  //Common Strings
  public static final String NEW_LINE = System.getProperty("line.separator");
  public static final String FILE_SEPARATOR = System.getProperty("file.separator");
  public static final String PATH_SEPARATOR = System.getProperty("path.separator");
  public static final String DELIMITER = " ";

  public static final String BEGIN_GENERAL              = "BEGIN_GENERAL";
  public static final String TOTAL_VISITS               = "TotalVisits";
  public static final String TOTAL_VISITS_HEADING 	= "Total number of visits by MIT users.";
  public static final String TOTAL_UNIQUE  		= "TotalUnique";
  public static final String TOTAL_UNIQUE_HEADING  	= "Unique MIT-assigned IP Addresses accessing the IQSS Dataverse Network.";
  //BEGIN_EXTRA_1
  public static final String BEGIN_EXTRA_1 		= "BEGIN_EXTRA_1";
  public static final String NUM_DOWNLOADS_HEADING 	= "Downloads of Data and Documentation from the Documentation, Data and Analysis Page";
  public static final String END_EXTRA_1 		= "END_EXTRA_1";
 //BEGIN_EXTRA_2
  public static final String BEGIN_EXTRA_2 		= "BEGIN_EXTRA_2";
  public static final String NUM_UNIQUEDOWNLOADS_HEADING = "Downloads of Data and Documentation from the Documentation, Data and Analysis Page by Number of IP Address";
  public static final String END_EXTRA_2 		= "END_EXTRA_2";
  //BEGIN_EXTRA_3
  public static final String BEGIN_EXTRA_3 		= "BEGIN_EXTRA_3";
  public static final String NUM_SUBSETJOBS_HEADING     = "Subsetting Interface Views Resulting in One or More Actions of Running an Actual Subsetting/Statistical Job by MIT Users";
  public static final String END_EXTRA_3 		= "END_EXTRA_3";
  //BEGIN_EXTRA_4
  public static final String BEGIN_EXTRA_4 		= "BEGIN_EXTRA_4";
  public static final String NUM_UNIQUESUBSETS_HEADING = "Subsetting Interface Views by Number of MIT-assigned IP Address.";
  public static final String END_EXTRA_4 		= "END_EXTRA_4";

  public static final String COMMA = ",";
  public static final String DOUBLE_QUOTE = "\"";
  public static final String SINGLE_QUOTE = "'";

}
