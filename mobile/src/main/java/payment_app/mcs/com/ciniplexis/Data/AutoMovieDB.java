package payment_app.mcs.com.ciniplexis.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.ExecOnCreate;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

import payment_app.mcs.com.ciniplexis.Interfaces.DB.MovieColumns;
import payment_app.mcs.com.ciniplexis.Interfaces.DB.ReviewColumns;

/**
 * Created by ogayle on 09/11/2015.
 */

@Database(
        version = AutoMovieDB.VERSION,
        packageName = "payment_app.mcs.com.ciniplexis.providers"
)
public class AutoMovieDB {

    public static final int VERSION = 1;

    @Table(MovieColumns.class)
    public static final String MOVIE = "movie";

    @Table(ReviewColumns.class)
    public static final String REVIEW = "review";

    @OnCreate
    public static void onCreate(Context context, SQLiteDatabase sqlDb) {

    }

    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase sqlDb, int oldVersion, int newVersion) {

    }

    @ExecOnCreate
    public static final String EXECUTE_SCRIPT = "PRAGMA foreign_keys = ON; ";
}
