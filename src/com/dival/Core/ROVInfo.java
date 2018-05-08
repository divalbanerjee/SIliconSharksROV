package com.dival.Core;

import com.sun.scenario.Settings;

public class ROVInfo {
    private static ROVStatus[] rovStatuses;
    private static int numItems;
    public ROVInfo(){}
    public static void start(){
       // numItems = Settings.getSetting("NumROVStatusSaved");
        rovStatuses = new ROVStatus[numItems];
        for(int i = 0; i < numItems; i++){
            rovStatuses[i] = new ROVStatus(-1);
        }
    }
    public static void insert(ROVStatus rovStatus){
        for (int i = 0; i < numItems-1; i++) {
            rovStatuses[i] = rovStatuses[i+1];
        }
        rovStatuses[numItems-1] = rovStatus;
    }
    public static ROVStatus[] getRovStatuses() {
        return rovStatuses;
    }
    public static ROVStatus update(int TimeStamp){
        //StringBuilder s = new StringBuilder();
        for (int i = rovStatuses.length-1; i >= 0; i--) {
            ROVStatus rovStatus = rovStatuses[i];
            //s.append(rovStatus.getTimeStamp()).append(' ');
            if (!rovStatus.isTelemetryUpdated() && rovStatus.getTimeStamp() == TimeStamp) {
                return rovStatus;
            }
        }
        //Message(2,"ROVInfo 37: "+Integer.toString(TimeStamp) +", "  + Integer.toString(getGlobalTimeStamp()) +", "+ s.toString());
        return null;
    }
    public static ROVStatus getMostRecentSentStatus(){
        return rovStatuses[numItems-1];
    }
    public static ROVStatus getMostRecentTelemetry(){
        if(rovStatuses == null) return new ROVStatus(-2);
        for (int i = rovStatuses.length-1; i >= 0; i--) {
            ROVStatus rovStatus = rovStatuses[i];
            if (rovStatus.isTelemetryUpdated()) {
                if(rovStatus.getTimeStamp() < 0){ //MainUpdateLoop.getGlobalTimeStamp()-1000){
                    return new ROVStatus(-2);
                }
                return rovStatus;
            }
        }
        return new ROVStatus(-2);
    }
    public static String getStatus(){
        ROVStatus avgStatus = new ROVStatus(-3);
        int counter = 0;
        for (int i = numItems-1; i >= 0 ; i--) {
            if(rovStatuses[i].getTimeStamp() == -1) break;
            if(rovStatuses[i].isTelemetryUpdated()){
                counter++;
                avgStatus.Sum(rovStatuses[i]);
                if(counter >= 5) break;
            }
        }
        if(counter == 0){
            ROVStatus rovStatus = new ROVStatus(-1);
            return rovStatus.getString("Avg0");
        }
        avgStatus.Scale(1.0/(double)counter);
        return avgStatus.getString("Avg" + String.valueOf(counter));
    }
}
