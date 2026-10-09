package com.romanfr.equalmending;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class EqualMendingClient implements ClientModInitializer {
    private static final String MOD_NAME = "Equal Mending Helper";
    private static List<ArmorStatus> lastStatus = List.of();
    private static int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                lastStatus = List.of();
                return;
            }
            if (++tickCounter >= 10) {
                tickCounter = 0;
                lastStatus = collectStatus(client);
            }
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden || lastStatus.isEmpty()) return;
            renderHud(drawContext, client);
        });
    }

    private static List<ArmorStatus> collectStatus(MinecraftClient client) {
        List<ArmorStatus> statuses = new ArrayList<>();
        add(statuses, "Helmet", client.player.getEquippedStack(EquipmentSlot.HEAD));
        add(statuses, "Chest", client.player.getEquippedStack(EquipmentSlot.CHEST));
        add(statuses, "Legs", client.player.getEquippedStack(EquipmentSlot.LEGS));
        add(statuses, "Boots", client.player.getEquippedStack(EquipmentSlot.FEET));
        return statuses;
    }

    private static void add(List<ArmorStatus> statuses, String label, ItemStack stack) {
        if (stack.isEmpty() || !stack.isDamageable()) return;
        int max = stack.getMaxDamage();
        int remaining = Math.max(0, max - stack.getDamage());
        int missing = max - remaining;
        statuses.add(new ArmorStatus(label, remaining, max, missing));
    }

    private static void renderHud(DrawContext context, MinecraftClient client) {
        int x = 8;
        int y = 8;
        int lineHeight = 11;
        int width = 166;
        int height = 16 + lastStatus.size() * lineHeight;
        context.fill(x - 3, y - 3, x + width, y + height, 0x99000000);
        context.drawTextWithShadow(client.textRenderer,
                Text.literal("Equal Mending Helper").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD),
                x, y, 0xFFFFFF);
        y += 13;

        for (ArmorStatus status : lastStatus) {
            int color;
            if (status.remaining <= 20) color = 0xFFFF5555;
            else if (status.remaining <= 50) color = 0xFFFFAA00;
            else color = 0xFF55FF55;
            String text = status.label + ": " + status.remaining + "/" + status.max;
            context.drawTextWithShadow(client.textRenderer, Text.literal(text), x, y, color);
            y += lineHeight;
        }

        lastStatus.stream()
                .min(Comparator.comparingInt(ArmorStatus::remaining))
                .ifPresent(status -> context.drawTextWithShadow(
                        client.textRenderer,
                        Text.literal("Repair focus: " + status.label).formatted(Formatting.YELLOW),
                        x, y, 0xFFFFFF));
    }

    private record ArmorStatus(String label, int remaining, int max, int missing) {}
}
