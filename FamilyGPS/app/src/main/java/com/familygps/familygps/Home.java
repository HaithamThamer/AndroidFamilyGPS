package com.familygps.familygps;


import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class Home extends AppCompatActivity {
    HDatabaseConnection db;
    ListView lstNumbers;
    EditText txtNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        Log.e("Home","1111");
        //Request Permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Home","222");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                Log.e("Home","3333");
            } else {
                ActivityCompat.requestPermissions(this, new String[]
                        {
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET
                }, 0);
                finish();
            }
        }
        Log.e("Home","555");
        txtNumber = (EditText)findViewById(R.id.txtNumber);
        lstNumbers = (ListView)findViewById(R.id.lstNumbers);
        Log.e("Home","666");
        //DatabaseConnection
        db = new HDatabaseConnection(this);
        Log.e("Home","777");
        //if (!db.isPasswordSetted()){
        //    startActivity(new Intent(Home.this,SetPassword.class));
        //}
        Log.e("Home","888");
        //استدعاء الارقام المدخلة مسبقا الى الواجهة من قاعدة البيانات
        lstNumbers.setAdapter(new NumberAdapter(db.getNumbers()));

        //Remove Contacts
        Log.e("Home","999");
        // التحكم في حساسية الـ Audio Jack
        IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        Receiver receiver = new Receiver();
        registerReceiver( receiver, receiverFilter );
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void btnAddNumber(View view) {
        if (txtNumber.getText().toString().length() > 3){
            db.addNumber(txtNumber.getText().toString());
            lstNumbers.setAdapter(new NumberAdapter(db.getNumbers()));
            txtNumber.setText("");
        }
    }

    class NumberAdapter extends BaseAdapter{
        ArrayList<Number> numbers = new ArrayList<>();
        public NumberAdapter(ArrayList<Number> numbers){
            this.numbers = numbers;
        }
        @Override
        public int getCount() {
            return numbers.size();
        }

        @Override
        public Object getItem(int i) {
            return numbers.get(i);
        }

        @Override
        public long getItemId(int i) {
            return numbers.get(i).id;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = getLayoutInflater().inflate(R.layout.number_layout,null);

            final TextView txtNumber = (TextView)v.findViewById(R.id.txtNumber);
            final TextView txtId = (TextView)v.findViewById(R.id.txtId);
            ToggleButton tglSaveMode = (ToggleButton)v.findViewById(R.id.tglSaveMode);
            Button btnRemove = (Button)v.findViewById(R.id.btnRemove);

            txtNumber.setText(numbers.get(i).number);
            txtId.setText(numbers.get(i).id+"");
            if (numbers.get(i).isSaveMode)
                tglSaveMode.toggle();
//if its safe mode it gonna stay safe mode
            tglSaveMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    db.saveMode(Integer.parseInt(txtId.getText().toString()),b ? 1 : 0);
                }
            });
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Home.this,"Removed", Toast.LENGTH_SHORT).show();
                    Log.e("setOnClickListener",txtId.getText().toString());
                    db.removeNumber(txtNumber.getText().toString());
                    lstNumbers.setAdapter(new NumberAdapter(db.getNumbers()));
                }
            });
            return v;
        }
    }
}