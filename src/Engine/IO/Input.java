package Engine.IO;

import Engine.Piston;
import Engine.Renderer.VRenderer;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class Input {

    private static Set<Integer> priorPressedKeys = new HashSet<>();
    private static Set<Integer> pressedKeys = new HashSet<>();

    private static Set<Integer> heldKeys = new HashSet<>();
    private static Set<Integer> releasedKeys = new HashSet<>();

    public static void init(){
        glfwSetKeyCallback(VRenderer.getWindow().getPointer(), (window, key, scancode, action, mods) -> {
            pressedKeys.add(key);
        });
    }

    public static void update() {
        releasedKeys.clear();



        priorPressedKeys.addAll(pressedKeys);
        pressedKeys.clear();
    }
}
