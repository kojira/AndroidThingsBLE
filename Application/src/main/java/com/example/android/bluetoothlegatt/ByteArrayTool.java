package com.example.android.bluetoothlegatt;


import java.nio.ByteBuffer;

public class ByteArrayTool {
	public static final String UTF_8 = "UTF-8";
	/**
	 * null or Emptyチェック
	 * @param byteArray　バイト配列
	 * @return　null or Empty　ならばtrue
	 */
	public static boolean isNullorEmpty(byte[] byteArray){
		if((byteArray != null)&&(byteArray.length > 0)){
			return(false);
		}
		else{
			return(true);
		}
	}

	/**
	 * Byteの取得
	 * @param bytes バイト配列
	 * @param index 取得位置
	 * @return
	 */
	public static byte getByte(byte[] bytes, int index){
		return ByteBuffer.wrap(bytes).get(index);
	}

	/**
	 * Shortの取得
	 * @param bytes バイト配列
	 * @return
	 */
	public static short getShort(byte[] bytes){
		return ByteBuffer.wrap(bytes).getShort();
	}

	/**
	 * Shortの取得
	 * @param bytes バイト配列
	 * @param index　取得位置
	 * @return
	 */
	public static short getShort(byte[] bytes, int index){
		return ByteBuffer.wrap(bytes).getShort(index);
	}

	/**
	 * Unsigned Shortの取得
	 * @param bytes バイト配列
	 * @param index　取得位置
	 * @return
	 */
	public static int getUnsignedShort(byte[] bytes, int index){
		return ((ByteBuffer.wrap(bytes).get(index)&0xFF) << 8)+((ByteBuffer.wrap(bytes).get(index+1)) & 0xFF);
	}

	/**
	 * Intの取得
	 * @param bytes バイト配列
	 * @param index　取得位置
	 * @return
	 */
	public static int getInt(byte[] bytes, int index) {
		return ByteBuffer.wrap(bytes).getInt(index);
	}

	/**
	 * Floatの取得
	 * @param bytes バイト配列
	 * @return
	 */
	public static float getFloat(byte[] bytes){
		return ByteBuffer.wrap(bytes).getFloat();
	}

	/**
	 * Floatの取得
	 * @param bytes バイト配列
	 * @param index 取得位置
	 * @return
	 */
	public static float getFloat(byte[] bytes, int index){
		return ByteBuffer.wrap(bytes).getFloat(index);
	}

	/**
	 * Stringの取得
	 * @param bytes バイト配列
	 * @return
	 */
	public static String getString(byte[] bytes){
		String result = null;
		try {
			result =  new String(bytes, UTF_8);
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * Stringの取得
	 * @param bytes バイト配列
	 * @param index 取得位置
	 * @param len 取得位置からの取得長
	 * @return
	 */
	public static String getString(byte[] bytes, int index, int len){
		String result = null;
		try {
			result =  new String(bytes, index, len, UTF_8);
		} catch (Exception e) {
		}
		return result;
	}
}
