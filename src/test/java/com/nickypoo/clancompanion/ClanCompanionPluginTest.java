package com.nickypoo.clancompanion;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ClanCompanionPluginTest
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(ClanCompanionPlugin.class);
        RuneLite.main(args);
    }
}
