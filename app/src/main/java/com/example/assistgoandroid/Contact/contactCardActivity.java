package com.example.assistgoandroid.Contact;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assistgoandroid.R;
import com.example.assistgoandroid.callActivity;

public class contactCardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_card_activity);

//        Glide.with(context)
//                .load(contact.getContactPicture())
//                .override(400, 400)
//                .centerCrop()
//                .fitCenter() // scale to fit entire image within ImageView
//                .transform(new RoundedCornersTransformation(200,10))
//                .placeholder(R.drawable.loading_contact)
//                .error(R.drawable.loading_contact)
//                .into(holder.contactProfilePicture);
    }

    public void onCallClick(View view){
        Intent intent = new Intent(this, callActivity.class);
        this.startActivity(intent);
    }

    public void onVideoCallClick(View view){
        Intent intent = new Intent(this, callActivity.class);
        this.startActivity(intent);
    }

    public void onMessageClick(View view){

    }

    public void onEditContactClick(View view){

    }
}
