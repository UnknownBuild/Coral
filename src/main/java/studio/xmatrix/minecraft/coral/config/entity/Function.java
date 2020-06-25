package studio.xmatrix.minecraft.coral.config.entity;

import lombok.Data;
import studio.xmatrix.minecraft.coral.config.validate.IValidator;
import studio.xmatrix.minecraft.coral.config.validate.Validator;
import studio.xmatrix.minecraft.coral.config.validate.ValidatorException;

@Data
public class Function implements IValidator {

    private static final String MSG_CALL_SLEEP_KEY = "function.msgCallSleep";
    private static final String MSG_DEATH_INFO_KEY = "function.msgDeathInfo";

    private Boolean msgCallSleep;
    private Boolean msgDeathInfo;

    @Override
    public void validate() throws ValidatorException {
        // msgCallSleep
        Validator.notNull(MSG_CALL_SLEEP_KEY, msgCallSleep);
        // msgDeathInfo
        Validator.notNull(MSG_DEATH_INFO_KEY, msgDeathInfo);
    }
}
