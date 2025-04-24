package demo.RealEstate.controller;

import demo.RealEstate.pswrdhashing.JwtUtil;
import demo.RealEstate.dto.PropertyDTO;
import demo.RealEstate.model.PropertyDAO;
import demo.RealEstate.services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PropertyService propertyService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createProperty(
            @RequestPart("property") PropertyDAO property,
            @RequestPart("file") MultipartFile file,
            @RequestParam("userId") Long userId
    ) {
        try {
            propertyService.saveProperty(property, file, userId);
            return ResponseEntity.ok("Property created");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public List<PropertyDTO> getAll() {
        return propertyService.getAllProperties();
    }

    @GetMapping("/{id}")
    public PropertyDTO getById(@PathVariable Long id) {
        return propertyService.getPropertyById(id);
    }

    @PostMapping(value = "/upload-direct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createPropertyDirect(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String address,
            @RequestParam Double price,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) List<MultipartFile> images,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(authHeader.replace("Bearer ", ""));
            PropertyDAO property = new PropertyDAO();
            property.setTitle(title);
            property.setDescription(description);
            property.setAddress(address);
            property.setPrice(price);

            propertyService.savePropertyWithImages(property, file, images, userId);
            return ResponseEntity.ok("Uploaded!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
