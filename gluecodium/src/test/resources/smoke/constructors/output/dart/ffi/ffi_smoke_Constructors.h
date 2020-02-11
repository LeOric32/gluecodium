#pragma once
#include "Export.h"
#include "OpaqueHandle.h"
#include <stdint.h>
#ifdef __cplusplus
extern "C" {
#endif
_GLUECODIUM_FFI_EXPORT FfiOpaqueHandle smoke_Constructors_create();
_GLUECODIUM_FFI_EXPORT FfiOpaqueHandle smoke_Constructors_create__Constructors(FfiOpaqueHandle other);
_GLUECODIUM_FFI_EXPORT FfiOpaqueHandle smoke_Constructors_create__String_ULong(FfiOpaqueHandle foo, uint64_t bar);
_GLUECODIUM_FFI_EXPORT void smoke_Constructors_create__String_return_release_handle(FfiOpaqueHandle handle);
_GLUECODIUM_FFI_EXPORT FfiOpaqueHandle smoke_Constructors_create__String_return_get_result(FfiOpaqueHandle handle);
_GLUECODIUM_FFI_EXPORT uint32_t smoke_Constructors_create__String_return_get_error(FfiOpaqueHandle handle);
_GLUECODIUM_FFI_EXPORT bool smoke_Constructors_create__String_return_has_error(FfiOpaqueHandle handle);
_GLUECODIUM_FFI_EXPORT FfiOpaqueHandle smoke_Constructors_create__String(FfiOpaqueHandle input);
_GLUECODIUM_FFI_EXPORT FfiOpaqueHandle smoke_Constructors_create__ListOf_1Double(FfiOpaqueHandle input);
_GLUECODIUM_FFI_EXPORT void smoke_Constructors_release_handle(FfiOpaqueHandle handle);
_GLUECODIUM_FFI_EXPORT FfiOpaqueHandle smoke_Constructors_ErrorEnum_create_handle_nullable(uint32_t value);
_GLUECODIUM_FFI_EXPORT void smoke_Constructors_ErrorEnum_release_handle_nullable(FfiOpaqueHandle handle);
_GLUECODIUM_FFI_EXPORT uint32_t smoke_Constructors_ErrorEnum_get_value_nullable(FfiOpaqueHandle handle);
#ifdef __cplusplus
}
#endif
