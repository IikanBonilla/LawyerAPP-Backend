package Development.Controller;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

@RestController
public class MetricsController {
    
    @Autowired
    private DataSource dataSource;
    
    @GetMapping("/metrics/db-pool")
    public Map<String, Object> getPoolMetrics() {
        HikariDataSource hikari = (HikariDataSource) dataSource;
        HikariPoolMXBean pool = hikari.getHikariPoolMXBean();
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("activeConnections", pool.getActiveConnections());
        metrics.put("idleConnections", pool.getIdleConnections());
        metrics.put("totalConnections", pool.getTotalConnections());
        metrics.put("threadsAwaitingConnection", pool.getThreadsAwaitingConnection());
        metrics.put("maxPoolSize", hikari.getMaximumPoolSize());
        
        return metrics;
    }
}
