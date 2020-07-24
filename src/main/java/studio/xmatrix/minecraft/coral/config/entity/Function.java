package studio.xmatrix.minecraft.coral.config.entity;

import lombok.Data;
import studio.xmatrix.minecraft.coral.config.validate.IValidator;
import studio.xmatrix.minecraft.coral.config.validate.Validator;
import studio.xmatrix.minecraft.coral.config.validate.ValidatorException;

@Data
public class Function implements IValidator {

    private static final String MSG_CALL_SLEEP_KEY = "function.msgCallSleep";
    private static final String MSG_DEATH_INFO_KEY = "function.msgDeathInfo";
    private static final String MSG_MOTD_KEY = "function.msgMotd";
    private static final String MSG_MOTD_ENABLED_KEY = "function.msgMotd.enabled";
    private static final String MSG_MOTD_TEXT_KEY = "function.msgMotd.text";

    private Boolean msgCallSleep;
    private Boolean msgDeathInfo;
    private MsgMotd msgMotd;

    @Override
    public void validate() throws ValidatorException {
        // msgCallSleep
        Validator.notNull(MSG_CALL_SLEEP_KEY, msgCallSleep);
        // msgDeathInfo
        Validator.notNull(MSG_DEATH_INFO_KEY, msgDeathInfo);
        // msgMotd
        Validator.notNull(MSG_MOTD_KEY, msgMotd);
        Validator.notNull(MSG_MOTD_ENABLED_KEY, msgMotd.enabled);
        Validator.notNull(MSG_MOTD_TEXT_KEY, msgMotd.text);
    }

    @Data
    public static class MsgMotd {
        private Boolean enabled;
        private String text;
    }
}
