package cn.georgeyang.hashever;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.Editable;
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

import com.hobbyone.HashDroid.HashFunctionOperator;

import online.magicbox.lib.PluginFragment;

/**
 * Created by george.yang on 16/5/8.
 */
public class HashFragment extends PluginFragment implements Runnable {
    private EditText mEditText = null;
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
    private HashFunctionOperator mHashOpe = null;
    private ProgressDialog mProgressDialog = null;
    private int miItePos = -1;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getPluginLayoutInflater().inflate(R.layout.fragment_hash,null);
        mEditText = (EditText) view.findViewById(R.id.edittext);
        mClearButton = (Button) view.findViewById(R.id.ClearButton);
        mGenerateButton = (Button) view.findViewById(R.id.GenerateButton);
        mSpinner = (Spinner) view.findViewById(R.id.spinner);
        mResultTV = (TextView) view.findViewById(R.id.label_result);
        mCopyButton = (Button) view.findViewById(R.id.CopyButton);
        mClipboard = (ClipboardManager) getActivity().getSystemService("clipboard");
        mFunctions = getPluginContext().getResources().getStringArray(R.array.Algo_Array);
        mCheckBox = (CheckBox) view.findViewById(R.id.UpperCaseCB);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getPluginContext(), R.array.Algo_Array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(5); // MD5 by default
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

        Log.i("test","add view:" + view);

        return view;
    }

    private void ComputeAndDisplayHash() {
        if (mHashOpe == null)
            mHashOpe = new HashFunctionOperator();
        String sAlgo = "";
        if (miItePos == 0)
            sAlgo = "Adler-32";
        else if (miItePos == 1)
            sAlgo = "CRC-32";
        else if (miItePos == 2)
            sAlgo = "haval";
        else if (miItePos == 3)
            sAlgo = "md2";
        else if (miItePos == 4)
            sAlgo = "md4";
        else if (miItePos == 5)
            sAlgo = "md5";
        else if (miItePos == 6)
            sAlgo = "ripemd-128";
        else if (miItePos == 7)
            sAlgo = "ripemd-160";
        else if (miItePos == 8)
            sAlgo = "sha-1";
        else if (miItePos == 9)
            sAlgo = "sha-256";
        else if (miItePos == 10)
            sAlgo = "sha-384";
        else if (miItePos == 11)
            sAlgo = "sha-512";
        else if (miItePos == 12)
            sAlgo = "tiger";
        else if (miItePos == 13)
            sAlgo = "whirlpool";
        mHashOpe.SetAlgorithm(sAlgo);

        String sCalculating = getPluginContext().getString(R.string.Calculating);
        mProgressDialog = ProgressDialog.show(getActivity(), "",
                sCalculating, true);

        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    // Call when the thread is started
    public void run() {
        msHash = "";
        if (mHashOpe != null)
            msHash = mHashOpe.StringToHash(msToHash);
        handler.sendEmptyMessage(0);
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
