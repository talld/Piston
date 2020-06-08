package Engine.IO;

import Engine.Renderer.Window;
import Engine.math.Vector2f;
import org.lwjgl.BufferUtils;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class Input {

    /*basic implementation

    creates arrays of all the down/up keys with method to check single keys for each

    TODO: replace with more efficient code
    TODO: TIDY UP
     */

    private static final int NOKEYCODES = 256;
    private static final int NOMOUSECODES = 16;
    private static ArrayList<Integer> currentDownKeys = new ArrayList<>();
    private static ArrayList<Integer> currentUpKeys = new ArrayList<>();

    private static ArrayList<Integer> priorKeys = new ArrayList<>();
    private static ArrayList<Integer> currentKeys = new ArrayList<>();

    private static ArrayList<Integer> pressedKeys = new ArrayList<>();
    private static ArrayList<Integer> releasedKeys = new ArrayList<>();

    private static ArrayList<Integer> downMouse = new ArrayList<>();
    private static ArrayList<Integer> upMouse = new ArrayList<>();

    private static ArrayList<Integer> priorMouse = new ArrayList<>();
    private static ArrayList<Integer> currentMouse = new ArrayList<>();

    private static ArrayList<Integer> pressedMouse = new ArrayList<>();
    private static ArrayList<Integer> releasedMouse = new ArrayList<>();

    public static void update(){

        currentDownKeys.clear();
        currentUpKeys.clear();
        pressedKeys.clear();
        releasedKeys.clear();

        for(int i = 0;i<NOKEYCODES;i++){
            if(getKey(i)==1){
                currentDownKeys.add(i);
            }
        }

        for(int i = 0;i<NOKEYCODES;i++){
            if(getKey(i)==0){
                currentUpKeys.add(i);
            }
        }

        for(Integer key : currentDownKeys){
            if(!currentKeys.contains(key))
            currentKeys.add(key);
        }

        for(Integer key : currentUpKeys){
            currentKeys.remove(key);
        }

        for(Integer key : currentKeys){
            if(!priorKeys.contains(key)){
                pressedKeys.add(key);
            }
        }

        for(Integer key : priorKeys){
            if(!currentKeys.contains(key)){
                releasedKeys.add(key);
            }
        }

        priorKeys.clear();
        priorKeys.addAll(currentKeys);

        downMouse.clear();
        upMouse.clear();
        pressedMouse.clear();
        releasedMouse.clear();

        for(int i = 0;i<NOMOUSECODES;i++){
            if(getMouse(i)==1){
                downMouse.add(i);
            }
        }

        for(int i = 0;i<NOMOUSECODES;i++){
            if(getMouse(i)==0){
                upMouse.add(i);
            }
        }

        for(Integer key : downMouse){
            if(!currentMouse.contains(key))
                currentMouse.add(key);
        }

        for(Integer key : upMouse){
            currentMouse.remove(key);
        }

        for(Integer key : currentMouse){
            if(!priorMouse.contains(key)){
                pressedMouse.add(key);
            }
        }

        for(Integer key : priorMouse){
            if(!currentMouse.contains(key)){
                releasedMouse.add(key);
            }
        }

        priorMouse.clear();
        priorMouse.addAll(currentMouse);

    }

    public static int getKey(int keyCode){
        return glfwGetKey(Window.getContex(),keyCode);
    }

    public static boolean checkKeyDown(int keyCode){
        return (currentDownKeys.contains(keyCode));
    }

    public static boolean checkKeyUp(int keyCode){
        return (currentUpKeys.contains(keyCode));
    }

    public static boolean checkKey(int keyCode){
        return (currentKeys.contains(keyCode));
    }

    public static boolean checkKeyPressed(int keyCode){
        return (pressedKeys.contains(keyCode));
    }

    public static boolean checkKeyReleased(int keyCode){
        return (releasedKeys.contains(keyCode));
    }

    public static int getMouse(int mouseCode){
        return (glfwGetMouseButton(Window.getContex(),mouseCode));
    }

    public static boolean checkMousePressed(int keyCode){
        return (pressedMouse.contains(keyCode));
    }

    public static boolean checkMouseReleased(int keyCode){
        return (releasedMouse.contains(keyCode));
    }

    public static boolean checkMouseDown(int keyCode){ return (downMouse.contains(keyCode)); }

    public static boolean checkMouseUp(int keyCode){
        return (upMouse.contains(keyCode));
    }

    public static boolean checkMouse(int keyCode){
        return (currentMouse.contains(keyCode));
    }

    public static Vector2f getMousePos(){
        Vector2f mousePos = new Vector2f();
        DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(Window.getContex(),posX,posY);
        mousePos.setX((float) posX.get(0));
        mousePos.setY((float)posY.get(0));
        return mousePos;
    }

}
