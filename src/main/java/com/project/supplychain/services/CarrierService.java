package com.project.supplychain.services;

import com.project.supplychain.DTOs.carrierDTOs.CarrierDTO;
import com.project.supplychain.enums.CarrierStatus;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.CarrierMapper;
import com.project.supplychain.models.Carrier;
import com.project.supplychain.repositories.CarrierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class CarrierService {

    @Autowired
    private CarrierRepository carrierRepository;

    @Autowired
    private CarrierMapper carrierMapper;

    public HashMap<String, Object> create(CarrierDTO dto) {
        Carrier entity = carrierMapper.toEntity(dto);
        if (entity.getStatus() == null) entity.setStatus(CarrierStatus.ACTIVE);
        if (entity.getCurrentDailyShipments() == null) entity.setCurrentDailyShipments(0);
        validateCarrier(entity);
        Carrier saved = carrierRepository.save(entity);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Carrier created successfully");
        result.put("carrier", carrierMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> get(UUID id) {
        Carrier found = carrierRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Carrier not found"));
        HashMap<String, Object> result = new HashMap<>();
        result.put("carrier", carrierMapper.toDTO(found));
        return result;
    }

    public HashMap<String, Object> list() {
        List<CarrierDTO> list = carrierRepository.findAll()
                .stream()
                .map(carrierMapper::toDTO)
                .toList();
        HashMap<String, Object> result = new HashMap<>();
        result.put("carriers", list);
        return result;
    }

    public HashMap<String, Object> update(UUID id, CarrierDTO dto) {
        Carrier existing = carrierRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Carrier not found"));

        existing.setCode(dto.getCode());
        existing.setName(dto.getName());
        existing.setPhone(dto.getPhone());
        existing.setBaseShippingRate(dto.getBaseShippingRate());
        existing.setMaxDailyCapacity(dto.getMaxDailyCapacity());
        existing.setCutOffTime(dto.getCutOffTime());
        existing.setStatus(dto.getStatus());

        validateCarrier(existing);
        Carrier saved = carrierRepository.save(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Carrier updated successfully");
        result.put("carrier", carrierMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> delete(UUID id) {
        Carrier existing = carrierRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Carrier not found"));
        carrierRepository.delete(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Carrier deleted successfully");
        return result;
    }

    public HashMap<String, Object> activate(UUID id) {
        Carrier existing = carrierRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Carrier not found"));
        existing.setStatus(CarrierStatus.ACTIVE);
        carrierRepository.save(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Carrier activated");
        result.put("carrier", carrierMapper.toDTO(existing));
        return result;
    }

    public HashMap<String, Object> suspend(UUID id) {
        Carrier existing = carrierRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Carrier not found"));
        existing.setStatus(CarrierStatus.SUSPENDED);
        carrierRepository.save(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Carrier suspended");
        result.put("carrier", carrierMapper.toDTO(existing));
        return result;
    }

    public HashMap<String, Object> resetDailyCount(UUID id) {
        Carrier existing = carrierRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Carrier not found"));
        existing.setCurrentDailyShipments(0);
        carrierRepository.save(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Carrier daily shipment count reset");
        result.put("carrier", carrierMapper.toDTO(existing));
        return result;
    }

    public void ensureCanShip(Carrier carrier) {
        if (carrier.getStatus() != CarrierStatus.ACTIVE) {
            throw new BadRequestException("Carrier is not ACTIVE");
        }
        if (carrier.getMaxDailyCapacity() != null && carrier.getCurrentDailyShipments() != null) {
            if (carrier.getCurrentDailyShipments() >= carrier.getMaxDailyCapacity()) {
                throw new BadRequestException("Carrier has reached max daily capacity");
            }
        }
        LocalDateTime now = LocalDateTime.now();
        if (carrier.getCutOffTime() != null && now.isAfter(carrier.getCutOffTime())) {
            throw new BadRequestException("Carrier cut-off time has passed for today");
        }
    }

    public void incrementDailyShipments(Carrier carrier) {
        int current = carrier.getCurrentDailyShipments() != null ? carrier.getCurrentDailyShipments() : 0;
        carrier.setCurrentDailyShipments(current + 1);
        carrierRepository.save(carrier);
    }

    private void validateCarrier(Carrier carrier) {
        if (carrier.getMaxDailyCapacity() != null && carrier.getMaxDailyCapacity() < 0) {
            throw new BadRequestException("maxDailyCapacity must be >= 0");
        }
        if (carrier.getCurrentDailyShipments() != null && carrier.getCurrentDailyShipments() < 0) {
            throw new BadRequestException("currentDailyShipments must be >= 0");
        }
    }
}
