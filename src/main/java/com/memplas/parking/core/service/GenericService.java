package com.memplas.parking.core.service;

import java.util.List;
import java.util.Optional;

public interface GenericService<DTO, ID> {
  DTO create(DTO dto);

  Optional<DTO> findById(ID id);

  List<DTO> findAll();

  DTO update(DTO dto);

  void deleteById(ID id);
}
