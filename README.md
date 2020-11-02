# Piston
Java based Vulkan engine using the LWJGL Bindings for use in visual toolchain and games-based applications

# Overview
A simple rendering application designed for small games and more primarily Toolchain projects such as asset creation and shader design

# Current features
* Single Camera with support for single Descriptor Set
* Runtime compilation of SPIR V and attachment of said byte code to shaders
* Basic GLFW input 
* Client and server skeleton for possible network features 
# Planned features
* text processor for user input
* Multiple Camera and Descriptor Set support
	* Descriptor Set creation with projection from user input 
	* Multiple on-screen viewports with Multiple Cameras
* Runtime shader creation from user input
	* Graphics recreation with new shaders
* Compute shaders
* Texture mapping
* Vertices and indices from mouse input/manual value entry 
* multidata file output (similar to .WAD)
