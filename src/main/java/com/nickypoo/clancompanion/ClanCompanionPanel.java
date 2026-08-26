package com.nickypoo.clancompanion;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.QuantityFormatter;

final class ClanCompanionPanel extends PluginPanel
{
    private static final Color GOLD = new Color(218, 170, 70);
    private static final Color RED = new Color(194, 76, 70);
    private static final Color BLUE = new Color(76, 145, 205);
    private static final Color GREEN = new Color(78, 154, 91);
    private static final Color PURPLE = new Color(148, 102, 188);
    private static final Color CARD = new Color(37, 37, 37);
    private static final Color CARD_DARK = new Color(31, 31, 31);
    private static final Color TEXT = new Color(225, 225, 225);
    private static final Color MUTED = new Color(145, 145, 145);
    private static final Color LINE = new Color(56, 56, 56);
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 17);
    private static final Font FONT_SUBTITLE = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font FONT_SECTION = new Font("SansSerif", Font.BOLD, 12);
    private static final Font FONT_VALUE_LARGE = new Font("SansSerif", Font.BOLD, 17);
    private static final Font FONT_VALUE = new Font("SansSerif", Font.BOLD, 12);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 9);
    private static final Font FONT_NORMAL = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 9);

    private final ClanCompanionPlugin plugin;

    private final JLabel duration = value("00:00:00");
    private final JLabel pvpKills = value("0");
    private final JLabel pvpDeaths = value("0");
    private final JLabel kd = value("0.00");
    private final JLabel pvpLoot = value("0 gp");
    private final JLabel biggestPk = value("0 gp");
    private final JLabel pvmKills = value("0");
    private final JLabel pvmLoot = value("0 gp");
    private final JLabel bestDrop = value("None");
    private final JLabel valuable = value("0");
    private final JLabel pvpGoalText = small("0 / 0");
    private final JLabel pvmGoalText = small("0 / 0");
    private final JProgressBar pvpGoal = progress(RED);
    private final JProgressBar pvmGoal = progress(BLUE);
    private final JPanel activityRows = new JPanel();

    ClanCompanionPanel(ClanCompanionPlugin plugin)
    {
        this.plugin = plugin;
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(BorderFactory.createEmptyBorder());

        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());
        content.setBackground(ColorScheme.DARK_GRAY_COLOR);
        content.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        int row = 0;
        addSection(content, gbc, row++, buildHeader(), 0);
        addSection(content, gbc, row++, buildSession(), 10);
        addSection(content, gbc, row++, buildPvp(), 10);
        addSection(content, gbc, row++, buildPvm(), 10);
        addSection(content, gbc, row++, buildChallenges(), 10);
        addSection(content, gbc, row++, buildActivity(), 10);
        addSection(content, gbc, row++, buildActions(), 10);

        gbc.gridy = row;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(Box.createVerticalGlue(), gbc);

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        northPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        northPanel.add(content, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(northPanel);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        scroll.getVerticalScrollBar().setBlockIncrement(120);
        scroll.getViewport().setBackground(ColorScheme.DARK_GRAY_COLOR);
        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    private static void addSection(JPanel parent, GridBagConstraints gbc, int row, JPanel panel, int top)
    {
        gbc.gridy = row;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(top, 0, 0, 0);
        parent.add(panel, gbc);
    }

    private JPanel buildHeader()
    {
        JPanel panel = clear();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("MISINFORMED");
        title.setForeground(GOLD);
        title.setFont(FONT_TITLE);

        JLabel subtitle = new JLabel("PvP and PvM Tracker");
        subtitle.setForeground(TEXT);
        subtitle.setFont(FONT_SUBTITLE);

        JLabel author = new JLabel("by Ybc");
        author.setForeground(MUTED);
        author.setFont(FONT_SMALL);

        panel.add(title);
        panel.add(Box.createVerticalStrut(2));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(2));
        panel.add(author);
        return panel;
    }

    private JPanel buildSession()
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_DARK);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE),
                BorderFactory.createEmptyBorder(9, 10, 9, 10)));

        panel.add(caption("SESSION"), BorderLayout.WEST);
        duration.setHorizontalAlignment(SwingConstants.RIGHT);
        duration.setFont(new Font("SansSerif", Font.BOLD, 13));
        panel.add(duration, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildPvp()
    {
        JPanel card = card("PVP", "Kills, deaths and loot", RED, true);

        JPanel stats = clear(new GridLayout(1, 3, 8, 0));
        stats.add(metric("KILLS", pvpKills));
        stats.add(metric("DEATHS", pvpDeaths));
        stats.add(metric("K/D", kd));
        card.add(stats);

        divider(card);

        JPanel loot = clear(new GridLayout(1, 2, 10, 0));
        loot.add(block("TOTAL LOOT", pvpLoot));
        loot.add(block("BIGGEST PK", biggestPk));
        card.add(loot);
        return card;
    }
    private static void divider(JPanel panel)
    {
        panel.add(Box.createVerticalStrut(10));

        JPanel line = new JPanel();
        line.setBackground(LINE);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setPreferredSize(new Dimension(10, 1));
        line.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(line);
        panel.add(Box.createVerticalStrut(9));
    }

    private JPanel buildPvm()
    {
        JPanel card = card("PVM", "Kills and valuable drops", BLUE, true);

        JPanel stats = clear(new GridLayout(1, 2, 10, 0));
        stats.add(metric("KILLS", pvmKills));
        stats.add(metric("TOTAL LOOT", pvmLoot));
        card.add(stats);

        divider(card);

        JPanel drops = clear(new GridLayout(1, 2, 10, 0));
        drops.add(block("BEST DROP", bestDrop));
        drops.add(block("VALUABLE", valuable));
        card.add(drops);
        return card;
    }

    private JPanel buildChallenges()
    {
        JPanel card = card("CHALLENGES", "Current session goals", GREEN, false);
        card.add(goal("PvP kills", pvpGoalText, pvpGoal));
        card.add(Box.createVerticalStrut(10));
        card.add(goal("PvM kills", pvmGoalText, pvmGoal));
        return card;
    }

    private JPanel buildActivity()
    {
        JPanel card = card("RECENT ACTIVITY", "Latest session events", PURPLE, false);
        activityRows.setOpaque(false);
        activityRows.setLayout(new BoxLayout(activityRows, BoxLayout.Y_AXIS));
        card.add(activityRows);
        return card;
    }

    private JPanel buildActions()
    {
        JPanel panel = clear();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel top = clear(new GridLayout(1, 2, 6, 0));
        JButton summary = button("Copy Summary", GOLD);
        summary.addActionListener(e -> copy(plugin.buildShareSummary()));
        JButton latest = button("Copy Latest", PURPLE);
        latest.addActionListener(e -> copy(plugin.buildLatestShareEvent()));
        top.add(summary);
        top.add(latest);

        JButton reset = button("Reset Session", RED);
        reset.addActionListener(e -> plugin.resetSession());

        panel.add(top);
        panel.add(Box.createVerticalStrut(6));
        panel.add(reset);
        return panel;
    }

    private JPanel card(String title, String subtitle, Color accent, boolean centerSubtitle)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 3, 1, 1, accent.darker()),
                BorderFactory.createEmptyBorder(10, 10, 11, 10)));

        JPanel heading = clear(new BorderLayout());
        heading.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        heading.setPreferredSize(new Dimension(10, 34));

        JPanel headingText = clear();
        headingText.setLayout(new BoxLayout(headingText, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(accent);
        titleLabel.setFont(FONT_SECTION);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setForeground(MUTED);
        subtitleLabel.setFont(FONT_SMALL);

        if (centerSubtitle)
        {
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            heading.add(headingText, BorderLayout.CENTER);
        }
        else
        {
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            heading.add(headingText, BorderLayout.WEST);
        }

        headingText.add(titleLabel);
        headingText.add(Box.createVerticalStrut(1));
        headingText.add(subtitleLabel);

        panel.add(heading);
        panel.add(Box.createVerticalStrut(10));
        return panel;
    }

    private JPanel metric(String name, JLabel val)
    {
        JPanel panel = clear();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        val.setAlignmentX(CENTER_ALIGNMENT);
        val.setHorizontalAlignment(SwingConstants.CENTER);
        val.setFont(FONT_VALUE_LARGE);
        JLabel label = caption(name);
        label.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(val);
        panel.add(Box.createVerticalStrut(2));
        panel.add(label);
        return panel;
    }

    private JPanel block(String name, JLabel val)
    {
        JPanel panel = clear();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        val.setFont(FONT_VALUE);
        panel.add(caption(name));
        panel.add(Box.createVerticalStrut(3));
        panel.add(val);
        return panel;
    }

    private JPanel goal(String name, JLabel val, JProgressBar bar)
    {
        JPanel panel = clear();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JPanel line = clear(new BorderLayout());
        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(TEXT);
        nameLabel.setFont(FONT_NORMAL);
        line.add(nameLabel, BorderLayout.WEST);
        line.add(val, BorderLayout.EAST);
        panel.add(line);
        panel.add(Box.createVerticalStrut(5));
        panel.add(bar);
        return panel;
    }

    private JPanel buildActivityRow(ActivityEntry entry)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_DARK);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, activityColor(entry.getType())),
                BorderFactory.createEmptyBorder(7, 8, 7, 8)));

        JPanel text = clear();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel event = new JLabel(shorten(entry.getText(), 38));
        event.setForeground(TEXT);
        event.setFont(FONT_NORMAL);
        JLabel time = new JLabel(entry.getTime());
        time.setForeground(MUTED);
        time.setFont(FONT_SMALL);
        text.add(event);
        text.add(Box.createVerticalStrut(2));
        text.add(time);
        panel.add(text, BorderLayout.CENTER);
        return panel;
    }

    private Color activityColor(ActivityEntry.Type type)
    {
        switch (type)
        {
            case PVP: return RED;
            case PVM: return BLUE;
            case DROP: return GOLD;
            case COLLECTION: return PURPLE;
            case DEATH: return new Color(180, 76, 76);
            default: return MUTED;
        }
    }

    void refresh()
    {
        SessionStats stats = plugin.getStats();
        long seconds = stats.getDuration().getSeconds();
        duration.setText(String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60));

        pvpKills.setText(Integer.toString(stats.getPvpKills()));
        pvpDeaths.setText(Integer.toString(stats.getPvpDeaths()));
        kd.setText(String.format("%.2f", stats.getKd()));
        pvpLoot.setText(gp(stats.getPvpLoot()));
        biggestPk.setText(gp(stats.getBiggestPk()));

        pvmKills.setText(Integer.toString(stats.getPvmKills()));
        pvmLoot.setText(gp(stats.getPvmLoot()));
        bestDrop.setText(shorten(stats.getBestPvmDrop(), 20));
        valuable.setText(Integer.toString(stats.getValuableDrops()));

        updateGoal(pvpGoal, pvpGoalText, stats.getPvpKills(), plugin.getPvpChallengeTarget());
        updateGoal(pvmGoal, pvmGoalText, stats.getPvmKills(), plugin.getPvmChallengeTarget());
        updateActivity(plugin.getActivitySnapshot());
        revalidate();
        repaint();
    }

    private void updateGoal(JProgressBar bar, JLabel text, int current, int target)
    {
        int maximum = Math.max(target, 1);
        bar.setMaximum(maximum);
        bar.setValue(Math.min(current, maximum));
        text.setText(current + " / " + target);
    }

    private void updateActivity(List<ActivityEntry> entries)
    {
        activityRows.removeAll();
        if (entries.isEmpty())
        {
            JLabel empty = new JLabel("No activity yet");
            empty.setForeground(MUTED);
            empty.setFont(new Font("SansSerif", Font.ITALIC, 10));
            activityRows.add(empty);
            return;
        }

        int shown = 0;
        for (ActivityEntry entry : entries)
        {
            if (shown >= 4) break;
            if (shown > 0) activityRows.add(Box.createVerticalStrut(5));
            JPanel row = buildActivityRow(entry);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            row.setAlignmentX(LEFT_ALIGNMENT);
            activityRows.add(row);
            shown++;
        }
    }

    private JButton button(String text, Color accent)
    {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setForeground(TEXT);
        button.setBackground(CARD_DARK);
        button.setFont(new Font("SansSerif", Font.BOLD, 10));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent.darker()),
                BorderFactory.createEmptyBorder(7, 7, 7, 7)));
        return button;
    }

    private void copy(String text)
    {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
    }

    private String gp(long value)
    {
        return QuantityFormatter.quantityToStackSize(value) + " gp";
    }

    private static JLabel value(String text)
    {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(FONT_VALUE);
        return label;
    }

    private static JLabel small(String text)
    {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        return label;
    }

    private static JLabel caption(String text)
    {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(FONT_LABEL);
        return label;
    }

    private static JProgressBar progress(Color color)
    {
        JProgressBar bar = new JProgressBar();
        bar.setForeground(color);
        bar.setBackground(new Color(24, 24, 24));
        bar.setBorderPainted(false);
        bar.setStringPainted(false);
        bar.setPreferredSize(new Dimension(10, 7));
        return bar;
    }

    private static JPanel clear()
    {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    private static JPanel clear(java.awt.LayoutManager layout)
    {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    private static String shorten(String text, int max)
    {
        if (text == null || text.isEmpty()) return "None";
        return text.length() <= max ? text : text.substring(0, Math.max(0, max - 3)) + "...";
    }
}
