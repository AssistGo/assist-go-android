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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.assistgoandroid.R;
import java.util.ArrayList;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Adapter for contact list
 */

public class contactListAdapter extends RecyclerView.Adapter<contactListAdapter.ViewHolder> {
    final String CONTACT_CARD = "CONTACT_CARD";
    Activity activity;
    private ArrayList<Contact> contactsList;
    Context context;

    public contactListAdapter(Activity activity, ArrayList<Contact> contactsList) {
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
        //todo set favorite on click
//        Runnable onSelectFavClick = () -> {
//            if (holder.favoriteHeart.getImageAlpha() == R.drawable.empty_heart_icon)
//                holder.favoriteHeart.setImageResource(R.drawable.filled_heart_icon);
//            else holder.favoriteHeart.setImageResource(R.drawable.empty_heart_icon);
//        };
//        onSelectFavClick.run();
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
        ImageView addNewContactButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            contactName = itemView.findViewById(R.id.tvContactName);
            contactProfilePicture = itemView.findViewById(R.id.ivContactProfilePicture);
            favoriteHeart = itemView.findViewById(R.id.ivFavoriteHeart);
            addNewContactButton = itemView.findViewById(R.id.ivAddNewContactButton);

            itemView.setOnClickListener(this);

            //todo bug: cannot select again after unselecting
            favoriteHeart.setOnClickListener(view -> {
                if (favoriteHeart.getDrawable().getConstantState() == favoriteHeart.getResources().getDrawable( R.drawable.empty_heart_icon).getConstantState()) {
                    Log.i("favorite", "selected favorite");
                    Glide.with(context).load(R.drawable.filled_heart_icon).into(favoriteHeart);
                }
                else {
                    Log.i("favorite", "unselected favorite");
                    Glide.with(context).load(R.drawable.empty_heart_icon).into(favoriteHeart);
                }
            });
        }

        @Override
        public void onClick(View view) {
            //get notified of particular movie which was clicked on
            Contact contact = contactsList.get(getAdapterPosition());
            Toast.makeText(context, contact.name, Toast.LENGTH_SHORT).show();
            //Use the intent system to navigate to the new activity
            Intent intent = new Intent(context, contactCardActivity.class);
            intent.putExtra(CONTACT_CARD, contact);
            context.startActivity(intent);
        }
    }
}
