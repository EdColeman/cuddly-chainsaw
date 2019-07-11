package org.apache.edcoleman.cuddly_chainsaw.du;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class DUCommand {

  private class CmdArgs {

    @Parameter(description = "tablename[s]")
    private List<String> files = new ArrayList<>();

    @Parameter(names = "--help", help = true)
    private boolean help;

    @Parameter(names = "-c", description = "display grand total, default false")
    private boolean total = false;

    @Parameter(names = "-a", description = "Use FileStat[] instead of iterator, default = false")
    private boolean array = false;

    @Parameter(names = "-h", description = "\"Human-readable\" output.  Use unit suffixes: Byte, Kilobyte, Megabyte, Gigabyte, Terabyte and Petabyte.")
    private boolean humanReadable = false;

  }

  private static final int unit = 1024;
  private static final String units = "KMGTPE";

  public static String humanReadableByteCount(long bytes) {

    if (bytes < unit) return bytes + " B";
    int exp = (int) (Math.log(bytes) / Math.log(unit));
    char pre = units.charAt(exp-1);

    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
  }
}
