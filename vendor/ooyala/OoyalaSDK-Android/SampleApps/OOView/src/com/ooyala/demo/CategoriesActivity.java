package com.ooyala.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import com.ooyala.demo.vo.SortBy;

public class CategoriesActivity extends Activity {
    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        super.onBackPressed();
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.categories);

        findViewById(R.id.popular).setOnClickListener(new CategoryOnClickListener(SortBy.MostPopular));
        findViewById(R.id.most_rated).setOnClickListener(new CategoryOnClickListener(SortBy.MostFavorite));
        findViewById(R.id.recent).setOnClickListener(new CategoryOnClickListener(SortBy.MostRecent));
        findViewById(R.id.watch_later).setOnClickListener(new CategoryOnClickListener(SortBy.WatchLater));

    }

    class CategoryOnClickListener implements View.OnClickListener {
        private final SortBy sortBy;

        CategoryOnClickListener(final SortBy sortBy) {
            this.sortBy = sortBy;
        }

        @Override
        public void onClick(final View view) {
            UserData.SORT_BY = sortBy;
            UserData.isSort = true;
            onBackPressed();
        }
    }
}
