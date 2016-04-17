package cn.georgeyang.seewifipwd.util;


import com.stericson.RootTools.RootTools;

import java.util.ArrayList;
import java.util.List;

import cn.georgeyang.seewifipwd.entity.NetWork;

/**
 * Created by user on 2014/12/10.
 */
public class WifiUtil {
    private static boolean isStarted;
    private static ArrayList<NetWork> list = null;
    private static NetWork temp;
    private static StringBuffer header;

    public static void reset () {
        list = new ArrayList<NetWork>();
//        list = (ArrayList<NetWork>)Collections.synchronizedList(list);
        header = new StringBuffer();
    }

    public synchronized static void append(String line) {
        if (line==null) {return;}
        //Log.w("test","line:" + line);

        if (line.replace(" ","").equals("network={")) {
            isStarted  = true;
            temp = new NetWork();
        } else if (line.replace(" ","").equals("}")) {
            isStarted = false;
            list.add(temp);
        } else if (isStarted) {
            String[] kv = line.trim().split("=");
            if (kv.length>1) {
                String key = kv[0];
                String value = kv[1];

                if (value!=null) {
                    value = value.trim();
                    if (value.length()>2) {
                        if (value.charAt(0)=='"') {
                            value = value.substring(1,value.length()-1);
                        }
                        if (value.charAt(value.length()-1)=='"') {
                            value = value.substring(0,value.length()-2);
                        }
                    }
                }

                if (key.equals("ssid")) {
                    temp.setSsid(value);
                } else if (key.equals("key_mgmt")) {
                    temp.setKey_mgmt(value);
                } else if (key.equals("psk")) {
                    temp.setPsk(value);
                    temp.setShowpsk(getShowPsk(value));
                } else if (key.equals("priority")) {
                    temp.setPriority(value);
                } else if (key.equals("auth_alg")) {
                    temp.setAuth_alg(value);
                } else if (key.equals("eapol_flag")) {
                    temp.setEapol_flags(value);
                } else if (key.equals("group")) {
                    temp.setGroup(value);
                } else if (key.equals("pairwise")) {
                    temp.setPairwise(value);
                } else if (key.equals("proto")) {
                    temp.setProto(value);
                } else if (key.equals("private_key")) {
                    temp.setPrivate_key(value);
                } else {
                    //Log.i("test","error:"  + key + ">>" + value);
                }
            }
        } else if (!isStarted){
            header.append(line + "\n");
        }

    }

    public static String getHeader () {
        return header.substring(0,header.length()-2);
    }

    public static ArrayList<NetWork> getWifiList () {
        return list;
    }

    public static String build (String header,ArrayList<NetWork> list) {
        StringBuffer sb = new StringBuffer(header);
        String temp = sb.toString();
        temp.replace("\n\n","\n");
        sb = new StringBuffer(temp);

        for(NetWork nw : list) {
            sb.append("\nnetwork={");
            if (nw.getSsid()!=null)
                sb.append("\n\tssid=" + nw.getSsid());
            if (nw.getPsk()!=null)
                sb.append("\n\tpsk=" + nw.getPsk());
            if (nw.getProto()!=null)
                sb.append("\n\tproto=" + nw.getProto());
            if (nw.getPrivate_key()!=null)
                sb.append("\n\tprivate_key=" + nw.getPrivate_key());
            if (nw.getAuth_alg()!=null)
                sb.append("\n\tauth_alg=" + nw.getAuth_alg());
            if (nw.getEapol_flags()!=null)
                sb.append("\n\teapol_flags=" + nw.getEapol_flags());
            if (nw.getGroup()!=null)
                sb.append("\n\tgroup=" + nw.getGroup());
            if (nw.getPairwise()!=null)
                sb.append("\n\tpairwise=" + nw.getPairwise());
            if (nw.getPriority()!=null)
                sb.append("\n\tprioity=" + nw.getPriority());
            if (nw.getKey_mgmt()!=null)
                sb.append("\n\tkey_mgmt=" + nw.getKey_mgmt());
            sb.append("\n}\n");
        }
        return sb.toString();
    }

