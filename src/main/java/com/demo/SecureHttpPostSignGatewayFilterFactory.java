package com.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SecureHttpPostSignGatewayFilterFactory extends
        AbstractGatewayFilterFactory<SecureHttpPostSignGatewayFilterFactory.Config> {

    private static final Logger logger = LoggerFactory.getLogger(SecureHttpPostSignGatewayFilterFactory.class);
    @Autowired
    private ModifyRequestBodyGatewayFilterFactory modifyRequestBodyFilter;

    public SecureHttpPostSignGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(final Config config) {
        ModifyRequestBodyGatewayFilterFactory.Config rewriteConfig = new ModifyRequestBodyGatewayFilterFactory.Config()
                .setRewriteFunction(
                        String.class,
                        String.class,
                        new SecureHttpPostSignRewriteFunction(config)
                );

        return ((exchange, chain) -> {
            logger.trace("Applied SecureHttpPostSignGatewayFilterFactory on request {}", exchange.getRequest().getId());
            return modifyRequestBodyFilter
                    .apply(rewriteConfig)
                    //Applies the filter on the filter chain.
                    .filter(exchange, chain);
        }
        );
    }

    // YAML CONFIG SHORTHAND
    // ==========================================
    public static class Config {

        protected String confidentialParamKey; //JSON KEY
        protected ConfidentialParameterType confidentialParamType; //TYPE

        public String getConfidentialParamKey() {
            return confidentialParamKey;
        }

        public void setConfidentialParamKey(String confidentialParamKey) {
            this.confidentialParamKey = confidentialParamKey;
        }

        public ConfidentialParameterType getConfidentialParamType() {
            return confidentialParamType;
        }

        public void setConfidentialParamType(ConfidentialParameterType confidentialParamType) {
            this.confidentialParamType = confidentialParamType;
        }
    }
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("confidentialParamKey", "confidentialParamType");
    }
}
