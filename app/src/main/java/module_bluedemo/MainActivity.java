package module_bluedemo;

import android.app.Application;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.bth.api.cls.Comm_Bluetooth;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.silionmodule.DataListener;
import com.silionmodule.Functional;
import com.silionmodule.ParamNames;
import com.silionmodule.ReaderException;
import com.silionmodule.SimpleReadPlan;
import com.silionmodule.StatusEventListener;
import com.silionmodule.TAGINFO;
import com.silionmodule.TagProtocol.TagProtocolE;
import com.silionmodule.TagReadData;
import com.tool.log.LogD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends TabActivity { // ActionBarActivity

	private final int ThreadMODE = 1;
	// 读线程：
	private Thread runThread;
	boolean isrun;
	TextView tv_once, tv_state, tv_tags,tv_cost;
	ExpandableListView tab4_left, tab4_right;

	Button button_read, button_stop, button_clear;
	private ListView listView;

	Map<String, TAGINFO> TagsMap= new LinkedHashMap<String, TAGINFO>();// 有序
	private Handler handler = new Handler();
	private EvidenceApplication myapp;
	private SoundPool soundPool;
	boolean isreading;
	//	RadioGroup gr_match;
	public static TabHost tabHost;
	public static TabSpec tab1, tab2;

	Lock lockobj = new ReentrantLock();// 锁
	String[] Coname;

	List<Map<String, ?>> ListMs = new ArrayList<Map<String, ?>>();
	MyAdapter Adapter;

	public class MyEpListAdapter extends ArrayAdapter {

		public MyEpListAdapter(Context context, int resource,
							   int textViewResourceId, List objects) {
			super(context, resource, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
		}

	}

	public enum Region_Conf {
		RG_NONE(0x0), RG_NA(0x01), RG_EU(0x02), RG_EU2(0X07), RG_EU3(0x08), RG_KR(
				0x03), RG_PRC(0x06), RG_PRC2(0x0A), RG_OPEN(0xFF);

		int p_v;

		Region_Conf(int v) {
			p_v = v;
		}

		public int value() {
			return this.p_v;
		}

		public static Region_Conf valueOf(int value) { // 手写的从int到enum的转换函数
			switch (value) {
				case 0:
					return RG_NONE;
				case 1:
					return RG_NA;
				case 2:
					return RG_EU;
				case 7:
					return RG_EU2;
				case 8:
					return RG_EU3;
				case 3:
					return RG_KR;
				case 6:
					return RG_PRC;
				case 0x0A:
					return RG_PRC2;
				case 0xff:
					return RG_OPEN;
			}
			return null;
		}
	}

	public Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0: {
					// showlist();
					Adapter.notifyDataSetChanged();
					Bundle bd = msg.getData();
					TextView et = (TextView) findViewById(R.id.textView_readoncecnt);
					et.setText(String.valueOf(bd.get("OnceCount")));
					TextView et2 = (TextView) findViewById(R.id.textView_readallcnt);
					et2.setText(String.valueOf(TagsMap.size()));

					break;
				}
				case 1: {
					Bundle bd = msg.getData();

					TextView et = (TextView) findViewById(R.id.textView_invstate);
					if (et != null)
						et.setText(" " + bd.get("Msg"));

					if (myapp.CommBth.ConnectState() != Comm_Bluetooth.CONNECTED) {
						if (et != null)
							et.setText("DISCONNECT...RECONNECT...");
						myapp.CommBth.ReConnect();

					}
					break;
				}
			}
		}
	};

	public Handler handler3 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle bd = msg.getData();
			switch (msg.what) {
				case 0: {
					String count = bd.get("Msg_cnt").toString();
					tv_once.setText(count);
					tv_tags.setText(bd.get("Msg_all").toString());
					tv_cost.setText(bd.get("Msg_time").toString());
					Adapter.notifyDataSetChanged();
					if (ListMs.size() != 0) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									Thread.sleep(2000);
									Intent resultIntent = new Intent();
									Bundle bundle = new Bundle();
									String epId = (String) ListMs.get(1).get(Coname[1]);
									bundle.putString("result", epId);
									resultIntent.putExtras(bundle);
									String saveKey = getIntent().getStringExtra("saveKey");
									resultIntent.setAction(saveKey);
									sendBroadcast(resultIntent);
									Handler handler = new Handler(getMainLooper(), new Callback() {
										@Override
										public boolean handleMessage(Message msg) {
											if (msg.what == 5)
												finish();
											return false;
										}
									});
									handler.sendEmptyMessage(5);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}

						}).start();
					}
					break;

				}
				case 1: {
					button_read.setText(EvidenceApplication.Constr_READ);
					tv_state.setText(bd.get("Msg_error_1").toString());
					break;
				}
				case 2: {
					tv_state.setText(bd.get("Msg_error_2").toString());
					break;
				}

			}
		}
	};

	StatusEventListener SL = new StatusEventListener() {

		@Override
		public void StatusCatch(Object t) {
			// TODO Auto-generated method stub

			Message msg = new Message();
			msg.what = 1;
			Bundle bundle = new Bundle();
			bundle.putString("Msg", (String) t);
			msg.setData(bundle);
			// 发送消息到Handler
			handler2.sendMessage(msg);
		}

	};

	DataListener DL = new DataListener() {

		@Override
		public void ReadData(TagReadData[] t) {
			// TODO Auto-generated method stub

			TagReadData[] trds = t;
			if (trds != null && trds.length > 0) {
				soundPool.play(1, 1, 1, 0, 0, 1);
				for (int i = 0; i < trds.length; i++) {
					if (!TagsMap.containsKey(trds[i].EPCHexstr())) {
						TAGINFO Ti = new TAGINFO();
						Ti.AntennaID = (byte) trds[i].Antenna();
						Ti.CRC = trds[i].CRC();
						Ti.EmbededData = trds[i].AData();
						Ti.EmbededDatalen = (short) trds[i].AData().length;
						Ti.EpcId = trds[i].EPCbytes();
						Ti.Epclen = (short) trds[i].EPCbytes().length;
						Ti.Frequency = trds[i].Frequency();
						Ti.PC = trds[i].PC();
						Ti.protocol = -1;
						Ti.ReadCnt = trds[i].ReadCount();
						Ti.RSSI = trds[i].RSSI();
						Ti.TimeStamp = (int) trds[i].Time().getTime();
						TagsMap.put(trds[i].EPCHexstr(), Ti);

						// list
						Map<String, String> m = new HashMap<String, String>();
						m.put(Coname[0], String.valueOf(TagsMap.size()));

						String epcstr = Functional.bytes_Hexstr(Ti.EpcId);
						if (epcstr.length() < 24)
							epcstr = String.format("%-24s", epcstr);

						m.put(Coname[1], epcstr);
						String cs = m.get("次数");
						if (cs == null)
							cs = "0";
						int isc = Integer.parseInt(cs) + Ti.ReadCnt;

						m.put(Coname[2], String.valueOf(isc));
						m.put(Coname[3],
								String.valueOf(Ti.AntennaID));
						m.put(Coname[4], "");
						m.put(Coname[5], String.valueOf(Ti.RSSI));
						m.put(Coname[6],
								String.valueOf(Ti.Frequency));

						if (Ti.EmbededDatalen > 0) {
							byte[] out = new byte[Ti.EmbededDatalen];
							System.arraycopy(Ti.EmbededData, 0, out, 0, Ti.EmbededDatalen);

							m.put(Coname[7], Functional.bytes_Hexstr(out));
						} else
							m.put(Coname[7], "                 ");

						ListMs.add(m);

					} else {
						TAGINFO tf = TagsMap.get(trds[i].EPCHexstr());
						tf.ReadCnt += trds[i].ReadCount();
						tf.RSSI = trds[i].RSSI();
						tf.Frequency = trds[i].Frequency();

						String epcstr = trds[i]
								.EPCHexstr();
						if (epcstr.length() < 24)
							epcstr = String.format("%-24s", epcstr);

						for (int k = 0; k < ListMs.size(); k++) {
							@SuppressWarnings("unchecked")
							Map<String, String> m = (Map<String, String>) ListMs
									.get(k);
							if (m.get(Coname[1]).equals(epcstr)) {

								m.put(Coname[2],
										String.valueOf(tf.ReadCnt));
								m.put(Coname[5],
										String.valueOf(tf.RSSI));
								m.put(Coname[6],
										String.valueOf(tf.Frequency));
								break;
							}
						}
					}
				}
			}

			Message msg = new Message();
			msg.what = 0;
			Bundle bundle = new Bundle();
			bundle.putInt("OnceCount", trds.length);
			msg.setData(bundle);
			// 发送消息到Handler
			handler2.sendMessage(msg);
		}

	};

	MyEpListAdapter mttab1adp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(1);
		setContentView(R.layout.activity_main_rfid);

		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);

		soundPool.load(this, R.raw.beep, 1);

		tabHost = (TabHost) findViewById(android.R.id.tabhost);

		tabHost.setup();
		tab1 = tabHost
				.newTabSpec("tab1")
				.setIndicator(EvidenceApplication.Constr_CONNECT,
						getResources().getDrawable(R.drawable.ic_launcher))
				.setContent(new Intent(this, Sub1TabActivity.class));
		tabHost.addTab(tab1);
		tab2 = tabHost.newTabSpec("tab2").setIndicator(EvidenceApplication.Constr_INVENTORY)
				.setContent(R.id.tab2);
		tabHost.addTab(tab2);


		tabHost.setCurrentTab(0);
		TabWidget tw = tabHost.getTabWidget();
		tw.getChildAt(1).setVisibility(View.INVISIBLE);

		/*
		 * Region_Conf rcf1=Region_Conf.valueOf(Integer.valueOf("8")); byte[]
		 * data=new byte[1]; data[0]=(byte)((Region_Conf)rcf1).value();
		 * System.out.println(String.valueOf(data[0]));
		 */

		Application app = getApplication();
		myapp = (EvidenceApplication) app;
		Coname= EvidenceApplication.Coname;
		myapp.Rparams = myapp.new ReaderParams();
		myapp.tabHost = tabHost;
		/*
		 * spinner_opbank= (Spinner)findViewById(R.id.spinner_opfbank);
		 * arradp_opbank = new
		 * ArrayAdapter<String>(this,android.R.layout.simple_spinner_item
		 * ,spibank); arradp_opbank.setDropDownViewResource(android.R.layout.
		 * simple_spinner_dropdown_item);
		 * spinner_opbank.setAdapter(arradp_opbank);
		 */

		button_read = (Button) findViewById(R.id.button_start);
		button_stop = (Button) findViewById(R.id.button_stop);
		button_stop.setEnabled(false);
		button_clear = (Button) findViewById(R.id.button_readclear);

		listView = (ListView) findViewById(R.id.listView_epclist);
