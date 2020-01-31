package net.pl3x.bukkit.discord4bukkit;

import net.pl3x.bukkit.discord4bukkit.bot.Bot;
import net.pl3x.bukkit.discord4bukkit.command.bukkit.CmdDiscord4Bukkit;
import net.pl3x.bukkit.discord4bukkit.configuration.Config;
import net.pl3x.bukkit.discord4bukkit.configuration.Lang;
import net.pl3x.bukkit.discord4bukkit.listener.BukkitListener;
import net.pl3x.bukkit.discord4bukkit.listener.ConsoleAppender;
import net.pl3x.bukkit.discord4bukkit.listener.SuperVanishListener;
import net.pl3x.bukkit.discord4bukkit.util.JarLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class D4BPlugin extends JavaPlugin {
    private static final String JDAVersion = "3.8.3";
    private static final String JDABuild = "464";

    private static final String JDAUrl = "https://github.com/DV8FromTheWorld/JDA/releases/download/v" + JDAVersion + "/JDA-" + JDAVersion + "_" + JDABuild + "-withDependencies.jar";
    private static final String JDAFile = "jda-" + JDAVersion + "_" + JDABuild + ".jar";

    private static D4BPlugin instance;

    private final Bot bot;

    private ConsoleAppender consoleAppender;

    public D4BPlugin() {
        super();
        instance = this;
        bot = new Bot(this);
    }

    @Override
    public void onLoad() {
        Config.reload();
        Lang.reload();

        if (new JarLoader().loadJar(JDAUrl, new File(new File(getDataFolder(), "libs"), JDAFile))) {
            Logger.info("JDA successfully loaded");
        } else {
            Logger.error("JDA could not be loaded!");
        }
    }

    @Override
    public void onEnable() {
        consoleAppender = new ConsoleAppender(this);

        getServer().getPluginManager().registerEvents(new BukkitListener(this), this);

        if (getServer().getPluginManager().isPluginEnabled("SuperVanish")) {
            getServer().getPluginManager().registerEvents(new SuperVanishListener(this), this);
        }

        getCommand("discord4bukkit").setExecutor(new CmdDiscord4Bukkit());

        bot.connect();
    }

    @Override
    public void onDisable() {
        consoleAppender.worker.interrupt();

        bot.disconnect();
    }

    public Bot getBot() {
        return bot;
    }

    public static D4BPlugin getInstance() {
        return instance;
    }
}
