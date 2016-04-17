package cn.georgeyang.seewifipwd.entity;


import android.text.TextUtils;

import java.io.Serializable;

import cn.georgeyang.seewifipwd.util.WifiUtil;

/**
 * Created by user on 2014/12/10.
 */
public class NetWork implements Serializable,Comparable<NetWork> {

    private boolean ischooice;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIschooice() {
        return ischooice;
    }

    public void setIschooice(boolean ischooice) {
        this.ischooice = ischooice;
    }

    private int index;
    private String ssid;
    private String remark;//备注
    private String psk;
    private String showpsk;//显示的psk，隐藏一些字符串
    private String proto;
    private String key_mgmt;//验证类型
    private String group;//组
    private String auth_alg;
    private String private_key;
    private String eapol_flags;
    private String pairwise;//ccmp、
    private String priority;

    private double latitude;
    private double lontitude;
    private String addr = "未知位置";
    private int maxLevel;//信息最好时的信号强度
    private int level;//地址的信号强度
    private String mac;//bssid
    private String bssid;
    private String capabilities;//加密方式
    private int frequency;//频率

    private int leneveUdataTime;//wifi强度更新次数

    private int networkId;//手机连接id
    private int status;

    //连接中
    private boolean isLinking;//连接中
    private int ip;
    private int linkSpeed;
    private int rssi;


    public int getIp() {
        return ip;
    }

    public void setIp(int ip) {
        this.ip = ip;
    }

    public int getLinkSpeed() {
        return linkSpeed;
    }

    public void setLinkSpeed(int linkSpeed) {
        this.linkSpeed = linkSpeed;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public boolean isLinking() {
        return isLinking;
    }

    public void setLinking(boolean isLinking) {
        this.isLinking = isLinking;
    }

    public int getLeneveUdataTime() {
        return leneveUdataTime;
    }

    public void setLeneveUdataTime(int leneveUdataTime) {
        this.leneveUdataTime = leneveUdataTime;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        if(TextUtils.isEmpty(addr) || addr.equals("未知位置")) {
            return;
        }
        this.addr = addr;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        if (latitude==0) {
            return;
        }
        this.latitude = latitude;
    }

    public double getLontitude() {
        return lontitude;
    }

    public void setLontitude(double lontitude) {
        if (lontitude==0) {
            return;
        }
        this.lontitude = lontitude;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getShowpsk() {
        if (showpsk==null) {
            showpsk = WifiUtil.getShowPsk(psk);
        }
        return showpsk;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setShowpsk(String showpsk) {
        this.showpsk = showpsk;
    }

    public String getProto() {
        return proto;
    }

    public void setProto(String proto) {
        this.proto = proto;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getAuth_alg() {
        return auth_alg;
    }

    public void setAuth_alg(String auth_alg) {
        this.auth_alg = auth_alg;
    }

    public String getPrivate_key() {
        return private_key;
    }

    public void setPrivate_key(String private_key) {
        this.private_key = private_key;
    }

    public String getEapol_flags() {
        return eapol_flags;
    }

    public void setEapol_flags(String eapol_flags) {
        this.eapol_flags = eapol_flags;
    }

    public String getPairwise() {
        return pairwise;
    }

    public void setPairwise(String pairwise) {
        this.pairwise = pairwise;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPsk() {
        return psk;
    }

    public void setPsk(String psk) {
        this.psk = psk;
    }

    public String getKey_mgmt() {
        return key_mgmt;
    }

    public void setKey_mgmt(String key_mgmt) {
        this.key_mgmt = key_mgmt;
    }

    @Override
    public int compareTo(NetWork netWork) {
        //当前与旧的对比，大于0，当前的大,排后面
//        return id-netWork.getId();

        if (level<0 && netWork.getLevel()<0) {
            return level>netWork.getLevel()?-1:1;
        } else if (level<0 && netWork.getLevel()==0) {
            return -1;
        } else if (level==0 && netWork.getLevel()<0) {
            return 1;
        } else if (level==0 && netWork.getLevel()==0) {
            return ssid.compareTo(netWork.getSsid());
        }
        return 0;

//        if (level!=0 && netWork.getLevel()!=0) {
//            return 1;
//        } else if (level==netWork.getLevel()) {
//            return 0;
//        } else {
//            return -1;
//        }

//       if (level != 0 && netWork.getLevel() != 0) {
//           return -level-netWork.getLevel();
//       } else if (level == 0 && netWork.getLevel() !=0) {
//            return -netWork.getLevel();
//       }else if (level != 0 && netWork.getLevel() == 0) {
//            return -level;
//       }
//        return 0;
    }
}
