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

package edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.spss;

import java.io.*;
import java.util.logging.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.security.NoSuchAlgorithmException;


import org.apache.commons.lang.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.data.*;
import edu.harvard.iq.dvn.unf.*;


import edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.util.*;
import java.text.*;


/**
 * A DVN-Project-implementation of <code>StatDataFileReader</code> for the 
 * SPSS Control Card format.
 * 
 * @author Leonid Andreev
 *
 * implemented based on Akio Sone's implementation of the SPSS/SAV format reader
 * and his older SPSS control card parser implementation in Perl.
 */
public class SPSSFileReader extends StatDataFileReader{


    // static fields:

    private static String[] FORMAT_NAMES = {"spss", "SPSS"};
    private static String[] EXTENSIONS = {"spss", "sps"};
    private static String[] MIME_TYPE = {"text/plain"};


    private static String SPSS_CONTROL_CARD_SIGNATURE = "$FL2";

    private static Logger dbgLog =
       Logger.getLogger(SPSSFileReader.class.getPackage().getName());

    private static final Map<String, String> formatCategoryTable = new HashMap<String, String>();
    private static final List<String> commandNames = new ArrayList<String>();
    
    private static String unfVersionNumber = "5";

    // global variables:

    private int caseQnty = 0;
    private int varQnty = 0;
    private char delimiterChar;


    private Map<String, String> commandStrings = new HashMap<String, String>();
    private List<String> variableNameList = new ArrayList<String>();
    private Map<String, Integer> unfVariableTypes = new HashMap<String, Integer>();

    SDIOMetadata smd = new SPSSMetadata();
    DataTable csvData = null;
    SDIOData sdiodata = null;

    NumberFormat doubleNumberFormatter = new DecimalFormat();
    String csvFileName = null;


    /**
     * Constructs a <code>SPSSFileReader</code> instance with a
     * <code>StatDataFileReaderSpi</code> object.
     *
     * @param originator a <code>StatDataFileReaderSpi</code> object.
     */

    public SPSSFileReader(StatDataFileReaderSpi originator) {
        super(originator);
        init();
    }

    
    //String[] variableFormatTypeList= null;
    //Map<String, String> printFormatTable = new LinkedHashMap<String, String>();
    //Map<String, String> printFormatNameTable = new LinkedHashMap<String, String>();



    static {
        commandNames.add("DataList");
        commandNames.add("VarLabel");
        commandNames.add("ValLabel");
        commandNames.add("MisValue");

        // also:
        // RECODE
        // FORMATS

        formatCategoryTable.put("","");
        formatCategoryTable.put("CONTINUE","other");
        formatCategoryTable.put("A","other");
        formatCategoryTable.put("AHEX","other");
        formatCategoryTable.put("COMMA","other");
        formatCategoryTable.put("DOLLAR","currency");
        formatCategoryTable.put("F","other");
        formatCategoryTable.put("IB","other");
        formatCategoryTable.put("PIBHEX","other");
        formatCategoryTable.put("P","other");
        formatCategoryTable.put("PIB","other");
        formatCategoryTable.put("PK","other");
        formatCategoryTable.put("RB","other");
        formatCategoryTable.put("RBHEX","other");
        formatCategoryTable.put("Z","other");
        formatCategoryTable.put("N","other");
        formatCategoryTable.put("E","other");
        formatCategoryTable.put("DATE","date");
        formatCategoryTable.put("TIME","time");
        formatCategoryTable.put("DATETIME","time");
        formatCategoryTable.put("ADATE","date");
        formatCategoryTable.put("JDATE","date");
        formatCategoryTable.put("DTIME","time");
        formatCategoryTable.put("WKDAY","other");
        formatCategoryTable.put("MONTH","other");
        formatCategoryTable.put("MOYR","date");
        formatCategoryTable.put("QYR","date");
        formatCategoryTable.put("WKYR","date");
        formatCategoryTable.put("PCT","other");
        formatCategoryTable.put("DOT","other");
        formatCategoryTable.put("CCA","currency");
        formatCategoryTable.put("CCB","currency");
        formatCategoryTable.put("CCC","currency");
        formatCategoryTable.put("CCD","currency");
        formatCategoryTable.put("CCE","currency");
        formatCategoryTable.put("EDATE","date");
        formatCategoryTable.put("SDATE","date");

    }
    
