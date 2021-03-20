package io.ib67.deobf;

import com.mojang.brigadier.Command;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Seeker {
    private String scanPath;
    private static final Pattern CS_INVOKE=Pattern.compile("(a\\.cs\\()(\\d*)(\\))");
    public Seeker(String scanPath){
        this.scanPath=scanPath;
    }
    public List<String> start(){
        List<String> result = new ArrayList<>();
        for (File listFile : FileUtils.listFiles(new File(scanPath), null, true)) {
            DeCompiler.decompile(listFile.getPath(),src->{
                Matcher matches=CS_INVOKE.matcher(src);
                    while(matches.find()){
                        result.add(matches.group(2));
                    }
            });
        }
        return result;
    }
}
