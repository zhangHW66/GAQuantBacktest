package com.gaquant.utils;

import com.gaquant.model.StockData;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** CSV 文件读取工具 */
public class CsvReader {

    public static List<StockData> read(String filePath, String dateFormat) throws IOException {
        List<StockData> data = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(dateFormat);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String header = br.readLine(); // 跳过标题行
            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(",");
                if (cols.length >= 6) {
                    StockData sd = new StockData();
                    sd.setDate(LocalDate.parse(cols[0].trim(), fmt));
                    sd.setOpen(Double.parseDouble(cols[1].trim()));
                    sd.setHigh(Double.parseDouble(cols[2].trim()));
                    sd.setLow(Double.parseDouble(cols[3].trim()));
                    sd.setClose(Double.parseDouble(cols[4].trim()));
                    sd.setVolume(Long.parseLong(cols[5].trim()));
                    data.add(sd);
                }
            }
        }
        return data;
    }
}
