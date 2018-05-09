package com.dival.Core;

import net.java.games.input.*;

import static com.SiliconSharks.MainUpdateLoop.Message;
import static com.SiliconSharks.MainUpdateLoop.getStackTrace;

public class Gamepad {
    //This is a Logitech F310 implementation, edit the # of specific components and their identifiers when you plug them in for other controllers
    private final int NumComponents = 9;
    private Component.Identifier[] identifiers = {Component.Identifier.Button.A,
                                                  Component.Identifier.Button.B,
                                                  Component.Identifier.Button.X,
                                                  Component.Identifier.Button.Y,
                                                  Component.Identifier.Axis.POV,
                                                  Component.Identifier.Axis.X,
                                                  Component.Identifier.Axis.Y,
                                                  Component.Identifier.Axis.RX,
                                                  Component.Identifier.Axis.RY};
    private double[] values = new double[9];
    private int type, ConnectionCounter;
    private Controller controller;
    Gamepad(int type) {
        // type is used to differentiate between different controller input in update()
        this.type = type;
        ConnectionCounter = 0;
        for(int i = 0; i < NumComponents; i++) values[i] = 0;
    }
    Controller getController(){return controller;}
    int getConnectionCounter(){
        // used in ControlSystem class to allow constant connection attempts
        ConnectionCounter++;
        return ConnectionCounter;
    }
    void setController(Controller controller){
        // sets controller
        this.controller=controller;
        ConnectionCounter = 0;
    }
    public void resetConnectionCounter(){
        // Resets Connection Counter to continue trying to get a connection (based on user input)
        ConnectionCounter = 0;
    }
    boolean pollController(){
        if(isConnected()){
            try {
                controller.poll();
            } catch (Exception ex) {
                Message(1,"Trouble Polling Controller: Possible Disconnection");
                Message(1,getStackTrace(ex));
                return false;
            }
            Component.Identifier eventIdentifier;
            EventQueue queue = controller.getEventQueue();
            Event event = new Event();
            while (queue.getNextEvent(event)) {
                StringBuilder buffer = new StringBuilder(controller.getName());
                buffer.append(" at ");
                buffer.append(event.getNanos()).append(", ");
                Component comp = event.getComponent();
                float value = event.getValue();
                buffer.append(comp.getName()).append(" changed to ").append(value);
                eventIdentifier = event.getComponent().getIdentifier();
                for(int i = 0; i < NumComponents; i++){
                    Message(1,eventIdentifier.getName());
                    if(identifiers[i] == eventIdentifier){
                        values[i] = value;
                        break;
                    }
                }
                //Message(0,buffer.toString());
            }
            return true;
        }else{
            return true;
        }
    }
    public boolean getButton(char c){
        switch (c){
            case 'A': return values[0] > 0.5;
            case 'B': return values[1] > 0.5;
            case 'X': return values[2] > 0.5;
            case 'Y': return values[3] > 0.5;
        }
        Message(1,"Error: Unhandled getButton() call in Gamepad.java");
        return false;
    }
    public double getAxis(String s){
        switch(s){
            case "LX": return values[5];
            case "LY": return values[6];
            case "RX": return values[7];
            case "RY": return values[8];
        }
        Message(1,"Error: Unhandled getAxis() call in Gamepad.java");
        return 0;
    }
    public String getDPad(){
        String s;
        if(values[4] == 0.125 || values[4] == 1 || values[4] == 0.875) s = "L";
        else if(values[4] == 0.25 || values[4] == 0 || values[4] == 0.75) s = "M";
        else s = "R";
        if(values[4] == 0.125 || values[4] == 0.25 || values[4] == 0.375) s += "U";
        else if(values[4] == 1 || values[4] == 0 || values[4] == 0.5) s += "M";
        else s = "D";
        return s;
    }
    void update(ROVStatus rovStatus, ROVStatus prevROVStatus){
        switch (type){
            case 0: rovStatus.setThruster(0,getAxis("LY"));
                    rovStatus.setThruster(1,getAxis("LY"));
                    rovStatus.setThruster(2,getAxis("RY"));
                    rovStatus.setThruster(0,rovStatus.getThruster(0)+getAxis("LX"));
                    rovStatus.setThruster(1,rovStatus.getThruster(1)-getAxis("LX"));
                    int nServos = 0;
                    String DPad = getDPad();
                    if(DPad.charAt(0) == 'L'){
                        nServos++;
                        rovStatus.setServo(0,rovStatus.getServo(0)-0.01);
                    }else if(DPad.charAt(0) == 'R') {
                        nServos++;
                        rovStatus.setServo(0, rovStatus.getServo(0) + 0.01);
                    }
                    if(getButton('A') && !getButton('Y')){
                        nServos++;
                        rovStatus.setServo(1,1);
                    }
                    if(getButton('Y') && !getButton('A')){
                        nServos++;
                        rovStatus.setServo(1,-1);
                    }
                    rovStatus.calibrate(nServos);
                    break;
            case 1:
        }
    }
    public boolean isConnected(){return (controller != null);}
}
