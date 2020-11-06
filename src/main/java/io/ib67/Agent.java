package io.ib67;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.instrument.Instrumentation;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Agent {
    public static void agentmain(String agentArgs,Instrumentation instru) {
        System.out.println("Agent loading with args: "+agentArgs);
        instru.addTransformer(new Transformer(getBlocked(agentArgs+"class-bl.json"), agentArgs+"vapu_dump_out/"));
    }
    private static List<String> getBlocked(String path){
        try {
            return new Gson().fromJson(new FileReader(new File(path)),new TypeToken<List<String>>(){}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Cannot read blocklists.");
        return Collections.EMPTY_LIST;
    }
}
