package Engine.Renderer.GraphicsPipeline;

import Engine.Geometry.Vertex;
import Engine.Renderer.RenderPass.RenderPass;
import Engine.Renderer.Swapchain.Swapchain;
import Engine.Renderer.Utilities.ErrorUtilities;
import Engine.Renderer.Utilities.RenderUtilities;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;


import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_glsl_fragment_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_glsl_vertex_shader;
import static org.lwjgl.vulkan.VK10.*;

public class GraphicsPipeline {

    private long graphicsPipelineLayout;

    private long graphicsPipeline;

    public GraphicsPipeline(){

    }

    public long create(VkDevice lDevice, Swapchain swapchain, RenderPass renderPass){

        try(MemoryStack stack = stackPush()){

            long vertexShaderModule = RenderUtilities.createShaderModule("Assets/Shaders/base.vert","main",shaderc_glsl_vertex_shader,lDevice);
            long fragmentShaderModule = RenderUtilities.createShaderModule("Assets/Shaders/base.frag","main2",shaderc_glsl_fragment_shader,lDevice);

            ByteBuffer entryPoint = stack.UTF8("main");

            VkPipelineShaderStageCreateInfo.Buffer shaderStagesCreateInfo = VkPipelineShaderStageCreateInfo.callocStack(2,stack);

            VkPipelineShaderStageCreateInfo vertexShaderStageCreateInfo = shaderStagesCreateInfo.get(0)
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                    .stage(VK_SHADER_STAGE_VERTEX_BIT)
                    .module(vertexShaderModule)
                    .pName(entryPoint);

            //Shaders

            VkPipelineShaderStageCreateInfo fragmentShaderStageCreateInfo = shaderStagesCreateInfo.get(1)
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                    .stage(VK_SHADER_STAGE_FRAGMENT_BIT)
                    .module(fragmentShaderModule)
                    .pName(entryPoint);

            //Vertex

            VkPipelineVertexInputStateCreateInfo VertexInputStateCreateInfo =  VkPipelineVertexInputStateCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
                    .pVertexBindingDescriptions(Vertex.getBindingDescription())
                    .pVertexAttributeDescriptions(Vertex.getAttributeDescription());

            //Assembly

            VkPipelineInputAssemblyStateCreateInfo inputAssemblyStateCreateInfo = VkPipelineInputAssemblyStateCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO)
                    .topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST)
                    .primitiveRestartEnable(false);

            //Views

            VkExtent2D extent = swapchain.getSwapchainExtent();

            VkViewport.Buffer viewport = VkViewport.callocStack(1,stack)
                    .x(0.0f)
                    .y(0.0f)
                    .width(extent.width())
                    .height(extent.height())
                    .minDepth(0.0f)
                    .maxDepth(1.0f);



            VkRect2D.Buffer scissor = VkRect2D.callocStack(1,stack);
            scissor.offset(VkOffset2D.callocStack(stack).set(0,0))
                    .extent(extent);



            VkPipelineViewportStateCreateInfo viewportStateCreateInfo = VkPipelineViewportStateCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO)
                    .pViewports(viewport)
                    .pScissors(scissor);

            //Rasterization

            VkPipelineRasterizationStateCreateInfo rasterizationStateCreateInfo = VkPipelineRasterizationStateCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO)
                    .depthClampEnable(false)
                    .rasterizerDiscardEnable(false)
                    .polygonMode(VK_POLYGON_MODE_FILL)
                    .lineWidth(1.0f)
                    .cullMode(VK_CULL_MODE_BACK_BIT)
                    .frontFace(VK_FRONT_FACE_CLOCKWISE)
                    .depthBiasEnable(false);

            //MULTISAMPLING

            VkPipelineMultisampleStateCreateInfo multisampling = VkPipelineMultisampleStateCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
                    .sampleShadingEnable(false)
                    .rasterizationSamples(VK_SAMPLE_COUNT_1_BIT);

            //COLOUR BLENDING

            VkPipelineColorBlendAttachmentState.Buffer colorBlendAttachment = VkPipelineColorBlendAttachmentState.callocStack(1, stack)
                    .colorWriteMask(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT | VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT)
                    .blendEnable(false);

            VkPipelineColorBlendStateCreateInfo colorBlending = VkPipelineColorBlendStateCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO)
                    .logicOpEnable(false)
                    .logicOp(VK_LOGIC_OP_COPY)
                    .pAttachments(colorBlendAttachment)
                    .blendConstants(stack.floats(0.0f, 0.0f, 0.0f, 0.0f));

            VkPipelineLayoutCreateInfo pipelineLayoutInfo = VkPipelineLayoutCreateInfo.callocStack(stack);
            pipelineLayoutInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO);

            LongBuffer pPipelineLayout = stack.longs(VK_NULL_HANDLE);

            int status = vkCreatePipelineLayout(lDevice, pipelineLayoutInfo, null, pPipelineLayout);

            if(status != VK_SUCCESS) {
                throw new RuntimeException("Failed to create pipeline layout");
            }

            graphicsPipelineLayout = pPipelineLayout.get(0);

            VkGraphicsPipelineCreateInfo.Buffer pipelineCreateInfo = VkGraphicsPipelineCreateInfo.callocStack(1,stack)
                    .sType(VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO)
                    .pStages(shaderStagesCreateInfo)
                    .pVertexInputState(VertexInputStateCreateInfo)
                    .pInputAssemblyState(inputAssemblyStateCreateInfo)
                    .pViewportState(viewportStateCreateInfo)
                    .pRasterizationState(rasterizationStateCreateInfo)
                    .pMultisampleState(multisampling)
                    .pDepthStencilState(null)
                    .pColorBlendState(colorBlending)
                    .pDepthStencilState(null)
                    .layout(graphicsPipelineLayout)
                    .renderPass(renderPass.getVkRenderPass())
                    .subpass(0);

            LongBuffer pGraphicsPipeline = stack.longs(VK_NULL_HANDLE);

            status = vkCreateGraphicsPipelines(lDevice,VK_NULL_HANDLE,pipelineCreateInfo,null,pGraphicsPipeline);

            if(status != VK_SUCCESS){
                throw new RuntimeException("Failed to create Graphics Pipeline: " + ErrorUtilities.getError(status));
            }

            graphicsPipeline = pGraphicsPipeline.get(0);

            vkDestroyShaderModule(lDevice,vertexShaderModule,null);
            vkDestroyShaderModule(lDevice,fragmentShaderModule,null);

        }

        return graphicsPipeline;

    }

    public void destroy(VkDevice lDevice){
        vkDestroyPipelineLayout(lDevice,graphicsPipelineLayout,null);
        vkDestroyPipeline(lDevice,graphicsPipeline,null);
    }

    public long getVkGraphicsPipeline() {
        return graphicsPipeline;
    }

    public long getVkGraphicsPipelineLayout(){
        return graphicsPipelineLayout;
    }
}
