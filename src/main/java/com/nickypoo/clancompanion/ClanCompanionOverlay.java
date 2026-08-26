package com.nickypoo.clancompanion;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.QuantityFormatter;

final class ClanCompanionOverlay extends OverlayPanel
{
    private final ClanCompanionPlugin plugin;
    private final ClanCompanionConfig config;

    @Inject
    ClanCompanionOverlay(ClanCompanionPlugin plugin, ClanCompanionConfig config)
    {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showOverlay())
        {
            return null;
        }

        SessionStats s = plugin.getStats();
        panelComponent.getChildren().add(TitleComponent.builder().text("Misinformed").build());

        if (config.overlaySessionTime())
        {
            long seconds = s.getDuration().getSeconds();
            panelComponent.getChildren().add(LineComponent.builder()
                .left("Session")
                .right(String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60))
                .build());
        }

        if (config.overlayPvpStats())
        {
            panelComponent.getChildren().add(LineComponent.builder()
                .left("PvP")
                .right(s.getPvpKills() + " K / " + s.getPvpDeaths() + " D")
                .build());
            panelComponent.getChildren().add(LineComponent.builder()
                .left("K/D")
                .right(String.format("%.2f", s.getKd()))
                .build());
        }

        if (config.overlayPvpLoot())
        {
            panelComponent.getChildren().add(LineComponent.builder()
                .left("PvP loot")
                .right(gp(s.getPvpLoot()))
                .build());
            panelComponent.getChildren().add(LineComponent.builder()
                .left("Biggest PK")
                .right(gp(s.getBiggestPk()))
                .build());
        }

        if (config.overlayPvmStats())
        {
            panelComponent.getChildren().add(LineComponent.builder()
                .left("PvM kills")
                .right(Integer.toString(s.getPvmKills()))
                .build());
        }

        if (config.overlayPvmLoot())
        {
            panelComponent.getChildren().add(LineComponent.builder()
                .left("PvM loot")
                .right(gp(s.getPvmLoot()))
                .build());
            panelComponent.getChildren().add(LineComponent.builder()
                .left("Best drop")
                .right(shorten(s.getBestPvmDrop(), 18))
                .build());
        }

        if (config.overlayChallenges())
        {
            if (config.trackPvp())
            {
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("PvP goal")
                    .right(s.getPvpKills() + " / " + plugin.getPvpChallengeTarget())
                    .build());
            }
            if (config.trackPvm())
            {
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("PvM goal")
                    .right(s.getPvmKills() + " / " + plugin.getPvmChallengeTarget())
                    .build());
            }
        }

        return super.render(graphics);
    }

    private String gp(long value)
    {
        return QuantityFormatter.quantityToStackSize(value);
    }

    private String shorten(String text, int max)
    {
        if (text == null || text.isEmpty())
        {
            return "None";
        }
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }
}
