package net.ensah.projetplateform.repository;

import net.ensah.projetplateform.entities.Dataset;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;

import javax.xml.crypto.Data;

public interface DatasetRepository extends JpaAttributeConverter<Dataset, Long> {
}
