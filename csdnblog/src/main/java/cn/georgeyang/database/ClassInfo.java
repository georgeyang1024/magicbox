package cn.georgeyang.database;

/**
 * Created by george.yang on 15/11/27.
 */

import android.text.TextUtils;

import java.lang.reflect.Field;

import cn.georgeyang.util.ReflectUtil;


/**
 * 表和class的信息
 */
public class ClassInfo {
    public Class _class;
    public boolean tableIsExist;
    public String tableName;//simplename
    public String createSql;
    public Field id;//必须是 _id，该字段必须存在
    public Field[] fields;
    public boolean isActivDB;

    public ClassInfo(Class cls) {
        this._class = cls;
        this.tableName = _class.getSimpleName();
//        this.tableTempName = _class.getSimpleName() + "_temp";

        String activeTableName = ReflectUtil.getValueByClassAnntation(cls, "com.activeandroid.annotation.Table", "name");
        if (!TextUtils.isEmpty(activeTableName)) {
            isActivDB = true;
            tableName = activeTableName;
        }

        fields = ReflectUtil.getAllNoStaticFiedFromClassAndSuper(cls);

        StringBuffer sb = new StringBuffer();
        boolean isIdAdded = false;
        if (isActivDB) {
            String idFiledName = ReflectUtil.getValueByClassAnntation(cls, "com.activeandroid.annotation.Table", "id");
            Field idField = ReflectUtil.findFieldWithAnntationAndValue(fields, "com.activeandroid.annotation.Column","name",idFiledName);
            if (idField==null) {
                throw new InstantiationError("the id:" + idFiledName + " have not Field in class");
            } else {
                sb.append(idFiledName + " " + Mdb.getFiedType(idField) + " PRIMARY KEY,");//Autoincrement
                this.id = idField;
                isIdAdded = true;
            }
        }

        for (Field field : fields) {//枚举字段
            field.setAccessible(true);//允许访问私有字段
            String dbfield = Mdb.getDBFiedName(isActivDB, field);// 字段名称 / field.getName();//字段名
            String dbtype = Mdb.getFiedType(field);
            if (dbfield.equals("_id")) {
                if (isIdAdded) {
                    continue;
                }
                isIdAdded = true;
                this.id = field;
                if (field.getType().getSimpleName().equals("int")) {
                    sb.append("_id " + dbtype + " PRIMARY KEY,");//Autoincrement
                } else {
                    sb.append("_id " + dbtype + ",");
                }
            } else {
                sb.append(dbfield + " " + dbtype + ",");
            }
        }
        String lastSql = sb.toString();
        lastSql = sb.substring(0, lastSql.length() - 1) + ")";

        this.createSql = "create table if not exists " + tableName + " (" + lastSql;
    }
}
