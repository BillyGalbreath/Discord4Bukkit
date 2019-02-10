package net.pl3x.bukkit.discord4bukkit;

import net.pl3x.bukkit.discord4bukkit.bot.Bot;
import net.pl3x.bukkit.discord4bukkit.configuration.Config;
import net.pl3x.bukkit.discord4bukkit.configuration.Lang;
import net.pl3x.bukkit.discord4bukkit.listener.BukkitListener;
import net.pl3x.bukkit.discord4bukkit.util.JarLoader;
import net.pl3x.bukkit.discord4bukkit.util.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class D4BPlugin extends JavaPlugin {
    private static final String JDAVersion = "3.8.2";
    private static final String JDABuild = "459";

    private static final String JDAUrl = "https://github.com/DV8FromTheWorld/JDA/releases/download/v" + JDAVersion + "/JDA-" + JDAVersion + "_" + JDABuild + "-withDependencies.jar";
    private static final String JDAFile = "jda-" + JDAVersion + "_" + JDABuild + ".jar";

    private final Logger logger;

    private final Bot bot;

    public D4BPlugin() {
        super();
        logger = new Logger(this);
        bot = new Bot(this);
    }

    @Override
    public void onLoad() {
        Config.reload(this);
        Lang.reload(this);

        if (new JarLoader(this).loadJar(JDAUrl, new File(new File(getDataFolder(), "libs"), JDAFile))) {
            getLogger().info("JDA successfully loaded");
        } else {
            getLogger().error("JDA could not be loaded!");
        }
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new BukkitListener(this), this);

        bot.connect();
    }

    @Override
    public void onDisable() {
        bot.disconnect();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public Bot getBot() {
        return bot;
    }
}
