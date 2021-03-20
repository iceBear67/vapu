package io.ib67;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Vapu.MODID, version = Vapu.VERSION)
public class Vapu
{
    public static final String MODID = "vapu";
    public static final String VERSION = "0.1";
    public static final Logger logger= LogManager.getLogger("VAPE");
    
    @EventHandler
    public void init(FMLInitializationEvent event){
        logger.info("Starting Vape.");
        a.a.start();
    }
}
