/*
 *

 */
package com.example.smoke;
public final class ChildClassFromClass extends ParentClass {
    /**
     * For internal use only.
     * @exclude
     */
    protected ChildClassFromClass(final long nativeHandle, final Object dummy) {
        super(nativeHandle, dummy);
    }
    public native void childClassMethod();
}
