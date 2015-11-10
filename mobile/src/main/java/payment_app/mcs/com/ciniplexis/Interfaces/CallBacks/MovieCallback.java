package payment_app.mcs.com.ciniplexis.Interfaces.CallBacks;

import payment_app.mcs.com.ciniplexis.Model.DataModels.MovieDataModel;

/**
 * Created by ogayle on 27/10/2015.
 */
public interface MovieCallback {
    void updateMovieTicket(int movieId, int amount, double cost);

    void updateFavorites(MovieDataModel movie);

}
