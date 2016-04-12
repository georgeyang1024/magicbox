package cn.georgeyang.wificonnect.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.georgeyang.wificonnect.R;
import cn.georgeyang.wificonnect.adapter.MyListViewAdapter;
import cn.georgeyang.wificonnect.api.OnNetworkChangeListener;
import cn.georgeyang.wificonnect.utils.WifiAdminUtils;
import cn.georgeyang.wificonnect.utils.WifiConnectUtils;


/**
 * Created by Xiho on 2016/2/2.
 */
public class WifiConnDialog extends Dialog {
    private Context context;
    private ScanResult scanResult;
    private TextView txtWifiName;
    private TextView txtSinglStrength;
    private TextView txtSecurityLevel;
    private TextView txtBtnConn;
    private TextView txtBtnCancel;
    private EditText edtPassword;
    private CheckBox cbxShowPass;
    // wifi列表的listview
    private ListView mListView;
    // wifi列表item的指针
    private int mPosition;
    // wifi列表的适配器
    private MyListViewAdapter mAdapter;
    // wifi列表的数据
    private List<ScanResult> mScanResultList;
    private String wifiName;
    private String securigyLevel;
    private int level;

    public WifiConnDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * 传wifi名称和信号等构造函数
     *
     * @param context
     * @param theme
     * @param wifiName
     * @param singlStren
     * @param securityLevl
     */
    private WifiConnDialog(Context context, int theme, String wifiName,
                           int singlStren, String securityLevl) {
        super(context, theme);
        this.context = context;
        this.wifiName = wifiName;
        this.level = singlStren;
        this.securigyLevel = securityLevl;
    }

    /**
     * 传当前wifi信息
     *
     * @param context
     * @param theme
     * @param scanResult
     * @param onNetworkChangeListener
     */
    public WifiConnDialog(Context context, int theme, ListView mListView,
                          int mPosition, MyListViewAdapter mAdapter, ScanResult scanResult,
                          List<ScanResult> mScanResultList,
                          OnNetworkChangeListener onNetworkChangeListener) {
        this(context, theme, scanResult.SSID, scanResult.level,
                scanResult.capabilities);
        this.mListView = mListView;
        this.mPosition = mPosition;
        this.mAdapter = mAdapter;
        this.scanResult = scanResult;
        this.mScanResultList = mScanResultList;
        this.onNetworkChangeListener = onNetworkChangeListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_wifi_conn);
        setCanceledOnTouchOutside(false);
        initView();
        setListener();
    }

    private void setListener() {

        edtPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (TextUtils.isEmpty(s)) {
                    txtBtnConn.setEnabled(false);
                    cbxShowPass.setEnabled(false);

                } else {
                    txtBtnConn.setEnabled(true);
                    cbxShowPass.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cbxShowPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    // 文本正常显示
                    edtPassword
                            .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    Editable etable = edtPassword.getText();
                    Selection.setSelection(etable, etable.length());

                } else {
                    // 文本以密码形式显示
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    // 下面两行代码实现: 输入框光标一直在输入文本后面
                    Editable etable = edtPassword.getText();
                    Selection.setSelection(etable, etable.length());

                }
            }
        });

        txtBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("txtBtnCancel");
                WifiConnDialog.this.dismiss();
            }
        });

        txtBtnConn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                WifiConnectUtils.WifiCipherType type = null;
                if (scanResult.capabilities.toUpperCase().contains("WPA")) {
                    type = WifiConnectUtils.WifiCipherType.WIFICIPHER_WPA;
                } else if (scanResult.capabilities.toUpperCase()
                        .contains("WEP")) {
                    type = WifiConnectUtils.WifiCipherType.WIFICIPHER_WEP;
                } else {
                    type = WifiConnectUtils.WifiCipherType.WIFICIPHER_NOPASS;
                }
                // 去连接网络
                WifiAdminUtils mWifiAdmin = new WifiAdminUtils(context);
                /**是否去连接了 */
                if (WifiConnDialog.this != null) {
                    dismiss();
                }
                boolean isConnect = mWifiAdmin.connect(scanResult.SSID, edtPassword.getText().toString().trim(), type);
                Log.d("WifiListActivity", isConnect + "是否去连接的值");
                if (isConnect) {
                    Log.d("WifiListActivity", "去连接wifi了");
                    onNetworkChangeListener.onNetWorkConnect();
                } else {
                    Log.d("WifiListActivity", "没有去连接wifi");
                    onNetworkChangeListener.onNetWorkConnect();
                }
//				return isConnect;
            }
        });
    }

    private void initView() {
        txtWifiName = (TextView) findViewById(R.id.txt_wifi_name);
        txtSinglStrength = (TextView) findViewById(R.id.txt_signal_strength);
        txtSecurityLevel = (TextView) findViewById(R.id.txt_security_level);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        cbxShowPass = (CheckBox) findViewById(R.id.cbx_show_pass);
        txtBtnCancel = (TextView) findViewById(R.id.txt_btn_cancel);
        txtBtnConn = (TextView) findViewById(R.id.txt_btn_connect);
        txtWifiName.setText(wifiName);
        txtSinglStrength.setText(WifiAdminUtils.singlLevToStr(level));
        txtSecurityLevel.setText(securigyLevel);
        txtBtnConn.setEnabled(false);
        cbxShowPass.setEnabled(false);

    }

    @Override
    public void show() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);

        super.show();
        getWindow().setLayout((int) (size.x * 9 / 10),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    private void showShortToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private OnNetworkChangeListener onNetworkChangeListener;
}
