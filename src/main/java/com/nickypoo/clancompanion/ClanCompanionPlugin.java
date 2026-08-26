package com.nickypoo.clancompanion;

import com.google.inject.Provides;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.api.Player;
import net.runelite.api.Varbits;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.events.PlayerLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.util.Text;

@PluginDescriptor(
    name = "Misinformed: PvP and PvM Tracker",
    description = "Track personal PvP and PvM sessions, loot, valuable drops, collection log unlocks and goals.",
    tags = {"clan", "pvp", "pvm", "loot", "session", "tracker", "collection log"}
)
public class ClanCompanionPlugin extends Plugin
{
    private static final int MAX_FEED_ENTRIES = 60;
    private static final String COLLECTION_LOG_PREFIX = "New item added to your collection log:";

    @Inject private Client client;
    @Inject private ClientToolbar clientToolbar;
    @Inject private OverlayManager overlayManager;
    @Inject private ClanCompanionOverlay overlay;
    @Inject private ClanCompanionConfig config;
    @Inject private ItemManager itemManager;
    @Inject private Notifier notifier;

    private final SessionStats stats = new SessionStats();
    private final Deque<ActivityEntry> activity = new ArrayDeque<>();
    private ClanCompanionPanel panel;
    private NavigationButton navButton;
    private int tickCounter;
    private String latestShareEvent = "No notable activity yet.";

