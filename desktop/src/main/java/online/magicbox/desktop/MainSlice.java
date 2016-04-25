package online.magicbox.desktop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.georgeyang.database.Mdb;
import cn.georgeyang.lib.UiThread;
import cn.georgeyang.network.NetCallback;
import cn.georgeyang.network.OkHttpRequest;
import cn.georgeyang.util.FileUtil;
import cn.georgeyang.util.HttpUtil;
import cn.georgeyang.util.ImageLoder;
import cn.georgeyang.util.Logutil;
import cn.georgeyang.util.NetUtil;
import cn.georgeyang.util.ShortCutUtil;
import it.sephiroth.widget.MultiDirectionSlidingDrawer;
import online.magicbox.desktop.adapter.NormalRecyclerViewAdapter;
import online.magicbox.desktop.entity.AppInfoBean;
import online.magicbox.desktop.entity.DesktopSettingEntity;
import online.magicbox.desktop.entity.PluginItemEntity;
import online.magicbox.desktop.subclass.PluginListProcessor;
import online.magicbox.lib.PluginActivity;
import online.magicbox.lib.PluginConfig;
import online.magicbox.lib.Slice;

/**
 * Created by george.yang on 2016-3-30.
 */
public class MainSlice extends Slice implements View.OnClickListener, UiThread.UIThreadEvent, SwipeRefreshLayout.OnRefreshListener, NetCallback<List<PluginItemEntity>> {
    public MainSlice(Context base, Object holder) {
        super(base, holder);
    }
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MultiDirectionSlidingDrawer multiDirectionSlidingDrawer;
    private PluginListProcessor processor;
    private NormalRecyclerViewAdapter adapter;
    private static List<AppInfoBean> cacheList;
    public static DesktopSettingEntity settingEntity;
    private long cacheSize;
    private TextView tv_update;
    //http://ptool.aliapp.com/markdown.read.do?file=about/funypic.md

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OkHttpRequest.init(this);
        Mdb.init(this);
        setContentView(R.layout.fragment_main);
        UiThread.init(this).setFlag("init").start(this);
        UiThread.init(this).setFlag("cache").start(this);
    }

    @Override
    public boolean onBackPressed() {
        if (multiDirectionSlidingDrawer.isOpened()) {
            multiDirectionSlidingDrawer.animateClose();
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_iconSize:
                settingEntity.iconSize++;
                if (settingEntity.iconSize<3 || settingEntity.iconSize>=6) {
                    settingEntity.iconSize = 3;
                }
                settingEntity.save();
                adapter = new NormalRecyclerViewAdapter(settingEntity.iconSize);
                adapter.setDataInThread(cacheList);
                mRecyclerView.setAdapter(adapter);
                break;
            case R.id.layout_sort:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("选择操作");
                builder.setItems(R.array.appSortBy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                settingEntity.iconSort = AppInfoBean.Sort_name;
                                break;
                            case 1:
                                settingEntity.iconSort = AppInfoBean.Sort_time;
                                break;
                        }
                        settingEntity.save();
                        UiThread.init(MainSlice.this).setFlag("cache").start(MainSlice.this);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
                break;
            case R.id.layout_clearCache:
                UiThread.init(this).setFlag("clearCache").start(this);
                break;
            case R.id.layout_autoUpdate:
                settingEntity.autoUpdate = !settingEntity.autoUpdate;
                if (settingEntity.autoUpdate) {
                    tv_update.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.icon_pop_list_choose_s,0);
                } else {
                    tv_update.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.icon_pop_list_choose_n,0);
                }
                settingEntity.save();
                break;
            case R.id.layout_shortcut:
                settingEntity.autoCreateShortCut = !settingEntity.autoCreateShortCut;
                if (settingEntity.autoCreateShortCut) {
                    tv_shortCut.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.icon_pop_list_choose_s,0);
                } else {
                    tv_shortCut.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.icon_pop_list_choose_n,0);
                }
                settingEntity.save();
                break;
        }
    }

    @Override
    public Object runInThread(String flag, Object obj, UiThread.Publisher publisher) {
        switch (flag) {
            case "init":
                findViewById(R.id.layout_iconSize).setOnClickListener(this);
                findViewById(R.id.layout_sort).setOnClickListener(this);
                findViewById(R.id.layout_clearCache).setOnClickListener(this);
                findViewById(R.id.layout_autoUpdate).setOnClickListener(this);
                findViewById(R.id.layout_shortcut).setOnClickListener(this);
                processor = new PluginListProcessor(this);
                multiDirectionSlidingDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.drawer);
                swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
                mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                //如果确定每个item的内容不会改变RecyclerView的大小，设置这个选项可以提高性能
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        if (newState==RecyclerView.SCROLL_STATE_IDLE) {
                            Vars.scrolling = false;
                        } else {
                            Vars.scrolling = true;
                        }
                    }
                });
                break;
            case "deal":
                if (obj==null) {
                    return null;
                }
                cacheList = processor.doSomething((List<PluginItemEntity>)obj);
                adapter.setDataInThread(cacheList);
                if (settingEntity.autoUpdate) {
                    UiThread.init(this).setObject(cacheList).setFlag("autoUpdate").start(this);
                }
                return cacheList;
            case "cache":
                if (settingEntity==null) {
                    settingEntity  = Mdb.getInstance().findOne(DesktopSettingEntity.class);
                    if (settingEntity==null) {
                        settingEntity = new DesktopSettingEntity();
                    }
                }
                if (settingEntity.iconSize<3 || settingEntity.iconSize>=6) {
                    settingEntity.iconSize = 3;
                }

                adapter = new NormalRecyclerViewAdapter(settingEntity.iconSize);

                if (cacheList!=null) {
                    Collections.sort(cacheList);
                    adapter.setDataInThread(cacheList);
                    return cacheList;
                }
                try {
                    String json = OkHttpRequest.getGetRequestCache(getView(), Vars.ListPlugin,buildParams());
                    Type type = new TypeToken<List<PluginItemEntity>>(){}.getType();
                    List<PluginItemEntity> list = new Gson().fromJson(json, type);
                    return list;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "initMenu":
                tv_cacheMenu = (TextView) findViewById(R.id.tv_cacheMenu);
                tv_shortCut = (TextView) findViewById(R.id.tv_shortcut);
                tv_update = (TextView) findViewById(R.id.tv_update);
                cacheSize = FileUtil.getFolderSize(getCacheDir());
                Logutil.showlog("cacheSize:" + cacheSize);
                break;
            case "clearCache":
                FileUtil.deleteFolderFile(getCacheDir().getAbsolutePath(),false);
                break;
            case "autoUpdate":
                if (NetUtil.isWifiActive(this)) {
                    List<AppInfoBean> list = (List<AppInfoBean>) obj;
                    for (AppInfoBean infoBean:list) {
                        if (infoBean.hasUpdate()) {
                            try {
                                File downFile = new File(this.getFilesDir(),String.format("%s_%s.apk",new Object[]{infoBean.packageName,infoBean.lastVersionCode}));
                                HttpUtil.downLoadFile2(infoBean.downUrl,downFile);
                                infoBean.downloading = false;
                                infoBean.isInstall = true;
                                infoBean.installVersionCode = infoBean.lastVersionCode;
                                Intent oldIntent = infoBean.intent;
                                Intent newIntent = PluginActivity.buildIntent(MainSlice.this,infoBean.packageName,infoBean.mainClass, PluginConfig.System,infoBean.installVersionCode+"");
                                infoBean.intent = newIntent;
                                infoBean.installTime = System.currentTimeMillis();

                                PluginItemEntity dbBean = Mdb.getInstance().findOnebyWhereDesc(PluginItemEntity.class,"_addTime",String.format("packageName='%s'",new Object[]{infoBean.packageName}));
                                dbBean.installVersionCode = infoBean.lastVersionCode;
                                dbBean.save();

                                try {
                                    ShortCutUtil.removeShortCut(MainSlice.this,infoBean.name,oldIntent);
                                    if (MainSlice.settingEntity.autoCreateShortCut) {
                                        Bitmap bitmap = ImageLoder.loadImage(MainSlice.this,infoBean.imageUrl,300,300);
                                        if (bitmap==null) {
                                            ShortCutUtil.createShortCut(MainSlice.this,infoBean.name,newIntent);
                                        } else {
                                            ShortCutUtil.createShortCutWithBitmap(MainSlice.this,infoBean.name,newIntent,bitmap);
                                        }
                                    }
                                } catch (Exception e) {

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    cacheList = list;
                    adapter.setDataInThread(cacheList);
                    return "";
                }
                break;
        }
        return null;
    }

    @Override
    public void runInUi(String flag, Object obj, boolean ispublish, float progress) {
        switch (flag) {
            case "init":
                Log.i("test","jni:" + Jni.hello());
                swipeRefreshLayout.setOnRefreshListener(this);
                findViewById(R.id.content).setOnClickListener(this);
                break;
            case "deal":
                swipeRefreshLayout.setRefreshing(false);
                if (obj!=null) {
                    adapter.notifyDataSetChanged();
                }
                if (dialog!=null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                break;
            case "cache":
                UiThread.init(this).setFlag("initMenu").start(this);
                if (mRecyclerView==null) {
                    mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                }
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
                mRecyclerView.setAdapter(adapter);
                if (obj!=null) {
                    if (obj instanceof List) {
                        List list = (List) obj;
                        if (list.size()>0) {
                            Object item0 = ((List)obj).get(0);
                            if (item0 instanceof AppInfoBean) {
                                adapter.notifyDataSetChanged();
                                return;
                            }
                        }
                    }
                    UiThread.init(this).setFlag("deal").setObject(obj).start(this);
                    showDialog();
                } else {
                    showDialog();
                    onRefresh();
                }
                break;
            case "initMenu":
                tv_cacheMenu.setText(String.format("清理缓存(%s)",new Object[]{FileUtil.setFileSize(cacheSize)}));
                if (settingEntity.autoUpdate) {
                    tv_update.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.icon_pop_list_choose_s,0);
                } else {
                    tv_update.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.icon_pop_list_choose_n,0);
                }
                if (settingEntity.autoCreateShortCut) {
                    tv_shortCut.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.icon_pop_list_choose_s,0);
                } else {
                    tv_shortCut.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.icon_pop_list_choose_n,0);
                }
                break;
            case "clearCache":
                if (cacheSize!=0) {
                    tv_cacheMenu.setText("清理缓存(0KB)");
                    Toast.makeText(this,"清理完成!",Toast.LENGTH_SHORT).show();
                }
                break;
            case "autoUpdate":
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private TextView tv_cacheMenu;
    private TextView tv_shortCut;
    private ProgressDialog dialog;
    private void showDialog () {
        dialog = ProgressDialog.show(this, null, "加载中", true,false, null);
    }

    @Override
    public void onRefresh() {
        Map<String,Object> params = buildParams();
        OkHttpRequest.getInstance().get(getView(),"", Vars.ListPlugin,params,this);
    }

    private Map<String,Object> buildParams () {
        Map<String,Object> params = new HashMap<>();
        params.put("packageName","online.magicbox.desktop");
        params.put("versionCode","0");
        params.put("lastId","0");
        return params;
    }

    @Override
    public void onSuccess(String flag, List<PluginItemEntity> object) {
        UiThread.init(this).setFlag("deal").setObject(object).start(this);
        UiThread.init(this).setFlag("initMenu").setRunDelay(5000).start(this);
    }

    @Override
    public void onFail(String flag, int code, Exception e) {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (!multiDirectionSlidingDrawer.isOpened()) {
                multiDirectionSlidingDrawer.animateOpen();
            } else {
                multiDirectionSlidingDrawer.animateClose();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
