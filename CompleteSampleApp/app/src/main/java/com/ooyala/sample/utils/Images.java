package com.ooyala.sample.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.util.Base64;

class Images {
  public static final int PLAY = 0;
  public static final int PAUSE = 1;
  public static final int FULLSCREEN = 2;
  public static final int SMALLSCREEN = 3;
  public static final int NEXT = 4;
  public static final int PREVIOUS = 5;
  public static final int CLOSED_CAPTIONS = 6;

  public static GradientDrawable gradientBackground(GradientDrawable.Orientation orientation) {
    GradientDrawable gradient = new GradientDrawable(orientation, new int[] { 0xFF000000, 0x80151515 });
    gradient.setCornerRadius(0f);
    return gradient;
  }

  public static void play(Canvas c, Paint p, int width, int height, int marginPixels) {
    Path path = new Path();
    float heightMinusMargins = height - 2f * marginPixels;
    float widthMinusMargins = width - 2f * marginPixels;
    float playButtonWidth = (float) (Math.sqrt(3) / 2f * heightMinusMargins);
    float widthStartPoint = marginPixels + (widthMinusMargins - playButtonWidth) / 2f;
    path.moveTo(widthStartPoint, marginPixels);
    path.lineTo(widthStartPoint + playButtonWidth, marginPixels + heightMinusMargins / 2f);
    path.lineTo(widthStartPoint, (float) height - (float) marginPixels);
    path.lineTo(widthStartPoint, marginPixels);
    c.drawPath(path, p);
  }

  public static void pause(Canvas c, Paint p, int width, int height, int marginPixels) {
    Path path = new Path();
    float widthMinusMargins = width - 2f * marginPixels;
    float heightMinusMargins = height - 2f * marginPixels;
    // Left rectangle
    path.moveTo(marginPixels + 1.5f * widthMinusMargins / 8f, marginPixels);
    path.lineTo(marginPixels + 3.5f * widthMinusMargins / 8f, marginPixels);
    path.lineTo(marginPixels + 3.5f * widthMinusMargins / 8f, marginPixels
        + heightMinusMargins);
    path.lineTo(marginPixels + 1.5f * widthMinusMargins / 8f, marginPixels
        + heightMinusMargins);
    path.lineTo(marginPixels + 1.5f * widthMinusMargins / 8f, marginPixels);
    // Right rectangle
    path.moveTo(marginPixels + 4.5f * widthMinusMargins / 8f, marginPixels);
    path.lineTo(marginPixels + 6.5f * widthMinusMargins / 8f, marginPixels);
    path.lineTo(marginPixels + 6.5f * widthMinusMargins / 8f, marginPixels + heightMinusMargins);
    path.lineTo(marginPixels + 4.5f * widthMinusMargins / 8f, marginPixels + heightMinusMargins);
    path.lineTo(marginPixels + 4.5f * widthMinusMargins / 8f, marginPixels);
    c.drawPath(path, p);
  }

  public static void fullscreen(Canvas c, Paint p, int width, int height, int marginPixels) {
    Path path = new Path();
    float marginX = marginPixels;
    float marginY = marginPixels;
    // square-ify margins
    if (width > height) {
      marginX += (width - height) / 2.0f;
    } else if (height > width) {
      marginY += (height - width) / 2.0f;
    }
    float widthMinusMargins = width - 2.0f * marginX;
    float heightMinusMargins = height - 2.0f * marginY;
    // Top triangle
    path.moveTo(marginX, marginY);
    path.lineTo(marginX + (widthMinusMargins / 2.0f), marginY);
    path.lineTo(marginX, marginY + (heightMinusMargins / 2.0f));
    path.lineTo(marginX, marginY);
    // Bottom triangle
    path.moveTo(marginX + widthMinusMargins, marginY + (heightMinusMargins / 2.0f));
    path.lineTo(marginX + widthMinusMargins, marginY + heightMinusMargins);
    path.lineTo(marginX + (widthMinusMargins / 2.0f), marginY + heightMinusMargins);
    path.lineTo(marginX + widthMinusMargins, marginY + (heightMinusMargins / 2.0f));
    c.drawPath(path, p);
    // Stems
    Paint linePaint = new Paint();
    linePaint.setDither(true);
    linePaint.setColor(p.getColor());
    linePaint.setStyle(Paint.Style.STROKE);
    float strokeWidth = widthMinusMargins / 5.0f;
    linePaint.setStrokeWidth(strokeWidth);
    c.drawLine(marginX + strokeWidth, marginY + strokeWidth, marginX + widthMinusMargins / 2.0f - strokeWidth
        / 2.0f, marginY + heightMinusMargins / 2.0f - strokeWidth / 2.0f, linePaint);
    c.drawLine(marginX + widthMinusMargins - strokeWidth, marginY + heightMinusMargins - strokeWidth, marginX
        + widthMinusMargins / 2.0f + strokeWidth / 2.0f, marginY + heightMinusMargins / 2.0f + strokeWidth
        / 2.0f, linePaint);
  }

