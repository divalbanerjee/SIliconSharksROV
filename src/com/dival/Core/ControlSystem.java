package com.dival.Core;

import com.SiliconSharks.Settings;
import net.java.games.input.*;

import java.lang.reflect.Constructor;

import static com.SiliconSharks.MainUpdateLoop.*;

@SuppressWarnings("unchecked")

public class ControlSystem {
    private static int KeyboardRefreshCounter = 0;
    private static Gamepad[] gamepads;
    public ControlSystem(){}
    public static void start(){
        currentROVStatus = new ROVStatus(getGlobalTimeStamp());
        CustomKeyboard.start();
        switch (Settings.getSetting("NumGamepad")){
            case 0:{
                // GUI and Keyboard testing only, no gamepads expected;
                gamepads = new Gamepad[0];
                break;
            }case 1:{
                // One controller expected, testing purposes
                gamepads = new Gamepad[]{new Gamepad(0)};
                break;
            }case 2:{
                // Practice and Competition use, 2 controllers
                gamepads = new Gamepad[]{new Gamepad(0), new Gamepad(0)};
                break;
            }
            default:{
                Message(1,"Unrecognized NumGamepad Setting: " + Integer.toString(Settings.getSetting("NumGamepad")));
            }
        }
        Message(1,"Successful ControlSystem Startup!");
    }
    private static ROVStatus currentROVStatus;
    public static Gamepad getGamepad(int index) {return gamepads[index];}
    public static void timerRefresh(){
        ROVStatus newROVStatus = new ROVStatus(getGlobalTimeStamp());
        KeyboardRefreshCounter++;
        if(KeyboardRefreshCounter >= Settings.getSetting("KeyboardUpdateRate")){
            CustomKeyboard.TimerRefresh();
            KeyboardRefreshCounter = 0;
        }
        boolean GamepadConnection = false;
        for(Gamepad gamepad: gamepads) {
            if (gamepad.isConnected()) {
                if (!gamepad.pollController()) {
                    gamepad.setController(null);
                    Message(1, "Error polling controller, disconnecting...");
                } else {
                    gamepad.update(newROVStatus,currentROVStatus);
                    GamepadConnection = true;
                }
            } else {
                int ConnectionCounter = gamepad.getConnectionCounter();
                if (ConnectionCounter <= Settings.getSetting("GamepadConnectionAttemptRate")*Settings.getSetting("NumGamepadConnectionAttempts")) {
                    if (ConnectionCounter % Settings.getSetting("GamepadConnectionAttemptRate") == 0) {
                        AttemptConnection(gamepad);
                    }
                }
            }
        }
        if(GamepadConnection){
            if(Settings.getSettingB("KeyboardEnabled") && Settings.getSettingB("KeyboardEnabledWhileGamepadConnected")) CustomKeyboard.update(newROVStatus,currentROVStatus);
        }else{
            if(Settings.getSettingB("KeyboardEnabled")) CustomKeyboard.update(newROVStatus,currentROVStatus);
        }
        currentROVStatus = newROVStatus;
    }
    private static void AttemptConnection(Gamepad gamepad){
        try{
            Message(0,"Attempting connection...");
            ControllerEnvironment controllerEnvironment = createDefaultEnvironment();
            Controller[] controllers = controllerEnvironment.getControllers();
            Message(0,"Found " + controllers.length + " controllers, scanning...");
            for(Controller controller : controllers){
                if(controller.getType() == Controller.Type.GAMEPAD){
                    for(Gamepad gamepad1 : gamepads){
                        if(gamepad1.isConnected() && controller.getName().equals(gamepad1.getController().getName()) &&
                                controller.getPortNumber()==gamepad1.getController().getPortNumber()){
                            return;
                        }
                    }
                    Message(1,"Found gamepad: "+ controller.getName());
                    gamepad.setController(controller);
                    break;
                }
            }
        }catch(ReflectiveOperationException ex){
            Message(2,getStackTrace(ex));
        }
    }
    private static ControllerEnvironment createDefaultEnvironment() throws ReflectiveOperationException {

        // Find constructor (class is package private, so we can't access it directly)
        Constructor<ControllerEnvironment> constructor = (Constructor<ControllerEnvironment>)
                Class.forName("net.java.games.input.DefaultControllerEnvironment").getDeclaredConstructors()[0];

        // Constructor is package private, so we have to deactivate access control checks
        constructor.setAccessible(true);

        // Create object with default constructor
        return constructor.newInstance();
    }
    public static ROVStatus getCurrentROVStatus(boolean ROVInfoInsert) {
        //DO NOT CALL THIS METHOD OUTSIDE OF SERIALCOMMUNICATION
        if(ROVInfoInsert){
            ROVInfo.insert(currentROVStatus);
        }
        return currentROVStatus;
    }
}
