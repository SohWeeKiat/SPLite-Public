package mapp.com.sg.splite.CCAViewerBackend;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import mapp.com.sg.splite.Util.XMLParser;

/**
 * Created by samue on 28/11/2017.
 */

public class CCAParser extends XMLParser {
    public CCARecord parse(InputStream in) throws XmlPullParserException,IOException
    {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in,null);
            parser.next();
            return readCCA(parser);
        } finally {
            in.close();
        }
    }

    private CCARecord readCCA(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        CCARecord ccaRecord = new CCARecord();
        parser.require(XmlPullParser.START_TAG, ns, "result");
        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (name.equals("total-points")) {
                    parser.next();
                    int totalpoints = Integer.parseInt(parser.getText());
                    ccaRecord.setTotalpoints(totalpoints);
                    parser.nextTag();

            }
            else if (name.equals("grade")) {
                    parser.next();
                    String grade = parser.getText();
                    switch (grade) {
                        case "BRONZE": ccaRecord.setCcagrade(CCAGrade.BRONZE); break;
                        case "SILVER": ccaRecord.setCcagrade(CCAGrade.SILVER);break;
                        case "GOLD":ccaRecord.setCcagrade(CCAGrade.GOLD);break;
                        case "HONOURS": ccaRecord.setCcagrade(CCAGrade.HONOURS); break;
                        default : ccaRecord.setCcagrade(CCAGrade.NO_GRADE); break;
                    }
                    parser.nextTag();

            }
            else if (name.equals("components")) {
                ccaRecord.setCCAComponents(readComponents(parser));
            }
            else {
                skip(parser);
            }
        }
        return ccaRecord;
    }

    private ArrayList<CCAComponent> readComponents(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        ArrayList<CCAComponent> ccaComponents = new ArrayList<CCAComponent>();
        ArrayList<String> componentnames = new ArrayList<>(Arrays.asList("leadership", "participation", "service", "enrichment",
                "representation", "community-service", "achievement-new", "competition"));
        parser.require(XmlPullParser.START_TAG, ns, "components");
        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (componentnames.contains(name)) {
                ccaComponents.add(readCCAComponent(parser,name));
            }
            else {
                skip(parser);
            }

        }
       return ccaComponents;

    }
    private CCAComponent readCCAComponent(XmlPullParser parser, String Cname) throws XmlPullParserException, IOException {
        CCAComponent ccaComponent = new CCAComponent();
        ArrayList<CCAActivity> ccaActivities = new ArrayList<CCAActivity>();
        ccaComponent.setName(Cname);
        while (parser.next() == XmlPullParser.START_TAG && !parser.getName().equals("points-component")) {
            ccaActivities.add(readActivity(parser));
        }
        // at <points-component>
        parser.next();
        ccaComponent.setTotalpoint(Integer.parseInt(parser.getText()));
        ccaComponent.setCcaActivtyArrayList(ccaActivities);

        parser.nextTag(); // </points-component>
        parser.nextTag(); // </end of component>
        return ccaComponent;
    }

    private CCAActivity readActivity(XmlPullParser parser) throws XmlPullParserException, IOException {
        CCAActivity ccaActivity = new CCAActivity();
        // handles first activity of each component, first activity read starts at <activity>
        if (parser.getName().equals("activity")) {
            parser.next();
            // handles <activity /> tag
            if (parser.getText() == "" || parser.getText() == null)
                return null;
            ccaActivity.setName(parser.getText());
            parser.nextTag(); // </activity>
        }
        loop: while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();

            switch (name) {
                case "activity" : parser.next(); ccaActivity.setName(parser.getText()); parser.nextTag(); break;
                case "rolename" : parser.next();ccaActivity.setRolename(parser.getText()); parser.nextTag(); break;
                case "start-date" : parser.next(); ccaActivity.setStartdate(parser.getText()); parser.nextTag(); break;
                case "end-date" : parser.next();ccaActivity.setEnddate(parser.getText()); parser.nextTag(); break;
                case "points-activity" : parser.next();ccaActivity.setPoints(Integer.parseInt(parser.getText()));
                    parser.nextTag(); break loop;

            }

        }
        return ccaActivity;
    }
}

