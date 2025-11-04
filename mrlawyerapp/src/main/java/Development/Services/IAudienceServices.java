package Development.Services;

import java.util.List;

import Development.Model.Audience;

public interface IAudienceServices {
    public List<Audience> listallAudience();
    public Audience saveAudience(Audience audience);
    public Audience searchByIdAudience(String id);
    public void deleteAudience(String id);

}
