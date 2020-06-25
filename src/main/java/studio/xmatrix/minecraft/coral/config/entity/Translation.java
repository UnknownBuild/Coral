package studio.xmatrix.minecraft.coral.config.entity;

import lombok.Data;
import studio.xmatrix.minecraft.coral.config.validate.IValidator;
import studio.xmatrix.minecraft.coral.config.validate.Validator;
import studio.xmatrix.minecraft.coral.config.validate.ValidatorException;

@Data
public class Translation implements IValidator {

    private static final String REGION_KEY = "translation.region";
    private static final String[] REGIONS = {"en_US", "zh_CN"};
    private static final String CUSTOM_LANG_FILE_KEY = "translation.customLangFile";
    private static final String CUSTOM_STYLE_FILE_KEY = "translation.customStyleFile";

    private String region;
    private String customLangFile;
    private String customStyleFile;


    public void validate() throws ValidatorException {
        // region
        Validator.notNull(REGION_KEY, region);
        Validator.in(REGION_KEY, region, REGIONS);
        // customLangFile
        // customStyleFile
    }
}
