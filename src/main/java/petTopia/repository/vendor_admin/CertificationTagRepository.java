package petTopia.repository.vendor_admin;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.vendor.CertificationTag;

public interface CertificationTagRepository extends JpaRepository<CertificationTag, Integer> {

	Optional<CertificationTag> findByTagName(String tagName);
}
