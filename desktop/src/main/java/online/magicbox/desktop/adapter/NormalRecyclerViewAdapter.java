package online.magicbox.desktop.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.georgeyang.database.Mdb;
import cn.georgeyang.lib.UiThread;
import cn.georgeyang.util.HttpUtil;
import cn.georgeyang.util.ImageLoder;
import cn.georgeyang.util.ShortCutUtil;
import online.magicbox.desktop.R;
import online.magicbox.desktop.entity.AppInfoBean;
import online.magicbox.desktop.entity.PluginItemBean;


/**
 * Created by george.yang on 2016-3-30.
 */
public class NormalRecyclerViewAdapter extends RecyclerView.Adapter<NormalRecyclerViewAdapter.NormalTextViewHolder> {
    private List<Object> lineData = new ArrayList<>();
    private Context mContext;
    private Activity activity;
    private int iconCount = 3;

    public NormalRecyclerViewAdapter (int lineCounts) {
        iconCount = lineCounts;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setDataInThread (List<AppInfoBean> list) {
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
                    if (lineCount==iconCount) {
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
            holder =  new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.item_listtitle, parent, false),iconCount);// ID #0x7f04001d
        } else {
            if (iconCount==3) {
                holder = new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.item_appline, parent, false),iconCount);// ID #0x7f04001d
            } else if (iconCount==4) {
                holder = new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.item_appline4, parent, false),iconCount);// ID #0x7f04001d
            } else if (iconCount==5) {
                holder = new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.item_appline5, parent, false),iconCount);// ID #0x7f04001d
            }
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(NormalTextViewHolder holder, int position) {
        Object object = lineData.get(position);
        if (object instanceof Integer) {
            int type = (Integer) object;
            holder.tv_title.setText(type==1?R.string.appTypePlugin:type==2?R.string.appTypeDownload:R.string.appTypeSystem);
        } else {
            List<AppInfoBean> innerList = (List<AppInfoBean>) object;
            for (int i=0;i<iconCount;i++) {
                if (i>=innerList.size()) {
                   holder.layout[i].setVisibility(View.INVISIBLE);
                } else {
                    final AppInfoBean infoBean = innerList.get(i);
                    holder.tv_name[i].setText(infoBean.name);
                    if (infoBean.icon!=null) {
                        holder.img_icon[i].setImageDrawable(infoBean.icon);
                    } else if (!TextUtils.isEmpty(infoBean.imageUrl)){
                        ImageLoder.loadImage(holder.img_icon[i],infoBean.imageUrl,300,300,R.mipmap.ic_launcher);
                    } else {
                        holder.img_icon[i].setImageResource(R.mipmap.ic_launcher);
                    }

                    if (infoBean.isInstall && infoBean.lastVersionCode!=infoBean.installVersionCode) {
                        holder.img_hasUpdate[i].setVisibility(View.VISIBLE);
                    } else {
                        holder.img_hasUpdate[i].setVisibility(View.GONE);
                    }

                    if (infoBean.downloading) {
                        holder.progressBars[i].setVisibility(View.VISIBLE);
                        holder.img_download[i].setVisibility(View.GONE);
                    } else if (infoBean.isInstall) {
                        holder.progressBars[i].setVisibility(View.GONE);
                        holder.img_download[i].setVisibility(View.GONE);
                    } else {
                        holder.img_download[i].setTag(infoBean);
                        holder.progressBars[i].setVisibility(View.GONE);
                        holder.img_download[i].setVisibility(View.VISIBLE);
                        holder.img_download[i].setOnClickListener(downLoadOnClickListener);
                    }
                    holder.layout[i].setVisibility(View.VISIBLE);
                    holder.layout[i].setTag(infoBean);
                    holder.layout[i].setOnClickListener(layoutOnClickListener);
                    holder.layout[i].setOnLongClickListener(longClickListener);

                }
            }
        }
    }

    private static final String SCHEME = "package";
    private View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            final AppInfoBean infoBean = (AppInfoBean) v.getTag();
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("选择操作");
            if (infoBean.type==AppInfoBean.Type_plugin) {
                builder.setItems(R.array.pluginAppMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                mContext.startActivity(infoBean.intent);
                                break;
                            case 1:
                                if (infoBean.installVersionCode!=infoBean.lastVersionCode) {
                                    String infoMsg = String.format("确认要更新这个程序吗(大小:%sM)?",new Object[]{infoBean.size+""});
                                    new AlertDialog.Builder(mContext).setTitle("提示").setMessage(infoMsg).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startDownload(infoBean);
                                        }
                                    }).setNegativeButton("取消",null).create().show();
                                } else {
                                    new AlertDialog.Builder(mContext).setTitle("提示").setMessage("无需更新，请留意图标右上角的小红点，若出现小红点即可更新!").setPositiveButton("确认", null).create().show();
                                }
                                break;
                            case 2:
                                new AlertDialog.Builder(mContext).setTitle("提示").setMessage("卸载后需重新下载才能使用,确定要卸载该应用?").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String name = infoBean.packageName + "_" + infoBean.installVersionCode;
                                        File apkFile = new File(mContext.getFilesDir().getAbsolutePath(),name + ".apk");
                                        apkFile.delete();
                                        File dexFile = new File(mContext.getCacheDir().getAbsolutePath(),name+".dex");
                                        dexFile.delete();
                                        infoBean.isInstall = false;
                                        infoBean.installVersionCode = -1;
                                        notifyDataSetChanged();
                                    }
                                }).setNegativeButton("取消",null).create().show();
                                break;
                            case 3:
                                ShortCutUtil.createShortCut(mContext,infoBean.name,infoBean.intent);
                                break;
                        }
                    }
                });
            } else {
                builder.setItems(R.array.appMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            switch (which) {
                                case 0:
                                    mContext.startActivity(infoBean.intent);
                                    break;
                                case 1:
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts(SCHEME, infoBean.packageName, null);
                                    intent.setData(uri);
                                    mContext.startActivity(intent);
                                    break;
                                case 2:
                                    Uri packageURI = Uri.parse("package:" + infoBean.packageName);
                                    Intent intent2 = new Intent(Intent.ACTION_DELETE,packageURI);
                                    mContext.startActivity(intent2);
                                    break;
                            }
                        } catch (Exception e) {

                        }
                    }
                });
            }
            builder.setNegativeButton("取消", null);
            builder.create().show();
            return false;
        }
    };

    private View.OnClickListener downLoadOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final AppInfoBean infoBean = (AppInfoBean) v.getTag();

            String infoMsg = String.format("确认要安装这个程序吗(大小:%sM)?",new Object[]{infoBean.size+""});
            new AlertDialog.Builder(mContext).setTitle("提示").setMessage(infoMsg).setNegativeButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startDownload(infoBean);
                }
            }).setPositiveButton("取消",null).create().show();
        }
    };

    private void startDownload(final AppInfoBean infoBean) {
        infoBean.downloading = true;
        notifyDataSetChanged();
        UiThread.init(mContext).setObject(infoBean).start(new UiThread.UIThreadEvent() {
            @Override
            public Object runInThread(String flag, Object obj, UiThread.Publisher publisher) {
                try {
                    AppInfoBean infoBean = (AppInfoBean) obj;
                    File downFile = new File(mContext.getFilesDir(),String.format("%s_%s.apk",new Object[]{infoBean.packageName,infoBean.lastVersionCode}));
                    HttpUtil.downLoadFile2(infoBean.downUrl,downFile);
                    infoBean.downloading = false;
                    infoBean.isInstall = true;
                    infoBean.installVersionCode = infoBean.lastVersionCode;

                    PluginItemBean dbBean = Mdb.getInstance().findOnebyWhereDesc(PluginItemBean.class,"_addTime",String.format("packageName='%s'",new Object[]{infoBean.packageName}));
                    dbBean.installVersionCode = infoBean.lastVersionCode;
                    dbBean.save();
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
                } else {
                    new AlertDialog.Builder(mContext).setTitle("提示").setMessage("下载失败!").setNegativeButton("确认", null).create().show();
                    infoBean.downloading = false;
                    notifyDataSetChanged();
                }
            }
        });
    }

    private View.OnClickListener layoutOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AppInfoBean appInfoBean = (AppInfoBean) v.getTag();
            v.getContext().startActivity(appInfoBean.intent);
        }
    };

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
        ImageView[] img_icon,img_download,img_hasUpdate;
        LinearLayout[] layout;
        ProgressBar[] progressBars;


        NormalTextViewHolder(View view,int size) {
            super(view);
            tv_title = (TextView) view.findViewById(R.id.tv_title);

            if (tv_title == null && view instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) view;
                tv_name = new TextView[size];
                img_icon = new ImageView[size];
                img_download = new ImageView[size];
                img_hasUpdate = new ImageView[size];
                progressBars = new ProgressBar[size];
                layout = new LinearLayout[size];
                for (int i=0;i<size;i++) {
                    layout[i] = (LinearLayout) linearLayout.getChildAt(i);
                    LinearLayout innerLayout = layout[i];

                    RelativeLayout relativeLayout = (RelativeLayout) innerLayout.getChildAt(0);
                    tv_name[i] = (TextView) innerLayout.getChildAt(1);

                    img_hasUpdate[i] = (ImageView) relativeLayout.getChildAt(0);
                    img_icon[i] = (ImageView) relativeLayout.getChildAt(1);
                    progressBars[i] = (ProgressBar) relativeLayout.getChildAt(2);
                    img_download[i] = (ImageView) relativeLayout.getChildAt(3);


                }
            }


