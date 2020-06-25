package studio.xmatrix.minecraft.coral.config.entity;

import com.fasterxml.jackson.annotation.JsonMerge;
import lombok.Data;
import studio.xmatrix.minecraft.coral.config.validate.IValidator;
import studio.xmatrix.minecraft.coral.config.validate.Validator;
import studio.xmatrix.minecraft.coral.config.validate.ValidatorException;

@Data
public class Config implements IValidator {

    private static final String TRANSLATION_KEY = "translation";
    private static final String COMMAND_KEY = "command";
    private static final String FUNCTION_KEY = "function";

    @JsonMerge
    private Translation translation;
    @JsonMerge
    private Command command;
    @JsonMerge
    private Function function;

    @Override
    public void validate() throws ValidatorException {
        // translation
        Validator.notNull(TRANSLATION_KEY, translation);
        translation.validate();
        // command
        Validator.notNull(COMMAND_KEY, command);
        command.validate();
        // function
        Validator.notNull(FUNCTION_KEY, function);
        function.validate();
    }
}
