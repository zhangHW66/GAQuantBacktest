package com.gaquant.utils;

import com.gaquant.model.StockData;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** CSV/TXT 文件读取 — 自动识别分隔符，按表头列名匹配，兼容不同列序和中文列名 */
public class CsvReader {

    private static final Map<String, String[]> ALIASES = Map.of(
        "date",   new String[]{"date", "日期", "time"},
        "open",   new String[]{"开盘", "open"},
        "high",   new String[]{"最高", "high"},
        "low",    new String[]{"最低", "low"},
        "close",  new String[]{"收盘", "close"},
        "volume", new String[]{"成交量", "volume", "vol"}
    );

    public static List<StockData> read(String filePath, String dateFormat) throws IOException {
        List<StockData> data = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(dateFormat);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String headerLine = br.readLine();
            if (headerLine == null) return data;

            String sep = detectSeparator(headerLine);

            Map<String, Integer> idx = buildIndex(headerLine.split(sep));

            Integer di = idx.get("date"),  oi = idx.get("open");
            Integer hi = idx.get("high"),  li = idx.get("low");
            Integer ci = idx.get("close"), vi = idx.get("volume");

            if (di == null || ci == null)
                throw new IOException("CSV缺少必要列: 需要日期和收盘价列");

            int width = max(di, oi, hi, li, ci, vi);

            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(sep);
                if (cols.length <= width) continue;

                StockData sd = new StockData();
                sd.setDate(LocalDate.parse(cols[di].trim(), fmt));
                sd.setClose(Double.parseDouble(cols[ci].trim()));
                sd.setOpen(  oi != null ? Double.parseDouble(cols[oi].trim()) : sd.getClose());
                sd.setHigh(  hi != null ? Double.parseDouble(cols[hi].trim()) : sd.getClose());
                sd.setLow(   li != null ? Double.parseDouble(cols[li].trim()) : sd.getClose());
                sd.setVolume(vi != null ? parseLong(cols[vi].trim()) : 0);
                data.add(sd);
            }
        }
        return data;
    }

    /** 根据表头自动识别分隔符（Tab 或 逗号） */
    private static String detectSeparator(String header) {
        int tabs = header.length() - header.replace("\t", "").length();
        int commas = header.length() - header.replace(",", "").length();
        return tabs > commas ? "\t" : ",";
    }

    private static Map<String, Integer> buildIndex(String[] headers) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            String h = headers[i].trim();
            for (var e : ALIASES.entrySet()) {
                for (String alias : e.getValue()) {
                    if (h.equalsIgnoreCase(alias)) {
                        map.put(e.getKey(), i);
                        break;
                    }
                }
            }
        }
        return map;
    }

    private static long parseLong(String s) {
        return (long) Double.parseDouble(s);
    }

    private static int max(Integer... vals) {
        int m = 0;
        for (Integer v : vals) if (v != null && v > m) m = v;
        return m;
    }
}
