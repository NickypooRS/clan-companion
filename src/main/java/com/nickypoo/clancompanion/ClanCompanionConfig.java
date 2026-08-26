package com.nickypoo.clancompanion;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("clancompanion")
public interface ClanCompanionConfig extends Config
{
    @ConfigSection(name = "Tracking", description = "Choose what Misinformed tracks.", position = 0)
    String trackingSection = "tracking";

    @ConfigSection(name = "Overlay", description = "Choose what appears on the in-game overlay.", position = 1)
    String overlaySection = "overlay";

    @ConfigSection(name = "Drops & Collection Log", description = "Valuable drop and collection log settings.", position = 2)
    String dropsSection = "drops";

    @ConfigSection(name = "Challenges", description = "Personal session challenge targets.", position = 3)
    String challengesSection = "challenges";

    @ConfigItem(keyName = "trackPvp", name = "Track PvP", description = "Track PvP kills, deaths and loot.", section = trackingSection, position = 0)
    default boolean trackPvp() { return true; }

    @ConfigItem(keyName = "trackPvm", name = "Track PvM", description = "Track NPC kills and loot.", section = trackingSection, position = 1)
    default boolean trackPvm() { return true; }

    @ConfigItem(keyName = "showOverlay", name = "Show overlay", description = "Show the Misinformed in-game overlay.", section = overlaySection, position = 0)
    default boolean showOverlay() { return true; }

    @ConfigItem(keyName = "overlaySessionTime", name = "Session timer", description = "Show session duration on the overlay.", section = overlaySection, position = 1)
    default boolean overlaySessionTime() { return true; }

    @ConfigItem(keyName = "overlayPvpStats", name = "PvP stats", description = "Show PvP kills, deaths and K/D.", section = overlaySection, position = 2)
    default boolean overlayPvpStats() { return true; }

    @ConfigItem(keyName = "overlayPvpLoot", name = "PvP loot", description = "Show PvP loot and biggest PK.", section = overlaySection, position = 3)
    default boolean overlayPvpLoot() { return true; }

    @ConfigItem(keyName = "overlayPvmStats", name = "PvM stats", description = "Show PvM kills.", section = overlaySection, position = 4)
    default boolean overlayPvmStats() { return true; }

    @ConfigItem(keyName = "overlayPvmLoot", name = "PvM loot", description = "Show PvM loot and best drop.", section = overlaySection, position = 5)
    default boolean overlayPvmLoot() { return true; }

    @ConfigItem(keyName = "overlayChallenges", name = "Challenge progress", description = "Show PvP and PvM challenge progress.", section = overlaySection, position = 6)
    default boolean overlayChallenges() { return false; }

    @Range(min = 0, max = 1000000000)
    @ConfigItem(keyName = "valuableDropThreshold", name = "Valuable drop threshold", description = "Drops at or above this GE value are highlighted.", section = dropsSection, position = 0)
    default int valuableDropThreshold() { return 1000000; }

    @ConfigItem(keyName = "notifyValuableDrops", name = "Valuable drop notifications", description = "Show a RuneLite notification for valuable drops.", section = dropsSection, position = 1)
    default boolean notifyValuableDrops() { return true; }

    @ConfigItem(keyName = "trackCollectionLog", name = "Collection log activity", description = "Add collection log unlocks to recent activity.", section = dropsSection, position = 2)
    default boolean trackCollectionLog() { return true; }

    @ConfigItem(keyName = "notifyCollectionLog", name = "Collection log notifications", description = "Show a RuneLite notification for collection log unlocks.", section = dropsSection, position = 3)
    default boolean notifyCollectionLog() { return true; }

    @Range(min = 1, max = 10000)
    @ConfigItem(keyName = "pvpChallengeGoal", name = "PvP kill goal", description = "Personal PvP kill target.", section = challengesSection, position = 0)
    default int pvpChallengeGoal() { return 25; }

    @Range(min = 1, max = 100000)
    @ConfigItem(keyName = "pvmChallengeGoal", name = "PvM kill goal", description = "Personal PvM kill target.", section = challengesSection, position = 1)
    default int pvmChallengeGoal() { return 100; }
}
