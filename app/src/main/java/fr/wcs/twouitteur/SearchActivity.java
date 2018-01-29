package fr.wcs.twouitteur;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ArrayAdapter<String> usersAdapter = null;
    private TweetAdapter adapter;
    private String friendSearched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Bottom Bar
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Link to MainActivity
        ImageView buttonBack = (ImageView) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        // Auto Completion ToolBar
        final AutoCompleteTextView SearchBarUsers = (AutoCompleteTextView) findViewById(R.id.autoCompleteSearchUsers);
        final ListView myList = findViewById(R.id.myList);

        final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mUsersDatabaseReference = mDatabase.getReference().child("User");

        final ArrayList<String> listUsers = new ArrayList<>();

        mUsersDatabaseReference.orderByChild("user_name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listUsers.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User myUser = data.getValue(User.class);
                    listUsers.add(myUser.getUser_name());
                }
                usersAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.search_box, R.id.tvHintCompletion, listUsers);
                AutoCompleteTextView autoCompleteSearchUsers = (AutoCompleteTextView) findViewById(R.id.autoCompleteSearchUsers);
                autoCompleteSearchUsers.setAdapter(usersAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ImageView buttonSearch = (ImageView) findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendSearched = SearchBarUsers.getText().toString();

                DatabaseReference mObjectDatabaseReference = mDatabase.getReference().child("Tweet");
                mObjectDatabaseReference.orderByChild("userName").equalTo(friendSearched)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ArrayList<Tweet> tweetList = new ArrayList<>();
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    Tweet twouit = data.getValue(Tweet.class);
//                                    if (!twouit.isObject_offered()){
                                        tweetList.add(0, twouit);
//                                    }
                                }
                                adapter = new TweetAdapter(getApplicationContext(), tweetList);
                                myList.setAdapter(adapter);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            }
        });
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
