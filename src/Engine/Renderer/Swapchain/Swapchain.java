package Engine.Renderer.Swapchain;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import static org.lwjgl.system.MemoryStack.stackPush;

public class Swapchain {

    private long swapchain;

    SwapchainSupportDetails supportDetails;

    public Swapchain(){

    }

    public long create(VkPhysicalDevice device, long surface) {
        try (MemoryStack stack = stackPush()) {
            supportDetails = new SwapchainSupportDetails(device,surface);

            //VkSwapchainCreateInfoKHR swapchainCreateInfo = VkSwapchainCreateInfoKHR.callocStack(stack)
            //        .sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
        }
        return 0l;
    }


    public void destroy(){

    }
}
