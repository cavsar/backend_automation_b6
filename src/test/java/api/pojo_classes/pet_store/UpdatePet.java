package api.pojo_classes.pet_store;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class UpdatePet {

    private int id;
    private Category category;
    private String name;
    private List<String> photoUrls;
    private List<Tags> tags;
    private String status;

}
