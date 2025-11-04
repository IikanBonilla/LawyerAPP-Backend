package Development.Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import Development.Model.Process;
import Development.Services.ProcessServices;



//Controller
@RestController
//url + localhost
@RequestMapping("processApi")
//Direccion de angular
@CrossOrigin(origins = "*")
public class ProcessController {
    private Logger logger = LoggerFactory.getLogger(Process.class);
    @Autowired
    private ProcessServices processService;

    @GetMapping("/process/listall")
    public List<Process> getAllProcess(){
        List<Process> processes = processService.listProcess();
        logger.info("Procesos encontrados {}", processes.size());
        processes.forEach(process -> logger.info("{}", process));
        return processes;
    }

    @PostMapping("/process/save")
    public Process createProcess(@RequestBody Process process){
        //Guarda el proceso (Con el cliente ya asignado)
        Process savedProcess = processService.saveProcess(process);

        //Muestra el proceso
        logger.info("Nuevo proceso: {}", savedProcess);
        return savedProcess;
    }

    @DeleteMapping("/process/delete")
    public void deleteProcess(@RequestParam String id){
        processService.deleteProcess(id);
        logger.info("Proceso eliminado con ID: {}", id);
    }
    
    @GetMapping("/process/getbyid")
    public Process searchByIdProcess(@RequestParam String id) {
        Process process = processService.searchByIdProcess(id);
        logger.info("{}", process);
        return process;

    }
    
    
}