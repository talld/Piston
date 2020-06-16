package Engine.Renderer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Pointer;
import org.lwjgl.vulkan.*;
import org.lwjgl.glfw.*;
import org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWVulkan.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;

public class RenderUtil {

    private static VkInstance Instance;
    private static VkPhysicalDevice chosenDevice;
    private static VkDevice lDevice;

    private static int graphicsFamilyIndex =-1;
    private static int presentFamilyIndex =-1;
    private static VkQueue graphicsQueue;
    public static ArrayList<Integer> Queues = new ArrayList<Integer>();

    private static PointerBuffer glfwExtensions;

    private static VkSurfaceCapabilitiesKHR pSCapabilities;
    private static int presentModeCount;
    private static int colorFormat;
    private static int colorSpace;
    private static int swapChain;

    public static void cls(){

    }

    public static void initRender(){

    }

    public static void VkInit(){

        VkApplicationInfo appInfo = VkApplicationInfo.calloc();                  //define app info
        appInfo.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO);                       //auto-generate structure info
        appInfo.applicationVersion(VK_MAKE_VERSION(1,0,0)); //auto generate version info
        appInfo.engineVersion(VK_MAKE_VERSION(1,0,0));      //^
        appInfo.apiVersion(VK_API_VERSION_1_0);                                 //default Vulkan version


