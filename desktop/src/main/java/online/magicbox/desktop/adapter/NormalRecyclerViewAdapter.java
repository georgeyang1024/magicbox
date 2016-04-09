package online.magicbox.desktop.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.georgeyang.lib.UiThread;
import cn.georgeyang.util.HttpUtil;
import online.magicbox.desktop.R;
import online.magicbox.desktop.entity.AppInfoBean;


/**
 * Created by george.yang on 2016-3-30.
 */
public class NormalRecyclerViewAdapter extends RecyclerView.Adapter<NormalRecyclerViewAdapter.NormalTextViewHolder> {
    private List<Object> lineData = new ArrayList<>();
    private Context mContext;
    //please init in thread
    public NormalRecyclerViewAdapter(List<AppInfoBean> list) {
        lineData.clear();
        if (list!=null) {
            Map<Integer,List<AppInfoBean>> typeMap = new HashMap<>();
            for (AppInfoBean infoBean:list) {
                List<AppInfoBean> innerList = typeMap.get(infoBean.type);
                if (innerList==null) {
                    innerList = new ArrayList<>();
                    typeMap.put(infoBean.type,innerList);
                }
                innerList.add(infoBean);
            }

            for (Integer type:typeMap.keySet()) {
                lineData.add(type);
                List<AppInfoBean> innerList = typeMap.get(type);
                int lineCount = 0;
                List<AppInfoBean> lineList = null;
                for (int i=0;i<innerList.size();i++) {
                    AppInfoBean infoBean = innerList.get(i);
                    if (lineCount==0 || lineList==null) {
                        lineList = new ArrayList<>();
                    }

                    lineList.add(infoBean);
                    lineCount++;
                    if (lineCount==3) {
                        lineData.add(lineList);
                        lineCount=0;
                    } else if (i==innerList.size()-1) {
                        lineData.add(lineList);
                    }
                }
            }
        }
    }

    private LayoutInflater mLayoutInflater;
    @Override
    public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater==null) {
            mContext = parent.getContext();
            mLayoutInflater = LayoutInflater.from(mContext);
        }
        NormalTextViewHolder holder = null;
        if (viewType==0) {
            holder =  new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.item_listtitle, parent, false));// ID #0x7f04001d
        } else {
            holder = new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.item_appline, parent, false));// ID #0x7f04001d
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(NormalTextViewHolder holder, int position) {
        Object object = lineData.get(position);
        if (object instanceof Integer) {
            int type = (Integer) object;
            String name = type==1?"魔盒應用":type==2?"安裝應用":"系統應用";
            holder.tv_title.setText(name);
        } else {
            List<AppInfoBean> innerList = (List<AppInfoBean>) object;
            for (int i=0;i<3;i++) {
                if (i>=innerList.size()) {
                   holder.layout[i].setVisibility(View.INVISIBLE);
                } else {
                    final AppInfoBean infoBean = innerList.get(i);
                    if (infoBean.isInstall) {
                        holder.progressBars[i].setVisibility(View.GONE);
                        holder.img_download[i].setVisibility(View.GONE);
                    } else if (infoBean.downloading) {
                        holder.progressBars[i].setVisibility(View.VISIBLE);
                        holder.img_download[i].setVisibility(View.GONE);
                    } else {
                        holder.progressBars[i].setVisibility(View.GONE);
                        holder.img_download[i].setVisibility(View.VISIBLE);
                        holder.img_download[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AlertDialog.Builder(mContext).setTitle("提示").setMessage("确认要安装这个程序吗?").setNegativeButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        infoBean.downloading = true;
                                        notifyDataSetChanged();
                                        UiThread.init(mContext).setObject(infoBean).start(new UiThread.UIThreadEvent() {
                                            @Override
                                            public Object runInThread(String flag, Object obj, UiThread.Publisher publisher) {
                                                AppInfoBean infoBean = (AppInfoBean) obj;
                                                File downFile = new File(mContext.getFilesDir(),String.format("%s_%s.apk",new Object[]{infoBean.packageName,infoBean.version}));
                                                try {
                                                    HttpUtil.downLoadFile2(infoBean.downUrl,downFile);
                                                    infoBean.downloading = false;
                                                    infoBean.isInstall = true;
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    return null;
                                                }
                                                return infoBean;
                                            }

                                            @Override
                                            public void runInUi(String flag, Object obj, boolean ispublish, float progress) {
                                                if (obj!=null) {
                                                    notifyDataSetChanged();
                                                }
                                            }
                                        });
                                    }
                                }).setPositiveButton("取消",null).create().show();
                            }
                        });
                    }
                    holder.layout[i].setVisibility(View.VISIBLE);
                    holder.layout[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.getContext().startActivity(infoBean.intent);
                        }
                    });
                    holder.tv_name[i].setText(infoBean.name);
                    holder.img_icon[i].setImageDrawable(infoBean.icon);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return lineData == null ? 0 : lineData.size();
    }

    @Override
    public int getItemViewType(int position) {
        //0標題,1app列表
        return lineData.get(position) instanceof Integer ? 0:1;
    }

    public static class NormalTextViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView[] tv_name;
        ImageView[] img_icon,img_download;
        LinearLayout[] layout;
        ProgressBar[] progressBars;

        NormalTextViewHolder(View view) {
            super(view);
            tv_title = (TextView) view.findViewById(R.id.tv_title);

            tv_name = new TextView[3];
            tv_name[0] = (TextView) view.findViewById(R.id.tv_appName1);
            tv_name[1] = (TextView) view.findViewById(R.id.tv_appName2);
            tv_name[2] = (TextView) view.findViewById(R.id.tv_appName3);

            img_icon = new ImageView[3];
            img_icon[0] = (ImageView) view.findViewById(R.id.img_icon1);
            img_icon[1] = (ImageView) view.findViewById(R.id.img_icon2);
            img_icon[2] = (ImageView) view.findViewById(R.id.img_icon3);

            img_download = new ImageView[3];
            img_download[0] = (ImageView) view.findViewById(R.id.img_download1);
            img_download[1] = (ImageView) view.findViewById(R.id.img_download2);
            img_download[2] = (ImageView) view.findViewById(R.id.img_download3);

            progressBars = new ProgressBar[3];
            progressBars[0] = (ProgressBar) view.findViewById(R.id.progressBar1);
            progressBars[1] = (ProgressBar) view.findViewById(R.id.progressBar2);
            progressBars[2] = (ProgressBar) view.findViewById(R.id.progressBar3);

            layout = new LinearLayout[3];
            layout[0] = (LinearLayout) view.findViewById(R.id.layout1);
            layout[1] = (LinearLayout) view.findViewById(R.id.layout2);
            layout[2] = (LinearLayout) view.findViewById(R.id.layout3);
        }
    }
}
