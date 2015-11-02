package payment_app.mcs.com.ciniplexis.Features.adapters;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;

import payment_app.mcs.com.ciniplexis.Contracts.DataContracts;
import payment_app.mcs.com.ciniplexis.Model.MovieViewModel;

/**
 * Created by ogayle on 27/10/2015.
 */
public abstract class CursorRecyclerAdapter<ViewHolder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<ViewHolder> {

    protected boolean mDataValid;
    protected Cursor mCursor;
    protected int mRowId;


    public CursorRecyclerAdapter(Cursor cursor) {
        initialize(cursor);
    }

    private void initialize(Cursor cursor) {
        boolean isCursorPresent = (cursor != null);
        mCursor = cursor;
        mDataValid = isCursorPresent;
        mRowId = isCursorPresent ? cursor.getColumnIndex(BaseColumns._ID) : -1;
        setHasStableIds(true);
    }

    //@Override
    public final void onBindViewHolder(ViewHolder holder, int position) {

        if (!mDataValid)
            throw new IllegalStateException("Cursor is invalid or not present");

        if (!mCursor.moveToPosition(position))
            throw new IllegalStateException("Could not move cursor to position " + position);

        onBindViewHolder(holder, mCursor);
    }

    public abstract void onBindViewHolder(ViewHolder view, Cursor cursor);


    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null)
            return mCursor.getCount();
        else
            return 0;
    }


    @Override
    public long getItemId(int position) {
        if (!hasStableIds() || !mDataValid || mCursor == null)
            return RecyclerView.NO_ID;


        if (!mCursor.moveToPosition(position))
            return RecyclerView.NO_ID;

        return mCursor.getLong(mRowId);
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (mCursor == newCursor) return null;

        int itemCount = getItemCount();

        Cursor holderCursor = mCursor;
        mCursor = newCursor;
        if (newCursor == null) {
            mRowId = -1;
            mDataValid = false;
            notifyItemRangeRemoved(0, itemCount);
        } else {
            mRowId = mCursor.getColumnIndex(BaseColumns._ID);
            mDataValid = true;
            notifyDataSetChanged();
        }
        return holderCursor;
    }

    public void changeCursor(Cursor cursor) {
        Cursor current = swapCursor(cursor);
        if (current != null)
            current.close();
    }
}
