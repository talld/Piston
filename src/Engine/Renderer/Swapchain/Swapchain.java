package Engine.Renderer.Swapchain;

import Engine.Renderer.PhysicalDevice.PhysicalDevice;
import Engine.Renderer.PhysicalDevice.QueueFamilyIndices;
import Engine.Renderer.Utilities.ErrorUtilities;
import Engine.Renderer.Window.Window;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class Swapchain {

    private long swapchain;

    private ArrayList<Long> swapchainImages;

    private ArrayList<Long> swapchainImagesViews;

    SwapchainSupportDetails supportDetails;

    private int swapchainImageFormat;
    private VkExtent2D swapchainExtent;

    public Swapchain(){

    }

    public long create(PhysicalDevice physicalDevice, VkDevice lDevice, Window window,long oldSwapchain) {
        try (MemoryStack stack = stackPush()) {

            VkPhysicalDevice device = physicalDevice.getPDevice();


            long surface = window.getSurface();
            supportDetails = new SwapchainSupportDetails(device,surface);

            VkExtent2D extent = supportDetails.selectSwapchainExtent(window,stack);

            VkSurfaceCapabilitiesKHR capabilities = supportDetails.getCapabilities(stack);

            VkSurfaceFormatKHR format = supportDetails.selectFormat(stack);

            int presnetationMode = supportDetails.selectPresentMode(stack);

            IntBuffer imageCount = stack.ints(capabilities.minImageCount() + 1);

            if(capabilities.maxImageCount() > 0 && imageCount.get(0) > capabilities.maxImageCount()) {
                imageCount.put(0, capabilities.maxImageCount());
            }

            VkSwapchainCreateInfoKHR swapchainCreateInfo = VkSwapchainCreateInfoKHR.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                    .surface(surface)
                    .minImageCount(imageCount.get(0))
                    .imageFormat(format.format())
                    .imageColorSpace(format.colorSpace())
                    .imageExtent(extent)
                    .imageArrayLayers(1)
                    .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT);

            QueueFamilyIndices indices = physicalDevice.getQueueFamilyIndices();

            if(indices.uniqueGraphicsIndices().size() == 1){
                swapchainCreateInfo.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE);
            }else{
                swapchainCreateInfo.imageSharingMode(VK_SHARING_MODE_CONCURRENT)
                                   .pQueueFamilyIndices(stack.ints(indices.graphicsFamilyIndex, indices.presentationFamilyIndex));
            }

            swapchainCreateInfo.preTransform(capabilities.currentTransform())
                               .compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
                               .presentMode(presnetationMode)
                               .clipped(true)
                               .oldSwapchain(oldSwapchain);

            LongBuffer pSwapchain = stack.longs(VK_NULL_HANDLE);

            int status = vkCreateSwapchainKHR(lDevice,swapchainCreateInfo,null,pSwapchain);

            if(status != VK_SUCCESS){
                throw new RuntimeException("Failed to create Swapchain: " + ErrorUtilities.getError(status));
            }

            swapchain = pSwapchain.get(0);

            vkGetSwapchainImagesKHR(lDevice, swapchain, imageCount, null);

            LongBuffer pSwapchainImages = stack.mallocLong(imageCount.get(0));

            vkGetSwapchainImagesKHR(lDevice, swapchain, imageCount, pSwapchainImages);

            swapchainImages = new ArrayList<>(imageCount.get(0));

            for(int i = 0;i < pSwapchainImages.capacity();i++) {
                swapchainImages.add(pSwapchainImages.get(i));
            }

            swapchainImageFormat = format.format();
            swapchainExtent = VkExtent2D.create().set(extent);
        }
        return swapchain;
    }

    public ArrayList<Long> createSwapchainImageViews(VkDevice lDevice){
        try(MemoryStack stack = stackPush()) {
            swapchainImagesViews = new ArrayList<>(swapchainImages.size());
            LongBuffer pImageView = stack.longs(1);
            for(long image : swapchainImages){
                VkImageViewCreateInfo imageViewCreateInfo = VkImageViewCreateInfo.callocStack(stack)
                        .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                        .image(image)
                        .viewType(VK_IMAGE_VIEW_TYPE_2D)
                        .format(swapchainImageFormat);

                imageViewCreateInfo.components().r(VK_COMPONENT_SWIZZLE_IDENTITY)
                                                .g(VK_COMPONENT_SWIZZLE_IDENTITY)
                                                .b(VK_COMPONENT_SWIZZLE_IDENTITY)
                                                .a(VK_COMPONENT_SWIZZLE_IDENTITY);

                imageViewCreateInfo.subresourceRange().aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                                                      .baseMipLevel(0)
                                                      .levelCount(1)
                                                      .baseArrayLayer(0)
                                                      .layerCount(1);

                int status = vkCreateImageView(lDevice,imageViewCreateInfo,null,pImageView);

                if(status != VK_SUCCESS){
                    throw new RuntimeException("Failed to create Image view " + image);
                }
                swapchainImagesViews.add(pImageView.get(0));
            }
        }
        return swapchainImages;
    }

    public ArrayList<Long> getSwapchainImagesViews(){
        return swapchainImagesViews;
    }

    public int getImageFormat(){
        return swapchainImageFormat;
    }

    public VkExtent2D getSwapchainExtent(){
        return swapchainExtent;
    }

    public void destroy(VkDevice lDevice){

        for(long imageView : swapchainImagesViews){
            vkDestroyImageView(lDevice,imageView,null);
        }

        vkDestroySwapchainKHR(lDevice,swapchain,null);
    }
}
