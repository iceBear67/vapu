package io.ib67;

import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;
import java.security.ProtectionDomain;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.CRC32;

public class Transformer implements ClassFileTransformer {
    private final List<String> blackListed;
    private final DateFormat sdf=SimpleDateFormat.getTimeInstance();
    private final String outputDir;
    private List<ClassLoader> cls=new ArrayList();
    private Lock lock= new ReentrantLock();
    private HashSet<String> transformedClass=new HashSet<>();
    private boolean foundDuplicateClass=false;
    private Executor writingQueue= Executors.newFixedThreadPool(4);
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
        writingQueue.execute(()->{
            lock.lock();
            boolean dup=transformedClass.contains(s+classLoader.toString());
            if(!transformedClass.contains(s+classLoader.toString())){
                transformedClass.add(s+classLoader.toString());
            }else {
                if(!foundDuplicateClass){
                    foundDuplicateClass=true;
                    System.out.println("First Duplicate Class Found: "+s +" Time:"+sdf.format(new Date()));
                }
            }
            System.out.println("Transform Class: "+s + " Time: "+sdf.format(new Date()) +(dup?"[Dup]":""));
            output(s,bytes,classLoader);
            if(!cls.contains(classLoader)){
                cls.add(classLoader);
                System.out.println("New classloader detected: "+classLoader+" parent:"+classLoader.getParent());
            }
            lock.unlock();
        });
        return bytes;
    }
    private void output(String className,byte[] data,ClassLoader cl){
        String realName=new ClassReader(data).getClassName();
        //As a path.
        String concat = className.replaceAll("\\.", "/")+realName+"."+encodeCRC32(data)+".class";
        File f=new File(outputDir+"/"+cl.toString()+"/"+ concat);
        f.getParentFile().mkdirs();
        try {
            Files.write(f.toPath(), data);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static String encodeCRC32(byte[] data) {
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return Long.toHexString(crc32.getValue());
    }
}