    private void init(){
        doubleNumberFormatter.setGroupingUsed(false);
        doubleNumberFormatter.setMaximumFractionDigits(340);

    }
    
    void setCaseQnty (int cQ) {
        caseQnty = cQ; 
    }

    void setVarQnty (int vQ) {
        varQnty = vQ;
    }

    void setDelimiterChar (char c) {
        delimiterChar = c; 
    }

    void setCsvFileName (String fn) {
        csvFileName = fn;
    }

    int getCaseQnty () {
        return caseQnty;
    }

    int getVarQnty () {
        return varQnty;
    }

    char getDelimiterChar () {
        return delimiterChar;
    }

    String getCsvFileName () {
        return csvFileName;
    }

    // Methods ---------------------------------------------------------------//
    /**
     * Read the given SPSS Control Card via a <code>BufferedInputStream</code>
     * object.  This method calls an appropriate method associated with the given 
     * field header by reflection.
     * 
     * @param stream a <code>BufferedInputStream</code>.
     * @return an <code>SDIOData</code> object
     * @throws java.io.IOException if a reading error occurs.
     */
    @Override
    public SDIOData read(BufferedInputStream cardStream) throws IOException {

        dbgLog.info("***** SPSSFileReader: read() start *****");
	    

        // TODO:
        // catch specific exceptions, provide quality diagnostics

        getSPSScommandLines(cardStream);

        smd.getFileInformation().put("mimeType", MIME_TYPE[0]);
        smd.getFileInformation().put("fileFormat", MIME_TYPE[0]);
        smd.getFileInformation().put("varFormat_schema", "SPSS");


        // Now we have the control card pre-parsed and the individual
        // parts of the card ("commands") stored separately.
        // Now we can go through these parts and evaluate the commands,
        // which is going to give us the metadata describing the data set.
        //
        // These are the parts of the SPSS data definition card:
        // DATA LIST (v?)
        // VAR LABELS (v)
        // VALUE LABELS (v)
        // FORMATS
        // MISSING VALUES (v)
        // RECODE

        int readStatus = 0;

        readStatus = read_DataList(commandStrings.get("DataList"));
        dbgLog.fine ("reading DataList. status: "+readStatus);

        readStatus = read_VarLabels(commandStrings.get("VarLabel"));
        dbgLog.fine ("reading VarLabels. status: "+readStatus);

        readStatus = read_ValLabels(commandStrings.get("ValLabel"));
        dbgLog.fine ("reading ValLabels. status: "+readStatus);

        readStatus = read_MisValues(commandStrings.get("MisValue"));
        dbgLog.fine ("reading MisValues. status: "+readStatus);

        // Now read the data file:

        CSVFileReader  csvFileReader = new CSVFileReader (getDelimiterChar());
        BufferedReader csvRd = new BufferedReader(new InputStreamReader(new FileInputStream(getCsvFileName())));

        csvData = csvFileReader.read(csvRd, smd);
        

        // Calculate the datasets statistics, summary and category, and 
        // the UNF signatures:

        calculateDatasetStats(csvData);

        // Create and return the SDIOData object:
        sdiodata = new SDIOData(smd, csvData);

        dbgLog.info("***** SPSSFileReader: read() end *****");


        return sdiodata;
    }

    // This method reads the card file and separates the individual parts (commands)
    // for further parsing.