  public static void smallscreen(Canvas c, Paint p, int width, int height, int marginPixels) {
    Path path = new Path();
    float marginX = marginPixels;
    float marginY = marginPixels;
    // square-ify margins
    if (width > height) {
      marginX += (width - height) / 2.0f;
    } else if (height > width) {
      marginY += (height - width) / 2.0f;
    }
    float widthMinusMargins = width - 2.0f * marginX;
    float heightMinusMargins = height - 2.0f * marginY;
    // Stems
    Paint linePaint = new Paint();
    linePaint.setDither(true);
    linePaint.setColor(p.getColor());
    linePaint.setStyle(Paint.Style.STROKE);
    float strokeWidth = widthMinusMargins / 5.0f;
    linePaint.setStrokeWidth(strokeWidth);
    c.drawLine(marginX, marginY, marginX + widthMinusMargins / 2.0f - strokeWidth, marginY
        + heightMinusMargins / 2.0f - strokeWidth, linePaint);
    c.drawLine(marginX + widthMinusMargins, marginY + heightMinusMargins, marginX + widthMinusMargins / 2.0f
        + strokeWidth, marginY + heightMinusMargins / 2.0f + strokeWidth, linePaint);
    // Top triangle
    path.moveTo(marginX + (widthMinusMargins / 2.0f) - strokeWidth / 4.0f, marginY);
    path.lineTo(marginX + (widthMinusMargins / 2.0f) - strokeWidth / 4.0f, marginY
        + (heightMinusMargins / 2.0f) - strokeWidth / 4.0f);
    path.lineTo(marginX, marginY + (heightMinusMargins / 2.0f) - strokeWidth / 4.0f);
    path.lineTo(marginX + (widthMinusMargins / 2.0f) - strokeWidth / 4.0f, marginY);
    // Bottom triangle
    path.moveTo(marginX + widthMinusMargins / 2.0f + strokeWidth / 4.0f, marginY
        + (heightMinusMargins / 2.0f) + strokeWidth / 4.0f);
    path.lineTo(marginX + widthMinusMargins, marginY + (heightMinusMargins / 2.0f) + strokeWidth / 4.0f);
    path.lineTo(marginX + widthMinusMargins / 2.0f + strokeWidth / 4.0f, marginY + heightMinusMargins);
    path.lineTo(marginX + widthMinusMargins / 2.0f + strokeWidth / 4.0f, marginY
        + (heightMinusMargins / 2.0f) + strokeWidth / 4.0f);
    c.drawPath(path, p);
  }

