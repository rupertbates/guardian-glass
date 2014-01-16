package com.theguardian.guardianglass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
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

public class MainActivity extends Activity {
    public static final String LOG_TAG = "GuardianGlass";
    private TextView titleTextView;
    private TextView trailTextView;
    private GuardianGroup group;
    private ImageView imageView;
    private GestureDetector gestureDetector;
    private int currentCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface tf = getFont();
        titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setTypeface(tf);
        titleTextView.setText("Fetching top story");
        trailTextView = (TextView) findViewById(R.id.trail);
        trailTextView.setTypeface(tf);
        trailTextView.setText("");
        imageView = (ImageView) findViewById(R.id.image);
        imageView.setBackgroundDrawable(null);
        gestureDetector = createGestureDetector(this);
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
                    // do something on tap
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
        if(currentCard >= group.cards.length)
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

    class FetchCardTask extends AsyncTask<String, Integer, GuardianGroup> {

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
        trailTextView.setText(Html.fromHtml(card.trailText));
        Picasso.with(MainActivity.this)
                .load(card.getImageUri())
                .placeholder(R.drawable.guardian_icon)
                .noFade()
                .into(imageView);

    }
}
