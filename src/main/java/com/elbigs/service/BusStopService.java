package com.elbigs.service;

import com.elbigs.dto.BusStopDto;
import com.elbigs.dto.ResponsDto;
import com.elbigs.dto.ShopReqDto;
import com.elbigs.entity.BusRouteEntity;
import com.elbigs.entity.BusStopEntity;
import com.elbigs.mapper.CommonMapper;
import com.elbigs.mapper.BusStopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class BusStopService {

    @Autowired
    private BusStopMapper busStopMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private MessageSource messageSource;

    public List<BusStopDto> selectBusStopList(ShopReqDto req) {
        List<BusStopDto> list = busStopMapper.selectBusStopList(req);
//        for(BusStopDto bus : list){
//            busStopMapper.selectBusStopList(bus.getId());
//        }
        return list;
    }

    public List<BusRouteEntity> selectBusStopRouteList(long busStopPk) {
        List<BusRouteEntity> list = busStopMapper.selectBusRouteList(busStopPk);
        return list;
    }

    public BusStopDto selectBusStop(long id) {
        return busStopMapper.selectBusStop(id);
    }

    private ResponsDto validateBusStop(BusStopEntity busStopEntity) {

        ResponsDto res = new ResponsDto();
        String requireMsg = messageSource.getMessage("error.msg.required", null, LocaleContextHolder.getLocale());

        if (!StringUtils.hasLength(busStopEntity.getName())) {
            res.addErrors("name", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(busStopEntity.getNumber())) {
            res.addErrors("number", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(busStopEntity.getLat())) {
            res.addErrors("lat", new String[]{requireMsg});
        }
        if (!StringUtils.hasLength(busStopEntity.getLng())) {
            res.addErrors("lng", new String[]{requireMsg});
        }

        return res;
    }

    @Transactional
    public ResponsDto updateBusStop(BusStopDto dto, long busStopId, long userPk) {

        boolean isNew = dto.getId() > 0 ? false : true;

        /* 유효성 체크 */
        ResponsDto validationRes = this.validateBusStop(dto);

        if (!validationRes.isSuccess()) {
            return validationRes;
        }

        dto.setId(busStopId);
        dto.setUserId(userPk);

        /* busStopEntity save */
        if (!isNew) {
            busStopMapper.updateBusStop(dto);
        } else {
            busStopMapper.insertBusStop(dto);
        }

        validationRes.setSuccess(true);
        return validationRes;
    }

    public void deleteBusStop(long id) {
        busStopMapper.deleteBusStop(id);
    }
}

