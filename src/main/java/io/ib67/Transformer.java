package io.ib67;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Transformer implements ClassFileTransformer {
    private final List<String> blackListed;
    private final DateFormat sdf=SimpleDateFormat.getTimeInstance();
    private final String outputDir;
    private int counter=0;
    private List<ClassLoader> cls=new ArrayList();
    public Transformer(List<String> blackListed,String path){
        System.out.println("Vapu Dumper Injected! Loading");
        outputDir=path;
        this.blackListed=blackListed;
    }
    @Override
    public byte[] transform(ClassLoader classLoader, String s, Class<?> aClass, ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
        if(blackListed.contains(s) || s.startsWith("java") || s.startsWith("net/minecraft") || s.startsWith("sun") || s.startsWith("com/sun") || s.startsWith("jdk")){
            return bytes;
        }
        if(!cls.contains(classLoader)){
            cls.add(classLoader);
            System.out.println("New classloader detected: "+classLoader+" parent:"+classLoader.getParent());
        }
        ClassReader cr=new ClassReader(bytes);
        System.out.println("Transform Class: "+cr.getClassName() + " Time: "+sdf.format(new Date()));
        output(cr.getClassName(),bytes);
        return bytes;
    }
    private void output(String className,byte[] data){
        System.out.println("class "+(++counter));
        File f=new File(outputDir+className.concat(".class"));
        //File f=new File(outputDir+(++counter)+".class");
        f.getParentFile().mkdirs();
        try {
            System.out.println("Writing to "+f.toPath().toString());
            if(f.exists()){
               System.err.println("ERR: File "+f+" already Exists.");
               System.err.println("HashCode: "+data.hashCode() + " Original File: "+FileUtils.readFileToByteArray(f).hashCode());
               return;
            }
            FileUtils.writeByteArrayToFile(f, data);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
