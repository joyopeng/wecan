package com.gofirst.scenecollection.evidence.model;

import com.gofirst.scenecollection.evidence.utils.ImageHttpBody;
import com.gofirst.scenecollection.evidence.utils.TextHttpBody;
import com.gofirst.scenecollection.evidence.utils.UpLoadRequest;

/**
 * Created by maxiran on 2016/3/16.
 */
public class UpLoadData {

    private String key;
    private String value;
    private String mime;
    private String fileName;
    private byte[] bytevalue;
    private UpLoadRequest.OnWriteHttpBody onWriteHttpBody;
    

    private UpLoadData(String key, String value, String mime, String fileName, byte[] bytevalue,UpLoadRequest.OnWriteHttpBody onWriteHttpBody) {
        this.key = key;
        this.value = value;
        this.mime = mime;
        this.fileName = fileName;
        this.bytevalue = bytevalue;
        this.onWriteHttpBody = onWriteHttpBody;
    }

    public byte[] getBytevalue() {
        return bytevalue;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMime() {
        return mime;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }


    public UpLoadRequest.OnWriteHttpBody getOnWriteHttpBody() {
		return onWriteHttpBody;
	}

	public static class Builder {

        private String key;
        private String value;
        private String mime;
        private String fileName;
        private byte[] bytevalue;
        private UpLoadRequest.OnWriteHttpBody onWriteHttpBody;
        
        public Builder setKey(String key) {
            this.key = key;
            return this;
        }

        public Builder setValue(String value) {
            this.value = value;
            return this;
        }

        public Builder setMime(String mime) {
            this.mime = mime;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setBytevalue(byte[] bytevalue) {
            this.bytevalue = bytevalue;
            return this;
        }

        public UpLoadData Create(){
        	this.onWriteHttpBody = mime.equals("image/jpeg") ? new ImageHttpBody() : new TextHttpBody();
            return new UpLoadData(key, value, mime, fileName, bytevalue, onWriteHttpBody);
        }

    }
}
