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

package edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.por;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.security.NoSuchAlgorithmException;
import java.util.logging.*;

import static java.lang.System.*;
import java.util.*;
import java.lang.reflect.*;
import java.util.regex.*;
import java.text.*;

import org.apache.commons.lang.*;
import org.apache.commons.lang.builder.*;
import org.apache.commons.io.*;
import org.apache.commons.io.input.*;
import org.apache.commons.codec.binary.Hex;

import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.data.*;
import edu.harvard.iq.dvn.unf.*;



import edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.util.*;


/**
 *
 * @author Akio Sone at UNC-Odum
 */
public class PORFileReader extends StatDataFileReader{

    // static fields ---------------------------------------------------------//

    private static final int POR_HEADER_SIZE = 500;
    
    /**
     *
     */
    public static final int POR_MARK_POSITION_DEFAULT = 461;
    /**
     *
     */
    public static String POR_MARK = "SPSSPORT";
    
    private boolean windowsNewLine = true;
    
    // 
    /**
     *
     */
    public static final int LENGTH_SECTION_HEADER = 1;

    // section 2
    // POR_MARK ends at 481
    /**
     *
     */
    public static final int LENGTH_SECTION_2_OFFSET = 481;
    /**
     *
     */
    public static final int LENGTH_SECTION_2 = 19;
    
    
    private static String[] FORMAT_NAMES = {"por", "POR"};
    private static String[] EXTENSIONS = {"por"};
    private static String[] MIME_TYPE = {"application/x-spss-por"};

    /** List of decoding methods for reflection */
    private static String[] decodeMethodNames = {
        "decodeProductName", "decodeLicensee", 
        "decodeFileLabel", "decodeNumberOfVariables", "decodeFieldNo5", 
        "decodeWeightVariable", "decodeVariableInformation", 
        "decodeMissValuePointNumeric", "decodeMissValuePointString", 
        "decodeMissValueRangeLow", "decodeMissValueRangeHigh", 
        "decodeMissValueRange", "decodeVariableLabel", 
        "decodeValueLabel", "decodeDocument", "decodeData" };

    private static List<Method> decodeMethods  = new ArrayList<Method>();
    private static Map<String, Method> decodeMethodMap = new LinkedHashMap<String, Method>();

    
    private static String[] decodeMethodIdString = {
            "1", "2", "3", "4", "5", "6", "7", "8", "8S", "9", 
            "A", "B", "C", "D", "E", "F" };
            
    private static String MissngVauleSymbol = ".";
    
    private static Pattern pattern4positiveInteger = Pattern.compile("[0-9A-T]+");
    private static Pattern pattern4Integer = Pattern.compile("[-]?[0-9A-T]+");

    static {

        for (int i=0; i< decodeMethodNames.length;i++){
            for (Method m: PORFileReader.class.getDeclaredMethods()){
                if (m.getName().equals(decodeMethodNames[i])){
                    decodeMethodMap.put(decodeMethodIdString[i], m);
                }
            }
        }

        for (String n: decodeMethodNames){
            for (Method m: PORFileReader.class.getDeclaredMethods()){
                if (m.getName().equals(n)){
                
                
                    decodeMethods.add(m);
                }
            }
        }
        
    }

    private static String unfVersionNumber = "3";

    // instance fields -------------------------------------------------------//

   private static Logger dbgLog =
       Logger.getLogger(PORFileReader.class.getPackage().getName());

    SDIOMetadata smd = new PORMetadata();
    

    SDIOData sdiodata;

    DataTable porDataSection = new DataTable();

    int release_number;
    int header_length;
    int data_label_length;

    
    double missing_value_double;

    //boolean isLittleEndian = false;

    Map<String, Integer> variableTypeTable = new LinkedHashMap<String, Integer>();

    List<Integer> variableTypelList = new ArrayList<Integer>();

    Map<String, Integer> StringVariableTable = new LinkedHashMap<String, Integer>();
    
    int value_label_table_length;
    
    Map<String, Map<String, String>> valueLabelTable =
            new LinkedHashMap<String, Map<String, String>>();
            
    Map<String, String> valueVariableMappingTable = new LinkedHashMap<String, String>();
    
    String[] unfValues = null;
    
    String fileUnfValue = null;

    File tempPORfile = null;
    String tempPORfileName = null;
    String lineTerminator = null;
    
    boolean isCurrentVariableString = false;
    
    Double systemMissingValue =Double.NaN;


    List<String> variableNameList = new ArrayList<String>();
    Map<String, String> variableLabelMap = new LinkedHashMap<String, String>();
    
    String currentVariableName = null;
    
    // missing value table: string/numeric data are stored  => String
    // the number of missing values are unknown beforehand => List
    Map<String, List<String>> missingValueTable = new LinkedHashMap<String, List<String>>();
    
    // variableName=> missingValue type[field code]
    Map<String, List<String>> missingValueCodeTable = new LinkedHashMap<String, List<String>>();
    
    int caseQnty=0;
    
    int varQnty=0;
    
    Set<Integer> decimalVariableSet = new HashSet<Integer>();
    
    
    // DecimalFormat for doubles
    // may need more setXXXX() to handle scientific data
    NumberFormat doubleNumberFormatter = new DecimalFormat();

            Object[][] dataTable2 = null;
            
    int[] variableTypeFinal= null;


    protected String MissingValueForTextDataFileNumeric = "NA";

    public String getMissingValueForTextDataFileNumeric() {
        return MissingValueForTextDataFileNumeric;
    }

    public void setMissingValueForTextDataFileNumeric(String MissingValueToken) {
        this.MissingValueForTextDataFileNumeric = MissingValueToken;
    }


    protected String MissingValueForTextDataFileString = "";

    public String getMissingValueForTextDataFileString() {
        return MissingValueForTextDataFileString;
    }

    public void setMissingValueForTextDataFileString(String MissingValueToken) {
        this.MissingValueForTextDataFileString = MissingValueToken;
    }

    // Constructor -----------------------------------------------------------//


            /**
             *
             * @param originator
             */
            public PORFileReader(StatDataFileReaderSpi originator){
        super(originator);
    }

    private void init(){
        //
        doubleNumberFormatter.setGroupingUsed(false);
        
    }

    // Methods ---------------------------------------------------------------//

