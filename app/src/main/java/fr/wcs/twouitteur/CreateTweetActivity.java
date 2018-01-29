package fr.wcs.twouitteur;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import pl.aprilapps.easyphotopicker.EasyImage;

public class CreateTweetActivity extends AppCompatActivity {

    StorageReference mStorageRef, childRef;
    String userName, userId, tweetDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tweet);

        // UserId / UserName via sharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userName = sharedPreferences.getString("userName", userName);
        userId = sharedPreferences.getString("userId", userId);

        // Link to MainActivity
        ImageView buttonQuit = (ImageView) findViewById(R.id.buttonQuit);
        buttonQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        // Get Date
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd MMMM");
        tweetDate = sdf.format(cal.getTime());

        // Send data to firebase on button click
        final EditText tweet_text = (EditText) findViewById(R.id.tweet_text);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseRef = database.getReference("Tweet");

        Button button_twouitter = (Button) findViewById(R.id.button_twouitter);
        button_twouitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweet_text_content = tweet_text.getText().toString();
                Tweet tweet = new Tweet(tweet_text_content, userName, userId, tweetDate);
                databaseRef.push().setValue(tweet);

                startActivity(new Intent(getApplicationContext(), MainActivity.class));

                Toast.makeText(CreateTweetActivity.this, "Twouit envoyé !", Toast.LENGTH_SHORT).show();
            }
        });

        // Get Avatar image from Firebase
        mStorageRef = FirebaseStorage.getInstance().getReference();
        childRef = mStorageRef.child(userId);
        ImageView imageAvatar = (ImageView) findViewById(R.id.imageAvatar);
        Glide.with(CreateTweetActivity.this)
                .using(new FirebaseImageLoader())
                .load(childRef)
                .error(R.drawable.twitter_avatar)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageAvatar);
    }
}
