package net.pl3x.bukkit.discord4bukkit.util;


import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.Webhook;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;
import net.pl3x.bukkit.discord4bukkit.D4BPlugin;
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
            if (!word.startsWith("@")) {
                continue; // must explicitly tag to mention
            }
            webhook.getGuild().getMembers().forEach(member -> {
                if (member == webhook.getGuild().getSelfMember()) {
                    return; // don't tag self
                }
                String name = word.substring(1);
                if (name.equalsIgnoreCase(member.getEffectiveName()) ||
                        name.equalsIgnoreCase(member.getUser().getName())) {
                    split.set(split.indexOf(word), member.getAsMention());
                }
            });
        }
        message = String.join(" ", split);

        JDA jda = D4BPlugin.getInstance().getBot().getClient();

        MessageBuilder msgBuilder = new MessageBuilder();
        msgBuilder.setContent(message);
        msgBuilder.stripMentions(jda, Message.MentionType.EVERYONE); // big no-no
        msgBuilder.stripMentions(jda, Message.MentionType.HERE); // just as bad
        msgBuilder.stripMentions(jda, Message.MentionType.ROLE); // annoying :S

        WebhookMessageBuilder webhookBuilder = new WebhookMessageBuilder();
        webhookBuilder.setContent(msgBuilder.build().getContentRaw());
        webhookBuilder.setUsername(username);
        webhookBuilder.setAvatarUrl(avatar);

        WebhookClient client = webhook.newClient().build();
        client.send(webhookBuilder.build());
        client.close();
    }
}
