package demo.RealEstate.controller;

import demo.RealEstate.jwt.JwtUtil;
import demo.RealEstate.dto.PropertyDTO;
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

    //uploading new property
    @PostMapping(value = "/upload-direct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadProperty(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String address,
            @RequestParam Double price,
            @RequestParam Double area,
            @RequestParam Integer rooms,
            @RequestParam String propertyType,
            @RequestParam String constructionType,
            @RequestParam String district,
            @RequestParam String phoneNumber,
            @RequestParam String email,
            @RequestParam(required = false) Integer totalFloors,
            @RequestParam(required = false) Integer floor,
            @RequestPart(required = false) MultipartFile file,
            @RequestPart(required = false) List<MultipartFile> images,
            @RequestHeader("Authorization") String authHeader
    ) throws Exception {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.replace("Bearer ", ""));
        propertyService.savePropertyFromRequest(
                title, description, address, price, area, rooms,
                propertyType, constructionType, district,
                phoneNumber, email, totalFloors, floor,
                file, images, userId
        );

        return ResponseEntity.ok("Property uploaded successfully.");
    }

    //returns all properties that belongs to current user(via JWT)
    @GetMapping("/my")
    public List<PropertyDTO> getUserProperties(@RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.replace("Bearer ", ""));
        return propertyService.getPropertiesByUser(userId);
    }

    //updating already existing property
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProperty(@PathVariable Long id,
                                                 @RequestBody PropertyDTO propertyDTO,
                                                 @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.replace("Bearer ", ""));
        propertyService.updateProperty(id, propertyDTO, userId);
        return ResponseEntity.ok("Updated successfully");
    }

    //deleting property
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProperty(@PathVariable Long id,
                                                 @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.getUserIdFromToken(authHeader.replace("Bearer ", ""));
        propertyService.deleteProperty(id, userId);
        return ResponseEntity.ok("Deleted successfully");
    }

    //returns all existing properties stored on server
    @GetMapping
    public List<PropertyDTO> getAllProperties() {
        return propertyService.getAllProperties();
    }
    //returns information about property by id
    @GetMapping("/{id}")
    public PropertyDTO getPropertyById(@PathVariable Long id) {
        return propertyService.getPropertyById(id);
    }
}