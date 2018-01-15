package com.gofirst.scenecollection.evidence.view.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.EvidenceExtra;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.BitmapUtils;
import com.gofirst.scenecollection.evidence.view.activity.CameraActivity;
import com.gofirst.scenecollection.evidence.view.activity.MipcaActivityCapture;
import com.gofirst.scenecollection.evidence.view.activity.ShowEvidenceExtra;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;
import com.gofirst.scenecollection.evidence.view.fragment.ScenePhotos;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Administrator on 2016/5/26.
 */
public class AddEvidenceAdapter extends BaseAdapter {

    private Activity context;
    private int QR_WIDTH;
    private int QR_HEIGHT;
    private EvidenceExtra evidenceExtra;
    private List<RecordFileInfo> list;
    private String id;
    private String templateId;
    private String mode;

    public AddEvidenceAdapter(Activity context, String id, String templateId) {
        this.context = context;
        QR_WIDTH = (int) context.getResources().getDisplayMetrics().density * 110;
        QR_HEIGHT = QR_WIDTH;
        this.id = id;
        this.templateId = templateId;
        getData();
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public int getCount() {
        return list.size() + 3;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (position > 2) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_child_item, parent, false);
            ImageView scene_photo = (ImageView) convertView.findViewById(R.id.imageview);
            scene_photo.setImageBitmap(BitmapUtils.revitionImageSize(list.get(position - 3).getFilePath()));
            scene_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ShowEvidenceExtra.class).putExtra("id", id)
                            .putExtra("position", position - 3);
                    intent.putExtra("id", id);
                    intent.putExtra("templateId", templateId);
                    intent.putExtra("mode", mode);
                    v.getContext().startActivity(intent);
                }
            });
        }
        if (position == 0) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_photo_layout, parent, false);
            convertView.findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ScenePhotos.tabflage.equals("7")) {
                        ScenePhotos.tabflage = "7";
                    }
                    v.getContext().startActivity(new Intent(v.getContext(), CameraActivity.class)
                            .putExtra("data", "addevidence")
                            .putExtra("tabflage", ScenePhotos.tabflage)
                            .putExtra("isEvidence", true)
                            .putExtra("father", evidenceExtra.getFather())
                            .putExtra("id", id));
                }
            });
        }
        if (position == 1) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_photo_layout, parent, false);
            ((TextView) convertView.findViewById(R.id.add_xx)).setText("扫描条码");
            convertView.findViewById(R.id.cross).setBackgroundResource(R.drawable.qr_code);
            convertView.findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, MipcaActivityCapture.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivityForResult(intent, 1);
                }
            });
        }
        if (position == 2) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_photo_layout, parent, false);
            final TextView name = ((TextView) convertView.findViewById(R.id.add_xx));
            name.setText("生成条码");
            final RelativeLayout imageView = (RelativeLayout) convertView.findViewById(R.id.take_picture);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createQRImage(evidenceExtra.getId());
                    getData();
                    notifyDataSetChanged();
                }
            });
        }
        return convertView;
    }


    public void createQRImage(String url) {
        try {
            if (url == null || "".equals(url) || url.length() < 1) {
                return;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            saveEvidenceQR(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    public String saveEvidenceQR(Bitmap bitmap) {
        File file = new File(context.getObbDir() + "/evidence");
        File filePath = null;
        if (!file.exists()) {
            Toast.makeText(context, file.mkdir() ? "文件夹创建成功" : "文件夹创建失败", Toast.LENGTH_SHORT).show();
        }
        try {

            //判断是否已经存在二维码
            if (list != null) {
                for (RecordFileInfo recordFileInfo : list) {
                    if (recordFileInfo.getChild() != null && recordFileInfo.getChild().equals("二维码")) {
                        filePath = new File(recordFileInfo.getFilePath());
                    }
                }
            }
            if (filePath == null) {
                filePath = new File(file, System.currentTimeMillis() + ".png");
            }
            RecordFileInfo recordFileInfo = new RecordFileInfo();
            recordFileInfo.setId(ViewUtil.getUUid());
            recordFileInfo.setCaseId(evidenceExtra.getCaseId());
            recordFileInfo.setFather(evidenceExtra.getFather());
            recordFileInfo.setChild("二维码");
            recordFileInfo.setSection(evidenceExtra.getSection());
            recordFileInfo.setFilePath(filePath.toString());
            recordFileInfo.setFileType("png");
            recordFileInfo.setFileDate(new Date());
            //若已经存在，数据库不存
            if (!filePath.exists()) {
                EvidenceApplication.db.save(recordFileInfo);
                EvidenceApplication.db.update(evidenceExtra);
            }
            Toast.makeText(context, !filePath.exists() ? "已生成二维码" : "已经覆盖二维码", Toast.LENGTH_SHORT).show();
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filePath.toString();
    }

    public void getData() {
        this.evidenceExtra = EvidenceApplication.db.findById(id, EvidenceExtra.class);
        list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "section = '" + evidenceExtra.getSection() + "' and fileType = 'png'", "fileDate desc");
    }
}


