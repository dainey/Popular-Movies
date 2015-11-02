package payment_app.mcs.com.ciniplexis.Interfaces;

/**
 * Created by ogayle on 27/10/2015.
 */
public interface MovieCallback {
    void reserveMovieTicket(int movieId);
    void removeReservation(int movieId);
}