    private void getSPSScommandLines (BufferedInputStream cardStream) throws IOException {
        dbgLog.fine("start dividing SPSS data definition file");
        int counter = 0;

        BufferedReader rd = new BufferedReader(new InputStreamReader(cardStream));
        String line = null;
        String linesCombined = null;

        List<String> SPSScommands = new ArrayList<String>();

        while ((line = rd.readLine()) != null) {
            // chop all blanks at the end, replace with a single whitespace:

            line = line.replaceFirst("[ \t\n]*$", " ");

            // skip blank, and similar lines:

            if (line.equals(" ") ||
                line.startsWith("comment") ||
                line.matches("^[ \t]*.$")) {
                    dbgLog.fine("skipping line");
            }

            // check first character:

            //String firstChar = line.substring(0, 1);

            if (line.startsWith(" ") || line.startsWith("\t")) {
                // continuation line;
                line = line.replaceAll("^[ \t]*", "");
                linesCombined = linesCombined + line;
            } else {
                // a new command line:
                if (linesCombined != null) {
                    SPSScommands.add(linesCombined);
                }
                linesCombined = line;
            }
         }

        rd.close();
        if (linesCombined != null) {
            SPSScommands.add(linesCombined);
        }

        String regexCommandLine = "^(\\w+?)\\s+?(\\w+)(.*)";
        Pattern patternCommandLine = Pattern.compile(regexCommandLine);

        for (int i = 0; i < SPSScommands.size(); i++) {
            String commandLine = SPSScommands.get(i);

            // Note that SPSS commands are not case-sensitive.

            String command1 = null;
            String command2 = null;
            String rest = null;

            Matcher commandMatcher = patternCommandLine.matcher(commandLine);

            if (commandMatcher.find()) {
                command1 = commandMatcher.group(1);
                command2 = commandMatcher.group(2);
                rest = commandMatcher.group(3);
            }

            dbgLog.fine("command1: "+command1);
            dbgLog.fine("command2: "+command2);
            dbgLog.fine("rest: "+rest);


            // TODO: code below executed only if rest != null -- ?

            // DATA LIST:
            
            if (command1 != null &&
                command2 != null &&
                command1.regionMatches(true, 0, "data", 0, 4) &&
                command2.regionMatches(true, 0, "list", 0, 4)) {

                if ( rest != null ) {
                    rest = rest.trim();
                    dbgLog.fine("saving "+rest+" as a DataList command");

                    if (commandStrings.get("DataList") == null) {
                        commandStrings.put("DataList", rest);
                    } else {
                        commandStrings.put("DataList", commandStrings.get("DataList")+"/"+rest);
                    }
                }
                
            // VARIABLE LABELS:    
             
            } else if ( command1 != null &&
                        command2 != null &&
                        command1.regionMatches(true, 0, "var", 0, 3) &&
                        command2.regionMatches(true, 0, "lab", 0, 3)) {

                if ( rest != null ) {
                    rest = rest.trim();

                    if (rest.length()>0 &&
                        rest.substring(rest.length()-1).equals(".")) {
                            rest = rest.substring(0, rest.length()-1);
                    }
                    dbgLog.fine("saving "+rest+" as a VarLabel command");
                    if (commandStrings.get("VarLabel") == null) {
                        commandStrings.put("VarLabel", rest);
                    } else {
                        commandStrings.put("VarLabel", commandStrings.get("VarLabel")+" "+rest);
                    }

                }
                
            // VALUE LABELS:

            } else if ( command1 != null &&
                        command2 != null &&
                        command1.regionMatches(true, 0, "val", 0, 3) &&
                        command2.regionMatches(true, 0, "lab", 0, 3)) {

                if ( rest != null ) {
                    rest = rest.trim();

                    if (rest.length()>0 &&
                        rest.substring(rest.length()-1).equals(".")) {
                            rest = rest.substring(0, rest.length()-2);
                    }
                    if (rest.length()>0 &&
                        rest.substring(rest.length()-1).equals("/")) {
                            rest = rest.substring(0, rest.length()-2);
                    }

                    dbgLog.fine("saving "+rest+"/ as a ValLabel command");
                    if (commandStrings.get("ValLabel") == null) {
                        commandStrings.put("ValLabel", rest+"/");
                    } else {
                        commandStrings.put("ValLabel", commandStrings.get("ValLabel")+rest+"/");
                    }
                }

            // MISSING VALUES: 
                
            } else if ( command1 != null &&
                        command2 != null &&
                        command1.regionMatches(true, 0, "mis", 0, 3) &&
                        command2.regionMatches(true, 0, "val", 0, 3)) {

                if ( rest != null ) {
                    rest = rest.trim();

                    if (rest.length()>0 &&
                        rest.substring(rest.length()-1).equals(".")) {
                            rest = rest.substring(0, rest.length()-2);
                    }

                    // TODO:
                    // Find out if converting these .toUpperCase() is the
                    // right thing to do.

                    dbgLog.fine("saving "+rest.toUpperCase()+" as the "+i+"-th MisValue command");

                    if (commandStrings.get("MisValue") == null) {
                        commandStrings.put("MisValue", rest);
                    } else {
                        commandStrings.put("MisValue", commandStrings.get("MisValue")+" "+rest.toUpperCase());
                    }

                }

            // NUMBER OF CASES: (optional -- may not be present)
                
            } else if ( command1 != null &&
                        command2 != null &&
                        command1.regionMatches(true, 0, "n", 0, 1) &&
                        command2.regionMatches(true, 0, "of", 0, 2)) {
                if ( rest != null ) {
                    rest = rest.trim();

                    if (rest.regionMatches(true,0,"cases",0,5)) {
                        rest = rest.substring(5);
                        rest = rest.trim();
                        String regexNumberOfCases = "^([0-9]*)";
                        Pattern patternNumberOfCases = Pattern.compile(regexNumberOfCases);
                        Matcher casesMatcher = patternNumberOfCases.matcher(rest);

                        if (casesMatcher.find()) {
                            setCaseQnty(Integer.valueOf(casesMatcher.group(1)));
                            smd.getFileInformation().put("caseQnty", getCaseQnty());
                        }
                    }
                }

            } // also:
            // RECODE
            // FORMATS


        }

    }

