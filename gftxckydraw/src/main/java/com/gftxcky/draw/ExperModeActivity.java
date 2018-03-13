package com.gftxcky.draw;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gftxcky.draw.primitive.GatherBean;
import com.gftxcky.draw.primitive.PaintType;
import com.gftxcky.draw.primitive.XCKYPoint;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExperModeActivity extends Activity {

    private final static String TAG = ExperModeActivity.class.getSimpleName();
    private Toast _mToast;
    private List<String> _modList;
    private List<ArrayList<String>> _modListChilds;
    private LinearLayout _mouldSelLinear;
    private LinearLayout _selEdMouldLinear;
    private int _modType;
    private float _selModX;
    private LinearLayout _mainContent;
    private ExperModeActivity _this;
    private ExperModeCanvas experModeCanvas = null;
    //工程相关
    private String _info = "";
    private String _projectID;
    private String _title;
    private int _picID;
    private String caseId, father;

    public View decorView;
    public CheckBox dragbox;
    public CheckBox scalebox;
    public CheckBox rotationbox;
    public LinearLayout textscalelayout;
    public LinearLayout checkboxlayout;
    private String storedGatherpath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _this = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //
        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        //
        setContentView(R.layout.expermode_activity);
        experModeCanvas = new ExperModeCanvas(this);
        Intent intent = getIntent();

        if (getIntent().getStringExtra("caseId") != null) {
            caseId = getIntent().getStringExtra("caseId");
        }
        if (getIntent().getStringExtra("father") != null) {
            father = getIntent().getStringExtra("father");
        }
        Log.d("caseIdPlan", caseId);

        //获取当前工程信息
        if (intent.getStringExtra("gatherpath") != null) {
            _projectID = intent.getStringExtra("ID");
            storedGatherpath = intent.getStringExtra("gatherpath");
            File palfile = new File(storedGatherpath);
            byte[] gather = fileToBytes(palfile);
            GatherBean gatherbean = (GatherBean) Serializer.decode(gather);
            if (gatherbean != null) {
                this.experModeCanvas.setData(gatherbean);
                List<String[]> tablInfos = gatherbean.getTableBean().getTxts();
                Calendar calendar = Calendar.getInstance();
                this.setInfo(tablInfos.get(4)[1] + "," +
                        tablInfos.get(1)[1] + "," +
                        tablInfos.get(2)[1] + "," +
                        tablInfos.get(3)[1] + "," +
                        tablInfos.get(0)[1] + "," +
                        calendar.get(1) + "年" + (calendar.get(2) + 1) + "月" + calendar.get(5) + "日");
                Log.d(TAG, this._info);
                this._title = gatherbean.getTableBean().getTitleText();
            }
            this._picID = intent.getIntExtra("CID", -1);
        } else {
            String pinfo = intent.getStringExtra("info");
            this._projectID = pinfo.split(",")[0];
            Calendar calendar = Calendar.getInstance();
            this.setInfo(new StringBuilder(pinfo.substring(_projectID.length() + 1)).append(",").append(calendar.get(1) + "年" + (calendar.get(2) + 1) + "月" + calendar.get(5) + "日").toString());
            Log.e("jiu", "id = " + _projectID + "; info = " + getInfo());
            this.experModeCanvas.setTabInfo(this.getInfo());
            this._title = experModeCanvas.getTableBean().getTitleText();
        }

        _mainContent = (LinearLayout) findViewById(R.id.CanvesCon);
        //初始化画布需要的东西
        getMainContent().addView(this.experModeCanvas);
        getMainContent().setBackgroundColor(Color.WHITE);
        ((ImageButton) findViewById(R.id.straight_line)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                experModeCanvas.setPaint(PaintType.StraightLine);
                getMainContent().removeAllViews();
                getMainContent().addView(experModeCanvas);
                ExperModeActivity.this.showToast("直线");
            }
        });
        ((ImageButton) findViewById(R.id.oblique_line)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                experModeCanvas.setPaint(PaintType.ObliqueLine);
                getMainContent().removeAllViews();
                getMainContent().addView(experModeCanvas);
                ExperModeActivity.this.showToast("斜线");
            }
        });
        ((ImageButton) findViewById(R.id.polygon_line)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                experModeCanvas.setPaint(PaintType.PolygonLine);
                getMainContent().removeAllViews();
                getMainContent().addView(experModeCanvas);
                ExperModeActivity.this.showToast("多边形线");
            }
        });
        ((ImageButton) findViewById(R.id.straight_wall)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                experModeCanvas.setPaint(PaintType.StraightWall);
                getMainContent().removeAllViews();
                getMainContent().addView(experModeCanvas);
                ExperModeActivity.this.showToast("直墙");
            }
        });
        ((ImageButton) findViewById(R.id.oblique_wall)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                experModeCanvas.setPaint(PaintType.ObliqueWall);
                getMainContent().removeAllViews();
                getMainContent().addView(experModeCanvas);
                ExperModeActivity.this.showToast("斜墙");
            }
        });
        ((ImageButton) findViewById(R.id.polygon_wall)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                experModeCanvas.setPaint(PaintType.PolygonWall);
                getMainContent().removeAllViews();
                getMainContent().addView(experModeCanvas);
                ExperModeActivity.this.showToast("多边形墙");
            }
        });
        ((ImageButton) findViewById(R.id.rect_wall)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                experModeCanvas.setPaint(PaintType.RectWall);
                getMainContent().removeAllViews();
                getMainContent().addView(experModeCanvas);
                ExperModeActivity.this.showToast("矩形墙");
            }
        });
        ((ImageButton) findViewById(R.id.text)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                experModeCanvas.setPaint(PaintType.Text);
                getMainContent().removeAllViews();
                getMainContent().addView(experModeCanvas);
                ExperModeActivity.this.showToast("文字");
            }
        });
        ((ImageButton) findViewById(R.id.window)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                experModeCanvas.setPaint(PaintType.Window);
                getMainContent().removeAllViews();
                getMainContent().addView(experModeCanvas);
                ExperModeActivity.this.showToast("窗");
            }
        });
        ((ImageButton) findViewById(R.id.door)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                experModeCanvas.setPaint(PaintType.Door);
                getMainContent().removeAllViews();
                getMainContent().addView(experModeCanvas);
                ExperModeActivity.this.showToast("门");
            }
        });
        ((ImageButton) findViewById(R.id.imp_model)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                final Dialog dialog = new Dialog(_this, R.style.SelectModelDialogTheme);
                dialog.setTitle("模型选择");
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.selmould_dialog);
                setMouldSelLinear((LinearLayout) dialog.findViewById(R.id.mouldSel));
                setSelEdMouldLinear((LinearLayout) dialog.findViewById(R.id.SelMould));
                Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner);
                spinner.setAdapter(ArrayAdapter.createFromResource(_this, R.array.modType, android.R.layout.simple_dropdown_item_1line));
                spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterview, View view,
                                               int index, long arg3) {
                        _modType = index;

                        loadMould(dialog);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterview) {
                    }
                });
                loadMould(dialog);
                dialog.show();
                ((Button) dialog.findViewById(R.id.SelMouldcancel)).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _this.getMouldSelLinear().removeAllViews();
                        _this.getSelEdMouldLinear().removeAllViews();
                        dialog.cancel();
                    }
                });
                ((Button) dialog.findViewById(R.id.insertMouldBtn)).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int index = 0; index < _this.getSelEdMouldLinear().getChildCount(); index++) {
                            TextView textview = (TextView) _this.getSelEdMouldLinear().getChildAt(index);
                            if (Integer.parseInt(textview.getTag().toString().split("-")[1]) == 1)
                                _this.getExperModeCanvas().loadDrawPath((new MouldBeanLoader(_this).loadMAYAMouldBean(textview.getTag().toString().split("-")[0], textview.getText().toString())));
                            else
                                _this.getExperModeCanvas().loadDrawPath((new MouldBeanLoader(_this).loadMouldBean(textview.getTag().toString().split("-")[0])));
                        }
                        getMainContent().removeAllViews();
                        getMainContent().addView(experModeCanvas);
                        dialog.cancel();
                        return;
                    }
                });
            }
        });
        ((ImageButton) findViewById(R.id.del)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                experModeCanvas.setPaint(PaintType.None);
                experModeCanvas.deleteSelect();
                getMainContent().removeAllViews();
                getMainContent().addView(experModeCanvas);
                ExperModeActivity.this.showToast("删除");
            }
        });
        ((ImageButton) findViewById(R.id.restore)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                experModeCanvas.setPaint(PaintType.None);
                if (experModeCanvas.restore()) {
                    ExperModeActivity.this.showToast("恢复");
                } else {
                    ExperModeActivity.this.showToast("已经是最后一步");
                }
                getMainContent().removeAllViews();
                getMainContent().addView(experModeCanvas);

            }
        });
        ((ImageButton) findViewById(R.id.revocation)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                experModeCanvas.setPaint(PaintType.None);
                if (experModeCanvas.revocation()) {
                    ExperModeActivity.this.showToast("撤销");
                } else {
                    ExperModeActivity.this.showToast("已经是最前一步");
                }
                getMainContent().removeAllViews();
                getMainContent().addView(experModeCanvas);
            }
        });
        ((ImageButton) findViewById(R.id.Tab)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] infos = getInfo().split(",");
                final Dialog dialog = new Dialog(_this, R.style.SelectModelDialogTheme);
                dialog.setTitle("制表信息");
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.showtab_dialog);
                LinearLayout linearlayout = (LinearLayout) dialog.findViewById(R.id.canvasLayout);
                TableInfoView tabView = new TableInfoView(_this, infos[0] + "," + infos[1] + "," + infos[2] + "," + infos[3] + "," + infos[5]);
                linearlayout.addView(tabView);
                final EditText name = (EditText) dialog.findViewById(R.id.canvasname);
                final EditText fasj = (EditText) dialog.findViewById(R.id.fasj);
                final EditText fadz = (EditText) dialog.findViewById(R.id.fadz);
                final EditText ztdw = (EditText) dialog.findViewById(R.id.ztdw);
                final EditText ztr = (EditText) dialog.findViewById(R.id.ztr);
                final EditText ztsj = (EditText) dialog.findViewById(R.id.ztsj);
                Calendar calendar = Calendar.getInstance();
                if (_title != null) {
                    name.setText(_title);
                } else {
                    name.setText(new StringBuilder("\"").append(calendar.get(1)).append("年").append(1 + calendar.get(2)).append("月").append(calendar.get(5)).append("日\" 平面示意图").toString());
                }
                fasj.setText(infos[0]);
                fadz.setText(infos[1]);
                ztdw.setText(infos[2]);
                //ztr.setText(infos[3]);
                ztr.setText(infos[3]);
                //ztsj.setText(infos[4]);
                ztsj.setText(new StringBuilder("" + calendar.get(1)).append("年").append(1 + calendar.get(2)).append("月").append(calendar.get(5)).append("日"));
                fasj.addTextChangedListener(new TableTextWatcher(_this, linearlayout, tabView, fasj, fadz, ztdw, ztr, ztsj, 0));
                fadz.addTextChangedListener(new TableTextWatcher(_this, linearlayout, tabView, fasj, fadz, ztdw, ztr, ztsj, 24));
                ztdw.addTextChangedListener(new TableTextWatcher(_this, linearlayout, tabView, fasj, fadz, ztdw, ztr, ztsj, 24));
                ztr.addTextChangedListener(new TableTextWatcher(_this, linearlayout, tabView, fasj, fadz, ztdw, ztr, ztsj, 12));
                ztsj.addTextChangedListener(new TableTextWatcher(_this, linearlayout, tabView, fasj, fadz, ztdw, ztr, ztsj, 0));
                ((Button) dialog.findViewById(R.id.tabaffirm)).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _this.setInfo(new StringBuilder(fasj.getText().toString()).append(",")
                                .append(fadz.getText().toString()).append(",")
                                .append(ztdw.getText().toString()).append(",")
                                .append(ztr.getText().toString()).append(",")
                                .append(experModeCanvas.getTableBean().getCaseType()).append(",")
                                .append(ztsj.getText().toString()).toString());
                        _this.getExperModeCanvas().setTabInfo(_this.getInfo());
                        _this.getExperModeCanvas().setTitle(name.getText().toString());
                        _this.getMainContent().removeAllViews();
                        _this.getMainContent().addView(_this.getExperModeCanvas());
                        dialog.cancel();
                    }
                });

                //
                ((Button) dialog.findViewById(R.id.tabcancel)).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });

        //
        ((ImageButton) findViewById(R.id.textzoomin)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                experModeCanvas.zoomInTextSize();
            }
        });
        ((ImageButton) findViewById(R.id.textzoomout)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                experModeCanvas.zoomOutSize();
            }
        });

        CopyAssets("mod", Environment.getExternalStorageDirectory().getPath() + "/xckydb/mod");
        CopyAssets("db", Environment.getExternalStorageDirectory().getPath() + "/xckydb");

        dragbox = (CheckBox) findViewById(R.id.dragbox);
        scalebox = (CheckBox) findViewById(R.id.scalebox);
        rotationbox = (CheckBox) findViewById(R.id.rotationbox);
        textscalelayout = (LinearLayout) findViewById(R.id.textscalelayout);
        checkboxlayout = (LinearLayout) findViewById(R.id.checkboxlayout);
    }


    /**
     * 加载模型数据
     */
    private void loadMould(Dialog dialog) {
        createDb();
        MouldData data;
        if (_modType == 0) {
            data = new MouldData("mouldinfo", new String[]{
                    "f", "count(a) as count"
            }, null, "f desc", "f", _modType);
        } else {
            data = new MouldData("mouldinfo", new String[]{
                    "b", "count(a) as count"
            }, null, "b desc", "b", _modType);
        }
        _modList = data.getModList();
        _modListChilds = data.getModListChilds();
        ((ExpandableListView) dialog.findViewById(R.id.expandableListView)).setAdapter(new ModListAdapter(this));
    }

    /**
     * 获取用户输入的文字
     */
    public void getDrawText(XCKYPoint point) {
        final XCKYPoint cpoint = point;
        final Dialog dialog = new Dialog(this, R.style.SelectModelDialogTheme);
        dialog.setTitle("输入文字");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.drawtext_dialog);
        final EditText edittext = (EditText) dialog.findViewById(R.id.DrawEditText);
        ((Button) dialog.findViewById(R.id.SaveTextBtn)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edittext.getText().toString().equals("") && edittext.getText().toString() != null) {
                    _this.experModeCanvas.addDrawText(edittext.getText().toString(), cpoint);
                }
                dialog.cancel();
            }
        });
        dialog.show();
    }


    private void CopyAssets(String assetDir, String dir) {

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/xckydb/null");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.d("MyPictures", "创建图片存储路径目录失败");
                Log.d("MyPictures", "mediaStorageDir : " + file.getPath());
                return;
            }
        }
        String[] files;
        try {
            // 获得Assets一共有几多文件
            files = this.getResources().getAssets().list(assetDir);
        } catch (IOException e1) {
            return;
        }
        File mWorkingPath = new File(dir);
        // 如果文件路径不存在
        if (!mWorkingPath.exists()) {
            // 创建文件夹
            if (!mWorkingPath.mkdirs()) {
                // 文件夹创建不成功时调用
            }
        }
        for (int i = 0; i < files.length; i++) {
            try {
                // 获得每个文件的名字
                String fileName = files[i];
                // 根据路径判断是文件夹还是文件
                Log.d("fileName", files[i]);
                if (!fileName.contains(".")) {
                    if (0 == assetDir.length()) {
                        CopyAssets(fileName, dir + fileName + "/");
                    } else {
                        CopyAssets(assetDir + "/" + fileName, dir + "/"
                                + fileName + "/");
                    }
                    continue;
                }
                File outFile = new File(mWorkingPath, fileName);
                if (outFile.exists())
                    outFile.delete();
                InputStream in = null;
                if (0 != assetDir.length())
                    in = getAssets().open(assetDir + "/" + fileName);
                else
                    in = getAssets().open(fileName);
                OutputStream out = new FileOutputStream(outFile);
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检测数据库是否存在，不存在创建数据库
     *
     * @return
     */
    public boolean createDb() {
        String dbpath = new StringBuilder(Environment.getExternalStorageDirectory().toString()).append("/xckydb/mod.db").toString();
        if (!new File(dbpath).exists()) {
            try {
                //得到资源
                AssetManager am = this.getResources().getAssets();
                //得到数据库的输入流
                InputStream is = am.open("mod.db");
                //用输出流写到SDcard上面
                FileOutputStream fos = new FileOutputStream(dbpath);
                //创建byte数组  用于1KB写一次
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                //最后关闭就可以了
                fos.flush();
                fos.close();
                is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return true;
    }

    private byte[] InputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }

    /**
     * 读取资源图片
     *
     * @param name
     * @return
     */
    public Bitmap getImageFromAssetsFile(String name) {
        /*AssetManager assetmanager = getResources().getAssets();
        InputStream inputstream;
		Bitmap bitmap;
		String dbpath=Environment.getExternalStorageDirectory().getPath()+"/xckydb/mod";
		File file = new File(dbpath);
		if (!file.exists()){
			if (!file.mkdirs()) {
				file.mkdir();
			}
		}
		try
		{
			inputstream= assetmanager.open(file.toString());
			bitmap = BitmapFactory.decodeStream(inputstream);
			inputstream.close();
		}
		catch(IOException ioexception )
		{
			bitmap = null;
			ioexception.printStackTrace();
		}
		return bitmap;*/


        AssetManager assetmanager = getResources().getAssets();
        InputStream inputstream;
        Bitmap bitmap;
        try {
            inputstream = assetmanager.open((new StringBuilder("mod/")).append(name).toString());
            bitmap = BitmapFactory.decodeStream(inputstream);
            inputstream.close();
        } catch (IOException ioexception) {
            bitmap = null;
            ioexception.printStackTrace();
        }
        return bitmap;
    }

    private static final int AIRPLAY_MESSAGE_HIDE_TOAST = 1;
    private Handler _mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AIRPLAY_MESSAGE_HIDE_TOAST:
                    cancelToast();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 提示
     *
     * @param paramString
     */
    public void showToast(String msg) {
        float time = 0.8f;
        if (_mToast == null) {
            _mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else {
            _mToast.setText(msg);
            _mToast.setDuration(Toast.LENGTH_SHORT);
        }
        _mToast.show();
        //隐藏
        _mHandler.sendMessageDelayed(_mHandler.obtainMessage(AIRPLAY_MESSAGE_HIDE_TOAST), (int) (time * 1000));
    }

    /**
     * 取消提示
     *
     * @param paramString
     */
    private void cancelToast() {
        if (_mToast != null) {
            _mToast.cancel();
        }
    }

    /**
     * 保存
     */
    public void onBackPressed() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        //localBuilder.setTitle("提示");
        localBuilder.setMessage("是否保存当前画图内容?");
        localBuilder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int arg1) {
                ExperModeActivity.this.finish();
            }
        });
        localBuilder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int arg1) {

                if (ExperModeActivity.this.experModeCanvas.getData().size() < 1) {
                    ExperModeActivity.this.finish();
                    return;
                }
                DisplayMetrics dm = new DisplayMetrics();
                ExperModeActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
                int w = dm.widthPixels - 30;
                int h = dm.heightPixels - 125;
                ContentValues contentvalues = new ContentValues();
                Bitmap bitmap = _this.experModeCanvas.getBitMap(w, h);
                contentvalues.put("CASE_ID", _projectID);
//				contentvalues.put("PRJ_DATA", Serializer.encode(Bitmap.createScaledBitmap(bitmap, 485, 292, true), 60));
//				contentvalues.put("Blob", Serializer.encode(_this.experModeCanvas.getData().get(_this.experModeCanvas.getData().size()-1)));
                contentvalues.put("TYPE", 3);
                DataBase db = new DataBase();
                Cursor cursor = db.query("SCENE_PICTURE", new String[]{
                        "PICTURE_NAME"
                }, new StringBuilder("ID=").append(_picID).toString(), null, null);
                if (cursor.moveToNext()) {
                    if (db.udpate("SCENE_PICTURE", contentvalues, new StringBuilder("ID=").append(_picID).toString()) < 1) {
                        _this.showToast("数据更新失败");
                        db.close();
                        return;
                    } else {
                        //保存图片
                        String picname = cursor.getString(0);
                        File picfile = new File((new StringBuilder("/sdcard/xckydb/")).append(_projectID).append("/").append(picname).toString());
                        File cfile = new File((new StringBuilder("/sdcard/xckydb/")).append(_projectID).append("/").toString());

                        if (!cfile.exists()) {
                            cfile.mkdir();
                        }
                        try {
                            FileOutputStream outputstream = new FileOutputStream(picfile);
                            BufferedOutputStream bufferotstream = new BufferedOutputStream(outputstream);
                            bufferotstream.write(Serializer.encode(bitmap, 80));
                            bufferotstream.close();
                            outputstream.close();
                        } catch (IOException exception) {
                            db.close();
                            _this.showToast("图片更新失败");
                            exception.printStackTrace();
                            return;
                        }

                        //保存工程
//						String prjname=picname+".plan";
                        File prjfile = new File(picfile.getAbsolutePath() + ".plan");
                        try {
                            FileOutputStream outputstream = new FileOutputStream(prjfile);
                            BufferedOutputStream bufferotstream = new BufferedOutputStream(outputstream);
                            bufferotstream.write(Serializer.encode(_this.experModeCanvas.getData().get(_this.experModeCanvas.getData().size() - 1)));
                            bufferotstream.close();
                            outputstream.close();
                        } catch (IOException exception) {
                            db.close();
                            _this.showToast("图片数据更新失败");
                            exception.printStackTrace();
                            return;
                        }
                    }
                } else {
                    Log.d("teatplan", "teatplan");
                    String fname = new StringBuilder(Md5.getMD5(new StringBuilder(_projectID)
                            .append(new Date().toString()).toString())).append(".png").toString();
                    File picfile = null;
                    if (storedGatherpath != null && !storedGatherpath.equals("")) {
                        picfile = new File((storedGatherpath.replace(".plan", "")));
                    } else {
                        picfile = new File((new StringBuilder("/sdcard/xckydb/")).append(caseId).append("/").append(fname).toString());
                    }
                    File cfile = new File((new StringBuilder("/sdcard/xckydb/")).append(caseId).toString());
                    if (!cfile.exists()) {
                        cfile.mkdir();
                    }
                    try {
                        FileOutputStream outputstream = new FileOutputStream(picfile);
                        BufferedOutputStream bufferotstream = new BufferedOutputStream(outputstream);
                        bufferotstream.write(Serializer.encode(bitmap, 80));
                        bufferotstream.close();
                        outputstream.close();
                    } catch (Exception exception) {
                        db.close();
                        _this.showToast("图片保存失败");
                        exception.printStackTrace();
                        return;
                    }
                    //保存工程
//					String prjname=fname+".plan";
                    File prjfile = new File(picfile.getAbsolutePath() + ".plan");
                    try {
                        FileOutputStream outputstream = new FileOutputStream(prjfile);
                        BufferedOutputStream bufferotstream = new BufferedOutputStream(outputstream);
                        bufferotstream.write(Serializer.encode(_this.experModeCanvas.getData().get(_this.experModeCanvas.getData().size() - 1)));
                        bufferotstream.close();
                        outputstream.close();
                    } catch (IOException exception) {
                        db.close();
                        _this.showToast("图片数据保存失败");
                        exception.printStackTrace();
                        return;
                    }

                    contentvalues.put("PICTURE_NAME", fname);
                    if (db.insert("SCENE_PICTURE", "ID", contentvalues) < 1) {
                        db.close();
                        _this.showToast("数据保存失败");
                        return;
                    }
                }
                cursor.close();
                db.close();
                ExperModeActivity.this.finish();
                return;
            }
        });
        localBuilder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public List<String> getModList() {
        return _modList;
    }

    public void setModList(List<String> _modList) {
        this._modList = _modList;
    }

    public List<ArrayList<String>> getModListChilds() {
        return _modListChilds;
    }

    public void setModListChilds(List<ArrayList<String>> _modListChilds) {
        this._modListChilds = _modListChilds;
    }

    public LinearLayout getMainContent() {
        return _mainContent;
    }

    public LinearLayout getMouldSelLinear() {
        return _mouldSelLinear;
    }

    public void setMouldSelLinear(LinearLayout mouldSelLinear) {
        _mouldSelLinear = mouldSelLinear;
    }


    public LinearLayout getSelEdMouldLinear() {
        return _selEdMouldLinear;
    }

    public void setSelEdMouldLinear(LinearLayout selMouldLinear) {
        this._selEdMouldLinear = selMouldLinear;
    }


    public float getSelModX() {
        return _selModX;
    }

    public void setSelModX(float selModX) {
        this._selModX = selModX;
    }

    public ExperModeCanvas getExperModeCanvas() {
        return experModeCanvas;
    }

    public void setExperModeCanvas(ExperModeCanvas experModeCanvas) {
        this.experModeCanvas = experModeCanvas;
    }


    public String getInfo() {
        return _info;
    }


    public void setInfo(String _info) {
        this._info = _info;
    }


    /**
     * @author Administrator
     */
    public class TableTextWatcher implements TextWatcher {
        ExperModeActivity _main;
        LinearLayout _layout;
        TableInfoView _view;
        int fontnum = 0;
        EditText name;
        EditText fasj;
        EditText fadz;
        EditText ztdw;
        EditText ztr;
        EditText ztsj;

        public TableTextWatcher(ExperModeActivity main, LinearLayout layout, TableInfoView view,
                                EditText fasj,
                                EditText fadz,
                                EditText ztdw,
                                EditText ztr,
                                EditText ztsj,
                                int fontnum) {
            _main = main;
            _layout = layout;
            _view = view;
            this.fontnum = fontnum;
            this.fasj = fasj;
            this.fadz = fadz;
            this.ztdw = ztdw;
            this.ztr = ztr;
            this.ztsj = ztsj;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (fontnum != 0 && editable.length() > fontnum) {
                _main.showToast("信息长度不能超过" + fontnum + "个字符");
            }
            _layout.removeAllViews();
            _view.setInfo(new StringBuilder(fasj.getText().toString()).append(",")
                    .append(fadz.getText().toString()).append(",")
                    .append(ztdw.getText().toString()).append(",")
                    .append(ztr.getText().toString()).append(",")
                    .append(ztsj.getText().toString()).toString());
            _layout.addView(_view);
            return;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }
    }


    private byte[] fileToBytes(File file) {
        byte[] buffer = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;

        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();

            byte[] b = new byte[1024];

            int n;

            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }

            buffer = bos.toByteArray();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            try {
                if (null != bos) {
                    bos.close();
                }
            } catch (IOException ex) {
            } finally {
                try {
                    if (null != fis) {
                        fis.close();
                    }
                } catch (IOException ex) {
                }
            }
        }

        return buffer;
    }
}
