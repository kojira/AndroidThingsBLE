package com.example.android.bluetoothlegatt;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

public class L {
    public static final boolean DEBUG = true;

    private static StringBuilder builder = new StringBuilder();
    private static File pathExternalPublicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private static String mLogFileName = null;
    private static SimpleDateFormat mSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private static SimpleDateFormat mSdf2 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
    private static FileOutputStream mFileStream = null;
    private static BufferedWriter mWriter = null;
    private static int count = 0;
    private static final int WRITE_COUNT = 100;

    public static void v(String msg) {
        if (DEBUG && msg != null) {
            String log = getTracedText(msg);
            android.util.Log.v(getTag(), log);
            writeSd("V", getTag().concat(" ").concat(log));
        }
    }

    public static void d(String msg) {
        if (DEBUG && msg != null) {
            String log = getTracedText(msg);
            android.util.Log.d(getTag(), log);
            writeSd("D", getTag().concat(" ").concat(log));
        }
    }

    public static void i(String msg) {
        if (DEBUG && msg != null) {
            String log = getTracedText(msg);
            android.util.Log.i(getTag(), log);
            writeSd("I", getTag().concat(" ").concat(log));
        }
    }

    public static void w(String msg) {
        if (DEBUG && msg != null) {
            String log = getTracedText(msg);
            android.util.Log.w(getTag(), log);
            writeSd("W", getTag().concat(" ").concat(log));
        }
    }

    public static void e(String msg) {
        if (DEBUG && msg != null) {
            String log = getTracedText(msg);
            android.util.Log.e(getTag(), log);
            writeSd("E", getTag().concat(" ").concat(log));
        }
    }

    public static void e(Exception e) {
        if (DEBUG) {
            String log;
            if (null != e) {
                log = getTracedText(e.getMessage());
            } else {
                log = "null";
            }
            android.util.Log.e(getTag(), log, e);
            writeSd("E", getTag().concat(" ").concat(log));
        }
    }

    private static String getTracedText(String text) {
        if (DEBUG) {
            StackTraceElement[] ste = (new Throwable()).getStackTrace();
            builder.setLength(0);
            builder.append(ste[2].getMethodName());
            builder.append("(");
            builder.append(ste[2].getFileName());
            builder.append(":");
            builder.append(ste[2].getLineNumber());
            builder.append(") ");
            if (null != text) {
                builder.append(text);
            } else {
                builder.append("null");
            }
            return builder.toString();
        } else {
            return "";
        }
    }

    private static String getTag() {
        StackTraceElement[] ste = (new Throwable()).getStackTrace();
        String className = ste[2].getClassName();
        return className.substring(className.lastIndexOf("."));
    }

    public static void openLogFile() {
        if (DEBUG) {
            if (mLogFileName == null) {
                mLogFileName = mSdf2.format(System.currentTimeMillis()).concat(".txt");
            }
            synchronized (pathExternalPublicDir) {
                try {
                    count = 0;
                    mFileStream = new FileOutputStream(pathExternalPublicDir.toString().concat("/").concat(mLogFileName), true);
                    mWriter = new BufferedWriter(new OutputStreamWriter(mFileStream, "MS932"));
                } catch (FileNotFoundException e) {
                } catch (UnsupportedEncodingException e) {
                    try {
                        mFileStream.close();
                    } catch (IOException e1) {
                    } finally {
                        mFileStream = null;
                        mWriter = null;
                    }
                }
            }
        }

    }

    public static void closeLogFile() {
        if (DEBUG) {
            synchronized (pathExternalPublicDir) {
                if (null != mWriter) {
                    try {
                        mWriter.close();
                    } catch (IOException e) {
                    } finally {
                        mWriter = null;
                    }
                }
                if (null != mFileStream) {
                    try {
                        mFileStream.close();
                    } catch (IOException e) {
                    } finally {
                        mFileStream = null;
                    }
                }
            }
        }
    }

    private static void writeSd(String level, String text) {
        if (DEBUG) {
            if (null != mWriter) {
                String time = mSdf.format(System.currentTimeMillis());
                try {
                    mWriter.append(time);
                    mWriter.append(" ");
                    mWriter.append(level);
                    mWriter.append(" ");
                    mWriter.append(text);
                    mWriter.append("\n");
                    count++;
                } catch (IOException e) {
                }
                if (count > WRITE_COUNT) {
                    openLogFile();
                    closeLogFile();
                }
            }
        }
    }

}