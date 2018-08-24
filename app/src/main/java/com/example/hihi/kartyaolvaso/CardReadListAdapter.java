package com.example.hihi.kartyaolvaso;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class CardReadListAdapter extends RecyclerView.Adapter<CardReadListAdapter.CardListViewHolder> {
    private String[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class CardListViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public CardListViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.textview_card_list_item);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CardReadListAdapter() {
        mDataset = new String[] {
                "Befizetés rendben.\nKód: 05200672 Olvasva: 12:14:01",
                "Nincs befizetés!\nKód: 05200581 Olvasva: 12:14:08",
                "Befizetés rendben.\nKód: 05200242 Olvasva: 12:14:15",
                "Befizetés rendben.\nKód: 05200941 Olvasva: 12:14:29",
                "Befizetés rendben.\nKód: 05200024 Olvasva: 12:14:33",
                "Befizetés rendben.\nKód: 05201133 Olvasva: 12:14:44",
                "Befizetés rendben.\nKód: 05201064 Olvasva: 12:14:51",
                "Kártya nincs használatban!\nKód: 05200282 Olvasva: 12:15:02",
                "Befizetés rendben.\nKód: 05200285 Olvasva: 12:15:11",
                "Befizetés rendben.\nKód: 05200729 Olvasva: 12:15:18",
                "Másik intézmény!\nKód: 05500332 Olvasva: 12:15:23",
                "Érvénytelen Kód!\nKód: -------- Olvasva: 12:15:31",
                "Befizetés rendben.\nKód: 05200672 Olvasva: 12:15:36",
                "Kártya már volt használva!\nKód: 05200672 Olvasva: 12:15:48"};
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardReadListAdapter.CardListViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_list_item, parent, false);

        CardListViewHolder vh = new CardListViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CardListViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset[position]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}