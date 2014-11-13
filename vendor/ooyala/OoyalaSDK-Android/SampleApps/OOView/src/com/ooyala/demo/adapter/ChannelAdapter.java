package com.ooyala.demo.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.ooyala.demo.Constants;
import com.ooyala.demo.R;
import com.ooyala.demo.social.Facebook;
import com.ooyala.demo.social.LikeWebClient;
import com.ooyala.demo.utils.ImageDownloader;
import com.ooyala.demo.vo.VideoInfoVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChannelAdapter extends BaseAdapter implements Filterable {
    public static final float IMAGE_ITEM_RATIO = 16f / 10f;
    public static final String TAG = ChannelAdapter.class.getSimpleName();

    public static final int TYPE_ONE = 0;
    public static final int TYPE_FOUR = 1;
    public static final int TYPE_MAX_COUNT = TYPE_FOUR + 1;

    private OnItemClickListener onItemClickListener;

    private static int width;
    private static int height;

    private static int smallWidth;
    private static int smallHeight;
    private List<VideoInfoVO[]> objects = new ArrayList<VideoInfoVO[]>();
    private Activity activity;

    private LayoutInflater layoutInflater;
    private List<VideoInfoVO> flatObjects = new ArrayList<VideoInfoVO>();

    private List<VideoInfoVO> originalFlatObjects;
    private Comparator<VideoInfoVO> comparator;

    private Facebook facebook;

    private ImageDownloader imageDownloader;

    private final Object mLock = new Object();
    public boolean flinging;
    private final int imageMargin;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(final CharSequence prefix) {
                FilterResults results = new FilterResults();

                if (prefix == null || prefix.length() == 0) {
                    ArrayList<VideoInfoVO> list;
                    synchronized (mLock) {
                        list = new ArrayList<VideoInfoVO>(originalFlatObjects);
                    }
                    results.values = list;
                    results.count = list.size();
                } else {
                    String prefixString = prefix.toString().toLowerCase();

                    ArrayList<VideoInfoVO> values;
                    synchronized (mLock) {
                        values = new ArrayList<VideoInfoVO>(originalFlatObjects);
                    }

                    final ArrayList<VideoInfoVO> newValues = new ArrayList<VideoInfoVO>();

                    for (final VideoInfoVO value : values) {
                        final String valueText = value.getTitle().toLowerCase();

                        if (valueText.contains(prefixString)) {
                            newValues.add(value);
                        } else {
                            final String[] words = valueText.split(" ");

                            for (final String word : words) {
                                if (word.contains(prefixString)) {
                                    newValues.add(value);
                                    break;
                                }
                            }
                        }
                    }

                    results.values = newValues;
                    results.count = newValues.size();

                }
                return results;
            }

            @Override
            protected void publishResults(final CharSequence charSequence, final FilterResults results) {
                if (results.values instanceof List) {
                    flatObjects.clear();
                    for (Object videoInfoVO : (List) results.values) {
                        flatObjects.add((VideoInfoVO) videoInfoVO);
                    }
                }
                setObjects(flatObjects);
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }


    public interface OnItemClickListener {
        void onItemClick(final Bitmap bitmap, VideoInfoVO videoInfoVO);
    }


    public ChannelAdapter(Activity activity, Facebook facebook, final ImageDownloader imageDownloader) {
        this.activity = activity;
        this.facebook = facebook;
        this.imageDownloader = imageDownloader;


        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        WindowManager systemService = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        imageMargin = activity.getResources().getDimensionPixelSize(R.dimen.item_image_margin);
        int fullWidth = systemService.getDefaultDisplay().getWidth();
        width = fullWidth - 2 * imageMargin;


        height = Math.round(width / IMAGE_ITEM_RATIO) - 2 * imageMargin;


        smallWidth = Math.round(width / 2f);

        smallHeight = Math.round(height / 2f) - 4 * imageMargin;
        imageDownloader.setMode(ImageDownloader.Mode.CORRECT);

    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setObjects(List<VideoInfoVO> flatObjects, final Comparator<VideoInfoVO> comparator) {
        this.flatObjects = new ArrayList<VideoInfoVO>(flatObjects);
        this.originalFlatObjects = new ArrayList<VideoInfoVO>(flatObjects);
        this.comparator = comparator;
        this.objects = generateGridList(flatObjects, comparator);
    }

    private void setObjects(List<VideoInfoVO> flatObjects) {
        this.flatObjects = flatObjects;
        this.objects = generateGridList(flatObjects, comparator);
    }


    private List<VideoInfoVO[]> generateGridList(final List<VideoInfoVO> videoList, final Comparator<VideoInfoVO> comparator) {
        List<VideoInfoVO[]> channelVideos = new ArrayList<VideoInfoVO[]>();
        int row = 0;


        for (int pos = 0; pos < videoList.size(); ) {
            final List<VideoInfoVO> subList;
            if (videoList.size() > pos + 5) {
                subList = new ArrayList<VideoInfoVO>(videoList.subList(pos, pos + 5));
            } else {
                subList = new ArrayList<VideoInfoVO>(videoList.subList(pos, videoList.size()));
            }

            int subListSize = subList.size();
            pos += subListSize;
            Collections.sort(subList, comparator);

            for (int subIndex = 0; subIndex < subListSize; ) {
                row++;
                final VideoInfoVO[] videoInfoVOs;
                if (row % 2 != 0) {
                    videoInfoVOs = new VideoInfoVO[]{subList.get(subIndex++)};
                } else {
                    int blockSize = subListSize - subIndex;
                    videoInfoVOs = new VideoInfoVO[blockSize];

                    for (int i = 0; i < blockSize; i++) {
                        videoInfoVOs[i] = subList.get(subIndex++);

                    }
                }
                channelVideos.add(videoInfoVOs);
            }

        }
        return channelVideos;
    }

    @Override
    public int getItemViewType(final int position) {
        return getItem(position).length == 1 ? TYPE_ONE : TYPE_FOUR;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public VideoInfoVO[] getItem(final int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final VideoHolder videoHolder;

        VideoInfoVO[] item = getItem(position);
        int viewType = item.length == 1 ? TYPE_ONE : TYPE_FOUR;
        if (convertView == null) {
            long cTime = System.currentTimeMillis();
            switch (viewType) {
                case TYPE_ONE:
                    convertView = layoutInflater.inflate(R.layout.video_block_item_one, parent, false);
                    break;
                case TYPE_FOUR:
                    convertView = layoutInflater.inflate(R.layout.video_block_item_four, parent, false);
                    break;
            }

            videoHolder = new VideoHolder(convertView);
            assert convertView != null;
            convertView.setTag(videoHolder);
            videoHolder.fill(true, item);
//            Log.d(TAG, "inflate [ " + (System.currentTimeMillis() - cTime) + " msec ]");
        } else {

            videoHolder = (VideoHolder) convertView.getTag();

            videoHolder.fill(item);
        }


        return convertView;

    }

    public class VideoHolder {
        private final ImageView videoThumb1;
        private final ImageView videoThumb2;
        private final ImageView videoThumb3;
        private final ImageView videoThumb4;
        private final ImageView videoThumbFull;

        private final TextView videoTitle1;
        private final TextView videoTitle2;
        private final TextView videoTitle3;
        private final TextView videoTitle4;
        private final TextView videoTitleFull;
        private final WebView likesFull;

        private final View tp1;
        private final View tp2;
        private final View tp3;
        private final View tp4;
        private final View tpFull;

        private final View v1;
        private final View v2;
        private final View v3;
        private final View v4;


        VideoHolder(final View base) {

            this.tp1 = base.findViewById(R.id.tp1);
            this.tp2 = base.findViewById(R.id.tp2);
            this.tp3 = base.findViewById(R.id.tp3);
            this.tp4 = base.findViewById(R.id.tp4);

            this.v1 = base.findViewById(R.id.v1);
            this.v2 = base.findViewById(R.id.v2);
            this.v3 = base.findViewById(R.id.v3);
            this.v4 = base.findViewById(R.id.v4);

            this.tpFull = base.findViewById(R.id.tpFull);

            this.videoThumb1 = (ImageView) base.findViewById(R.id.video_thumb_webview1);
            this.videoThumb2 = (ImageView) base.findViewById(R.id.video_thumb_webview2);
            this.videoThumb3 = (ImageView) base.findViewById(R.id.video_thumb_webview3);
            this.videoThumb4 = (ImageView) base.findViewById(R.id.video_thumb_webview4);
            this.videoThumbFull = (ImageView) base.findViewById(R.id.video_thumb_webviewFull);


            this.videoTitle1 = (TextView) base.findViewById(R.id.video_1_title);
            this.videoTitle2 = (TextView) base.findViewById(R.id.video_2_title);
            this.videoTitle3 = (TextView) base.findViewById(R.id.video_3_title);
            this.videoTitle4 = (TextView) base.findViewById(R.id.video_4_title);
            this.videoTitleFull = (TextView) base.findViewById(R.id.video_full_title);

            if (this.videoThumb1 != null) {
                this.videoThumb1.setOnClickListener(new OnVideoThumbClickListener(videoTitle1));
                this.videoThumb2.setOnClickListener(new OnVideoThumbClickListener(videoTitle2));
                this.videoThumb3.setOnClickListener(new OnVideoThumbClickListener(videoTitle3));
                this.videoThumb4.setOnClickListener(new OnVideoThumbClickListener(videoTitle4));
            } else if (this.videoThumbFull != null) {
                this.videoThumbFull.setOnClickListener(new OnVideoThumbClickListener(videoTitleFull));

            }

            this.likesFull = (WebView) base.findViewById(R.id.facebook_like_full);
            if (this.likesFull != null) {
                this.likesFull.getSettings().setJavaScriptEnabled(true);
                this.likesFull.setBackgroundColor(Constants.TRANSPARENT);
                this.likesFull.setFocusableInTouchMode(false);
                this.likesFull.setFocusable(false);
                this.likesFull.getSettings().setAppCacheEnabled(true);

                this.likesFull.setWebViewClient(new LikeWebClient(activity, facebook, ChannelAdapter.this));
            }


        }


        public void fill(VideoInfoVO... videoInfoVOs) {
            fill(false, videoInfoVOs);
        }


        public void fill(final boolean requestLayout, VideoInfoVO... videoInfoVOs) {
            if (requestLayout) {
                if (videoInfoVOs.length > 1) {
                    ViewGroup.LayoutParams layoutParams1 = videoThumb1.getLayoutParams();
                    ViewGroup.LayoutParams layoutParams2 = videoThumb2.getLayoutParams();
                    ViewGroup.LayoutParams layoutParams3 = videoThumb3.getLayoutParams();
                    ViewGroup.LayoutParams layoutParams4 = videoThumb4.getLayoutParams();

                    ViewGroup.LayoutParams tp1lp = tp1.getLayoutParams();
                    ViewGroup.LayoutParams tp2lp = tp2.getLayoutParams();
                    ViewGroup.LayoutParams tp3lp = tp3.getLayoutParams();
                    ViewGroup.LayoutParams tp4lp = tp4.getLayoutParams();


                    tp1lp.width = tp2lp.width = tp3lp.width = tp4lp.width = layoutParams1.width = layoutParams2.width = layoutParams3.width = layoutParams4.width = smallWidth;
                    layoutParams1.height = layoutParams2.height = layoutParams3.height = layoutParams4.height = smallHeight;

                    videoThumb1.requestLayout();
                    videoThumb2.requestLayout();
                    videoThumb3.requestLayout();
                    videoThumb4.requestLayout();

                    videoTitle1.requestLayout();
                    videoTitle2.requestLayout();
                    videoTitle3.requestLayout();
                    videoTitle4.requestLayout();
                } else {
                    ViewGroup.LayoutParams layoutParams = videoThumbFull.getLayoutParams();
                    ViewGroup.LayoutParams vTitleLP = videoTitleFull.getLayoutParams();

                    ViewGroup.LayoutParams tpFulllp = tpFull.getLayoutParams();
                    tpFulllp.width = layoutParams.width = width;
                    vTitleLP.width = width - activity.getResources().getDimensionPixelSize(R.dimen.web_view_width) - 4 * imageMargin;
                    layoutParams.height = height;
                    videoThumbFull.requestLayout();
                    videoTitleFull.requestLayout();
                }


            }

            if (videoInfoVOs.length == 1) {

                setTitle(videoInfoVOs[0], this.videoTitleFull);
                if (!flinging) {
                    setLikes(videoInfoVOs[0], this.likesFull);
                }
                imageDownloader.download(videoInfoVOs[0].getThumbnail(), videoThumbFull);
            } else {
                if (videoInfoVOs.length > 0 && videoInfoVOs[0] != null) {
                    if (v1.getVisibility() != View.VISIBLE) {
                        v1.setVisibility(View.VISIBLE);
                    }
                    setTitle(videoInfoVOs[0], videoTitle1);
                    imageDownloader.download(videoInfoVOs[0].getThumbnail(), videoThumb1);
                } else {
                    v1.setVisibility(View.GONE);
                }

                if (videoInfoVOs.length > 1 && videoInfoVOs[1] != null) {
                    if (v2.getVisibility() != View.VISIBLE) {
                        v2.setVisibility(View.VISIBLE);
                    }

                    setTitle(videoInfoVOs[1], videoTitle2);
                    imageDownloader.download(videoInfoVOs[1].getThumbnail(), videoThumb2);
                } else {
                    v2.setVisibility(View.GONE);
                }

                if (videoInfoVOs.length > 2 && videoInfoVOs[2] != null) {
                    if (v3.getVisibility() != View.VISIBLE) {
                        v3.setVisibility(View.VISIBLE);
                    }

                    setTitle(videoInfoVOs[2], videoTitle3);
                    imageDownloader.download(videoInfoVOs[2].getThumbnail(), videoThumb3);
                } else {
                    v3.setVisibility(View.GONE);
                }

                if (videoInfoVOs.length > 3 && videoInfoVOs[3] != null) {
                    if (v4.getVisibility() != View.VISIBLE) {
                        v4.setVisibility(View.VISIBLE);
                    }

                    setTitle(videoInfoVOs[3], videoTitle4);
                    imageDownloader.download(videoInfoVOs[3].getThumbnail(), videoThumb4);
                } else {
                    v4.setVisibility(View.GONE);
                }
            }
        }

        public void setLikes(final VideoInfoVO videoInfo) {
            setLikes(videoInfo, likesFull);
        }

        private void setLikes(final VideoInfoVO videoInfo, final WebView webView) {
            if (videoInfo != null && webView != null) {
                Object tag = webView.getTag();
                String url = String.format("https://www.facebook.com/plugins/like.php?locale=en_US&href=%s&send=false&layout=button_count&show_faces=false&action=like&colorscheme=light", videoInfo.getEncodedThumbnail());
                if (tag != null && tag.equals(url)) {
                    webView.reload();
                } else {
                    webView.setTag(url);
                    webView.loadUrl(url);
                }
            }
        }


        private void setTitle(final VideoInfoVO videoInfo, final TextView textView) {
            if (videoInfo != null) {
                textView.setTag(videoInfo);

                textView.setText(videoInfo.getTitle());
            }
        }
    }


    class OnVideoThumbClickListener implements View.OnClickListener {
        private final View view;

        OnVideoThumbClickListener(final View view) {
            this.view = view;
        }


        @Override
        public void onClick(final View view) {
            ImageView imageView = ((ImageView) view);
            final Bitmap drawingCache;
            if (imageView.getDrawable() instanceof BitmapDrawable) {
                drawingCache = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                onItemClickListener.onItemClick(drawingCache, (VideoInfoVO) this.view.getTag());
            } else {
//                imageDownloader.download(imageView.getTag().toString(), imageView);
            }

        }
    }
}