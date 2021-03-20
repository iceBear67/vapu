package io.ib67.deobf;

import org.benf.cfr.reader.api.CfrDriver;
import org.benf.cfr.reader.api.OutputSinkFactory;
import org.benf.cfr.reader.api.SinkReturns;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class DeCompiler {
    public static void decompile(String classPath,Consumer<String> consumer){
        OutputSinkFactory mySink = new OutputSinkFactory() {
            @Override
            public List<SinkClass> getSupportedSinks(SinkType sinkType, Collection<SinkClass> collection) {
                if (sinkType == SinkType.JAVA && collection.contains(SinkClass.DECOMPILED)) {
                    // I'd like "Decompiled".  If you can't do that, I'll take STRING.
                    return Arrays.asList(SinkClass.DECOMPILED, SinkClass.STRING);
                } else {
                    // I only understand how to sink strings, regardless of what you have to give me.
                    return Collections.singletonList(SinkClass.STRING);
                }
            }

            Consumer<SinkReturns.Decompiled> dumpDecompiled = d -> {
                System.out.println("Package [" + d.getPackageName() + "] Class [" + d.getClassName() + "]");
                consumer.accept(d.getJava());
            };

            @Override
            public <T> Sink<T> getSink(SinkType sinkType, SinkClass sinkClass) {
                if (sinkType == SinkType.JAVA && sinkClass == SinkClass.DECOMPILED) {
                    return x -> dumpDecompiled.accept((SinkReturns.Decompiled) x);
                }
                return ignore -> {};
            }
        };

        CfrDriver driver = new CfrDriver.Builder().withOutputSink(mySink).build();
        driver.analyse(Collections.singletonList(classPath));
    }
}
