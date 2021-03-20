package io.ib67;

import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.net.URLDecoder;
import java.nio.file.Paths;

public class Injector {
    private int pid;
    public Injector(int pid){
        this.pid=pid;
    }
    public void inject(String path,String arg){
        try {
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            System.out.println("Loading agent: "+decodedPath);
            VirtualMachine vm = VirtualMachine.attach(String.valueOf(pid));
            vm.loadAgent(decodedPath, arg);
            vm.detach();
            System.out.println("Injection succeed! Output dir: vapu_dump_out/");
        }catch(Exception e){
            System.err.println("Failed to inject!");
            e.printStackTrace();
        }
    }
}

