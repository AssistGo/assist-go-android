package com.example.assistgoandroid.emergency;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;

import com.example.assistgoandroid.R;

import java.util.ArrayList;
import java.util.Locale;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
/**
 * Le Jie Bennett and Sena
 * https://stackoverflow.com/questions/57863500/how-can-i-get-my-current-location-in-android-using-gps
 */
public class emergencyActivity extends AppCompatActivity {
    ListView listView_em;
    String[] emergencyCountry;
    String[] emergencyNumbers;
    HashMap<String,String> emergencyCountryNumbers = new HashMap<>();

    //User Location
    //https://stackoverflow.com/questions/57863500/how-can-i-get-my-current-location-in-android-using-gps
    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;
    String userCountry;
    String ambulanceNumber,policeNumber,firefighterNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_page_layout);

        //Creates the hashmap used to look up locations
        createEmergencyCountryNumberHashmap();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            Log.d("emergencyActivity", "onCreate: Trying");
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gps_loc != null) {
            Log.d("emergencyActivity", "onCreate: if");
            final_loc = gps_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }
        else if (network_loc != null) {
            Log.d("emergencyActivity", "onCreate: else if");

            final_loc = network_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }
        else {
            Log.d("emergencyActivity", "onCreate: else");

            latitude = 0.0;
            longitude = 0.0;
        }
        Log.d("emergencyActivity", "onCreate: lat" + latitude + "long " + longitude);


        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);

        try {

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Log.d("emergencyActivity", "onCreate: address" + addresses);
            if (addresses != null && addresses.size() > 0) {
                userCountry = addresses.get(0).getCountryName();
            }
            else {
                userCountry = "Unknown";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Get Country emergency numbers
        Log.d("emergencyActivity", "onCreate: " + userCountry);
        String emergencyNumbersFromHashmap = emergencyCountryNumbers.get(userCountry);
        emergencyNumbersFromHashmap = "911/911/911";
        Log.d("emergencyActivity", "onCreate: " + emergencyNumbersFromHashmap);
        String [] splitEmergencyNumbers = emergencyNumbersFromHashmap.split("/");
        ambulanceNumber = splitEmergencyNumbers[1];
        policeNumber = splitEmergencyNumbers[0];
        firefighterNumber = splitEmergencyNumbers[2];
        Log.d("emergencyActivity", "onCreate: ambulance" + ambulanceNumber);
        Log.d("emergencyActivity", "onCreate: police" + policeNumber);
        Log.d("emergencyActivity", "onCreate: firefighter" + firefighterNumber);

        //To prevent actual numbers from being called for demo
        ambulanceNumber = "5183391997";
        policeNumber = "5183391997";
        firefighterNumber = "5183391997";







        //Set up the cells and search bar for settings page
        String[] title =  getResources().getStringArray(R.array.emergency_titles);
        int[] images = {R.drawable.image_am, R.drawable.image_police, R.drawable.image_fire};

        listView_em = findViewById(R.id.listView_em);

        // now create an adapter class
        MyAdapter adapterem = new MyAdapter(this, title, images);
        listView_em.setAdapter(adapterem);
        //onclick listener for each cell
        listView_em.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position ==  0) {
                    //Intent intent = new Intent(emergencyActivity.this, ambulanceActivity.class);
                    Intent intent = new Intent(emergencyActivity.this, DialerActivityGit.class);
                    intent.putExtra("EMERGENCY_SERVICE", "AMBULANCE");
                    intent.putExtra("number",ambulanceNumber);
                    intent.putExtra("name","Ambulance");

                    startActivity(intent);
                }
                if (position ==  1) {
                    //Intent intent = new Intent(emergencyActivity.this, policeActivity.class);
                    Intent intent = new Intent(emergencyActivity.this, DialerActivityGit.class);
                    intent.putExtra("EMERGENCY_SERVICE", "POLICE");
                    intent.putExtra("number",policeNumber);
                    intent.putExtra("name","Police");



                    startActivity(intent);
                }
                if (position ==  2) {
                    //Intent intent = new Intent(emergencyActivity.this, fiirefighterActivity.class);
                    Intent intent = new Intent(emergencyActivity.this, DialerActivityGit.class);
                    intent.putExtra("EMERGENCY_SERVICE", "FIREFIGHTER");
                    intent.putExtra("number",firefighterNumber);
                    intent.putExtra("name","Firefighter");

                    startActivity(intent);
                }
                if (position ==  3) {
                    //Intent intent = new Intent(emergencyActivity.this, callActivity.class);
                    Intent intent = new Intent(emergencyActivity.this, DialerActivityGit.class);
                    startActivity(intent);
                }

            }
        });

    }


    public void getSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            String resultString = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            // TODO PARSE STRING HERE!!!!

        }
    }
    private void createEmergencyCountryNumberHashmap(){
        //Police/Amubulance/Fire
        emergencyCountry =  getResources().getStringArray(R.array.emergency_country);
        emergencyNumbers = getResources().getStringArray(R.array.emergency_numbers);
        for (int i = 0; i < emergencyCountry.length; i++) {
            emergencyCountryNumbers.put(emergencyCountry[i], emergencyNumbers[i]);
        }

    }
}
class MyAdapter extends ArrayAdapter<String> {

    Context context;
    String[] title;
    int[] imgs;

    MyAdapter (Context c, String[] title, int[] imgs) {
        super(c, R.layout.emergy_page_cell, R.id.textView_em1, title);
        this.context = c;
        this.title = title;
        this.imgs = imgs;

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.emergy_page_cell, parent, false);
        ImageView images = row.findViewById(R.id.image);
        TextView myTitle = row.findViewById(R.id.textView_em1);

        // now set our images and title of each cell on the view
        images.setImageResource(imgs[position]);
        myTitle.setText(title[position]);

        return row;
    }


}


