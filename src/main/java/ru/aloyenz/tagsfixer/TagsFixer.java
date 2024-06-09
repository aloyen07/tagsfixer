package ru.aloyenz.tagsfixer;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("tagsfixer")
public class TagsFixer {
    private static final Logger LOGGER = LogManager.getLogger();

    public TagsFixer() {
        LOGGER.info("Hello from small TagsFixer mod.");
        LOGGER.info("Did you know that...");
        LOGGER.warn("FOXES RULES THE WORLD!");
    }
}
