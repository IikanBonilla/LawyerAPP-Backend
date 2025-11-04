package Development.Services;
import java.util.List;

import Development.Model.Process;
public interface IProcessServices {
    public List<Process> listProcess();
    public Process searchByIdProcess(String id);
    public Process saveProcess(Process process);
    public void deleteProcess(String id);

}
