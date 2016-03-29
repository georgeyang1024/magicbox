package cn.georgeyang.lib;

import org.json.JSONObject;

/**
 * Created by george.yang on 2015/10/30.
 */
public class FragmentTagVo {
    public FragmentTagVo(){};
    public FragmentTagVo(int index, String animType) {
        this.index = index;
        this.animType = animType;
    }
    public String animType;
    public int index;


    public static FragmentTagVo build(String json) {
        FragmentTagVo tagVo = new FragmentTagVo();
        try {
            JSONObject jsonObject = new JSONObject(json);
            tagVo.index = jsonObject.getInt("index");
            tagVo.animType = jsonObject.getString("animType");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tagVo;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("index",index);
            jsonObject.put("animType",animType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