    // methods for parsing individual parts of the Control Card:

    // DATA LIST:

    int read_DataList (String dataListCommand) {
        int readStatus = 0;

        // Read the first line (DATA LIST ...) to determine
        // the field separator:
        // This line should be "/"-terminated (?)

        dbgLog.fine("dataList command: "+dataListCommand);
 
        List<Integer> variableTypeList= new ArrayList<Integer>();

        String delimiterString = null;

        //String datalistRegex = "^data\\s+list\\s+list\\('(.)'\\)\\s+?/";
        String datalistRegex = "^list\\('(.)'\\).*/";
        Pattern datalistPattern = Pattern.compile(datalistRegex, java.util.regex.Pattern.CASE_INSENSITIVE);
        Matcher datalistMatcher = datalistPattern.matcher(dataListCommand);

        if (datalistMatcher.find()) {
            delimiterString = datalistMatcher.group(1);
            setDelimiterChar(delimiterString.charAt(0));
            dbgLog.fine("found delimiter: "+delimiterString);
        } else {
            return 1;   // No delimiter declaration found
                        //(not a delimited file perhaps?)
        }

        // Cut off the remaining lines containing the variable definitions:

        int separatorIndex = dataListCommand.indexOf("/");

        if (separatorIndex == -1) {
            return 2; // No slash found after the first line of the Data List command.
        }

        dataListCommand = dataListCommand.substring(separatorIndex+1);


        // Parse the variable section. For a delimited file this should be
        // a list of variable name + data type pairs.
        // "fortran" type definitions are assumed.

        dbgLog.fine ("parsing "+dataListCommand+" for variable declarations.");

        int variableCounter = 0;

        String varName = null;
        String varType = null;

        String varDeclarationRegex = "\\s*(\\S+)\\s+\\((\\S+)\\)";
        Pattern varDeclarationPattern = Pattern.compile(varDeclarationRegex);
        Matcher varDeclarationMatcher = varDeclarationPattern.matcher(dataListCommand);

        while (varDeclarationMatcher.find()) {
            varName = varDeclarationMatcher.group(1);
            varType = varDeclarationMatcher.group(2);

            dbgLog.fine ("found variable "+varName+", type "+varType);

            variableCounter++;
            variableNameList.add(varName);

            if (varType.startsWith("a") || varType.startsWith("A")) {
                // String:
                variableTypeList.add(-1);
                unfVariableTypes.put(varName, -1);
            } else {
                // Numeric:
                variableTypeList.add(0);

                // Extended numeric types for the UNF calculation:
                // (we need to be able to differentiate between Integers and
                // real numbers)

                if (varType.indexOf(".") > 0 || varType.matches("^[0-9]")) {
                    unfVariableTypes.put(varName, 1);
                } else {
                    unfVariableTypes.put(varName, 0);
                }

            }
           
        }

        // TODO: validate variable entries;
        // return error code if any unsupported variable declarations are
        // found (for example, fixed width columns).

        smd.getFileInformation().put("varQnty", variableCounter);
        setVarQnty(variableCounter);
        dbgLog.fine("varQnty="+getVarQnty());

        smd.setVariableName(variableNameList.toArray(new String[variableNameList.size()]));

        smd.setVariableTypeMinimal(ArrayUtils.toPrimitive(
            variableTypeList.toArray(new Integer[variableTypeList.size()])));

        //TODO: ? smd.getFileInformation().put("caseWeightVariableName", caseWeightVariableName);
        //TODO: ? smd.setVariableFormat(printFormatList);
        //TODO: ? smd.setVariableFormatName(printFormatNameTable);


        return readStatus;
    }

