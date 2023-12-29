package de.adesso.energyconsumptionoptimizer.service.user;

import de.adesso.energyconsumptionoptimizer.model.user.*;
import de.adesso.energyconsumptionoptimizer.repository.user.ApplianceRepository;
import de.adesso.energyconsumptionoptimizer.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ApplianceService {
    private final UserRepository userRepository;
    private final ApplianceRepository applianceRepository;
    private final ApplianceMapper applianceMapper;
    private final UserMapper userMapper;

    @Transactional
    public ApplianceDto addAppliance(ApplianceDto appliance, UUID userId) {
        User user = this.userRepository.findById(userId).get();
        appliance.setUser(this.userMapper.userEntityToDto(user));

        Appliance savedAppliance = this.applianceRepository.save(this.applianceMapper.dtoToEntity(appliance));

        List<Appliance> applianceList = user.getAppliances();

        applianceList.add(savedAppliance);
        user.setAppliances(applianceList);
        this.userRepository.save(user);
        return this.applianceMapper.entityToDto(savedAppliance);
    }

    @Transactional
    public List<ApplianceDto> addAppliances(List<ApplianceDto> applianceDtos, UUID userId) {
            User user = this.userRepository.findById(userId).get();
            UserDto userDto = this.userMapper.userEntityToDto(user);
            List<Appliance> applianceList = user.getAppliances();

            applianceDtos.stream().forEach(a -> a.setUser(userDto));
            List<Appliance> appliances = applianceDtos.stream().map(a -> this.applianceMapper.dtoToEntity(a)).collect(Collectors.toList());
            applianceList.addAll(appliances);
            user.setAppliances(applianceList);

            List<ApplianceDto> savedDtos = this.applianceRepository.saveAll(applianceList).stream().map(a -> this.applianceMapper.entityToDto(a)).collect(Collectors.toList());
            this.userRepository.save(user);
            return savedDtos;
    }

    public ApplianceDto updateAppliance(ApplianceDto appliance) {
        return this.applianceMapper.entityToDto(this.applianceRepository.save(this.applianceMapper.dtoToEntity(appliance)));
    }

    public void deleteAppliance(ApplianceDto appliance) {
         this.applianceRepository.delete(this.applianceMapper.dtoToEntity(appliance));
    }

    public List<ApplianceDto> getByUserId(UUID userId) {
        List<ApplianceDto> applianceDtos = this.applianceRepository.findAppliancesByUserId(userId).stream().map(a -> this.applianceMapper.entityToDto(a)).collect(Collectors.toList());
        return applianceDtos;
    }

    public ApplianceDto getByUserIdAndDeviceName(UUID userId, String deviceName) throws Exception {
        try {
            List <ApplianceDto> appliances = this.getByUserId(userId);
            ApplianceDto appliance;
            for (ApplianceDto device : appliances) {
                if (device.getName().equals(deviceName)) {
                    appliance = device;
                    return appliance;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new Exception("Appliance not found");
    }
}
