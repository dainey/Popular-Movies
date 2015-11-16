package payment_app.mcs.com.ciniplexis.Model.DataModels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ogayle on 12/11/2015.
 */
public abstract class BaseEntity {
    @SerializedName("id")
    protected Object id;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }
}
