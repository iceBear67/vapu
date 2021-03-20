package io.ib67.deobf;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;

public class ObserverTransformer implements ClassFileTransformer {
    public ClassLoader lastActivatedCL;
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if(loader instanceof SecureClassLoader){
            lastActivatedCL=loader;
        }
        return classfileBuffer;
    }
}
