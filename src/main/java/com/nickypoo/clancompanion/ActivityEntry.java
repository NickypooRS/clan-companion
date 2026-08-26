package com.nickypoo.clancompanion;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

final class ActivityEntry
{
    enum Type { PVP, PVM, DROP, COLLECTION, DEATH }

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    private final Type type;
    private final String text;
    private final String time;

    ActivityEntry(Type type, String text)
    {
        this.type = type;
        this.text = text;
        this.time = LocalTime.now().format(TIME);
    }

    Type getType() { return type; }
    String getText() { return text; }
    String getTime() { return time; }
}
