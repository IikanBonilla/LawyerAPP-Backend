package Development.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import Development.Model.Document;

public interface DocumentRepository extends JpaRepository<Document, String>{
    
}
