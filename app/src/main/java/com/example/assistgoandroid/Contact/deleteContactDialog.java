package com.example.assistgoandroid.Contact;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.example.assistgoandroid.R;

public class deleteContactDialog extends AppCompatDialogFragment {

    private deleteContactDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_contact_dialog, null);

        builder.setView(view)
                .setNegativeButton("No", (dialogInterface, i) -> {
                })

                .setPositiveButton("Yes", (dialogInterface, i) -> listener.deleteContact());

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (deleteContactDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context +
                    "must implement deleteContactDialogListener");
        }
    }

    public interface deleteContactDialogListener {
        void deleteContact();
    }
}
