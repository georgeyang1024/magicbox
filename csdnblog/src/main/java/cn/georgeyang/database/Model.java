package cn.georgeyang.database;

import cn.georgeyang.csdnblog.bean.ParcelableEntity;

/**
 *
 * Created by george.yang on 2015/9/16.
 */
public abstract class Model extends ParcelableEntity {
    public int _id;
    public long _addTime;
    public long _updateTime;

//    private static final Mdb[] dbCache = new Mdb[1];
//
//    protected static void init(Context context) {
//        if (dbCache[0]==null) {
//            dbCache[0] = new Mdb(context);
//        }
//    }
//
//    protected static Mdb getMdB() {
//        return dbCache[0];
//    }

    public Model (){
//        this._id = dbCache[0].getLastId(this.getClass()) + 1;
    }

    public boolean delete() {
        return Mdb.getInstance().delete(this);
    }

    public boolean save() {
        return Mdb.getInstance().insertOrUpdate(this);
    }

}

