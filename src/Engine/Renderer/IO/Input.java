package Engine.Renderer.IO;

import Engine.Renderer.Renderer;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class Input {

    private static Set<Integer> priorPressedKeys = new HashSet<>();
    private static Set<Integer> pressedKeys = new HashSet<>();

    private static Set<Integer> heldKeys = new HashSet<>();
    private static Set<Integer> releasedKeys = new HashSet<>();

    public static void init(){
        glfwSetKeyCallback(Renderer.getWindow().getPointer(), (window, key, scancode, action, mods) -> {
            pressedKeys.add(key);
        });
    }

    public static void update() {
        releasedKeys.clear();



        priorPressedKeys.addAll(pressedKeys);
        pressedKeys.clear();
    }
}