  public static void next(Canvas c, Paint p, int width, int height, int marginPixels) {
    Path path = new Path();
    float widthMinusMargins = width - 2f * marginPixels;
    float heightMinusMargins = height - 2f * marginPixels;
    float barWidth = (widthMinusMargins / 10f);
    // Left Arrow
    path.moveTo(marginPixels, marginPixels);
    path.lineTo(marginPixels + ((widthMinusMargins - barWidth) / 2f), marginPixels
        + (heightMinusMargins) / 2f);
    path.lineTo(marginPixels, marginPixels + heightMinusMargins);
    path.lineTo(marginPixels, marginPixels);
    // Right Arrow
    path.moveTo(marginPixels + ((widthMinusMargins - barWidth) / 2f), marginPixels);
    path.lineTo(marginPixels + widthMinusMargins - barWidth, marginPixels
        + (heightMinusMargins) / 2f);
    path.lineTo(marginPixels + ((widthMinusMargins - barWidth) / 2f), marginPixels
        + heightMinusMargins);
    path.lineTo(marginPixels + ((widthMinusMargins - barWidth) / 2f), marginPixels);
    c.drawPath(path, p);
    // Vertical Bar
    Paint linePaint = new Paint();
    linePaint.setDither(true);
    linePaint.setColor(p.getColor());
    linePaint.setStyle(Paint.Style.STROKE);
    linePaint.setStrokeWidth(barWidth);
    c.drawLine(marginPixels + (widthMinusMargins) - barWidth / 2f, marginPixels,
        marginPixels + (widthMinusMargins) - barWidth / 2f,
        marginPixels + heightMinusMargins, linePaint);
  }

  public static void previous(Canvas c, Paint p, int width, int height, int marginPixels) {
    Path path = new Path();
    float widthMinusMargins = width - 2f * marginPixels;
    float heightMinusMargins = height - 2f * marginPixels;
    float barWidth = (widthMinusMargins / 10f);
    // Vertical Bar
    Paint linePaint = new Paint();
    linePaint.setDither(true);
    linePaint.setColor(p.getColor());
    linePaint.setStyle(Paint.Style.STROKE);
    linePaint.setStrokeWidth(barWidth);
    c.drawLine(marginPixels + barWidth / 2f, marginPixels, marginPixels + barWidth
        / 2f, marginPixels + heightMinusMargins, linePaint);
    // Left Arrow
    path.moveTo(marginPixels + barWidth, marginPixels + heightMinusMargins / 2f);
    path.lineTo(marginPixels + barWidth + ((widthMinusMargins - barWidth) / 2f), marginPixels);
    path.lineTo(marginPixels + barWidth + ((widthMinusMargins - barWidth) / 2f), marginPixels
        + heightMinusMargins);
    path.lineTo(marginPixels + barWidth, marginPixels + heightMinusMargins / 2f);
    // Right Arrow
    path.moveTo(marginPixels + barWidth + ((widthMinusMargins - barWidth) / 2f), marginPixels
        + heightMinusMargins / 2f);
    path.lineTo(marginPixels + widthMinusMargins, marginPixels);
    path.lineTo(marginPixels + widthMinusMargins, marginPixels + heightMinusMargins);
    path.lineTo(marginPixels + barWidth + ((widthMinusMargins - barWidth) / 2f), marginPixels
        + heightMinusMargins / 2f);
    c.drawPath(path, p);
  }

