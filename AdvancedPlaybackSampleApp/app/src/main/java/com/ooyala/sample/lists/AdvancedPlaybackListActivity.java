package com.ooyala.sample.lists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ooyala.sample.R;
import com.ooyala.sample.players.AudioOnlyPlayerActivity;
import com.ooyala.sample.players.ChangeVideoPlayerActivity;
import com.ooyala.sample.players.CustomControlsPlayerActivity;
import com.ooyala.sample.players.CustomOverlayPlayerActivity;
import com.ooyala.sample.players.InsertAdPlayerActivity;
import com.ooyala.sample.players.MultipleVideosPlayerActivity;
import com.ooyala.sample.players.NotificationsPlayerActivity;
import com.ooyala.sample.players.PlayWithInitialTimePlayerActivity;
import com.ooyala.sample.players.PluginPlayerActivity;
import com.ooyala.sample.players.ProgrammaticVolumePlayerActivity;
import com.ooyala.sample.players.ReinitPlayerActivity;
import com.ooyala.sample.players.ReinitSkinPlayerActivity;
import com.ooyala.sample.players.SampleVideoPlayerActivity;
import com.ooyala.sample.players.SetEmbedCodePlayerActivity;
import com.ooyala.sample.players.UnbundledPlayerActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;

import java.util.LinkedHashMap;
import java.util.Map;

public class AdvancedPlaybackListActivity extends Activity implements OnItemClickListener {
  public final static String getName() {
    return "Advanced Playback";
  }

  private static Map<String, PlayerSelectionOption> selectionMap;
  ArrayAdapter<String> selectionAdapter;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getName());

    selectionMap = new LinkedHashMap<String, PlayerSelectionOption>();
    //Populate the embed map
    selectionMap.put(PlayWithInitialTimePlayerActivity.getName(), new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", PlayWithInitialTimePlayerActivity.class) );
    selectionMap.put(MultipleVideosPlayerActivity.getName(), new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", MultipleVideosPlayerActivity.class) );
    selectionMap.put(InsertAdPlayerActivity.getName(), new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", InsertAdPlayerActivity.class) );
    selectionMap.put(ChangeVideoPlayerActivity.getName(), new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", ChangeVideoPlayerActivity.class) );
    selectionMap.put(PluginPlayerActivity.getName(), new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", PluginPlayerActivity.class));
    selectionMap.put(SampleVideoPlayerActivity.getName(), new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", SampleVideoPlayerActivity.class));
    selectionMap.put(CustomControlsPlayerActivity.getName(), new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", CustomControlsPlayerActivity.class) );
    selectionMap.put(CustomOverlayPlayerActivity.getName(), new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", CustomOverlayPlayerActivity.class) );
    selectionMap.put(UnbundledPlayerActivity.getName(), new PlayerSelectionOption("http://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", UnbundledPlayerActivity.class));
    selectionMap.put(NotificationsPlayerActivity.getName(), new PlayerSelectionOption("92cWp0ZDpDm4Q8rzHfVK6q9m6OtFP-ww", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", NotificationsPlayerActivity.class) );
    selectionMap.put(ProgrammaticVolumePlayerActivity.getName(), new PlayerSelectionOption("Y1ZHB1ZDqfhCPjYYRbCEOz0GR8IsVRm1", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", ProgrammaticVolumePlayerActivity.class) );
    selectionMap.put(ReinitPlayerActivity.getName(), new PlayerSelectionOption("E4bDRwZTE6rMB8oYrzOsuHSPz0XM0dAV", "BzY2syOq6kIK6PTXN7mmrGVSJEFj", "http://www.ooyala.com", ReinitPlayerActivity.class) );
    selectionMap.put(ReinitSkinPlayerActivity.getName(), new PlayerSelectionOption("E4bDRwZTE6rMB8oYrzOsuHSPz0XM0dAV", "BzY2syOq6kIK6PTXN7mmrGVSJEFj", "http://www.ooyala.com", ReinitSkinPlayerActivity.class) );
    selectionMap.put(SetEmbedCodePlayerActivity.getName(), new PlayerSelectionOption("oza2pxODE6Vm_TTHPKJ68gBD8JQ7oTuj", "c0cTkxOqALQviQIGAHWY5hP0q9gU", "http://www.ooyala.com", SetEmbedCodePlayerActivity.class) );
    selectionMap.put(AudioOnlyPlayerActivity.getNameM4a(), new PlayerSelectionOption("A3aTBmZzE6bzQUkQlDOrUu4cjfOlCGPa", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53", "http://www.ooyala.com", AudioOnlyPlayerActivity.class) );
    selectionMap.put(AudioOnlyPlayerActivity.getNameHls(), new PlayerSelectionOption("NybzBmZzE6n6LYZgsxJNthUnAn1_xrcV", "Q1bW0yOsRxnrzAjzXI2wUlZp9h53", "http://www.ooyala.com", AudioOnlyPlayerActivity.class) );

    setContentView(com.ooyala.sample.R.layout.list_activity_layout);

    //Create the adapter for the ListView
    selectionAdapter = new ArrayAdapter<String>(this, R.layout.list_activity_list_item);
    for(String key : selectionMap.keySet()) {
      selectionAdapter.add(key);
    }
    selectionAdapter.notifyDataSetChanged();

    //Load the data into the ListView
    ListView selectionListView = (ListView) findViewById(R.id.mainActivityListView);
    selectionListView.setAdapter(selectionAdapter);
    selectionListView.setOnItemClickListener(this);
  }

  @Override
  public void onItemClick(AdapterView<?> l, View v, int pos, long id) {
    PlayerSelectionOption selection =  selectionMap.get(selectionAdapter.getItem(pos));
    Class<? extends Activity> selectedClass = selection.getActivity();

    //Launch the correct activity with the embed code as an extra
    Intent intent = new Intent(this, selectedClass);
    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    intent.putExtra("embed_code", selection.getEmbedCode());
    intent.putExtra("pcode", selection.getPcode());
    intent.putExtra("domain", selection.getDomain());
    startActivity(intent);
    return;
  }
}