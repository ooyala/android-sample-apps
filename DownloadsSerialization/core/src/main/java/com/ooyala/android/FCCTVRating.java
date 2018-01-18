package com.ooyala.android;

import java.util.Arrays;

import android.text.TextUtils;

/**
 * Encapsulates the UI-relevant rating data of an asset.
 */
public class FCCTVRating {

  public final String ageRestriction;
  public final String labels;
  public final String clickthrough;
  
  public FCCTVRating( String ageRestriction, String labels, String clickthrough ) {
    
    if( ageRestriction != null ) {
      ageRestriction = ageRestriction.toUpperCase().replace( "TV-", "" );
    }
    this.ageRestriction = ageRestriction;
    
    if( labels != null ) {
      labels = labels.toUpperCase().replace( ",", " " ).replace( ";", " " );
      String[] labelsArray = labels.split( "\\s+" );
      Arrays.sort( labelsArray, String.CASE_INSENSITIVE_ORDER );
      labels = TextUtils.join( "", labelsArray );
    }
    this.labels = labels;
    
    this.clickthrough = clickthrough;
  }

  @Override
  // generated with Eclipse.
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((ageRestriction == null) ? 0 : ageRestriction.hashCode());
    result = prime * result + ((clickthrough == null) ? 0 : clickthrough.hashCode());
    result = prime * result + ((labels == null) ? 0 : labels.hashCode());
    return result;
  }

  @Override
  // generated with Eclipse.
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FCCTVRating other = (FCCTVRating) obj;
    if (ageRestriction == null) {
      if (other.ageRestriction != null)
        return false;
    } else if (!ageRestriction.equals(other.ageRestriction))
      return false;
    if (clickthrough == null) {
      if (other.clickthrough != null)
        return false;
    } else if (!clickthrough.equals(other.clickthrough))
      return false;
    if (labels == null) {
      if (other.labels != null)
        return false;
    } else if (!labels.equals(other.labels))
      return false;
    return true;
  }
}
