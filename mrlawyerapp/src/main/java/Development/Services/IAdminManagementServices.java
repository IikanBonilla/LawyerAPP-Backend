package Development.Services;

import java.util.Map;

public interface IAdminManagementServices {
    public void suspendAdmin(String idAdmin);
    public void activateAdmin(String idAdmin);
    public void inactivateAdmin(String idAdmin);
    public Map<String, Object> getAdminStatus(String idAdmin);
}