        VkInstanceCreateInfo cInfo = VkInstanceCreateInfo.calloc();            //define Creation info
        cInfo.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);                   //auto-generate structure info
        cInfo.pApplicationInfo(appInfo);                                       //parse app info

        glfwExtensions = glfwGetRequiredInstanceExtensions();    //find required extensions
        if(glfwExtensions == null){
            throw new IllegalStateException("could not create required instances");
        }

        PointerBuffer pRequiredExtensions  = memAllocPointer(glfwExtensions.remaining()+1);
        pRequiredExtensions.put(glfwExtensions);
        ByteBuffer VK_EXT_DEBUG_REPORT_EXTENSION = memUTF8(VK_EXT_DEBUG_REPORT_EXTENSION_NAME);
        pRequiredExtensions.put(VK_EXT_DEBUG_REPORT_EXTENSION);
        pRequiredExtensions.flip();                                            //put the debug extension first

        cInfo.ppEnabledExtensionNames(glfwExtensions);                         //enable them




        PointerBuffer pBInstance = memAllocPointer(1);                    //allocate memory for Vulkan instance
        int  check = VK10.vkCreateInstance(cInfo,null,pBInstance);   //create instance with check int
        long pInstance = pBInstance.get(0);                                    //gen the position of newly created instance
        if(check != VK_SUCCESS) {                                              //check for fail
            throw new IllegalStateException("VKINIT failed");                  //error out
        }else{
            Instance = new VkInstance(pInstance,cInfo);                       //assign instance;
        }
        memFree(pBInstance);                                                 //free up space taken buy the instance point now that it has been assigned to a var
        memFree(VK_EXT_DEBUG_REPORT_EXTENSION);
        memFree(pRequiredExtensions);
                                                                             //calloc is auto removed from the heap and destroyed after use so no need to clean them up

        VkInitHardware();                                                    //call next step;
    }

    public static void VkInitDebug(){
        //TODO: validation layers
    }

    private static void VkInitHardware(){
        VkPhysicalDevice device = null;                                                             //set up a blank device that will become the GPU
        IntBuffer bDeviceCount = memAllocInt(1);                                               //create buffered into to store device count
        int check = vkEnumeratePhysicalDevices(getInstance(),bDeviceCount,null);     //assign count to buffer
        if(check!=VK_SUCCESS){                                                                      //did the count fail?
            throw new IllegalStateException("Failed to access number of physical devices");         //error out
        }
        int deviceCount  = bDeviceCount.get(0);                                                    //create a int version of the device count for checks
        if(deviceCount<1){                                                                         //check device count to see if any devices where actually found
            throw new IllegalStateException("Failed to find GPU with vulkan support");             //error out
            }
        PointerBuffer pDevices = memAllocPointer(deviceCount);                                     //create a pointerbuffer for each device found
        check = vkEnumeratePhysicalDevices(getInstance(),bDeviceCount,pDevices);                   //bind it to the devices
        memFree(bDeviceCount);                                                                     //device count is no longer needed
        if(deviceCount<1){                                                                         //was a device even found
            throw new IllegalStateException("Failed to fetch physical devices");                   //error out
        }

        chosenDevice = VkSelectDevice(pDevices);                                                   //chose the best found device


        memFree(pDevices);                                                                         //free pointers

    }

    public static void VkPostInit(){
        VkFindQueueFamilys(chosenDevice);
        VkCreateGraphicsFamily();
        VkCreateSwapChain(Window.getHeight(),Window.getWidth(),false);
    }

    private static VkPhysicalDevice VkSelectDevice(PointerBuffer pDevices){//
        int highest = 0;
        long bestDevice = -1;
        long currentDevice = 0;
        for(int i = 0; i<(pDevices.remaining());i++){       //select highest scoring device
            currentDevice =pDevices.get(i);
            if(highest<VkEvaluateDevice(currentDevice)){
               highest = VkEvaluateDevice(currentDevice);
               bestDevice = currentDevice;                  //not the most efficient system in the world but as an init stage it's negligible
           }
        }
        if(bestDevice == -1){
            throw new IllegalStateException("Failed to find compatible graphics device");
        }
        return new VkPhysicalDevice(bestDevice,getInstance());
    }

    private static int VkEvaluateDevice(long pDevice){                                         //scores GPU ready for device selection
        VkPhysicalDevice device = new VkPhysicalDevice(pDevice,getInstance());                 //device point is set to device
        VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.calloc();                 //set device features fields in memory
        VkPhysicalDeviceProperties properties = VkPhysicalDeviceProperties.calloc();           //set device properties fields memory
        vkGetPhysicalDeviceFeatures(device,features);                                          //bind device features to fields
        vkGetPhysicalDeviceProperties(device,properties);                                      //bind device properties to fields

        if(VkFindGraphicQueueFamily(device,0)==-1){
            return -1;
        }

        int maxImage = properties.limits().maxImageDimension2D();                              //max texture size
        float maxAniso = properties.limits().maxSamplerAnisotropy();                           //max points for anisotropic filtering
        int maxMemAloc = properties.limits().maxMemoryAllocationCount();                       //max memory
        int maxGeoShaderIn = properties.limits().maxGeometryInputComponents();                 //max input points for geometry shader
        int maxFragShaderIn = properties.limits().maxFragmentInputComponents();                //max input points for fragment shader

        int score = Math.round(maxImage+maxAniso);

        if(properties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU){                   //is the gpu external to system?
            score+=(maxMemAloc+maxGeoShaderIn+maxFragShaderIn*2);                              //shader and memory is external to system memory so more valuable
        }else{
            score+=(maxMemAloc+maxGeoShaderIn+maxFragShaderIn);                                //shared with system memory : worse
        }


        return score;                                                                          //gpu rating
    }

    public static int VkFindGraphicQueueFamily(VkPhysicalDevice device, int startIndex){                            //is gpu compatible with main core systems TODO: add more checks as needed
        IntBuffer QFCount = memAllocInt(1);                                                                    //allocate memory for family counter
        vkGetPhysicalDeviceQueueFamilyProperties(device,QFCount,null);                         //count the families
        int IQFCount = QFCount.get(0);                                                                              //int version for processing
        VkQueueFamilyProperties.Buffer QFProperties = VkQueueFamilyProperties.calloc(IQFCount);                     //create a queue family buffer with IQFCount Blocks
        vkGetPhysicalDeviceQueueFamilyProperties(device,QFCount,QFProperties);                                      //bind the properties to the buffer
        int QueueFamilyIndex;
        for (QueueFamilyIndex=startIndex; QueueFamilyIndex < IQFCount; QueueFamilyIndex++){                         //loop through every family
            if ((QFProperties.get(QueueFamilyIndex).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0)                     //is it a compatible graphics family?
                memFree(QFCount);                                                                                   //memory clean up
            memFree(QFProperties);
            return QueueFamilyIndex;                                                                            //return it
        }
        memFree(QFCount);                                                                                           //memory clean up
        memFree(QFProperties);
        return -1;                                                                                                  //couldn't find graphics family
    }

    public static void VkFindQueueFamilys(VkPhysicalDevice device){
        IntBuffer QFCount = memAllocInt(1);                                                                    //allocate memory for family counter
        vkGetPhysicalDeviceQueueFamilyProperties(device,QFCount,null);                         //count the families
        int IQFCount = QFCount.get(0);                                                                              //int version for processing
        VkQueueFamilyProperties.Buffer QFProperties = VkQueueFamilyProperties.calloc(IQFCount);                     //create a queue family buffer with IQFCount Blocks
        vkGetPhysicalDeviceQueueFamilyProperties(device,QFCount,QFProperties);                                      //bind the properties to the buffer
        int queueFamilyIndex;
        IntBuffer surfaces = memAllocInt(IQFCount);
        for (queueFamilyIndex=0; queueFamilyIndex < IQFCount; queueFamilyIndex++){                                  //loop through every family
            surfaces.position(queueFamilyIndex);                                                                    //checking for present support
            vkGetPhysicalDeviceSurfaceSupportKHR(device,queueFamilyIndex,Window.getSurface(),surfaces);             //add VK_TRUE/FALSE for every entry for its support
        }

        IntBuffer graphics =memAllocInt(IQFCount);
        for (queueFamilyIndex=0; queueFamilyIndex < IQFCount; queueFamilyIndex++){                                   //loop through every family
            if ((QFProperties.get(queueFamilyIndex).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {                    //is it a compatible graphics family?
                graphics.put(queueFamilyIndex);                                                                      //add it to the list
            }else{
                graphics.put(-1);                                                                                    //if it isn't mark it as such
            }
                memFree(QFProperties);
                memFree(QFCount);
        }

        //boolean foundPair = false;
        for (queueFamilyIndex=0; queueFamilyIndex < IQFCount; queueFamilyIndex++){
            if((graphics.get(queueFamilyIndex)!=-1)&(surfaces.get(queueFamilyIndex)==VK_TRUE)){                      //loop through every family to find one that supports both
                //foundPair = true;
                graphicsFamilyIndex = queueFamilyIndex;
                presentFamilyIndex = queueFamilyIndex;
            }
        }
        /* TODO: add support for multiple queue indices
        // section for multiple queue indices still requires support in queue creation methods
        if(!foundPair){
            for(queueFamilyIndex=0; queueFamilyIndex < IQFCount; queueFamilyIndex++){
                if(graphics.get(queueFamilyIndex)!=-1){
                    graphicsFamilyIndex = queueFamilyIndex;
                    break;
                }
            }

            for(queueFamilyIndex=0; queueFamilyIndex < IQFCount; queueFamilyIndex++){
                if(surfaces.get(queueFamilyIndex)==VK_TRUE){
                    presentFamilyIndex = queueFamilyIndex;
                    break;
                }
            }
        }
        */
        if(presentFamilyIndex == -1){
            throw new IllegalStateException("failed to find a surface queue family");
        }

        if(graphicsFamilyIndex == -1){
            throw new IllegalStateException("failed to find a graphics queue family");
        }

        memFree(QFCount);                                                                                           //memory clean up
        memFree(QFProperties);
    }

    private static void VkCreateGraphicsFamily(){
        VkDeviceQueueCreateInfo.Buffer dCQInfos = VkDeviceQueueCreateInfo.calloc(Queues.size());                    //creation info for multiple queue indices currently unused


        VkDeviceQueueCreateInfo.Buffer dCQInfo = VkDeviceQueueCreateInfo.calloc(1);                      //device creation info
        dCQInfo.sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO);
        dCQInfo.queueFamilyIndex(graphicsFamilyIndex);
        FloatBuffer pPriority = memAllocFloat(1).put(0.0f);
        pPriority.flip();
        dCQInfo.pQueuePriorities(pPriority);



        VkPhysicalDeviceFeatures dFeatures = VkPhysicalDeviceFeatures.calloc();                                   //device feature info
        ByteBuffer VK_KHR_SWAPCHAIN_EXTENSION = memUTF8(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
        glfwExtensions.put(VK_KHR_SWAPCHAIN_EXTENSION);                                                           //add swap chain extension
        glfwExtensions.flip();                                                                                    //make it the first in the list
        VkDeviceCreateInfo dCInfo = VkDeviceCreateInfo.calloc();                                                  //device creation info
        dCInfo.sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO);
        dCInfo.ppEnabledExtensionNames(glfwExtensions);                                                           //bind extensions
        dCInfo.pQueueCreateInfos(dCQInfo);
        dCInfo.pEnabledFeatures(dFeatures);


        PointerBuffer pDevice = memAllocPointer(1);
        int check = vkCreateDevice(chosenDevice,dCInfo,null,pDevice);
        long device = pDevice.get(0);
        memFree(pDevice);
        if(check != VK_SUCCESS){
            throw new  IllegalStateException("Failed to create logical device");
        }
        lDevice = new VkDevice(device,chosenDevice,dCInfo);

        graphicsQueue = VkCreateDeviceQueue(lDevice,graphicsFamilyIndex);
        dCInfo.free();
    }


    public static VkQueue VkCreateDeviceQueue(VkDevice device, int queueFamilyIndex){
        PointerBuffer pQueue = memAllocPointer(1);
        vkGetDeviceQueue(device,queueFamilyIndex,0,pQueue);              //bind the family to the point
        long queue =pQueue.get(0);
        memFree(pQueue);
        return new VkQueue(queue,device);                                          //create and return device Queue from family
    }

    private static void chooseColorFormatAndSpace(VkSurfaceFormatKHR.Buffer surfFormats){
        if (presentModeCount == 1 && surfFormats.get(0).format() == VK_FORMAT_UNDEFINED) {
            colorFormat = VK_FORMAT_B8G8R8A8_UNORM;
        } else {
            colorFormat = surfFormats.get(0).format();
        }
        colorSpace = surfFormats.get(0).colorSpace();
    }

    public static void VkCreateSwapChain(int newWidth,int newHeight,boolean reCreate){

        pSCapabilities = VkSurfaceCapabilitiesKHR.calloc();
        int check = vkGetPhysicalDeviceSurfaceCapabilitiesKHR(chosenDevice,Window.getSurface(),pSCapabilities);
        if(check!=VK_SUCCESS){
            throw new IllegalStateException("Failed to find number of device surface capabilities ");
        }

        IntBuffer pPresentModeCount = memAllocInt(1);
        check = vkGetPhysicalDeviceSurfacePresentModesKHR(chosenDevice,Window.getSurface(),pPresentModeCount, null);
        presentModeCount = pPresentModeCount.get(0);
        if (check != VK_SUCCESS) {
            throw new IllegalStateException("Failed to get number of physical device surface presentation modes");
        }

        IntBuffer pPresentModes = memAllocInt(presentModeCount);
        check = vkGetPhysicalDeviceSurfacePresentModesKHR(chosenDevice,Window.getSurface(), pPresentModeCount, pPresentModes);
        memFree(pPresentModeCount);
        if (check != VK_SUCCESS) {
            throw new IllegalStateException("Failed to get device surface presentation modes");
        }

        IntBuffer pFormatCount = memAllocInt(1);
        int err = vkGetPhysicalDeviceSurfaceFormatsKHR(chosenDevice,Window.getSurface(), pFormatCount, null);
        int formatCount = pFormatCount.get(0);
        if (err != VK_SUCCESS) {
            throw new IllegalStateException("Failed to get  device surface format count");
        }

        VkSurfaceFormatKHR.Buffer surfFormats = VkSurfaceFormatKHR.calloc(formatCount);
        err = vkGetPhysicalDeviceSurfaceFormatsKHR(chosenDevice,Window.getSurface(), pFormatCount, surfFormats);
        memFree(pFormatCount);
        if (err != VK_SUCCESS) {
            throw new IllegalStateException("Failed to get device surface formats");
        }

        chooseColorFormatAndSpace(surfFormats);

        int ScPresentMode = VK_PRESENT_MODE_FIFO_KHR;
        for(int i = 0; i<formatCount;i++){
            if(pPresentModes.get(i)==VK_PRESENT_MODE_MAILBOX_KHR){
                ScPresentMode = VK_PRESENT_MODE_MAILBOX_KHR;
                break;
            }
        }

        VkExtent2D extent2D = pSCapabilities.currentExtent();
        int width = extent2D.width();
        int height = extent2D.height();

        int desiredNumberOfSwapchainImages = pSCapabilities.minImageCount() + 1;
        if ((pSCapabilities.maxImageCount() > 0) && (desiredNumberOfSwapchainImages > pSCapabilities.maxImageCount())) {
            desiredNumberOfSwapchainImages = pSCapabilities.maxImageCount();
        }


    }


    public static VkInstance getInstance() {
        return Instance;
    }

    public static VkPhysicalDevice getChosenDevice() {
        return chosenDevice;
    }

    public static void setChosenDevice(VkPhysicalDevice chosenDevice) {
        RenderUtil.chosenDevice = chosenDevice;
    }

    public static VkDevice getLogicDevice() {
        return lDevice;
    }



}
