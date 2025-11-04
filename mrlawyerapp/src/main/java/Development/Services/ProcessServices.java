package Development.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Development.Model.Process;
import Development.Repository.ProcessRepository;

@Service
public class ProcessServices implements IProcessServices{
    @Autowired
    private ProcessRepository processRepository;
    
    @Override
    public List<Process> listProcess(){
        return this.processRepository.findAll();
    }

    @Override
    public Process saveProcess(Process process) {
        return this.processRepository.save(process);
    }

    @Override
    public void deleteProcess(String id) {
        this.processRepository.deleteById(id);
    }

    @Override
    public Process searchByIdProcess(String id) {
        return this.processRepository.findById(id).orElse(null);
    }
    
}
