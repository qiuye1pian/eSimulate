package org.esimulate.core.pojo;

import org.esimulate.core.pojo.load.ThermalLoadValueDto;
import org.esimulate.util.DateTimeUtil;
import org.esimulate.util.TimeValueCsvConverter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ThermalLoadValueDtoTest {

    @Test
    public void testGettersAndSetters() {
        ThermalLoadValueDto thermalLoadValueDto = TimeValueCsvConverter.convertByCsvLineForTest("2023-10-01 12:00:00,10.0", ThermalLoadValueDto::new);
        assertEquals(BigDecimal.valueOf(10.0), thermalLoadValueDto.getValue());
        assertEquals("2023-10-01 12:00:00", DateTimeUtil.format(thermalLoadValueDto.getTime()));
    }


    @Test
    public void testConvertByCsvContent() {
        List<String> contentLines = Arrays.asList("2023-10-01 12:00:00,10.0", "2023-10-01 13:00:00,15.0");

        List<ThermalLoadValueDto> thermalLoadValueDtoList = TimeValueCsvConverter.convertByCsvContent(contentLines, ThermalLoadValueDto::new);
        assertEquals(2, thermalLoadValueDtoList.size());
        assertEquals(thermalLoadValueDtoList.get(0).getValue(), BigDecimal.valueOf(10.0));
        assertEquals(thermalLoadValueDtoList.get(1).getValue(), BigDecimal.valueOf(15.0));
        assertEquals("2023-10-01 12:00:00", DateTimeUtil.format(thermalLoadValueDtoList.get(0).getTime()));
        assertEquals("2023-10-01 13:00:00", DateTimeUtil.format(thermalLoadValueDtoList.get(1).getTime()));
    }

    @Test
    public void testConvertByCsvContentAndSort() {
        List<String> contentLines = Arrays.asList("2023-10-01 12:00:00,10.0", "2023-10-01 11:00:00,15.0");

        List<ThermalLoadValueDto> thermalLoadValueDtoList = TimeValueCsvConverter.convertByCsvContent(contentLines, ThermalLoadValueDto::new);
        assertEquals(2, thermalLoadValueDtoList.size());
        assertEquals(thermalLoadValueDtoList.get(0).getValue(), BigDecimal.valueOf(15.0));
        assertEquals(thermalLoadValueDtoList.get(1).getValue(), BigDecimal.valueOf(10.0));
        assertEquals("2023-10-01 11:00:00", DateTimeUtil.format(thermalLoadValueDtoList.get(0).getTime()));
        assertEquals("2023-10-01 12:00:00", DateTimeUtil.format(thermalLoadValueDtoList.get(1).getTime()));
    }

    @Test
    public void testTitle() {
        ThermalLoadValueDto thermalLoadValueDto = TimeValueCsvConverter.convertByCsvLineForTest("日期,值", ThermalLoadValueDto::new);
        assertNull(thermalLoadValueDto);
    }

}