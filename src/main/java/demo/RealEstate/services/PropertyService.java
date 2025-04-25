package demo.RealEstate.services;

import demo.RealEstate.dto.PropertyDTO;
import demo.RealEstate.model.PropertyDAO;
import demo.RealEstate.model.UserDAO;
import demo.RealEstate.repository.PropertyRepository;
import demo.RealEstate.repository.UserRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRep userRep;

    @Value("${upload.models.path}")
    private String modelDir;

    @Value("${upload.images.path}")
    private String imageDir;


    public void saveProperty(PropertyDAO property, MultipartFile file, Long userId) throws Exception {
        UserDAO user = userRep.findById(userId).orElseThrow();
        property.setUser(user);

        if (file != null && !file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get("src/main/resources/static/models/" + fileName);
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            property.setModelFileName(fileName);
        }


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
    public void savePropertyWithImages(PropertyDAO property, MultipartFile modelFile, List<MultipartFile> images, Long userId) throws Exception {
        UserDAO user = userRep.findById(userId).orElseThrow();
        property.setUser(user);

        if (modelFile != null && !modelFile.isEmpty()) {
            String modelFileName = System.currentTimeMillis() + "_" + modelFile.getOriginalFilename();
            Path modelPath = Paths.get(modelDir + modelFileName);
            Files.createDirectories(modelPath.getParent());
            Files.copy(modelFile.getInputStream(), modelPath, StandardCopyOption.REPLACE_EXISTING);
            property.setModelFileName(modelFileName);
        }

        if (images != null && !images.isEmpty()) {
            List<String> imageFileNames = new ArrayList<>();
            for (MultipartFile img : images) {
                String imageFileName = System.currentTimeMillis() + "_" + img.getOriginalFilename();
                Path imagePath = Paths.get(imageDir + imageFileName);
                Files.createDirectories(imagePath.getParent());
                Files.copy(img.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                imageFileNames.add(imageFileName);
            }
            property.setImageFileNames(imageFileNames);
        }

        propertyRepository.save(property);
    }
}
