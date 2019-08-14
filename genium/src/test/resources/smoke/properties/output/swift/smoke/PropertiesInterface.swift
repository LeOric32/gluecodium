//
//
// Automatically generated. Do not modify. Your changes will be lost.
import Foundation
internal func getRef(_ ref: PropertiesInterface?, owning: Bool = true) -> RefHolder {
    guard let reference = ref else {
        return RefHolder(0)
    }
    if let instanceReference = reference as? NativeBase {
        let handle_copy = smoke_PropertiesInterface_copy_handle(instanceReference.c_handle)
        return owning
            ? RefHolder(ref: handle_copy, release: smoke_PropertiesInterface_release_handle)
            : RefHolder(handle_copy)
    }
    var functions = smoke_PropertiesInterface_FunctionTable()
    functions.swift_pointer = Unmanaged<AnyObject>.passRetained(reference).toOpaque()
    functions.release = {swift_class_pointer in
        if let swift_class = swift_class_pointer {
            Unmanaged<AnyObject>.fromOpaque(swift_class).release()
        }
    }
    functions.smoke_PropertiesInterface_structProperty_get = {(swift_class_pointer) in
        let swift_class = Unmanaged<AnyObject>.fromOpaque(swift_class_pointer!).takeUnretainedValue() as! PropertiesInterface
        return copyToCType(swift_class.structProperty).ref
    }
    functions.smoke_PropertiesInterface_structProperty_set = {(swift_class_pointer, newValue) in
        let swift_class = Unmanaged<AnyObject>.fromOpaque(swift_class_pointer!).takeUnretainedValue() as! PropertiesInterface
        swift_class.structProperty = moveFromCType(newValue)
    }
    let proxy = smoke_PropertiesInterface_create_proxy(functions)
    return owning ? RefHolder(ref: proxy, release: smoke_PropertiesInterface_release_handle) : RefHolder(proxy)
}
public protocol PropertiesInterface : AnyObject {
    var structProperty: ExampleStruct { get set }
}
internal class _PropertiesInterface: PropertiesInterface {
    var structProperty: ExampleStruct {
        get {
            return moveFromCType(smoke_PropertiesInterface_structProperty_get(self.c_instance))
        }
        set {
            let c_newValue = moveToCType(newValue)
            return moveFromCType(smoke_PropertiesInterface_structProperty_set(self.c_instance, c_newValue.ref))
        }
    }
    let c_instance : _baseRef
    init(cPropertiesInterface: _baseRef) {
        guard cPropertiesInterface != 0 else {
            fatalError("Nullptr value is not supported for initializers")
        }
        c_instance = cPropertiesInterface
    }
    deinit {
        smoke_PropertiesInterface_release_handle(c_instance)
    }
}
extension _PropertiesInterface: NativeBase {
    var c_handle: _baseRef { return c_instance }
}
internal func PropertiesInterfacecopyFromCType(_ handle: _baseRef) -> PropertiesInterface {
    if let swift_pointer = smoke_PropertiesInterface_get_swift_object_from_cache(handle),
        let re_constructed = Unmanaged<AnyObject>.fromOpaque(swift_pointer).takeUnretainedValue() as? PropertiesInterface {
        return re_constructed
    }
    return _PropertiesInterface(cPropertiesInterface: smoke_PropertiesInterface_copy_handle(handle))
}
internal func PropertiesInterfacemoveFromCType(_ handle: _baseRef) -> PropertiesInterface {
    if let swift_pointer = smoke_PropertiesInterface_get_swift_object_from_cache(handle),
        let re_constructed = Unmanaged<AnyObject>.fromOpaque(swift_pointer).takeUnretainedValue() as? PropertiesInterface {
        smoke_PropertiesInterface_release_handle(handle)
        return re_constructed
    }
    return _PropertiesInterface(cPropertiesInterface: handle)
}
internal func PropertiesInterfacecopyFromCType(_ handle: _baseRef) -> PropertiesInterface? {
    guard handle != 0 else {
        return nil
    }
    return PropertiesInterfacemoveFromCType(handle) as PropertiesInterface
}
internal func PropertiesInterfacemoveFromCType(_ handle: _baseRef) -> PropertiesInterface? {
    guard handle != 0 else {
        return nil
    }
    return PropertiesInterfacemoveFromCType(handle) as PropertiesInterface
}
internal func copyToCType(_ swiftClass: PropertiesInterface) -> RefHolder {
    return getRef(swiftClass, owning: false)
}
internal func moveToCType(_ swiftClass: PropertiesInterface) -> RefHolder {
    return getRef(swiftClass, owning: true)
}
internal func copyToCType(_ swiftClass: PropertiesInterface?) -> RefHolder {
    return getRef(swiftClass, owning: false)
}
internal func moveToCType(_ swiftClass: PropertiesInterface?) -> RefHolder {
    return getRef(swiftClass, owning: true)
}
public struct ExampleStruct {
    public var value: Double
    public init(value: Double) {
        self.value = value
    }
    internal init(cHandle: _baseRef) {
        value = moveFromCType(smoke_PropertiesInterface_ExampleStruct_value_get(cHandle))
    }
}
internal func copyFromCType(_ handle: _baseRef) -> ExampleStruct {
    return ExampleStruct(cHandle: handle)
}
internal func moveFromCType(_ handle: _baseRef) -> ExampleStruct {
    defer {
        smoke_PropertiesInterface_ExampleStruct_release_handle(handle)
    }
    return copyFromCType(handle)
}
internal func copyToCType(_ swiftType: ExampleStruct) -> RefHolder {
    let c_value = moveToCType(swiftType.value)
    return RefHolder(smoke_PropertiesInterface_ExampleStruct_create_handle(c_value.ref))
}
internal func moveToCType(_ swiftType: ExampleStruct) -> RefHolder {
    return RefHolder(ref: copyToCType(swiftType).ref, release: smoke_PropertiesInterface_ExampleStruct_release_handle)
}
internal func copyFromCType(_ handle: _baseRef) -> ExampleStruct? {
    guard handle != 0 else {
        return nil
    }
    let unwrappedHandle = smoke_PropertiesInterface_ExampleStruct_unwrap_optional_handle(handle)
    return ExampleStruct(cHandle: unwrappedHandle) as ExampleStruct
}
internal func moveFromCType(_ handle: _baseRef) -> ExampleStruct? {
    defer {
        smoke_PropertiesInterface_ExampleStruct_release_optional_handle(handle)
    }
    return copyFromCType(handle)
}
internal func copyToCType(_ swiftType: ExampleStruct?) -> RefHolder {
    guard let swiftType = swiftType else {
        return RefHolder(0)
    }
    let c_value = moveToCType(swiftType.value)
    return RefHolder(smoke_PropertiesInterface_ExampleStruct_create_optional_handle(c_value.ref))
}
internal func moveToCType(_ swiftType: ExampleStruct?) -> RefHolder {
    return RefHolder(ref: copyToCType(swiftType).ref, release: smoke_PropertiesInterface_ExampleStruct_release_optional_handle)
}