/* FileBrowser.java -- 
   Copyright (C) 2010 Christophe Bouyer (Hobby One)

This file is part of Hash Droid.

Hash Droid is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Hash Droid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Hash Droid. If not, see <http://www.gnu.org/licenses/>.
 */

package cn.georgeyang.hashever;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hobbyone.HashDroid.IconifiedText;
import com.hobbyone.HashDroid.IconifiedTextListAdapter;

import online.magicbox.lib.Slice;

public class FileBrowser extends Slice implements AdapterView.OnItemClickListener {

	private List<IconifiedText> directoryEntries = new ArrayList<IconifiedText>();
	private File currentDirectory = new File("/");
	private String msPathSelectedFile = "";
	public static final String PATH_FILE_OUT_ID = "PATH_FILE_OUT";
	public static final String PATH_FILE_IN_ID = "PATH_FILE_IN";

    public FileBrowser(Context base, Object holder) {
        super(base, holder);
    }
    private ListView listView;

    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
        setContentView(R.layout.slice_fileselect);
        listView = (ListView) findViewById(R.id.listView);

		boolean bBrowseToRoot = true;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			msPathSelectedFile = extras.getString(PATH_FILE_IN_ID);
			if (!msPathSelectedFile.equals("")) {
				File CurrentFile = new File(msPathSelectedFile);
				if (CurrentFile != null && CurrentFile.exists()) {
					if (CurrentFile.getParent() != null) {
						this.browseTo(CurrentFile.getParentFile());
						bBrowseToRoot = false;
					}
				}
			}
		}
		if (bBrowseToRoot == true)
			browseToRoot();


        listView.setSelection(0);
        listView.setOnItemClickListener(this);
	}

	/**
	 * This function browses to the root-directory of the file-system.
	 */
	private void browseToRoot() {
		browseTo(new File("/"));
	}

	/**
	 * This function browses up one level according to the field:
	 * currentDirectory
	 */
	private void upOneLevel() {
		if (this.currentDirectory.getParent() != null)
			this.browseTo(this.currentDirectory.getParentFile());
	}

	private void browseTo(final File aDirectory) {
		if (aDirectory.canRead()) {
			// On relative we display the full path in the title.
//			this.setTitle(aDirectory.getAbsolutePath());
			if (aDirectory.isDirectory()) {
				this.currentDirectory = aDirectory;
				fill(aDirectory.listFiles());
			} else {
				OnClickListener okButtonListener = new OnClickListener() {
					// @Override
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// Lets start an intent to View the file, that was
						// clicked...
						FileBrowser.this.openFile(aDirectory);
					}
				};
				OnClickListener cancelButtonListener = new OnClickListener() {
					// @Override
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// Do nothing ^^
					}
				};
				Resources res = getResources();
				String sTitle = String.format(
						res.getString(R.string.selectedfile_confirmation),
						aDirectory.getName());

				// Show an Alert with the ButtonListeners we created
				new AlertDialog.Builder(this)
						.setIcon(R.drawable.file)
						.setTitle(sTitle)
						.setPositiveButton(getString(R.string.Yes_but),
								okButtonListener)
						.setNegativeButton(getString(R.string.No_but),
								cancelButtonListener).show();
			}
		} else // The folder or the file cannot be read : display a message
		{
			OnClickListener ButtonListener = new OnClickListener() {
				// @Override
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// Do nothing ^^
				}
			};
			Resources res = getResources();
			String sTitle = String.format(
					res.getString(R.string.selectedfile_error),
					aDirectory.getName());
			// Show an Alert with the ButtonListener we created
			new AlertDialog.Builder(this)
					.setIcon(R.drawable.error)
					.setTitle(sTitle)
					.setPositiveButton(getString(R.string.Ok_but),
							ButtonListener).show();
		}
	}

	private void openFile(File aFile) {
		Intent i = new Intent();
		msPathSelectedFile = aFile.getAbsolutePath();
		i.putExtra(PATH_FILE_OUT_ID, msPathSelectedFile);
		setResult(RESULT_OK, i);
		finish();
	}

	private void fill(File[] files) {
		this.directoryEntries.clear();

		// and the ".." == 'Up one level'
		if (this.currentDirectory.getParent() != null)
			this.directoryEntries.add(new IconifiedText(
					getString(R.string.up_one_level), getResources()
							.getDrawable(R.drawable.uponelevel), 0));

		Drawable currentIcon = null;
		for (File currentFile : files) {
			int iType = 0;
			if (currentFile.isDirectory()) {
				currentIcon = getResources().getDrawable(R.drawable.folder);
			} else {
				iType = 1;
				currentIcon = getResources().getDrawable(R.drawable.file);
			}

			/*
			 * We have to cut the current-path at the beginning
			 */
			int currentPathStringLength = this.currentDirectory
					.getAbsolutePath().length();
			String sFileName = currentFile.getAbsolutePath().substring(
					currentPathStringLength);
			sFileName = sFileName.replaceAll("/", "");
			this.directoryEntries.add(new IconifiedText(sFileName, currentIcon,
					iType));
		}
		Collections.sort(this.directoryEntries);

		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
		itla.setListItems(this.directoryEntries);
        listView.setAdapter(itla);
//		this.setListAdapter(itla);
	}

//	@Override
//	protected void onListItemClick(ListView l, View v, int position, long id) {
////		super.onListItemClick(l, v, position, id);
//
//	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selectedFileString = this.directoryEntries.get(position)
                .getText();
        if (selectedFileString.equals(getString(R.string.up_one_level))) {
            this.upOneLevel();
        } else {
            File clickedFile = new File(this.currentDirectory.getAbsolutePath()
                    + "/" + this.directoryEntries.get(position).getText());
            if (clickedFile != null)
                this.browseTo(clickedFile);
        }
    }
}