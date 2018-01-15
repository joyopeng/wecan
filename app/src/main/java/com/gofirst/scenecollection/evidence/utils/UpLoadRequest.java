package com.gofirst.scenecollection.evidence.utils;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.request.JsonObjectRequest;
import com.gofirst.scenecollection.evidence.model.UpLoadData;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UpLoadRequest extends JsonObjectRequest {

	private String BOUNDARY = "---------IAmACuteDividerORZ";
	private String MUTILPART_FROM_DATA = "multipart/form-data";
	private List<UpLoadData> fileList = new ArrayList<>();
	private String endLine = "--" + BOUNDARY + "--" + "\r\n";

	public UpLoadRequest(int method, String url, Listener<JSONObject> listener,
			List<UpLoadData> fileList) {
		super(Method.POST, url, null, listener);
		this.fileList = fileList;
	}

	@Override
	public byte[] getBody() {
		if (fileList == null || fileList.size() == 0) {
			throw new IllegalArgumentException(
					"fileList should not be null or empty !");
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		UpLoadData uploadData;
		for (int i = 0; i < fileList.size(); i++) {
			uploadData = fileList.get(i);
			StringBuffer sb = new StringBuffer();
			uploadData.getOnWriteHttpBody().writeHttpBody(sb,bos,uploadData,BOUNDARY);
		}

		try {
			bos.write(endLine.getBytes("utf-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bos.toByteArray();
	}

	@Override
	public String getBodyContentType() {
		return MUTILPART_FROM_DATA + "; boundary=" + BOUNDARY;
	}


	public interface OnWriteHttpBody {
		void writeHttpBody(StringBuffer sb, ByteArrayOutputStream bs,
				UpLoadData uploadData, String BOUNDARY);
	}
}
