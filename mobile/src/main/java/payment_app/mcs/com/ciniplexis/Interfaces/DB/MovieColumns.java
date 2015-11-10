package payment_app.mcs.com.ciniplexis.Interfaces.DB;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.DataType.Type;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by ogayle on 09/11/2015.
 */
public interface MovieColumns {

    @DataType(Type.INTEGER)
    @PrimaryKey
    String _ID = "_id";

    @DataType(Type.TEXT)
    @NotNull
    String TITLE = "title";

    @DataType(Type.TEXT)
    @NotNull
    String RELEASE_DATE = "release_date";

    @DataType(Type.REAL)
    @NotNull
    String RATING = "rating";

    @DataType(Type.REAL)
    @NotNull
    String PRICE = "price";

    @DataType(Type.REAL)
    String POPULARITY = "popularity";

    @DataType(Type.TEXT)
    String PLOT = "plot";

    @DataType(Type.TEXT)
    @NotNull
    String IMAGE = "image_url";

    @DataType(Type.TEXT)
    String BACKGROUND_IMAGE = "background_image_url";

    @DataType(Type.INTEGER)
    String IS_PURCHASED = "is_purchased";

    @DataType(Type.INTEGER)
    String IS_FAVORITE = "is_favorite";


}
