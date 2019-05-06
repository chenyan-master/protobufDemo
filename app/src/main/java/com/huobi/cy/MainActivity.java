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
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding mainBinding;

    private byte[] bytesData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mainBinding.btnRead.setOnClickListener(this);
        mainBinding.btnWrite.setOnClickListener(this);
        mainBinding.btnReadList.setOnClickListener(this);
        mainBinding.btnWriteList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_read:
                testReadPB();
                break;
            case R.id.btn_write:
                bytesData = testWritePB(mainBinding.etAccount.getText().toString(), mainBinding.etPassword.getText().toString());
                StringBuilder sb = new StringBuilder();
                for(int i = 0;i < bytesData.length;i ++) {
                    sb.append(String.format("%8s", Integer.toBinaryString(bytesData[i] & 0xFF)).replace(' ', '0')).append(" ");
                }
                Log.d("MainActivity", "bytesData: " + sb.toString());
                break;
            case R.id.btn_read_list:
                testReadList();
                break;
            case R.id.btn_write_list:
                bytesData = testWriteList(mainBinding.etAccount.getText().toString(), mainBinding.etPassword.getText().toString());
                StringBuilder sb1 = new StringBuilder();
                for(int i = 0;i < bytesData.length;i ++) {
                    sb1.append(String.format("%8s", Integer.toBinaryString(bytesData[i] & 0xFF)).replace(' ', '0')).append(" ");
                }
                Log.d("MainActivity", "bytesData: " + sb1.toString());
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

    /**
     * 写list
     * @return
     */
    public byte[] testWriteList(String account, String password) {
        UserInfo.ListOfUsers.Builder builder = UserInfo.ListOfUsers.newBuilder();
        for(int i = 0;i < 100;i ++) {
            UserInfo.User.Builder user = UserInfo.User.newBuilder();
            user.setAccount(account + i);
            user.setPassword(password + i);
            builder.addUsers(user);
        }
        //对象写入字节数组输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            builder.build().writeTo(baos);
        } catch (IOException e) {
        }
        return baos.toByteArray();
    }

    /**
     * 读List
     */
    public void testReadList() {
        if(bytesData != null) {
            try {
                //字节数组解析成实体对象
                UserInfo.ListOfUsers listOfUsers = UserInfo.ListOfUsers.parseFrom(bytesData);
                List<UserInfo.User> list = listOfUsers.getUsersList();
                Iterator<UserInfo.User> iterator = list.iterator();
                StringBuilder sb = new StringBuilder();
                while (iterator.hasNext()) {
                    UserInfo.User user = iterator.next();
                    sb.append(user.getAccount()).append(",").append(user.getPassword()).append("\n");
                }
                mainBinding.tvProtobuf.setText(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
