package net.pl3x.bukkit.discord4bukkit.listener;

import com.vdurmont.emoji.EmojiParser;
import net.pl3x.bukkit.discord4bukkit.D4BPlugin;
import net.pl3x.bukkit.discord4bukkit.configuration.Lang;
import org.bukkit.ChatColor;
import org.bukkit.advancement.AdvancementDisplay;
import org.bukkit.advancement.FrameType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class BukkitListener implements Listener {
    private final D4BPlugin plugin;

    public BukkitListener(D4BPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getBot().sendMessageToDiscord(":heavy_plus_sign: **" + event.getJoinMessage() + "**");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getBot().sendMessageToDiscord(":heavy_minus_sign: **" + event.getQuitMessage() + "**");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        plugin.getBot().sendMessageToDiscord(":skull: **" + event.getDeathMessage() + "**");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        AdvancementDisplay display = event.getAdvancement().getDisplay();

        if (display == null) {
            return; // nothing to display
        }

        if (!display.shouldAnnounceToChat()) {
            return; // do not display
        }

        if (!event.getPlayer().getWorld().isGameRule("announceAdvancements")) {
            return; // do not display
        }

        FrameType frame = display.getFrameType();
        String icon;
        switch (frame) {
            case CHALLENGE:
                icon = Lang.ADVANCEMENT_ICON_CHALLENGE;
                break;
            case GOAL:
                icon = Lang.ADVANCEMENT_ICON_GOAL;
                break;
            case TASK:
            default:
                icon = Lang.ADVANCEMENT_ICON_TASK;
        }

        plugin.getBot().sendMessageToDiscord(ChatColor.stripColor(Lang.colorize(Lang.ADVANCEMENT_FORMAT
                .replace("{icon}", icon)
                .replace("{player}", event.getPlayer().getName())
                .replace("{type}", frame.name().toLowerCase())
                .replace("{title}", display.getTitle())
                .replace("{description}", display.getDescription()))));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = EmojiParser.parseToUnicode(event.getMessage()
                .replace(":)", ":smiley:")
                .replace("(:", ":smiley:")
                .replace(":P", ":stuck_out_tongue:")
                .replace(":p", ":stuck_out_tongue:")
                .replace(":(", ":frowning:")
                .replace(":c", ":frowning:")
                .replace(":C", ":frowning:")
                .replace("D:", ":frowning:")
                .replace(":D", ":smile:")
                .replace("xD", ":smile:")
                .replace("XD", ":smile:")
                .replace(":'(", ":cry:")
        );
        if (!event.isAsynchronous()) {
            new BukkitRunnable() {
                public void run() {
                    plugin.getBot().sendMessageToDiscord(event.getPlayer(), message);
                }
            }.runTaskAsynchronously(plugin);
        } else {
            plugin.getBot().sendMessageToDiscord(event.getPlayer(), message);
        }
    }
}
