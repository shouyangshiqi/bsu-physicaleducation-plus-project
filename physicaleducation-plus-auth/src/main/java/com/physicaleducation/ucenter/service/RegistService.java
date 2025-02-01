package com.physicaleducation.ucenter.service;

import com.physicaleducation.model.RestResponse;
import com.physicaleducation.ucenter.model.dto.RegistParamsDto;

/**
 * @author khran
 * @version 1.0
 * @description
 */

public interface RegistService {
     RestResponse register(RegistParamsDto registParamsDto);
}
