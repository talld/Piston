package Engine.Memory;

import Engine.Geometry.Vertex;
import Engine.Memory.Buffer.Buffer;
import Engine.Objects.Camera.Camera;
import Engine.Renderer.ValidationLayers.ValidationLayers;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.lwjgl.util.vma.*;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.util.vma.Vma.*;
import static org.lwjgl.vulkan.VK10.*;

public class MemoryUtillities {

    private static VkPhysicalDevice pDevice;
    private static VkDevice lDevice;

    private static long allocator;

    public static void init(VkInstance instance ,VkPhysicalDevice nPDevice, VkDevice nLDevice){
        pDevice = nPDevice;
        lDevice = nLDevice;
        try(MemoryStack stack = stackPush()) {

            VmaVulkanFunctions vulkanFunctions = VmaVulkanFunctions.callocStack(stack).set(instance, lDevice);

            VmaAllocatorCreateInfo allocatorCreateInfo = VmaAllocatorCreateInfo.callocStack(stack)
                    .physicalDevice(pDevice)
                    .device(lDevice)
                    .pVulkanFunctions(vulkanFunctions);

            PointerBuffer pAllocator = stack.mallocPointer(1);
            vmaCreateAllocator(allocatorCreateInfo, pAllocator);
            allocator = pAllocator.get(0);
        }
    }

    public static void destroy(){
        vmaDestroyAllocator(allocator);
    }

    public static Buffer createBuffer(long size, int bufferUsage, int memoryUsage){
        VkBufferCreateInfo bufferCreateInfo = VkBufferCreateInfo.callocStack(stackGet())
                .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                .size(size)
                .usage(bufferUsage)
                .sharingMode(VK_SHARING_MODE_EXCLUSIVE);

        VmaAllocationCreateInfo allocationCreateInfo = VmaAllocationCreateInfo.callocStack(stackGet())
                .usage(memoryUsage);

        LongBuffer pBuffer = stackGet().longs(VK_NULL_HANDLE);
        PointerBuffer pAllocation = stackGet().mallocPointer(1);

        vmaCreateBuffer(allocator, bufferCreateInfo, allocationCreateInfo, pBuffer, pAllocation, null);

        return new Buffer(allocator, pBuffer.get(0),pAllocation.get(0),size);
    }

    public static void copyBuffer(VkQueue queue, long commandPool, Buffer src, Buffer dst, long size){
        try(MemoryStack stack = stackPush()){

            VkCommandBufferAllocateInfo commandBufferAllocateInfo = VkCommandBufferAllocateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                    .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                    .commandPool(commandPool)
                    .commandBufferCount(1);

            PointerBuffer pCommandBuffer = stack.mallocPointer(1);
            vkAllocateCommandBuffers(lDevice,commandBufferAllocateInfo, pCommandBuffer);

            VkCommandBufferBeginInfo commandBufferBeginInfo = VkCommandBufferBeginInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
                    .flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);

            VkCommandBuffer commandBuffer = new VkCommandBuffer(pCommandBuffer.get(0), lDevice);

            VkBufferCopy.Buffer copyRegion = VkBufferCopy.callocStack(1, stack)
                    .srcOffset(0)
                    .dstOffset(0)
                    .size(size);

            vkBeginCommandBuffer(commandBuffer,commandBufferBeginInfo);
            {
                vkCmdCopyBuffer(commandBuffer, src.getBuffer(), dst.getBuffer(), copyRegion);
            }
            vkEndCommandBuffer(commandBuffer);

            VkSubmitInfo submitInfo = VkSubmitInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                    .pCommandBuffers(pCommandBuffer);
            vkQueueSubmit(queue,submitInfo,VK_NULL_HANDLE);
            vkQueueWaitIdle(queue);
        }
    }



    private static int findMemoryType(int typeFilter, int properties) {

        VkPhysicalDeviceMemoryProperties memProperties = VkPhysicalDeviceMemoryProperties.mallocStack();
        vkGetPhysicalDeviceMemoryProperties(pDevice, memProperties);

        for (int i = 0; i < memProperties.memoryTypeCount(); i++) {
            if ((typeFilter & (1 << i)) != 0 && (memProperties.memoryTypes(i).propertyFlags() & properties) == properties) {
                return i;
            }
        }
        throw new RuntimeException("Failed to find suitable memory type: " + typeFilter + " " + properties);
    }

    public static void memCopy(ByteBuffer buffer, Vertex[] vertices) {
        for(Vertex vertex : vertices) {
            buffer.putFloat(vertex.getPos().x());   //anything written in the Buffer will we written to memory
            buffer.putFloat(vertex.getPos().y());   //starting at the pointerbuffer location
            buffer.putFloat(vertex.getPos().z());

            buffer.putFloat(vertex.getColour().x());
            buffer.putFloat(vertex.getColour().y());
            buffer.putFloat(vertex.getColour().z());
        }
    }

    public static void memCopy(ByteBuffer buffer, short[] indices) {
        for(short index : indices) {
            buffer.putShort(index);
        }
        buffer.rewind();
    }

    public static void memCopy(ByteBuffer buffer, Matrix4f projection, Matrix4f view, Matrix4f model){
        final int mat4Size = 16 * Float.BYTES;

        projection.get(mat4Size * 2, buffer);
        view.get(mat4Size, buffer);
        model.get(0, buffer);


    }
}
