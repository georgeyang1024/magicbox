package cn.georgeyang.hashever;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hobbyone.HashDroid.HashFunctionOperator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.Date;

import online.magicbox.lib.PluginActivity;
import online.magicbox.lib.PluginFragment;
import online.magicbox.lib.Slice;

/**
 * Created by george.yang on 16/5/8.
 */
public class FileFragment extends PluginFragment implements Runnable {
    View view;
    private static final int RESULT_OK = Activity.RESULT_OK;

    private Button mSelectFileButton = null;
    private CheckBox mCheckBox = null;
    private Button mGenerateButton = null;
    private Button mCopyButton = null;
    private Button mCompCBButton = null;
    private Spinner mSpinner = null;
    private TextView mResultTV = null;
    private String msFilePath = "";
    private String msFileSize = "";
    private String msFileDateModified = "";
    private String msFileTimeModified = "";
    private String msHash = "";
    private String[] mFunctions;
    private ClipboardManager mClipboard = null;
    private final int SELECT_FILE_REQUEST = 0;
    private HashFunctionOperator mHashOpe = null;
    private ProgressDialog mProgressDialog = null;
    private File mFileToHash = null;
    private FileInputStream mFileIS = null;
    private int miItePos = -1;
    private static final String TAG = "HashDroid";

