package com.gofirst.scenecollection.evidence.sync;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gofirst.scenecollection.evidence.utils.SharePre;

/**
 * 后台一键恢复出厂设置服务提供信息校验
 */

public class MonitorDeviceInfoProvider extends ContentProvider{

    private String[] cloumNames =
            {"userId","machineCode","longitude","latitude","userName","userOrg","userOrgName","modularVersionNo"};
    private UriMatcher matcher;

    @Override
    public boolean onCreate() {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI("com.gofirst.scenecollection.evidence.provider","device",1);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (matcher.match(uri) == 1) {
            SharePre sharePre = new SharePre(getContext(), "user_info", Context.MODE_PRIVATE);
            MatrixCursor cursor = new MatrixCursor(cloumNames, 1);
            MatrixCursor.RowBuilder builder = cursor.newRow();
            builder.add(cloumNames[0], sharePre.getString("user_id", ""));
            builder.add(cloumNames[1], sharePre.getString("machineCode", ""));
            builder.add(cloumNames[2], sharePre.getString("longitude", ""));
            builder.add(cloumNames[3], sharePre.getString("latitude", ""));
            builder.add(cloumNames[4], sharePre.getString("user_name", ""));
            builder.add(cloumNames[5], sharePre.getString("organizationId", ""));
            builder.add(cloumNames[6], sharePre.getString("organizationCname", ""));
            builder.add(cloumNames[7], sharePre.getString("modularVersionNo", ""));
            return cursor;
        }
     return  null;
    }

    /**
     * 这些信息全是单条信息所以返回一种格式mime
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        if (matcher.match(uri) == 1) {
            return "vnd.android.cursor.item/vnd.com.gofirst.scenecollection.evidence.provider.device";
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
