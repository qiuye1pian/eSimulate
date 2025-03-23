package org.esimulate.core.controller.load;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.esimulate.core.model.load.heat.ThermalLoadScheme;
import org.esimulate.core.model.load.heat.ThermalLoadValue;
import org.esimulate.core.pojo.LoadPageQuery;
import org.esimulate.core.pojo.ThermalLoadSchemeDto;
import org.esimulate.core.pojo.ThermalLoadValueDto;
import org.esimulate.core.service.load.ThermalLoadSchemeService;
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
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/load/thermal-load-schemes")
public class ThermalLoadSchemeController {

    @Autowired
    private ThermalLoadSchemeService thermalLoadSchemeService;

    @PostMapping("/getListByPage")
    public Page<ThermalLoadSchemeDto> getListByPage(@RequestBody LoadPageQuery loadPageQuery) {
        return thermalLoadSchemeService.getListByPage(loadPageQuery)
                .map(ThermalLoadSchemeDto::new);
    }

    @PostMapping("/add")
    @Deprecated
    public ThermalLoadScheme addThermalLoadScheme(@RequestBody ThermalLoadSchemeDto thermalLoadSchemeDto) {
        return thermalLoadSchemeService.addThermalLoadScheme(thermalLoadSchemeDto);
    }

    @PostMapping("/upload")
    public ThermalLoadScheme uploadFile(@RequestParam("id") Long id, @RequestParam("file") MultipartFile file) {
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

            // 3️⃣ 解析后的内容
            log.debug("文件内容共有{}行", lineList.size());

            // 4️⃣ 返回更新后的对象
            return thermalLoadSchemeService.updateThermalLoadScheme(id, lineList);

        } catch (IOException ioException) {
            log.error("解析文件内容失败", ioException);
            throw new RuntimeException("解析文件内容失败", ioException);
        }
    }


    @PostMapping("/uploadScheme")
    public ThermalLoadScheme uploadScheme(@RequestParam("schemeName") @NonNull String schemeName,
                                          @RequestParam("file") MultipartFile file) {

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
            // 开始计时
            long start = System.currentTimeMillis();
            // 返回更新后的对象
            ThermalLoadScheme scheme = thermalLoadSchemeService.createScheme(schemeName, lineList);
            // 结束计时
            long end = System.currentTimeMillis();
            // 计算时间差
            long duration = end - start;
            // 打印时间差
            log.info("上传方案 [{}] 共耗时：{} ms，记录数：{}", schemeName, duration, lineList.size());
            return scheme;

        } catch (IOException ioException) {
            log.error("解析文件内容失败", ioException);
            throw new RuntimeException("解析文件内容失败", ioException);
        }
    }

    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadLoadValues(@RequestBody ThermalLoadSchemeDto thermalLoadSchemeDto) {

        List<ThermalLoadValueDto> loadValueDtoList = thermalLoadSchemeService.getLoadValuesBySchemeId(thermalLoadSchemeDto.getId()).stream()
                .map(ThermalLoadValueDto::new)
                .collect(Collectors.toList());

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {

            // 写入 CSV 表头
            writer.write("时间,负荷值\n");

            // 写入每一行数据
            for (ThermalLoadValueDto value : loadValueDtoList) {
                writer.write(value.toLine());
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

    @PostMapping("/getLoadValues")
    public List<ThermalLoadValueDto> getLoadValues(@RequestBody ThermalLoadSchemeDto thermalLoadSchemeDto) {
        return thermalLoadSchemeService.getLoadValuesBySchemeId(thermalLoadSchemeDto.getId()).stream()
                .map(ThermalLoadValueDto::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/delete")
    public String deleteScheme(@RequestBody ThermalLoadSchemeDto thermalLoadSchemeDto) {
        thermalLoadSchemeService.deleteThermalLoadScheme(thermalLoadSchemeDto.getId());
        return "删除成功";
    }
}