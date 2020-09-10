package Engine.Renderer.Swapchain;

import Engine.Renderer.Window.Window;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;

import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfacePresentModesKHR;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_B8G8R8A8_SRGB;

public class SwapchainSupportDetails {



    private VkSurfaceFormatKHR format;
    private int presentMode;
    public int colorSpace;

    public VkExtent2D swapchainExtent;

    public SwapchainSupportDetails(VkPhysicalDevice device, long surface) {

        this.format = null;
        this.presentMode = -1;
        this.colorSpace = -1;

        try (MemoryStack stack = stackPush()) {

            IntBuffer pFormatCount = stack.ints(-1);                                                  //create and populate available format info
            vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, pFormatCount, null);

            if(pFormatCount.get(0)==-1){
                throw new RuntimeException("Failed to enumerate device surface formats");
            }

            VkSurfaceFormatKHR.Buffer pFormats = VkSurfaceFormatKHR.mallocStack(pFormatCount.get(0), stack);
            vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, pFormatCount, pFormats);

            for(int i = 0; i < pFormats.capacity(); i++){ //search for 32bit rgba color in an non linear color space
                if(pFormats.get(i).format()==VK_FORMAT_B8G8R8A8_SRGB && pFormats.get(i).colorSpace() == VK_COLOR_SPACE_SRGB_NONLINEAR_KHR){
                    format = pFormats.get(i);
                }
            }

            if(pFormats == null){        //if a desired format could not be found get whatever is available
                format = pFormats.get(0);
            }

            colorSpace = format.colorSpace();

            IntBuffer pPresentationModeCount = stack.ints(-1);
            vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface, pPresentationModeCount, null);

            if(pPresentationModeCount.get(0)==-1){
                throw new RuntimeException("Failed to enumerate device surface presentation modes");
            }

            IntBuffer pPresentationModes = stack.mallocInt(pPresentationModeCount.get(0));
            vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface, pPresentationModeCount, pPresentationModes);

            for(int i = 0; i < pPresentationModes.capacity(); i++) {   //checks for desired non tearing present mode
                if(pPresentationModes.get(i) == VK_PRESENT_MODE_MAILBOX_KHR){
                    presentMode = VK_PRESENT_MODE_MAILBOX_KHR;
                }
            }

            if(presentMode == -1){  //if a present mode was not found use default non tearing mode
                presentMode = VK_PRESENT_MODE_FIFO_KHR;
            }



        }
    }

    //this is separate to the constructor so it is not called during physical device selection and extent is not a compatibility feature
    public void chooseSwapchainExtent(VkPhysicalDevice device, Window window){
        try(MemoryStack stack = stackPush()){
            long surface = window.getSurface();
            VkSurfaceCapabilitiesKHR capabilities = VkSurfaceCapabilitiesKHR.callocStack(stack);
            vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device, surface, capabilities);

            if(capabilities.currentExtent().width() != Integer.MAX_VALUE){
                swapchainExtent = capabilities.currentExtent();
                return;
            }

            swapchainExtent.width(window.getWidth());
            swapchainExtent.height(window.getHeight());

            swapchainExtent.width(Math.max(capabilities.minImageExtent().width(),Math.min(capabilities.maxImageExtent().width(), swapchainExtent.width())));
            swapchainExtent.width(Math.max(capabilities.minImageExtent().height(),Math.min(capabilities.maxImageExtent().height(), swapchainExtent.height())));

        }
    }

    public boolean isValid(){
        return  (format != null) && (presentMode != -1) && (colorSpace != -1);
    }



}
