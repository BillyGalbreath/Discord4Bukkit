package net.pl3x.bukkit.discord4bukkit.util;


import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.Webhook;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class WebhookUtil {
    private static Webhook[] webhooks = new Webhook[2];
    private static int currentWebhook = 0;

    public static void setup(TextChannel channel) {
        webhooks[0] = channel.createWebhook("#d4b_1").complete();
        webhooks[1] = channel.createWebhook("#d4b_2").complete();
    }

    public static void sendMessageToDiscord(Player player, String message) {
        sendMessageToDiscord("https://minotar.net/helm/" + player.getName() + "/100.png", player.getDisplayName(), message);
    }

    public static void sendMessageToDiscord(String avatar, String username, String message) {
        currentWebhook = currentWebhook == 0 ? 1 : 0;
        Webhook webhook = webhooks[currentWebhook];
        if (webhook == null) {
            return;
        }

        message = ChatColor.stripColor(message);

        List<String> split = Arrays.asList(message.split(" "));
        for (String word : split) {
            if (word.equalsIgnoreCase(webhook.getGuild().getSelfMember().getEffectiveName())) {
                split.set(split.indexOf(word), webhook.getGuild().getOwner().getAsMention());
            }
        }
        message = String.join(" ", split);

        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setContent(message);
        builder.setUsername(username);
        builder.setAvatarUrl(avatar);

        WebhookClient client = webhook.newClient().build();
        client.send(builder.build());
        client.close();
    }
}
