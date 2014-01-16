package com.theguardian.guardianglass;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity {
    private TextView titleTextView;
    private TextView trailTextView;
    private GuardianGroup group;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText("Fetching top story");
        trailTextView = (TextView) findViewById(R.id.trail);
        trailTextView.setText("");
        imageView = (ImageView) findViewById(R.id.image);
        imageView.setBackgroundDrawable(null);
        new FetchCardTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            openOptionsMenu();
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_menu_item:
                share();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void share() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        //shareIntent.putExtra(Intent.EXTRA_SUBJECT, getCard().title);
        StringBuilder text = new StringBuilder(getCard().title);
        text.append("\n\n");
        text.append(getUrl(getCard()));

        shareIntent.putExtra(Intent.EXTRA_TEXT, text.toString());
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_story_title)));
    }

    private GuardianCard getCard() {
        return group.cards[0];
    }

    private String getUrl(GuardianCard card) {
        return card.uri.replace("http://mobile-apps.guardianapis.com/uk/items/", "http://theguardian.com/");
    }

    class FetchCardTask extends AsyncTask<String, Integer, GuardianGroup>{

        @Override
        protected GuardianGroup doInBackground(String... params) {
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                URL url = new URL("http://mobile-apps.guardianapis.com/uk/groups/top-stories");
                return mapper.readValue(url, GuardianGroup.class);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(GuardianGroup result) {
            if(result == null)
                titleTextView.setText("Couldn't load card");
            else{
                group = result;
                titleTextView.setText(getCard().title);
                trailTextView.setText(Html.fromHtml(getCard().trailText));
                Picasso.with(MainActivity.this)
                        .load(getCard().getImageUri())
                        .into(imageView);
                imageView.setAlpha(0.5f);
            }
        }
    }
}
