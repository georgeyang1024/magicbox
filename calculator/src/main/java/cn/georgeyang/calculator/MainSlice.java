package cn.georgeyang.calculator;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.transition.Slide;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.georgeyang.calculator.R;
import online.magicbox.lib.Slice;


/**
 * Created by george.yang on 16/4/10.
 */
public class MainSlice extends Slice {
    String str="";
    EditText et;
    int c=0,flag=0;
    double b=0.0,g=0.0,f=0.0;
    View vi;

    public MainSlice(Context base, Object holder) {
        super(base, holder);
    }

    public double calculater(){
        switch(c){
            case 0:f=g;break;
            case 1:f=b+g;break;
            case 2:f=b-g;break;
            case 3:f=b*g;break;
            case 4:f=b/g;break;
        }

        b=f;
        c=0;

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final Button number[]=new Button[10];
        final Button fuhao[]=new Button[11];

        fuhao[0]=(Button)findViewById(R.id.button01);
        fuhao[1]=(Button)findViewById(R.id.button02);
        fuhao[2]=(Button)findViewById(R.id.button03);
        fuhao[3]=(Button)findViewById(R.id.button04);
        fuhao[4]=(Button)findViewById(R.id.button05);
        fuhao[5]=(Button)findViewById(R.id.button06);
        fuhao[6]=(Button)findViewById(R.id.buttonce);
        fuhao[7]=(Button)findViewById(R.id.buttonc);
        fuhao[8]=(Button)findViewById(R.id.zheng);
        fuhao[9]=(Button)findViewById(R.id.kaifang);
        fuhao[10]=(Button)findViewById(R.id.pingfang);

        number[0]=(Button)findViewById(R.id.button0);
        number[1]=(Button)findViewById(R.id.button1);
        number[2]=(Button)findViewById(R.id.button2);
        number[3]=(Button)findViewById(R.id.button3);
        number[4]=(Button)findViewById(R.id.button4);
        number[5]=(Button)findViewById(R.id.button5);
        number[6]=(Button)findViewById(R.id.button6);
        number[7]=(Button)findViewById(R.id.button7);
        number[8]=(Button)findViewById(R.id.button8);
        number[9]=(Button)findViewById(R.id.button9);

        et=(EditText) findViewById(R.id.textView1);

        et.setText(str);

        fuhao[6].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                str="";
                et.setText(str);
                vi=v;

            }
        });

        fuhao[7].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                b=0.0;c=0;g=0.0;
                str="";
                et.setText(str);

            }
        });

        fuhao[8].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(vi!=fuhao[5]&&str!=""){
                    char ch=str.charAt(0);
                    if(ch=='-')
                        str=str.replace("-","");
                    else
                        str="-"+str;
                    et.setText(str);
                }
            }
        });

        fuhao[9].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(str!=""){
                    double a=string2double(str);
                    str=Math.sqrt(a)+"";
                    et.setText(str);
                }}
        });

        fuhao[10].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(str!=""){
                    double a=string2double(str);
                    str=""+a*a;
                    et.setText(str);
                }}
        });

        number[0].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(flag==1){
                    str="";
                    str+=0;
                    et.setText(str);
                    flag=0;
                } else{
                    char ch1[];
                    ch1=str.toCharArray();
                    if(!(ch1.length==1&&ch1[0]=='0'))
                    {str+=0;
                        et.setText(str);}

                }
                vi=v;
            }
        });


        number[1].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(flag==1){
                    str="";
                    str+=1;
                    et.setText(str);
                    flag=0;
                } else{
                    str+=1;
                    et.setText(str);
                }
                vi=v;
            }
        });

        number[2].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(flag==1){
                    str="";
                    str+=2;
                    et.setText(str);
                    flag=0;
                } else{
                    str+=2;
                    et.setText(str);
                }
                vi=v;
            }
        });

        number[3].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(flag==1){
                    str="";
                    str+=3;
                    et.setText(str);
                    flag=0;
                } else{
                    str+=3;
                    et.setText(str);
                }
                vi=v;
            }
        });

        number[4].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(flag==1){
                    str="";
                    str+=4;
                    et.setText(str);
                    flag=0;
                } else{
                    str+=4;
                    et.setText(str);
                }
                vi=v;
            }
        });

        number[5].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(flag==1){
                    str="";
                    str+=5;
                    et.setText(str);
                    flag=0;
                } else{
                    str+=5;
                    et.setText(str);
                }
                vi=v;
            }
        });

        number[6].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(flag==1){
                    str="";
                    str+=6;
                    et.setText(str);
                    flag=0;
                } else{
                    str+=6;
                    et.setText(str);
                }
                vi=v;
            }
        });

        number[7].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(flag==1){
                    str="";
                    str+=7;
                    et.setText(str);
                    flag=0;
                } else{
                    str+=7;
                    et.setText(str);
                }
                vi=v;
            }
        });

        number[8].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(flag==1){
                    str="";
                    str+=8;
                    et.setText(str);
                    flag=0;
                } else{
                    str+=8;
                    et.setText(str);
                }
                vi=v;
            }
        });

        number[9].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(flag==1){
                    str="";
                    str+=9;
                    et.setText(str);
                    flag=0;
                } else{
                    str+=9;
                    et.setText(str);
                }
                vi=v;
            }
        });

        fuhao[0].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(str!=""){
                    if(vi==fuhao[0]||vi==fuhao[1]||vi==fuhao[2]||vi==fuhao[3]){
                        c=1;
                    } else{
                        g=string2double(str);
                        calculater();
                        str=""+f;
                        et.setText(str);
                        c=1;
                        flag=1;
                        vi=v;
                    }}
            }
        });

        fuhao[1].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(str!=""){
                    if(vi==fuhao[0]||vi==fuhao[1]||vi==fuhao[2]||vi==fuhao[3]){
                        c=2;
                    } else{
                        g=string2double(str);
                        calculater();
                        str=""+f;
                        et.setText(str);
                        c=2;
                        flag=1;
                        vi=v;
                    }}
            }
        });

        fuhao[2].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(str!=""){
                    if(vi==fuhao[0]||vi==fuhao[1]||vi==fuhao[2]||vi==fuhao[3]){
                        c=3;
                    } else{
                        g=string2double(str);
                        calculater();
                        str=""+f;
                        et.setText(str);
                        c=3;
                        flag=1;
                        vi=v;
                    }}
            }
        });

        fuhao[3].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(str!=""){
                    if(vi==fuhao[0]||vi==fuhao[1]||vi==fuhao[2]||vi==fuhao[3]){
                        c=4;
                    } else{
                        g=string2double(str);
                        calculater();
                        str=""+f;
                        et.setText(str);
                        c=4;
                        flag=1;
                        vi=v;
                    }}
            }
        });

        fuhao[4].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(str!=""&&vi!=fuhao[0]&&vi!=fuhao[1]&&vi!=fuhao[2]&&vi!=fuhao[3]){
                    g=string2double(str);
                    calculater();
                    str=""+f;
                    et.setText(str);
                    flag=1;
                    vi=v;

                }
            }
        });


        fuhao[5].setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(str==""){
                    str+=".";
                    et.setText(str);
                }
                else{
                    char ch1[];int x=0;
                    ch1=str.toCharArray();
                    for(int i=0;i<ch1.length;i++)
                        if(ch1[i]=='.')
                            x++;
                    if(x==0){
                        str+=".";
                        et.setText(str);
                    }
                }

            }
        });
    }

    private static double string2double (String str) {
        try {
            return Double.valueOf(str);
        } catch (Exception e) {

        }
        return 0;
    }
}
