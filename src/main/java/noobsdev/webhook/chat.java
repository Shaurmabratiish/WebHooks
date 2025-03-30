package noobsdev.webhook;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.io.IOException;

public class chat {
    public static void onReceiveMessage(ClientChatReceivedEvent event) {
        String message = event.getMessage().getString();
        if(message.contains("»") && !message.startsWith("Вопросы") && !message.startsWith("Друзья") && !message.startsWith("Донат-чат") && !message.startsWith("Креатив-чат")) {
            if (Webhook.world) {
                if(message.contains("@")) {
                    message.replace("@", "#");
                }
                DiscordWebhook webhook = new DiscordWebhook(Webhook.WEBHOOK);

                webhook.setContent("`" + message + "`");

                try {
                    webhook.execute();
                } catch (IOException e) {

                }

            }
        }
    }
    public static void onClientChat(ClientChatEvent event) {
        String message = event.getMessage();

        if (message.startsWith("/start")) {
            event.setCanceled(true);
            if(!Webhook.world) {
                Webhook.world = true;
                Minecraft.getInstance().player.sendMessage(new StringTextComponent("\n Start Webhook messages! \n"), Minecraft.getInstance().player.getUUID());
            }else {
                Webhook.world = false;
                Minecraft.getInstance().player.sendMessage(new StringTextComponent("\n End Webhook messages! \n"), Minecraft.getInstance().player.getUUID());
            }
        }
    }
}
