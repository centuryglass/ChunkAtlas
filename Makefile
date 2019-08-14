###################### Initialize project variables: ##########################
# Executable name:
TARGET_APP = MCMap
# Build type: either Debug or Release
CONFIG?=Release
# Enable or disable verbose output
VERBOSE?=0
V_AT:=$(shell if [ $(VERBOSE) != 1 ]; then echo '@'; fi)


# Project directories:
PROJECT_DIR:=$(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))
SOURCE_DIR:=$(PROJECT_DIR)/Source
BUILD_DIR:=$(PROJECT_DIR)/build/$(CONFIG)
OBJDIR:=$(BUILD_DIR)/intermediate

TARGET_BUILD_PATH:=$(PROJECT_DIR)/$(TARGET_APP)

# Command used to strip unneeded symbols from object files:
STRIP?=strip

# Use the build system's architecture by default.
TARGET_ARCH?=-march=native

# Command used to clean out build files:
CLEANCMD:=rm -rf $(OBJDIR)


########################## Primary build target: ##############################
$(TARGET_BUILD_PATH) : build
	@echo Linking "$(TARGET_APP):"
	$(V_AT)$(CXX) $(LINK_ARGS)

############################### Set build flags: ##############################
#### Config-specific flags: ####
ifeq ($(CONFIG),Debug)
    OPTIMIZATION?=0
    GDB_SUPPORT?=1
    # Debug-specific preprocessor definitions:
    CONFIG_FLAGS=-DDEBUG=1
endif

ifeq ($(CONFIG),Release)
    OPTIMIZATION?=1
    GDB_SUPPORT?=0
endif

# Set optimization level flags:
ifeq ($(OPTIMIZATION),1)
    CONFIG_CFLAGS=-O3 -flto
    CONFIG_LDFLAGS:=-flto
else
    CONFIG_CFLAGS=-O0
endif

# Set debugger flags:
ifeq ($(GDB_SUPPORT),1)
    CONFIG_CFLAGS:=$(CONFIG_CFLAGS) -g -ggdb
else
    CONFIG_LDFLAGS:=$(CONFIG_LDFLAGS) -fvisibility=hidden
endif

#### C compilation flags: ####
CFLAGS:=$(TARGET_ARCH) $(CONFIG_CFLAGS) $(CFLAGS)

#### C++ compilation flags: ####
CXXFLAGS:=-std=gnu++17 $(CXXFLAGS)

#### C Preprocessor flags: ####

# Include directories:
INCLUDE_FLAGS:=-I$(SOURCE_DIR) \
               -I$(SOURCE_DIR)/Mapping \
               -I$(SOURCE_DIR)/SaveData \
               -I$(SOURCE_DIR)/WorldInfo \
               $(INCLUDE_FLAGS)

# Disable dependency generation if multiple architectures are set
DEPFLAGS:=$(if $(word 2, $(TARGET_ARCH)), , -MMD)

PKG_CONFIG_LIBS=libpng zlib

ifeq ($(VERBOSE),1)
    DEFINE_FLAGS:=-DVERBOSE=1
endif

CPPFLAGS:=-pthread \
          $(DEPFLAGS) \
          $(CONFIG_FLAGS) \
          $(DEFINE_FLAGS) \
          $(INCLUDE_FLAGS) \
          $(shell pkg-config --cflags $(PKG_CONFIG_LIBS)) \
          $(CPPFLAGS)

#### Linker flags: ####
        
LDFLAGS := -lpthread $(TARGET_ARCH) $(CONFIG_LDFLAGS) \
            $(shell pkg-config --libs $(PKG_CONFIG_LIBS)) \
            $(GZ_LDFLAGS) \
	        $(LDFLAGS)

#### Aggregated build arguments: ####

# Map objects use data to draw map images.
MAP_OBJECTS:=$(OBJDIR)/MapImage.o \
             $(OBJDIR)/Mapper.o \
             $(OBJDIR)/BasicMapper.o \
             $(OBJDIR)/BiomeMapper.o \
             $(OBJDIR)/ActivityMapper.o \
             $(OBJDIR)/StructureMapper.o \
             $(OBJDIR)/DirectoryMapper.o \
             $(OBJDIR)/MapCollector.o


# Data objects extract information from Minecraft region files.
DATA_OBJECTS:=$(OBJDIR)/ChunkNBT.o \
              $(OBJDIR)/MCAFile.o

# World objects store information about a Minecraft world.
WORLD_OBJECTS:=$(OBJDIR)/ChunkData.o \
               $(OBJDIR)/Biome.o \
               $(OBJDIR)/Structure.o \

OBJECTS:=$(MAP_OBJECTS) \
         $(DATA_OBJECTS) \
         $(WORLD_OBJECTS) \
         $(OBJDIR)/Main.o \
         $(OBJECTS)


# Complete set of flags used to compile source files:
BUILD_FLAGS:=$(CFLAGS) $(CXXFLAGS) $(CPPFLAGS)

# Complete set of arguments used to link the program:
LINK_ARGS:= -o $(TARGET_BUILD_PATH) $(OBJECTS) $(LDFLAGS)

###################### Supporting Build Targets: ##############################
.PHONY: build clean

build : $(OBJECTS)

clean :
	-$(V_AT)rm -rf $(PROJECT_DIR)/build
	-$(V_AT)rm -f $(TARGET_BUILD_PATH)

$(OBJECTS) :
	@echo "Compiling $(<F):"
	$(V_AT)mkdir -p $(OBJDIR)
	$(V_AT)$(CXX) $(BUILD_FLAGS) -o "$@" -c "$<"

-include $(OBJECTS:%.o=%.d)

# Map Objects:
$(OBJDIR)/MapImage.o: \
	$(SOURCE_DIR)/Mapping/MapImage.cpp
$(OBJDIR)/Mapper.o: \
	$(SOURCE_DIR)/Mapping/Mapper.cpp
$(OBJDIR)/BasicMapper.o: \
	$(SOURCE_DIR)/Mapping/BasicMapper.cpp
$(OBJDIR)/BiomeMapper.o: \
	$(SOURCE_DIR)/Mapping/BiomeMapper.cpp
$(OBJDIR)/ActivityMapper.o: \
	$(SOURCE_DIR)/Mapping/ActivityMapper.cpp
$(OBJDIR)/StructureMapper.o: \
	$(SOURCE_DIR)/Mapping/StructureMapper.cpp
$(OBJDIR)/DirectoryMapper.o: \
	$(SOURCE_DIR)/Mapping/DirectoryMapper.cpp
$(OBJDIR)/MapCollector.o: \
	$(SOURCE_DIR)/Mapping/MapCollector.cpp

# Data Objects:
$(OBJDIR)/MCAFile.o: \
	$(SOURCE_DIR)/SaveData/MCAFile.cpp
$(OBJDIR)/ChunkNBT.o: \
	$(SOURCE_DIR)/SaveData/ChunkNBT.cpp

# World Objects:
$(OBJDIR)/ChunkData.o: \
	$(SOURCE_DIR)/WorldInfo/ChunkData.cpp
$(OBJDIR)/Biome.o: \
	$(SOURCE_DIR)/WorldInfo/Biome.cpp
$(OBJDIR)/Structure.o: \
	$(SOURCE_DIR)/WorldInfo/Structure.cpp

$(OBJDIR)/Main.o: \
	$(SOURCE_DIR)/Main.cpp
