package io.ib67;

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
        output(s,bytes);
        return bytes;
    }
    private void output(String className,byte[] data){
        File f=new File(outputDir+className.replaceAll("\\.","/").concat(".class"));
        f.getParentFile().mkdirs();
        try {
            Files.write(f.toPath(), data);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
