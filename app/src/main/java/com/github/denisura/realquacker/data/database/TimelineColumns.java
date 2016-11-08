package com.github.denisura.realquacker.data.database;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface TimelineColumns {

    @DataType(INTEGER)
    @AutoIncrement
    @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
    String _ID = "_id";

    @DataType(INTEGER)
    String TWEET_ID = "tweet_id";

    @DataType(TEXT)
    String TIMELINE_USER = "timeline_user";
}