    @Provides
    ClanCompanionConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ClanCompanionConfig.class);
    }

    @Override
    protected void startUp()
    {
        stats.reset();
        panel = new ClanCompanionPanel(this);
        navButton = NavigationButton.builder()
            .tooltip("Misinformed: PvP and PvM Tracker")
            .icon(createNavIcon())
            .priority(7)
            .panel(panel)
            .build();
        clientToolbar.addNavigation(navButton);
        overlayManager.add(overlay);
        addActivity(ActivityEntry.Type.PVM, "Session started");
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        if (navButton != null)
        {
            clientToolbar.removeNavigation(navButton);
        }
        panel = null;
        navButton = null;
        activity.clear();
    }

    @Subscribe
    public void onPlayerLootReceived(PlayerLootReceived event)
    {
        if (!config.trackPvp())
        {
            return;
        }

        long value = valueOf(event.getItems());
        stats.addPvpKill(value);
        String target = event.getPlayer() != null && event.getPlayer().getName() != null
            ? event.getPlayer().getName() : "player";

        addActivity(ActivityEntry.Type.PVP, "PvP kill: " + target + " — " + formatGp(value));
        latestShareEvent = "Misinformed — PvP Kill\nOpponent: " + target + "\nLoot: " + formatGp(value);
    }

    @Subscribe
    public void onNpcLootReceived(NpcLootReceived event)
    {
        if (!config.trackPvm())
        {
            return;
        }

        long value = valueOf(event.getItems());
        stats.addPvmKill(value);
        String npc = event.getNpc() != null && event.getNpc().getName() != null
            ? event.getNpc().getName() : "NPC";

        addActivity(ActivityEntry.Type.PVM, "PvM: " + npc + " — " + formatGp(value));

        for (ItemStack item : event.getItems())
        {
            long itemValue = itemValue(item);
            if (itemValue >= config.valuableDropThreshold())
            {
                ItemComposition composition = itemManager.getItemComposition(item.getId());
                String name = composition == null ? "Item " + item.getId() : composition.getName();

                stats.addValuableDrop(name, itemValue);
                addActivity(ActivityEntry.Type.DROP,
                    "Valuable drop: " + name + " — " + formatGp(itemValue));

                latestShareEvent = "Misinformed — Valuable Drop\nItem: " + name
                    + "\nFrom: " + npc + "\nValue: " + formatGp(itemValue);

                if (config.notifyValuableDrops())
                {
                    notifier.notify("Misinformed: " + name + " — " + formatGp(itemValue));
                }
            }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (!config.trackCollectionLog() || event.getType() != ChatMessageType.GAMEMESSAGE)
        {
            return;
        }

        String message = Text.removeTags(event.getMessage());
        if (message == null || !message.startsWith(COLLECTION_LOG_PREFIX))
        {
            return;
        }

        String item = message.substring(COLLECTION_LOG_PREFIX.length()).trim();
        if (item.isEmpty())
        {
            item = "Collection log unlock";
        }

        addActivity(ActivityEntry.Type.COLLECTION, "Collection log: " + item);
        latestShareEvent = "Misinformed — Collection Log\nNew unlock: " + item;

        if (config.notifyCollectionLog())
        {
            notifier.notify("Misinformed: Collection log — " + item);
        }
    }

    @Subscribe
    public void onActorDeath(ActorDeath event)
    {
        if (!config.trackPvp())
        {
            return;
        }

        Actor actor = event.getActor();
        Player local = client.getLocalPlayer();
        if (actor == local && client.getVarbitValue(Varbits.PVP_SPEC_ORB) == 1)
        {
            stats.addPvpDeath();
            addActivity(ActivityEntry.Type.DEATH, "PvP-area death recorded");
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (++tickCounter % 5 == 0)
        {
            refreshPanel();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOGGED_IN)
        {
            refreshPanel();
        }
    }

    private BufferedImage createNavIcon()
    {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color gold = new Color(218, 165, 32);
        Color dark = new Color(36, 36, 36);
        Color light = new Color(230, 230, 230);

        Polygon shield = new Polygon(
            new int[] {16, 28, 27, 23, 16, 9, 5, 4},
            new int[] {2, 7, 19, 25, 30, 25, 19, 7}, 8);
        g.setColor(gold);
        g.fillPolygon(shield);

        Polygon inner = new Polygon(
            new int[] {16, 25, 24, 21, 16, 11, 8, 7},
            new int[] {5, 9, 18, 23, 27, 23, 18, 9}, 8);
        g.setColor(dark);
        g.fillPolygon(inner);
        g.setColor(gold);
        g.setStroke(new BasicStroke(1.2f));
        g.drawPolygon(inner);

        g.setColor(light);
        g.fillOval(13, 10, 6, 6);
        g.fillOval(8, 13, 5, 5);
        g.fillOval(19, 13, 5, 5);
        g.fillOval(11, 16, 10, 8);
        g.fillOval(6, 18, 9, 7);
        g.fillOval(17, 18, 9, 7);
        g.dispose();
        return image;
    }

    private long valueOf(Iterable<ItemStack> items)
    {
        long total = 0;
        for (ItemStack item : items)
        {
            total += itemValue(item);
        }
        return total;
    }

    private long itemValue(ItemStack item)
    {
        int unitPrice = itemManager.getItemPrice(item.getId());
        return (long) Math.max(0, unitPrice) * Math.max(0, item.getQuantity());
    }

    private String formatGp(long value)
    {
        return QuantityFormatter.quantityToStackSize(value) + " gp";
    }

    private void addActivity(ActivityEntry.Type type, String text)
    {
        activity.addFirst(new ActivityEntry(type, text));
        while (activity.size() > MAX_FEED_ENTRIES)
        {
            activity.removeLast();
        }
        refreshPanel();
    }

    private void refreshPanel()
    {
        if (panel != null)
        {
            SwingUtilities.invokeLater(panel::refresh);
        }
    }

    SessionStats getStats() { return stats; }
    List<ActivityEntry> getActivitySnapshot()
    {
        return Collections.unmodifiableList(new ArrayList<>(activity));
    }
    int getPvpChallengeTarget() { return config.pvpChallengeGoal(); }
    int getPvmChallengeTarget() { return config.pvmChallengeGoal(); }

    void resetSession()
    {
        stats.reset();
        activity.clear();
        latestShareEvent = "No notable activity yet.";
        addActivity(ActivityEntry.Type.PVM, "Session reset");
    }

    String buildLatestShareEvent() { return latestShareEvent; }

    String buildShareSummary()
    {
        long seconds = stats.getDuration().getSeconds();
        String duration = String.format("%02d:%02d:%02d",
            seconds / 3600, (seconds % 3600) / 60, seconds % 60);

        return "Misinformed — Session Summary\n"
            + "Duration: " + duration + "\n"
            + "PvP: " + stats.getPvpKills() + " kills / " + stats.getPvpDeaths()
            + " deaths (K/D " + String.format("%.2f", stats.getKd()) + ")\n"
            + "PvP loot: " + formatGp(stats.getPvpLoot())
            + " | Biggest PK: " + formatGp(stats.getBiggestPk()) + "\n"
            + "PvM: " + stats.getPvmKills() + " kills | Loot: " + formatGp(stats.getPvmLoot()) + "\n"
            + "Valuable drops: " + stats.getValuableDrops() + " | Best: " + stats.getBestPvmDrop()
            + (stats.getBestPvmDropValue() > 0
                ? " (" + formatGp(stats.getBestPvmDropValue()) + ")" : "");
    }
}
