package Engine.Swapchain;

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

public class SwapchainSupportDetails {

    public VkSurfaceCapabilitiesKHR capabilities;
    public VkSurfaceFormatKHR.Buffer formats;
    public IntBuffer presentationModes;
    public List<Long> swapchainImages;
    public VkExtent2D swapChainExtent;

    public SwapchainSupportDetails(VkPhysicalDevice device, long surface) {
        try (MemoryStack stack = stackPush()) {

            capabilities = VkSurfaceCapabilitiesKHR.callocStack(stack);
            vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device, surface, capabilities);

            IntBuffer pformatCount = stack.ints(-1);
            vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, pformatCount, null);
            formats = VkSurfaceFormatKHR.callocStack(pformatCount.get(0), stack);
            vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, pformatCount, formats);

            IntBuffer pPresentationModeCount = stack.ints(-1);
            vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface, pPresentationModeCount, null);
            vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface, pPresentationModeCount, presentationModes);
        }
    }



}
