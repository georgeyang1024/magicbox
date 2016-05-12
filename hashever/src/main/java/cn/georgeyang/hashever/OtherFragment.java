package cn.georgeyang.hashever;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import online.magicbox.lib.PluginFragment;

/**
 * Created by george.yang on 16/5/8.
 */
public class OtherFragment extends PluginFragment implements Runnable {
    private EditText mEditText = null,mEditPwd = null;
    private CheckBox mCheckBox = null;
    private Button mClearButton = null;
    private Button mGenerateButton = null;
    private Button mCopyButton = null;
    private Spinner mSpinner = null;
    private TextView mResultTV = null;
    private ClipboardManager mClipboard = null;
    private String msHash = "";
    private String msToHash = "";
    private String[] mFunctions;
    private ProgressDialog mProgressDialog = null;
    private int miItePos = -1;
    private View view;
    private View layout_pwd;
    private String pwd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getPluginLayoutInflater().inflate(R.layout.fragment_other,null);

        mEditText = (EditText) view.findViewById(R.id.edittext);
        mEditPwd = (EditText) view.findViewById(R.id.editpwd);
        mClearButton = (Button) view.findViewById(R.id.ClearButton);
        mGenerateButton = (Button) view.findViewById(R.id.GenerateButton);
        mSpinner = (Spinner) view.findViewById(R.id.spinner);
        mResultTV = (TextView) view.findViewById(R.id.label_result);
        mCopyButton = (Button) view.findViewById(R.id.CopyButton);
        mClipboard = (ClipboardManager) getActivity().getSystemService("clipboard");
        mFunctions = getPluginContext().getResources().getStringArray(R.array.Algo_Array2);
        mCheckBox = (CheckBox) view.findViewById(R.id.UpperCaseCB);
        layout_pwd = view.findViewById(R.id.layout_pwd);
        layout_pwd.setVisibility(View.GONE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getPluginContext(), R.array.Algo_Array2, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(0); // base64 by default
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {
                // your code here
                // Hide the copy button
                if (!msHash.equals(""))
                    mCopyButton.setVisibility(View.INVISIBLE);
                // Clean the result text view
                if (mResultTV != null)
                    mResultTV.setText("");

                if (position==0) {
                    layout_pwd.setVisibility(View.GONE);
                } else {
                    layout_pwd.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on clicks
                mEditText.setText("");
                mEditPwd.setText("");
                if (mResultTV != null)
                    mResultTV.setText("");
                msHash = "";
                if (mCopyButton != null)
                    mCopyButton.setVisibility(View.INVISIBLE);
            }
        });

        mGenerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on clicks
                miItePos = mSpinner.getSelectedItemPosition();
                Editable InputEdit = mEditText.getText();
                msToHash = InputEdit.toString();
                pwd = mEditPwd.getText().toString();
                ComputeAndDisplayHash();
            }
        });

        mCopyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on clicks
                if (mClipboard != null) {
                    mClipboard.setText(msHash);
                    String sCopied = getPluginContext().getString(R.string.copied);
                    Toast.makeText(getActivity(), sCopied,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCheckBox.setChecked(false); // lower case by default
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on clicks
                if (!msHash.equals("")) {
                    // A hash value has already been calculated,
                    // just convert it to lower or upper case
                    String OldHash = msHash;
                    if (mCheckBox.isChecked()) {
                        msHash = OldHash.toUpperCase();
                    } else {
                        msHash = OldHash.toLowerCase();
                    }
                    if (mResultTV != null) {
                        String sResult = mResultTV.getText().toString();
                        sResult = sResult.replaceAll(OldHash, msHash);
                        mResultTV.setText(sResult);
                    }
                }
            }
        });


        return view;
    }

    private void ComputeAndDisplayHash() {
//        String sAlgo = "";
//        if (miItePos == 0)
//            sAlgo = "Adler-32";
//        else if (miItePos == 1)
//            sAlgo = "CRC-32";
//        else if (miItePos == 2)
//            sAlgo = "haval";
//        else if (miItePos == 3)
//            sAlgo = "md2";
//        else if (miItePos == 4)
//            sAlgo = "md4";
//        else if (miItePos == 5)
//            sAlgo = "md5";
//        else if (miItePos == 6)
//            sAlgo = "ripemd-128";
//        else if (miItePos == 7)
//            sAlgo = "ripemd-160";
//        else if (miItePos == 8)
//            sAlgo = "sha-1";
//        else if (miItePos == 9)
//            sAlgo = "sha-256";
//        else if (miItePos == 10)
//            sAlgo = "sha-384";
//        else if (miItePos == 11)
//            sAlgo = "sha-512";
//        else if (miItePos == 12)
//            sAlgo = "tiger";
//        else if (miItePos == 13)
//            sAlgo = "whirlpool";

        String sCalculating = getPluginContext().getString(R.string.Calculating);
        mProgressDialog = ProgressDialog.show(getActivity(), "",
                sCalculating, true);

        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    // Call when the thread is started
    public void run() {
        String newP = "";
        msHash = "";
//        if (mHashOpe != null)
//            msHash = mHashOpe.StringToHash(msToHash);
        switch (miItePos) {
            case 0:
                msHash = Base64.encodeToString(msToHash.getBytes(),Base64.DEFAULT);
                break;
            case 1:
                //pwd
                newP = getPwdWithLength(pwd,8);
                try {
                    msHash = DES.encryptDES(msToHash,newP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    msHash = AES.encrypt(pwd, msToHash);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                try {
                    byte[] salt = PBE.initSalt();
                    msHash = Base64.encodeToString(PBE.encrypt(msToHash.getBytes(), pwd, salt),Base64.DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
        Log.d("test","newP:" + newP);
        handler.sendEmptyMessage(0);
    }


    /**
     * 获取指定倍数的密钥
     * @param pwd
     * @param length
     * @return
     */
    private String getPwdWithLength(String pwd,int length) {
        StringBuffer stringBuffer = new StringBuffer();
        if (TextUtils.isEmpty(pwd)) {
            for (int i=0;i<length;i++) {
                stringBuffer.append("#");
            }
        } else {
            stringBuffer = new StringBuffer(pwd);
            int yu = pwd.length() % length;
            if (yu!=0) {
                for (int i=0;i<length-yu;i++) {
                    stringBuffer.append("#");
                }
            }
        }
        return stringBuffer.toString();
    }


    // This method is called when the computation is over
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // Hide the progress dialog
            if (mProgressDialog != null)
                mProgressDialog.dismiss();

            Resources res = getPluginContext().getResources();
            String sTextTitle = String.format(res.getString(R.string.Text),
                    msToHash);
            String sTextHashTitle = "";
            if (!msHash.equals("")) {
                if (mCheckBox != null) {
                    if (mCheckBox.isChecked()) {
                        msHash = msHash.toUpperCase();
                    } else {
                        msHash = msHash.toLowerCase();
                    }
                }
                String Function = "";
                if (miItePos >= 0)
                    Function = mFunctions[miItePos];
                sTextHashTitle = String.format(res.getString(R.string.Hash),
                        Function, msHash);
                // Show the copy button
                if (mCopyButton != null)
                    mCopyButton.setVisibility(View.VISIBLE);
            } else {
                sTextHashTitle = String.format(
                        res.getString(R.string.unable_to_calculate), msToHash);
                // Hide the copy button
                if (mCopyButton != null)
                    mCopyButton.setVisibility(View.INVISIBLE);
            }

            if (mResultTV != null)
                mResultTV.setText(sTextTitle + sTextHashTitle);
        }
    };
}
