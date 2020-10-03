package Engine.Objects.Camera;

import Engine.Memory.Buffer.Buffer;
import Engine.Memory.MemoryUtillities;
import Engine.Renderer.DescriptorSet.DescriptorPool;
import Engine.Renderer.Swapchain.Swapchain;
import Engine.Renderer.Utilities.ErrorUtilities;
import org.joml.Matrix2fc;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkDescriptorSetAllocateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkWriteDescriptorSet;

import java.nio.LongBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.util.vma.Vma.*;
import static org.lwjgl.vulkan.VK10.*;

public class Camera {

    private final int uboSize;
    private VkDevice lDevice;

    private Matrix4f view;
    private Matrix4f projection;
    private Matrix4f model;

    private float x;
    private float y;
    private float z;
    private float xx;
    private float yy;
    private float zz;
    private ArrayList<Buffer> uniformBuffers;
    private ArrayList<Long> descriptorSets;
    private double deltaTime;
    private double pastTime;

    public Camera(){
        this.view = new Matrix4f();
        this.projection = new Matrix4f();
        this.model = new Matrix4f();
        this.x  = 0f;
        this.y  = 0f;
        this.z  = 0f;
        this.xx  = 0f;
        this.yy  = 0f;
        this.zz  = 0f;
        this.uboSize = 3 * 16 * Float.BYTES;

        uniformBuffers = new ArrayList<Buffer>();
    }

    public void create(VkDevice lDevice, Swapchain swapchain, long descriptorSetLayout, long descriptorPool){
        view.lookAt(x,0f,3f, 0.0f, 0.0f, 0.0f, 0, 1f, 0f);
        projection.perspective((float) Math.toRadians(45),
                (float)swapchain.getSwapchainExtent().width() / (float)swapchain.getSwapchainExtent().height(), 0.1f, 10.0f);
        projection.m11(projection.m11() * -1);
        model.rotate((float) (2 * Math.toRadians(90)), 0.0f, 0.0f, 1.0f);


        this.lDevice = lDevice;

        initUBOs(swapchain, descriptorSetLayout, descriptorPool);
    }

    public void recreate(Swapchain swapchain, long descriptorSetLayout, long descriptorPool){

        destroy();

        initUBOs(swapchain, descriptorSetLayout, descriptorPool);
    }

    private void initUBOs(Swapchain swapchain, long descriptorSetLayout, long descriptorPool){

        int swapchainImageCount = swapchain.getSwapchainImagesViews().size();

        try(MemoryStack stack = MemoryStack.stackPush()) {

            for (int i = 0; i < swapchainImageCount; i++) {
                uniformBuffers.add(MemoryUtillities.createBuffer(uboSize, VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT, VMA_MEMORY_USAGE_CPU_ONLY));
            }
            LongBuffer layouts = stack.mallocLong(swapchainImageCount);

            for (int i = 0; i < swapchainImageCount; i++) {
                layouts.put(i, descriptorSetLayout);
            }

            VkDescriptorSetAllocateInfo allocInfo = VkDescriptorSetAllocateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO)
                    .descriptorPool(descriptorPool)
                    .pSetLayouts(layouts);

            LongBuffer pDescriptorSets = stack.mallocLong(swapchainImageCount);

            int status = vkAllocateDescriptorSets(lDevice, allocInfo, pDescriptorSets);
            if (status != VK_SUCCESS) {
                throw new RuntimeException("Failed to create Descriptor set: " + ErrorUtilities.getError(status));
            }
            descriptorSets = new ArrayList<>(pDescriptorSets.capacity());

            VkDescriptorBufferInfo.Buffer bufferInfo = VkDescriptorBufferInfo.callocStack(1, stack);
            bufferInfo.offset(0);
            bufferInfo.range(uboSize);

            VkWriteDescriptorSet.Buffer descriptorWrite = VkWriteDescriptorSet.callocStack(1, stack);
            descriptorWrite.sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET);
            descriptorWrite.dstBinding(0);
            descriptorWrite.dstArrayElement(0);
            descriptorWrite.descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
            descriptorWrite.descriptorCount(1);
            descriptorWrite.pBufferInfo(bufferInfo);

            for (int i = 0; i < pDescriptorSets.capacity(); i++) {

                long descriptorSet = pDescriptorSets.get(i);

                bufferInfo.buffer(uniformBuffers.get(i).getBuffer());

                descriptorWrite.dstSet(descriptorSet);

                vkUpdateDescriptorSets(lDevice, descriptorWrite, null);

                descriptorSets.add(descriptorSet);
            }
        }
    }

    public void update(){
        view.translate(x,y,z);
        x=0f;
        y=0f;
        z=0f;
        if(xx!=0 || yy!=0 || zz!=0) {
            model.rotate((float) (0.1 * Math.toRadians(90)), xx, yy, zz);
        }
        xx=0f;
        yy=0f;
        zz=0f;
    }

    public void renderUpdate(int imageIndex) {

            try(MemoryStack stack = stackPush()) {

                deltaTime = pastTime - glfwGetTime();

                pastTime = glfwGetTime();





                Buffer buffer = uniformBuffers.get(imageIndex);

                PointerBuffer data = stack.mallocPointer(1);

                vmaMapMemory(buffer.getAllocator(), buffer.getAllocation(), data);
                {
                    MemoryUtillities.memCopy(data.getByteBuffer(0, uboSize), projection,view,model);
                }
                vmaUnmapMemory(buffer.getAllocator(), buffer.getAllocation());
            }
        }


    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void setXX(float x) {
        this.xx = x;
    }

    public void setYY(float y) {
        this.yy = y;
    }

    public void setZZ(float z) {
        this.zz = z;
    }

    public Matrix4f getView() {
        return view;
    }

    public Matrix4f getProjection() {
        return projection;
    }

    public void destroy(){

        for(Buffer buffer : uniformBuffers){
            buffer.destroy();
        }

        uniformBuffers.clear();
        descriptorSets.clear();
    }

    public long getDescriptorSets(int i) {
        return descriptorSets.get(i);
    }
}
