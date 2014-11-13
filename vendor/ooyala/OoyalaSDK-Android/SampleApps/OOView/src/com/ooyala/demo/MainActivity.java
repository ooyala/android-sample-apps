package com.ooyala.demo;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieSyncManager;
import android.widget.AbsListView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.ooyala.demo.adapter.ChannelAdapter;
import com.ooyala.demo.dao.DBAdapter;
import com.ooyala.demo.task.ChanelLoadTask;
import com.ooyala.demo.task.FacebookLoadTask;
import com.ooyala.demo.utils.ImageDownloader;
import com.ooyala.demo.vo.SortBy;
import com.ooyala.demo.vo.VideoInfoVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.ooyala.demo.Constants.BITMAP;
import static com.ooyala.demo.Constants.EMBED_CODE;
import static com.ooyala.demo.Constants.PREVIEW_IMAGE_URL;
import static com.ooyala.demo.Constants.mostFavoriteComparator;
import static com.ooyala.demo.Constants.mostPopularComparator;
import static com.ooyala.demo.Constants.mostRecentComparator;
import static com.ooyala.demo.UserData.SORT_BY;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final ArrayList<VideoInfoVO> filteredChannelVideos = new ArrayList<VideoInfoVO>();
    public static final int MAIN_DISPLAYED_CHILD = 1;
    private ChannelAdapter channelAdapter;
    private DBAdapter dbAdapter;
    //    private ChannelVO activeChannel;
    private Comparator<VideoInfoVO> activeListComparator = mostRecentComparator;
    private Comparator<VideoInfoVO> activeBlockComparator = mostPopularComparator;

    private ViewFlipper viewFlipper;
    private TextView searchTextView;
    private View noVideoFoundView;
    private ListView channelVideoListView;

    static {
        UserData.imageDownloader.setMode(ImageDownloader.Mode.CORRECT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        CookieSyncManager.getInstance().startSync();
        if (UserData.originalChannelVideos != null && UserData.originalChannelVideos.size() > 0) {
            switch (SORT_BY) {
                case MostPopular:
                    sortMostPopular();
                    break;
                case MostFavorite:
                    sortMostFavorite();
                    break;
                case MostRecent:
                    sortMostRecent();
                    break;
                case WatchLater:
                    sortWatchLater();
                    break;
            }
        }

    }

    private void sortWatchLater() {
        filteredChannelVideos.clear();

        List<String> watchList = dbAdapter.findWatchList(Constants.OOYALA_API_KEY);
        for (VideoInfoVO originalChannelVideo : UserData.originalChannelVideos) {
            if (watchList.contains(originalChannelVideo.getEmbedCode())) {
                filteredChannelVideos.add(originalChannelVideo);
            }
        }
        sort(filteredChannelVideos);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        SORT_BY = SortBy.MostRecent;
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();


        dbAdapter = new DBAdapter(this);


        setContentView(R.layout.main);


        viewFlipper = (ViewFlipper) findViewById(R.id.main_flipper);
        final ImageView searchButton = (ImageView) findViewById(R.id.search);
        searchTextView = (TextView) findViewById(R.id.search_text);
        final ImageView loadingView = (ImageView) findViewById(R.id.loading);
        final View categories = findViewById(R.id.categories);
        final View noNetwork = findViewById(R.id.no_network);
        final View retryButton = findViewById(R.id.retry);
        channelVideoListView = (ListView) findViewById(R.id.channel_listview);
        noVideoFoundView = findViewById(R.id.no_results);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                onSearchRequested();
            }
        });


        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation outAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        animation.setDuration(700);
        outAnimation.setDuration(700);
        viewFlipper.setAnimateFirstView(true);
        viewFlipper.setAnimation(animation);
        viewFlipper.setInAnimation(animation);
        viewFlipper.setOutAnimation(outAnimation);

        animation = AnimationUtils.loadAnimation(this, R.anim.main_load);

        loadingView.setAnimation(animation);

        channelAdapter = new ChannelAdapter(MainActivity.this, UserData.FACEBOOK, UserData.imageDownloader);


        categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(new Intent(MainActivity.this, CategoriesActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


        channelAdapter.setOnItemClickListener(new ChannelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Bitmap bitmap, final VideoInfoVO videoInfoVO) {
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                intent.putExtra(BITMAP, bitmap);
                intent.putExtra(EMBED_CODE, videoInfoVO.getEmbedCode());
                intent.putExtra(PREVIEW_IMAGE_URL, videoInfoVO.getThumbnail());
                intent.putExtra(Constants.NAME, videoInfoVO.getTitle());

                startActivity(intent);
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
            }
        });

        channelVideoListView.setAdapter(channelAdapter);
        channelVideoListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_FLING && scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    channelAdapter.flinging = false;
                    int count = view.getChildCount();

                    for (int i = 0; i < count; i++) {

                        VideoInfoVO[] videoInfoVOs = (VideoInfoVO[]) view.getItemAtPosition(i);
                        if (videoInfoVOs.length == 1) {
                            View convertView = view.getChildAt(i);
                            ChannelAdapter.VideoHolder videoHolder = (ChannelAdapter.VideoHolder) convertView.getTag();
                            videoHolder.setLikes(videoInfoVOs[0]);
                        }
                    }
                } else {
                    channelAdapter.flinging = true;
                }
            }

            @Override
            public void onScroll(final AbsListView absListView, final int i, final int i1, final int i2) {
            }
        });
        channelAdapter.setObjects(UserData.originalChannelVideos, mostPopularComparator);

        if (!handleIntent(getIntent())) {
            final ChanelLoadTask chanelLoadTask = new ChanelLoadTask(this, channelAdapter, UserData.originalChannelVideos, viewFlipper) {
                @Override
                protected void onPostExecute(final Boolean result) {
                    super.onPostExecute(result);
                    if (result) {
                        noNetwork.setVisibility(View.GONE);
                        channelVideoListView.setVisibility(View.VISIBLE);

                        //noinspection unchecked
                        new FacebookLoadTask(UserData.originalChannelVideos).execute();
                    } else {
                        noNetwork.setVisibility(View.VISIBLE);
                        channelVideoListView.setVisibility(View.GONE);
                    }

                }
            };
            //noinspection unchecked
            chanelLoadTask.execute();
        }
    }


    @Override
    public boolean onSearchRequested() {
        if (viewFlipper.getDisplayedChild() != MAIN_DISPLAYED_CHILD) {
            return false;
        }
        Bundle appData = new Bundle();

        startSearch(null, false, appData, false);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private boolean handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String searchQuery = intent.getStringExtra(SearchManager.QUERY);

            viewFlipper.setDisplayedChild(MAIN_DISPLAYED_CHILD);
            searchTextView.setVisibility(View.VISIBLE);
            searchTextView.setText(getString(R.string.search_results_text, searchQuery));

            channelAdapter.getFilter().filter(searchQuery, new Filter.FilterListener() {
                @Override
                public void onFilterComplete(final int count) {
                    if (count == 0) {
                        channelVideoListView.setVisibility(View.GONE);
                        noVideoFoundView.setVisibility(View.VISIBLE);
                    } else {
                        channelVideoListView.setVisibility(View.VISIBLE);
                        noVideoFoundView.setVisibility(View.GONE);
                    }
                }
            });

            return true;
        }
        return false;
    }


    private void sortMostPopular() {
        activeListComparator = mostPopularComparator;
        activeBlockComparator = mostRecentComparator;
        if (!UserData.isSort) {
            return;
        }

        sort(UserData.originalChannelVideos);
    }


    private void sortMostFavorite() {
        activeListComparator = mostFavoriteComparator;
        activeBlockComparator = mostPopularComparator;

        if (!UserData.isSort) {
            return;
        }

        sort(UserData.originalChannelVideos);
    }

    private void sortMostRecent() {
        activeListComparator = mostRecentComparator;
        activeBlockComparator = mostPopularComparator;

        if (!UserData.isSort) {
            return;
        }

        sort(UserData.originalChannelVideos);
    }

    private void sort(final List<VideoInfoVO> videos) {
        searchTextView.setVisibility(View.GONE);
        searchTextView.setText(Constants.EMPTY);

        Collections.sort(videos, activeListComparator);
        channelAdapter.setObjects(videos, activeBlockComparator);
        channelAdapter.notifyDataSetChanged();
        UserData.isSort = false;
    }

}
