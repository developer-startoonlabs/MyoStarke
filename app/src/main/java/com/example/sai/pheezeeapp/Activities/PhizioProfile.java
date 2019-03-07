package com.example.sai.pheezeeapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sai.pheezeeapp.R;
import com.example.sai.pheezeeapp.services.MqttHelper;
import com.example.sai.pheezeeapp.services.PicassoCircleTransformation;
import com.squareup.picasso.Picasso;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import static com.example.sai.pheezeeapp.activities.PatientsView.ivBasicImage;

public class PhizioProfile extends AppCompatActivity {
    EditText et_phizio_name, et_phizio_email, et_phizio_phone;
    MqttHelper mqttHelper ;
    String mqtt_phizio_profile_update = "phizioprofile/update";
    String mqtt_phizio_profile_update_response = "phizioprofile/update/response";

    boolean message_sent = false;


    String mqtt_phizio_profilepic_change = "phizio/profilepic/upload";
    String mqtt_phizio_profilepic_change_response = "phizio/profilepic/upload/response";
    String mqtt_get_profile_pic_response = "phizio/getprofilepic/response";
    TextView tv_edit_profile_pic, tv_edit_profile_details;



    ImageView iv_phizio_profilepic;



    //For Alert Dialog
    final CharSequence[] items = { "Take Photo", "Choose from Library",
            "Cancel" };

    AlertDialog.Builder builder;

    Button btn_update, btn_cancel_update;