    private View findViewById(int id) {
        return view.findViewById(id);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getPluginLayoutInflater().inflate(R.layout.fragment_file,null);

        mSelectFileButton = (Button) findViewById(R.id.SelectFileButton);
        mGenerateButton = (Button) findViewById(R.id.GenerateButton);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mResultTV = (TextView) findViewById(R.id.label_result);
        mCopyButton = (Button) findViewById(R.id.CopyButton);
        mCompCBButton = (Button) findViewById(R.id.CompareCBButon);
        mClipboard = (ClipboardManager) getPluginContext().getSystemService(Context.CLIPBOARD_SERVICE);
        mFunctions = getPluginContext().getResources().getStringArray(R.array.Algo_Array);
        mCheckBox = (CheckBox) findViewById(R.id.UpperCaseCB);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getPluginContext(), R.array.Algo_Array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(5); // MD5 by default

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {
                // your code here
                // Hide the copy & compare buttons
                if (!msHash.equals(""))
                    mCopyButton.setVisibility(View.INVISIBLE);
                mCompCBButton.setVisibility(View.INVISIBLE);
                // Clean the result text view
                if (mResultTV != null)
                    mResultTV.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        mSelectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on clicks
               Intent intent = PluginActivity.buildIntent(getPluginContext(),FileBrowser.class);
                intent.putExtra(FileBrowser.PATH_FILE_IN_ID, msFilePath);
                startActivityForResult(intent, SELECT_FILE_REQUEST);
            }
        });

        mGenerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on clicks
                if (!msFilePath.equals("")) {
                    miItePos = mSpinner.getSelectedItemPosition();
                    mFileToHash = new File(msFilePath);
                    if (mFileToHash != null && mFileToHash.exists())
                        ComputeAndDisplayHash();
                    else {
                        String sWrongFile = getPluginContext().getString(R.string.wrong_file);
                        if (mResultTV != null)
                            mResultTV.setText(sWrongFile);
                        if (mCopyButton != null)
                            mCopyButton.setVisibility(View.INVISIBLE);
                        if (mCompCBButton != null)
                            mCompCBButton.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        mCopyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on clicks
                if (mClipboard != null) {
                    mClipboard.setText(msHash);
                    String sCopied = getPluginContext().getString(R.string.copied);
                    Toast.makeText(getPluginContext(), sCopied,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCompCBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on clicks
                try {
                    if (mClipboard != null) {
                        String sCBContents = mClipboard.getText().toString();
                        Log.i(TAG, "Hash: " + msHash);
                        Log.i(TAG, "Clip: " + sCBContents);
                        if (sCBContents.equalsIgnoreCase(msHash)) {
                            Toast.makeText(getPluginContext(), getPluginContext().getString(R.string.IdenticalHashes),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getPluginContext(), getPluginContext().getString(R.string.DifferentHashes),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    // Auto-generated catch block
                    e.printStackTrace();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_FILE_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Bundle extras = data.getExtras();
                    msFilePath = extras.getString(FileBrowser.PATH_FILE_OUT_ID);
                    File FileToHash = new File(msFilePath);
                    if (FileToHash != null && mSelectFileButton != null)
                        mSelectFileButton.setText(FileToHash.getAbsolutePath());
                }
            }
        }
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
            sAlgo = "Haval";
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

        if (mFileToHash != null) {
            try {
                mFileIS = new FileInputStream(mFileToHash);
            } catch (FileNotFoundException e) {
            }
            String sCalculating = getPluginContext().getString(R.string.Calculating);
            mProgressDialog = ProgressDialog.show(getPluginContext(), "",
                    sCalculating, true);

            Thread thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    // Call when the thread is started
    public void run() {
        msHash = "";
        msFileSize = "";
        msFileDateModified = "";
        msFileTimeModified = "";
        if (mHashOpe != null && mFileIS != null)
            msHash = mHashOpe.FileToHash(mFileIS);
        if (mFileToHash != null) {
            // Get size of file
            long lSize = mFileToHash.length();
            msFileSize = FileSizeDisplay(lSize, false);
            // Get date modified
            Date date = new Date(mFileToHash.lastModified());
            DateFormat dateFormat = android.text.format.DateFormat
                    .getDateFormat(getPluginContext());
            DateFormat timeFormat = android.text.format.DateFormat
                    .getTimeFormat(getPluginContext());
            msFileDateModified = dateFormat.format(date).toString();
            msFileTimeModified = timeFormat.format(date).toString();
        }
        handler.sendEmptyMessage(0);
    }

    private String FileSizeDisplay(long lbytes, boolean bSI) {
        int unit = bSI ? 1000 : 1024;
        if (lbytes < unit)
            return lbytes + " B";
        int exp = (int) (Math.log(lbytes) / Math.log(unit));
        String pre = (bSI ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
                + (bSI ? "" : "i");
        return String.format("%.2f %sB", lbytes / Math.pow(unit, exp), pre);
    }

    // This method is called when the computation is over
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // Hide the progress dialog
            if (mProgressDialog != null)
                mProgressDialog.dismiss();

            if (mFileToHash != null) {
                Resources res = getPluginContext().getResources();
                String sFileNameTitle = String
                        .format(res.getString(R.string.FileName),
                                mFileToHash.getName());
                String sFileSizeTitle = String.format(
                        res.getString(R.string.FileSize), msFileSize);
                String sFileDateModifiedTitle = String.format(
                        res.getString(R.string.FileDateModified),
                        msFileDateModified, msFileTimeModified);
                String sFileHashTitle = "";
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
                    sFileHashTitle = String.format(
                            res.getString(R.string.Hash), Function, msHash);
                    // Show the copy button
                    if (mCopyButton != null)
                        mCopyButton.setVisibility(View.VISIBLE);
                    // Show the compare clipboard button
                    if (mCompCBButton != null)
                        mCompCBButton.setVisibility(View.VISIBLE);
                } else {
                    sFileHashTitle = String.format(
                            res.getString(R.string.unable_to_calculate),
                            mFileToHash.getName());
                    // Hide the copy button
                    if (mCopyButton != null)
                        mCopyButton.setVisibility(View.INVISIBLE);
                    // Hide the compare clipboard button
                    if (mCompCBButton != null)
                        mCompCBButton.setVisibility(View.INVISIBLE);
                }

                if (mResultTV != null)
                    mResultTV.setText(sFileNameTitle + sFileSizeTitle
                            + sFileDateModifiedTitle + sFileHashTitle);
            }
        }
    };
}
