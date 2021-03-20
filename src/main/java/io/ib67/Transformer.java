package io.ib67;

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
import java.util.*;
import java.util.zip.CRC32;

public class Transformer implements ClassFileTransformer {
    private final List<String> blackListed;
    private final DateFormat sdf=SimpleDateFormat.getTimeInstance();
    private final String outputDir;
    private final HashSet<String> loadedCRC=new HashSet<>();
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
        System.out.println("Transform Class: "+s + " Time: "+sdf.format(new Date()));
        output(s,bytes,classLoader);
        if(!cls.contains(classLoader)){
            cls.add(classLoader);
            System.out.println("New classloader detected: "+classLoader+" parent:"+classLoader.getParent());
        }
        return bytes;
    }
    private void output(String className,byte[] data,ClassLoader cl){
        String realName=new ClassReader(data).getClassName();
        counter++;
        File f=new File(outputDir+cl.toString()+"/"+className.replaceAll("\\.","/").concat(".class"));
        f.getParentFile().mkdirs();
        try {
            int i=0;
            while(f.exists()){
                CRC32 c=new CRC32();
                c.update(data);
                long writingCRC32=c.getValue();
                if(loadedCRC.contains(className+writingCRC32)){
                    i++;
                    f=new File(outputDir+cl.toString()+"/"+className.replaceAll("\\.","/").concat(".class.")+i);
                    System.out.println("CONFLICT FILE: "+className+" CRC32: "+Long.toHexString(writingCRC32));
                }
        }
            CRC32 c=new CRC32();
            c.update(data);
            if(!loadedCRC.contains(className+c.getValue())){
                loadedCRC.add(className+c.getValue());
            }
            long originalCRC32=c.getValue();
            Files.write(f.toPath(), data);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
