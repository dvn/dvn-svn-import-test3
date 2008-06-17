/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.dsb.impl;

import java.util.*;
import java.util.Scanner;
import java.io.*;
import java.io.FileNotFoundException;
import static java.lang.System.*;
import org.apache.commons.lang.*;

import edu.harvard.hmdc.vdcnet.dsb.*;


/**
 *
 * @author a.sone
 */
 
public class DvnJavaFieldCutter implements FieldCutter{
    
    public boolean debug=true;
    
    public  void subsetFile(String infile, String outfile, Set<Integer> columns) {
        subsetFile(infile, outfile, columns, "\t");
    }

    public void subsetFile(String infile, String outfile, Set<Integer> columns,
        String delimiter) {
        try {
          Scanner scanner =  new Scanner(new File(infile));
          
          if (debug){
            System.out.println("outfile="+outfile);
          }
          
          BufferedWriter out = new BufferedWriter(new FileWriter(outfile));
          scanner.useDelimiter("\\n");

          while (scanner.hasNext()) {
              String[] line = (scanner.next()).split(delimiter);
              if ((line.length <= 1) && (line[0].equals(""))) {
                  break;
              }
              List<String> ln = new ArrayList<String>();
              for (Integer i : columns) {
                  ln.add(line[i]);
              }
              
              if (debug){
                System.out.println(StringUtils.join(ln,"\t"));
              }
              
              out.write(StringUtils.join(ln,"\t")+"\n");
              ln=null;
          }
          
          scanner.close();
          out.close();
          
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