    JSONObject json_phizio;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phizio_profile);
        mqttHelper = new MqttHelper(PhizioProfile.this,"phizioprofile");

        //Shared Preference
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();

        try {
            json_phizio = new JSONObject(sharedPref.getString("phiziodetails", ""));
            Log.i("Patient View", json_phizio.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        setPhizioDetails();

    }

    private void setPhizioDetails() {
        et_phizio_name =  findViewById(R.id.tv_phizio_name);
        et_phizio_email =  findViewById(R.id.tv_phizio_email);
        et_phizio_phone =  findViewById(R.id.tv_phizio_phone);
        tv_edit_profile_details  = findViewById(R.id.edit_phizio_details);
        tv_edit_profile_pic = findViewById(R.id.change_profile_pic);


        tv_edit_profile_details.setPaintFlags(tv_edit_profile_details.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
        tv_edit_profile_pic.setPaintFlags(tv_edit_profile_pic.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);


        iv_phizio_profilepic = (ImageView)findViewById(R.id.iv_phizio_profilepic);
        try {
            if(!json_phizio.getString("phizioprofilepicurl").equals("empty")) {
                String temp = null;
                try {
                    temp = json_phizio.getString("phizioprofilepicurl");
                    Log.i("temp",temp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                temp = temp.replaceFirst("@", "%40");
                temp = "https://s3.ap-south-1.amazonaws.com/pheezee/" + temp;
                Log.i("inside check", temp);
                Picasso.get().load(temp)
                        .placeholder(R.drawable.user_icon)
                        .error(R.drawable.user_icon)
                        .transform(new PicassoCircleTransformation())
                        .into(iv_phizio_profilepic);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }


        btn_update = findViewById(R.id.btn_update_details);
        btn_cancel_update = findViewById(R.id.btn_cancel_update);






        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if(topic.equals(mqtt_phizio_profile_update_response)){

                    Log.i("MQ ME",message.toString());
                    json_phizio.put("phizioname",et_phizio_name.getText().toString());
                    json_phizio.put("phiziophone",et_phizio_phone.getText().toString());

                    editor.putString("phiziodetails",json_phizio.toString());
                    editor.commit();
                }

                else if(topic.equals(mqtt_get_profile_pic_response)){
                    /*Bitmap bitmap = BitmapFactory.decodeByteArray(message.getPayload(), 0, message.getPayload().length);
                    iv_phizio_profilepic.setImageBitmap(bitmap);
                    PatientsView.ivBasicImage.setImageBitmap(bitmap);*/
                }

                else if(topic.equals(mqtt_phizio_profilepic_change_response)){

                    editor = sharedPref.edit();
                    try {
                        json_phizio.put("phizioprofilepicurl",message.toString());
                        editor.putString("phiziodetails",json_phizio.toString());
                        editor.commit();
                        Log.i("array",json_phizio.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });


        et_phizio_name.setFocusable(false);
        et_phizio_name.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        et_phizio_name.setClickable(false);


        tv_edit_profile_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //setting focus on for name to update the edittext
                et_phizio_name.setFocusable(true);
                et_phizio_name.setFocusableInTouchMode(true); // user touches widget on phone with touch screen
                et_phizio_name.setClickable(true);

                //setting focus on for phone to update the edittext
                et_phizio_phone.setFocusable(true);
                et_phizio_phone.setFocusableInTouchMode(true); // user touches widget on phone with touch screen
                et_phizio_phone.setClickable(true);



                //Setting the visibility of the buttons tured on
                btn_cancel_update.setVisibility(View.VISIBLE);
                btn_update.setVisibility(View.VISIBLE);
            }
        });


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_phizio_name = et_phizio_name.getText().toString();
                String str_phizio_phone= et_phizio_phone.getText().toString();

                JSONObject object = new JSONObject();
                MqttMessage message = new MqttMessage();
                try {
                    object.put("phizioname",str_phizio_name);
                    object.put("phiziophone",str_phizio_phone);
                    object.put("phizioemail",et_phizio_email.getText().toString());
                    message.setPayload(object.toString().getBytes());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mqttHelper.publishMqttTopic(mqtt_phizio_profile_update,message);





                btn_update.setVisibility(View.INVISIBLE);
                btn_cancel_update.setVisibility(View.INVISIBLE);

                //setting focus on for name to update the edittext
                et_phizio_name.setFocusable(false);
                et_phizio_name.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                et_phizio_name.setClickable(false);

                //setting focus on for phone to update the edittext
                et_phizio_phone.setFocusable(false);
                et_phizio_phone.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                et_phizio_phone.setClickable(false);
            }
        });


        btn_cancel_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    et_phizio_name.setText(json_phizio.getString("phizioname"));
                    et_phizio_email.setText(json_phizio.getString("phizioemail"));
                    et_phizio_phone.setText(json_phizio.getString("phiziophone"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                btn_cancel_update.setVisibility(View.INVISIBLE);
                btn_update.setVisibility(View.INVISIBLE);

                //setting focus on for name to update the edittext
                et_phizio_name.setFocusable(false);
                et_phizio_name.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                et_phizio_name.setClickable(false);

                //setting focus on for phone to update the edittext
                et_phizio_phone.setFocusable(false);
                et_phizio_phone.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                et_phizio_phone.setClickable(false);
            }
        });




        tv_edit_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(i, RESULT_LOAD_IMAGE);
                builder = new AlertDialog.Builder(PhizioProfile.this);
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Take Photo")) {
                            if(ContextCompat.checkSelfPermission(PhizioProfile.this, Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_DENIED) {
                                ActivityCompat.requestPermissions(PhizioProfile.this, new String[]{Manifest.permission.CAMERA}, 0);
                                cameraIntent();
                            }
                            else {
                                cameraIntent();
                            }
                        } else if (items[item].equals("Choose from Library")) {
                            galleryIntent();
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        try {
            et_phizio_name.setText(json_phizio.getString("phizioname"));
            et_phizio_email.setText(json_phizio.getString("phizioemail"));
            et_phizio_phone.setText(json_phizio.getString("phiziophone"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void galleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);

    }


    private void cameraIntent() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttHelper.mqttAndroidClient.unregisterResources();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Bitmap photo = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    iv_phizio_profilepic.setImageBitmap(photo);
                    ivBasicImage.setImageBitmap(photo);
                    JSONObject object = new JSONObject();

                    MqttMessage message = new MqttMessage();

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    String encodedString = Base64.encodeToString(byteArray,Base64.DEFAULT);
                    try {
                        object.put("image",encodedString);
                        object.put("phizioemail",et_phizio_email.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    message.setPayload(object.toString().getBytes());

                    mqttHelper.publishMqttTopic(mqtt_phizio_profilepic_change,message);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    iv_phizio_profilepic.setImageURI(selectedImage);
                    ivBasicImage.setImageURI(selectedImage);

                    iv_phizio_profilepic.invalidate();
                    BitmapDrawable drawable = (BitmapDrawable) iv_phizio_profilepic.getDrawable();
                    Bitmap photo = drawable.getBitmap();
                    MqttMessage message = new MqttMessage();
                    JSONObject object = new JSONObject();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    String encodedString = Base64.encodeToString(byteArray,Base64.DEFAULT);
                    try {
                        object.put("image",encodedString);
                        object.put("phizioemail",et_phizio_email.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    message.setPayload(object.toString().getBytes());

                    mqttHelper.publishMqttTopic(mqtt_phizio_profilepic_change,message);
                }
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    public Context getContext(){
        return this;
    }
}
