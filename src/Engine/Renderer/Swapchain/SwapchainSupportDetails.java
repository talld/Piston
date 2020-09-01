package Engine.Renderer.Swapchain;

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

    public VkSurfaceCapabilitiesKHR capabilities;
    public VkSurfaceFormatKHR.Buffer formats;
    public IntBuffer presentationModes;

    public VkSurfaceFormatKHR format = null;
    public int colorSpace;

    public List<Long> swapchainImages;
    public VkExtent2D swapChainExtent;

    public SwapchainSupportDetails(VkPhysicalDevice device, long surface) {
        try (MemoryStack stack = stackPush()) {

            capabilities = VkSurfaceCapabilitiesKHR.callocStack(stack);
            vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device, surface, capabilities);

            IntBuffer pFormatCount = stack.ints(-1);
            vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, pFormatCount, null);
            formats = VkSurfaceFormatKHR.callocStack(pFormatCount.get(0), stack);
            vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, pFormatCount, formats);

            IntBuffer pPresentationModeCount = stack.ints(-1);
            vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface, pPresentationModeCount, null);
            vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface, pPresentationModeCount, presentationModes);

            for(int i = 0; i < formats.capacity(); i++){
                if(formats.get(i).format()==VK_FORMAT_B8G8R8A8_SRGB && formats.get(i).colorSpace() == VK_COLOR_SPACE_SRGB_NONLINEAR_KHR){
                    format = formats.get(i);
                }
            }

            if(formats == null){
                format = formats.get(0);
            }

        }
    }



}
