import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Case {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String date;

    @JsonProperty("placeOfEvent")
    private String placeOfEvent;

    @JsonProperty("namesOfVictims")
    private List<String> namesOfVictims;

    @JsonProperty("investigatorId")
    private int investigatorId;

    @Getter
    @JsonProperty("charges")
    private List<String> charges;

    public List<String> getCharges() {
        return this.charges;
    }

    public List<String> getAttributeValues(String attribute) {
        List<String> values = new ArrayList<>();
        try {
            Field field = this.getClass().getDeclaredField(attribute);
            field.setAccessible(true);

            Object value = field.get(this);

            if (value instanceof List<?>) {
                for (Object obj : (List<?>) value) {
                    values.add(obj.toString());
                }
            } else {
                values.add(value.toString());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return values;
    }
}
