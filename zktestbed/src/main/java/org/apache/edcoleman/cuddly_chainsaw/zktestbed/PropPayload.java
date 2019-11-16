package org.apache.edcoleman.cuddly_chainsaw.zktestbed;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PropPayload {

  private final static Logger log = LogManager.getLogger();

  private static Gson gson = new Gson();

  // number of bytes used to pad output arrays to prevent resizing.
  private static final int BUF_PADDING = 64;

  public enum CompressionType {
    NONE, GZIP
  }

  private CompressionType compressionType = CompressionType.NONE;
  private final Map<String,PropValue> props = new TreeMap<>();

  public PropPayload() {
  }

  public void addProp(final String name, final PropValue v) {
    props.put(name, v);
  }

  public byte[] toBytes() {

    String v = gson.toJson(props);

    try {

      ByteArrayOutputStream bos;

      ByteArrayOutputStream gzipStream = new ByteArrayOutputStream(v.length() + 32);

      log.debug("bos len {}", gzipStream.size());
      log.debug("in len {}", v.length());

      GZIPOutputStream gout = new GZIPOutputStream(gzipStream);
      gout.write(v.getBytes(StandardCharsets.UTF_8));
      gout.close();

      log.debug("bos len {}", gzipStream.size());

      if (v.length() > gzipStream.size()) {

        bos = new ByteArrayOutputStream(gzipStream.size() + BUF_PADDING);
        DataOutputStream out = new DataOutputStream(bos);

        out.writeUTF(CompressionType.GZIP.name());
        out.write(gzipStream.toByteArray());

        out.close();

      } else {

        bos = new ByteArrayOutputStream(v.length() + BUF_PADDING);
        DataOutputStream out = new DataOutputStream(bos);

        out.writeUTF(compressionType.name());
        out.writeUTF(v);

        out.close();

      }

      return bos.toByteArray();

    } catch (IOException ex) {
      throw new IllegalStateException("Failed to compress data", ex);
    }

  }

  public static PropPayload fromBytes(final byte[] bytes) {

    PropPayload propPayload = new PropPayload();

    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
    DataInputStream in = new DataInputStream(bis);

    try {

      propPayload.compressionType = CompressionType.valueOf(in.readUTF());

      switch (propPayload.compressionType) {
        case NONE:
          String json = in.readUTF();
          propPayload.props.putAll(gson.fromJson(json, propPayload.props.getClass()));
          break;
        case GZIP:
          byte[] a = in.readAllBytes();
          GZIPInputStream gin = new GZIPInputStream(new ByteArrayInputStream(a));
          Reader r = new InputStreamReader(gin);

          Map<String,PropValue> m = gson.fromJson(r, propPayload.props.getClass());
          propPayload.props.putAll(m);

          return propPayload;

        default:
          throw new IllegalStateException(
              "unsupported compression type " + propPayload.compressionType);
      }

      return propPayload;

    } catch (IOException ex) {
      throw new IllegalStateException("Failed to convert from byte[]", ex);
    }

  }

  @Override public String toString() {
    return new StringJoiner(", ", PropPayload.class.getSimpleName() + "[", "]")
        .add("compressionType=" + compressionType).add("props=" + props).toString();
  }
}
