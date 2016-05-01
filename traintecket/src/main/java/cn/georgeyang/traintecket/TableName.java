package cn.georgeyang.traintecket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class TableName {
	// 解析火车票数据
	public static List<Map<String, String>> parseJsonTiket(String vehicle) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		String[] str = new String[2];

		try {

			JSONObject object = new JSONObject(vehicle);
			JSONObject object1 = object.getJSONObject("data");

			String date = object1.getString("searchDate");
			if(date!=null)
			{
				str = date.split("\\&");
			}

			JSONArray array = object1.getJSONArray("datas");

			for (int i = 0; i < array.length(); i++)
			{
				map = new HashMap<String, String>();
				JSONObject obj = array.getJSONObject(i);
				map.put("车次", "车次: "+obj.opt("station_train_code"));
				map.put("起始站","起始站: "+obj.opt("start_station_name"));
				if(obj.opt("end_station_name").toString().equals(obj.opt("to_station_name").toString()))
				{
					map.put("终点站", "终点站: "+obj.opt("end_station_name"));
				}
				else
				{
					map.put("终点站", "经过: "+obj.opt("to_station_name"));
				}
				map.put("出发时间", "出发时间: "+obj.opt("start_time"));
				map.put("到达时间", "到达时间: "+obj.opt("arrive_time"));
				map.put("历时", "历时: "+obj.opt("lishi"));
				map.put("商务座", "商务座: "+obj.opt("swz_num"));
				map.put("特等座", "特等座: "+obj.opt("tz_num"));
				map.put("一等座", "一等座: "+obj.opt("zy_num"));
				map.put("二等座", "二等座: "+obj.opt("ze_num"));
				map.put("高级软卧", "高级软卧: "+obj.opt("gr_num"));
				map.put("无座", "无座: "+obj.opt("wz_num"));
				map.put("软卧", "软卧: "+obj.opt("rw_num"));
				map.put("硬卧", "硬卧: "+obj.opt("yw_num"));
				map.put("硬座", "硬座: "+obj.opt("yz_num"));
				map.put("软座", "软座: "+obj.opt("rz_num"));
				map.put("日期", "日期: "+str[0]);
				map.put("备注", "备注: "+obj.opt("note"));
				map.put("info", "(以上信息仅供参考,请以车站为准)");
				list.add(map);
			}
			return list;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

	}

}
