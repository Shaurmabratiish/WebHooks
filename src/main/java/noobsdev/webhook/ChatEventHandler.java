package noobsdev.webhook;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobsdev.webhook.async.MillenniumScheduler;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod.EventBusSubscriber(modid = "webhook", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChatEventHandler {
    private static final Queue<String> messageQueue = new ConcurrentLinkedQueue<>();
    private static final int MESSAGES_PER_TICK = 1; // Максимальное количество сообщений за тик
    private static boolean isProcessing = false;
    private static int TICK_TO_MESSAGE = 0;
    @SubscribeEvent
    public static void onChatReceived(ClientChatReceivedEvent event) {
        if (!Webhook.world) return;

        String message = event.getMessage().getString();
        if (shouldProcessMessage(message)) {
            String cleaned = message.replace("@", "").replace("`", "");
            messageQueue.add(cleaned); // Добавляем в очередь
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || messageQueue.isEmpty() || isProcessing) return;

        isProcessing = true;
        MillenniumScheduler.run(() -> {
            try {
                if(TICK_TO_MESSAGE >= 4) {
                    TICK_TO_MESSAGE = 0;
                    int processed = 0;
                    while (!messageQueue.isEmpty() && processed < MESSAGES_PER_TICK) {
                        String message = messageQueue.poll();
                        if (message != null) {
                            Webhook.HOOK.setContent("`" + message + "`");
                            Webhook.HOOK.execute();
                            processed++;
                        }
                    }
                }else {
                    TICK_TO_MESSAGE += 1;
                }
            } catch (IOException e) {
                notifyPlayer("Ошибка отправки в Discord: " + e.getMessage());
            } finally {
                isProcessing = false;
            }
        });
    }

    @SubscribeEvent
    public static void onChatSend(ClientChatEvent event) {
        String message = event.getMessage();

        if (message.startsWith("/start")) {
            event.setCanceled(true);
            toggleBotState();
        }
        else if (message.startsWith("/troll")) {
            event.setCanceled(true);
            sendTrollMessage(message.replace(".troll", "").trim());
        }
        if(!message.startsWith("/l") && !message.startsWith("/login") && message.startsWith("/")) {
            messageQueue.add("Бот ввёл команду: " + message);
        }
    }

    private static boolean shouldProcessMessage(String message) {
        return message.contains("»") &&
                !message.startsWith("Вопросы") &&
                !message.startsWith("Друзья");
    }

    private static void toggleBotState() {
        Webhook.world = !Webhook.world;
        String response = Webhook.world ? "\n Start \n" : "\n End \n";
        notifyPlayer(response);
    }

    private static void sendTrollMessage(String message) {
        if (message.isEmpty()) {
            notifyPlayer("Введите сообщение после .troll");
            return;
        }

        MillenniumScheduler.run(() -> {
            try {
                Webhook.HOOK.setContent("`" + message + "`");
                Webhook.HOOK.execute();
                notifyPlayer("Сообщение отправлено: " + message);
            } catch (IOException e) {
                notifyPlayer("Ошибка отправки тролля: " + e.getMessage());
            }
        });
    }

    private static void notifyPlayer(String message) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendMessage(new StringTextComponent(message), Minecraft.getInstance().player.getUUID());
        }
    }
}
