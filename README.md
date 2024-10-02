Para criar uma configuração de datasources que seja dinâmica e não exija a adição manual de novos inquilinos (tenants) no application.yml, você pode usar um banco de dados para armazenar as informações dos tenants e, em seguida, carregar essas informações em tempo de execução. Abaixo está um exemplo de como implementar isso:

1. Armazenar Configurações dos Tenants em um Banco de Dados
Crie uma tabela no seu banco de dados para armazenar as informações dos tenants. Por exemplo:

sql
Copiar código
CREATE TABLE tenants (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    driver_class_name VARCHAR(255) NOT NULL
);
2. Entidade Tenant
Crie uma entidade para mapear a tabela tenants:

java
Copiar código
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String url;
    private String username;
    private String password;
    private String driverClassName;

    // Getters e setters
}
3. Repositório para Tenant
Crie um repositório para acessar as informações dos tenants:

java
Copiar código
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Tenant findByName(String name);
}
4. Configuração do DataSource Dinâmico
Implemente um datasource dinâmico que carrega as informações do banco de dados:

java
Copiar código
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Autowired
    private TenantRepository tenantRepository;

    @Bean
    public DataSource dataSource() {
        AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return TenantContext.getCurrentTenant();
            }
        };

        List<Tenant> tenants = tenantRepository.findAll();
        Map<Object, Object> targetDataSources = new HashMap<>();

        for (Tenant tenant : tenants) {
            DataSourceProperties properties = new DataSourceProperties();
            properties.setUrl(tenant.getUrl());
            properties.setUsername(tenant.getUsername());
            properties.setPassword(tenant.getPassword());
            properties.setDriverClassName(tenant.getDriverClassName());

            targetDataSources.put(tenant.getName(), properties.initializeDataSourceBuilder().build());
        }

        routingDataSource.setTargetDataSources(targetDataSources);
        return routingDataSource;
    }
}
5. Contexto do Inquilino
A classe TenantContext permanece a mesma que descrevi anteriormente:

java
Copiar código
public class TenantContext {
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    public static void setCurrentTenant(String tenant) {
        currentTenant.set(tenant);
    }

    public static void clear() {
        currentTenant.remove();
    }
}
6. Uso
Como antes, você deve definir o inquilino atual antes de realizar operações de banco de dados:

java
Copiar código
@Service
public class MyService {

    public void someMethod(String tenantId) {
        TenantContext.setCurrentTenant(tenantId);
        // Executar operações de banco de dados
        // ...
        TenantContext.clear();
    }
}
Vantagens dessa Abordagem
Dinamismo: Você pode adicionar novos tenants diretamente no banco de dados sem precisar modificar o código ou a configuração.
Escalabilidade: A solução pode ser facilmente escalada para suportar um número indefinido de tenants.
Gerenciamento Centralizado: Todas as configurações dos tenants estão em um único lugar (o banco de dados), facilitando a manutenção.
Considerações Finais
Certifique-se de que a lógica para determinar o tenant atual esteja adequada ao seu aplicativo, que pode ser baseada em cabeçalhos HTTP, parâmetros de consulta ou outros critérios. Além disso, lembre-se de lidar adequadamente com a segurança das credenciais armazenadas.