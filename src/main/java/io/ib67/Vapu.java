package io.ib67;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Scanner;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class Vapu{
    private static final CommandDispatcher<String> dispatcher=new CommandDispatcher<>();
    public static void main(String[] args){
        dispatcher.register(
                LiteralArgumentBuilder.<String>literal("inject")
                .then(
                        RequiredArgumentBuilder.<String,Integer>argument("pid",integer()).executes(s->{
                            new Injector(getInteger(s,"pid")).inject();
                            return 0; }
                        )
                )
        );
        waitCommand();
    }
    private static void waitCommand(){
        Scanner scanner=new Scanner(System.in);
        while(scanner.hasNext()){
            String s=scanner.nextLine();
            try {
                dispatcher.execute(s, s);
            }catch(CommandSyntaxException e){
                System.out.println(e.getMessage());
            }
        }
    }
}
