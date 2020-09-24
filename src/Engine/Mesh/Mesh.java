package Engine.Mesh;

import Engine.Geometry.Vertex;
import Engine.Memory.Buffer;
import Engine.Memory.MemoryUtillities;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkDevice;

import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.vulkan.VK10.*;

public class Mesh {

    private VkDevice lDevice;

    private Buffer vertexBuffer;

    private long size;

    private Vertex[] vertices;

    public Mesh(VkDevice lDevice){
        this.lDevice = lDevice;
    }

    public void create(Vertex[] vertices){

        this.vertices = vertices;

        size = vertices.length * Vertex.getSize();

        vertexBuffer = MemoryUtillities.createBuffer(lDevice, size, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);

        MemoryUtillities.allocateMemory(vertexBuffer,VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);

        PointerBuffer data = stackGet().mallocPointer(1);

        vkMapMemory(lDevice,vertexBuffer.getMemory(), 0, vertexBuffer.getSize(), 0, data);

        MemoryUtillities.memcpyVertex(data.getByteBuffer(0, (int) vertexBuffer.getSize()), vertices);

        vkUnmapMemory(lDevice, vertexBuffer.getMemory());
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public Buffer getVertexBuffer() {
        return vertexBuffer;
    }
}
