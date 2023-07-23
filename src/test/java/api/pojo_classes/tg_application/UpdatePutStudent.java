package api.pojo_classes.tg_application;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePutStudent {
    private String firstName;
    private String lastName;
    private String email;
    private String DOB;
}