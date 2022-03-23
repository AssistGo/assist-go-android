package com.example.assistgoandroid.Setttings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.assistgoandroid.R;

public class ChangeUserNameDialog extends AppCompatDialogFragment {
    EditText newFirstName, newLastName;
    private ChangeUserNameDialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.change_username_popup,null);
        builder.setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String firstName = newFirstName.getText().toString();
                String lastName = newLastName.getText().toString();
                if (firstName.isEmpty() || lastName.isEmpty()){
                    Toast.makeText(view.getContext(), "First or Last Name Cannot Be Blank", Toast.LENGTH_SHORT).show();

                }
                else {
                    listener.setFirstAndLastName(firstName, lastName);
                }


            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        newFirstName = view.findViewById(R.id.newFirstName);
        newLastName = view.findViewById(R.id.newLastName);
        return builder.create();

    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            listener = (ChangeUserNameDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ChangeUserNameDialogListener");
        }
    }
    public interface ChangeUserNameDialogListener{
        void setFirstAndLastName(String firstName, String lastName);
    }

}