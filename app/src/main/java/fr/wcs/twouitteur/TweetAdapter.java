package fr.wcs.twouitteur;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.ArrayList;

/**
 * Created by wilder on 30/10/17.
 */

public class TweetAdapter extends BaseAdapter {

    String tweetKey;
    StorageReference mStorageRef, childRef;

    private Context context;
    private ArrayList<Tweet> item;

    public TweetAdapter(Context context, ArrayList<Tweet> item){
        this.context = context;
        this.item = item;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int i) {
        return item.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, final ViewGroup viewGroup) {
        if (convertView == null){
            convertView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.layout_tweet, viewGroup, false);
        }

        final Tweet currentItem = (Tweet) getItem(i);
        final int position = i;

        TextView textTweet= convertView.findViewById(R.id.myTweet);
        TextView userNameTweet= convertView.findViewById(R.id.myTweetUserName);
        TextView dateTweet= convertView.findViewById(R.id.myTweetDate);
        ImageButton tweetArrow = convertView.findViewById(R.id.tweetArrow);
        ImageView imageItem = convertView.findViewById(R.id.imageItem);

        textTweet.setText(currentItem.getTweetText());
        userNameTweet.setText(currentItem.getUserName());
        dateTweet.setText(currentItem.getTweetDate());
        String userTweetId = currentItem.getUserTweetId();

        // Get Avatar image from Firebase
        mStorageRef = FirebaseStorage.getInstance().getReference();
        childRef = mStorageRef.child(userTweetId);
        Glide.with(viewGroup.getContext())
                .using(new FirebaseImageLoader())
                .load(childRef)
                .error(R.drawable.twitter_avatar)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageItem);

        tweetArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(viewGroup.getContext());
                builder
                        .setTitle("Twouitteur")
                        .setPositiveButton("Partager ce tweet",
                                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, currentItem.getTweetText());
                                sendIntent.setType("text/plain");
                                context.startActivity(sendIntent);
                            }
                        })
                        .setNegativeButton("Supprimer ce tweet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                DatabaseReference refTweet = FirebaseDatabase.getInstance().getReference();
                                refTweet.child("Tweet")
                                        .orderByChild("tweetText")
                                        .equalTo(currentItem.getTweetText())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                        tweetKey = childSnapshot.getKey();
                                                        DatabaseReference deleteTweet = FirebaseDatabase.getInstance()
                                                                .getReference().child("Tweet").child(tweetKey);
                                                        deleteTweet.removeValue();
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            });
                            }
                        })
                        .setNeutralButton("Annuler", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(R.drawable.twitter_bird)
                        .show();
            }
        });
        return convertView;
    }
}