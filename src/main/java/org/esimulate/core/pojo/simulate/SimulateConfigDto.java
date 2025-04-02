package org.esimulate.core.pojo.simulate;

import lombok.Data;

import java.util.List;

@Data
public class SimulateConfigDto {

    List<LoadDto> loadDtoList;

    List<ModelDto> modelDtoList;

    List<EnvironmentDto> environmentDtoList;

}
