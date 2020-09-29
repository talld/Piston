package Engine.Memory;

import Engine.Geometry.Vertex;
import Engine.Memory.Buffers.SingleBuffer.Buffer;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class MemoryUtillities {

    //TODO: single memory allocation for grouped buffers + Dynamic offsets

    public static Buffer createBuffer(VkDevice lDevice, long size, int usage){
        try(MemoryStack stack = stackPush()) {

            VkBufferCreateInfo bufferInfo = VkBufferCreateInfo.callocStack(stack);
            bufferInfo.sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO);
            bufferInfo.size(size);
            bufferInfo.usage(usage);
            bufferInfo.sharingMode(VK_SHARING_MODE_EXCLUSIVE);

            LongBuffer pBuffer = stack.mallocLong(1);

            int status = vkCreateBuffer(lDevice, bufferInfo, null, pBuffer);

            if (status !=VK_SUCCESS){
                throw new RuntimeException("Failed to create vertex buffer");
            }

            return new Buffer(lDevice, size, pBuffer.get(0));
        }
    }

    public static long createGroupBuffer(VkDevice lDevice, long size, int usage){
        try(MemoryStack stack = stackPush()) {

            VkBufferCreateInfo bufferInfo = VkBufferCreateInfo.callocStack(stack);
            bufferInfo.sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO);
            bufferInfo.size(size);
            bufferInfo.usage(usage);
            bufferInfo.sharingMode(VK_SHARING_MODE_EXCLUSIVE);

            LongBuffer pBuffer = stack.mallocLong(1);

            int status = vkCreateBuffer(lDevice, bufferInfo, null, pBuffer);

            if (status !=VK_SUCCESS){
                throw new RuntimeException("Failed to create vertex buffer");
            }

            return pBuffer.get(0);
        }
    }

    public static void copyBuffer(VkQueue queue, long commandPool, Buffer src, Buffer dst){

        try(MemoryStack stack = stackPush()){
            VkCommandBufferAllocateInfo commandBufferAllocateInfo = VkCommandBufferAllocateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                    .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                    .commandPool(commandPool)
                    .commandBufferCount(1);

            PointerBuffer pCommandBuffer = stack.mallocPointer(1);
            vkAllocateCommandBuffers(dst.getLDevice(),commandBufferAllocateInfo, pCommandBuffer);

            VkCommandBufferBeginInfo commandBufferBeginInfo = VkCommandBufferBeginInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
                    .flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);

            VkCommandBuffer commandBuffer = new VkCommandBuffer(pCommandBuffer.get(0), dst.getLDevice());


            VkBufferCopy.Buffer copyRegion = VkBufferCopy.callocStack(1, stack)
                    .srcOffset(0)
                    .dstOffset(0)
                    .size(src.getSize());

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

    public static void copyBuffer(VkDevice lDevice,VkQueue queue, long commandPool, long src, long dst, long size){
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
                vkCmdCopyBuffer(commandBuffer, src, dst, copyRegion);
            }
            vkEndCommandBuffer(commandBuffer);

            VkSubmitInfo submitInfo = VkSubmitInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                    .pCommandBuffers(pCommandBuffer);
            vkQueueSubmit(queue,submitInfo,VK_NULL_HANDLE);
            vkQueueWaitIdle(queue);
        }
    }

    public static void allocateBuffer(Buffer buffer, int memProperties){
        try(MemoryStack stack = stackPush()){

            VkDevice lDevice = buffer.getLDevice();

            long pBuffer = buffer.getBuffer();

            VkMemoryRequirements memRequirements = VkMemoryRequirements.mallocStack(stack);
            vkGetBufferMemoryRequirements(lDevice, pBuffer, memRequirements);

            VkMemoryAllocateInfo allocInfo = VkMemoryAllocateInfo.callocStack(stack);
            allocInfo.sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO);
            allocInfo.allocationSize(memRequirements.size());
            allocInfo.memoryTypeIndex(findMemoryType(lDevice.getPhysicalDevice(),memRequirements.memoryTypeBits(), memProperties));

            LongBuffer pMemory = stack.mallocLong(1);

            int status = vkAllocateMemory(lDevice, allocInfo, null, pMemory);

            if(status != VK_SUCCESS) {
                throw new RuntimeException("Failed to allocate vertex buffer memory");
            }

            buffer.setMemory(pMemory.get(0));

        }
    }

    public static long allocateBuffer(VkDevice lDevice, long buffer, int memProperties){
        try(MemoryStack stack = stackPush()){


            VkMemoryRequirements memRequirements = VkMemoryRequirements.mallocStack(stack);
            vkGetBufferMemoryRequirements(lDevice, buffer, memRequirements);

            VkMemoryAllocateInfo allocInfo = VkMemoryAllocateInfo.callocStack(stack);
            allocInfo.sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO);
            allocInfo.allocationSize(memRequirements.size());
            allocInfo.memoryTypeIndex(findMemoryType(lDevice.getPhysicalDevice(),memRequirements.memoryTypeBits(), memProperties));

            LongBuffer pMemory = stack.mallocLong(1);

            int status = vkAllocateMemory(lDevice, allocInfo, null, pMemory);

            if(status != VK_SUCCESS) {
                throw new RuntimeException("Failed to allocate vertex buffer memory");
            }

            return pMemory.get(0);

        }
    }

    private static int findMemoryType(VkPhysicalDevice pDevice, int typeFilter, int properties) {

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


}
