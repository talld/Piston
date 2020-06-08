package Engine.Renderer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Pointer;
import org.lwjgl.vulkan.*;
import org.lwjgl.glfw.*;
import org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

public class RenderUtil {

    private static VkInstance Instance;

    public static void cls(){
    }

    public static void initRender(){

    }

    public static void VKInit(){

        VkApplicationInfo appInfo = VkApplicationInfo.calloc();                  //define app info
        appInfo.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO);                       //auto-generate structure info
        appInfo.applicationVersion(VK_MAKE_VERSION(1,0,0)); //auto generate version info
        appInfo.engineVersion(VK_MAKE_VERSION(1,0,0));      //^
        appInfo.apiVersion(VK_API_VERSION_1_0);                                 //default Vulkan version


        VkInstanceCreateInfo cInfo = VkInstanceCreateInfo.calloc();            //define Creation info
        cInfo.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);                   //auto-generate structure info
        cInfo.pApplicationInfo(appInfo);                                       //parse app info

        glfwGetRequiredInstanceExtensions();                                   //find required extensions
        cInfo.enabledExtensionCount();                                         //count them
        cInfo.ppEnabledExtensionNames();                                       //enable them

        cInfo.enabledLayerCount();                                             //enable layers
        PointerBuffer pBInstance = memAllocPointer(1);                    //allocate memory for Vulkan instance
        int  check = VK10.vkCreateInstance(cInfo,null,pBInstance);   //create instance with check int
        long pInstance = pBInstance.get(0);                                    //gen the position of newly created instance
        if(check != VK_SUCCESS) {                                              //check for fail
            System.err.println("VKINIT failed");                               //error out
        }else{
            Instance = new VkInstance(pInstance,cInfo);                       //assign instance;
        }
    }

    public static VkInstance getInstance() {
        return Instance;
    }
}
