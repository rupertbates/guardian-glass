package com.theguardian.guardianglass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.squareup.picasso.Picasso;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    public static final String LOG_TAG = "GuardianGlass";
    private TextView titleTextView;
    private TextView timestampText;
    private GuardianGroup group;
    private ImageView imageView;
    private GestureDetector gestureDetector;
    private int currentCard;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface tf = getFont();
        titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setTypeface(tf);
        titleTextView.setText("Fetching stories...");
        timestampText = (TextView) findViewById(R.id.timestamp_text);
        timestampText.setTypeface(tf);
        timestampText.setVisibility(View.GONE);
        imageView = (ImageView) findViewById(R.id.image);
        gestureDetector = createGestureDetector(this);
        tts = new TextToSpeech(this, this);
        new FetchCardTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }


    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);
        //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                    openOptionsMenu();
                    return true;
                } else if (gesture == Gesture.TWO_TAP) {
                    // do something on two finger tap
                    return true;
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                    swipeRight();
                    return true;
                } else if (gesture == Gesture.SWIPE_LEFT) {
                    swipeLeft();
                    return true;
                }
                return false;
            }
        });
        gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
            @Override
            public void onFingerCountChanged(int previousCount, int currentCount) {
                // do something on finger count changes
            }
        });
        gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
            @Override
            public boolean onScroll(float displacement, float delta, float velocity) {
                // do something on scrolling
                return true;
            }
        });
        return gestureDetector;
    }

    private void swipeLeft() {
        if(currentCard == 0)
            return;
        currentCard--;
        setCardContent(currentCard);

    }

    private void swipeRight() {
        if(currentCard == group.cards.length - 1)
            return;
        currentCard++;
        setCardContent(currentCard);

    }

    /*
     * Send generic motion events to the gesture detector
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (gestureDetector != null) {
            return gestureDetector.onMotionEvent(event);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu_item:
                Toast.makeText(this, "Story saved to your Guardian account", Toast.LENGTH_LONG).show();
                return true;
            case R.id.read_menu_item:
                readTrail();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void readTrail() {
        tts.speak(getCard(currentCard).trailText, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void share() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        //shareIntent.putExtra(Intent.EXTRA_SUBJECT, getCard().title);
        StringBuilder text = new StringBuilder(getCard(currentCard).title);
        text.append("\n\n");
        text.append(getUrl(getCard(currentCard)));

        shareIntent.putExtra(Intent.EXTRA_TEXT, text.toString());
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_story_title)));
    }

    private GuardianCard getCard(int i) {
        return group.cards[i];
    }

    private String getUrl(GuardianCard card) {
        return card.uri.replace("http://mobile-apps.guardianapis.com/uk/items/", "http://theguardian.com/");
    }

    public Typeface getFont(){
        return Typeface.createFromAsset(getAssets(), "GdnEgyDE2Lig.otf");
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.UK);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    class FetchCardTask extends AsyncTask<String, Integer, GuardianGroup> {

        @Override
        protected GuardianGroup doInBackground(String... params) {
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                URL url = new URL("http://mobile-apps.guardianapis.com/uk/groups/section/technology"); //http://mobile-apps.guardianapis.com/uk/groups/top-stories");
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
            if (result == null)
                titleTextView.setText("Couldn't load card");
            else {
                group = result;
                setCardContent(0);
            }
        }
    }

    private void setCardContent(int i) {
        currentCard = i;
        GuardianCard card = getCard(i);
        titleTextView.setText(card.title);
        timestampText.setText(card.getDisplayTime());
        timestampText.setVisibility(View.VISIBLE);
        if(TextUtils.isEmpty(card.getImageUri())){
            imageView.setImageResource(R.drawable.loading);
            return;
        }
        Picasso.with(MainActivity.this)
                .load(card.getImageUri())
                .placeholder(R.drawable.loading)
                .error(R.drawable.loading)
                .noFade()
                .into(imageView);

    }
}