    // VARIABLE LABELS:

    int read_VarLabels (String varLabelsCommand) {
        int readStatus = 0;

        // List of variable labels.
        // These are variable name + variable label pairs.

        dbgLog.fine ("parsing "+varLabelsCommand+" for variable labels.");

        Map<String, String> variableLabelMap = new LinkedHashMap<String, String>();

        String varName = null;
        String varLabel = null;

        String varLabelRegex = "\\s*(\\S+)\\s+\"([^\"]+)\"";
        Pattern varLabelPattern = Pattern.compile(varLabelRegex);
        Matcher varLabelMatcher = varLabelPattern.matcher(varLabelsCommand);

        while (varLabelMatcher.find()) {
            varName = varLabelMatcher.group(1);
            varLabel = varLabelMatcher.group(2);

            dbgLog.fine ("found variable label for "+varName+": "+varLabel);
            variableLabelMap.put(varName, varLabel);
        }

        // TODO:
        // Validate the entries, make sure that the variables are legit, etc.

        smd.setVariableLabel(variableLabelMap);

        return readStatus;
    }

    int read_ValLabels (String valLabelsCommand) {

        int readStatus = 0;

        Map<String, Map<String, String>> valueLabelTable =
                new LinkedHashMap<String, Map<String, String>>();
        Map<String, String> valueVariableMappingTable = new LinkedHashMap<String, String>();


        // Value Labels are referenced by the (declared) variable names,
        // followed by a number of value-"label" pairs; for every variable
        // the entry is terminated with a "/".

        dbgLog.fine ("parsing "+valLabelsCommand+" for value labels.");


        String varName = null;
        String valLabelDeclaration = null;

        String varValue = null;
        String valueLabel = null;

        String valLabelRegex = "\\s*(\\S+)\\s+([^/]+)/";
        Pattern valLabelPattern = Pattern.compile(valLabelRegex);
        Matcher valLabelMatcher = valLabelPattern.matcher(valLabelsCommand);

        String labelDeclarationRegex = "\\s*(\\S+)\\s+(\"[^\"]*\")";
        Pattern labelDeclarationPattern = Pattern.compile(labelDeclarationRegex);


        while (valLabelMatcher.find()) {
            varName = valLabelMatcher.group(1);
            valLabelDeclaration = valLabelMatcher.group(2);

            dbgLog.fine ("found value label declaration for "+varName+": "+valLabelDeclaration);

            Map <String, String> valueLabelPairs = new LinkedHashMap<String, String>();


            Matcher labelDeclarationMatcher = labelDeclarationPattern.matcher(valLabelDeclaration);

            while (labelDeclarationMatcher.find()) {
                varValue = labelDeclarationMatcher.group(1);
                valueLabel = labelDeclarationMatcher.group(2);

                dbgLog.fine ("found label "+valueLabel+" for value "+varValue);

                Boolean isNumeric = false; // dummy placeholder

                if (isNumeric){
                    // Numeric variable:
                    dbgLog.fine("processing numeric value label");
                    valueLabelPairs.put(doubleNumberFormatter.format(new Double(varValue)), valueLabel);
                } else {
                    // String variable
                    dbgLog.fine("processing string value label");
                    valueLabelPairs.put(varValue, valueLabel );
                }
            }

            valueLabelTable.put(varName,valueLabelPairs);
            valueVariableMappingTable.put(varName, varName);

            // TODO:
            // Do SPSS cards support shared value label sets -- ?

        }

        smd.setValueLabelTable(valueLabelTable);
        smd.setValueLabelMappingTable(valueVariableMappingTable);


        // TODO: 
        // Better validation, error reporting.

        return readStatus;
    }

