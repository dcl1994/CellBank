package com.example.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.example.smsdemo.R;

/**
 * 联系我们界面
 */
public class ContactUs extends Activity {
    private TextView mytextview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        mytextview= (TextView) findViewById(R.id.kefu_phone);

        mytextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到拨号界面
                Uri uri = Uri.parse("tel:0769-22890598");
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });



    }
    //返回上一步
    public void return1(View v){
        finish();
    }
}
