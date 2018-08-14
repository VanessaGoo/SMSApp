package sg.edu.rp.c346.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView tvTo;
    EditText etTo;
    TextView tvContent;
    EditText etContent;
    Button btnSend;
    Button btnViaMessage;
    BroadcastReceiver br = new MessageReceiver();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        tvTo = findViewById(R.id.textViewTo);
        etTo = findViewById(R.id.editTextTo);
        tvContent = findViewById(R.id.textViewContent);
        etContent = findViewById(R.id.editTextContent);
        btnSend = findViewById(R.id.button);
        btnViaMessage = findViewById(R.id.buttonVia);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmsManager smsManager = SmsManager.getDefault();
                String num = etTo.getText().toString();
                String con = etContent.getText().toString();
                String splitNum[] = num.split(",");
                for(String allNum : splitNum){
                    smsManager.sendTextMessage(allNum,null,con,null,null);
                }
                Toast.makeText(getBaseContext(),"Message sent",Toast.LENGTH_SHORT).show();
                etContent.setText("");
            }
        });
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br, filter);

        btnViaMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    SmsManager smsManager = SmsManager.getDefault();
                    String num = etTo.getText().toString();
                    String con = etContent.getText().toString();

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("sms:" + num));
                    intent.putExtra("sms_body", con);
                    startActivity(intent);
            }
        });
    }
    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }
}