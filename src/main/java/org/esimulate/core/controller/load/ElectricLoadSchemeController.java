package org.esimulate.core.controller.load;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.esimulate.core.model.load.electric.ElectricLoadScheme;
import org.esimulate.core.model.load.electric.ElectricLoadValue;
import org.esimulate.core.pojo.ElectricLoadSchemeDto;
import org.esimulate.core.pojo.LoadPageQuery;
import org.esimulate.core.service.load.ElectricLoadSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/load/electric-load-schemes")
public class ElectricLoadSchemeController {

    @Autowired
    private ElectricLoadSchemeService electricLoadSchemeService;

    @PostMapping("/getListByPage")
    public Page<ElectricLoadSchemeDto> getListByPage(@RequestBody LoadPageQuery loadPageQuery) {
        return electricLoadSchemeService.getListByPage(loadPageQuery)
                .map(ElectricLoadSchemeDto::new);
    }

    @PostMapping("/add")

    @Deprecated
    public ElectricLoadScheme addElectricLoadScheme(@RequestBody ElectricLoadSchemeDto electricLoadSchemeDto) {
        return electricLoadSchemeService.addElectricLoadScheme(electricLoadSchemeDto.getName());
    }

    @PostMapping("/upload")
    public ElectricLoadScheme uploadFile(@RequestParam("id") Long id, @RequestParam("file") MultipartFile file) {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传失败：文件不能为空");
        }

        try {
            // 解析文件内容
            List<String> lineList = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lineList.add(line);
                }
            }

            // 返回更新后的对象
            return electricLoadSchemeService.updateElectricLoadScheme(id, lineList);

        } catch (IOException ioException) {
            log.error("解析文件内容失败", ioException);
            throw new RuntimeException("解析文件内容失败", ioException);
        }
    }


    @PostMapping("/uploadScheme")
    public ElectricLoadScheme uploadScheme(@RequestParam("schemeName") @NonNull String schemeName,
                                           @RequestParam("file") MultipartFile file) {

        // 1️⃣ 检查文件是否为空
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传失败：文件不能为空");
        }

        try {
            // 2️⃣ 解析文件内容
            List<String> lineList = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lineList.add(line);
                }
            }

            // 4️⃣ 返回更新后的对象
            return electricLoadSchemeService.createScheme(schemeName, lineList);

        } catch (IOException ioException) {
            log.error("解析文件内容失败", ioException);
            throw new RuntimeException("解析文件内容失败", ioException);
        }
    }


    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadLoadValues(@RequestParam("id") @NonNull Long id) {
        List<ElectricLoadValue> loadValues = electricLoadSchemeService.getLoadValuesBySchemeId(id);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {

            // 写入 CSV 表头
            writer.write("时间,负荷值\n");

            // 写入每一行数据
            for (ElectricLoadValue value : loadValues) {
                writer.write(value.getDatetime().toString() + "," + value.getLoadValue() + "\n");
            }

            writer.flush();

            // 设置响应头，触发文件下载
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=load_values.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(out.toByteArray());

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(("生成CSV文件失败: " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }



}