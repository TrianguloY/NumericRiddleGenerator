package com.trianguloy.numericriddlegenerator;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;

public class Riddle extends Activity {

    private NumberPicker[] pickers;
    private File folder;
    private TextView textView;
    private ImageView imageView;
    private ScrollView scrollView;
    private ProgressBar progressBarView;

    private Preferences pref;

    ///////////////////// INITIALIZATIONS /////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riddle);

        pref = new Preferences(this);

        initializeViews();

        if (checksPermission()) {
            initialize_readme();
        }


    }

    private void initializeViews() {
        //text
        textView = (TextView) findViewById(R.id.textView);

        //image
        imageView = (ImageView) findViewById(R.id.imageView_picture);
        imageView.setVisibility(View.GONE);

        //scrollView
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        //progressBar
        progressBarView = (ProgressBar) findViewById(R.id.progressBar);
        progressBarView.setVisibility(View.GONE);

        //pickers
        pickers = new NumberPicker[4];

        pickers[0] = (NumberPicker) findViewById(R.id.numberPicker0);
        pickers[1] = (NumberPicker) findViewById(R.id.numberPicker1);
        pickers[2] = (NumberPicker) findViewById(R.id.numberPicker2);
        pickers[3] = (NumberPicker) findViewById(R.id.numberPicker3);

        for (int i = 0; i < 4; ++i) {
            pickers[i].setMinValue(0);
            pickers[i].setMaxValue(9);
            pickers[i].setWrapSelectorWheel(true);
        }

    }

    private void initialize_readme() {

        boolean saveDemo = false;
        boolean saveReadme = false;

        //folder
        folder = new File(Environment.getExternalStorageDirectory().getPath(), getString(R.string.filename_directory));
        if (!folder.exists()) {
            saveDemo = true;
            saveReadme = true;
            folder.mkdirs();


            File nomedia = new File(folder, getString(R.string.filename_nomedia));
            Utils.createFileIfNecessary(nomedia, getString(R.string.filecontent_nomedia));
        }

        //readme
        boolean showMessage = !pref.getDontShowAgain();
        if (!pref.getVersion().equals(BuildConfig.VERSION_NAME)) {
            pref.setVersion(BuildConfig.VERSION_NAME);
            saveReadme = true;
        }


        if (saveReadme || showMessage) {
            String readmeString = Utils.getRawResource(this, R.raw.readme);
            if (saveReadme) {
                File f = new File(folder, getString(R.string.filename_readme));
                f.delete();
                Utils.createFileIfNecessary(f, readmeString);
            }
            if (showMessage) {
                final boolean finalSaveDemo = saveDemo;
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_readme)
                        .setMessage(readmeString)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(finalSaveDemo) {
                                    askCopyDemo();
                                }else{
                                    initialize_app();
                                }
                            }
                        })
                        .setNegativeButton(R.string.dontShowAgain, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                pref.setDontShowAgain(true);
                                if(finalSaveDemo) {
                                    askCopyDemo();
                                }else{
                                    initialize_app();
                                }
                            }
                        })
                        .show();
                return;
            }
        }

        initialize_app();

    }

    private void askCopyDemo() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_demo)
                .setMessage(R.string.message_demo)
                .setCancelable(false)
                .setPositiveButton("Load demo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.copyDemo(getApplicationContext(), new File(Environment.getExternalStorageDirectory().getPath(), getString(R.string.filename_directory)));
                        initialize_app();
                    }
                })
                .setNegativeButton("Don't load", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        initialize_app();
                    }
                })
                .show();
    }

    private void initialize_app(){

        //title
        File title = new File(folder, getString(R.string.filename_title));
        Utils.createFileIfNecessary(title, getString(R.string.app_name));
        setTitle(Utils.getStringFromFile(this, title));

        //as if button was clicked
        buttonClick(null);
    }


    ///////////////////// ACTIONS /////////////////////////////

    public void buttonClick(View view) {
        final Context cntx = this;
        progressBarView.setVisibility(View.VISIBLE);
        progressBarView.post(new Runnable() {
            @Override
            public void run() {

                //get the current number
                StringBuilder n = new StringBuilder();
                for (NumberPicker picker : pickers) {
                    n.append(picker.getValue());
                }
                Log.d("number", n.toString());

                //get the textfile (can exist or not)
                File textFile = new File(folder, n.toString() + getString(R.string.extension_text));

                //get the image (can exist or not)
                File imageFile = null;
                for (String extension : getString(R.string.extension_image_list).split(getString(R.string.extension_image_split))) {
                    imageFile = new File(folder, n.toString() + extension);
                    if (imageFile.exists()) {
                        break;
                    }
                }

                //show/hide the image
                if (imageFile != null && imageFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }
                imageView.requestLayout();

                //show/hide the text
                if (textFile.exists()) {
                    textView.setText(Utils.getStringFromFile(cntx, textFile));
                    textView.setVisibility(View.VISIBLE);
                } else {
                    if (imageFile != null && imageFile.exists()) {
                        textView.setText("");
                        textView.setVisibility(View.GONE);
                    } else {
                        File defaultfile = new File(folder, getString(R.string.filename_default));
                        Utils.createFileIfNecessary(defaultfile, getString(R.string.default_default));
                        textView.setText(Utils.getStringFromFile(cntx, defaultfile));
                        textView.setVisibility(View.VISIBLE);
                    }
                }
                textView.requestLayout();

                //scroll to be always on top
                scrollView.scrollTo(0, 0);

                progressBarView.setVisibility(View.GONE);
            }
        });
    }

    /////////////////////////////// PERMISIONS ////////////////////////////////


    final private int NUMBER = 7642853;//I smashed the numeric row


    private boolean checksPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            //should be granted
            return true;
        }

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Utils.showMessage(this, getString(R.string.title_askPermission), getString(R.string.message_askPermission), new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, NUMBER);
                    }
                });
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, NUMBER);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case NUMBER:
                initialize_readme();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
