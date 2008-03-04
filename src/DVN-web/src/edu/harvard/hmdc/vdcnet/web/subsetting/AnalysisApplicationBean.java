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
 * AnalysisApplicationBean.java
 *
 * Created on October 16, 2006, 11:16 PM
 */
package edu.harvard.hmdc.vdcnet.web.subsetting;

import com.sun.rave.web.ui.appbase.AbstractApplicationBean;
import javax.faces.FacesException;

import edu.harvard.hmdc.vdcnet.dsb.DSBWrapper;


import javax.xml.bind.*;
/*
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
*/
import java.util.*;
import static java.lang.System.*;
import java.io.StringReader;

// for BigInteger 2 int
import java.math.*;
import java.io.IOException;

// zelig-config class
import edu.harvard.hmdc.vdcnet.dsb.zelig.*;

// zelig-menu option
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import com.sun.rave.web.ui.model.Option;
import com.sun.rave.web.ui.model.OptionGroup;
import com.sun.rave.web.ui.model.OptionTitle;
import com.sun.rave.web.ui.model.Separator;


/**
 * <p>Application scope data bean for your application.  Create properties
 *  here to represent cached data that should be made available to all users
 *  and pages in the application.</p>
 *
 * <p>An instance of this class will be created for you automatically,
 * the first time your application evaluates a value binding expression
 * or method binding expression that references a managed bean using
 * this class.</p>
 */
public class AnalysisApplicationBean extends AbstractApplicationBean implements java.io.Serializable  {
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    protected static Zelig zlg;
    protected static Zelig zlgxtb; 

    
    protected AdvancedStatGUIdata guiSpec;
    protected Map<String, AdvancedStatGUIdata.Model> specMap;
    //protected List<SelectItemGroup> modelMenuOptions;
    protected List<Option> modelMenuOptions;
    
    //protected Map<String, AdvancedStatGUIdata.Model> modelId2SpecMap;
    //protected List <AdvancedStatGUIdata.Model> zlgList;
    //protected Map<String, String> modelCategory;
    //protected List<String> modelCategoryList;
    //protected Map<String, String> modelId2NameMap;

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }
    // </editor-fold>


    /** 
     * <p>Construct a new application data bean instance.</p>
     */
    public AnalysisApplicationBean() {
    }

    /** 
     * <p>This method is called when this bean is initially added to
     * application scope.  Typically, this occurs as a result of evaluating
     * a value binding or method binding expression, which utilizes the
     * managed bean facility to instantiate this bean and store it into
     * application scope.</p>
     * 
     * <p>You may customize this method to initialize and cache application wide
     * data values (such as the lists of valid options for dropdown list
     * components), or to allocate resources that are required for the
     * lifetime of the application.</p>
     */

    public Map<String, AdvancedStatGUIdata.Model> getSpecMap(){
      return this.specMap;
    }
    /*
    public List<SelectItemGroup> getModelMenuOptions(){
      return this.modelMenuOptions;
    }
    */
    public List<Option> getModelMenuOptions(){
      return this.modelMenuOptions;
    }

