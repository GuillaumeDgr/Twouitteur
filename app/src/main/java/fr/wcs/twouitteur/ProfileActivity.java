package fr.wcs.twouitteur;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import pl.aprilapps.easyphotopicker.EasyImage;

public class ProfileActivity extends AppCompatActivity {

    StorageReference mStorageRef, childRef;
    String userId;
    byte[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // UserId / UserName via sharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = sharedPreferences.getString("userId", userId);

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

        // Avatar and Alert Dialog
        ImageView imageviewAvatar = (ImageView) findViewById(R.id.imageviewAvatar);
        imageviewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder
                        .setTitle("Twouitteur")
                        .setPositiveButton("Choisir depuis la galerie",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    EasyImage.openGallery(ProfileActivity.this, 0);
                                }
                            })
                        .setNegativeButton("Prendre une nouvelle photo", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EasyImage.openCamera(ProfileActivity.this, 0);
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

        // Get Avatar image from Firebase
        mStorageRef = FirebaseStorage.getInstance().getReference();
        childRef = mStorageRef.child(userId);
        ImageView imageAvatar = (ImageView) findViewById(R.id.imageviewAvatar);
        Glide.with(ProfileActivity.this)
                .using(new FirebaseImageLoader())
                .load(childRef)
                .error(R.drawable.twitter_avatar)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageAvatar);
    }

    // Avatar and Alert Dialog
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                Toast.makeText(ProfileActivity.this, getString(R.string.erreur_photo),
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                ImageView imageviewAvatar = (ImageView) findViewById(R.id.imageviewAvatar);
                imageviewAvatar.setImageDrawable(Drawable.createFromPath(imageFile.getPath()));

                // Send to Firebase
                Drawable avatarDrawable = imageviewAvatar.getDrawable();
                Bitmap avatar = ((BitmapDrawable) avatarDrawable).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                avatar.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                // Firebase
                mStorageRef = FirebaseStorage.getInstance().getReference();
                childRef = mStorageRef.child(userId);
                UploadTask uploadTask = childRef.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ProfileActivity.this, getString(R.string.upload_photo),
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, getString(R.string.upload_photo_pb),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                Toast.makeText(ProfileActivity.this, getString(R.string.annuler_photo),
                        Toast.LENGTH_SHORT).show();
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