    public SDIOData read(BufferedInputStream stream) throws IOException{

        dbgLog.info("***** PORFileReader: read() start *****");

        decodeHeader(stream);

        dbgLog.fine("tempPORfileName="+tempPORfileName);
        
        BufferedReader bfReader = null;
        
        try {
            

            bfReader = new BufferedReader(new InputStreamReader(
                       new FileInputStream(tempPORfileName), "US-ASCII"));
            if (bfReader == null){
                dbgLog.fine("bfReader is null");
                throw new IOException("bufferedReader is null");
            }
            //dbgLog.fine("tempPORfile="+tempPORfile.getAbsolutePath());
            
            decodeSec2(bfReader);
            
            while(true){

                char[] header = new char[LENGTH_SECTION_HEADER]; // 1 byte

                bfReader.read(header);

                String headerId = Character.toString(header[0]);
                
                dbgLog.fine("////////////////////// headerId="+headerId+ "//////////////////////");
                
                if (headerId.equals("Z")){
                    throw new IOException("reading failure: wrong headerId(Z) here");
                }
                
                
                
                
                if (headerId.equals("8") && isCurrentVariableString){
                    headerId = "8S";
                }
                try {
                
                    (decodeMethodMap.get(headerId)).invoke(this, bfReader);
                    
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (InvocationTargetException ex) {
                    ex.printStackTrace();
                }
                
                // for last iteration
                if (headerId.equals(decodeMethodIdString[decodeMethodIdString.length-1])){
                    // finished the last block (F == data) 
                    // without reaching the end of this file.
                    break;
                }
            }
            
            // post-parsing processing
            // save metadata to smd 
            // varialbe Name
            
            smd.setVariableName(variableNameList.toArray(new String[variableNameList.size()]));
            // variableLabelMap
            smd.setVariableLabel(variableLabelMap);
            // missing-value table
            smd.setMissingValueTable(missingValueTable);

            smd.setValueLabelTable(valueLabelTable);

            smd.setVariableTypeMinimal(ArrayUtils.toPrimitive(
                variableTypelList.toArray(new Integer[variableTypelList.size()])));

        } catch (FileNotFoundException ex){
            err.println("file is not found");
            ex.printStackTrace();
        } catch (IOException ex){
            ex.printStackTrace();
        } finally {
            try {
                if (bfReader!= null){
                    bfReader.close();
                }
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }

        if (sdiodata == null){
            sdiodata = new SDIOData(smd, porDataSection);
        }
        dbgLog.info("***** PORFileReader: read() end *****");
        return sdiodata;
    }
    
    /**
     *
     * @param stream
     */
    public void decodeHeader(BufferedInputStream stream){
        dbgLog.fine("***** decodeHeader(): start *****");

        if (stream  == null){
            throw new IllegalArgumentException("file == null!");
        }
        
        dbgLog.fine("applying the por test\n");
        
        byte[] headerByes = new byte[POR_HEADER_SIZE];
        
        try{
            if (stream.markSupported()){
                stream.mark(1000);
            }
            int nbytes = stream.read(headerByes, 0, POR_HEADER_SIZE);

            //printHexDump(headerByes, "hex dump of the byte-array");

            if (nbytes == 0){
                throw new IOException("decodeHeader: reading failure");
            } else if ( nbytes < 491) {
               // Size test: by defnition, it must have at least
                // 491-byte header, i.e., the file size less than this threshold
                // is not a POR file
               dbgLog.fine("this file is NOT spss-por type");
               throw new IllegalArgumentException("file is not spss-por type");
            }
            // rewind the current reading position back to the beginning
            if (stream.markSupported()){
                stream.reset();
            }
        
        } catch (IOException ex){
            ex.printStackTrace();
        }
        // line-terminating characters are usually one or two by defnition
        // however, a POR file saved by a genuine SPSS for Windows
        // had a three-character line terminator, i.e., failed to remove the
        // original file's one-character terminator when it was opened, and
        // saved it with the default two-character terminator without
        // removing original terminators. So we have to expect such a rare
        // case
        //
        // terminator
        // windows [0D0A]=>   [1310] = [CR/LF]
        // unix    [0A]  =>   [10]
        // mac     [0D]  =>   [13]
        // 3char  [0D0D0A]=> [131310] spss for windows rel 15
        //
        // terminating characters should be found at the following
        //                             column positions[counting from 0]:
        // unix    case: [0A]   : [80], [161], [242], [323], [404], [485]
        // windows case: [0D0A] : [81], [163], [245], [327], [409], [491]
        //           : [0D0D0A] : [82], [165], [248], [331], [414], [495]
        
        // convert b into a ByteBuffer
        
        ByteBuffer buff = ByteBuffer.wrap(headerByes);
        byte[] nlch = new byte[36];
        int pos1;
        int pos2;
        int pos3;
        int ucase = 0;
        int wcase = 0;
        int mcase = 0;
        int three = 0;
        int nolines = 6;
        int nocols = 80;
        for (int i = 0; i < nolines; ++i) {
            int baseBias = nocols * (i + 1);
            // 1-char case
            pos1 = baseBias + i;
            buff.position(pos1);
            dbgLog.finer("\tposition(1)=" + buff.position());
            int j = 6 * i;
            nlch[j] = buff.get();

            if (nlch[j] == 10) {
                ucase++;
            } else if (nlch[j] == 13) {
                mcase++;
            }

            // 2-char case
            pos2 = baseBias + 2 * i;
            buff.position(pos2);
            dbgLog.finer("\tposition(2)=" + buff.position());
            
            nlch[j + 1] = buff.get();
            nlch[j + 2] = buff.get();

            // 3-char case
            pos3 = baseBias + 3 * i;
            buff.position(pos3);
            dbgLog.finer("\tposition(3)=" + buff.position());
            
            nlch[j + 3] = buff.get();
            nlch[j + 4] = buff.get();
            nlch[j + 5] = buff.get();

            dbgLog.finer(i + "-th iteration position =" +
                    nlch[j] + "\t" + nlch[j + 1] + "\t" + nlch[j + 2]);
            dbgLog.finer(i + "-th iteration position =" +
                    nlch[j + 3] + "\t" + nlch[j + 4] + "\t" + nlch[j + 5]);
            
            if ((nlch[j + 3] == 13) &&
                (nlch[j + 4] == 13) &&
                (nlch[j + 5] == 10)) {
                three++;
            } else if ((nlch[j + 1] == 13) && (nlch[j + 2] == 10)) {
                wcase++;
            }

            buff.rewind();
        }
        if (three == nolines) {
            dbgLog.fine("0D0D0A case");
            lineTerminator = "0D0D0A";
            windowsNewLine = false;
        } else if ((ucase == nolines) && (wcase < nolines)) {
            dbgLog.fine("0A case");
            lineTerminator = "0A";
            windowsNewLine = false;
        } else if ((ucase < nolines) && (wcase == nolines)) {
            dbgLog.fine("0D0A case");
            lineTerminator = "0D0A";
        } else if ((mcase == nolines) && (wcase < nolines)) {
            dbgLog.fine("0D case");
            lineTerminator = "0D0A";
            windowsNewLine = false;
        }


        buff.rewind();
        int PORmarkPosition = POR_MARK_POSITION_DEFAULT;
        if (windowsNewLine) {
            PORmarkPosition = PORmarkPosition + 5;
        } else if (three == nolines) {
            PORmarkPosition = PORmarkPosition + 10;
        }

        byte[] pormark = new byte[8];
        buff.position(PORmarkPosition);
        buff.get(pormark, 0, 8);
        String pormarks = new String(pormark);

        //dbgLog.fine("pormark =>" + pormarks + "<-");
        dbgLog.fine("pormark[hex: 53 50 53 53 50 4F 52 54 == SPSSPORT] =>" +
                new String(Hex.encodeHex(pormark)) + "<-");

        if (pormarks.equals(POR_MARK)) {
            dbgLog.fine("POR ID toke test: Passed");
            init();
            
            smd.getFileInformation().put("mimeType", MIME_TYPE[0]);
            smd.getFileInformation().put("fileFormat", MIME_TYPE[0]);

        } else {
            dbgLog.fine("this file is NOT spss-por type");
            throw new IllegalArgumentException(
                "decodeHeader: POR ID token was not found");
        }

        // save the POR file without new line characters

        FileOutputStream fileOutPOR = null;
        Writer fileWriter = null;

        // Scanner class can handle three-character line-terminator
        Scanner porScanner = null;
        
        try {
            tempPORfile = File.createTempFile("tempPORfile.", ".por");

            tempPORfileName   = tempPORfile.getAbsolutePath();

            fileOutPOR = new FileOutputStream(tempPORfile);

            fileWriter = new BufferedWriter(new OutputStreamWriter(fileOutPOR, "utf8"));

            porScanner = new Scanner(stream);

            while(porScanner.hasNextLine()){
                //String line = porScanner.nextLine().toString();
                //dbgLog.fine(line.length()+"\t"+line);
                fileWriter.write(porScanner.nextLine().toString());
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex){
            ex.printStackTrace();
        } finally{
            try{
                if (fileWriter != null){
                    fileWriter.close();
                }
                if (porScanner != null){
                    porScanner.close();
                }
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }
        dbgLog.fine("smd dump:"+smd.toString());
        dbgLog.fine("***** decodeHeader(): end *****");

    }


    /**
     *
     * @param reader
     */
    protected void decodeSec2(BufferedReader reader){
        dbgLog.fine("***** decodeSec2(): start *****");
        if (reader ==null){
            throw new IllegalArgumentException("decodeSec2: stream == null!");
        }
        // skip first 481-byte block thru "SPSSPORT"
        // read next 19 bytes
        // 2-byte + '/' + 8-character +


        char[] Sec2_bytes = new char[LENGTH_SECTION_2];

        try {

            reader.skip(LENGTH_SECTION_2_OFFSET);
            
            if (reader.markSupported()){
                reader.mark(100000);
            }
            
            dbgLog.fine("reached here");
            int nbytes_sec2 = reader.read(Sec2_bytes, 0, LENGTH_SECTION_2);

            if (nbytes_sec2 == 0){
                throw new IOException("decodeSec2: reading error");
            } else {
                dbgLog.fine("bytes read="+nbytes_sec2);
            }
            //printHexDump(Sec2_bytes, "Section 2 header");

            String sec2 = new String(Sec2_bytes);
            dbgLog.fine(sec2);
            String[] section2 = StringUtils.split(sec2, '/');

            dbgLog.fine("section2="+StringUtils.join(section2, "|"));

            String fileCreationDate =null;
            String fileCreationTime = null;
            if ((section2.length == 3)&& (section2[0].startsWith("A"))){
                fileCreationDate = section2[1].substring(0,7);
                fileCreationTime = section2[2];
            }
            dbgLog.fine("fileCreationDate="+fileCreationDate);
            
            smd.getFileInformation().put("fileCreationDate", fileCreationDate);
            smd.getFileInformation().put("fileCreationTime", fileCreationTime);
            
        } catch (IOException ex){
            ex.printStackTrace();
        }

        dbgLog.fine("***** decodeSec2(): end *****");
    }


    /**
     *
     * @param reader
     */
    protected void decodeProductName(BufferedReader reader){
        dbgLog.fine("***** 1: decodeProductName(): start *****");
        if (reader ==null){
            throw new IllegalArgumentException("decodeProductName: reader == null!");
        }
        String productName=null;
        try {
            productName = parseStringField(reader);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        smd.getFileInformation().put("productName", productName);
        dbgLog.fine("productName="+productName);
        dbgLog.fine("***** decodeProductName(): end *****");
    }


    /**
     *
     * @param reader
     */
    protected void decodeLicensee(BufferedReader reader){
        dbgLog.fine("***** 2: decodeLicensee(): start *****");
        
        if (reader ==null){
            throw new IllegalArgumentException("decodeLicensee: reader == null!");
        }
        String licenseeName=null;
        try {
            licenseeName = parseStringField(reader);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        dbgLog.fine("licenseeName="+licenseeName);
        smd.getFileInformation().put("licenseeName", licenseeName);
        
        dbgLog.fine("***** decodeLicensee(): end *****");
    }


    /**
     *
     * @param reader
     */
    protected void decodeFileLabel(BufferedReader reader){
        dbgLog.fine("***** 3: decodeFileLabel(): start *****");
        
        
        
        if (reader ==null){
            throw new IllegalArgumentException("decodeFileLabel: reader == null!");
        }
        String fileLabel=null;
        try {
            fileLabel = parseStringField(reader);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        dbgLog.fine("fileLabel="+fileLabel);
        
        smd.getFileInformation().put("fileLabel", fileLabel);

        
        dbgLog.fine("***** decodeFileLabel(): end *****");
    }


    /**
     *
     * @param reader
     */
    protected void decodeNumberOfVariables(BufferedReader reader){
        dbgLog.fine("***** 4: decodeNumberOfVariables(): start *****");
        
        
        if (reader ==null){
            throw new IllegalArgumentException("decodeNumberOfVariables: reader == null!");
        }
        

        String temp = null;
        char[] tmp = new char[1];
        StringBuilder sb = new StringBuilder();
        try {
            while (reader.read(tmp) > 0) {
                temp = Character.toString(tmp[0]); //new String(tmp);
                if (temp.equals("/")) {
                    break;
                } else {
                    sb.append(temp);
                }
                //temp = sb.toString();//new String(tmp);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String rawNumberOfVariables = sb.toString();

        int rawLength = rawNumberOfVariables.length();

        String numberOfVariables = 
        StringUtils.stripStart((StringUtils.strip(rawNumberOfVariables)), "0");
        
        if ((numberOfVariables.equals(""))&&
                (numberOfVariables.length() == rawLength)){
            numberOfVariables ="0";
        }

        int numberOfVariables10 = Integer.valueOf(numberOfVariables, 30);
        
        dbgLog.fine("number of variables="+numberOfVariables10);


        // initialize variable-related storage objects
        
        varQnty = numberOfVariables10;
        smd.getFileInformation().put("varQnty", varQnty);

        dbgLog.fine("***** decodeNumberOfVariables(): end *****");
    }


    /**
     *
     * @param reader
     */
    protected void decodeFieldNo5(BufferedReader reader){
        dbgLog.fine("***** 5: decodeFieldNo5(): start *****");
    
        if (reader ==null){
            throw new IllegalArgumentException("decodeFieldNo5: reader == null!");
        }    
        
        int field5;
        try {
            field5 = parseNumericField(reader);

            dbgLog.fine("field5="+field5);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        

        dbgLog.fine("***** decodeFieldNo5(): end *****");
    }


    /**
     *
     * @param reader
     */
    protected void decodeWeightVariable(BufferedReader reader){
        dbgLog.fine("***** 6: decodeWeightVariable(): start *****");
        
        if (reader ==null){
            throw new IllegalArgumentException("decodeWeightVariable: reader == null!");
        }    
        
        String weightVariableName=null;
        
        try {
            weightVariableName = parseStringField(reader);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        smd.getFileInformation().put("caseWeightVariableName", weightVariableName);
        
        dbgLog.fine("caseWeightVariableName="+weightVariableName);
        dbgLog.fine("***** decodeWeightVariable(): end *****");
    }


    /**
     *
     * @param reader
     */
    protected void decodeVariableInformation(BufferedReader reader){
        dbgLog.fine("***** 7: decodeVariableInformation(): start *****");
        
        if (reader ==null){
            throw new IllegalArgumentException("decodeVariableInformation: reader == null!");
        } 

        try {
        // step 1: variable type
            int variableType = parseNumericField(reader);

            dbgLog.fine("variableType="+variableType);
            
            variableTypelList.add(variableType);
            
            if (variableType > 0){
                isCurrentVariableString = true;
                dbgLog.fine("************* current variable is String type *************");
            } else {
                isCurrentVariableString = false;
            }
            
            
        // step 2: variable name            
            String variableName = parseStringField(reader);
            
            currentVariableName = variableName;
            variableNameList.add(variableName);
            dbgLog.fine("variableName="+variableName);
           
            variableTypeTable.put(variableName,variableType);
           
            if (variableType > 0){
                StringVariableTable.put(variableName,variableType);
            }
           
        // step 3: format(print/write)

            int[] printWriteFormatTable = new int[6];
            for (int i=0; i < 6; i++){
                printWriteFormatTable[i]= parseNumericField(reader);
            }
            
            dbgLog.fine("printWriteFormatTable="+printWriteFormatTable);
            
            // TO DO
            
            // save data
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }












        
        dbgLog.fine("***** decodeVariableInformation(): end *****");
    }


    /**
     *
     * @param reader
     */
    protected void decodeMissValuePointNumeric(BufferedReader reader){
        dbgLog.fine("***** 8: decodeMissValuePointNumeric(): start *****");
        if (reader ==null){
            throw new IllegalArgumentException("decodeMissValuePointNumeric: reader == null!");
        }
        
        if (missingValueCodeTable.containsKey(currentVariableName)){
            missingValueCodeTable.get(currentVariableName).add("8");
        } else {
            List<String> mvc = new ArrayList<String>();
            mvc.add("8");
            missingValueCodeTable.put(currentVariableName, mvc);
        }


        String missingValuePoint=null;

        try {
            // missing values are not always integers
            String base30value = getNumericFieldAsRawString(reader);
            if (base30value.indexOf(".")>=0){
                missingValuePoint = doubleNumberFormatter.format(
                    base30Tobase10Conversion(base30value));
            } else {
                missingValuePoint= Integer.valueOf(base30value, 30).toString();
            }

            dbgLog.fine("missingValuePoint="+missingValuePoint);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        if (missingValueTable.containsKey(currentVariableName)){
            // already stored
            (missingValueTable.get(currentVariableName)).add(missingValuePoint);
        } else {
            // no missing value stored
            List<String> mv = new ArrayList<String>();
            mv.add(missingValuePoint);
            missingValueTable.put(currentVariableName, mv);
        }
        
        
        dbgLog.fine("***** decodeMissValuePointNumeric(): end *****");
    }


    /**
     *
     * @param reader
     */
    protected void decodeMissValuePointString(BufferedReader reader){
        dbgLog.fine("***** 8S: decodeMissValuePointString(): start *****");
        //dbgLog.fine("***** 8S: decodeMissValuePointString(): start *****");
        if (reader ==null){
            throw new IllegalArgumentException("decodeMissValuePointString: reader == null!");
        }    
        
        if (missingValueCodeTable.containsKey(currentVariableName)){
            missingValueCodeTable.get(currentVariableName).add("8");
        } else {
            List<String> mvc = new ArrayList<String>();
            mvc.add("8");
            missingValueCodeTable.put(currentVariableName, mvc);
        }
        
        String missingValuePointString =null;
        
        try {
            missingValuePointString = parseStringField(reader);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        dbgLog.fine("missingValuePointString="+missingValuePointString);
        
        if (missingValueTable.containsKey(currentVariableName)){
            // already stored
            (missingValueTable.get(currentVariableName)).add(missingValuePointString);
        } else {
            // no missing value stored
            List<String> mv = new ArrayList<String>();
            mv.add(missingValuePointString);
            missingValueTable.put(currentVariableName, mv);
        }
        
        
        dbgLog.fine("***** decodeMissValuePointString(): end *****");
    }


    /**
     * 
     * @param reader
     */
    protected void decodeMissValueRangeLow(BufferedReader reader){
        dbgLog.fine("***** 9: decodeMissValueRangeLow(): start *****");
        
        if (reader ==null){
            throw new IllegalArgumentException("decodeMissValueRangeLow: reader == null!");
        }
        
        if (missingValueCodeTable.containsKey(currentVariableName)){
            missingValueCodeTable.get(currentVariableName).add("9");
        } else {
            List<String> mvc = new ArrayList<String>();
            mvc.add("9");
            missingValueCodeTable.put(currentVariableName, mvc);
        }

        String missingValueRangeLOtype=null;

        try {

            // missing values are not always integers
            String base30value = getNumericFieldAsRawString(reader);
            
            if (base30value.indexOf(".")>=0){
                missingValueRangeLOtype = doubleNumberFormatter.format(
                    base30Tobase10Conversion(base30value));
            } else {
                missingValueRangeLOtype= Integer.valueOf(base30value, 30).toString();
            }

            dbgLog.fine("missingValueRangeLOtype="+missingValueRangeLOtype);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        
        if (missingValueTable.containsKey(currentVariableName)){
            // already stored
            (missingValueTable.get(currentVariableName)).add(missingValueRangeLOtype);
        } else {
            // no missing value stored
            List<String> mv = new ArrayList<String>();
            mv.add(missingValueRangeLOtype);
            missingValueTable.put(currentVariableName, mv);
        }
        
        dbgLog.fine("***** decodeMissValueRangeLow(): end *****");
    }


    /**
     *
     * @param reader
     */
    protected void decodeMissValueRangeHigh(BufferedReader reader){
        dbgLog.fine("***** A: decodeMissValueRangeHigh(): start *****");
        
        if (reader ==null){
            throw new IllegalArgumentException("decodeMissValueRangeHigh: reader == null!");
        }
        
        if (missingValueCodeTable.containsKey(currentVariableName)){
            missingValueCodeTable.get(currentVariableName).add("A");
        } else {
            List<String> mvc = new ArrayList<String>();
            mvc.add("A");
            missingValueCodeTable.put(currentVariableName, mvc);
        }

        String missingValueRangeHItype = null;

        try {
        
            // missing values are not always integers
            String base30value = getNumericFieldAsRawString(reader);
            
            if (base30value.indexOf(".")>=0){
                missingValueRangeHItype = doubleNumberFormatter.format(
                    base30Tobase10Conversion(base30value));
            } else {
                missingValueRangeHItype= Integer.valueOf(base30value, 30).toString();
            }

            dbgLog.fine("missingValueRangeHItype="+missingValueRangeHItype);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (missingValueTable.containsKey(currentVariableName)){
            // already stored
            (missingValueTable.get(currentVariableName)).add(missingValueRangeHItype);
        } else {
            // no missing value stored
            List<String> mv = new ArrayList<String>();
            mv.add(missingValueRangeHItype);
            missingValueTable.put(currentVariableName, mv);
        }
        
        
        
        dbgLog.fine("***** decodeMissValueRangeHigh(): end *****");
    }
    
    
    /**
     *
     * @param reader
     */
    protected void decodeMissValueRange(BufferedReader reader){
        dbgLog.fine("***** B: decodeMissValueRange(): start *****");

        if (reader ==null){
            throw new IllegalArgumentException("decodeMissValueRange: reader == null!");
        }

        if (missingValueCodeTable.containsKey(currentVariableName)){
            missingValueCodeTable.get(currentVariableName).add("B");
        } else {
            List<String> mvc = new ArrayList<String>();
            mvc.add("B");
            missingValueCodeTable.put(currentVariableName, mvc);
        }
        
        String[] missingValueRange = new String[2];

        try {

            // missing values are not always integers
            String base30value0 = getNumericFieldAsRawString(reader);
            
            if (base30value0.indexOf(".")>=0){
                missingValueRange[0] = doubleNumberFormatter.format(
                    base30Tobase10Conversion(base30value0));
            } else {
                missingValueRange[0]= Integer.valueOf(base30value0, 30).toString();
            }
            
            String base30value1 = getNumericFieldAsRawString(reader);

            if (base30value1.indexOf(".")>=0){
                missingValueRange[1] = doubleNumberFormatter.format(
                    base30Tobase10Conversion(base30value1));
            } else {
                missingValueRange[1]= Integer.valueOf(base30value1, 30).toString();
            }
            
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        dbgLog.fine("missingValueRange="+missingValueRange);
        

        if (missingValueTable.containsKey(currentVariableName)){
            // already stored
            (missingValueTable.get(currentVariableName)).add(missingValueRange[0]);
            (missingValueTable.get(currentVariableName)).add(missingValueRange[1]);
        } else {
            // no missing value stored
            List<String> mv = new ArrayList<String>();
            mv.add(missingValueRange[0]);
            mv.add(missingValueRange[1]);
            missingValueTable.put(currentVariableName, mv);
        }
        
        
        
        dbgLog.fine("***** decodeMissValueRange(): end *****");
    }
    

    /**
     *
     * @param reader
     */
    protected void decodeVariableLabel(BufferedReader reader){
        dbgLog.fine("***** C: decodeVariableLabel(): start *****");
                
        if (reader ==null){
            throw new IllegalArgumentException("decodeVariableLabel: reader == null!");
        }    

        try {
        
            String variableLabel = parseStringField(reader);
            variableLabelMap.put(currentVariableName, variableLabel);
            dbgLog.fine("variableLabel="+variableLabel);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // note: not all variables have their variable label; therefore,
        // saving them to the metatadata object is done within read() method
        
        dbgLog.fine("***** decodeVariableLabel(): end *****");
    }
    
    
    /**
     *
     * @param reader
     */
    protected void decodeValueLabel(BufferedReader reader){
        dbgLog.fine("***** D: decodeValueLabel(): start *****");

        dbgLog.fine("variableTypeTable="+(variableTypeTable));
        dbgLog.fine("variableTypelList="+variableTypelList);
        
        Map<String, String> valueLabelSet = new LinkedHashMap<String, String>();
        
        try {
            int numberOfVariables = parseNumericField(reader);
            
            dbgLog.fine("numberOfVariables for this set="+numberOfVariables);

            String[] variableNames = new String[numberOfVariables];

            for (int i= 0; i< numberOfVariables; i++){
                variableNames[i] = parseStringField(reader);
            }
            dbgLog.fine("variableNames="+variableNames.toString());

            int numberOfvalueLabelSets = parseNumericField(reader);

            dbgLog.fine("numberOfvalueLabelSets="+numberOfvalueLabelSets);
            boolean isStringType = variableTypeTable.get(variableNames[0]) > 0 ? true : false;
            
            for (int i=0; i<numberOfvalueLabelSets ;i++){
                String[] tempValueLabel = new String[2];
                if (isStringType){
                    // String case
                    tempValueLabel[0] = parseStringField(reader);
                } else {
                    // Numeric case
                    // values may not be always integers
                    String base30value = getNumericFieldAsRawString(reader);

                    if (base30value.indexOf(".")>=0){
                        tempValueLabel[0] = doubleNumberFormatter.format(
                            base30Tobase10Conversion(base30value));
                    } else {
                        tempValueLabel[0]= Integer.valueOf(base30value, 30).toString();
                    }
                    
                    
                }
                dbgLog.fine(i+"-th value="+tempValueLabel[0]);
                tempValueLabel[1] = parseStringField(reader);
                dbgLog.fine(i+"-th label="+tempValueLabel[1]);
                valueLabelSet.put(tempValueLabel[0],tempValueLabel[1]);
            }
            // save the value-label mapping list
            // use the first variable name as the key
            valueLabelTable.put(variableNames[0], valueLabelSet);
            
            // create a mapping table that finds the key variable for this mapping table
            for (String vn : variableNames){
                valueVariableMappingTable.put(vn, variableNames[0]);
                dbgLog.fine(vn);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        dbgLog.fine("valueLabelTable:\n"+valueLabelTable);
        dbgLog.fine("valueLabelSet:\n"+valueLabelSet);

        dbgLog.fine("valueVariableMappingTable:\n"+valueVariableMappingTable);
        
        
        dbgLog.fine("***** decodeValueLabel(): end *****");
    }


    /**
     *
     * @param reader
     */
    protected void decodeDocument(BufferedReader reader){
        dbgLog.fine("***** E: decodeDocument(): start *****");
        
        
        if (reader ==null){
            throw new IllegalArgumentException("decodeVariableLabel: reader == null!");
        }    
        
        int noOfdocumentLines;
        String[] document = null;
        
        try {
            noOfdocumentLines = parseNumericField(reader);
            
            dbgLog.fine("noOfdocumentLines="+noOfdocumentLines);

            document = new String[noOfdocumentLines];

            for (int i= 0; i< noOfdocumentLines; i++){

                document[i] = parseStringField(reader);
            }
            
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        smd.getFileInformation().put("document", StringUtils.join(document," " ));
        
        
        dbgLog.fine("***** decodeDocument(): end *****");
    }


    /**
     *
     * @param reader
     * @throws java.io.IOException
     */
    protected void decodeData(BufferedReader reader) throws IOException{
        dbgLog.fine("***** F: decodeData(): start *****");
        
        // set-up for dumping data as a tab-delimited file
        FileOutputStream fileOutTab = null;
        PrintWriter pwout = null;
        
        try {
            // create a File object to save the tab-delimited data file
            File tabDelimitedDataFile = File.createTempFile("tempTabfile.", ".tab");

            String tabDelimitedDataFileName   = tabDelimitedDataFile.getAbsolutePath();

            // save the temp file name in the metadata object
            smd.getFileInformation().put("tabDelimitedDataFileLocation", tabDelimitedDataFileName);

            fileOutTab = new FileOutputStream(tabDelimitedDataFile);
            
            pwout = new PrintWriter(new OutputStreamWriter(fileOutTab, "utf8"), true);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex){
            ex.printStackTrace();
        }
        
        // contents (variable) checker concering decimals
        variableTypeFinal= new int[varQnty];
        Arrays.fill(variableTypeFinal, 0);

        
        // raw-case counter
        int j = 0; // case 
        
        int lastVariable = varQnty -1;
        
        int numberOfDecimalVariables = 0;
        
        // for writing a tab-delimited observation
        //StringBuilder sb_CasewiseRecord = new StringBuilder("");


        boolean isMissingValue = false;
        
        StringBuilder sb_StringLengthBase30 = new StringBuilder("");
        StringBuilder sb_datumNumericBase30 = new StringBuilder("");

        List<String[]> dataTableList = new ArrayList<String[]>();
        
        int StringLengthBase10=0;
        
        try{
            // use while because the number of cases (observations) is usually unknown
            FBLOCK: while(true){
                j++;
                String[] casewiseRecord = new String[varQnty];
                for (int i=0; i<varQnty; i++){
                    // check the type of this variable
                    boolean isStringType = 
                        variableTypeTable.get(variableNameList.get(i)) > 0 ? true : false;
                    


                    if (isStringType){
                        // String
                        dbgLog.fine(i+"-th var is String variable case");

                        variableTypeFinal[i]=-1;

                        String buffer = "";
                        char[] tmp = new char[1];

                        int nint;
                        while((nint = reader.read(tmp))>0){
                            buffer =  Character.toString(tmp[0]);
                            if (buffer.equals("/")){
                                break;
                            } else if (buffer.equals("Z")){
                                if (i == 0){
                                    // the reader has passed the last case
                                    // subtract 1 from the j counter
                                    caseQnty = j-1;
                                    break FBLOCK;
                                }
                            } else {
                                sb_StringLengthBase30.append(buffer);
                            }  
                            //dbgLog.fine("buffer(String case)="+buffer);
                            //dbgLog.fine("sb_StringLengthBase30="+sb_StringLengthBase30.toString());

                        }
                        if (nint == 0){
                            //dbgLog.fine("reached to the eof");
                            caseQnty = j -1;
                            break FBLOCK;
                        }
                        

                        dbgLog.fine(j+"-th case "+i+"=th var:datum length=" +sb_StringLengthBase30.toString());
                        // this length value should be a positive integer
                        Matcher mtr = pattern4positiveInteger.matcher(sb_StringLengthBase30.toString());
                        if (mtr.matches()){
                            StringLengthBase10 = Integer.valueOf(sb_StringLengthBase30.toString(), 30);
                            dbgLog.fine("length of the string (StringLengthBase10)="+StringLengthBase10);
                        } else{
                            throw new IOException("reading F(data) section: "+
                                            "string: length is not integer");
                        }
                        
                        // read this string-variable's contents after "/"
                        char[] char_datumString = new char[StringLengthBase10];
                        reader.read(char_datumString);

                        String datumString = new String(char_datumString);
                        dbgLog.fine("string data="+datumString);

                        if ((StringLengthBase10==1)&&(datumString.equals(" "))){
                            datumString = Double.toHexString(systemMissingValue);
                        }
                        
                        
                        
//dataTable2[i][j-1] = datumString;

                        
                        // tab-delimited file processing
                        //if (i == lastVariable){
                            //sb_CasewiseRecord.append(datumString);
                        casewiseRecord[i]= datumString;
                        //} else {
                         //   sb_CasewiseRecord.append(datumString + "\t");
                        //}



                        // reset working objects
                        StringLengthBase10=0;

                        sb_StringLengthBase30.setLength(0);

                    } else {
                    
                    // numeric case
                    
                        dbgLog.fine(i+"th variable is numeric");
                        
                        int MissingValue = 0;
                        isMissingValue = false;
                        String datumNumericBase10 = null;
                        String buffer = "";
                        char[] tmp = new char[1];
                        int nint;
                        while((nint = reader.read(tmp))>0){
                            sb_datumNumericBase30.append(buffer);
                            buffer = Character.toString(tmp[0]);

                            if (buffer.equals("/")){
                                break;
                            } else if (buffer.equals("Z")){
                                if (i == 0){
                                    // the reader has passed the last case
                                    // subtract 1 from the j counter
                                    dbgLog.fine("Z-mark was detected");
                                    caseQnty = j-1;
                                    break FBLOCK;
                                }
                            } else if (buffer.equals("*")) {
                                datumNumericBase10 = Double.toHexString(systemMissingValue);
                                isMissingValue = true;
                                
                                // read next char '.' as part of missing value
                                reader.read(tmp);
                                buffer = Character.toString(tmp[0]);
                                break;
                            }
                            //dbgLog.fine("buffer(numeric ase)="+buffer);
                            //dbgLog.fine("sb_datumNumericBase30="+sb_datumNumericBase30.toString());
                        }
                        if (nint == 0){
                            //dbgLog.fine("reached to the eof");
                            caseQnty = j -1;
                            break FBLOCK;
                        }
                        //dbgLog.fine("buffer(numeric case)="+buffer);
                        dbgLog.fine(j+"-th case "+i+"=th var:(N:base-30)=" +sb_datumNumericBase30.toString());
                        
                        String datumNumericBase30 = sb_datumNumericBase30.toString();
                        
                        
                        
                        if (!isMissingValue){
                        
                            // integer-test by regex 

                            Matcher matcher = pattern4Integer.matcher(datumNumericBase30);

                            if (matcher.matches()){
                                // integer case
                                datumNumericBase10 = Integer.valueOf(datumNumericBase30, 30).toString();
                            } else {
                                // double case
                                datumNumericBase10 = doubleNumberFormatter.format(
                                    base30Tobase10Conversion(datumNumericBase30));
                            }
                            
                            dbgLog.fine(j+"th case"+i+"-th var(base-10)="+datumNumericBase10);
                            
                            
                            
                            // date/time case : TO DO
                            
                            
                            
                            
                                
                            // decimal-point check (varialbe is integer or not)
                            if (variableTypeFinal[i]==0){
                                if (datumNumericBase10.indexOf(".") >=0){
                                    dbgLog.fine("decimal data= "+ datumNumericBase10);
                                    variableTypeFinal[i] = 1;
                                    numberOfDecimalVariables++;
                                }
                            }
                        }
                        
                        
 //dataTable2[i][j-1] = datumNumericBase10;
                        casewiseRecord[i]= datumNumericBase10;
//                        if (i == lastVariable){
//                            sb_CasewiseRecord.append(datumNumericBase10);
//                        } else {
//                            sb_CasewiseRecord.append(datumNumericBase10 + "\t");
//                        }

                        isMissingValue = false ;

                        // reset the number-building stringBuilder
                        sb_datumNumericBase30.setLength(0);

                    } // if: string vs numeric

                }// loop-i (variable)

                dataTableList.add(casewiseRecord);
                dbgLog.finer(j+"-th:"+StringUtils.join(casewiseRecord, "\t"));
                // print the i-th case
                pwout.println(StringUtils.join(casewiseRecord, "\t"));

                // reset the case builder
                //sb_CasewiseRecord.setLength(0);
            } // while-block

            // close the print writer
            pwout.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        // exit processing
        
        // save a decimal-type variable's number
        for (int l=0; l< variableTypeFinal.length;l++){
            if (variableTypeFinal[l]>0){
                decimalVariableSet.add(l);
            }
        }

        smd.setDecimalVariables(decimalVariableSet);
        
        dbgLog.fine("variableTypeFinal="+ArrayUtils.toString(variableTypeFinal));
        dbgLog.fine("numberOfDecimalVariables="+numberOfDecimalVariables);
        dbgLog.fine("decimalVariableSet="+decimalVariableSet);


        smd.getFileInformation().put("caseQnty", caseQnty);



        dbgLog.fine("caseQnty="+caseQnty);
        // store data in column(variable)-wise for calculating variable-wise
        // statistics
        dataTable2 = new Object[varQnty][caseQnty];

        for (int jl=0; jl<caseQnty;jl++){

           for (int jk=0;jk<varQnty;jk++){

              dataTable2[jk][jl] = dataTableList.get(jl)[jk] ;
           }
        }

//        print2Darray(dataTable2, "dataTable2");

        //out.println("dataTable2:\n"+Arrays.deepToString(dataTable2));






        unfValues = new String[varQnty];

        for (int k=0;k<varQnty; k++){
            int variableTypeNumer = variableTypeFinal[k];
            
//            if (variableTypeNumer >0){
//                variableTypeNumer = 1;
//            } else {
//                variableTypeNumer= 0;
//            }
            
            try {
                unfValues[k] = getUNF(dataTable2[k], variableTypeNumer,
                    unfVersionNumber, k);
                dbgLog.fine(k+"th unf value"+unfValues[k]);

            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            } catch (UnfException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            }
        }
        

        dbgLog.fine("unf set:\n"+ReflectionToStringBuilder.toString(unfValues));
        
        try {
            fileUnfValue = UNFUtil.calculateUNF(unfValues,unfVersionNumber);

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        } catch (UnfException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        
        dbgLog.fine("file-unf="+fileUnfValue);
        
        porDataSection.setUnf(unfValues);

        porDataSection.setFileUnf(fileUnfValue);

        smd.setVariableUNF(unfValues);
        
        smd.getFileInformation().put("fileUNF", fileUnfValue);
        dbgLog.fine("unf values:\n"+unfValues);
        
        porDataSection.setData(dataTable2);


        
        dbgLog.fine("***** decodeData(): end *****");
    }
    
    
    
    
    
    
    // utility methods  -----------------------------------------------------//
    
    private int parseNumericField(BufferedReader reader) throws IOException{
        String temp = null;
        char[] tmp = new char[1];
        StringBuilder sb = new StringBuilder();
        while(reader.read(tmp) > 0 ){
            temp = Character.toString(tmp[0]);//new String(tmp);
            if (temp.equals("/")){
                break;
            } else {
                sb.append(temp);
            }
            //temp = sb.toString();//new String(tmp);
        }
        String base30numberString = sb.toString();
        dbgLog.fine("base30numberString="+base30numberString);
        int base10equivalent = Integer.valueOf(base30numberString, 30);
        dbgLog.fine("base10equivalent="+base10equivalent);
        return base10equivalent;
    }


    private String parseStringField(BufferedReader reader) throws IOException{
        String temp = null;
        char[] tmp = new char[1];
        StringBuilder sb = new StringBuilder();
        while(reader.read(tmp) > 0 ){
            temp = Character.toString(tmp[0]);//new String(tmp);
            if (temp.equals("/")){
                break;
            } else {
                sb.append(temp);
            }
            //temp = sb.toString();//new String(tmp);
        }
        String base30numberString = sb.toString();
        //dbgLog.fine("base30numberString="+base30numberString);
        int base10equivalent = Integer.valueOf(base30numberString, 30);
        //dbgLog.fine("base10equivalent="+base10equivalent);
        char[] stringBody = new char[base10equivalent];
        reader.read(stringBody);
        String stringData = new String(stringBody);
        dbgLog.fine("stringData="+stringData);
        return stringData;
    }



    private String getNumericFieldAsRawString(BufferedReader reader) throws IOException{
        String temp = null;
        char[] tmp = new char[1];
        StringBuilder sb = new StringBuilder();
        while(reader.read(tmp) > 0 ){
            temp = Character.toString(tmp[0]);//new String(tmp);
            if (temp.equals("/")){
                break;
            } else {
                sb.append(temp);
            }
            //temp = sb.toString();//new String(tmp);
        }
        String base30numberString = sb.toString();
        dbgLog.fine("base30numberString="+base30numberString);

        return base30numberString;
    }


    /**
     *
     * @param base30String
     * @return
     */
    public static double base30Tobase10Conversion(String base30String){

        // new base(radix) number
        int oldBase = 30;
        //dbgLog.fine("base30String="+base30String);

        // trim white-spaces from the both ends
        String base30StringClean = StringUtils.trim(base30String);
        //dbgLog.fine("base30StringClean="+base30StringClean);

        // check the negative/positive sign
        boolean isNegativeNumber = false;
        boolean hasPositiveSign = false;
        if (base30StringClean.startsWith("-")){
            isNegativeNumber = true;
        }

        if (base30StringClean.startsWith("+")){
            hasPositiveSign = true;
        }

        // remove the sign if exits
        String base30StringNoSign = null;

        if ((isNegativeNumber) ||(hasPositiveSign)){
            base30StringNoSign = base30StringClean.substring(1);
        } else {
            base30StringNoSign = new String(base30StringClean);
        }

        // check the scientific notation
        // if so, divide it into the significand and exponent

        boolean hasExponent = false;
        boolean hasPositiveExponent = false;
        boolean hasNegativeExponent = false;

        String significand  = null;
        String exponent = null;

        int plusIndex = base30StringNoSign.indexOf("+");
        int minusIndex = base30StringNoSign.indexOf("-");

        if (plusIndex> 0){
            // positive exponent
            //dbgLog.fine("plusIndex="+plusIndex);
            significand = base30StringNoSign.substring(0, plusIndex);
            //dbgLog.fine("significand="+ significand);
            String exponentRaw = base30StringNoSign.substring(plusIndex+1);
            exponent = exponentRaw.substring(exponentRaw.indexOf("0")+1);
            hasPositiveExponent=true;
            hasExponent = true;
            //dbgLog.fine("exponent="+exponent);
        } else if (minusIndex > 0){
            // negative exponent
            //dbgLog.fine("minusIndex="+minusIndex);
            significand = base30StringNoSign.substring(0, minusIndex);
            String exponentRaw    =  base30StringNoSign.substring(minusIndex+1);
            exponent = exponentRaw.substring(exponentRaw.indexOf("0")+1);
            //dbgLog.fine("exponent="+exponent);
            hasNegativeExponent=true;
            hasExponent = true;
        } else {
            significand = new String(base30StringNoSign);
        }

        //dbgLog.fine("significand="+ significand);

        // convert the exponent to a base-10 number if the exponent exits
        int exponentValue = 0;
        if (hasExponent){
            exponentValue = Integer.valueOf(exponent, oldBase);
            if (hasNegativeExponent){
                exponentValue = ~exponentValue+1;
            }
        }

        //dbgLog.fine("exponentValue="+exponentValue);

        // check the position of the decimal point
        // index-no starts from 0
        int index_dpn = significand.indexOf(".");

        //dbgLog.fine("dpPosition="+index_dpn);

        int length_Significand = significand.length();

        // note: split("") method incorrectly divided a string into a String[]
        // "" was inserted as the first element
        // is there any left-over after StringUtils.remove()?
        char[] base30Significand= null;
        if (index_dpn >= 0){
            base30Significand = StringUtils.remove(significand, ".").toCharArray();
            if (base30Significand.length != (length_Significand-1)){
               err.println("Tokeninzing the significand failed");
            }
        } else {
            base30Significand = significand.toCharArray();
            if (base30Significand.length != length_Significand){
                err.println("Tokeninzing the significand failed");
            }
        }

        //dbgLog.fine("base30Significand="+ArrayUtils.toString(base30Significand));

        // create a power-value array
        int[] powerNumbers = new int[base30Significand.length];
        // if the decimal point does not exist, set the index to
        // the length  of the significand
        if (index_dpn < 0){
            index_dpn = base30Significand.length;
        }
        
        for (int j=0;j<powerNumbers.length;j++){
            if (j < index_dpn){
                powerNumbers[j] = index_dpn-j -1;
            } else {
                powerNumbers[j] = -1*(j+1-index_dpn);
            }
        }

        //dbgLog.fine(ArrayUtils.toString(powerNumbers));

        // adjust the power value with the exponent
        if (hasExponent){
            for (int i=0; i< powerNumbers.length;i++){
                powerNumbers[i] += exponentValue;
            }
        }

        // calculate the base-10 value as a double
        double base = 10;
        double base10value = 0d;
        for (int k=0;k<powerNumbers.length;k++){
            base10value += Long.valueOf(
                        String.valueOf(base30Significand[k]), oldBase)*
                        Math.pow(oldBase, powerNumbers[k]);
        }

        // negative sign if applicable
        if (isNegativeNumber){
            base10value = -1d*base10value;
        }

        return base10value;
    }
    
    void print2Darray(Object[][] datatable, String title){
        dbgLog.fine(title);
        for (int i=0; i< datatable.length; i++){
            dbgLog.fine(StringUtils.join(datatable[i], "|"));
        }
    }    
    
    private String getUNF(Object[] varData, int variableType, 
        String unfVersionNumber, int variablePosition)
        throws NumberFormatException, UnfException,
        IOException, NoSuchAlgorithmException{
        String unfValue = null;

        dbgLog.fine("varData:\n"+ReflectionToStringBuilder.toString(varData));
        dbgLog.fine("variableType="+variableType);
        dbgLog.fine("unfVersionNumber="+unfVersionNumber);
        dbgLog.fine("variablePosition="+variablePosition);
    
        switch(variableType){

            case 0:
                // integer case
                dbgLog.fine("integer case");
                // data are stored as storing

                long[] ldata = new long[varData.length];
                for (int i=0;i<varData.length;i++){
                    if (((String)varData[i]).equals("NaN")){
                        ldata[i] = Long.MAX_VALUE;
                    } else {
                        ldata[i] = Long.parseLong((String)varData[i]);
                    }
                }

                unfValue = UNFUtil.calculateUNF(ldata, unfVersionNumber);

                dbgLog.finer("sumstat:long case="+Arrays.deepToString(
                        ArrayUtils.toObject(StatHelper.calculateSummaryStatistics(ldata))));
                        
                smd.getSummaryStatisticsTable().put(variablePosition,
                    ArrayUtils.toObject(StatHelper.calculateSummaryStatistics(ldata)));


                Map<String, Integer> catStat = StatHelper.calculateCategoryStatistics(ldata);
                dbgLog.fine("catStat="+catStat);

                smd.getCategoryStatisticsTable().put(variableNameList.get(variablePosition), catStat);


                break;
            case 1:
                // double case
                dbgLog.fine("double case");
                // data are store as storing

                double[] ddata = new double[varData.length];
                for (int i=0;i<varData.length;i++){
                    if (((String)varData[i]).equals("NaN")){
                        ddata[i] = Double.NaN;
                    } else {
                        ddata[i] =  Double.parseDouble((String)varData[i]);
                    }
                }

                unfValue = UNFUtil.calculateUNF(ddata, unfVersionNumber);

                smd.getSummaryStatisticsTable().put(variablePosition,
                    ArrayUtils.toObject(StatHelper.calculateSummaryStatistics(ddata)));

                break;
            case  -1:
                // String case
                dbgLog.fine("string case");

                String[] strdata = Arrays.asList(varData).toArray(
                    new String[varData.length]);

                unfValue = UNFUtil.calculateUNF(strdata, unfVersionNumber);
                dbgLog.fine("string:unfValue"+unfValue);

                smd.getSummaryStatisticsTable().put(variablePosition,
                    StatHelper.calculateSummaryStatistics(strdata));

                Map<String, Integer> StrCatStat = StatHelper.calculateCategoryStatistics(strdata);
                dbgLog.fine("catStat="+StrCatStat);

                smd.getCategoryStatisticsTable().put(variableNameList.get(variablePosition), StrCatStat);

                break;
            default:
                dbgLog.fine("unknown variable type found");
                String errorMessage =
                    "unknow variable Type found at varData section";
                    throw new IllegalArgumentException(errorMessage);

        } // switch
    
        //dbgLog.fine("unfvalue(last)="+unfValue);
        return unfValue;
    }
    
    

}
