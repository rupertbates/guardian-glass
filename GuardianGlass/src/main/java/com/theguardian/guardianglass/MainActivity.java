package com.theguardian.guardianglass;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.glass.app.Card;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Card card = new Card(this);
        card.setText("Hello world");
        card.setFootnote("theguardian.com");
        setContentView(card.toView());
    }
}
