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

import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.data.*;



import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.logging.*;
import javax.imageio.IIOException;
import static java.lang.System.*;
import java.util.*;

/**
 *
 * @author asone
 */
public class PORFileReader extends StatDataFileReader{


   private static Logger dbgLog =
       Logger.getLogger(PORFileReader.class.getPackage().getName());

   /**
    * 
    * @param originator
    */
   public PORFileReader(StatDataFileReaderSpi originator){
        super(originator);
    }

    public SDIOData read(BufferedInputStream stream) throws IOException{
        dbgLog.fine("PORFileReader: read() is called");
        SDIOMetadata metadata = readHeader(stream);
        readMetadata(stream, metadata);
        SDIOData sd = readData(stream, metadata);

        return sd;
    }
    /**
     *
     * @param stream
     * @return
     */
    public SDIOMetadata readHeader(BufferedInputStream stream){
        SDIOMetadata smd = new PORMetadata();

        return smd;
    }

    /**
     *
     * @param stream
     * @param metadata
     */
    public void readMetadata(BufferedInputStream stream, SDIOMetadata metadata){

	}

    /**
     *
     * @param stream
     * @param metadata
     * @return
     */
    public SDIOData readData(BufferedInputStream stream, SDIOMetadata metadata){
        SDIOData sd = new SDIOData();

        return sd;
    }
}
