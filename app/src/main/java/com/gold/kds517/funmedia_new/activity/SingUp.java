package com.gold.kds517.funmedia_new.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.gold.kds517.funmedia_new.R;
import com.gold.kds517.funmedia_new.adapter.CustomAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SingUp extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    EditText txt_name,txt_mail;
    Spinner spinner;
    CustomAdapter customAdapter;
    Button btn_send;
    List<String> mail_datas;
    String mail_data,name,mail;
    String subject,message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .penaltyLog()
                .detectAll()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .penaltyLog()
                .detectAll()
                .build());
        setContentView(R.layout.activity_sing_up);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mail_datas = new ArrayList<>();
        for (int i = 0; i < getResources().getStringArray(R.array.mail_array).length; i++) {
            mail_datas.add(getResources().getStringArray(R.array.mail_array)[i]);
        }
        txt_name = findViewById(R.id.name);
        txt_mail = findViewById(R.id.mail);
        btn_send = findViewById(R.id.send_btn);
        btn_send.setOnClickListener(this);
        spinner = findViewById(R.id.spinner);
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinner);

        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
        spinner.setOnItemSelectedListener(this);
        customAdapter = new CustomAdapter(getApplicationContext(),R.layout.item_spinner,mail_datas);
        customAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(customAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_btn:
                name = txt_name.getText().toString();
                mail = txt_mail.getText().toString();
                if(name.isEmpty() || mail.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Name or E-mail is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                SpannableString sp0 = new SpannableString("User Name "+name+" SUBSCRIPTION RENEWAL");
                SpannableString sp1 = new SpannableString("User Email: ");
                SpannableString sp2 = new SpannableString(mail);
                SpannableString sp5 = new SpannableString("Requested Subscription Renewal: ");
                SpannableString sp6 = null;
                sp6 = new SpannableString(mail_data);
                sp0.setSpan(new RelativeSizeSpan(4f), 0, sp0.length(), 0);
                sp0.setSpan(new StyleSpan(Typeface.BOLD), 0, sp2.length(), 0);
                sp1.setSpan(new RelativeSizeSpan(2f), 0, sp1.length(), 0);
                sp2.setSpan(new RelativeSizeSpan(4f), 0, sp2.length(), 0);
                sp2.setSpan(new StyleSpan(Typeface.BOLD), 0, sp2.length(), 0);
                sp5.setSpan(new RelativeSizeSpan(2f), 0, sp5.length(), 0);
                sp6.setSpan(new RelativeSizeSpan(4f), 0, sp6.length(), 0);
                sp6.setSpan(new StyleSpan(Typeface.BOLD), 0, sp6.length(), 0);
                CharSequence finalText = TextUtils.concat(sp1, " ", sp2,  "\n\n",sp5," ",sp6);
                CharSequence subject0 = TextUtils.concat(sp0);
                subject = String.valueOf(subject0);
                message = String.valueOf(finalText);
                sendMail();
//                Toast.makeText(getApplicationContext(),"Sending...",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mail_data = mail_datas.get(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    protected void sendMail(){
        String destmailid = "funmedia.iptv@gmail.com";
//        String destmailid = "gribeiro8@gmail.com";
        String sendrmailid = "apprequest@mytv.support";
        final String uname = "apprequest@mytv.support";
        final String pwd = "qOHdtLIpIkJNHM";
        String smtphost = "mail.mytv.support";
        Properties propvls = new Properties();
        propvls.put("mail.smtp.auth", "true");
        propvls.put("mail.smtp.starttls.enable", "true");
        propvls.put("mail.smtp.host", smtphost);
        propvls.put("mail.smtp.port", "2525");
        Session sessionobj = Session.getInstance(propvls,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(uname, pwd);
                    }
                });
        try {
            javax.mail.Message messageobj = new MimeMessage(sessionobj);
            messageobj.setFrom(new InternetAddress(sendrmailid));
            messageobj.setRecipients(javax.mail.Message.RecipientType.TO,InternetAddress.parse(destmailid));
            messageobj.setSubject(subject);
            messageobj.setText(message);
            Transport.send(messageobj);
            Toast.makeText(this,"Thank you for the renewal request, your request has been sent to your reseller, someone will contact you soon", Toast.LENGTH_LONG).show();
        } catch (MessagingException exp) {
            Toast.makeText(this,"Sorry, Please try again", Toast.LENGTH_LONG).show();
//            throw new RuntimeException(exp);
        }
    }
}
