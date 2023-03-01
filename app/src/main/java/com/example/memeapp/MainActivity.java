package com.example.memeapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.memeapp.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    //    for view binding
    ActivityMainBinding binding;
    //    private static final String TAG = "Swipe Position";
//    private float x1,x2,y1,y2;
//    private static int Min_Distance= 150;
//    private GestureDetector gestureDetector;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());//for binding
        setContentView(binding.getRoot());

//        binding.finalImg.gestureDetector = new GestureDetector(MainActivity.this,this);
        relativeLayout = findViewById(R.id.finalImg);

        relativeLayout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();

                getMeme();
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                getMeme();
            }

            @Override
            public void onSwipeUp()
            {
                shareMeme();
            }


        });


//        change action bar color
        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FFB600"));
        assert actionBar != null;
        actionBar.setBackgroundDrawable(colorDrawable);


        // binding used and we can write findview by id

        getMeme();
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getMeme();
            }
        });


        binding.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareMeme();
            }
        });

    }


    private void getMeme() {

//        call api
//
        String url = "https://meme-api.com/gimme";

        binding.loader.setVisibility(View.VISIBLE);// for progress bar
        binding.imgMeme.setVisibility(View.GONE); // for hide meme image

//        create request que
        RequestQueue queue = Volley.newRequestQueue(this);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String imgUrl = response.getString("url");// get url
                            //using glide library
                            Glide.with(getApplicationContext()).load(imgUrl).into(binding.imgMeme);//set img in the image view
                            binding.loader.setVisibility(View.GONE);// for hide progress bar
                            binding.imgMeme.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                       // Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        queue.add(jsonObjectRequest);//pass a json object request





    }


    private void shareMeme() {
        Bitmap image = getBitmapFromView(binding.finalImg);
        shareImageText(image);


    }

    private void shareImageText(Bitmap image) {
        Uri uri = getImageToShare(image);
        Intent intent = new Intent(Intent.ACTION_SEND);
        // putting image to uri

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        //set type of image
        intent.setType("image/png");


//        calling startActivity to share

        startActivity(Intent.createChooser(intent, "Share Image Via"));
    }

    private Uri getImageToShare(Bitmap image) {
        File imageFolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder, "meme.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(this, "com.aman.shareImage.fileProvider", file);

        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return uri;
    }

    private Bitmap getBitmapFromView(RelativeLayout imgMeme) {
        //define bitmap same height ans width
        Bitmap returnBitmap = Bitmap.createBitmap(imgMeme.getWidth(), imgMeme.getHeight(), Bitmap.Config.ARGB_8888);
//        bind the canvas it
        Canvas canvas = new Canvas(returnBitmap);
//      get a background view of layout
        Drawable background = imgMeme.getBackground();
        if (background != null) {
            background.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        imgMeme.draw(canvas);// provide getBitmap form view in this
        return returnBitmap;

    }
    //show

    @Override
    public void onBackPressed() {

        AlertDialog.Builder exitdilog = new AlertDialog.Builder(this);
        exitdilog.setTitle("Exit");
        exitdilog.setIcon(R.drawable.logout);
        exitdilog.setMessage("Are you sure want to exit");

        exitdilog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               MainActivity.super.onBackPressed();
            }
        });
        exitdilog.show();



    }
}