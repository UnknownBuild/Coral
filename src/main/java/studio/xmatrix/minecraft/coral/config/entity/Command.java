package studio.xmatrix.minecraft.coral.config.entity;

import com.fasterxml.jackson.annotation.JsonMerge;
import lombok.Data;
import studio.xmatrix.minecraft.coral.config.validate.IValidator;
import studio.xmatrix.minecraft.coral.config.validate.Validator;
import studio.xmatrix.minecraft.coral.config.validate.ValidatorException;

@Data
public class Command implements IValidator {

    private static final String HERE_KEY = "command.here";
    private static final String HERE_ENABLED_KEY = "command.here.enabled";
    private static final String HERE_DURATION_KEY = "command.here.duration";
    private static final String WRU_KEY = "command.wru";

    @JsonMerge
    private Here here;
    private Boolean wru;

    @Override
    public void validate() throws ValidatorException {
        // here
        Validator.notNull(HERE_KEY, here);
        Validator.notNull(HERE_ENABLED_KEY, here.enabled);
        Validator.notNull(HERE_DURATION_KEY, here.duration);
        Validator.match(HERE_DURATION_KEY, here.duration, "(\\d+m)?\\d+s");
        // wru
        Validator.notNull(WRU_KEY, wru);
    }

    @Data
    public static class Here {

        private Boolean enabled;
        private String duration;

        public int getDuration() {
            int result = 0;
            int index = duration.indexOf('m');
            if (index != -1) {
                result += 60 * Integer.parseInt(duration.substring(0, index));
                result += Integer.parseInt(duration.substring(index + 1, duration.length() - 1));
            } else {
                result += Integer.parseInt(duration.substring(0, duration.length() - 1));
            }
            return result;
        }
    }
}
