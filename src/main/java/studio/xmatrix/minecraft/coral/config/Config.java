package studio.xmatrix.minecraft.coral.config;

import com.fasterxml.jackson.annotation.JsonMerge;
import lombok.Data;

@Data
public class Config {

    @JsonMerge
    private Translation translation;

    @JsonMerge
    private Command command;

    @JsonMerge
    private Function function;

    @Data
    public static class Translation {
        private String region;
        private String customLangFile;
        private String customStyleFile;
    }

    @Data
    public static class Command {

        @JsonMerge
        private Here here;

        private Boolean wru;

        @Data
        public static class Here {
            private Boolean enabled;
            private String duration;
        }
    }

    @Data
    public static class Function {
        private Boolean msgCallSleep;
    }
}
