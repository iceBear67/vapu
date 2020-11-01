package io.ib67;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Transformer implements ClassFileTransformer {
    private final List<String> blackListed=new ArrayList<>();
    private final Path outputDir;
    public Transformer(Class<?>[] blackListed,Path path){
        outputDir=path;
        for (Class<?> aClass : blackListed) {
                this.blackListed.add(aClass.getCanonicalName());
        }
    }
    @Override
    public byte[] transform(ClassLoader classLoader, String s, Class<?> aClass, ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
        if(blackListed.contains(s)){
            return bytes;
        }
        output(s,bytes);
        return bytes;
    }
    private void output(String className,byte[] data){
        File f=new File(outputDir.toString()+className.replaceAll("\\.","/").concat(".class"));
        f.getParentFile().mkdirs();
        try {
            Files.write(f.toPath(), data);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
