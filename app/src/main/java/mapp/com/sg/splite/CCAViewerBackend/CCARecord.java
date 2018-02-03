package mapp.com.sg.splite.CCAViewerBackend;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by samue on 28/11/2017.
 */

public class CCARecord implements Serializable {
    private int totalpoints;
    private CCAGrade ccagrade;
    private ArrayList<CCAComponent> CCAComponents;

    public CCARecord()
    {

    }

    public CCARecord(int points, CCAGrade ccagrade, ArrayList<CCAComponent> components) {
        this.totalpoints = points;
        this.ccagrade = ccagrade;
        this.CCAComponents = components;
    }

    public int getTotalpoints() {
        return totalpoints;
    }

    public void setTotalpoints(int totalpoints) {
        this.totalpoints = totalpoints;
    }

    public CCAGrade getCcagrade() {
        return ccagrade;
    }

    public void setCcagrade(CCAGrade ccagrade) {
        this.ccagrade = ccagrade;
    }

    public ArrayList<CCAComponent> getCCAComponents() {
        return CCAComponents;
    }

    public void setCCAComponents(ArrayList<CCAComponent> CCAComponents) {
        this.CCAComponents = CCAComponents;
    }
}
