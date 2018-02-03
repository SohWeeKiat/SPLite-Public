package mapp.com.sg.splite.MyATSBackEnd;

import java.io.Serializable;

/**
 * Created by Wee Kiat on 13/11/2017.
 */

public class AtsResult implements Serializable{

    private String studentID;
    private String submissionDateTime;
    private String state;
    private String atsClass;
    private String lessonTime;
    private String atsCode;

    public AtsResult()
    {
    }

    public AtsResult(String studentID, String state, String atsClass, String lessonTime, String atsCode) {
        this.studentID = studentID;
        this.state = state;
        this.atsClass = atsClass;
        this.lessonTime = lessonTime;
        this.atsCode = atsCode;
    }

    public String getStudentID()
    {
        return studentID;
    }

    public void setStudentID(String studentID)
    {
        this.studentID = studentID;
    }

    public String getSubmissionDateTime()
    {
        return submissionDateTime;
    }

    public void setSubmissionDateTime(String submissionDateTime)
    {
        this.submissionDateTime = submissionDateTime;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getAtsClass()
    {
        return atsClass;
    }

    public void setAtsClass(String atsClass)
    {
        this.atsClass = atsClass;
    }

    public String getLessonTime()
    {
        return lessonTime;
    }

    public void setLessonTime(String lessonTime)
    {
        this.lessonTime = lessonTime;
    }

    public String getAtsCode()
    {
        return atsCode;
    }

    public void setAtsCode(String atsCode)
    {
        this.atsCode = atsCode;
    }
}
