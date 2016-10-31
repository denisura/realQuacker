package com.github.denisura.realquacker.data.database;

import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface UserColumns {

    @DataType(INTEGER)
    @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
    String _ID = "_id";

    @DataType(TEXT)
    @NotNull
    String NAME = "name";

    @DataType(TEXT)
    @NotNull
    String SCREEN_NAME = "screen_name";

    @DataType(TEXT)
    @NotNull
    String DESCRIPTION = "description";

    @DataType(INTEGER)
    @NotNull
    String FOLLOWERS_COUNT = "followers_count";

    @DataType(INTEGER)
    @NotNull
    String FRIENDS_COUNT = "friends_count";

    @DataType(INTEGER)
    @NotNull
    String FAVOURITES_COUNT = "favourites_count";

    @DataType(INTEGER)
    @NotNull
    String STATUSES_COUNT = "statuses_count";

    @DataType(TEXT)
    @NotNull
    String PROFILE_IAMGE_URL = "profile_image_url";
}
