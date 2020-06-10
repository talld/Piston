package Game;

import Engine.Geometry.Vertex;
import Engine.IO.Input;
import Engine.math.Vector3f;
import org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.glfw.GLFW.*;

public class Game {


    public Game(){

    }

    public void input(){

    }

    public void update(){
        if(Input.checkKeyReleased(GLFW_KEY_E)){
            System.out.println("e released");
        }
    }

    public void render(){

    }

}
