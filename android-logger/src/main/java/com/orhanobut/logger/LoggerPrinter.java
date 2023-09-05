package com.orhanobut.logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import static com.moorgen.util.Utils.checkNotNull;
import static com.orhanobut.logger.Logger.ASSERT;
import static com.orhanobut.logger.Logger.DEBUG;
import static com.orhanobut.logger.Logger.ERROR;
import static com.orhanobut.logger.Logger.INFO;
import static com.orhanobut.logger.Logger.VERBOSE;
import static com.orhanobut.logger.Logger.WARN;
import com.moorgen.util.Utils;

class LoggerPrinter implements Printer {

  /**
   * It is used for json pretty print
   */
  private static final int JSON_INDENT = 2;

  /**
   * Provides one-time used tag for the log message
   */
  private final ThreadLocal<String> localTag = new ThreadLocal<>();

  private static org.slf4j.Logger Log = LoggerFactory.getLogger("default");

  @Override public Printer t(String tag) {
    if (tag != null) {
      localTag.set(tag);
    }
    return this;
  }

  @Override public void d(@NonNull String message, @Nullable Object... args) {
    log(DEBUG, null, message, args);
  }

  @Override public void d(@Nullable Object object) {
    log(DEBUG, null, Utils.toString(object));
  }

  @Override public void e(@NonNull String message, @Nullable Object... args) {
    e(null, message, args);
  }

  @Override public void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
    log(ERROR, throwable, message, args);
  }

  @Override public void w(@NonNull String message, @Nullable Object... args) {
    log(WARN, null, message, args);
  }

  @Override public void i(@NonNull String message, @Nullable Object... args) {
    log(INFO, null, message, args);
  }

  @Override public void v(@NonNull String message, @Nullable Object... args) {
    log(VERBOSE, null, message, args);
  }

  @Override public void wtf(@NonNull String message, @Nullable Object... args) {
    log(ASSERT, null, message, args);
  }

  @Override public void json(@Nullable String json) {
    if (Utils.isEmpty(json)) {
      d("Empty/Null json content");
      return;
    }
    try {
      json = json.trim();
      if (json.startsWith("{")) {
        JSONObject jsonObject = new JSONObject(json);
        String message = jsonObject.toString(JSON_INDENT);
        d(message);
        return;
      }
      if (json.startsWith("[")) {
        JSONArray jsonArray = new JSONArray(json);
        String message = jsonArray.toString(JSON_INDENT);
        d(message);
        return;
      }
      e("Invalid Json");
    } catch (JSONException e) {
      e("Invalid Json");
    }
  }

  @Override public void xml(@Nullable String xml) {
    if (Utils.isEmpty(xml)) {
      d("Empty/Null xml content");
      return;
    }
    try {
      Source xmlInput = new StreamSource(new StringReader(xml));
      StreamResult xmlOutput = new StreamResult(new StringWriter());
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      transformer.transform(xmlInput, xmlOutput);
      d(xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
    } catch (TransformerException e) {
      e("Invalid xml");
    }
  }

  @Override public synchronized void log(int priority,
      @Nullable String tag,
      @Nullable String message,
      @Nullable Throwable throwable) {
    if (throwable != null && message != null) {
      message += " : " + Utils.getStackTraceString(throwable);
    }
    if (throwable != null && message == null) {
      message = Utils.getStackTraceString(throwable);
    }
    if (Utils.isEmpty(message)) {
      message = "Empty/NULL log message";
    }

    switch (priority){
      case VERBOSE:
        trace(tag,message);
        break;
      case DEBUG:
        debug(tag,message);
        break;
      case INFO:
        info(tag,message);
        break;
      case WARN:
        warn(tag,message);
        break;
      case ERROR:
        error(tag,message);
        break;
    }
  }

  private Marker getMarker(String tag){
     if(!Utils.isEmpty(tag)) {
        return MarkerFactory.getMarker(tag);
     }
     return  null ;
  }

  private void debug(String tag,String message){
    Marker marker = getMarker(tag);
    if(marker != null){
      Log.debug(marker,message);
    }else{
      Log.debug(message);
    }
  }

  private void info(String tag,String message){
    Marker marker = getMarker(tag);
    if(marker != null){
      Log.info(marker,message);
    }else{
      Log.info(message);
    }
  }

  private void warn(String tag,String message){
    Marker marker = getMarker(tag);
    if(marker != null){
      Log.warn(marker,message);
    }else{
      Log.warn(message);
    }
  }

  private void error(String tag,String message){
    Marker marker = getMarker(tag);
    if(marker != null){
      Log.error(marker,message);
    }else{
      Log.error(message);
    }
  }

  private void trace(String tag,String message){
    Marker marker = getMarker(tag);
    if(marker != null){
      Log.trace(marker,message);
    }else{
      Log.trace(message);
    }
  }

  /**
   * This method is synchronized in order to avoid messy of logs' order.
   */
  private synchronized void log(int priority,
      @Nullable Throwable throwable,
      @NonNull String msg,
      @Nullable Object... args) {
    checkNotNull(msg);

    String tag = getTag();
    String message = createMessage(msg, args);
    log(priority, tag, message, throwable);
  }

  /**
   * @return the appropriate tag based on local or global
   */
  @Nullable private String getTag() {
    String tag = localTag.get();
    if (tag != null) {
      localTag.remove();
      return tag;
    }
    return null;
  }

  @NonNull private String createMessage(@NonNull String message, @Nullable Object... args) {
    return args == null || args.length == 0 ? message : String.format(message, args);
  }
}
