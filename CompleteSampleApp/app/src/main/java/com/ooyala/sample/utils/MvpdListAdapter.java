package com.ooyala.sample.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import com.adobe.adobepass.accessenabler.models.Mvpd;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MvpdListAdapter extends ArrayAdapter<Mvpd> {
  
  private static HashMap<String, Drawable> imageCache = new HashMap<String, Drawable>();
  
  public MvpdListAdapter(Context context, List<Mvpd> mvpds) {
    super(context, 0, mvpds);
  }
  
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Mvpd mvpd = getItem(position);
    Resources r = getContext().getResources();
    
    LinearLayout layout = new LinearLayout(getContext());

    layout.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, 100));
    layout.setGravity(Gravity.CENTER_VERTICAL);
    
    ImageView image = new ImageView(getContext());
    image.setLayoutParams(new LayoutParams(
        (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 112, r.getDisplayMetrics()),
        (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 33, r.getDisplayMetrics())));

    new AsyncImageLoader().loadImageIntoView(mvpd.getLogoUrl(), image);
    layout.addView(image);
    
    TextView text = new TextView(getContext());
    int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());
    text.setPadding(padding, padding, padding, padding);
    text.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
    text.setText(mvpd.getDisplayName());
    layout.addView(text);

    return layout;
  }
  
  
  
  private class AsyncImageLoader extends AsyncTask<String, Integer, Drawable> {
    private ImageView view;
    
    
    public void loadImageIntoView(String url, ImageView view) {
      this.view = view;
      
      if(imageCache.containsKey(url)) {
        onPostExecute(imageCache.get(url));
      } else {
        execute(url);
      }
    }

    @Override
    protected Drawable doInBackground(String... urls) {
      try {
        URL imageUrl = new URL(urls[0]);
        InputStream is = (InputStream) imageUrl.getContent();
        Drawable d = Drawable.createFromStream(is, "src");
        imageCache.put(urls[0], d);
        return d;
       } catch (MalformedURLException e) {
        return null;
       } catch (IOException e) {
        return null;
       }
    }
    
    public void onPostExecute(Drawable image) {
      view.setImageDrawable(image);
    }
  }
}
