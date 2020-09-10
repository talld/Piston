package Engine.Renderer.Utilities;

import org.lwjgl.vulkan.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.*;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.util.shaderc.Shaderc.*;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_into_spv;
import static org.lwjgl.vulkan.VK10.*;

public class EngineUtilities {

    private static long createCompiler() {
        long options = shaderc_compile_options_initialize();          //init shaderC compiler basic settings;
        shaderc_compile_options_set_generate_debug_info(options);     //create compiler to create with default settings
        long compiler = shaderc_compiler_initialize();                //compiler would default settings any way this is just to make adding settings later easier
        if (compiler == NULL) {
            throw new IllegalStateException("failed to create compiler");
        }
        return compiler;
    }

    private static String getFileSource(String path) {
        try {
            InputStream in = new FileInputStream(path);
            int size = in.available();
            byte[] b = new byte[size];                      //byte array for file bytes
            in.read(b, 0, size);
            return new String(b, StandardCharsets.UTF_8);   //file in encoded in UTF-8
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static long createShaderModule(String path, String name, int stage, VkDevice device) {
        VkShaderModuleCreateInfo cInfo = VkShaderModuleCreateInfo.calloc();
        long c = createCompiler();
        cInfo.sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO);
        long result = shaderc_compile_into_spv(c, getFileSource(path), stage, name, "main", 0l);
        cInfo.pCode(shaderc_result_get_bytes(result));                           //stores results from file reading in buffer then gives said buffer as code pointer
        LongBuffer pModule = memAllocLong(1);
        int check = vkCreateShaderModule(device, cInfo, null, pModule);
        if (check != VK_SUCCESS) {
            throw new IllegalStateException("Failed to create shader:" + name);
        }
        long module = pModule.get();
        memFree(pModule);
        return module;
    }


}