package Development.Controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import Development.Model.Audience;
import Development.Services.AudienceServices;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



//Define controller
@RestController
//URL + LocalHost
@RequestMapping("audienceApi")
//Direction Angular
@CrossOrigin(origins = "*")
public class AudienceController {
    private Logger logger = LoggerFactory.getLogger(Audience.class);
    @Autowired
    private AudienceServices audienceService;

    @GetMapping("path")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    @PostMapping("path")
    public String postMethodName(@RequestBody String entity) {
        //TODO: process POST request
        
        return entity;
    }
    @DeleteMapping("/audience")
    public void deleteAudience(@RequestParam String id){
        //TODO: AUDIENCE DELETE REQUEST
    }
    @PutMapping("/{id}/put")
    public String putMethodName(@PathVariable String id, @RequestBody String entity) {
        //TODO: process PUT request
        
        return entity;
    }
    
    

}
