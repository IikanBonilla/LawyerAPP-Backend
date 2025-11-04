package Development.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Development.Model.Audience;
import Development.Repository.AudienceRepository;

@Service
public class AudienceServices implements IAudienceServices{

    @Autowired
    private AudienceRepository audienceRepository;
    @Override
    public List<Audience> listallAudience() {
        return this.audienceRepository.findAll();
    }

    @Override
    public Audience saveAudience(Audience audience) {
        return this.audienceRepository.save(audience);
    }

    @Override
    public Audience searchByIdAudience(String id) {
        return this.audienceRepository.findById(id).orElse(null);
    }
    
    @Override
    public void deleteAudience(String id){
        this.audienceRepository.deleteById(id);
    }
}
