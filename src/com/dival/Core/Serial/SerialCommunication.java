package com.dival.Core.Serial;

import com.SiliconSharks.Controller.ControlSystem;
import com.SiliconSharks.ROVComponents.ROVInfo;
import com.SiliconSharks.ROVComponents.ROVStatus;
import jssc.*;

import java.util.ArrayList;

import static com.SiliconSharks.MainUpdateLoop.Message;
import static com.SiliconSharks.MainUpdateLoop.getStackTrace;
import static com.dival.Core.MainUpdateLoop.Message;

public class SerialCommunication {
    private static boolean Connected;
    private static SerialPort serialPort = null;
    private static ArrayList<String> prevPorts = new ArrayList<>();
    private static String currentPort, successfulPort;
    private static int SendPackageCounter = 0, NotConnectedCounter= 0, NotReceivedCounter = 0, WaitSend = 0;
    private static String[] portNames = new String[]{};
    private static int[] multipliers;
    private static boolean thrustersactive = true;
    public static void setThrustersactive(boolean b){
        thrustersactive = b;
    }
    public static void setFlip(int index, boolean value){
        if(value){
            multipliers[index] = 1;
        }else{
            multipliers[index] = -1;
        }
    }
    public SerialCommunication(){}
    public static void start(){
        Connected = false;
        successfulPort =" ";
        multipliers = new int[Settings.getSetting("NumThrusters")+Settings.getSetting("NumServos")];
        for (int i = 0; i <multipliers.length ; i++) {
            multipliers[i] = 1;
        }
        Message(1,"Successful SerialCommunication Startup!");
    }
    public static void timerRefresh(){
        if (Connected) {
            if(WaitOver()) {
                trySendPackage();
                tryDisconnect();
            }
        } else {
            tryConnect();
        }
    }
    private static boolean WaitOver(){
        if(WaitSend < Settings.getSetting("SerialConnectionPauseDuration") && WaitSend > -1){
            WaitSend++;
        }else if(WaitSend >= Settings.getSetting("SerialConnectionPauseDuration")){
            WaitSend = -1;
        }else {
            return true;
        }
        return false;
    }
    private static void trySendPackage(){
        SendPackageCounter++;
        if (SendPackageCounter >= Settings.getSetting("SerialUpdateRate")) {
            //Message(0, "Serial is currently connected");
            SendPackageCounter = 0;
            //Message(0, "Sending Package...");
            if (sendPackage()) {
                //Message(0, "Package Sent!");
            } else {
                Message(1, "Package Not Sent! Scroll up for Exception Stack Trace");
            }
        }
    }
    private static String hex(int n) {
        return String.format("0x%8s", Integer.toHexString(n)).replace(' ', '0').substring(2);
    }
    private static void writeInt(int n)throws SerialPortException{
        serialPort.writeString(hex(n));
    }
    private static void writeFloat(float f) throws SerialPortException{
        serialPort.writeString(hex(Float.floatToRawIntBits(f)));
    }
    private static boolean sendPackage(){
        if(Connected) {
            try{
                ROVStatus rovStatus = ControlSystem.getCurrentROVStatus(true);
                writeInt(rovStatus.getTimeStamp());
                int c = 0;
                if(thrustersactive) {
                    for (int i = 0; i < Settings.getSetting("NumThrusters"); i++) {
                        writeFloat(multipliers[c] * ((float) rovStatus.getThruster(i)));
                        c++;
                    }
                }else{
                    for (int i = 0; i < Settings.getSetting("NumThrusters"); i++) {
                        writeFloat(0.0f);
                        c++;
                    }
                }
                for(int i = 0; i < Settings.getSetting("NumServos"); i++) {
                    writeFloat(multipliers[c]*((float) rovStatus.getServo(i)));
                    c++;
                }
            }catch(SerialPortException ex){
                Message(0,"Error: Package Send Failed!");
                Message(2,getStackTrace(ex));
                return false;
            }
        }else{
            return false;
        }
        return true;
    }
    private static void tryDisconnect(){
        NotReceivedCounter++;
        if (NotReceivedCounter > Settings.getSetting("SerialDurationBeforeDisconnect")) {
            Message(1,"Long Duration without Telemetry, Attempting Disconnect...");
            if(Disconnect()) {
                Message(0,"Disconnection Successful");
            }else{
                Message(0,"Disconnection Unsuccessful");
            }
        }
    }
    private static void tryConnect(){
        NotConnectedCounter++;
        if (NotConnectedCounter >= Settings.getSetting("SerialConnectionAttemptRate")) {
            Message(0,"Attempting connection...");
            NotConnectedCounter = 0;
            if(AttemptConnection()){
                Message(0,"Connection Successful");
            }else{
                Message(0,"Connection Unsuccessful");
            }
        }
    }
    private static boolean Disconnect(){
        try{
            serialPort.closePort();
            SendPackageCounter = 0;
            NotConnectedCounter = 0;
            NotReceivedCounter = 0;
            WaitSend = 0;
            serialPort = null;
            currentPort = "";
            Connected = false;
            return true;
        }catch(SerialPortException ex){
            Message(1,getStackTrace(ex));
            return false;
        }
    }
    private static boolean AttemptConnection(){
        if(!Connected){
            portNames = SerialPortList.getPortNames();
            if (portNames.length >= 1) {
                if(portNames.length == 1){
                    handleSinglePort();
                }else{
                    if(successfulPort.equals(" ")){
                        handleNoSuccessfulPorts();
                    }else{
                        handleSuccessfulPort();
                    }
                }
                return serialPortInitialization();
            }else{
                Message(0,"Error: No current port detected!");
                return false;
            }
        }else{
            Message(0, "Error: Already Connected!");
            return false;
        }
    }
    private static void handleSinglePort(){
        serialPort = new SerialPort(portNames[0]);
        currentPort = portNames[0];
        Message(0,"One Port Detected, Connecting to Port: " + portNames[0] + "...");
    }
    private static void handleNoSuccessfulPorts(){
        Message(0,"No previous connections successful, selecting new port...");
        for(String port : portNames){
            if(!isInPrevPorts(port)){
                serialPort = new SerialPort(port);
                currentPort = port;
                Message(0,"New port that has not been connected to previously: " + port);
                return;
            }
        }
        selectRandomPort();
    }
    private static boolean isInPrevPorts(String port){
        for(String prevPort : prevPorts){
            if(port.equals(prevPort)){
                return true;
            }
        }
        return false;
    }
    private static void handleSuccessfulPort(){
        for(String port : portNames){
            if(port.equals(successfulPort)){
                Message(0,"Previous Successful Port is available, connecting to: "+successfulPort);
                serialPort = new SerialPort(successfulPort);
                currentPort = successfulPort;
                return;
            }
        }
        selectRandomPort();
    }
    private static void selectRandomPort(){
        int RandomNum = (int) (Math.random()*portNames.length);
        serialPort = new SerialPort(portNames[RandomNum]);
        currentPort = portNames[RandomNum];
        Message(0,"Connecting to random port: "+ portNames[RandomNum]);
    }
    private static boolean serialPortInitialization(){
        try {
            prevPorts.add(currentPort);
            serialPort.openPort();
            serialPort.setParams(
                    Settings.getSetting("SerialBaudRate"),
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(
                    SerialPort.FLOWCONTROL_RTSCTS_IN |
                            SerialPort.FLOWCONTROL_RTSCTS_OUT);
            serialPort.addEventListener(SerialCommunication::handleSerialEvent, SerialPort.MASK_RXCHAR);
            Connected = true;
            SendPackageCounter = 0;
            NotConnectedCounter = 0;
            NotReceivedCounter = 0;
            WaitSend = 0;
            Message(0,"Connection has been Successful");
            return true;
        }catch(SerialPortException ex){
            Message(1,getStackTrace(ex));
            Message(1,"Connection failed! Read Stack Trace for details");
            return false;
        }
    }
    private static int getint()throws SerialPortException{
        String s = serialPort.readHexString(4);
        Long l = Long.valueOf(String.valueOf(s.charAt(9)) + s.charAt(10) + s.charAt(6) + s.charAt(7) + s.charAt(3) + s.charAt(4) + s.charAt(0) + s.charAt(1),16);
        return l.intValue();
    }
    private static float getfloat()throws SerialPortException{
        return Float.intBitsToFloat(getint());
    }
    private static void setupSensor(ROVStatus.Sensor s)throws SerialPortException{
        s.setX(getfloat());
        s.setY(getfloat());
        s.setZ(getfloat());
    }
    private static void handleSerialEvent(SerialPortEvent serialPortEvent){
        if (serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() >= 1) {
            try {
                if(serialPort.getInputBufferBytesCount() >= 68){
                    ROVStatus rovStatus = ROVInfo.update(getint());
                    if(rovStatus == null) return;
                    for(int i = 0; i < 5; i++){
                        rovStatus.setAmperage(i,(((double)getint())-512)/13.476371);
                    }
                    rovStatus.setVoltage(getint());
                    rovStatus.getSystem().setCalibration(getint());
                    rovStatus.getGyro().setCalibration(getint());
                    rovStatus.getAccel().setCalibration(getint());
                    rovStatus.getMagnet().setCalibration(getint());
                    setupSensor(rovStatus.getSystem());
                    setupSensor(rovStatus.getMagnet());
                    setupSensor(rovStatus.getAccel());
                    setupSensor(rovStatus.getGyro());
                    rovStatus.setTemperature(getfloat());
                    NotReceivedCounter = 0;
                    successfulPort = currentPort;
                    rovStatus.setTelemetryUpdated(true);
                }
            } catch (SerialPortException ex) {
                Message(1,"Error in receiving string from COM-port");
                Message(1,getStackTrace(ex));
            }
        }
    }
    public static boolean isConnected() {
        return Connected;
    }
}