    int read_MisValues (String misValuesCommand) {
        int readStatus = 0;

        // Missing Values:
        // These are declared by the (previously declared) variable name,
        // followed by a comma-separated list of values in parentheses.
        //  for ex.: FOOBAR (1, 2, 3)

        dbgLog.fine ("parsing "+misValuesCommand+" for missing values.");

        Map<String, List<String>> missingValueTable = new LinkedHashMap<String, List<String>>();

        String varName = null;
        String misValuesDeclaration = null;

        String misValue = null;

        String misValuesRegex = "\\s*(\\S+)\\s+\\(([^\\)]+)\\)";
        Pattern misValuesPattern = Pattern.compile(misValuesRegex);
        Matcher misValuesMatcher = misValuesPattern.matcher(misValuesCommand);

        String misValDeclarationRegex = "\\s*([^,]+)\\s*,*";
        Pattern misValDeclarationPattern = Pattern.compile(misValDeclarationRegex);


        while (misValuesMatcher.find()) {
            varName = misValuesMatcher.group(1);
            misValuesDeclaration = misValuesMatcher.group(2);

            dbgLog.fine ("found missing values declaration for "+varName+": "+misValuesDeclaration);

            Matcher misValDeclarationMatcher = misValDeclarationPattern.matcher(misValuesDeclaration);

            Boolean isNumericVariable = false;
            List<String> mv = new ArrayList<String>();

            while (misValDeclarationMatcher.find()) {
                misValue = misValDeclarationMatcher.group(1);

                dbgLog.fine ("found missing value: "+misValue);

                if (isNumericVariable) {
                    // No support for ranges (yet?)
                    // TODO: find out if SPSS cards support ranges of
                    // missing values.
                    mv.add(doubleNumberFormatter.format(new Double(misValue)));
                } else {
                    mv.add(misValue);
                }
            }

            missingValueTable.put(varName, mv);

        }

        // TODO:
        // Better validation, error reporting.

        smd.setMissingValueTable(missingValueTable);

        return readStatus;
    }

    // Method for calculating the UNF signatures.
    //
    // It really isn't awesome that each of our file format readers has 
    // its own UNF calculation! It should be format-independent; the 
    // method should be defined in just one place, and, preferably, it should
    // run on the TAB file and the data set metadata from the database. 
    // I.e., it should be reproducible outside of the ingest. 
    // 
    // TODO: bring this up, soon.

