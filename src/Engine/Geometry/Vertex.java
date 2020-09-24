package Engine.Geometry;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.vulkan.VK10.*;

public class Vertex {

    private Vector3fc pos;
    private Vector3fc colour;

    private static int SizeOf = (3+3) * Float.BYTES; //one vertex is 6 floats 3 for pos 3 for col;

    private static final int POS_OFFSET = 0;
    private static final int COLOUR_OFFSET = 3 * Float.BYTES;

    private static VkVertexInputAttributeDescription.Buffer attributeDescriptions;

    private static VkVertexInputBindingDescription.Buffer bindingDescriptions;

    public Vertex(Vector3fc pos, Vector3fc colour){
        this.pos = pos;
        this.colour = colour;
    }

    public static void init(){

        bindingDescriptions = VkVertexInputBindingDescription.callocStack(1)
                .binding(0) //index in array of bindings
                .stride(SizeOf)
                .inputRate(VK_VERTEX_INPUT_RATE_VERTEX);

        attributeDescriptions = VkVertexInputAttributeDescription.callocStack(2);

        VkVertexInputAttributeDescription posDescription = attributeDescriptions.get(0)
                .binding(0)
                .location(0)
                .format(VK_FORMAT_R32G32B32_SFLOAT)
                .offset(POS_OFFSET);

        VkVertexInputAttributeDescription colourDescription = attributeDescriptions.get(1)
                .binding(0)
                .location(1)
                .format(VK_FORMAT_R32G32B32_SFLOAT)
                .offset(COLOUR_OFFSET);

        attributeDescriptions.rewind();
    }

    public static VkVertexInputBindingDescription.Buffer getBindingDescription(){
        return bindingDescriptions;
    }

    public static VkVertexInputAttributeDescription.Buffer getAttributeDescription() {
        return attributeDescriptions;
    }

    public static long getSize(){
        return SizeOf;
    }

    public Vector3fc getPos() {
        return pos;
    }

    public Vector3fc getColour() {
        return colour;
    }
}
