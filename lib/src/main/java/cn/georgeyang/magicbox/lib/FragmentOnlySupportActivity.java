package cn.georgeyang.magicbox.lib;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 让activity只支持一个
 * Created by george.yang on 15/11/18.
 */
public abstract class FragmentOnlySupportActivity extends PlugActivity {
    public abstract int getContainerId();
    private int containerId = getContainerId();
    public static boolean fragmentChangeing = false;
    private static final String TAG = "FragmentLoder";

    public <T extends Fragment> T getFragment(FragmentManager manager, Class clazz) {
        return getFragment(manager, false, clazz);
    }

    public List<Fragment> getFragments(FragmentManager fragmentManager) {
        try {
            Field field = fragmentManager.getClass().getDeclaredField("mAdded");
            field.setAccessible(true);
            return (List<Fragment>) field.get(fragmentManager);
        } catch (Exception e) {
           e.printStackTrace();
        }
        return null;
    }

    public <T extends Fragment> T getFragment(FragmentManager manager, boolean showing, Class clazz) {
        if (clazz == null) {
            return null;
        }
        if (manager == null) {
            manager = getFragmentManager();
        }
        List<Fragment> fragmentlist = getFragments(manager);
        if (fragmentlist != null) {
            for (Fragment fragment : fragmentlist) {
                if (fragment == null || fragment.isRemoving() || !fragment.isAdded() || (showing && fragment.isHidden())) {
                    continue;
                }
                if (clazz.getName().equals(fragment.getClass().getName())) {
                    return (T) fragment;
                } else {
                    Fragment fragmentInner = getFragment(fragment.getFragmentManager(), showing, clazz);
                    if (fragmentInner != null) {
                        return (T) fragmentInner;
                    }
                }
            }
        }
        return null;
    }

    public void loadFragment(Fragment tagfragment, FragmentTransaction mFragmentTransaction) {
        loadFragment(tagfragment,mFragmentTransaction, AnimType.LeftInRightOut);
    }

    public void loadFragment(Fragment tagfragment) {
        loadFragment(tagfragment,AnimType.LeftInRightOut);
    }

    public void loadFragment(Fragment tagfragment,AnimType animType) {
        loadFragment(tagfragment, null, animType);
    }

    private int fragmentIndex;
    public void loadFragment(Fragment tagfragment, FragmentTransaction mFragmentTransaction,AnimType animType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (isDestroyed()) {
                return;
            }
        }
        if (isFinishing()) {
            return;
        }
        Log.i(TAG,"load:" + tagfragment);
        if (!fragmentChangeing) {
            fragmentChangeing = true;
            if (tagfragment!=null) {
                if (mFragmentTransaction == null) {
                    mFragmentTransaction = getFragmentManager().beginTransaction();
                }

                //動畫
                if (animType==AnimType.ZoomShow) {
                    mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                } else if (animType==AnimType.NONE) {
                    //none
                } else {
                    //自定義
                    int[] anims = AnimType.getAnimRes(animType);
                    mFragmentTransaction.setCustomAnimations(anims[0], anims[1],anims[2],anims[3]);
                }

                List<Fragment> fragmentlist = getFragments(getFragmentManager());
                if (fragmentlist != null)
                    for (int i = 0; i < fragmentlist.size(); i++) {
                        Fragment fragment = fragmentlist.get(i);
                        if (fragment != null) {
                            mFragmentTransaction.hide(fragment);
                        }
                    }

                fragmentIndex++;
                FragmentTagVo tagVo = new FragmentTagVo(fragmentIndex,animType==null?AnimType.NONE.toString():animType.toString());
                try {
                    mFragmentTransaction.add(containerId, tagfragment, JSONObject.toJSONString(tagVo));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mFragmentTransaction.show(tagfragment);
                mFragmentTransaction.commitAllowingStateLoss();
            }
            fragmentChangeing = false;
        }
    }

    public void pushMsgToSubFragments (boolean needShow,int pushCode,Intent pushData) {
        pushMsgToSubFragments(getFragmentManager(), needShow,pushCode, Activity.RESULT_OK, pushData);
    }


    public void pushMsgToSubFragments (FragmentManager fragmentManager,boolean needShow,int requestCode,int resultCode, Intent data) {
        List<Fragment> fragmentlist = getFragments(fragmentManager);
        if (fragmentlist!=null)
            for (Fragment fragment:fragmentlist) {
                if (fragment==null) {
                    continue;
                }
                if (fragment.isAdded()) {
                    if (needShow) {
                        if (!fragment.isHidden()) {
                            Log.i(TAG,"send to:" + fragment);
                            fragment.onActivityResult(requestCode, resultCode, data);
                        }
                    } else {
                        Log.i(TAG,"send to:" + fragment);
                        fragment.onActivityResult(requestCode, resultCode, data);
                    }

                    pushMsgToSubFragments(getFragmentManager(),needShow,requestCode,resultCode,data);
                }
            }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"onActivityResult:" + requestCode);
        try {
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        pushMsgToSubFragments(getFragmentManager(), false, requestCode, resultCode, data);
    }


    final List<Fragment> removeFragment = new ArrayList<>();
    public boolean removeFragment(Class clazz) {
        if (clazz == null) {
            return false;
        }
        Log.i(TAG,"try remove:" + clazz.getName());
        synchronized (removeFragment) {
            List<Fragment> fragmentlist = getFragments(getFragmentManager());
            if (fragmentlist != null) {
                for (Fragment fragment : fragmentlist) {
                    if (fragment == null) {
                        continue;
                    }
                    if (clazz.getName().equals(fragment.getClass().getName())) {
                        Log.i(TAG,"remove:" + clazz.getName());
                        removeFragment.add(fragment);
                    }
                }

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                for (Fragment fragment : removeFragment) {
                    ft.remove(fragment);
                }
                ft.commitAllowingStateLoss();
                removeFragment.clear();
            }
        }
        return true;
    }

