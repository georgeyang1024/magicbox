package cn.georgeyang.lib;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.TypedValue;

import java.io.InputStream;

/**
 *
 * Created by george.yang on 2016-3-29.
 */
public class PluginResources extends Resources {
    private Resources root;
    public PluginResources(AssetManager assets,Resources root) {
        super(assets, root.getDisplayMetrics(), root.getConfiguration());
        this.root = root;
    }

    @Override
    public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
        try {
            super.getValue(id, outValue, resolveRefs);
        } catch (Exception e) {

        }
        root.getValue(id,outValue,resolveRefs);
    }

    @Override
    public void getValue(String name, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
        try {
            super.getValue(name, outValue, resolveRefs);
        } catch (Exception e) {

        }
        root.getValue(name, outValue, resolveRefs);
    }

    @Override
    public XmlResourceParser getXml(int id) throws NotFoundException {
        try {
            return super.getXml(id);
        } catch (Exception e) {

        }
        return root.getXml(id);
    }

    @Override
    public XmlResourceParser getAnimation(int id) throws NotFoundException {
        try {
            return super.getAnimation(id);
        } catch (Exception e) {

        }
        return root.getAnimation(id);
    }

    @Override
    public int getIdentifier(String name, String defType, String defPackage) {
        try {
            return super.getIdentifier(name, defType, defPackage);
        } catch (Exception e) {

        }
        return root.getIdentifier(name, defType, defPackage);
    }

    @Override
    public InputStream openRawResource(int id, TypedValue value) throws NotFoundException {
        try {
            return super.openRawResource(id, value);
        } catch (Exception e) {

        }
        return root.openRawResource(id, value);
    }

    @Override
    public InputStream openRawResource(int id) throws NotFoundException {
        try {
            return super.openRawResource(id);
        } catch (Exception e) {

        }
        return root.openRawResource(id);
    }

//    @Override
//    public XmlResourceParser getLayout(int id) throws NotFoundException {
//        try {
//            return super.getLayout(id);
//        } catch (Exception e) {
//
//        }
//        return root.getLayout(id);
//    }

//    @Override
//    public AssetFileDescriptor openRawResourceFd(int id) throws NotFoundException {
//        try {
//            return super.openRawResourceFd(id);
//        } catch (Exception e) {
//
//        }
//        return root.openRawResourceFd(id);
//    }

    @Override
    public int getInteger(int id) throws NotFoundException {
        try {
            return super.getInteger(id);
        } catch (Exception e) {

        }
        return root.getInteger(id);
    }

    @Override
    public String getString(int id) throws NotFoundException {
        try {
            return super.getString(id);
        } catch (Exception e) {

        }
        return root.getString(id);
    }
}
