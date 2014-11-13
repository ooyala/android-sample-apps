package com.ooyala.AdobePassDemoApp;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.adobe.adobepass.accessenabler.api.AccessEnabler;
import com.adobe.adobepass.accessenabler.api.AccessEnablerException;
import com.adobe.adobepass.accessenabler.api.IAccessEnablerDelegate;
import com.adobe.adobepass.accessenabler.models.Event;
import com.adobe.adobepass.accessenabler.models.MetadataKey;
import com.adobe.adobepass.accessenabler.models.MetadataStatus;
import com.adobe.adobepass.accessenabler.models.Mvpd;
import com.ooyala.AdobePassDemoApp.crypto.SignatureGenerator;
import com.ooyala.AdobePassDemoApp.crypto.SigningCredential;
import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;

public class AdobePassLoginController implements IAccessEnablerDelegate, MvpdSelectedListener, NavigatedbackToAppListener, EmbedTokenGenerator {
  private AccessEnabler accessEnabler;
  private Context context;
  private AlertDialogFlipper flipper;
  private OnAuthorizationChangedListener authChangedListener;
  private Boolean isAuthenticating = false;
  private HashMap<String, EmbedTokenGeneratorCallback> embedTokenCallbacks;
  private String requestor;

  public AdobePassLoginController(Context context, String requestor, InputStream keystore, String keypass,
      OnAuthorizationChangedListener authChangedListener) {
    this.requestor = requestor;
    this.context = context;
    this.authChangedListener = authChangedListener;
    this.embedTokenCallbacks = new HashMap<String, EmbedTokenGeneratorCallback>();

    try {
      accessEnabler = AccessEnabler.Factory.getInstance(context);
      accessEnabler.setDelegate(new ThreadSafeAccessEnablerDelegate(this));
      String signedRequestorId = new SignatureGenerator(new SigningCredential(keystore, keypass)).generateSignature(requestor);
      ArrayList<String> spUrls = new ArrayList<String>();
      spUrls.add("sp.auth-staging.adobe.com/adobe-services");
      accessEnabler.setRequestor(requestor, signedRequestorId, spUrls);
    } catch (AccessEnablerException e) {
      e.printStackTrace();
    }
  }

  public void login() {
    accessEnabler.getAuthentication();
  }

  public void logout() {
    accessEnabler.logout();
  }

  public void checkAuth() {
    accessEnabler.checkAuthentication();
  }

  //Event Handlers
  @Override
  public void onMvpdSelected(Mvpd mvpd) {
    accessEnabler.setSelectedProvider(mvpd.getId());
  }

  @Override
  public void onNavigatedBackToApp() {
    if (isAuthenticating) {
      accessEnabler.getAuthenticationToken();
      flipper.dismiss();
      isAuthenticating = false;
    } else {
      checkAuth();
    }
  }

  //Adobe Pass Methods
  @Override
  public void setRequestorComplete(int status) {
    if (status == AccessEnabler.ACCESS_ENABLER_STATUS_ERROR) {
      showErrorDialog("Error Setting Requestor");
    }
  }

  @Override
  public void displayProviderDialog(final ArrayList<Mvpd> mvpds) {
    if (isAuthenticating) {
      return;
    }

    isAuthenticating = true;
    flipper = new AlertDialogFlipper(
        context,
        "Select Provider",
        new MvpdListAdapter(context, mvpds),
        new OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            onMvpdSelected(mvpds.get(arg2));
          }
        },
        new AlertDialogFlipper.OnPopListener() {
          @Override
          public void onPop(View v) {
            accessEnabler.setSelectedProvider(null);
            if (v instanceof WebView) {
              accessEnabler.getAuthentication();
            } else {
              isAuthenticating = false;
            }
          }
        });
  }

  @Override
  public void navigateToUrl(String url) {
    WebView w = new MvpdLoginView(context, this);
    w.loadUrl(url);
    if (flipper != null) {
      flipper.push(w);
    }
  }

  @Override
  public void setAuthenticationStatus(int status, String code) {
    if (isAuthenticating) {
      return;
    }
    if (status == AccessEnabler.ACCESS_ENABLER_STATUS_ERROR &&
        !code.equals(AccessEnabler.USER_NOT_AUTHENTICATED_ERROR) &&
        !code.equals(AccessEnabler.PROVIDER_NOT_SELECTED_ERROR)) {
      showErrorDialog("Could not authenticate");
    }
    authChangedListener.authChanged(status == AccessEnabler.ACCESS_ENABLER_STATUS_SUCCESS);
  }

  @Override
  public void setToken(String token, String resource) {
    EmbedTokenGeneratorCallback callback = embedTokenCallbacks.get(resource);
    embedTokenCallbacks.remove(resource);
    String embedToken = "/sas/embed_token/pcode/" + resource + "?auth_type=adobepass";
    try {
      embedToken += "&requestor=" + URLEncoder.encode(requestor, "UTF-8");
      embedToken += "&token=" + URLEncoder.encode(token, "UTF-8");
      embedToken += "&resource=" + URLEncoder.encode(resource, "UTF-8");

    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    callback.setEmbedToken(embedToken);
  }

  @Override
  public void tokenRequestFailed(String resource, String code, String description) {
    EmbedTokenGeneratorCallback callback = embedTokenCallbacks.get(resource);
    embedTokenCallbacks.remove(resource);
    callback.setEmbedToken("");
  }

  @Override
  public void selectedProvider(Mvpd mvpd) {
    // do nothing
  }

  @Override
  public void sendTrackingData(Event arg0, ArrayList<String> arg1) {
    //do nothing
  }

  @Override
  public void setMetadataStatus(MetadataKey arg0, MetadataStatus arg1) {
    //do nothing

  }

  @Override
  public void preauthorizedResources(ArrayList<String> arg0) {
    //do nothing
  }

  //Utility Functions
  private void showErrorDialog(String error) {
    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
    alertDialog.setTitle("Error");
    alertDialog.setMessage(error);
    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
       public void onClick(DialogInterface dialog, int which) {
       }
    });
    alertDialog.show();
  }

  @Override
  public void getTokenForEmbedCodes(List<String> embedCodes,
      EmbedTokenGeneratorCallback callback) {
    String resource = embedCodes.get(0);
    embedTokenCallbacks.put(resource, callback);
    accessEnabler.getAuthorization(resource);
  }
}
