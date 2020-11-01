package io.ib67;

import com.sun.tools.attach.VirtualMachine;

import java.net.URLDecoder;

public class Injector {
    private int pid;
    public Injector(int pid){
        this.pid=pid;
    }
    public void inject(){
        try {
            String path = Agent.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            VirtualMachine vm = VirtualMachine.attach(String.valueOf(pid));
            vm.loadAgent(decodedPath);
            vm.detach();
        }catch(Exception e){
            System.err.println("Failed to inject!");
            e.printStackTrace();
        }
    }
}

