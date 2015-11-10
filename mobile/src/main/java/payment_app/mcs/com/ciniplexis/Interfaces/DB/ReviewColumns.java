package payment_app.mcs.com.ciniplexis.Interfaces.DB;

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
    @DataType(Type.INTEGER)
    @PrimaryKey
    String _ID = "_id";

    @DataType(Type.TEXT)
    String USERNAME = "username";

    @DataType(Type.TEXT)
    String COMMENT = "comment";

    @DataType(Type.TEXT)
    String COMMENT_DATE = "comment_date";

    @DataType(Type.INTEGER)
    @References(table = AutoMovieDB.MOVIE, column = MovieColumns._ID)
    String FK_MOVIE_ID = "fk_movie_id";
}
