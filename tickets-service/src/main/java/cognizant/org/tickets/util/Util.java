package cognizant.org.tickets.util;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class Util {

    public static List<String> getRequestFieldErrors(BindingResult result) {
        List<String> errors = result.getFieldErrors()
                .stream()
                .map(err -> "Field " + err.getField() + " - " + err.getDefaultMessage())
                .collect(Collectors.toList());
        log.info("Errors -> {}", objectToJson(errors));
        return errors;
    }

    public static String objectToJson(Object object) {
        String response = null;
        try {
            response = new Gson().toJson(object);
        } catch (Exception e) {
            log.error("Error creating JSON", e);
        }
        return response;
    }

}
