package org.techtown.project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import org.techtown.project.databinding.ActivityLoginBinding;

import static androidx.annotation.Dimension.DP;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private static final int SIGN_OUT = 6000;
    private static final int EXIT = 8000;
    String checkname="hoon";
    String TAG = "LoginActivity";
    ActivityLoginBinding loginBinding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;
    ProgressDialog progressDialog;
    GoogleSignInClient mGoogleSignInClient;
    String providerId;
    String uid;
    String name;
    String email;
    Uri photourl;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        TextView textView = (TextView) loginBinding.signInButton.getChildAt(0);
        textView.setText(" Google ???????????? ??????????????? ");

        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            user();
            intent();
            }
        else {
            Toast.makeText(this,"?????????????????????",Toast.LENGTH_SHORT).show();
        }


        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == loginBinding.signInButton) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                    Log.d(TAG, "btn singin");

                }
            }
        };
        loginBinding.signInButton.setOnClickListener(click);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {        //????????? ??????
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                //login_check=1;

                Log.d(TAG, "code sign in ");
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
         if(resultCode==SIGN_OUT){     //???????????????????????? ??????????????? ?????? ??????
            signOut();
        }
        if(resultCode==EXIT){     // ???????????????????????? ??????????????? ???????????? ??????
            finish();
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loading();
                            loadingEnd();
                        } else {
                        }
                    }
                });
    }

    public void signOut() {          //?????????????????????
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(LoginActivity.this,LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                        startActivity(intent);
                    }
                });
    }
    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }

    public void loading() {           //????????? ?????? ?????????
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("????????? ????????????");
                        progressDialog.show();
                        user();
                    }
                }, 0);
    }
    public void loadingEnd() {         //????????? ????????? ?????????
        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        checkname="jeon";
                        progressDialog.dismiss();
                        Log.d(TAG, "loadingEnd");
                        intent();
                    }
                }, 5000);
    }
    public void user(){                  //???????????? ????????? ?????? ?????????
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            for(UserInfo profile : currentUser.getProviderData()){
                providerId = profile.getProviderId();
                uid = profile.getUid();
                name = profile.getDisplayName();
                Log.d(TAG, "name : "+name);
                email = profile.getEmail();
                Log.d(TAG, "email : "+email);
                photourl = profile.getPhotoUrl();
            }
        }
        else{
            Log.d(TAG, "ERROR -------------------------------------------ERROR");
        }
    }


    public void intent(){         //?????? ??????????????? ??????????????????  ????????? ?????????
        Intent userintent = new Intent(LoginActivity.this,MainActivity.class);
        userintent.putExtra("name",checkname);
        userintent.putExtra("googleName",name);
        userintent.putExtra("googleEmail",email);
        userintent.putExtra("googleUri",photourl);
        startActivityForResult(userintent,9005);
    }

}
