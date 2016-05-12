/* IconifiedTextView.java -- 
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

package com.hobbyone.HashDroid;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IconifiedTextView extends LinearLayout {

	private TextView mText;
	private ImageView mIcon;

	public IconifiedTextView(Context context, IconifiedText aIconifiedText) {
		super(context);

		/*
		 * First Icon and the Text to the right (horizontal), not above and
		 * below (vertical)
		 */
		this.setOrientation(HORIZONTAL);

		mIcon = new ImageView(context);
		mIcon.setImageDrawable(aIconifiedText.getIcon());
		// left, top, right, bottom
		mIcon.setPadding(0, 2, 5, 0); // 5px to the right

		/*
		 * At first, add the Icon to ourself (! we are extending LinearLayout)
		 */
		addView(mIcon, new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		mText = new TextView(context);
		mText.setText(aIconifiedText.getText());
		mText.setTextSize(20);
		/* Now the text (after the icon) */
		addView(mText, new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	public void setText(String words) {
		mText.setText(words);
	}

	public void setIcon(Drawable bullet) {
		mIcon.setImageDrawable(bullet);
	}
}