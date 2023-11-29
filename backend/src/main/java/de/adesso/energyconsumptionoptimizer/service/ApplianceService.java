package de.adesso.energyconsumptionoptimizer.service;

import de.adesso.energyconsumptionoptimizer.model.user.Appliance;
import de.adesso.energyconsumptionoptimizer.model.user.User;
import de.adesso.energyconsumptionoptimizer.repository.ApplianceRepository;
import de.adesso.energyconsumptionoptimizer.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ApplianceService {
    private final UserRepository userRepository;
    private final ApplianceRepository applianceRepository;

    @Transactional
    public Appliance addAppliance(Appliance appliance, UUID userId) {
        User user = this.userRepository.findById(userId).get();
        appliance.setUser(user);
        List<Appliance> applianceList = user.getAppliances();
        applianceList.add(appliance);
        user.setAppliances(applianceList);
        this.applianceRepository.save(appliance);
        this.userRepository.save(user);
        return this.applianceRepository.save(appliance);
    }

    @Transactional
    public List<Appliance> addAppliances(List<Appliance> appliances, UUID userId) {
            User user = this.userRepository.findById(userId).get();
            List<Appliance> applianceList = user.getAppliances();
            applianceList.addAll(applianceList);
            user.setAppliances(applianceList);
            this.applianceRepository.saveAll(appliances);
            this.userRepository.save(user);
            return this.applianceRepository.saveAll(appliances);
    }

    public Appliance updateAppliance(Appliance appliance) {
        return this.applianceRepository.save(appliance);
    }

    public void deleteAppliance(Appliance appliance) {
         this.applianceRepository.delete(appliance);
    }

    public List<Appliance> getByUserId(UUID userId) {
        return this.applianceRepository.findAppliancesByUserId(userId);
    }

    public Appliance getByUserIdAndDeviceName(UUID userId, String deviceName) throws Exception {
        try {
            List <Appliance> appliances = this.getByUserId(userId);
            Appliance appliance;
            for (Appliance device : appliances) {
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