//		gr_match = (RadioGroup) findViewById(R.id.radioGroup_opmatch);

		tv_once = (TextView) findViewById(R.id.textView_readoncecnt);
		tv_state = (TextView) findViewById(R.id.textView_invstate);
		tv_tags = (TextView) findViewById(R.id.textView_readallcnt);
		tv_cost = (TextView) findViewById(R.id.textView_cost);
		for (int i = 0; i < Coname.length; i++)
			h.put(Coname[i], Coname[i]);

		button_read.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {

					if(Adapter==null)
					{
						Map<String, String> h = new HashMap<String, String>();
						for(int i=0;i<Coname.length;i++)
							h.put(Coname[i], Coname[i]);
						ListMs.add(h);
						Adapter  = new MyAdapter(getApplicationContext(),ListMs,
								R.layout.listitemview_inv, Coname,new int[] { R.id.textView_readsort,
								R.id.textView_readepc, R.id.textView_readcnt,R.id.textView_readant,
								R.id.textView_readpro,R.id.textView_readrssi,R.id.textView_readfre,
								R.id.textView_reademd});

						listView.setAdapter(Adapter);
					}
					myapp.Mreader.addStatusListener(SL);
					if (myapp.Mode == 0) {
						SimpleReadPlan srp = new SimpleReadPlan(myapp.Rparams.uants);

						try {

							if(myapp.Rparams.To!=null||myapp.Rparams.Tf!=null)
							{
								srp = new SimpleReadPlan(myapp.Rparams.uants,
										TagProtocolE.Gen2, myapp.Rparams.Tf,
										myapp.Rparams.To, 10);
							}

							myapp.Mreader.paramSet(
									ParamNames.Reader_Read_Plan, srp);

							/* 取消检测天线 */
							myapp.Mreader.paramSet(
									ParamNames.Reader_Antenna_CheckPort, false);

							if (ThreadMODE == 0)
								handler.postDelayed(runnable_MainActivity, 0);
							else {
								isrun = true;
								runThread = new Thread(runnable_alone);
								runThread.start();
							}

							isreading = true;
							myapp.isread = true;
							ReadHandleUI();

						} catch (ReaderException e) {
							// TODO Auto-generated catch block
							/*Toast.makeText(MainActivity.this, EvidenceApplication.Constr_SetFaill+e.GetMessage(),
									Toast.LENGTH_SHORT).show();*/
							return;
						}
					} else if (myapp.Mode == 1) {
						try {
							myapp.Mreader.addDataListener(DL);
							myapp.Mreader.StartTagEvent();
							myapp.isread = true;
							isreading = true;
							ReadHandleUI();
						} catch (ReaderException e) {
							// TODO Auto-generated catch block
							/*Toast.makeText(MainActivity.this, EvidenceApplication.Constr_SetFaill+e.GetMessage(),
									Toast.LENGTH_SHORT).show();*/
							return;
						}
					}
				} catch (Exception ex) {
					/*Toast.makeText(MainActivity.this, EvidenceApplication.Constr_SetFaill+ex.getMessage(),
							Toast.LENGTH_SHORT).show();*/
				}
			}

		});

		button_stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				StopHandle();
				myapp.isread = false;

			}
		});

		button_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(Adapter!=null)
				{
					TagsMap.clear();
					ListMs.clear();
					// showlist();

					ListMs.add(h);
					Adapter.notifyDataSetChanged();
				}

				TextView et = (TextView) findViewById(R.id.textView_readoncecnt);
				et.setText("0");

				TextView et2 = (TextView) findViewById(R.id.textView_readallcnt);
				et2.setText("0");

				TextView et3 = (TextView) findViewById(R.id.textView_invstate);
				et3.setText("...");

				TextView et4 = (TextView) findViewById(R.id.textView_cost);
				et4.setText("0");

				myapp.Rparams.Curepc = "";
			}
		});
		this.listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				arg1.setBackgroundColor(Color.YELLOW);

