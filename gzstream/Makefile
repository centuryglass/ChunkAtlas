# ============================================================================
# gzstream, C++ iostream classes wrapping the zlib compression library.
# Copyright (C) 2001  Deepak Bandyopadhyay, Lutz Kettner
# 
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
# 
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
# 
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
# ============================================================================
# 
# File          : Makefile
# Revision      : $Revision: 1.3 $
# Revision_date : $Date: 2001/10/04 15:09:28 $
# Author(s)     : Deepak Bandyopadhyay, Lutz Kettner
# 
# ============================================================================

# ----------------------------------------------------------------------------
# adapt these settings to your need:
# add '-DGZSTREAM_NAMESPACE=name' to CPPFLAGS to place the classes
# in its own namespace. Note, this macro needs to be set while creating
# the library as well while compiling applications based on it.
# As an alternative, gzstream.C and gzstream.h can be edited.
# ----------------------------------------------------------------------------

# Minor modifications made so that this makefile can be directly included in
# the MCMap makefile. -Anthony Brown

GZ_DIR:=$(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))
# CXX      = CC -n32 -LANG:std   # for SGI Irix 6.5, MIPSpro CC version 7.30
GZ_CXX      = g++   # for Linux RedHat 6.1, g++ version 2.95.2

GZ_CPPFLAGS := -I$(GZ_DIR) -O
GZ_LDFLAGS := -L$(GZ_DIR) -lgzstream -lz
GZ_AR       = ar cr

# ----------------------------------------------------------------------------
# plain simple rules to make and cleanup the library:
# make gz_default;   compiles the library
# make gz_test;      compiles and executes test. O.K. message marks success.
# make gz_clean;     removes temporary files
# make gz_cleanall;  removes temporary files, the library, and programs
# ----------------------------------------------------------------------------

.PHONY: gz_default gz_test gz_clean gz_cleanall

gz_default: $(GZ_DIR)/libgzstream.a

gz_test:    $(GZ_DIR)/test_gzip $(GZ_DIR)/test_gunzip
	$(GZ_DIR)/test_gzip $(GZ_DIR)/COPYING.LIB $(GZ_DIR)/gz.tmp.gz
	gunzip $(GZ_DIR)/gz.tmp.gz
	diff $(GZ_DIR)/COPYING.LIB $(GZ_DIR)/gz.tmp
	gzip $(GZ_DIR)/gz.tmp
	$(GZ_DIR)/test_gunzip $(GZ_DIR)/gz.tmp.gz $(GZ_DIR)/gz.tmp
	diff $(GZ_DIR)/COPYING.LIB $(GZ_DIR)/gz.tmp
	rm $(GZ_DIR)/gz.tmp.gz $(GZ_DIR)/gz.tmp
	# *** O.K. Test finished successfully. ***

$(GZ_DIR)/gzstream.o : $(GZ_DIR)/gzstream.C $(GZ_DIR)/gzstream.h
	$(GZ_CXX) $(CPPFLAGS) -c -o $(GZ_DIR)/gzstream.o $(GZ_DIR)/gzstream.C

$(GZ_DIR)/test_gzip.o : $(GZ_DIR)/test_gzip.C $(GZ_DIR)/gzstream.h
	$(GZ_CXX) $(CPPFLAGS) -c -o $(GZ_DIR)/test_gzip.o $(GZ_DIR)/test_gzip.C

$(GZ_DIR)/test_gunzip.o : $(GZ_DIR)/test_gunzip.C $(GZ_DIR)/gzstream.h
	$(GZ_CXX) $(CPPFLAGS) -c -o $(GZ_DIR)/test_gunzip.o $(GZ_DIR)/test_gunzip.C

$(GZ_DIR)/libgzstream.a : $(GZ_DIR)/gzstream.o
	$(GZ_AR) $(GZ_DIR)/libgzstream.a $(GZ_DIR)/gzstream.o

$(GZ_DIR)/test_gzip : $(GZ_DIR)/test_gzip.o $(GZ_DIR)/libgzstream.a
	$(GZ_CXX) -o $(GZ_DIR)/test_gzip $(GZ_DIR)/test_gzip.o $(GZ_LDFLAGS)

$(GZ_DIR)/test_gunzip : $(GZ_DIR)/test_gunzip.o $(GZ_DIR)/libgzstream.a
	$(GZ_CXX) -o test_gunzip test_gunzip.o $(GZ_LDFLAGS)

gz_clean :
	-rm $(GZ_DIR)/*.o

gz_cleanall :
	-rm $(GZ_DIR)/*.o $(GZ_DIR)/libgzstream.a $(GZ_DIR)/test_gzip \
    $(GZ_DIR)/test_gunzip

# ============================================================================
# EOF