    private String getUNF(Object[] varData, String[] dateFormats, int variableType,
        String unfVersionNumber, int variablePosition)
        throws NumberFormatException, UnfException,
        IOException, NoSuchAlgorithmException {
        String unfValue = null;


        dbgLog.fine("variableType="+variableType);
        dbgLog.finer("unfVersionNumber="+unfVersionNumber);
        dbgLog.fine("variablePosition="+variablePosition);
        dbgLog.fine("variableName="+variableNameList.get(variablePosition));

        switch(variableType){
            case 0:
                // Integer case
                // note: due to DecimalFormat class is used to
                // remove an unnecessary decimal point and 0-padding
                // numeric (double) data are now String objects

                dbgLog.fine("Integer case");

                // Convert array of Strings to array of Longs
                Long[] ldata = new Long[varData.length];
                for (int i = 0; i < varData.length; i++) {
                    if (varData[i] != null) {
                        ldata[i] = new Long((String) varData[i]);
                    }
                }
                unfValue = UNF5Util.calculateUNF(ldata);
                dbgLog.finer("integer:unfValue=" + unfValue);

                dbgLog.finer("sumstat:long case=" + Arrays.deepToString(
                        ArrayUtils.toObject(StatHelper.calculateSummaryStatistics(ldata))));

                smd.getSummaryStatisticsTable().put(variablePosition,
                        ArrayUtils.toObject(StatHelper.calculateSummaryStatistics(ldata)));


                Map<String, Integer> catStat = StatHelper.calculateCategoryStatistics(ldata);
                smd.getCategoryStatisticsTable().put(variableNameList.get(variablePosition), catStat);

                break;

            case 1:
                // double case
                // note: due to DecimalFormat class is used to
                // remove an unnecessary decimal point and 0-padding
                // numeric (double) data are now String objects

                dbgLog.finer("double case");

                 // Convert array of Strings to array of Doubles
                Double[]  ddata = new Double[varData.length];
                for (int i=0;i<varData.length;i++) {
                    if (varData[i]!=null) {
                        ddata[i] = new Double((String)varData[i]);
                    }
                }
                unfValue = UNF5Util.calculateUNF(ddata);
                dbgLog.finer("double:unfValue="+unfValue);
                smd.getSummaryStatisticsTable().put(variablePosition,
                    ArrayUtils.toObject(StatHelper.calculateSummaryStatisticsContDistSample(ddata)));

                break;
            case -1:
                // String case
                dbgLog.finer("string case");


                String[] strdata = Arrays.asList(varData).toArray(
                    new String[varData.length]);
                dbgLog.finer("string array passed to calculateUNF: "+Arrays.deepToString(strdata));
                unfValue = UNF5Util.calculateUNF(strdata, dateFormats);
                dbgLog.finer("string:unfValue="+unfValue);

                smd.getSummaryStatisticsTable().put(variablePosition,
                    StatHelper.calculateSummaryStatistics(strdata));

                Map<String, Integer> StrCatStat = StatHelper.calculateCategoryStatistics(strdata);
                //out.println("catStat="+StrCatStat);

                smd.getCategoryStatisticsTable().put(variableNameList.get(variablePosition), StrCatStat);

                break;
            default:
                dbgLog.fine("unknown variable type found");
                String errorMessage =
                    "unknow variable Type found at varData section";
                    throw new IllegalArgumentException(errorMessage);

        } // switch

        dbgLog.fine("unfvalue(last)="+unfValue);
        return unfValue;
    }

    // This method calculates the summary and category statistics, as well
    // as the UNF signatures for the variables and the dataset as a whole.

    private void calculateDatasetStats (DataTable csvData) {
        String fileUNFvalue = null;

        String[] unfValues = new String[getVarQnty()];

         // TODO:
         // Catch and differentiate between different exception
         // that the UNF methods throw.

        for (int j=0; j<getVarQnty(); j++){
            int variableTypeNumer = unfVariableTypes.get(variableNameList.get(j));

            try {
                dbgLog.finer("j = "+j);

                unfValues[j] = getUNF(csvData.getData()[j], null, variableTypeNumer,
                    unfVersionNumber, j);
                dbgLog.fine(j+"th unf value"+unfValues[j]);

            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            } catch (UnfException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
                //throw ex;
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            }
        }


        dbgLog.fine("unf set:\n"+Arrays.deepToString(unfValues));

        try {
            fileUNFvalue = UNF5Util.calculateUNF(unfValues);

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
            //throw ex;
        }

        // Set the UNFs we have calculated, the ones for the individual
        // variables and the file-level UNF:

        csvData.setUnf(unfValues);
        csvData.setFileUnf(fileUNFvalue);

        smd.setVariableUNF(unfValues);
        smd.getFileInformation().put("fileUNF", fileUNFvalue);

        dbgLog.fine("unf values:\n"+unfValues);

    }
    
    public static void main(String[] args) {
        BufferedInputStream spssCardStream = null;
        SDIOData processedCard = null;
        SPSSFileReader spssReader = null;

        String testCardFile = args[0];
        
        try {

            spssCardStream = new BufferedInputStream(new FileInputStream(testCardFile));

            spssReader = new SPSSFileReader(null);
            processedCard = spssReader.read(spssCardStream);
        } catch (IOException ex) {
            System.out.println("exception caught!");
            if (spssReader == null) {
                System.out.println("failed to create an SPSS file reader.");
            }
        }


        System.out.println("Hello World.");
    }

}
