package mapp.com.sg.splite.CrowdWatchBackEnd;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import mapp.com.sg.splite.Util.XMLParser;

/**
 * Created by Wee Kiat on 9/11/2017.
 */

public class FCHeatMapParser extends XMLParser{

    public Map parse(InputStream in) throws XmlPullParserException,IOException
    {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in,null);
            parser.next();
            parser.next();
            return readFC(parser);
        } finally {
            in.close();
        }
    }

    private Map readFC(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        Map entries = new HashMap<>();

        parser.require(XmlPullParser.START_TAG, ns, "fc");
        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            if (name.equals("location")) {
                CrowdWatchLocation cwl = readLocation(parser);
                entries.put(cwl.getName(),cwl);
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private CrowdWatchLocation readLocation(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, ns, "location");

        String type = parser.getAttributeValue(null,"type");
        String name = parser.getAttributeValue(null,"name");
        int heatMapValue = Integer.parseInt(parser.getAttributeValue(null,"heatMapValue"));
        String location = parser.getAttributeValue(null,"location");

        for(int next_type = parser.next();;next_type = parser.next()){
            if (next_type == XmlPullParser.END_TAG && parser.getName() != null
                    && parser.getName().equals("location")){
                break;
            }
        }
        return new CrowdWatchLocation(type, name, heatMapValue,location);
    }
}
