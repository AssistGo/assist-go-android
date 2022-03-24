package com.example.assistgoandroid.Call;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assistgoandroid.Contact.Contact;
import com.example.assistgoandroid.Contact.contactCardActivity;
import com.example.assistgoandroid.Contact.contactListAdapter;
import com.example.assistgoandroid.R;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class callAdapter extends RecyclerView.Adapter<callAdapter.ViewHolder>{

    Activity activity;
    private List<Contact> calledContactList;
    final String CONTACT_CARD = "CONTACT_CARD";
    Context context;

    public callAdapter(Activity activity, List<Contact> calledContactList) {
        this.activity = activity;
        this.calledContactList = calledContactList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public callAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_call, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull callAdapter.ViewHolder holder, int position) {
        Contact contact = calledContactList.get(position);

        holder.contactName.setText(contact.getName());
        Glide.with(context)
                .load(contact.getContactPicture())
                .override(400, 400)
                .centerCrop()
                .fitCenter() // scale to fit entire image within ImageView
                .transform(new RoundedCornersTransformation(200,10))
                .placeholder(R.drawable.loading_contact)
                .error(R.drawable.loading_contact)
                .into(holder.contactProfilePicture);
    }

    @Override
    public int getItemCount() {
        return calledContactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        TextView contactName, timeSinceCalled;
        ImageView contactProfilePicture;

        public ViewHolder(View itemView) {
            super(itemView);

            contactName = itemView.findViewById(R.id.tvContactName);
            timeSinceCalled = itemView.findViewById(R.id.tvTimeSinceCalled);
            contactProfilePicture = itemView.findViewById(R.id.ivContactProfilePicture);

            //set contact item clickable
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Contact contact = calledContactList.get(getAdapterPosition());
            //Toast.makeText(context, contact.name, Toast.LENGTH_SHORT).show();
            //Use the intent system to navigate to the new activity
            Intent intent = new Intent(context, contactCardActivity.class);
            intent.putExtra(CONTACT_CARD, contact);
            context.startActivity(intent);
        }
    }

    public void setFilterList(List<Contact> filteredList){
        this.calledContactList = filteredList;
        notifyDataSetChanged();
    }
}
