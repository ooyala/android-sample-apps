package com.skin.ooyalaskinsampleapplication.ooyala;


import com.skin.ooyalaskinsampleapplication.PlayerSelectionOption;

/**
 * Created by anshulsachdeva on 21/02/17.
 */
public interface OoyalaListingCallback {

    void onOoyalListingDialogDismiss(PlayerSelectionOption doc, int clickedPosition);
}
