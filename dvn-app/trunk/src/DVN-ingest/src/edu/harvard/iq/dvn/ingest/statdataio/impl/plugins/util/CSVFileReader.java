/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
package edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.util;

import java.io.*;
import java.util.logging.*;
import org.apache.commons.lang.StringUtils;


import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.data.*;

/**
 * This is a reader for a CSV (character-separated values) data file.
 * Note that this is not a fully-functional data file reader plugin
 * (it doesn't extend StatDataFileReader), this class only reads the
 * data (i.e., DataTable) from a plain text file. The metadata describing
 * the data set and its variables should be supplied elsewhere.
 * (For example, via an SPSS control card; the assumption is that in the
 * future we'll be offering support for other data-less metadata declarations,
 * then all these different readers will be able to use this data reader)
 *
 * @author Leonid Andreev
 *
 */
public class CSVFileReader implements java.io.Serializable {
    private char delimiterChar='\t';

    private static Logger dbgLog =
       Logger.getLogger(CSVFileReader.class.getPackage().getName());


    public CSVFileReader () {
    }

    public CSVFileReader (char delimiterChar) {
        this.delimiterChar = delimiterChar;
    }


    // version of the read method that parses the CSV file and stores
    // its content in the data table matrix (in memory).
    // TODO: remove this method.
    // Only the version that reads the file and stores it in a TAB file
    // should be used.


  public int read(BufferedReader csvReader, SDIOMetadata smd, PrintWriter pwout) throws IOException {
    dbgLog.warning("CSVFileReader: Inside CSV File Reader");
      
        //DataTable csvData = new DataTable();
        int varQnty = 0;

        try {
            varQnty = new Integer(smd.getFileInformation().get("varQnty").toString());
        } catch (Exception ex) {
            //return -1;
            throw new IOException ("CSV File Reader: Could not obtain varQnty from the dataset metadata.");
        }

        if (varQnty == 0) {
            //return -1;
            throw new IOException ("CSV File Reader: varQnty=0 in the dataset metadata!");
        }

        String[] caseRow = new String[varQnty];

        String line;
        String[] valueTokens;

        int lineCounter = 0;

        boolean[] isCharacterVariable = smd.isStringVariable();
        boolean[] isContinuousVariable = smd.isContinuousVariable();
        boolean[] isDateVariable = smd.isDateVariable(); 


        dbgLog.fine("CSV reader; varQnty: "+varQnty);
        dbgLog.fine("CSV reader; delimiter: "+delimiterChar);

        while ((line = csvReader.readLine()) != null) {
            // chop the line:
            line = line.replaceFirst("[\r\n]*$", "");
            valueTokens = line.split(""+delimiterChar, -2);

            if (valueTokens == null) {
                throw new IOException("Failed to read line "+(lineCounter+1)+" of the Data file.");

            }

            if (valueTokens.length != varQnty) {
                throw new IOException("Reading mismatch, line "+(lineCounter+1)+" of the Data file: " +
                        varQnty + " delimited values expected, "+valueTokens.length+" found.");
            }

            //dbgLog.fine("case: "+lineCounter);

            for ( int i = 0; i < varQnty; i++ ) {
                //dbgLog.fine("value: "+valueTokens[i]);

                if (isCharacterVariable[i]) {
                    // String. Adding to the table, quoted.
                    // Empty strings stored as " " (one white space):
                    if (valueTokens[i] != null && (!valueTokens[i].equals(""))) {
                        String charToken = valueTokens[i];
                        // Dealing with quotes: 
                        // remove the leading and trailing quotes, if present:
                        charToken = charToken.replaceFirst("^\"", "");
                        charToken = charToken.replaceFirst("\"$", "");
                        // escape the remaining ones:
                        charToken = charToken.replace("\"", "\\\"");
                        // final pair of quotes:
                        if (isDateVariable==null || (!isDateVariable[i])) {
                            charToken = "\"" + charToken + "\"";
                        }
                        caseRow[i] = charToken;
                    } else {
                        if (isDateVariable==null || (!isDateVariable[i])) {
                           caseRow[i] = "\" \"";
                        } else {
                           caseRow[i] = ""; 
                        }
                    }

                } else if (isContinuousVariable[i]) {
                    // Numeric, Double:
                    try {
                        Double testDoubleValue = new Double(valueTokens[i]);
                        caseRow[i] = testDoubleValue.toString();//valueTokens[i];
                    } catch (Exception ex) {
                        dbgLog.fine("caught exception reading numeric value; variable: "+i+", case: "+lineCounter+"; value: "+valueTokens[i]);

                        //dataTable[i][lineCounter] = (new Double(0)).toString();
                        caseRow[i] = "";
                    }
                } else {
                    // Numeric, Integer:
                    try {
                        Integer testIntegerValue = new Integer(valueTokens[i]);
                        caseRow[i] = testIntegerValue.toString();
                    } catch (Exception ex) {
                        dbgLog.fine("caught exception reading numeric value; variable: "+i+", case: "+lineCounter+"; value: "+valueTokens[i]);

                        //dataTable[i][lineCounter] = "0";
                        caseRow[i] = "";
                    }
                }
            }

            pwout.println(StringUtils.join(caseRow, "\t"));

            lineCounter++;
        }

        //csvData.setData(dataTable);
        //return csvData;

        pwout.close();
        return lineCounter;
    }

}