  public static void closedCaptions(Canvas c, Paint p, int width, int height, int marginPixels) {
    String encodedImage = "iVBORw0KGgoAAAANSUhEUgAAAUAAAAE1CAMAAAChocnqAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAAZQTFRF////////VXz1bAAAAAJ0Uk5T/wDltzBKAAAEC0lEQVR42uzdQXIjMQxDUeD+l56sZ5UOKImUPva0xWcnVa1mWzKJIggABBBAAAmAAAIIIAEQQAABJAACCCCABEAAAQSQAAgggAASAAEEEEACIIAAAkgABBBAAAmAAAIIIAEQQAABBJAACCCAABIAAQQQQALgeEC1yxxANU5/QLVPZ0ANSU9AjUo7QI1LK0CNTBtAjU0PQOl5QT3KV0aoZ/mKCPWyX4WgnvYrENTTfAWEet0vFdTzfqGg8MsEhV8mKPwyQeGXCQq/TFD4ZYLCLxMEcCOghGACKCGYAEoIAngQUEIwAZQQPAF4cOysF+AcuN0rXwHYbgbyPOBgvdUNFAO6bY4CXuC3rIlKQDdPd0D7DsF6wEv41rRSBWjfI1gNeJNffTc1gPZVggBOA7TvEqwFvM2vuKMCQPs6QQAnAdr3CVYC6kVAbQS03/4KAgjgbED7SkEAAQQQQAABBPB6QPtOQQABBBBAAAEEEEAAAQQQwMcBJ1Q0BvxetKNiDOD3sh0VYwC/F+6omAP4vXJHxRzA73uWOyrmAH6fP9lRMQfw+yTtjoo5gN8fyNhRMQfw+zNBOyquA9TmijmA35+M3FEB4DOA0tfudlQA+AygPje3o+JSQG2rABBAAAEEEEAAAQQQQAABBBBANhMAZD+Q/cAUcGcF90S4qQTgr9/oe287KphMeGcygdkYprOYD0w+4A6ATKjGb1R8eVBUMQjQlf/a6yoGAfKciHlS6Tjg/y/RpWISYMsAeAcgP/0EIIAAAggggABOBeSHuAEEcDYgx2G8+BUs7IkjgQAcD8ixaI8J1vbD0ZAA3gDI8bjPCJb3whHhGwA5pH4PoO7wOwjYm3BRE/IjgqtaKAbsSriuAZUvoJ/h0sWvAGxluHrlWrSO45C7FrwYcEHWrm4ZYGvBg36/B3RfwJN+EwE1FLCt4FE/yx7+R3zWz/JwwcN+QwG1YkkbAPt9BY/7WZ4seN7PnzcfWgme9/P3i+dGgA38/IeLvzaCHfz8l6u/JoAt/Pynq5fL4t2Axi8ENH4h4D2E+c63nxb0OUDjFwLOJ6y5eXXi1tc9fCmgn/dz/EJv8xUATiQsvYF/cArgAr4iwEGG9SMkx6dRJuvVAjY3XDXE1GYuap7dKsAGn8bOBfpCQQMYARrASNAARoIGMAI0gJkggJmgAYwADWAkaAAjQQMYARrATBDATNAARoAGMBI0gJGgAYwADWAkaAABPCloACNBAxgBGsBI0ABGggYwAjSAmSCAmaABjAANYCRoACNBAxgBGsBI0ABmggBmgAbwjgAIIIAAAkgABBBAAAmAAAIIIAEQQAABJAACCCCABEAAAQSQAAgggAASAAEEEEACIIAAAkgABBBAAMlP/gkwAEQgHow8opxxAAAAAElFTkSuQmCC";

    byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    options.inDither = true;
    Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
    c.drawBitmap(Bitmap.createScaledBitmap(image, width, height,true), 0, 0, p);

  }
  public static void drawImage(int i, Context ct, Canvas c, int background, int fill, int width, int height,
      int marginDP, boolean glow) {
    c.drawColor(background);
    Paint p = new Paint();
    p.setDither(true);
    p.setAntiAlias(true);
    p.setColor(fill);
    p.setStyle(Paint.Style.FILL);
    int marginPixels = dpToPixels(ct, marginDP);
    switch (i) {
    case PLAY:
      Images.play(c, p, width, height, marginPixels);
      break;
    case PAUSE:
      Images.pause(c, p, width, height, marginPixels);
      break;
    case FULLSCREEN:
      Images.fullscreen(c, p, width, height, marginPixels);
      break;
    case SMALLSCREEN:
      Images.smallscreen(c, p, width, height, marginPixels);
      break;
    case NEXT:
      Images.next(c, p, width, height, marginPixels);
      break;
    case PREVIOUS:
      Images.previous(c, p, width, height, marginPixels);
      break;
    case CLOSED_CAPTIONS:
      Images.closedCaptions(c, p, width, height, marginPixels);
      break;
    }
    if (glow) {
      GradientDrawable glowDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
          new int[] { 0xFFFFFFFF, 0x00FFFFFF });
      glowDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
      glowDrawable.setGradientRadius(width / 2);
      glowDrawable.setBounds(0, 0, width, height);
      glowDrawable.setDither(true);
      glowDrawable.draw(c);
    }
  }

  public static int dpToPixels(Context c, int dp) {
    final float scale = c.getResources().getDisplayMetrics().density;
    return (int) (dp * scale + 0.5f);
  }
}
