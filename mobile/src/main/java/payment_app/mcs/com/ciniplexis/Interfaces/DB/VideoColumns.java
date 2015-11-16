package payment_app.mcs.com.ciniplexis.Interfaces.DB;

import android.provider.BaseColumns;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import payment_app.mcs.com.ciniplexis.Data.AutoMovieDB;

/**
 * Created by ogayle on 12/11/2015.
 */
public interface VideoColumns {
    @DataType(DataType.Type.TEXT)
    @PrimaryKey
    String _ID = BaseColumns._ID;

    @DataType(DataType.Type.TEXT)
    String ISO_LANGUAGE = "iso_language";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String NAME = "name";

    @DataType(DataType.Type.TEXT)
    String KEY = "key";

    @DataType(DataType.Type.TEXT)
    String SITE = "site";

    @DataType(DataType.Type.TEXT)
    String SIZE = "size";

    @DataType(DataType.Type.TEXT)
    String TYPE = "type";

    @DataType(DataType.Type.INTEGER)
    @References(table = AutoMovieDB.MOVIE, column = MovieColumns._ID)
    String FK_MOVIE_ID = "fk_movie_id";
}
