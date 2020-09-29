package Engine.Mesh;

import Engine.Geometry.Vertex;
import Engine.Memory.Buffers.GroupBuffer.BufferDescriptor;
import Engine.Memory.Buffers.GroupBuffer.GroupBuffer;
import Engine.Memory.Buffers.SingleBuffer.Buffer;
import Engine.Memory.MemoryUtillities;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkQueue;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class Mesh {

    private VkDevice lDevice;


    private Vertex[] vertices;
    private short[] indices;


    private long verticesSize;
    private Buffer vertexBuffer;

    private long indicesSize;
    private Buffer indexBuffer;

    private VkQueue transferQueue;
    private long transferPool;

    private long indexOffset;
    private long vertexOffset;

    private Integer ID;

    public Mesh(VkDevice lDevice, long transferPool, VkQueue transferQueue){
        this.lDevice = lDevice;
        this.transferPool = transferPool;
        this.transferQueue = transferQueue;

    }

    public void create(Vertex[] vertices, short[] indices){
        this.vertices = vertices;
        this.indices = indices;
        this.verticesSize = vertices.length * (6*Float.BYTES);
        this.indicesSize = indices.length * Short.BYTES;
        System.out.println(Short.BYTES);
        this.create();
    }

    public GroupBuffer create(Vertex[] vertices, short[] indices, GroupBuffer groupBuffer, Integer ID){
        this.vertices = vertices;
        this.indices = indices;
        this.verticesSize = vertices.length * (6*Float.BYTES);
        this.indicesSize = indices.length * Short.BYTES;
        this.ID = ID;
        return this.create(groupBuffer);
    }


    public GroupBuffer create(GroupBuffer groupBuffer) {
        try (MemoryStack stack = stackPush()) {
            BufferDescriptor verticesBuffer =  new BufferDescriptor(verticesSize);
            BufferDescriptor indicesBuffer =  new BufferDescriptor(indicesSize);
            groupBuffer.addBufferDescriptor(ID,"vert",verticesBuffer);
            vertexOffset = groupBuffer.getBufferDescriptor(ID,"vert").getOffset();
            groupBuffer.addBufferDescriptor(ID,"ind",indicesBuffer);
            indexOffset = groupBuffer.getBufferDescriptor(ID,"ind").getOffset();

        }
        return groupBuffer;
    }

    public void push(GroupBuffer groupBuffer){

        try(MemoryStack stack = stackPush()) {
            PointerBuffer data = stack.mallocPointer(1);

            vkMapMemory(lDevice, groupBuffer.getStagingMemory(), groupBuffer.getBufferDescriptor(ID, "vert").getOffset(), verticesSize, 0, data);
            {
                MemoryUtillities.memCopy(data.getByteBuffer(0, (int) verticesSize), vertices);
            }
            vkUnmapMemory(lDevice, groupBuffer.getStagingMemory());

            vkMapMemory(lDevice, groupBuffer.getStagingMemory(), groupBuffer.getBufferDescriptor(ID, "ind").getOffset(), indicesSize, 0, data);
            {
                MemoryUtillities.memCopy(data.getByteBuffer(0, (int) indicesSize), indices);
            }
            vkUnmapMemory(lDevice, groupBuffer.getStagingMemory());
        }
    }

    public void create(){
        try(MemoryStack stack = stackPush()) {

            Buffer stagingBuffer = MemoryUtillities.createBuffer(lDevice, (int) verticesSize,VK_BUFFER_USAGE_VERTEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_SRC_BIT);
            vertexBuffer = MemoryUtillities.createBuffer(lDevice, (int) verticesSize,VK_BUFFER_USAGE_VERTEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT);

            MemoryUtillities.allocateBuffer(stagingBuffer, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
            MemoryUtillities.allocateBuffer(vertexBuffer, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);

            stagingBuffer.bind();
            vertexBuffer.bind();

            PointerBuffer data = stack.mallocPointer(1);

            vkMapMemory(lDevice, stagingBuffer.getMemory(), 0, stagingBuffer.getSize(), 0, data);
            {
                MemoryUtillities.memCopy(data.getByteBuffer(0, (int) stagingBuffer.getSize()), vertices);
            }
            vkUnmapMemory(lDevice, stagingBuffer.getMemory());

            MemoryUtillities.copyBuffer(transferQueue, transferPool, stagingBuffer, vertexBuffer);

            stagingBuffer.destroy();

            stagingBuffer = MemoryUtillities.createBuffer(lDevice, (int) indicesSize,VK_BUFFER_USAGE_INDEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_SRC_BIT);
            MemoryUtillities.allocateBuffer(stagingBuffer, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);

            indexBuffer = MemoryUtillities.createBuffer(lDevice, (int) indicesSize,VK_BUFFER_USAGE_INDEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT);
            MemoryUtillities.allocateBuffer(indexBuffer, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);

            stagingBuffer.bind();
            indexBuffer.bind();

            vkMapMemory(lDevice, stagingBuffer.getMemory(), 0, stagingBuffer.getSize(), 0, data);
            {
                MemoryUtillities.memCopy(   data.getByteBuffer(0, (int) stagingBuffer.getSize()), indices);
            }
            vkUnmapMemory(lDevice, stagingBuffer.getMemory());


            MemoryUtillities.copyBuffer(transferQueue, transferPool, stagingBuffer, indexBuffer);


            stagingBuffer.destroy();
        }
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public Buffer getVertexBuffer() {
        return vertexBuffer;
    }

    public long getVertexOffset(){
        return vertexOffset;
    }

    public short[] getIndices(){
        return indices;
    }

    public Buffer getIndexBuffer(){
        return indexBuffer;
    }

    public long getIndexOffset(){
        return indexOffset;
    }

    public void destroy(){
        //vertexBuffer.destroy();
        //indexBuffer.destroy();
    }
}