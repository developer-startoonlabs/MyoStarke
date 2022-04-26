package com.start.apps.pheezee.classes;


import org.json.JSONArray;

public class PatientActivitySingleton {
    private static PatientActivitySingleton INSTANCE = null;
    public static String patientid=null;
    public static String patientname=null;
    public static String phizioemail=null;
    public static JSONArray ActivityList=null;

    // other instance variables can be here

    private PatientActivitySingleton() {};


    public static PatientActivitySingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PatientActivitySingleton();
        }
        return(INSTANCE);
    }

    public void setPatientDetails(String patientid,String patientname,String phizioemail,JSONArray ActivityList) {

        if(patientid==null && patientname==null && phizioemail==null && ActivityList==null)
        {
            this.patientid = patientid;
            this.patientname = patientname;
            this.phizioemail = phizioemail;
            this.ActivityList = ActivityList;
            INSTANCE = null;

        }else {

            if (this.patientid == null) {

                this.patientid = patientid;
                this.patientname = patientname;
                this.phizioemail = phizioemail;
                this.ActivityList = ActivityList;

                return;
            }else if(this.patientid.equalsIgnoreCase(patientid) == true)
            {
                this.patientid = patientid;
                this.patientname = patientname;
                this.phizioemail = phizioemail;

                JSONArray result = new JSONArray();

                try {

                    for (int i = 0; i < this.ActivityList.length(); i++) {
                        result.put(this.ActivityList.get(i));
                    }
                    for (int i = 0; i < ActivityList.length(); i++) {
                        result.put(ActivityList.get(i));
                    }
                } catch (Throwable e) {

                }
                this.ActivityList = result;
                return;
            }else if(this.patientid.equalsIgnoreCase(patientid) == false)
            {

                this.patientid = patientid;
                this.patientname = patientname;
                this.phizioemail = phizioemail;

                this.ActivityList = ActivityList;

            }

        }

    }

    public String getpatientname()
    {
        return this.patientname;
    }

    public String getpatientid()
    {
        return this.patientid;
    }

    public String getphizioemail()
    {
        return this.phizioemail;
    }

    public JSONArray getactivitylist()
    {
        return this.ActivityList;
    }

    public void PopElement()
    {
        if( INSTANCE!= null && this.ActivityList != null) {
            if(this.ActivityList.length() > 0) {
                this.ActivityList.remove(this.ActivityList.length() - 1);
            }
        }
    }

}
