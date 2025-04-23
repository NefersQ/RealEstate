package demo.RealEstate.services;

import demo.RealEstate.dto.PropertyDTO;
import demo.RealEstate.exception.ApiException;
import demo.RealEstate.mapper.PropertyMapper;
import demo.RealEstate.model.PropertyDAO;
import demo.RealEstate.model.UserDAO;
import demo.RealEstate.repository.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private PropertyMapper propertyMapper;

    @InjectMocks
    private PropertyService propertyService;

    private PropertyDAO testProperty;
    private PropertyDTO testPropertyDTO;
    private UserDAO testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserDAO();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");

        testProperty = new PropertyDAO();
        testProperty.setId(1L);
        testProperty.setTitle("Test Property");
        testProperty.setDescription("Test Description");
        testProperty.setAddress("Test Address");
        testProperty.setPrice(200000.0);
        testProperty.setArea(100.0);
        testProperty.setRooms(3);
        testProperty.setPropertyType(PropertyDAO.PropertyType.APARTMENT);
        testProperty.setConstructionType(PropertyDAO.ConstructionType.BRICK);
        testProperty.setDistrict(PropertyDAO.District.CENTER);
        testProperty.setUser(testUser);

        testPropertyDTO = new PropertyDTO();
        testPropertyDTO.setId(1L);
        testPropertyDTO.setTitle("Test Property");
        testPropertyDTO.setDescription("Test Description");
        testPropertyDTO.setAddress("Test Address");
        testPropertyDTO.setPrice(200000.0);
        testPropertyDTO.setArea(100.0);
        testPropertyDTO.setRooms(3);
        testPropertyDTO.setPropertyType("APARTMENT");
        testPropertyDTO.setConstructionType("BRICK");
        testPropertyDTO.setDistrict("CENTER");
    }

    @Test
    void getPropertiesByUser_ShouldReturnUserProperties() {
        List<PropertyDAO> properties = Arrays.asList(testProperty);
        List<PropertyDTO> propertiesDTO = Arrays.asList(testPropertyDTO);

        when(propertyRepository.findAllByUser_UserId(1L)).thenReturn(properties);
        when(propertyMapper.toDTOList(properties)).thenReturn(propertiesDTO);

        List<PropertyDTO> result = propertyService.getPropertiesByUser(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Property");
    }

    @Test
    void updateProperty_ShouldUpdateProperty_WhenUserIsOwner() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(PropertyDAO.class))).thenReturn(testProperty);

        propertyService.updateProperty(1L, testPropertyDTO, 1L);

        verify(propertyRepository).save(any(PropertyDAO.class));
    }

    @Test
    void updateProperty_ShouldThrowException_WhenUserIsNotOwner() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));

        assertThrows(ApiException.class, () -> {
            propertyService.updateProperty(1L, testPropertyDTO, 2L);
        });
    }

    @Test
    void deleteProperty_ShouldDeleteProperty_WhenUserIsOwner() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));

        propertyService.deleteProperty(1L, 1L);

        verify(propertyRepository).delete(testProperty);
    }

    @Test
    void deleteProperty_ShouldThrowException_WhenUserIsNotOwner() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));

        assertThrows(ApiException.class, () -> {
            propertyService.deleteProperty(1L, 2L);
        });
    }

    @Test
    void getAllProperties_ShouldReturnAllProperties() {
        List<PropertyDAO> properties = Arrays.asList(testProperty);
        List<PropertyDTO> propertiesDTO = Arrays.asList(testPropertyDTO);

        when(propertyRepository.findAll()).thenReturn(properties);
        when(propertyMapper.toDTOList(properties)).thenReturn(propertiesDTO);

        List<PropertyDTO> result = propertyService.getAllProperties();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Property");
    }

    @Test
    void getPropertyById_ShouldReturnProperty_WhenExists() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));
        when(propertyMapper.toDTO(testProperty)).thenReturn(testPropertyDTO);

        PropertyDTO result = propertyService.getPropertyById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Property");
    }
}