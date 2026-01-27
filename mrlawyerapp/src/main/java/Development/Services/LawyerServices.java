package Development.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Development.DTOs.GetLawyerDTO;
import Development.Model.LawyerProfile;
import Development.Model.Status;
import Development.Repository.LawFirmRepository;
import Development.Repository.LawyerRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class LawyerServices implements ILawyerServices {

    @Autowired
    private LawyerRepository lawyerRepository;

    @Autowired
    private LawFirmRepository firmRepository;

    @Override
    public List<GetLawyerDTO> getLawyersByFirm(String idFirm) {
        if(!firmRepository.existsById(idFirm)){
            throw new EntityNotFoundException("No existe firma con id: " + idFirm);
        }
        return this.lawyerRepository.findAllLawyersByFirm(idFirm);
    }

    @Override
    public void updateLawyerStatus(String idLawyer, Status status) {
        LawyerProfile lawyer = lawyerRepository.findById(idLawyer).orElseThrow(
            () -> new EntityNotFoundException("No existe un abogado con id: " + idLawyer)
            );

        lawyer.setStatus(status);
        lawyerRepository.save(lawyer);
        
    }

    @Override
    public void deleteLawyer(String idLawyer) {
         if (!lawyerRepository.existsById(idLawyer)) {
        throw new EntityNotFoundException("No existe un abogado con id: " + idLawyer);
        }
    
        
    this.lawyerRepository.deleteById(idLawyer);
    }

    @Override
    public LawyerProfile findLawyerByUsername(String username) {
        try{
        return lawyerRepository.findByUserUsername(username);
        } catch (Exception ex){
            throw new EntityNotFoundException("No existe un abogado con username: " + username);
        }
    }

    @Override
    public GetLawyerDTO getLawyerByIdUser(String idUser) {
        if(idUser == null || idUser.isEmpty()){
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo o vacío");
        }
        return lawyerRepository.findByIdUser(idUser);
    }
    
}