    public void setCanBackPress(boolean can) {
        this.mCanBackPress = can;
    }

    protected boolean mCanBackPress = true;
    protected OnBackPressedListener onBackPressedListener;

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        this.onBackPressedListener = listener;
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null) {
            onBackPressedListener.OnBackKeyDown();
            return;
        }

        if (!mCanBackPress) {
            return;
        }
        backPressed();
    }


    /**
     * 返回fragment,动画版本的
     */
    public void backPressed () {
        boolean waitAnimEndUnLock = false;
        if (!fragmentChangeing) {
            fragmentChangeing = true;
            try {
                FragmentManager fragmentManager = getFragmentManager();
                Log.i(TAG,"pending:" + fragmentManager.executePendingTransactions());
                List<Fragment> fragmentlist = getFragments(fragmentManager);
                if (fragmentlist==null) {
                    finish();
                }
                Log.i(TAG,"list:" + fragmentlist);
                for (int i = fragmentlist.size()-1; i >= 0 ; i--) {
                    Fragment fragment = fragmentlist.get(i);
                    if (!(fragment == null || !fragment.isVisible())) {//可视且不是没有设置view的fragment
                        //应用自己的返回逻辑
                        //                        if (fragment instanceof ContainerFragment) {
                        //                            ContainerFragment containerFragment = (ContainerFragment)fragment;
                        //                            String fragmentName =containerFragment.content.getClass().getSimpleName();
                        //                            if (fragmentName.equals("home")) {
                        //                                finish();
                        //                                return;
                        //                            }
                        //                        }

                        Fragment backFragment = null;
                        Log.i(TAG,"tag:" + fragment.getTag());
                        FragmentTagVo tagVo = JSONObject.parseObject(fragment.getTag(),FragmentTagVo.class);
                        int fragIndex = tagVo.index-1;
                        Looper:
                        while (fragIndex >= 0) {
                            FindByTag:
                            for (AnimType animType:AnimType.values()) {
                                tagVo = new FragmentTagVo(fragIndex,animType.toString());
                                backFragment = fragmentManager.findFragmentByTag(JSONObject.toJSONString(tagVo));
                                if (backFragment!=null) {
                                    break FindByTag;
                                }
                            }
                            Log.i(TAG,"backFragment:" + backFragment);
                            if (backFragment != null) {
                                Log.i(TAG,"backFragment add?:" + backFragment.isAdded());
                                Log.i(TAG,"backFragment removing?:" + backFragment.isRemoving());
                            }
                            if (backFragment == null || !backFragment.isAdded() || backFragment.isRemoving()) {
                                --fragIndex;
                            } else {
                                break Looper;
                            }
                        }
                        Log.i(TAG,"currFragment:" + fragment.getClass().getSimpleName());
                        Log.i(TAG,"backFragment:" + backFragment);

                        if (backFragment != null) {
                            tagVo = JSONObject.parseObject(fragment.getTag(),FragmentTagVo.class);
                            AnimType exitAnim = null;
                            if(tagVo!=null)
                                for (AnimType animType:AnimType.values()) {
                                    if (animType.toString().equals(tagVo.animType.toString())) {
                                        exitAnim = animType;
                                    }
                                }

                            final Fragment animShowFragment = backFragment;
                            final Fragment animHideFragment = fragment;
                            if (exitAnim==null || exitAnim==AnimType.NONE) {
                                FragmentTransaction mFragmentTransaction = getFragmentManager().beginTransaction();
                                mFragmentTransaction.show(animShowFragment);
                                mFragmentTransaction.remove(animHideFragment);
                                mFragmentTransaction.commitAllowingStateLoss();
                            } else if (exitAnim==AnimType.ZoomShow) {
                                FragmentTransaction mFragmentTransaction = getFragmentManager().beginTransaction();
                                mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                                mFragmentTransaction.show(animShowFragment);
                                mFragmentTransaction.remove(animHideFragment);
                                mFragmentTransaction.commitAllowingStateLoss();
                            } else {
                                FragmentTransaction mFragmentTransaction = getFragmentManager().beginTransaction();
                                int[] animRes = AnimType.getAnimRes(exitAnim);
                                mFragmentTransaction.setCustomAnimations(animRes[2],animRes[3]);
                                mFragmentTransaction.show(animShowFragment);
                                mFragmentTransaction.remove(animHideFragment);
                                mFragmentTransaction.commitAllowingStateLoss();
                            }
//                                waitAnimEndUnLock = true;
//                                int[] animRes = AnimType.getAnimRes(exitAnim);
//                                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), animRes[3]);
//                                animation.setAnimationListener(new Animation.AnimationListener() {
//                                    @Override
//                                    public void onAnimationStart(Animation animation) {
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationEnd(Animation animation) {
//                                        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
//                                        mFragmentTransaction.show(animShowFragment);
//                                        mFragmentTransaction.remove(animHideFragment);
//                                        mFragmentTransaction.commitAllowingStateLoss();
//
//                                        fragmentChangeing  = false;
//                                    }
//
//                                    @Override
//                                    public void onAnimationRepeat(Animation animation) {
//
//                                    }
//                                });
//                                fragment.getView().startAnimation(animation);
//                                backFragment.getView().setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), animRes[2]));
//                            }
                        } else {
                            finish();
                        }
                    }//end if (!(fragment == null || !fragment.isVisible())) {//可视的
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            KeyboradUtil.hideSoftKeyboard(this);
            if (!waitAnimEndUnLock) {
                fragmentChangeing = false;
            }
        }
    }



}
