package com.project.shopapp.services.address;

import com.project.shopapp.models.District;
import com.project.shopapp.models.Province;
import com.project.shopapp.models.Ward;

import java.util.List;

public interface AddressService {
    List<Province> getAllProvinces();
    List<District> getDistrictsByProvince(String provinceCode);
    List<Ward> getWardsByDistrict(String districtCode);
}
