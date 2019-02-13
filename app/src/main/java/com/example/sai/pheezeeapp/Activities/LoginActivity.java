package com.example.sai.pheezeeapp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sai.pheezeeapp.R;
import com.example.sai.pheezeeapp.services.MqttHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{





    private static final String TAG = "Google Api";
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN=1;
    LoginButton loginButton;
    FloatingActionButton googleLoginButton;
    private static final String EMAIL = "email";
    CallbackManager callbackManager;
    AccessToken accessToken = AccessToken.getCurrentAccessToken();
    ImageView imageView;
    Bitmap  bitmap;
    Uri savedImageURI;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    MqttHelper mqttHelper;


    Button btn_login_login;


    //EditTexts for email and password
    EditText et_login_email, getEt_login_password;

    //Srings for edittexts
    String str_login_email, str_login_password;




    //Mqtt Topics
    String mqtt_subs_login_response = "login/phizio/response";    //phizio login response from server
    String mqtt_pubs_login_phizio = "login/phizio";    //phizio login response from server

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //MqttHelper base connecting class object defination

        mqttHelper = new MqttHelper(this);


        //EditText defiition

        et_login_email = (EditText)findViewById(R.id.et_login_email);
        getEt_login_password = (EditText)findViewById(R.id.et_login_password);


        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        googleLoginButton = findViewById(R.id.google_login_button);
        callbackManager = CallbackManager.Factory.create();
        imageView = findViewById(R.id.imageView3);


        btn_login_login = (Button)findViewById(R.id.btn_login_login);

        btn_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_login_email = et_login_email.getText().toString();
                str_login_password = getEt_login_password.getText().toString();
                MqttMessage mqttMessage = new MqttMessage();
                JSONObject jsonObject = new JSONObject();
                if(str_login_email.equals("")||str_login_password.equals("")){
                        showToast("Invalid Credentials");
                }
                else {


                    try {
                        jsonObject.put("phiziopassword",str_login_password);
                        jsonObject.put("phizioemail",str_login_email);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mqttMessage.setPayload(jsonObject.toString().getBytes());
                    mqttHelper.publishMqttTopic(mqtt_pubs_login_phizio,mqttMessage);
                }
            }
        });

        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if(topic.equals(mqtt_subs_login_response)){
                    String str = message.toString();
                    if(str.equals("\"invalid\"")){
                        Log.i("MQTT MESSAGE RESPONSE", "User with this mail already exists");
                        showToast("Invalid Credentials");
                    }
                    else {
                        editor = sharedPref.edit();
                        editor.putBoolean("isLoggedIn",true);
                        editor.putString("phiziodetails",message.toString());
                        editor.commit();
                        Log.i("MQTT MESSAGE RESPONSE", message.toString());



                        Intent i = new Intent(LoginActivity.this, PatientsView.class);
                        startActivity(i);
                        finish();
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        startActivity(new Intent(LoginActivity.this,OnStartActivity.class));
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.e(TAG,object.toString());
                                Log.e(TAG,response.toString());

                                try {
                                    String userId = object.getString("id");
                                    URL profilePicture = new URL("https://graph.facebook.com/" + userId + "/picture?width=500&height=500");
                                    String firstName = null;
                                    if(object.has("first_name"))
                                        firstName = object.getString("first_name");
                                    String lastName = null;
                                    if(object.has("last_name"))
                                        lastName = object.getString("last_name");
                                    String email = null;
                                    if (object.has("email"))
                                        email = object.getString("email");
                                    String birthday;
                                    if (object.has("birthday"))
                                        birthday = object.getString("birthday");
                                    String gender;
                                    if (object.has("gender"))
                                        gender = object.getString("gender");

                                    Picasso.get().load(profilePicture.toString()).into(getTarget("profilePic"));
                                    editor = sharedPref.edit();
                                    editor.putString("fullName",firstName+" "+lastName);
                                    editor.putString("email",email);
                                    editor.apply();
                                } catch (JSONException | MalformedURLException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        //Here we put the requested fields to be returned from the JSONObject
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email, birthday, gender");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            handleSignInResult(task);
            //startActivity(new Intent(LoginActivity.this,OnStartActivity.class));
        }
        Toast.makeText(this,"done1",Toast.LENGTH_LONG).show();
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            Log.i("TESTING","inside handlesignin");
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //String name = account.getDisplayName();
            //Log.i("GOOGLE",name);
            //Toast.makeText(this,account.getId(),Toast.LENGTH_LONG).show();
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }

    }

    public void loginWithGoogle(View view) {
        signIn();
    }

    public void goToUserType(View view) {
        startActivity(new Intent(this,UserType.class));
    }

    public void loginWithFacebook(View view) {
        loginButton.callOnClick();
    }



    private Target getTarget(final String fileName){
        Target target = new Target(){
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File file = Environment.getExternalStoragePublicDirectory(fileName);

                        try {
                            if(file.createNewFile())
                                System.out.println("created-------------------------");
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                            ostream.flush();
                            ostream.close();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        return target;
    }



    @SuppressLint("ResourceType")
    public void showToast(String message){
         Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }


    @Override
    protected void onStop() {
        super.onStop();
        mqttHelper.mqttAndroidClient.unregisterResources();
        mqttHelper.mqttAndroidClient.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttHelper.mqttAndroidClient.unregisterResources();
        mqttHelper.mqttAndroidClient.close();

    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mqttHelper.mqttAndroidClient.unregisterResources();
        mqttHelper.mqttAndroidClient.close();
    }
}
