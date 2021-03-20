package io.ib67;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.ib67.deobf.Seeker;
import io.ib67.scanner.ClassScanner;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class Vapu{
    private static final CommandDispatcher<String> dispatcher=new CommandDispatcher<>();
    private static final Gson gson=new GsonBuilder().setPrettyPrinting().create();
    public static void main(String[] args){
        dispatcher.register(
                LiteralArgumentBuilder.<String>literal("inject")
                .then(
                        RequiredArgumentBuilder.<String,Integer>argument("pid",integer()).executes(s->{
                            new Injector(getInteger(s,"pid")).inject(Agent.class.getProtectionDomain().getCodeSource().getLocation().getFile().replaceFirst("\\/",""),new File(".").getAbsolutePath()+"\\");
                            return 0; }
                        )
                )
        );
        dispatcher.register(
                LiteralArgumentBuilder.<String>literal("obfgen")
                        .then(
                                RequiredArgumentBuilder.<String,Integer>argument("pid",integer()).executes(s->{
                                    new Injector(getInteger(s,"pid")).inject(Agent.class.getProtectionDomain().getCodeSource().getLocation().getFile().replaceFirst("\\/",""),"obfgen");
                                    return 0; }
                                )
                        )
        );
        dispatcher.register(
                LiteralArgumentBuilder.<String>literal("scan")
                .then(
                        RequiredArgumentBuilder.<String,String>argument("path",string()).executes(s->{
                            Set<String> result=new ClassScanner(getString(s,"path")).scan();
                            System.out.println("Scan finished!"+result.size()+" classes was found.");
                            JsonArray jsonArray=new JsonArray();
                            result.forEach(jsonArray::add);
                            try {
                                Files.write(Paths.get("./class-bl.json"),gson.toJson(jsonArray).getBytes());
                            } catch (IOException e) {
                                System.err.println("Failed to write database!");
                                e.printStackTrace();
                            }
                            return 0;
                        })
                )
        );
        dispatcher.register(
                LiteralArgumentBuilder.<String>literal("namefix").executes(s->{
                    File temp=new File("vapu_dump_out/");
                    File out=new File("out");
                    for (File file : FileUtils.listFiles(temp, null, true)) {
                        if(file.getName().endsWith("class")){
                            try {
                                System.out.println("Loading..." + file.getName());
                                String newName = new ClassReader(new FileInputStream(file)).getClassName();
                                System.out.println("Mapping: " + file.getName() + " to " + newName + ".class");
                                File destin = new File(out + "/" + newName + ".class");
                                destin.getParentFile().mkdirs();
                                FileUtils.copyFile(file,destin);
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                        }else{
                            System.out.println("Skip..."+file.getName());
                        }
                    }
                    return 0;
                        }
                )
        );
        dispatcher.register(
                LiteralArgumentBuilder.<String>literal("pack").then(
                        RequiredArgumentBuilder.<String,String>argument("src",string())
                        .executes(source->{
                            String src=source.getArgument("src",String.class);
                            try {
                                Packager.compress(src,"./dump-"+new SimpleDateFormat().format(new Date())+".jar");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return 0;
                        })
                )
        );
        dispatcher.register(
                LiteralArgumentBuilder.<String>literal("seek")
                .then(
                        RequiredArgumentBuilder.<String,String>argument("where",string())
                        .executes(src->{
                            System.out.println("Processing classes...");
                            Seeker seeker=new Seeker(src.getArgument("where",String.class));
                            try {
                                Files.write(new File("obfKeys.json").toPath(),gson.toJson(seeker.start()).getBytes(StandardCharsets.UTF_8));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("Operation Finished!");
                            return 0;
                        })
                )
        );
        waitCommand();
    }
    private static void waitCommand(){
        Scanner scanner=new Scanner(System.in);
        System.out.print("$ ");
        while(scanner.hasNext()){
            String s=scanner.nextLine();
            try {
                dispatcher.execute(s, s);
            }catch(CommandSyntaxException e){
                System.out.println(e.getMessage());
            }
            System.out.print("$ ");
        }
    }
}
