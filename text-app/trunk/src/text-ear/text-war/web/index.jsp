<%-- 
    Document   : index
    Created on : Oct 15, 2010, 11:11:43 AM
    Author     : ekraffmiller
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <title>OpenLayers Graticule Example</title>

        <link rel="stylesheet" href="../theme/default/style.css" type="text/css" />
        <link rel="stylesheet" href="style.css" type="text/css" />
        <style type="text/css">
            #map {
                width: 600px;
                height: 300px;
                border: 1px solid black;
                float:left;
            }
            #map2 {
                width: 400px;
                height: 400px;
                border: 1px solid black;
                float:left;
            }
        </style>
        <script src="http://openlayers.org/api/OpenLayers.js"></script>

        <script src="http://proj4js.org/lib/proj4js-compressed.js"></script>
        <script type="text/javascript">
            Proj4js.defs["EPSG:42304"]="+title=Atlas of Canada, LCC +proj=lcc +lat_1=49 +lat_2=77 +lat_0=49 +lon_0=-95 +x_0=0 +y_0=0 +ellps=GRS80 +datum=NAD83 +units=m +no_defs";

            var graticuleCtl1, graticuleCtl2;
            var map1, map2;
            function init(){
              initLonLat();
              initProjected();
            }
            function initLonLat(){
                graticuleCtl1 = new OpenLayers.Control.Graticule({
                    numPoints: 2,
                    labelled: false,
                    tartetSize: 200
                });
                map1 = new OpenLayers.Map('map', {
                    controls: [
                      graticuleCtl1,
                      new OpenLayers.Control.LayerSwitcher(),
                      new OpenLayers.Control.PanZoomBar(),
                      new OpenLayers.Control.Navigation()
                      ]
                });

                var ol_wms = new OpenLayers.Layer.WMS( "OpenLayers WMS",
                    "http://vmap0.tiles.osgeo.org/wms/vmap0",
                    {layers: 'basic'}, {wrapDateLine: true} );

                map1.addLayers([ol_wms]);
                if (!map1.getCenter()) map1.zoomToMaxExtent();
            };

            function initProjected(){
                var extent = new OpenLayers.Bounds(-2200000,-712631,3072800,3840000);
                graticuleCtl2 = new OpenLayers.Control.Graticule({
                    labelled: true,
                    targetSize: 200
                });
                var mapOptions = {
                      controls: [
                        graticuleCtl2,
                        new OpenLayers.Control.LayerSwitcher(),
                        new OpenLayers.Control.PanZoomBar(),
                        new OpenLayers.Control.Navigation()
                      ],
                      //scales: tempScales,
                      maxExtent: extent,
                      maxResolution: 50000,
                      units: 'm',
                      projection: 'EPSG:42304'
                };
                map2 = new OpenLayers.Map('map2', mapOptions);

                var dm_wms = new OpenLayers.Layer.WMS( "DM Solutions Demo",
                  "http://www2.dmsolutions.ca/cgi-bin/mswms_gmap", {
                     layers: "bathymetry",
                     format: "image/png"
                  },{
                      singleTile: true
                  });

                map2.addLayers([dm_wms]);
                if (!map2.getCenter()) map2.zoomToExtent(extent);
            }
        </script>
    </head>
    <body onload="init()">
        <h1 id="title">Graticule Example</h1>

        <div id="tags">
          graticule, grid
        </div>

        <p id="shortdesc">
            Adds a Graticule control to the map to display a grid of
            latitude and longitude.
        </p>

        <div id="map" class="smallmap"></div>
        <div id="map2" class="smallmap"></div>

        <div id="docs"></div>
        <br style="clear:both" />
        <ul>

            <li><a href="#"
                onclick="graticuleCtl1.activate(); graticuleCtl2.activate(); return false;">Activate graticule controls</a></li>
            <li><a href="#"
                onclick="graticuleCtl1.deactivate(); graticuleCtl2.deactivate(); return false;">Deactivate graticule controls</a></li>
        </ul>
    </body>

</html>