//				EditText et = (EditText) findViewById(R.id.editText_opfilterdata);
//				EditText et2 = (EditText) findViewById(R.id.editText_opfilsadr);
				HashMap<String, String> hm = (HashMap<String, String>) listView
						.getItemAtPosition(arg2);
				String epc = hm.get("EPC ID");
				myapp.Rparams.Curepc = epc;
				// et.setText(epc);
				// et2.setText("32");
				// gr_match.check(gr_match.getChildAt(0).getId());
				// spinner_opbank.setSelection(1);

				for (int i = 0; i < listView.getCount(); i++) {
					if (i != arg2) {
						View v = listView.getChildAt(i);
						if (v != null) {
							ColorDrawable cd = (ColorDrawable) v
									.getBackground();
							if (Color.YELLOW == cd.getColor()) {
								int[] colors = { Color.WHITE,
										Color.rgb(219, 238, 244) };// RGB颜色
								v.setBackgroundColor(colors[i % 2]);// 每隔item之间颜色不同
							}
						}
						else
						{
							break;
						}
					}
				}
			}

		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - myapp.exittime) > 2000) {
				Toast.makeText(getApplicationContext(), EvidenceApplication.Constr_Putandexit,
						Toast.LENGTH_SHORT).show();
				myapp.exittime = System.currentTimeMillis();
			} else {
				finish();
				// System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	void StopHandle() {
		isreading = false;
		if(myapp.Mreader!=null)
			myapp.Mreader.removeStatusListener(SL);
		if (myapp.Mode == 0) {
			if(ThreadMODE==0)
				handler.removeCallbacks(runnable_MainActivity);
			else
			{
				isrun=false;
				try {
					if(runThread!=null)
						runThread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			StopHandleUI();
		} else {
			try {
				if(myapp.Mreader!=null)
				{myapp.Mreader.EndTagEvent();
					myapp.Mreader.removeDataListener(DL);
				}
				StopHandleUI();
			} catch (ReaderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	Map<String, String> h = new HashMap<String, String>();
	private void showlist() {

		Iterator<Entry<String, TAGINFO>> iesb;
		synchronized (this)
		{
			Map<String, TAGINFO> TagsMap2=new LinkedHashMap<String, TAGINFO>();
			TagsMap2.putAll(TagsMap);
			iesb=TagsMap2.entrySet().iterator();
		}
		int j=1;
		ListMs.add(h);

		while (iesb.hasNext()) {
			TAGINFO bd = iesb.next().getValue();
			Map<String, String> m = new HashMap<String, String>();
			m.put(Coname[0], String.valueOf(j));
			j++;

			m.put(Coname[1], Functional.bytes_Hexstr(bd.EpcId));
			String cs = m.get("次数");
			if (cs == null)
				cs = "0";
			int isc = Integer.parseInt(cs) + bd.ReadCnt;

			m.put(Coname[2], String.valueOf(isc));
			m.put(Coname[3], String.valueOf(bd.AntennaID));

			m.put(Coname[4], "");
			m.put(Coname[5], String.valueOf(bd.RSSI));
			m.put(Coname[6], String.valueOf(bd.Frequency));

			if (bd.EmbededDatalen > 0)
				m.put(Coname[7], Functional.bytes_Hexstr(bd.EmbededData));
			else
				m.put(Coname[7], "                 ");

			ListMs.add(m);
		}
		///*
		Adapter  = new MyAdapter(this,ListMs,
				R.layout.listitemview_inv, Coname,new int[] { R.id.textView_readsort,
				R.id.textView_readepc, R.id.textView_readcnt,R.id.textView_readant,
				R.id.textView_readpro,R.id.textView_readrssi,R.id.textView_readfre,R.id.textView_reademd});


		// layout为listView的布局文件，包括三个TextView，用来显示三个列名所对应的值
		// ColumnNames为数据库的表的列名
		// 最后一个参数是int[]类型的，为view类型的id，用来显示ColumnNames列名所对应的值。view的类型为TextView
		listView.setAdapter(Adapter);

		//*/
	}

	private Runnable runnable_MainActivity = new Runnable() {
		public void run() {

			synchronized (this) {
				if (isreading) {

					if (myapp.CommBth.ConnectState() == Comm_Bluetooth.CONNECTED) {
						try {
							TagReadData[] trds = myapp.Mreader
									.Read(myapp.Rparams.readtime);

							if (trds != null && trds.length > 0) {
								lockobj.lock();
								soundPool.play(1, 1, 1, 0, 0, 1);
								for (int i = 0; i < trds.length; i++) {
									if (!TagsMap.containsKey(trds[i]
											.EPCHexstr())) {
										TAGINFO Ti = new TAGINFO();
										Ti.AntennaID = (byte) trds[i].Antenna();
										Ti.CRC = trds[i].CRC();
										Ti.EmbededData = trds[i].AData();
										Ti.EmbededDatalen = (short) trds[i]
												.AData().length;
										Ti.EpcId = trds[i].EPCbytes();
										Ti.Epclen = (short) trds[i].EPCbytes().length;
										Ti.Frequency = trds[i].Frequency();
										Ti.PC = trds[i].PC();
										Ti.protocol = -1;
										Ti.ReadCnt = trds[i].ReadCount();
										Ti.RSSI = trds[i].RSSI();
										Ti.TimeStamp = (int) trds[i].Time()
												.getTime();
										TagsMap.put(trds[i].EPCHexstr(), Ti);
									} else {
										TAGINFO tf = TagsMap.get(trds[i]
												.EPCHexstr());
										tf.ReadCnt += trds[i].ReadCount();
										tf.RSSI = trds[i].RSSI();
										tf.Frequency = trds[i].Frequency();
									}
								}
								lockobj.unlock();
							}

							TextView et = (TextView) findViewById(R.id.textView_readoncecnt);
							et.setText(String.valueOf(trds.length));

							if(trds!=null&&trds.length>0)
								Adapter.notifyDataSetChanged();

						} catch (ReaderException rex) {
							TextView et = (TextView) findViewById(R.id.textView_invstate);
							et.setText("error:" + rex.GetMessage());
							handler.postDelayed(this, myapp.Rparams.sleep);
							return;
						} catch (Exception ex) {
							TextView et = (TextView) findViewById(R.id.textView_invstate);
							et.setText("error:" + ex.toString()
									+ ex.getMessage());
							handler.postDelayed(this, myapp.Rparams.sleep);
							return;
						}

					}
				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				TextView et = (TextView) findViewById(R.id.textView_readallcnt);
				et.setText(String.valueOf(listView.getCount() - 1));
				handler.postDelayed(this, myapp.Rparams.sleep);
			}
		}
	};

	private Runnable runnable_alone = new Runnable() {
		public void run() {

			while (isrun) {
				synchronized (this) {
					if (isreading) {
						if (myapp.CommBth.ConnectState() == Comm_Bluetooth.CONNECTED) {
							TagReadData[] trds = null;
							long st = System.currentTimeMillis();
							try {
								trds = myapp.Mreader
										.Read(myapp.Rparams.readtime);
								/*
								LogD.LOGD("read cost time:"
										+ String.valueOf(System
												.currentTimeMillis() - st));
												*/
								if (trds != null && trds.length > 0) {

									soundPool.play(1, 1, 1, 0, 0, 1);
									for (int i = 0; i < trds.length; i++) {
										if (!TagsMap.containsKey(trds[i]
												.EPCHexstr())) {
											TAGINFO Ti = new TAGINFO();
											Ti.AntennaID = (byte) trds[i]
													.Antenna();
											Ti.CRC = trds[i].CRC();
											Ti.EmbededData = trds[i].AData();
											Ti.EmbededDatalen = (short) trds[i]
													.AData().length;
											Ti.EpcId = trds[i].EPCbytes();
											Ti.Epclen = (short) trds[i]
													.EPCbytes().length;
											Ti.Frequency = trds[i].Frequency();
											Ti.PC = trds[i].PC();
											Ti.protocol = -1;
											Ti.ReadCnt = trds[i].ReadCount();
											Ti.RSSI = trds[i].RSSI();
											Ti.TimeStamp = (int) trds[i].Time()
													.getTime();
											TagsMap.put(trds[i].EPCHexstr(), Ti);

											// list
											Map<String, String> m = new HashMap<String, String>();
											m.put(Coname[0], String.valueOf(TagsMap.size()));

											String epcstr = Functional.bytes_Hexstr(Ti.EpcId);
											if (epcstr.length() < 24)
												epcstr = String.format("%-24s", epcstr);

											m.put(Coname[1], epcstr);
											String cs = m.get("次数");
											if (cs == null)
												cs = "0";
											int isc = Integer.parseInt(cs) + Ti.ReadCnt;

											m.put(Coname[2], String.valueOf(isc));
											m.put(Coname[3],
													String.valueOf(Ti.AntennaID));
											m.put(Coname[4], "");
											m.put(Coname[5], String.valueOf(Ti.RSSI));
											m.put(Coname[6],
													String.valueOf(Ti.Frequency));

											if (Ti.EmbededDatalen > 0) {
												byte[] out = new byte[Ti.EmbededDatalen];
												System.arraycopy(Ti.EmbededData, 0, out, 0, Ti.EmbededDatalen);

												m.put(Coname[7], Functional.bytes_Hexstr(out));
											} else
												m.put(Coname[7], "                 ");

											ListMs.add(m);

										} else {
											TAGINFO tf = TagsMap.get(trds[i]
													.EPCHexstr());
											tf.ReadCnt += trds[i].ReadCount();
											tf.RSSI = trds[i].RSSI();
											tf.Frequency = trds[i].Frequency();
											tf.AntennaID = (byte) trds[i]
													.Antenna();

											String epcstr = trds[i]
													.EPCHexstr();
											if (epcstr.length() < 24)
												epcstr = String.format("%-24s", epcstr);

											for (int k = 0; k < ListMs.size(); k++) {
												@SuppressWarnings("unchecked")
												Map<String, String> m = (Map<String, String>) ListMs
														.get(k);
												if (m.get(Coname[1]).equals(epcstr)) {

													m.put(Coname[2],
															String.valueOf(tf.ReadCnt));
													m.put(Coname[5],
															String.valueOf(tf.RSSI));
													m.put(Coname[6],
															String.valueOf(tf.Frequency));
													break;
												}
											}
										}

										// 过滤读tid----------------
										/*
										int count=0;
										while(true)
										{ try {
											byte[] rdata = new byte[12];
											Gen2TagFilter g2tf=null;
											byte[] rpaswd = new byte[4];
											byte[] fdata = Functional.hexstr_Bytes(trds[i].EPCHexstr());

											g2tf = new Gen2TagFilter(MemBankE.EPC,
													 32, fdata, fdata.length*8);
											myapp.Mreader.paramSet(ParamNames.Reader_Tagop_Antenna
													,trds[i].Antenna());
											short[] epddata=myapp.Mreader.ReadTagMemWords(g2tf,
													MemBankE.TID, 0,4);

											TAGINFO tf2 = TagsMap.get(trds[i]
													.EPCHexstr());
											tf2.EmbededData=Functional.hexstr_Bytes(Functional.shorts_HexStr(epddata));
                                            tf2.EmbededDatalen=(short) tf2.EmbededData.length;
										} catch (ReaderException ex) {
											LogD.LOGD(ex.GetMessage());
										}
										if(count++>3)
											break;
										}
									 	//*/
									}

								}
							} catch (ReaderException rex) {
								Message msg2 = new Message();
								msg2.what = 1;
								Bundle bundle2 = new Bundle();
								bundle2.putString("Msg_error_1",
										"error:" + rex.GetMessage());
								msg2.setData(bundle2);
								handler3.sendMessage(msg2);
								//isrun = false;
								LogD.LOGD(rex.GetMessage());

							}
							catch (Exception ex) {

								Message msg = new Message();
								msg.what = 2;
								Bundle bundle = new Bundle();
								bundle.putString(
										"Msg_error_2",
										"error:" + ex.toString()
												+ ex.getMessage());
								msg.setData(bundle);
								LogD.LOGD(ex.toString()
										+ ex.getMessage());
								handler3.sendMessage(msg);
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								continue;

							}


							if (trds != null && trds.length > 0) {
								Message msg = new Message();
								Bundle bundle = new Bundle();
								msg.what = 0;
								bundle.putString("Msg_cnt",
										(String.valueOf(trds.length)));
								int vl = (int) (System.currentTimeMillis() - st);
								bundle.putString("Msg_time",
										(String.valueOf(vl)));
								synchronized (this) {
									bundle.putString("Msg_all",
											(String.valueOf(TagsMap.size())));
								}
								msg.setData(bundle);
								handler3.sendMessage(msg);
							}
							try {
								Thread.sleep(myapp.Rparams.sleep);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} else {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (myapp.isread) {
			Toast.makeText(MainActivity.this, EvidenceApplication.Constr_stopscan, Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		int id = item.getItemId();

//		if (id == R.id.action_debug) {
//
//			if (myapp.m != null
//					&& myapp.CommBth.ConnectState() == Comm_Bluetooth.CONNECTED) {
//				Intent intent = new Intent(MainActivity.this,
//						SubDebugActivity.class);
//				startActivityForResult(intent, 0);
//				return true;
//			}
//			Toast.makeText(MainActivity.this, MyApplication.Constr_scanselectabluereaderandconnect,
//					Toast.LENGTH_SHORT).show();
//			return false;
//		} else if (id == R.id.action_system) {
//			if (myapp.m != null) {
//				Intent intent = new Intent(MainActivity.this,
//						SubSystemActivity.class);
//				startActivityForResult(intent, 0);
//				return true;
//			}
//
//			Toast.makeText(MainActivity.this, MyApplication.Constr_scanselectabluereader,
//					Toast.LENGTH_SHORT).show();
//			return true;
//		}
//		 else if (id == R.id.action_custom) {
//			 if (myapp.m != null
//						&& myapp.CommBth.ConnectState() == Comm_Bluetooth.CONNECTED) {
//					Intent intent = new Intent(MainActivity.this,
//							SubCustomActivity.class);
//					startActivityForResult(intent, 0);
//					return true;
//				}
//				Toast.makeText(MainActivity.this, MyApplication.Constr_scanselectabluereaderandconnect,
//						Toast.LENGTH_SHORT).show();
//				return false;
//			}
		return super.onOptionsItemSelected(item);
	}



	private void ReadHandleUI() {
		this.button_read.setEnabled(false);
		this.button_stop.setEnabled(true);
		TabWidget tw = myapp.tabHost.getTabWidget();
		tw.getChildAt(0).setEnabled(false);
		tw.getChildAt(2).setEnabled(false);
		if (myapp.Mode == 0)
			tw.getChildAt(3).setEnabled(false);

	}

	private void StopHandleUI() {
		button_read.setEnabled(true);
		button_stop.setEnabled(false);
		TabWidget tw = myapp.tabHost.getTabWidget();
		tw.getChildAt(0).setEnabled(true);
		if (tw.getChildCount() > 2)
			tw.getChildAt(2).setEnabled(true);
		if (tw.getChildCount() > 3 && myapp.Mode == 0)
			tw.getChildAt(3).setEnabled(true);
	}

	/*
	 * protected void onPause() {
	 *
	 * long now=System.currentTimeMillis();
	 * if(!(myapp.exittime<now&&now-myapp.exittime<2000)) { myapp.exittime=now;
	 * Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
	 * return; }
	 * 
	 * super.onPause(); }
	 * 
	 * protected void onResume() { //this.setVisible(true);
	 * 
	 * super.onResume(); }
	 */

	protected void onDestroy() {

		if (button_read.isEnabled())
			StopHandle();

		if (myapp.Mreader != null)
			myapp.Mreader.DisConnect();
		// /*
		if (myapp.CommBth.getRemoveType() == 4
				&& myapp.CommBth.ConnectState() != Comm_Bluetooth.DISCONNECTED)
			myapp.CommBth.DisConnect();
		// */
		super.onDestroy();
	}
}
