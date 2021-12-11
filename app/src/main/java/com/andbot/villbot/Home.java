package com.andbot.villbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Home extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private RecyclerView chatsRV;
    private LottieAnimationView sendMsgIB;
    private ConstraintLayout botbody;
    private EditText userMsgEdt;
    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";
    private String Name, Gender, t_gender;
    private RequestQueue mRequestQueue;
    private ArrayList<MessageModal> messageModalArrayList;
    private MessageRVAdapter messageRVAdapter;
    Database_manager mydb;
    private int color_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                color_tv = Color.WHITE;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                color_tv = Color.BLACK;
                break;
        }
        chatsRV = findViewById(R.id.idRVChats);
        sendMsgIB = findViewById(R.id.idIBSend);
        userMsgEdt = findViewById(R.id.idEdtMessage);
        botbody = findViewById(R.id.botbody);
        mRequestQueue = Volley.newRequestQueue(Home.this);
        mRequestQueue.getCache().clear();
        messageModalArrayList = new ArrayList<>();
        mydb = new Database_manager(this);
        try {
            mydb.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Cursor my_db = mydb.fetch();

        if ((my_db.moveToFirst()) == false) {
            Log.d("Null DB", "True ");
            showCustomDialog();
        }
        else {
            my_db.moveToFirst();
            Name = my_db.getString(0);
            Gender = my_db.getString(1);
        }

        sendMsgIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userMsgEdt.getText().toString().isEmpty()) {
                    Toast.makeText(Home.this, "Please enter your message..", Toast.LENGTH_SHORT).show();
                    return;
                }

                botbody.setVisibility(View.GONE);
                sendMessage(userMsgEdt.getText().toString());
                userMsgEdt.setText("");
            }
        });

        messageRVAdapter = new MessageRVAdapter(messageModalArrayList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Home.this, RecyclerView.VERTICAL, false);
        chatsRV.setLayoutManager(linearLayoutManager);
        chatsRV.setAdapter(messageRVAdapter);
    }

    private void sendMessage(String userMsg) {
        messageModalArrayList.add(new MessageModal(userMsg, USER_KEY, Gender, color_tv));
        messageRVAdapter.notifyDataSetChanged();

        String url = "http://api.brainshop.ai/get?bid=161486&key=XeXmAiVuz2g66QcZ&uid=0&msg=" + userMsg;
        RequestQueue queue = Volley.newRequestQueue(Home.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String botResponse = response.getString("cnt");
                    messageModalArrayList.add(new MessageModal(botResponse, BOT_KEY, "B", color_tv));

                    messageRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();

                    messageModalArrayList.add(new MessageModal("No response", BOT_KEY, "B", color_tv));
                    messageRVAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                messageModalArrayList.add(new MessageModal("Sorry no response found", BOT_KEY, "B", color_tv));
                Toast.makeText(Home.this, "No response from the bot..", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonObjectRequest);
    }
    void showCustomDialog() {

        final Dialog dialog = new Dialog(Home.this);
        EditText gn_ev;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.init_dialog);
        gn_ev = dialog.findViewById(R.id.gn_cover);
        Spinner spinner = (Spinner) dialog.findViewById(R.id.gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genderarr, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setBackgroundColor(000000);
        spinner.setOnItemSelectedListener(this);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> arg0, View v, int position, long id)
            {
                t_gender = arg0.getItemAtPosition(position).toString();
                v.setVisibility(View.GONE);
                Log.d("gender", t_gender);
                if(!t_gender.equals(""))
                {
                    gn_ev.setText(t_gender);
                }
                else{
                    gn_ev.setText("Select the Gender");
                }
            }

            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
        });
        final EditText name = dialog.findViewById(R.id.name);
        Button submitButton = dialog.findViewById(R.id.submit_button);
        View gender_error = dialog.findViewById(R.id.g_line);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name_TV = name.getText().toString();
                if(name_TV.isEmpty())
                {
                    name.setError("Full name is required!");
                    name.requestFocus();
                    return;
                }
                if(t_gender.equals(""))
                {
                    gender_error.setVisibility(View.VISIBLE);
                    spinner.requestFocus();
                    return;
                }
                mydb.insert(name_TV,t_gender);
                Log.d("Locale DB",String.valueOf(mydb.fetch()));
                Cursor cursor = mydb.fetch();
                if(cursor.moveToFirst()){
                    do{
                        String name = cursor.getString(0);
                        String gender = cursor.getString(1);
                        Log.d("name out",name);
                        Log.d("gender out",gender);
                    }while(cursor.moveToNext());
                    startActivity(new Intent(getApplicationContext(),Home.class));
                    overridePendingTransition(0,0);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}