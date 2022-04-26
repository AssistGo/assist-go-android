package com.example.assistgoandroid.Contact;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.assistgoandroid.R;
import com.example.assistgoandroid.models.Contact;

import java.util.List;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Adapter for contact list
 */

public class contactListAdapter extends RecyclerView.Adapter<contactListAdapter.ViewHolder> {
    final String CONTACT_CARD = "CONTACT_CARD";
    Activity activity;
    private List<Contact> contactsList;
    Context context;

    public contactListAdapter(Activity activity, List<Contact> contactsList) {
        this.activity = activity;
        this.contactsList = contactsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public contactListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull contactListAdapter.ViewHolder holder, int position) {
        Contact contact = contactsList.get(position);

        holder.contactName.setText(contact.getFullName());

        if (contact.isFavorite())
            DrawableCompat.setTint(holder.favoriteHeart.getDrawable(), ContextCompat.getColor(context, R.color.red));
        else
            DrawableCompat.setTint(holder.favoriteHeart.getDrawable(), ContextCompat.getColor(context, R.color.favorite_button_background_color));

        Glide.with(context)
                .load(contact.getProfileImageUrl())
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
        return contactsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row

        TextView contactName;
        ImageView contactProfilePicture;
        ImageView favoriteHeart;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            contactName = itemView.findViewById(R.id.tvContactName);
            contactProfilePicture = itemView.findViewById(R.id.ivContactProfilePicture);
            favoriteHeart = itemView.findViewById(R.id.ivFavoriteHeart);

            itemView.setOnClickListener(this);

            favoriteHeart.setOnClickListener(view -> {
                Contact contact = contactsList.get(getAdapterPosition());
                contact.setFavorite(!contact.isFavorite());
                if (contact.isFavorite())
                    DrawableCompat.setTint(favoriteHeart.getDrawable(), ContextCompat.getColor(context, R.color.red));
                else
                    DrawableCompat.setTint(favoriteHeart.getDrawable(), ContextCompat.getColor(context, R.color.favorite_button_background_color));
                Log.i("contactList", contact.getFullName() + " favorite: " + contact.isFavorite());
            });
        }

        @Override
        public void onClick(View view) {
            Contact contact = contactsList.get(getAdapterPosition());
            //Toast.makeText(context, contact.name, Toast.LENGTH_SHORT).show();
            //Use the intent system to navigate to the new activity
            Intent intent = new Intent(context, contactCardActivity.class);
            intent.putExtra(CONTACT_CARD, contact);
            context.startActivity(intent);
        }
    }

    public void setFilterList(List<Contact> filteredList){
        this.contactsList = filteredList;
        notifyDataSetChanged();
    }
}
