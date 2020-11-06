package io.ib67;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.ib67.scanner.ClassScanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
                            new Injector(getInteger(s,"pid")).inject(Agent.class.getProtectionDomain().getCodeSource().getLocation().getFile().replaceFirst("\\/",""));
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
