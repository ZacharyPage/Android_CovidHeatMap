package com.example.covidheatmap.ui.testing_centers;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.covidheatmap.MainActivity;
import com.example.covidheatmap.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestingCenterFragment extends Fragment {
    private ListView lv;
    private ArrayList<String> al;
    private ArrayAdapter<String> aa;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //Allows the application to change the view to the Testing Center Fragment
        View v = inflater.inflate(R.layout.fragment_testing_centers, container, false);

        // Initializing & Declaring Variables
        lv = (ListView) v.findViewById(R.id.TestingCenterList);
        al = new ArrayList<String>();
        aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1,al);
        lv.setAdapter(aa);

        List<String[]> listTC = null;

        //Adding the cities to the ListView
        try{
            listTC = readItems(R.raw.testing_centers);
            for (int i = 0; i < listTC.size(); i++)
            {
                al.add(listTC.get(i)[0] + "\n" +
                        listTC.get(i)[1] + "\n" +
                        listTC.get(i)[2] + " to " +
                        listTC.get(i)[3]);
            }
        }catch (JSONException e) {
            Toast.makeText(getActivity(), "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        }

        // Event Click Listener
        // Function: Copies the address of the location selected for use in a different application
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Address Copied to Clipboard", Toast.LENGTH_LONG).show();
            }
        });
        return v;
    }

    private ArrayList<String[]> readItems(int resource) throws JSONException {
        ArrayList<String[]> list = new ArrayList<>();
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            String CityState = object.getString("CityState");
            String Address = object.getString("Address");
            int HourOpen = object.getInt("HourOpen");
            int HourClose = object.getInt("HourClose");
            list.add(new String[] {CityState, Address,
                    ConvertIntToTime(HourOpen), ConvertIntToTime(HourClose)});
        }
        return list;
    }

    private String ConvertIntToTime(int i) {
        // Morning
        if(i < 12 || i == 24)
        {
            if(i < 10)
                return "0" + i +":00am";
            return i + ":00am";
        }
        else if(i == 12)
            return "12:00am";
        // Afternoon
            i -= 12;
        if (i < 10)
            return "0" + i + ":00pm";
        return i + ":00pm";
    }
}