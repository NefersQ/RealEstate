package demo.RealEstate.services;

import demo.RealEstate.dto.PropertyDTO;
import demo.RealEstate.model.PropertyDAO;
import demo.RealEstate.model.UserDAO;
import demo.RealEstate.repository.PropertyRepository;
import demo.RealEstate.repository.UserRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRep userRep;

    private final String modelDir = "src/main/resources/static/models/";

    public void saveProperty(PropertyDAO property, MultipartFile file, Long userId) throws Exception {
        UserDAO user = userRep.findById(userId).orElseThrow();
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(modelDir + fileName);
        Files.createDirectories(path.getParent());
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        property.setModelFileName(fileName);
        property.setUser(user);

        propertyRepository.save(property);
    }

    public List<PropertyDTO> getAllProperties() {
        return propertyRepository.findAll().stream()
                .map(PropertyDTO::from)
                .collect(Collectors.toList());
    }

    public PropertyDTO getPropertyById(Long id) {
        PropertyDAO property = propertyRepository.findById(id).orElseThrow();
        return PropertyDTO.from(property);
    }
}
