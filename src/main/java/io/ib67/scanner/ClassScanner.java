package io.ib67.scanner;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClassScanner {
    private final String rootDir;
    public ClassScanner(String rootDir){
        this.rootDir=rootDir;
    }
    public Set<String> scan(){
        HashSet set=new HashSet();
        for(File file: FileUtils.listFiles(new File(rootDir),new String[]{"jar"},true)){
            try {
                System.out.println("Scanning: "+file.toPath().toString());
                set.addAll(readJarFile(file));
            }catch(IOException e){
                System.err.println("Failed to load: "+file.toURI().toString());
                System.err.println("Reason: "+e.getMessage());
            }
        }
        return set;
    }
    private static Set<String> readJarFile( File ppath) throws IOException {
        HashSet<String> set=new HashSet();
        ZipFile zipFile = new ZipFile(ppath);
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.endsWith(".class") && getClassNameLen(name)>2+1+5) { //(2 char).class
                set.add(name.replace(".class", ""));
            }
        }
        return set;
    }
    private static int getClassNameLen(String originalName){
        String[] s=originalName.split("\\$");
        if(s.length!=0){
            return s[0].length();
        }
        return originalName.length();
    }
}
