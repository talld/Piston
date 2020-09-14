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
import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkCreateSwapchainKHR;
import static org.lwjgl.vulkan.VK10.*;

public class Swapchain {

    private long swapchain;

    public List<Long> swapchainImages;

    SwapchainSupportDetails supportDetails;

    public Swapchain(){

    }

    public long create(PhysicalDevice physicalDevice, VkDevice lDevice, Window window) {
        try (MemoryStack stack = stackPush()) {

            VkPhysicalDevice device = physicalDevice.getDevice();


            long surface = window.getSurface();
            supportDetails = new SwapchainSupportDetails(device,surface);
            supportDetails.chooseSwapchainExtent(device,window);
            int imageCount = supportDetails.capabilities.minImageCount()+1;

            if(supportDetails.capabilities.maxImageCount() > 0 && imageCount > supportDetails.capabilities.maxImageCount()) {
                imageCount = supportDetails.capabilities.maxImageCount();
            }

            VkSwapchainCreateInfoKHR swapchainCreateInfo = VkSwapchainCreateInfoKHR.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                    .surface(surface)
                    .minImageCount(imageCount)
                    .imageFormat(supportDetails.format.format())
                    .imageColorSpace(supportDetails.format.colorSpace())
                    .imageExtent(supportDetails.swapchainExtent)
                    .imageArrayLayers(1)
                    .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT);

            QueueFamilyIndices indices = physicalDevice.getQueueFamilyIndices();

            if(indices.uniqueGraphicsIndices().size() == 1){
                swapchainCreateInfo.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE);
            }else{

                IntBuffer pQueueFamilyIndices = stack.ints(0,indices.uniqueGraphicsIndices().size());

                for(Object i : indices.uniqueGraphicsIndices()){
                    pQueueFamilyIndices.put((int) i);
                }

                swapchainCreateInfo
                        .imageSharingMode(VK_SHARING_MODE_CONCURRENT)
                        .pQueueFamilyIndices(pQueueFamilyIndices);
            }

            swapchainCreateInfo
                    .preTransform(supportDetails.capabilities.currentTransform())
                    .compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
                    .presentMode(supportDetails.presentMode)
                    .clipped(true)
                    .oldSwapchain(VK_NULL_HANDLE);

            LongBuffer pSwapchain = stack.longs(VK_NULL_HANDLE);

            int status =vkCreateSwapchainKHR(lDevice,swapchainCreateInfo,null,pSwapchain);

            if(status != VK_SUCCESS){
                throw new RuntimeException("Failed to createSwapchain");
            }

            swapchain = pSwapchain.get();
        }
        return swapchain;
    }


    public void destroy(){

    }
}
