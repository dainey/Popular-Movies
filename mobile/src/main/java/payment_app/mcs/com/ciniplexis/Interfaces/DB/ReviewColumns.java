package payment_app.mcs.com.ciniplexis.Interfaces.DB;

import android.provider.BaseColumns;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.DataType.Type;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import payment_app.mcs.com.ciniplexis.Data.AutoMovieDB;

/**
 * Created by ogayle on 09/11/2015.
 */
public interface ReviewColumns {
    @DataType(Type.TEXT)
    @PrimaryKey
    String _ID = BaseColumns._ID;

    @DataType(Type.TEXT)
    @NotNull
    String USERNAME = "username";

    @DataType(Type.TEXT)
    String COMMENT = "comment";

    @DataType(Type.TEXT)
    String COMMENT_DATE = "comment_date";

    @DataType(Type.TEXT)
    String COMMENT_URL = "comment_url";

    @DataType(Type.INTEGER)
    @References(table = AutoMovieDB.MOVIE, column = MovieColumns._ID)
    String FK_MOVIE_ID = "fk_movie_id";
}
