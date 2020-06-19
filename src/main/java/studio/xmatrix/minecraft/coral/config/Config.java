package studio.xmatrix.minecraft.coral.config;

import com.fasterxml.jackson.annotation.JsonMerge;
import lombok.Data;

@Data
public class Config {

    @JsonMerge
    private Translation translation;

    @JsonMerge
    private Function function;

    @Data
    public static class Translation {
        private String region;
        private String customLangFile;
        private String customStyleFile;
    }

    @Data
    public static class Function {
        private Boolean msgCallSleep;
    }
}
