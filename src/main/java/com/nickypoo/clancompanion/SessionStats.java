package com.nickypoo.clancompanion;

import java.time.Duration;
import java.time.Instant;

final class SessionStats
{
    private Instant startedAt = Instant.now();
    private int pvpKills;
    private int pvpDeaths;
    private long pvpLoot;
    private long biggestPk;
    private int pvmKills;
    private long pvmLoot;
    private int valuableDrops;
    private String bestPvmDrop = "None";
    private long bestPvmDropValue;

    void reset()
    {
        startedAt = Instant.now();
        pvpKills = 0;
        pvpDeaths = 0;
        pvpLoot = 0;
        biggestPk = 0;
        pvmKills = 0;
        pvmLoot = 0;
        valuableDrops = 0;
        bestPvmDrop = "None";
        bestPvmDropValue = 0;
    }

    void addPvpKill(long lootValue)
    {
        pvpKills++;
        addPvpLoot(lootValue);
    }

    void addPvpLoot(long lootValue)
    {
        pvpLoot += lootValue;
        biggestPk = Math.max(biggestPk, lootValue);
    }

    void addPvpDeath()
    {
        pvpDeaths++;
    }

    void addPvmKill(long lootValue)
    {
        pvmKills++;
        pvmLoot += lootValue;
    }

    void addValuableDrop(String itemName, long value)
    {
        valuableDrops++;
        if (value > bestPvmDropValue)
        {
            bestPvmDropValue = value;
            bestPvmDrop = itemName;
        }
    }

    int getPvpKills() { return pvpKills; }
    int getPvpDeaths() { return pvpDeaths; }
    long getPvpLoot() { return pvpLoot; }
    long getBiggestPk() { return biggestPk; }
    int getPvmKills() { return pvmKills; }
    long getPvmLoot() { return pvmLoot; }
    int getValuableDrops() { return valuableDrops; }
    String getBestPvmDrop() { return bestPvmDrop; }
    long getBestPvmDropValue() { return bestPvmDropValue; }

    double getKd()
    {
        return pvpDeaths == 0 ? pvpKills : (double) pvpKills / pvpDeaths;
    }

    Duration getDuration()
    {
        return Duration.between(startedAt, Instant.now());
    }
}
