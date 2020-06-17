package studio.xmatrix.minecraft.coral.config;

import com.fasterxml.jackson.annotation.JsonMerge;
import lombok.Data;

@Data
public class Config {

    @JsonMerge
    private Function function;

    @Data
    public static class Function {
        private Boolean msgCallSleep;
    }
}
