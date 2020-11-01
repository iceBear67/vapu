package io.ib67;

import java.lang.instrument.Instrumentation;
import java.nio.file.Paths;

public class Agent {
    public static void agentmain(Instrumentation instru){
        instru.addTransformer(new Transformer(instru.getAllLoadedClasses(), Paths.get("vapu_dump_out")));
    }
}
