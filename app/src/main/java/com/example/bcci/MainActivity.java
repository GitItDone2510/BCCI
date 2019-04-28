package com.example.bcci;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    final int img = 1;
    final int PIC_CROP = 2;
    ImageView imgView;
    String path;
    TessBaseAPI mTess;
    TextView tvDisplayName, tvDisplayPhone, tvDisplayEmail;
    String dataPath = "";
    Bitmap bitmap;
    File image;
    Uri photoURI;
    ConstraintLayout constraintLayout;
    AnimationDrawable animationDrawable;
    Button btnOCR, btnContacts;
    private static long back_pressed;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab);
        imgView = (ImageView) findViewById(R.id.imageView2);

        btnOCR = (Button) findViewById(R.id.button);
        btnContacts = (Button) findViewById(R.id.button2);
        tvDisplayName = (TextView) findViewById(R.id.textView3);
        tvDisplayPhone = (TextView) findViewById(R.id.textView4);
        tvDisplayEmail = findViewById(R.id.textView5);

        constraintLayout = findViewById(R.id.Constraint);

        starters();
    }

    private void starters() {

        dataPath = getFilesDir() + "/tesseract/";    // Returns the absolute path to the directory on the filesystem
        mTess = new TessBaseAPI();
        checkFile(new File(dataPath + "tessdata/"));
        String language = "eng";
        mTess.init(dataPath, language);

        animationDrawable = (AnimationDrawable) constraintLayout.getBackground();

        animationDrawable.setEnterFadeDuration(1500);
        animationDrawable.setExitFadeDuration(2500);

        animationDrawable.start();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cam();
            }
        });


        btnOCR.setOnClickListener(new View.OnClickListener() {      // on clicking Run OCR button of app
            @Override
            public void onClick(View v) {
                processImage();
            }
        });
        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToContacts();
            }
        });
    }


    private void checkFile(File dir) {
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles();
        }

        if (dir.exists()) {
            String dataFilePath = dataPath + "/tessdata/eng.traineddata";
            File datafile = new File(dataFilePath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {

            String filePath = dataPath + "/tessdata/eng.traineddata";
            AssetManager assetManager = getAssets();
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filePath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cam() {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePic.resolveActivity(getPackageManager()) != null) {
            File photoFile;
            photoFile = createFile();
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this, "com.example.bcci.fileprovider", photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePic, img);


            }
        }
    }

    public void setPic() throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        InputStream is;
        is = new FileInputStream(path);
        int w = imgView.getMaxWidth();
        int h = imgView.getMaxHeight();
        options.inSampleSize = Math.max(options.outWidth / w, options.outHeight / h);
        bitmap = BitmapFactory.decodeStream(is, null, options);
        imgView.setImageBitmap(bitmap);
    }

    public File createFile() {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = new File(storageDir, "image.jpg");
        path = image.getPath();
        return image;

    }

//    public void cropImage(Uri photoURI) {
//
//        Intent cropIntent = new Intent("com.android.camera.action.CROP");
//        cropIntent.setDataAndType(photoURI, "File");
//        cropIntent.putExtra("crop", true);
//        cropIntent.putExtra("aspectX", 1);
//        cropIntent.putExtra("aspectY", 1);
//        cropIntent.putExtra("outputX", 128);
//        cropIntent.putExtra("outputY", 128);
//        cropIntent.putExtra("return-data", true);
//        startActivityForResult(cropIntent, PIC_CROP);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == img) {
                try {
                    setPic();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void processImage() {        // image processing part

        String OCRresult = null;
        mTess.setImage(bitmap);
        OCRresult = mTess.getUTF8Text();
        extractName(OCRresult);
        extractEmail(OCRresult);
        extractPhone(OCRresult);
        mTess.end();
    }

    public void extractName(String str) {
        System.out.println("Getting the Name");
        final String NAME_REGEX = "^[A-z]+\\s+(((. ')[A-z])?)[A-z]+$";
        Pattern p = Pattern.compile(NAME_REGEX, Pattern.MULTILINE);
        Matcher m = p.matcher(str);
        if (m.find()) {
            System.out.println(m.group());
            tvDisplayName.setText(m.group());
        }
    }

    public void extractEmail(String str) {
        System.out.println("Getting the email");
        final String EMAIL_REGEX = "^(.)+@(.)+$";
        Pattern p = Pattern.compile(EMAIL_REGEX, Pattern.MULTILINE);
        Matcher m = p.matcher(str);   // get a matcher object
        if (m.find()) {
            System.out.println(m.group());
            tvDisplayEmail.setText(m.group());
        }
    }

    public void extractPhone(String str) {
        System.out.println("Getting Phone Number");
        final String PHONE_REGEX = "(?:^|\\D)(\\d{3})[)\\-. ]*?(\\d{3})[\\-. ]*?(\\d{4})(?:$|\\D)";
        Pattern p = Pattern.compile(PHONE_REGEX, Pattern.MULTILINE);
        Matcher m = p.matcher(str);   // get a matcher object
        if (m.find()) {
            System.out.println(m.group());
            tvDisplayPhone.setText(m.group());
        }
    }

    private void addToContacts() {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        if (tvDisplayName.getText().length() > 0 || (tvDisplayPhone.getText().length() > 0 || tvDisplayEmail.getText().length() > 0)) {
            intent.putExtra(ContactsContract.Intents.Insert.NAME, tvDisplayName.getText());
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, tvDisplayEmail.getText());
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, tvDisplayPhone.getText());
            intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "No information to add to contacts!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {

        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit",
                    Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }
}
