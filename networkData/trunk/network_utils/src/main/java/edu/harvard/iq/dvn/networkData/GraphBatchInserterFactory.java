package edu.harvard.iq.dvn.networkData;

import java.sql.SQLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;


public class GraphBatchInserterFactory{
    private RestrictedURLClassLoader rucl;
    public GraphBatchInserterFactory(String libPath) throws IOException{
        File libDir = new File(libPath);
        URL[] urls = (URL[])FileUtils.toURLs(
                        (File[])FileUtils.listFiles(
                            libDir, new String[]{"jar"}, false).
                            toArray(new File[0]));

        rucl = new RestrictedURLClassLoader(urls,
                    GraphBatchInserterFactory.class.getClassLoader());
    }

    public GraphBatchInserter newInstance(String neoDb, String sqlDb, String insertProps, String neoProps) throws ClassNotFoundException{
        Class gbiimplclass = Class.forName("edu.harvard.iq.dvn.networkData.GraphBatchInserterImpl", true, rucl);
        try{
            return (GraphBatchInserter)gbiimplclass.getConstructors()[0].
                        newInstance(neoDb, sqlDb, insertProps, neoProps);
        } catch(Exception e){
            System.out.println(e);
            return null;
        }
    }
}

