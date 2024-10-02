package br.com.innoact.multi_tanancy.infra.configs;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Optional;

public class TenantAwareRoutingDataSource extends AbstractRoutingDataSource {
    /**
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return Optional.ofNullable(TenantContext.getInstance().getCurrentTenant()).orElse(TenantContext.DEFAULT_TENANT);
    }
}
