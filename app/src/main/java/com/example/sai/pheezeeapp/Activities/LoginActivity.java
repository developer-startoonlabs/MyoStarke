package com.example.sai.pheezeeapp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sai.pheezeeapp.R;
import com.example.sai.pheezeeapp.services.MqttHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.Login;
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
import com.trncic.library.DottedProgressBar;

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
    CallbackManager callbackManager;
    MqttHelper mqttHelper;

    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;
    DottedProgressBar dottedProgressBar;

    Button btn_login_login;


    //EditTexts for email and password
    EditText et_login_email, getEt_login_password;

    //Srings for edittexts
    String str_login_email, str_login_password;




    //Mqtt Topics
    String mqtt_subs_login_response = "login/phizio/response";    //phizio login response from server
    String mqtt_pubs_login_phizio = "login/phizio";    //phizio login response from server


    TextView tv_signup,tv_login,tv_welcome_message,tv_login_welcome_user;

    LinearLayout ll_login,ll_signin_section,btn_login,tv_forgot_password,ll_signup_section,ll_welcome;
    RelativeLayout rl_login_section;


    EditText et_mail,et_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_continue);

        initializeView();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

//        //MqttHelper base connecting class object defination

        mqttHelper = new MqttHelper(this);
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
                    Log.i("message",message.toString());
                    if(str.equals("\"invalid\"")){
                        Log.i("MQTT MESSAGE RESPONSE", "User with this mail already exists");
                        setWelcomeText("Invalid Credentials");
//                        showToast("Invalid Credentials");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                disableWelcomeView();
                                enablePreviousView();
                            }
                        },1000);

                    }
                    else {
                        JSONObject object = new JSONObject(message.toString());
                        String name = object.getString("phizioname");
                        Log.i("hello","hello");
                        editor = sharedPref.edit();
                        editor.putBoolean("isLoggedIn",true);
                        editor.putString("phiziodetails",message.toString());
                        editor.commit();
                        Log.i("MQTT MESSAGE RESPONSE", message.toString());

                        setWelcomeText("Welcome");
                        tv_login_welcome_user.setText(name);
                        dottedProgressBar.startProgress();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(LoginActivity.this, PatientsView.class);
                                startActivity(i);
                                finish();
                                dottedProgressBar.stopProgress();
                            }
                        },1000);

                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
//
//
//        LoginManager.getInstance().registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(final LoginResult loginResult) {
//                        startActivity(new Intent(LoginActivity.this,OnStartActivity.class));
//                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
//                            @Override
//                            public void onCompleted(JSONObject object, GraphResponse response) {
//                                Log.e(TAG,object.toString());
//                                Log.e(TAG,response.toString());
//
//                                try {
//                                    String userId = object.getString("id");
//                                    URL profilePicture = new URL("https://graph.facebook.com/" + userId + "/picture?width=500&height=500");
//                                    String firstName = null;
//                                    if(object.has("first_name"))
//                                        firstName = object.getString("first_name");
//                                    String lastName = null;
//                                    if(object.has("last_name"))
//                                        lastName = object.getString("last_name");
//                                    String email = null;
//                                    if (object.has("email"))
//                                        email = object.getString("email");
//                                    String birthday;
//                                    if (object.has("birthday"))
//                                        birthday = object.getString("birthday");
//                                    String gender;
//                                    if (object.has("gender"))
//                                        gender = object.getString("gender");
//
//                                    Picasso.get().load(profilePicture.toString()).into(getTarget("profilePic"));
//                                    editor = sharedPref.edit();
//                                    editor.putString("fullName",firstName+" "+lastName);
//                                    editor.putString("email",email);
//                                    editor.apply();
//                                } catch (JSONException | MalformedURLException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                        //Here we put the requested fields to be returned from the JSONObject
//                        Bundle parameters = new Bundle();
//                        parameters.putString("fields", "id, first_name, last_name, email, birthday, gender");
//                        request.setParameters(parameters);
//                        request.executeAsync();
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        // App code
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//                        // App code
//                    }
//                });
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        googleLoginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                signIn();
//            }
//        });
    }

    private void disableWelcomeView() {
        ll_welcome.setVisibility(View.INVISIBLE);
        dottedProgressBar.setVisibility(View.INVISIBLE);
    }

    private void enablePreviousView() {
        ll_signup_section.setVisibility(View.VISIBLE);
        ll_signin_section.setVisibility(View.VISIBLE);
        rl_login_section.setVisibility(View.VISIBLE);
    }


    private void initializeView() {
        final Animation animation_up = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.slide_up_dialog);
        tv_login = findViewById(R.id.btn_login_login);
        ll_login = findViewById(R.id.ll_signup);
        rl_login_section = findViewById(R.id.rl_login_section);
        ll_signin_section = findViewById(R.id.layout_signin);
        tv_signup = findViewById(R.id.login_tv_signup);
        btn_login = findViewById(R.id.btn_login);
        tv_forgot_password = findViewById(R.id.btn_forgot_password);
        dottedProgressBar = findViewById(R.id.dot_progress_bar);
        et_mail = findViewById(R.id.login_et_email);
        et_password = findViewById(R.id.login_et_password);
        ll_signup_section = findViewById(R.id.ll_login_btn);
        ll_welcome = findViewById(R.id.ll_welcome_section);
        tv_welcome_message = findViewById(R.id.tv_welcome_message);
        tv_login_welcome_user = findViewById(R.id.login_tv_welcome_user);

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_login_section.startAnimation(animation_up);
                ll_signin_section.setVisibility(View.VISIBLE);
                ll_signin_section.startAnimation(animation_up);
                ll_login.setVisibility(View.GONE);
            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_login_email = et_mail.getText().toString();
                str_login_password = et_password.getText().toString();
                MqttMessage mqttMessage = new MqttMessage();
                JSONObject jsonObject = new JSONObject();
                if(str_login_email.equals("")||str_login_password.equals("")){
                        showToast("Invalid Credentials");
                }
                else {
                    Log.i("credentials",str_login_email+" "+str_login_password);

                    try {
                        jsonObject.put("phiziopassword",str_login_password);
                        jsonObject.put("phizioemail",str_login_email);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mqttMessage.setPayload(jsonObject.toString().getBytes());
                    mqttHelper.publishMqttTopic(mqtt_pubs_login_phizio,mqttMessage);

                    disablePreviousView();
                    enableWelcomeView();
                    setWelcomeText("Loging in..");
                    dottedProgressBar.startProgress();
                }
            }
        });

    }

    private void setWelcomeText(String str){
        tv_welcome_message.setText(str);
    }

    private void enableWelcomeView() {
        ll_welcome.setVisibility(View.VISIBLE);
        dottedProgressBar.setVisibility(View.VISIBLE);
    }

    private void disablePreviousView() {
        ll_signup_section.setVisibility(View.INVISIBLE);
        ll_signin_section.setVisibility(View.INVISIBLE);
        rl_login_section.setVisibility(View.INVISIBLE);
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
//        mqttHelper.mqttAndroidClient.unregisterResources();
//        mqttHelper.mqttAndroidClient.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mqttHelper.mqttAndroidClient.unregisterResources();
//        mqttHelper.mqttAndroidClient.close();

    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        mqttHelper.mqttAndroidClient.unregisterResources();
//        mqttHelper.mqttAndroidClient.close();
    }
}
