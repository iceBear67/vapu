package io.ib67;

import com.sun.tools.attach.VirtualMachine;

import java.net.URLDecoder;
import java.nio.file.Paths;

public class Injector {
    private int pid;
    public Injector(int pid){
        this.pid=pid;
    }
    public void inject(String path){
        try {
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            System.out.println("Loading agent: "+decodedPath);
            VirtualMachine vm = VirtualMachine.attach(String.valueOf(pid));
            String out=Paths.get("vapu_dump_out/").toAbsolutePath().toString();
            vm.loadAgent(decodedPath, out+"/");
            vm.detach();
            System.out.println("Inject succeed! Output dir:"+out);
        }catch(Exception e){
            System.err.println("Failed to inject!");
            e.printStackTrace();
        }
    }
}

