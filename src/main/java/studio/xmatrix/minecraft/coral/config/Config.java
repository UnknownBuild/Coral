package studio.xmatrix.minecraft.coral.config;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import studio.xmatrix.minecraft.coral.config.validate.IValidator;
import studio.xmatrix.minecraft.coral.config.validate.Validator;
import studio.xmatrix.minecraft.coral.config.validate.ValidatorException;

@Data
@AllArgsConstructor
public class Config implements IValidator {

    private static final String[] REGIONS = {"en_US", "zh_CN"};

    @SerializedName("command.here")
    private Boolean commandHere;
    @SerializedName("command.here.duration")
    private String commandHereDuration;

    @SerializedName("command.wru")
    private Boolean commandWru;

    @SerializedName("function.msgCallSleep")
    private Boolean functionMsgCallSleep;

    @SerializedName("function.msgDeathInfo")
    private Boolean functionMsgDeathInfo;

    @SerializedName("function.msgMotd")
    private Boolean functionMsgMotd;
    @SerializedName("function.msgMotd.text")
    private String functionMsgMotdText;

    @SerializedName("translation.customLangFile")
    private String translationCustomLangFile;
    @SerializedName("translation.customStyleFile")
    private String translationCustomStyleFile;
    @SerializedName("translation.region")
    private String translationRegion;

    @Override
    public void validate() throws ValidatorException {
        Validator.notNull("command.here", commandHere);
        Validator.notNull("command.here.duration", commandHereDuration);
        Validator.match("command.here.duration", commandHereDuration, "(\\d+m)?\\d+s");
        Validator.notNull("command.wru", commandWru);

        Validator.notNull("function.msgCallSleep", functionMsgCallSleep);
        Validator.notNull("function.msgDeathInfo", functionMsgDeathInfo);
        Validator.notNull("function.msgMotd", functionMsgMotd);;
        Validator.notNull("function.msgMotd.text", functionMsgMotdText);

        Validator.notNull("translation.customLangFile", translationCustomLangFile);
        Validator.notNull("translation.customStyleFile", translationCustomStyleFile);
        Validator.notNull("translation.region", translationRegion);
        Validator.in("translation.region", translationRegion, REGIONS);
    }

    public int getCommandHereDuration() {
        int result = 0;
        int index = commandHereDuration.indexOf('m');
        if (index != -1) {
            result += 60 * Integer.parseInt(commandHereDuration.substring(0, index));
            result += Integer.parseInt(commandHereDuration.substring(index + 1, commandHereDuration.length() - 1));
        } else {
            result += Integer.parseInt(commandHereDuration.substring(0, commandHereDuration.length() - 1));
        }
        return result;
    }

    public String getTranslationRegion() {
        return translationRegion.toLowerCase();
    }
}
