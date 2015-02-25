package com.ooyala.sample.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ViewFlipper;

public class AlertDialogFlipper {

  private static final Animation toLeft = new TranslateAnimation(
      Animation.RELATIVE_TO_PARENT, 0,
      Animation.RELATIVE_TO_PARENT, -1,
      Animation.RELATIVE_TO_PARENT, 0,
      Animation.RELATIVE_TO_PARENT, 0);
  private static final Animation toRight = new TranslateAnimation(
      Animation.RELATIVE_TO_PARENT, 0,
      Animation.RELATIVE_TO_PARENT, 1,
      Animation.RELATIVE_TO_PARENT, 0,
      Animation.RELATIVE_TO_PARENT, 0);
  private static final Animation fromLeft = new TranslateAnimation(
      Animation.RELATIVE_TO_PARENT, -1,
      Animation.RELATIVE_TO_PARENT, 0,
      Animation.RELATIVE_TO_PARENT, 0,
      Animation.RELATIVE_TO_PARENT, 0);
  private static final Animation fromRight = new TranslateAnimation(
      Animation.RELATIVE_TO_PARENT, 1,
      Animation.RELATIVE_TO_PARENT, 0,
      Animation.RELATIVE_TO_PARENT, 0,
      Animation.RELATIVE_TO_PARENT, 0);

  private AlertDialog dialog;
  private ViewFlipper flipper;
  private OnPopListener popListener;
  
  //create and show the dialog flipper.
  public AlertDialogFlipper(Context context, String title, ListAdapter adapter, OnItemClickListener clickListener,
      OnPopListener popListener) {
    //set up animations
    fromLeft.setDuration(500);
    fromLeft.setInterpolator(new AccelerateInterpolator());
    fromRight.setDuration(500);
    fromRight.setInterpolator(new AccelerateInterpolator());
    toLeft.setDuration(500);
    toLeft.setInterpolator(new AccelerateInterpolator());
    toRight.setDuration(500);
    toRight.setInterpolator(new AccelerateInterpolator());

    this.popListener = popListener;
    
    flipper = new ViewFlipper(context) {
      @Override
      protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Context context = getContext();
        WindowManager wm = (WindowManager) context
            .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        setMeasuredDimension(display.getWidth(), display.getHeight());
      }
    };

    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(title);
    builder.setAdapter(adapter, null);

    //use back button to go back in view hierarchy
    builder.setCancelable(false);
    builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
      @Override
      public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
          pop();
          return true;
        }
        return false;
      }
    });
 
    //create and show
    dialog = builder.show();

    //using an adapter and letting AlertDialog create a listview allows us to use private styles.
    //preferably just use a listview with your own style, this will be much cleaner than
    //hijacking the internal listview
    
    //remove from parent...
    ViewGroup parent = ((ViewGroup)dialog.getListView().getParent());
    parent.removeView(dialog.getListView());
    
    //...and add to flipper
    //first view is always the dialog's listview
    flipper.addView(dialog.getListView());
    dialog.getListView().setOnItemClickListener(clickListener);

    //then re-add flipper in place of listview
    parent.addView(flipper);

    //clear flags that prevent keyboard from showing in webview
    dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
  }
 
  public void push(View view) {
    flipper.addView(view);

    flipper.setInAnimation(fromRight);
    flipper.setOutAnimation(toLeft);
    flipper.showNext();
  }

  public void pop() {
    popListener.onPop(flipper.getCurrentView());
    if (flipper.getDisplayedChild() > 0) {
      flipper.setInAnimation(fromLeft);
      flipper.setOutAnimation(toRight);
      flipper.showPrevious();
      flipper.removeViewAt(flipper.getDisplayedChild() + 1);
    } else {
      dismiss();
    }
  }

  public void dismiss() {
    dialog.dismiss();
  }
  
  public interface OnPopListener {
    public void onPop(View v);
  }
}
