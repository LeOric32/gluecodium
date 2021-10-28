//
//
#pragma once
#ifdef __cplusplus
extern "C" {
#endif
#include "cbridge/include/BaseHandle.h"
#include "cbridge/include/Export.h"
#include <stdint.h>
_GLUECODIUM_C_EXPORT void smoke_EnumsInTypeCollectionInterface_release_handle(_baseRef handle);
_GLUECODIUM_C_EXPORT _baseRef smoke_EnumsInTypeCollectionInterface_copy_handle(_baseRef handle);
_GLUECODIUM_C_EXPORT const void* smoke_EnumsInTypeCollectionInterface_get_swift_object_from_wrapper_cache(_baseRef handle);
_GLUECODIUM_C_EXPORT void smoke_EnumsInTypeCollectionInterface_cache_swift_object_wrapper(_baseRef handle, const void* swift_pointer);
_GLUECODIUM_C_EXPORT void smoke_EnumsInTypeCollectionInterface_remove_swift_object_from_wrapper_cache(_baseRef handle);
_GLUECODIUM_C_EXPORT uint32_t smoke_EnumsInTypeCollectionInterface_flipEnumValue(uint32_t input);
#ifdef __cplusplus
}
#endif
