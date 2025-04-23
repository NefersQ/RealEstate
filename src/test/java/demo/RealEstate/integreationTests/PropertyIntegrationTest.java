package demo.RealEstate.integreationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.RealEstate.dto.PropertyDTO;
import demo.RealEstate.model.UserDAO;
import demo.RealEstate.jwt.AuthResponse;
import demo.RealEstate.repository.PropertyRepository;
import demo.RealEstate.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import demo.RealEstate.request.LoginRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class PropertyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    private UserDAO testUser;
    private String authToken;
    private Long propertyId;

    @BeforeEach
    void setUp() throws Exception {
        testUser = new UserDAO();
        testUser.setName("Property");
        testUser.setSurname("Test");
        testUser.setUsername("propertytest");
        testUser.setEmail("property@test.com");
        testUser.setPassword("Password123!");

        userRepository.deleteAll();
        propertyRepository.deleteAll();

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail(testUser.getUsername());
        loginRequest.setPassword(testUser.getPassword());

        MvcResult result = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        AuthResponse response = objectMapper.readValue(content, AuthResponse.class);
        authToken = response.getToken();

        MockMultipartFile textFile = new MockMultipartFile(
                "file", "test.glb", MediaType.TEXT_PLAIN_VALUE, "test data".getBytes());
        MockMultipartFile imageFile = new MockMultipartFile(
                "images", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image".getBytes());

        mockMvc.perform(multipart("/api/v1/properties/upload-direct")
                        .file(textFile)
                        .file(imageFile)
                        .param("title", "Test Property")
                        .param("description", "Test Description")
                        .param("address", "Test Address")
                        .param("price", "150000.0")
                        .param("area", "100.0")
                        .param("rooms", "3")
                        .param("propertyType", "APARTMENT")
                        .param("constructionType", "BRICK")
                        .param("district", "CENTER")
                        .param("phoneNumber", "1234567890")
                        .param("email", "property@test.com")
                        .param("floor", "5")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        MvcResult propertiesResult = mockMvc.perform(get("/api/v1/properties/my")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();

        String propertiesContent = propertiesResult.getResponse().getContentAsString();
        PropertyDTO[] properties = objectMapper.readValue(propertiesContent, PropertyDTO[].class);
        if (properties.length > 0) {
            propertyId = properties[0].getId();
        }
    }

    @AfterEach
    void tearDown() {
        propertyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getAllProperties_ShouldReturnProperties() throws Exception {
        mockMvc.perform(get("/api/v1/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Property"));
    }

    @Test
    void getPropertyById_ShouldReturnProperty() throws Exception {
        mockMvc.perform(get("/api/v1/properties/" + propertyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Property"))
                .andExpect(jsonPath("$.propertyType").value("APARTMENT"));
    }

    @Test
    void getUserProperties_ShouldReturnUserProperties() throws Exception {
        mockMvc.perform(get("/api/v1/properties/my")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Property"));
    }

    @Test
    void updateProperty_ShouldUpdatePropertyData() throws Exception {
        PropertyDTO updatedProperty = new PropertyDTO();
        updatedProperty.setTitle("Updated Property");
        updatedProperty.setDescription("Updated Description");
        updatedProperty.setAddress("Updated Address");
        updatedProperty.setPrice(200000.0);
        updatedProperty.setArea(120.0);
        updatedProperty.setRooms(4);
        updatedProperty.setPropertyType("APARTMENT");
        updatedProperty.setConstructionType("BRICK");
        updatedProperty.setDistrict("CENTER");
        updatedProperty.setPhoneNumber("0987654321");
        updatedProperty.setEmail("updated@test.com");
        updatedProperty.setFloor(3);

        mockMvc.perform(put("/api/v1/properties/" + propertyId)
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProperty)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated successfully"));

        mockMvc.perform(get("/api/v1/properties/" + propertyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Property"));
    }

    @Test
    void deleteProperty_ShouldDeleteProperty() throws Exception {
        mockMvc.perform(delete("/api/v1/properties/" + propertyId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted successfully"));

        mockMvc.perform(get("/api/v1/properties/my")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
