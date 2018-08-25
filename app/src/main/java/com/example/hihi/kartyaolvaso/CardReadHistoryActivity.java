package com.example.hihi.kartyaolvaso;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CardReadHistoryActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_read_history);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_card_read_history);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CardReadListAdapter(this.getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
    }

    public void openSettings(View view){
        Intent settingsIntent = new Intent(CardReadHistoryActivity.this, SettingsActivity.class);
        CardReadHistoryActivity.this.startActivity(settingsIntent);
    }
}
