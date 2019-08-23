package net.pl3x.bukkit.discord4bukkit.util;


import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.Webhook;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;
import net.pl3x.bukkit.discord4bukkit.D4BPlugin;
import net.pl3x.bukkit.discord4bukkit.Logger;
import net.pl3x.bukkit.discord4bukkit.bot.Bot;
import net.pl3x.bukkit.discord4bukkit.configuration.Config;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class WebhookUtil {
    private static String[] webhooks = new String[]{"#d4b_1", "#d4b_2"};
    private static int currentWebhook = 0;
    private static String lastUsername;

    public static void sendMessageToDiscord(Bot bot, Player player, String message) {
        sendMessageToDiscord(bot, "https://minotar.net/helm/" + player.getName() + "/100.png", player.getDisplayName(), message);
    }

    public static void sendMessageToDiscord(Bot bot, String avatar, String username, String message) {
        if (username == null || !username.equals(lastUsername)) {
            currentWebhook = currentWebhook == 0 ? 1 : 0;
        }
        lastUsername = username;

        String hookName = webhooks[currentWebhook];
        TextChannel channel = bot.getClient().getTextChannelById(Config.CHAT_CHANNEL);
        Webhook webhook = channel.getWebhooks().complete().stream()
                .filter(hook -> hook.getName().equals(hookName))
                .findFirst().orElse(null);
        if (webhook == null) {
            Logger.info("Could not find webhook! Creating a new one. (" + hookName + ")");
            webhook = channel.createWebhook(hookName).complete();
            if (webhook == null) {
                Logger.warn("Could not send message to discord. Webhook not found!");
                return;
            }
        }

        message = ChatColor.stripColor(message);

        List<String> split = Arrays.asList(message.split(" "));
        for (String word : split) {
            if (!word.startsWith("@")) {
                continue; // must explicitly tag to mention
            }
            Guild guild = webhook.getGuild();
            guild.getMembers().forEach(member -> {
                if (member == guild.getSelfMember()) {
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

        String content = msgBuilder.build().getContentRaw();
        if (content == null || content.isEmpty()) {
            return; // dont send empty messages
        }

        WebhookMessageBuilder webhookBuilder = new WebhookMessageBuilder();
        webhookBuilder.setContent(content);
        webhookBuilder.setUsername(username);
        webhookBuilder.setAvatarUrl(avatar);

        WebhookClient client = webhook.newClient().build();
        client.send(webhookBuilder.build());
        client.close();
    }
}
