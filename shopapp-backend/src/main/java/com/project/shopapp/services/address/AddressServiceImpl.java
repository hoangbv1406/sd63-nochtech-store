package com.project.shopapp.services.address;

import com.project.shopapp.models.District;
import com.project.shopapp.models.Province;
import com.project.shopapp.models.Ward;
import com.project.shopapp.repositories.DistrictRepository;
import com.project.shopapp.repositories.ProvinceRepository;
import com.project.shopapp.repositories.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;

    @Override
    public List<Province> getAllProvinces() {
        return provinceRepository.findAll();
    }

    @Override
    public List<District> getDistrictsByProvince(String provinceCode) {
        return districtRepository.findByProvinceCode(provinceCode);
    }

    @Override
    public List<Ward> getWardsByDistrict(String districtCode) {
        return wardRepository.findByDistrictCode(districtCode);
    }

}
