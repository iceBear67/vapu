package io.ib67.deobf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.ib67.Agent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObfMapGen {
    public static void generate(String intListLoc) throws Exception{
        Map<String,String> map=new HashMap<>();
        List<String> context= new Gson().fromJson(new String(Files.readAllBytes(Paths.get(intListLoc))),new TypeToken<List<String>>(){}.getType());

        Class<?> aclazz=null;
        for (Class allLoadedClass : Agent.instru.getAllLoadedClasses()) {
            if("a.a".equals(allLoadedClass.getCanonicalName())){
                aclazz=allLoadedClass;
            }
        }
        Method method=null;
        for (Method declaredMethod : aclazz.getDeclaredMethods()) {
            if (declaredMethod.getName().equals("cs")) {
                method=declaredMethod;
            }
        }
        Method finalMethod = method;
        context.forEach(e->{
            try {
                map.put(e, (String) finalMethod.invoke(null,Integer.parseInt(e)));
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            } catch (InvocationTargetException invocationTargetException) {
                invocationTargetException.printStackTrace();
            }
        });
        Files.write(Paths.get("obfmap.json"),new Gson().toJson(map).getBytes(StandardCharsets.UTF_8));
        System.out.println("Saved as:"+Paths.get("obfmap.json"));
    }
}
