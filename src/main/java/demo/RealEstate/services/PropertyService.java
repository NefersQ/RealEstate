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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRep userRep;

    private final String modelDir = "src/main/resources/static/models/";
    private final String imageDir = "src/main/resources/static/images/";


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

        // 3D модель — необязательна
        if (modelFile != null && !modelFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + modelFile.getOriginalFilename();
            Path path = Paths.get(modelDir + fileName);
            Files.createDirectories(path.getParent());
            Files.copy(modelFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            property.setModelFileName(fileName);
        }

        // Изображения — список
        if (images != null && !images.isEmpty()) {
            List<String> fileNames = new ArrayList<>();
            for (MultipartFile image : images) {
                String imgName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path imgPath = Paths.get(imageDir + imgName);
                Files.createDirectories(imgPath.getParent());
                Files.copy(image.getInputStream(), imgPath, StandardCopyOption.REPLACE_EXISTING);
                fileNames.add(imgName);
            }
            property.setImageFileNames(fileNames);
        }

        propertyRepository.save(property);
    }
}
