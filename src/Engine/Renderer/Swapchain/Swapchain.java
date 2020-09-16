package Engine.Renderer.Swapchain;

import Engine.Renderer.PhysicalDevice.PhysicalDevice;
import Engine.Renderer.PhysicalDevice.QueueFamilyIndices;
import Engine.Renderer.Window.Window;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.List;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class Swapchain {

    private long swapchain;

    public List<Long> swapchainImages;

    SwapchainSupportDetails supportDetails;

    public Swapchain(){

    }

    public long create(PhysicalDevice physicalDevice, VkDevice lDevice, Window window) {
        try (MemoryStack stack = stackPush()) {

            VkPhysicalDevice device = physicalDevice.getPDevice();


            long surface = window.getSurface();
            supportDetails = new SwapchainSupportDetails(device,surface);

            VkExtent2D swapchainExtent = supportDetails.selectSwapchainExtent(window,stack);

            VkSurfaceCapabilitiesKHR capabilities = supportDetails.getCapabilities(stack);

            VkSurfaceFormatKHR format = supportDetails.selectFormat(stack);

            int presnetationMode = supportDetails.selectPresentMode(stack);

            int imageCount = capabilities.minImageCount()+1;

            if(capabilities.maxImageCount() > 0 && imageCount > capabilities.maxImageCount()) {
                imageCount = capabilities.maxImageCount();
            }

            VkSwapchainCreateInfoKHR swapchainCreateInfo = VkSwapchainCreateInfoKHR.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                    .surface(surface)
                    .minImageCount(imageCount)
                    .imageFormat(format.format())
                    .imageColorSpace(format.colorSpace())
                    .imageExtent(swapchainExtent)
                    .imageArrayLayers(1)
                    .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT);

            QueueFamilyIndices indices = physicalDevice.getQueueFamilyIndices();

            if(indices.uniqueGraphicsIndices().size() == 1){
                swapchainCreateInfo.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE);
            }else{
                swapchainCreateInfo
                        .imageSharingMode(VK_SHARING_MODE_CONCURRENT)
                        .pQueueFamilyIndices(stack.ints(indices.graphicsFamilyIndex, indices.presentationFamilyIndex));
            }

            swapchainCreateInfo
                    .preTransform(capabilities.currentTransform())
                    .compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
                    .presentMode(presnetationMode)
                    .clipped(true)
                    .oldSwapchain(VK_NULL_HANDLE);

            LongBuffer pSwapchain = stack.longs(VK_NULL_HANDLE);

            int status = vkCreateSwapchainKHR(lDevice,swapchainCreateInfo,null,pSwapchain);

            if(status != VK_SUCCESS){
                throw new RuntimeException("Failed to createSwapchain");
            }

            swapchain = pSwapchain.get();
        }
        return swapchain;
    }


    public void destroy(VkDevice lDevice){
        vkDestroySwapchainKHR(lDevice,swapchain,null);
    }
}
