package Engine.Renderer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.Version;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCreateInfoEXT;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import static Engine.Renderer.VValidationLayers.populateDebugMessengerCreateInfo;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK10.*;

public class VInstance {

    VkInstance instance;

    public VInstance(){

    }

    public VkInstance create(PointerBuffer valdiationLayers) {

        if(VValidationLayers.ENABLE_VALIDATION_LAYERS && !VValidationLayers.checkValidationLayerSupport()) {
            throw new RuntimeException("Validation requested but not supported");
        }

        try (MemoryStack stack = stackPush()) {

            VkApplicationInfo applicationInfo = VkApplicationInfo.callocStack()
                    .pApplicationName(stack.UTF8("app"))
                    .applicationVersion(VK_MAKE_VERSION(0,1,1))
                    .pEngineName(stack.UTF8("engine"))
                    .engineVersion(VK_MAKE_VERSION(0,1,1))
                    .apiVersion(VK_API_VERSION_1_0);

            VkInstanceCreateInfo instanceCreateInfo = VkInstanceCreateInfo.callocStack()
                    .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                    .pApplicationInfo(applicationInfo)
                    .ppEnabledExtensionNames(getRequiredExtensions(valdiationLayers));

            if(valdiationLayers != null){

                instanceCreateInfo.ppEnabledLayerNames(valdiationLayers);

                VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo = VkDebugUtilsMessengerCreateInfoEXT.callocStack(stack);
                populateDebugMessengerCreateInfo(debugCreateInfo);
                instanceCreateInfo.pNext(debugCreateInfo.address());

            }
            PointerBuffer instancePtr = stack.mallocPointer(1);

            if(vkCreateInstance(instanceCreateInfo, null, instancePtr) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create instance");
            }

            instance = new VkInstance(instancePtr.get(0), instanceCreateInfo);

            return  instance;
        }
    }

    private PointerBuffer getRequiredExtensions(PointerBuffer valdiationLayers) {

        PointerBuffer glfwExtensions = glfwGetRequiredInstanceExtensions();

        if(valdiationLayers != null){

            MemoryStack stack = stackGet();

            PointerBuffer extensions = stack.mallocPointer(glfwExtensions.capacity() + 1);

            extensions.put(glfwExtensions);
            extensions.put(stack.UTF8(VK_EXT_DEBUG_UTILS_EXTENSION_NAME));

            return extensions.rewind();
        }
        return glfwExtensions;
    }

    public void destroy(){
        vkDestroyInstance(instance,null);
    }
}
