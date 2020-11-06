package io.ib67;

import java.lang.instrument.Instrumentation;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class Agent {
    public static void agentmain(String agentArgs,Instrumentation instru) {
        System.out.println("Agent loading with args: "+agentArgs);
        instru.addTransformer(new Transformer(instru.getAllLoadedClasses(), agentArgs));
    }
}