    private static int successCount;
    private static int failCount;
    public static String add(String header,ArrayList<NetWork> oldlist,ArrayList<NetWork> newList) {
        successCount = 0;
        failCount = 0;
        if (oldlist == null || newList == null) {
            return "";
        }

        ArrayList<NetWork> slist = new ArrayList<NetWork>();//old没有的数据,临时
        for (NetWork nw1 : newList) {
            for (NetWork nw2 : oldlist) {
                if (nw1.getSsid().equals(nw2.getSsid())) {
                    failCount++;
                } else {
                    successCount++;
                    slist.add(nw1);
                }
            }
        }
        oldlist.addAll(slist);

        return build(header,oldlist);
    }

    public static int getFailCount() {
        return failCount;
    }

    public static int getSuccessCount() {
        return successCount;
    }


    public static String getShowPsk(String psk) {
        if (psk==null) {
            return "";
        }
        char head=0,end = 0;
        int len;
        if (psk.length() > 2) {
            head = psk.charAt(0);
            end = psk.charAt(psk.length()-1);

            len = psk.length()-2;
        } else{
            len = psk.length();
        }

        StringBuffer sb = new StringBuffer();
        if (head!=0)
            sb.append(head);

        for (int i=0;i<len;i++) {
            sb.append("*");
        }


        if (end!=0)
            sb.append(end);

        return sb.toString();
    }



    public static List<NetWork> listAllWifiDate () {
        try {
            List<String> strs = RootTools.sendShell("cat data/misc/wifi/wpa_supplicant.conf", 31000);
            if (strs==null) return null;

            header = new StringBuffer();
            List<NetWork> list = new ArrayList<NetWork>();
            boolean isStarted = false;
            for (String line :strs) {
                if (line==null) {continue;}
                //Log.w("test","line:" + line);

                if (line.replace(" ","").equals("network={")) {
                    isStarted  = true;
                    temp = new NetWork();
//                    temp.setLatitude(APPLocation.getLatitude(null));
//                    temp.setLontitude(APPLocation.getLontitude(null));
//                    temp.setAddr(APPLocation.getAddress(null));
                } else if (line.replace(" ","").equals("}")) {
                    isStarted = false;
                    list.add(temp);
                } else if (isStarted) {
                    String[] kv = line.trim().split("=");
                    if (kv.length>1) {
                        String key = kv[0];
                        String value = kv[1];

                        if (value!=null) {
                            value = value.trim();
                            if (value.length()>2) {
                                if (value.charAt(0)=='"') {
                                    value = value.substring(1,value.length()-1);
                                }
                                if (value.charAt(value.length()-1)=='"') {
                                    value = value.substring(0,value.length()-2);
                                }
                            }
                        }

                        if (key.equals("ssid")) {
                            temp.setSsid(value);
                        } else if (key.equals("key_mgmt")) {
                            temp.setKey_mgmt(value);
                        } else if (key.equals("psk")) {
                            temp.setPsk(value);
                            temp.setShowpsk(getShowPsk(value));
                        } else if (key.equals("priority")) {
                            temp.setPriority(value);
                        } else if (key.equals("auth_alg")) {
                            temp.setAuth_alg(value);
                        } else if (key.equals("eapol_flag")) {
                            temp.setEapol_flags(value);
                        } else if (key.equals("group")) {
                            temp.setGroup(value);
                        } else if (key.equals("pairwise")) {
                            temp.setPairwise(value);
                        } else if (key.equals("proto")) {
                            temp.setProto(value);
                        } else if (key.equals("private_key")) {
                            temp.setPrivate_key(value);
                        } else {
                            //Log.i("test","error:"  + key + ">>" + value);
                        }
                    }
                } else if (!isStarted){
                    header.append(line + "\n");
                }
            }

            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
