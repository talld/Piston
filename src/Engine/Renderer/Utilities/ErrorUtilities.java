package Engine.Renderer.Utilities;

public class ErrorUtilities {

    public static String getError(int result){
        switch (result) {
            case (1):
                return "VK_NOT_READY";
            case (2):
                return "VK_TIME_OUT";
            case (3):
                return "VK_EVENT_SET";
            case (4):
                return "VK_EVENT_RESET";
            case (5):
                return "VK_INCOMPLETE";
            case (-1):
                return "VK_ERROR_OUT_OF_HOST_MEMORY";
            case (-2):
                return "VK_ERROR_OUT_OF_DEVICE_MEMORY";
            case (-3):
                return "VK_ERROR_INITIALIZATION_FAILED";
            case (-4):
                return "VK_ERROR_DEVICE_LOST";
            case (-5):
                return "VK_ERROR_MEMORY_MAP_FAILED";
            case (-6):
                return "VK_ERROR_LAYER_NOT_PRESENT";
            case (-7):
                return "VK_ERROR_EXTENSION_NOT_PRESENT";
            case (-8):
                return "VK_ERROR_FEATURE_NOT_PRESENT";
            case (-9):
                return "VK_ERROR_INCOMPATIBLE_DRIVER";
            case (-10):
                return "VK_ERROR_TOO_MANY_OBJECTS";
            case (-11):
                return "VK_ERROR_FORMAT_NOT_SUPPORTED";
            case (-12):
                return "VK_ERROR_FRAGMENTED_POOL";
            case (-13):
                return "VK_ERROR_UNKNOWN";


        }
        return "";
    }

}
