package org.esimulate.core.controller.environment;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.esimulate.core.model.environment.sunlight.SunlightIrradianceScheme;
import org.esimulate.core.pojo.environment.SunlightIrradianceSchemeDto;
import org.esimulate.core.pojo.environment.SunlightIrradianceValueDto;
import org.esimulate.core.pojo.load.LoadPageQuery;
import org.esimulate.core.pojo.load.LoadValueChartDto;
import org.esimulate.core.service.environment.SunlightIrradianceSchemeService;
import org.esimulate.util.DateTimeUtil;
import org.esimulate.util.TimeValueCsvConverter;
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
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/environment/sunlight")
public class SunlightController {


    @Autowired
    private SunlightIrradianceSchemeService sunlightIrradianceSchemeService;

    @PostMapping("/getListByPage")
    public Page<SunlightIrradianceSchemeDto> getListByPage(@RequestBody LoadPageQuery loadPageQuery) {
        return sunlightIrradianceSchemeService.getListByPage(loadPageQuery)
                .map(SunlightIrradianceSchemeDto::new);
    }

    @PostMapping("/add")
    public SunlightIrradianceScheme addSunlightIrradianceScheme(@RequestBody SunlightIrradianceSchemeDto sunlightIrradianceSchemeDto) {
        return sunlightIrradianceSchemeService.addSunlightIrradianceScheme(sunlightIrradianceSchemeDto.getName());
    }

    @PostMapping("/upload")
    public SunlightIrradianceScheme uploadFile(@RequestParam("id") Long id, @RequestParam("file") MultipartFile file) {
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
            return sunlightIrradianceSchemeService.updateSunlightIrradianceScheme(id, lineList);

        } catch (IOException ioException) {
            log.error("解析文件内容失败", ioException);
            throw new RuntimeException("解析文件内容失败", ioException);
        }
    }


    @PostMapping("/uploadScheme")
    public SunlightIrradianceScheme uploadScheme(@RequestParam("schemeName") @NonNull String schemeName,
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

            // 开始计时
            long start = System.currentTimeMillis();
            // 返回更新后的对象
            SunlightIrradianceScheme scheme = sunlightIrradianceSchemeService.createScheme(schemeName, lineList);
            // 结束计时
            long end = System.currentTimeMillis();
            // 计算时间差
            long duration = end - start;
            // 打印时间差
            log.info("上传方案 [{}] 共耗时：{} ms，记录数：{}", schemeName, duration, lineList.size());
            // 4️⃣ 返回更新后的对象

            return scheme;

        } catch (IOException ioException) {
            log.error("解析文件内容失败", ioException);
            throw new RuntimeException("解析文件内容失败", ioException);
        }
    }


    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadLoadValues(@RequestBody SunlightIrradianceSchemeDto sunlightIrradianceSchemeDto) {
        List<SunlightIrradianceValueDto> sunlightIrradianceValueDtoList = sunlightIrradianceSchemeService
                .getLoadValuesBySchemeId(sunlightIrradianceSchemeDto.getId())
                .stream()
                .map(SunlightIrradianceValueDto::new)
                .collect(Collectors.toList());


        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {

            writer.write('\uFEFF');

            // 写入 CSV 表头
            writer.write("时间,负荷值\n");

            // 写入每一行数据
            for (SunlightIrradianceValueDto value : sunlightIrradianceValueDtoList) {
                writer.write(TimeValueCsvConverter.toLine(value));
            }

            writer.flush();

            // 设置响应头，触发文件下载
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=load_values.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(out.toByteArray());

        } catch (Exception e) {
            log.error("生成CSV文件失败", e);
            return ResponseEntity.internalServerError()
                    .body(("生成CSV文件失败: " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }

    @PostMapping("/getLoadValues")
    public LoadValueChartDto getLoadValues(@RequestBody SunlightIrradianceSchemeDto sunlightIrradianceSchemeDto) {

        List<SunlightIrradianceValueDto> sortedSunlightIrradianceValueDtoList = sunlightIrradianceSchemeService.getLoadValuesBySchemeId(sunlightIrradianceSchemeDto.getId())
                .stream()
                .map(SunlightIrradianceValueDto::new)
                .collect(Collectors.toList());

        List<String> xAxisData = sortedSunlightIrradianceValueDtoList.stream().
                map(x -> DateTimeUtil.formatNoYearString(x.getTime()))
                .collect(Collectors.toList());

        List<BigDecimal> yAxisData = sortedSunlightIrradianceValueDtoList.stream()
                .map(SunlightIrradianceValueDto::getValue)
                .collect(Collectors.toList());

        return new LoadValueChartDto(xAxisData, yAxisData);
    }


    @PostMapping("/delete")
    public String deleteScheme(@RequestBody SunlightIrradianceSchemeDto sunlightIrradianceSchemeDto) {
        sunlightIrradianceSchemeService.deleteSunlightIrradianceScheme(sunlightIrradianceSchemeDto.getId());
        return "删除成功";
    }

}
