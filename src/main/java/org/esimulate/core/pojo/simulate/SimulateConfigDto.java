package org.esimulate.core.pojo.simulate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulateConfigDto {

    List<LoadDto> loadDtoList;

    List<ModelDto> modelDtoList;

    List<EnvironmentDto> environmentDtoList;

}
