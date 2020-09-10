package Engine.Renderer.Swapchain;

import Engine.Renderer.Window.Window;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.util.List;

import static org.lwjgl.system.MemoryStack.stackPush;

public class Swapchain {

    private long swapchain;

    public List<Long> swapchainImages;

    SwapchainSupportDetails supportDetails;

    public Swapchain(){

    }

    public long create(VkPhysicalDevice device, Window window) {
        try (MemoryStack stack = stackPush()) {
            long surface = window.getSurface();
            supportDetails = new SwapchainSupportDetails(device,surface);
            supportDetails.chooseSwapchainExtent(device,window);
            //VkSwapchainCreateInfoKHR swapchainCreateInfo = VkSwapchainCreateInfoKHR.callocStack(stack)
            //        .sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
        }
        return 0l;
    }


    public void destroy(){

    }
}
