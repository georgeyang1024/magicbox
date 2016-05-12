/* IconifiedText.java -- 
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

import android.graphics.drawable.Drawable;

public class IconifiedText implements Comparable<IconifiedText> {

	private String mText = "";
	private Drawable mIcon;
	private int mType = 0; // 0 : directory, 1 : file

	public IconifiedText(String text, Drawable bullet, int type) {
		mIcon = bullet;
		mText = text;
		mType = type;
	}

	public String getText() {
		return mText;
	}

	public Drawable getIcon() {
		return mIcon;
	}

	public int getType() {
		return mType;
	}

	@Override
	public int compareTo(IconifiedText other) {
		int iOtherType = other.getType();
		if (this.mType != iOtherType) {
			if (this.mType < iOtherType)
				return -1;
			else
				return 1;
		} else {
			if (this.mText != null)
				return this.mText.compareToIgnoreCase(other.getText());
			else
				throw new IllegalArgumentException();
		}

	}
}
