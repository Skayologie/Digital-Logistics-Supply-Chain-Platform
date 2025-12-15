package com.project.supplychain.services;

import com.project.supplychain.DTOs.supplierDTOs.SupplierDTO;
import com.project.supplychain.mappers.SupplierMapper;
import com.project.supplychain.models.Supplier;
import com.project.supplychain.repositories.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierService supplierService;

    @Test
    void createSupplier_success_returnsDto() {
        SupplierDTO dto = new SupplierDTO();
        dto.setName("S1");
        Supplier entity = new Supplier();
        Supplier saved = new Supplier();
        saved.setId(UUID.randomUUID());
        saved.setName("S1");

        when(supplierMapper.toEntity(dto)).thenReturn(entity);
        when(supplierRepository.save(entity)).thenReturn(saved);
        when(supplierMapper.toDTO(saved)).thenReturn(dto);

        var res = supplierService.createSupplier(dto);
        assertThat(res).containsKey("message");
        assertThat(res.get("supplier")).isEqualTo(dto);
    }

    @Test
    void getSupplier_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(supplierRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> supplierService.getSupplier(id)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void listSuppliers_returnsList() {
        Supplier s = new Supplier(); s.setId(UUID.randomUUID()); s.setName("S");
        SupplierDTO dto = new SupplierDTO(); dto.setName("S");
        when(supplierRepository.findAll()).thenReturn(List.of(s));
        when(supplierMapper.toDTO(s)).thenReturn(dto);

        var res = supplierService.listSuppliers();
        assertThat(res).containsKey("suppliers");
    }

    @Test
    void updateSupplier_success_updatesAndReturns() {
        UUID id = UUID.randomUUID();
        Supplier existing = new Supplier(); existing.setId(id); existing.setName("Old");
        SupplierDTO dto = new SupplierDTO(); dto.setName("New"); dto.setContactInfo("C");
        Supplier saved = new Supplier(); saved.setId(id); saved.setName("New"); saved.setContactInfo("C");

        when(supplierRepository.findById(id)).thenReturn(Optional.of(existing));
        when(supplierRepository.save(existing)).thenReturn(saved);
        when(supplierMapper.toDTO(saved)).thenReturn(dto);

        var res = supplierService.updateSupplier(id, dto);
        assertThat(res).containsEntry("message", "Supplier updated successfully");
        assertThat(res.get("supplier")).isEqualTo(dto);
    }

    @Test
    void deleteSupplier_success_deletes() {
        UUID id = UUID.randomUUID();
        Supplier existing = new Supplier(); existing.setId(id);
        when(supplierRepository.findById(id)).thenReturn(Optional.of(existing));

        var res = supplierService.deleteSupplier(id);
        assertThat(res).containsEntry("message", "Supplier deleted successfully");
        verify(supplierRepository).delete(existing);
    }
}
