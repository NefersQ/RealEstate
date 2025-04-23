package demo.RealEstate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.RealEstate.dto.PropertyDTO;
import demo.RealEstate.jwt.JwtUtil;
import demo.RealEstate.services.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PropertyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PropertyService propertyService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private PropertyController propertyController;

    private ObjectMapper objectMapper;
    private PropertyDTO testPropertyDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(propertyController).build();
        objectMapper = new ObjectMapper();

        testPropertyDTO = new PropertyDTO();
        testPropertyDTO.setId(1L);
        testPropertyDTO.setTitle("Test Property");
        testPropertyDTO.setDescription("This is a test property");
        testPropertyDTO.setAddress("123 Test Street");
        testPropertyDTO.setPrice(100000.0);
        testPropertyDTO.setArea(120.0);
        testPropertyDTO.setRooms(3);
        testPropertyDTO.setPropertyType("APARTMENT");
        testPropertyDTO.setConstructionType("BRICK");
        testPropertyDTO.setDistrict("CENTER");
        testPropertyDTO.setPhoneNumber("1234567890");
        testPropertyDTO.setEmail("test@example.com");
    }

    @Test
    void uploadProperty_ShouldReturnSuccess_WhenValid() throws Exception {
        when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1L);
        doNothing().when(propertyService).savePropertyFromRequest(
                anyString(), anyString(), anyString(), anyDouble(), anyDouble(), anyInt(),
                anyString(), anyString(), anyString(), anyString(), anyString(),
                any(), any(), any(), any(), anyLong());

        MockMultipartFile file = new MockMultipartFile(
                "file", "model.glb", "application/octet-stream", "model data".getBytes());
        MockMultipartFile image = new MockMultipartFile(
                "images", "image.jpg", "image/jpeg", "image data".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/properties/upload-direct")
                        .file(file)
                        .file(image)
                        .param("title", "Test Property")
                        .param("description", "Test Description")
                        .param("address", "Test Address")
                        .param("price", "100000.0")
                        .param("area", "120.0")
                        .param("rooms", "3")
                        .param("propertyType", "APARTMENT")
                        .param("constructionType", "BRICK")
                        .param("district", "CENTER")
                        .param("phoneNumber", "1234567890")
                        .param("email", "test@example.com")
                        .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(content().string("Property uploaded successfully."));
    }

    @Test
    void getUserProperties_ShouldReturnProperties() throws Exception {
        when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1L);
        when(propertyService.getPropertiesByUser(1L)).thenReturn(Arrays.asList(testPropertyDTO));

        mockMvc.perform(get("/api/v1/properties/my")
                        .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Property")));
    }

    @Test
    void updateProperty_ShouldReturnSuccess_WhenValid() throws Exception {
        when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1L);
        doNothing().when(propertyService).updateProperty(eq(1L), any(PropertyDTO.class), eq(1L));

        mockMvc.perform(put("/api/v1/properties/1")
                        .header("Authorization", "Bearer validToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPropertyDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated successfully"));
    }

    @Test
    void deleteProperty_ShouldReturnSuccess_WhenValid() throws Exception {
        when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1L);
        doNothing().when(propertyService).deleteProperty(1L, 1L);

        mockMvc.perform(delete("/api/v1/properties/1")
                        .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted successfully"));
    }

    @Test
    void getAllProperties_ShouldReturnAllProperties() throws Exception {
        when(propertyService.getAllProperties()).thenReturn(Arrays.asList(testPropertyDTO));

        mockMvc.perform(get("/api/v1/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Property")));
    }

    @Test
    void getPropertyById_ShouldReturnProperty_WhenExists() throws Exception {
        when(propertyService.getPropertyById(1L)).thenReturn(testPropertyDTO);

        mockMvc.perform(get("/api/v1/properties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Property")));
    }
}