/*     
    public List<AdvancedStatGUIdata.Model> getZlgList(){
      return this.zlgList;
    }
    
    public List<String> getModelCategoryList(){
      return this.modelCategoryList;
    }
    public Map<String, String> getModelCategory(){
      return this.modelCategory;
    }
    public Map<String, String> getModelId2NameMap(){;
      return this.modelId2NameMap;
    }
*/     
    public void init() {
        // Perform initializations inherited from our superclass
        super.init();
        // Perform application initialization that must complete
        // *before* managed components are initialized
        // TODO - add your own initialiation code here

        // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Initialization">
        // Initialize automatically managed components
        // *Note* - this logic should NOT be modified
        try {
            _init();
        } catch (Exception e) {
            log("AnalysisApplicationBean Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e: new FacesException(e);
        }
        // </editor-fold>
        // Perform application initialization that must complete
        // *after* managed components are initialized
        // TODO - add your own initialization code here

        /*
            add the zelig-specific initialization code
            
        */
        try {
          // create a JAXBContext capable of handling classes generated into
          // the r.zelig package
          JAXBContext jc = JAXBContext.newInstance( "edu.harvard.hmdc.vdcnet.dsb.zelig" );

          // create an Unmarshaller
          Unmarshaller u = jc.createUnmarshaller();

          DSBWrapper dsb = new DSBWrapper();
          
          // String zeligConfig = dsb. getZeligConfig();
          // log("zeligconfig:\n"+zeligConfig);
          zlg = (Zelig)u.unmarshal( new StringReader(dsb. getZeligConfig()) );

          //zlg = (Zelig)u.unmarshal( new FileInputStream( "configZeligGUI.xml" ) );



        } catch( JAXBException je ) {
            je.printStackTrace();
        } catch( IOException ioe ) {
             ioe.printStackTrace();
        }
        //log("before: model size: " + zlg.getModel().size());
        guiSpec = new AdvancedStatGUIdata(zlg.getModel());


    specMap = guiSpec.getModelId2SpecMap();
    
    //out.println( "specMap: " + specMap +"\n");
    
    Set kyzlg = specMap.keySet();
    
    out.println("entry keys of zelig="+kyzlg);

    
    
    // x-tab and other non-zelig options
    // currently x-tab only
    // this part might be done by properties or resourcebundle or pref
    
    StringBuilder sbxtb = new StringBuilder(
    "<?xml version='1.0' encoding='UTF-8'?>"+
    "<zelig xmlns='http://gking.harvard.edu/zelig' "+
    "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' "+
    "xsi:schemaLocation='http://gking.harvard.edu/zelig "+
    "http://vdc-hmdc.harvard.edu/VDC/Schema/analysis/ZeligInterfaceDefinition.xsd'>"+
    "<model name='xtb' label='Categorical Data Analysis'>"+
    "<description>Cross-Tabulation</description>"+
    "<helpLink url=''/><packageDependency name='stats' version= '0.1'/>"+
    "<formula minEquations='1' maxEquations='1' simulEq='0'>"+
    "<equation name='mu'><outcome minVar='2' label='Classification'>"+
    "<modelingType>discrete</modelingType><modelingType>nominal</modelingType>"+
    "<modelingType>ordinal</modelingType><modelingType>binary</modelingType></outcome>"+
    "<outcome maxVar='1' minVar='0' label='Frequency'>"+
    "<modelingType>discrete</modelingType></outcome>"+
    "</equation></formula><setx maxSetx='0'/></model></zelig>");
    
    String configXtabGUI=sbxtb.toString();
    
    out.println("config Xtab:\n"+configXtabGUI+"\n");
    
    try {
      // create a JAXBContext
      JAXBContext jc = JAXBContext.newInstance( "edu.harvard.hmdc.vdcnet.dsb.zelig" );

      // create an Unmarshaller
      Unmarshaller u = jc.createUnmarshaller();

      zlgxtb = (Zelig)u.unmarshal( new StringReader(configXtabGUI) );
     

    } catch( JAXBException je ) {
        je.printStackTrace();
    }

    AdvancedStatGUIdata xtbSpec = new AdvancedStatGUIdata(zlgxtb.getModel());
    Map<String, AdvancedStatGUIdata.Model> specMapXtb = xtbSpec.getModelId2SpecMap();

    out.println("specMapXtb: " + specMapXtb +"\n");
    
    Set kyxtb = specMapXtb.keySet();
    out.println("entry keys of xtb="+kyxtb);
    
    // add xtab to the map by category
    Set kyXtb = specMapXtb.entrySet();
    for (Iterator it = kyXtb.iterator(); it.hasNext();){
      Map.Entry et = (Map.Entry)it.next();
      out.println("xtab key="+et.getKey()+"\n=" + et.getValue());
      specMap.put( (String)et.getKey(), (AdvancedStatGUIdata.Model)et.getValue() );
    }
    out.println("new specMapXtb:\n"+specMapXtb);
    
    out.println("entry keys of zelig(after update)="+kyzlg);
    
    
    // add xtab to the map by mdlName
    Map<String, List> tmpId = guiSpec.getModelCategoryId();
    
    out.println("tmpId(before update)\n"+tmpId);
    
    Map<String, List> tmpIdxtb = xtbSpec.getModelCategoryId();
    
    Set kyTmpIdxtb = tmpIdxtb.entrySet();
    for (Iterator it = kyTmpIdxtb.iterator(); it.hasNext();){
      Map.Entry et = (Map.Entry)it.next();
      out.println("xtab cat key="+et.getKey()+" value="+et.getValue());
      
      tmpId.put((String)et.getKey(), (List)et.getValue());
    }
    
    out.println("\ntmpId(after update)\n"+tmpId);
    
    
    Set entr = tmpId.entrySet();
    out.println("How many model categories="+entr.size());
    // object to be used in the subsetting page
    
    
    modelMenuOptions = new ArrayList<Option>();
    modelMenuOptions.add(new OptionTitle("Choose a Statistical Model"));
    
    //modelMenuOptions.add(new Separator());
    
    // add xtab option here
    // category = Categorical Data Analysis
    // title = Cross-Tabulation
    // name = xtb
/*
      sessionObj.ngc['xtb'] = new guiCntrlr('xtb','xtb','stats','Categorical Data Analysis','',2,2,'','Cross-Tabulation',0);
      sessionObj.ngc['xtb'].bxAttr['xtb_BxR1'] = ['D',2,NOVARS,'NOTcontinuous','Classification'];
      sessionObj.ngc['xtb'].bxAttr['xtb_BxR2'] = ['D',0,1,'NOTcontinuous','Frequency'];
*/

/*
    
      OptionGroup cdaGrp = new OptionGroup();
      cdaGrp.setValue("Categorical Data Analysis");
      cdaGrp.setLabel("Categorical Data Analysis");
      cdaGrp.setDisabled(false);
      cdaGrp.setOptions( new Option[] { new Option("xtb", "Cross-Tabulation") });
      modelMenuOptions.add(cdaGrp);
*/

    //int mdlGrpSize = entr.size();
    //OptionGroup[] mdlGrp = new OptionGroup[mdlGrpSize];
    int ii=0;
    for (Iterator it = entr.iterator(); it.hasNext();){
      Map.Entry et = (Map.Entry)it.next();
      ii++;
      out.println(et.getKey()+"\n=" + et.getValue());
      
      OptionGroup mdlGrp = new OptionGroup();
      
      String sigNo = String.format("%02d", ii);
      StringBuilder sb = new StringBuilder("MdlGrp_");
      sb.append(sigNo);
      String mdlGrpId = sb.toString();
      out.print("group ID token="+mdlGrpId+"\n");
      
      List<String> IdSet = (List)et.getValue();
      out.println("IdSet="+IdSet+"\n");
      
      // for each Id set
      List<Option> tmp = new ArrayList<Option>();
      int flcnt=0;
      for (int i=0; i<IdSet.size(); i++){
        if (specMap.containsKey(IdSet.get(i))){
            tmp.add(new Option(specMap.get(IdSet.get(i)).getMdlName(), specMap.get(IdSet.get(i)).getTitle()));
            out.println(specMap.get(IdSet.get(i)).getMdlName()+"\t"+specMap.get(IdSet.get(i)).getTitle());
        } else{
            flcnt++;
        }
      } // for each model
      
      if (IdSet.size() > flcnt){
          mdlGrp.setValue((String)et.getKey());
          mdlGrp.setLabel((String)et.getKey());
          mdlGrp.setDisabled(false);
          mdlGrp.setOptions((Option[])(tmp.toArray(new Option[tmp.size()])));
          modelMenuOptions.add(mdlGrp);
      
      } else {
         out.println("former" + ii+"-th group was empty and excluded ");
         ii--;
      }
      out.println("end of "+ii+"-th group\n\n");
    } // for each model-category
      out.println("\nmodelMenuOptions(size)="+modelMenuOptions.size()+"\n");
      out.println("dump of modelMenuOptions[AnalysisApplicationBean]\n"+modelMenuOptions);

    // contents check of modelMenuOptions
    // JEE5 tutorila book provides an example that uses an ArrayList instead of arrary to hold SelectItemGroups
/*
    for (SelectItemGroup sige : modelMenuOptions) {
      out.println("1st element(label)="+sige.getLabel());
      out.println("2nd element(desc) ="+sige.getDescription());
      out.println("4th element(array)="+sige.getSelectItems().length+" items\n");
      for (int j = 0; j< sige.getSelectItems().length; j++){
        out.println("value="+sige.getSelectItems()[j].getValue());
        out.println("label="+sige.getSelectItems()[j].getLabel());
      }
    }
*/


/*
        // store all zelig model specs into List
        zlgList = guiSpec.getModel();
        //out.println( "after: model size: " + zlgList.size());
        log( "after: getModel size: " + zlgList.size());
        
        
        modelCategory = guiSpec.getModelCategory();
        log("\nMap =  guiSpec.getModelCategory()[Model ID => Model Name]\n"+modelCategory);

        modelCategoryList= guiSpec.getModelCategoryList();
        log("\nunique model categories:\n"+modelCategoryList);

        modelId2NameMap=guiSpec.getModelId2NameMap();
        log("\nunique model categories:\n"+modelId2NameMap);
*/
    }

    /** 
     * <p>This method is called when this bean is removed from
     * application scope.  Typically, this occurs as a result of
     * the application being shut down by its owning container.</p>
     * 
     * <p>You may customize this method to clean up resources allocated
     * during the execution of the <code>init()</code> method, or
     * at any later time during the lifetime of the application.</p>
     */
    public void destroy() {
    }
    
    
    /**
     * <p>Return an appropriate character encoding based on the
     * <code>Locale</code> defined for the current JavaServer Faces
     * view.  If no more suitable encoding can be found, return
     * "UTF-8" as a general purpose default.</p>
     *
     * <p>The default implementation uses the implementation from
     * our superclass, <code>AbstractApplicationBean</code>.</p>
     */
    public String getLocaleCharacterEncoding() {
        return super.getLocaleCharacterEncoding();
    }
    
    
    // TODO - add the creation statement of the zelig-config object
    /*
      private object_type XXXXXXX;
    
    */
    
    
    // TODO - add getter for the above the zelig-config object.
    /*
      public object_type getXXXXXX () {
      
        return this.XXXXXXX;
      }
    */
}