//            tv_name = new TextView[3];
//            tv_name[0] = (TextView) view.findViewById(R.id.tv_appName1);
//            tv_name[1] = (TextView) view.findViewById(R.id.tv_appName2);
//            tv_name[2] = (TextView) view.findViewById(R.id.tv_appName3);
//
//            img_icon = new ImageView[3];
//            img_icon[0] = (ImageView) view.findViewById(R.id.img_icon1);
//            img_icon[1] = (ImageView) view.findViewById(R.id.img_icon2);
//            img_icon[2] = (ImageView) view.findViewById(R.id.img_icon3);
//
//            img_download = new ImageView[3];
//            img_download[0] = (ImageView) view.findViewById(R.id.img_download1);
//            img_download[1] = (ImageView) view.findViewById(R.id.img_download2);
//            img_download[2] = (ImageView) view.findViewById(R.id.img_download3);
//
//            img_hasUpdate = new ImageView[3];
//            img_hasUpdate[0] = (ImageView) view.findViewById(R.id.img_hasUpdate1);
//            img_hasUpdate[1] = (ImageView) view.findViewById(R.id.img_hasUpdate2);
//            img_hasUpdate[2] = (ImageView) view.findViewById(R.id.img_hasUpdate3);
//
//            progressBars = new ProgressBar[3];
//            progressBars[0] = (ProgressBar) view.findViewById(R.id.progressBar1);
//            progressBars[1] = (ProgressBar) view.findViewById(R.id.progressBar2);
//            progressBars[2] = (ProgressBar) view.findViewById(R.id.progressBar3);
//
//            layout = new LinearLayout[3];
//            layout[0] = (LinearLayout) view.findViewById(R.id.layout1);
//            layout[1] = (LinearLayout) view.findViewById(R.id.layout2);
//            layout[2] = (LinearLayout) view.findViewById(R.id.layout3);
        }
    }
}
