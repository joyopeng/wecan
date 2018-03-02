package com.gftxcky.draw;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class MouldData {
	private List<String> modList;
	private List<ArrayList<String>> modListChilds;

	public MouldData(String table, String[] columns, String selection, String orderBy, String groupBy, int modType)
	{
		Cursor cursor;
		DataBase data = new DataBase("mod");
		cursor = data.query(table, columns, selection, orderBy, groupBy);
		setModList(new ArrayList<String>());
		setModListChilds(new ArrayList<ArrayList<String>>());
		if (modType != 0)
		{
			while (cursor.moveToNext())
			{
				Cursor ccursor;
				getModList().add((new StringBuilder(cursor.getString(0))).append(" (").append(cursor.getString(1)).append(")").toString());
				ccursor = data.query(table, new String[] {
					"c", "count(a) as count"
				}, (new StringBuilder("b='")).append(cursor.getString(0)).append("'").toString(), "c desc", "c");
				ArrayList<String> arraylist = new ArrayList<String>();
				while (ccursor.moveToNext()) {
					arraylist.add((new StringBuilder(ccursor.getString(0))).append(" (").append(ccursor.getString(1)).append(")").toString());
				}
				ccursor.close();
				getModListChilds().add(arraylist);
			}
		}
		else
		{
			while (cursor.moveToNext()) 
			{
				getModList().add((new StringBuilder(String.valueOf(cursor.getString(0)))).append(" (").append(cursor.getString(1)).append(")").toString());
				getModListChilds().add(new ArrayList<String>());
			}
		}
		cursor.close();
		data.close();
		return;
			
	}

	public List<String> getModList() {
		return modList;
	}

	public void setModList(List<String> modList) {
		this.modList = modList;
	}

	public List<ArrayList<String>> getModListChilds() {
		return modListChilds;
	}

	public void setModListChilds(List<ArrayList<String>> modListChilds) {
		this.modListChilds = modListChilds;
	}


}
