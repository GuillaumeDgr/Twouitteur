package fr.wcs.twouitteur;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MessagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Bottom Bar
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Link to CreateTweetActivity
        ImageView buttonWrite = (ImageView) findViewById(R.id.buttonWrite);
        buttonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CreateTweetActivity.class));
            }
        });

        TextView empty = (TextView) findViewById(R.id.empty);
    }

    // Bottom Navigation Bar
    BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    return true;
                case R.id.navigation_notifications:
                    startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
                    return true;
                case R.id.navigation_messages:
                    startActivity(new Intent(getApplicationContext(), MessagesActivity.class));
                    return true;
                case R.id.navigation_me:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    return true;
            }
            return false;
        }
    };
}
