package cn.georgeyang.csdnblog.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONObject;


/**
 * Created by george.yang on 2016-5-25.
 */
public class ParcelableEntity implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(JSONObject.toJSONString(this));
        parcel.writeString(this.getClass().getName());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ParcelableEntity createFromParcel(Parcel in) {
            String json = in.readString();
            String clazz = in.readString();
            Object object = null;
            try {
                object = JSONObject.parseObject(json,getClass().forName(clazz));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return (ParcelableEntity) object;
        }

        public ParcelableEntity[] newArray(int size) {
            return new ParcelableEntity[size];
        }
    };
}
