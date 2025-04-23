package demo.RealEstate.services;

import demo.RealEstate.dto.PropertyDTO;
import demo.RealEstate.exception.ApiException;
import demo.RealEstate.mapper.PropertyMapper;
import demo.RealEstate.model.PropertyDAO;
import demo.RealEstate.model.UserDAO;
import demo.RealEstate.repository.PropertyRepository;
import demo.RealEstate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyMapper propertyMapper;

    @Value("${upload.models.path}")
    private String modelDir;

    @Value("${upload.images.path}")
    private String imageDir;
    //returns all properties of user by id
    public List<PropertyDTO> getPropertiesByUser(Long userId) {
        List<PropertyDAO> properties = propertyRepository.findAllByUser_UserId(userId);
        return propertyMapper.toDTOList(properties);
    }
    // updates property info
    public void updateProperty(Long propertyId, PropertyDTO dto, Long userId) {
        PropertyDAO property = propertyRepository.findById(propertyId).orElseThrow();
        if (!property.getUser().getUserId().equals(userId)) {
            throw new ApiException("Access denied", HttpStatus.FORBIDDEN);
        }

        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setAddress(dto.getAddress());
        property.setPrice(dto.getPrice());
        property.setArea(dto.getArea());
        property.setRooms(dto.getRooms());
        property.setPhoneNumber(dto.getPhoneNumber());
        property.setEmail(dto.getEmail());

        property.setPropertyType(PropertyDAO.PropertyType.valueOf(dto.getPropertyType()));
        property.setConstructionType(PropertyDAO.ConstructionType.valueOf(dto.getConstructionType()));
        property.setDistrict(PropertyDAO.District.valueOf(dto.getDistrict()));

        if (dto.getPrice() != null && dto.getArea() != null && dto.getArea() > 0) {
            BigDecimal price = BigDecimal.valueOf(dto.getPrice());
            BigDecimal area = BigDecimal.valueOf(dto.getArea());
            BigDecimal pricePerSqm = price.divide(area, 2, RoundingMode.HALF_UP);
            property.setPricePerSqm(pricePerSqm.doubleValue());
        }

        property.setFloor(dto.getFloor());
        property.setTotalFloors(dto.getTotalFloors());

        propertyRepository.save(property);
    }
    //delete property by id
    public void deleteProperty(Long id, Long userId) {
        PropertyDAO property = propertyRepository.findById(id)
                .orElseThrow(() -> new ApiException("Property not found", HttpStatus.NOT_FOUND));
        if (!property.getUser().getUserId().equals(userId)) {
            throw new ApiException("Access denied", HttpStatus.FORBIDDEN);
        }
        propertyRepository.delete(property);
    }
    // returns all properties on server
    public List<PropertyDTO> getAllProperties() {
        List<PropertyDAO> properties = propertyRepository.findAll();
        return propertyMapper.toDTOList(properties);
    }
    //returns property info by property id
    public PropertyDTO getPropertyById(Long id) {
        PropertyDAO property = propertyRepository.findById(id)
                .orElseThrow(() -> new ApiException("Property with ID " + id + " not found", HttpStatus.NOT_FOUND));
        return propertyMapper.toDTO(property);
    }

    public void savePropertyFromRequest(String title, String description, String address, Double price, Double area,
                                        Integer rooms, String propertyType, String constructionType, String district,
                                        String phoneNumber, String email, Integer totalFloors, Integer floor,
                                        MultipartFile modelFile, List<MultipartFile> images, Long userId
    ) throws Exception {
        UserDAO user = userRepository.findById(userId).orElseThrow();
        PropertyDAO property = new PropertyDAO();

        property.setTitle(title);
        property.setDescription(description);
        property.setAddress(address);
        property.setPrice(price);
        property.setArea(area);
        property.setRooms(rooms);
        property.setPhoneNumber(phoneNumber);
        property.setEmail(email);

        property.setPropertyType(PropertyDAO.PropertyType.valueOf(propertyType));
        property.setConstructionType(PropertyDAO.ConstructionType.valueOf(constructionType));
        property.setDistrict(PropertyDAO.District.valueOf(district));

        if (property.getArea() != null && property.getArea() > 0 && property.getPrice() != null && property.getPrice() > 0) {
            BigDecimal priceDecimal = BigDecimal.valueOf(property.getPrice());
            BigDecimal areaDecimal = BigDecimal.valueOf(property.getArea());
            BigDecimal pricePerSqm = priceDecimal.divide(areaDecimal, 2, RoundingMode.HALF_UP);
            property.setPricePerSqm(pricePerSqm.doubleValue());
        }

        if (property.getPropertyType() == PropertyDAO.PropertyType.HOUSE && totalFloors != null) {
            property.setTotalFloors(totalFloors);
        }

        if (property.getPropertyType() == PropertyDAO.PropertyType.APARTMENT && floor != null) {
            property.setFloor(floor);
        }

        if (modelFile != null && !modelFile.isEmpty()) {
            String modelFileName = System.currentTimeMillis() + "_" + modelFile.getOriginalFilename();
            Path modelPath = Paths.get(modelDir).resolve(modelFileName);
            Files.createDirectories(modelPath.getParent());
            Files.copy(modelFile.getInputStream(), modelPath, StandardCopyOption.REPLACE_EXISTING);
            property.setModelFileName(modelFileName);
        }

        if (images != null && !images.isEmpty()) {
            List<String> imageFileNames = new ArrayList<>();
            for (MultipartFile img : images) {
                String imageFileName = System.currentTimeMillis() + "_" + img.getOriginalFilename();
                Path imagePath = Paths.get(imageDir).resolve(imageFileName);
                Files.createDirectories(imagePath.getParent());
                Files.copy(img.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                imageFileNames.add(imageFileName);
            }
            property.setImageFileNames(imageFileNames);
        }

        property.setUser(user);
        propertyRepository.save(property);
    }
}