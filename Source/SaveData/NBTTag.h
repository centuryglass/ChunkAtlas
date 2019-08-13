/**
 * @file NBTTag.h
 *
 * @brief  Lists NBT tag types.
 */

enum class NBTTag
{
    tEnd = 0,
    tByte,
    tShort,
    tInt,
    tLong,
    tFloat,
    tDouble,
    tByteArray,
    tString,
    tList,
    tCompound,
    tIntArray,
    tLongArray
};
