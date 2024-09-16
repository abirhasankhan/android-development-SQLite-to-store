package edu.ewubd.assignment01_abir013;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ContactFormActivity extends AppCompatActivity {

    TextView tvError;
    EditText etName, etEmail, etPhoneHome, etPhoneOffice;
    Button btnCancel, btnSave;
    ImageView imageView01;
    FloatingActionButton floatingButton;

    SharedPreferences myPref;

    ActivityResultLauncher<Intent> launchSomeActivity;

    ExecutorService e;
    Handler h;

    Bitmap selectedImageBitmap;
    String base64String;

    String key= "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_form);

        myPref = getApplicationContext().getSharedPreferences("DATA", MODE_PRIVATE);

        e = Executors.newCachedThreadPool();
        h = new Handler(Looper.getMainLooper());

        tvError = findViewById(R.id.tvError);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneHome = findViewById(R.id.etPhoneHome);
        etPhoneOffice = findViewById(R.id.etPhoneOffice);


        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        imageView01 = findViewById(R.id.imageView01);
        floatingButton = findViewById(R.id.floatingActionButton);

        floatingButton.setOnClickListener(view -> funUpload());


        btnCancel.setOnClickListener(view -> funCancel());
        btnSave.setOnClickListener(view -> funSave());


// choose photo
        launchSomeActivity = registerForActivityResult(
                new ActivityResultContracts
                        .StartActivityForResult(),
                result -> {
                    if (result.getResultCode()
                            == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        // selected Image uri  operation  here....
                        if (data != null
                                && data.getData() != null) {
                            Uri selectedImageUri = data.getData();
                            //Bitmap selectedImageBitmap;
                            try {
                                selectedImageBitmap
                                        = MediaStore.Images.Media.getBitmap(
                                        this.getContentResolver(),
                                        selectedImageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            imageView01.setImageBitmap(selectedImageBitmap);
                        }
                    }
                });
    }

    // photo upload
    private void funUpload() {
        Intent i = new Intent();

        i.setAction(Intent.ACTION_PICK); //pick image and open all app for choosing photo
       // i.setAction(Intent.ACTION_GET_CONTENT); // get content and open file manger

       // i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //open only photo gallery
        i.setType("image/*"); //open photo gallery and file manger for choosing photo

        launchSomeActivity.launch(i);

    }

    // checking Name validation error
    private boolean validateName(){
        String name = etName.getText().toString().trim();
        if(name.isEmpty()) {
            etName.setError("Field can not be empty");
            return false;
        }else {
            etName.setError(null);
            return true;
        }
    }

    // checking Email validation error
    private boolean validateEmail(){
        String email = etEmail.getText().toString().trim();
        if(email.isEmpty()) {
            etEmail.setError("Field can not be empty");
            return false;
        }else {
            etEmail.setError(null);
            return true;
        }
    }

    // checking PhoneHome validation error
    private boolean validatePhoneHome(){
        String phoneHome = etPhoneHome.getText().toString().trim();
        if(phoneHome.isEmpty()) {
            etPhoneHome.setError("Field can not be empty");
            return false;
        }else {
            etPhoneHome.setError(null);
            return true;
        }
    }

    // checking Photo validation error
    private boolean validatePhoto(){

        if(selectedImageBitmap == null) {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText("Your photo field is empty");
            return false;
        }else {

            return true;
        }
    }

    // Save function
    private void funSave() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneHome = etPhoneHome.getText().toString().trim();
        String phoneOffice = etPhoneOffice.getText().toString().trim();

        if (!validateName() || !validateEmail() || !validatePhoneHome() || !validatePhoto() ){
            tvError.setVisibility(View.VISIBLE);
            tvError.setText("Error, there is an invalid value");

            // fade out view nicely after 3 seconds
            AlphaAnimation alphaAnim = new AlphaAnimation(1.0f,0.0f);
            alphaAnim.setStartOffset(2000);                        // start in 3 seconds
            alphaAnim.setDuration(400);
            alphaAnim.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                public void onAnimationEnd(Animation animation)
                {
                    // make invisible when animation completes
                    tvError.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            tvError.setAnimation(alphaAnim);
        } else {
            tvError.setVisibility(View.INVISIBLE);
        }


        //Store in SharedPreferences
        myPref.edit().putString("Name",name).apply();
        myPref.edit().putString("email",email).apply();
        myPref.edit().putString("phoneHome",phoneHome).apply();
        myPref.edit().putString("phoneOffice",phoneOffice).apply();
        myPref.edit().putString("photo",base64String).apply();


        // Retrieve from SharedPreferences
        String strName = myPref.getString("Name","");
        String strEmail = myPref.getString("email","");
        String strPhoneHome = myPref.getString("phoneHome","");
        String strPhoneOffice = myPref.getString("phoneOffice","");
        String strBase64String = myPref.getString("photo","");


       // System.out.println(strName +" "+ strEmail +" "+ strPhoneHome +" "+ strPhoneOffice +" "+ strBase64String);


        if (validateName() && validateEmail() && validatePhoneHome() && validatePhoto()){

            //VISIBLE save Dialog for 4s
            AlertDialog dia = new AlertDialog.Builder(this)
                    .setMessage("Your Contact info has been saved")
                    .create();

            dia.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    final Button noButton = dia.getButton(AlertDialog.BUTTON_NEGATIVE);
                    new CountDownTimer(4000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {

                            if(dia.isShowing()){
                                dia.dismiss();


                                // It will reset all Edit field
                                etName.setText(null);
                                etEmail.setText(null);
                                etPhoneHome.setText(null);
                                etPhoneOffice.setText(null);
                                imageView01.setImageBitmap(null);
                            }

                        }
                    }.start();
                }
            });

            dia.show();
        }

        if(selectedImageBitmap != null) {

            e.execute(() -> {

                base64String = bitmapToBase64(selectedImageBitmap);

                h.post(() -> {

                    //System.out.println(base64String);

                    // save to SQLite
                    if (key.length() == 0) {
                        key = name + System.currentTimeMillis();
                    }

                    String values = name +"-::-"+ email +"-::-"+ phoneHome +"-::-"+ phoneOffice +"-::-"+ base64String;

                    KeyValueDB kvdb = new KeyValueDB(getApplicationContext());
                    kvdb.insertKeyValue(key,values);

                    System.out.println("Key is: "+key);
                    System.out.println(values);
                });

            });
        }

/*
        // save to SQLite
        if (key.length() == 0) {
            key = name + System.currentTimeMillis();
        }

        String values = name +"-::-"+ base64String +"-::-"+ email +"-::-"+ phoneHome +"-::-"+ phoneOffice;

        KeyValueDB kvdb = new KeyValueDB(getApplicationContext());
        kvdb.insertKeyValue(key,values);

        System.out.println("Key is: "+key);
        //System.out.println(values);
*/
       // loadData("ak1670669391523");


    }

    // load Data from SQLite
    public void loadData(String key){
        KeyValueDB kv = new KeyValueDB(this);
        String v = kv.getValueByKey(key);
        String value[] = v.split("-::-");
        for(int i=0; i<value.length; i++){
            System.out.println(value[i]);
        }
    }

    // convert Bitmap to Base64
    private String bitmapToBase64(Bitmap selectedImageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // convert Base64 to Bitmap
    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }


    // Cancel function for closing the app
    private void funCancel() {
        finishAndRemoveTask(); //Finishes all activities in this task and removes it from the recent tasks list.
        //this.finishAffinity();

    }
}