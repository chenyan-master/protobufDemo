package com.huobi.cy;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.huobi.cy.databinding.ActivityMainBinding;
import com.huobi.cy.protobuf.UserInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding mainBinding;

    private byte[] bytesData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mainBinding.btnRead.setOnClickListener(this);
        mainBinding.btnWrite.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_read:
                testReadPB();
                break;
            case R.id.btn_write:
                bytesData = testWritePB(mainBinding.etAccount.getText().toString(), mainBinding.etPassword.getText().toString());
                Log.d("MainActivity", "bytesData: " + bytesData.toString());
                break;
            default:
                break;
        }
    }

    /**
     * protobuf字节数组解析成对象
     */
    public void testReadPB() {
        if(bytesData != null) {
            try {
                //字节数组解析成实体对象
                UserInfo.User user = UserInfo.User.parseFrom(bytesData);
                mainBinding.tvProtobuf.setText("account: " + user.getAccount() + ", password: " + user.getPassword());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * protobuf对象转成二进制
     * @return
     */
    public byte[] testWritePB(String account, String password) {
        UserInfo.User.Builder builder = UserInfo.User.newBuilder();
        builder.setAccount(account)
                .setPassword(password);

        //对象写入字节数组输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            builder.build().writeTo(baos);
        } catch (IOException e) {
        }
        return baos.toByteArray();
    }
}
