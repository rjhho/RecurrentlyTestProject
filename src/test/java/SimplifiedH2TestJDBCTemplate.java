import org.example.Bericht;
import org.jobrunr.configuration.JobRunr;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.cron.Cron;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Configuration
@EnableAutoConfiguration
@ComponentScan("org.example")
@RunWith(SpringRunner.class)
public class SimplifiedH2TestJDBCTemplate {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private StorageProvider storageProvider;

    @Before
    public void setup() {
        storageProvider = new InMemoryStorageProvider();

        JobRunr
                .configure()
                .useStorageProvider(storageProvider)
                .useBackgroundJobServer()
                .initialize();
    }

    public Bericht getBericht() {
        String sql = "SELECT * FROM BERICHTEN";
        return (Bericht) jdbcTemplate.queryForObject(
                sql,
                new BeanPropertyRowMapper(Bericht.class));
    }

    public int getCountOfBerichten() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM BERICHTEN", Integer.class);
    }

    @Test
    public void verwijderInDatabase() throws InterruptedException, SQLException {
        System.out.println("jdbc: " + jdbcTemplate);
        System.out.println("amount before update: " + getCountOfBerichten());
        String sql = "UPDATE Berichten SET message='TEST2'";
        Bericht beforeUpdate = getBericht();
        System.out.println("message before update: " + beforeUpdate.getMessage());
        BackgroundJob.scheduleRecurrently("id_taak_1", Cron.every15seconds(), () -> {
            jdbcTemplate.update(sql);
        });
        Thread.sleep(20000);
        Bericht afterUpdate = getBericht();
        System.out.println("amount before update: " + getCountOfBerichten());
        System.out.println("message after update: " + afterUpdate);

        assertEquals("TEST2", afterUpdate.getMessage());
    }

    @After
    public void stopJobRunr() {
        JobRunr.destroy();
    }
}
