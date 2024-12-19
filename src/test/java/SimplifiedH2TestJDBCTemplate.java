import org.example.Bericht;
import org.example.BerichtService;
import org.jobrunr.configuration.JobRunr;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.cron.Cron;
import org.jobrunr.server.BackgroundJobServerConfiguration;
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
    private BerichtService berichtService;
    private StorageProvider storageProvider;

    @Before
    public void setup() {
        storageProvider = new InMemoryStorageProvider();

        JobRunr
                .configure()
                .useStorageProvider(storageProvider)
                .useBackgroundJobServer(BackgroundJobServerConfiguration.usingStandardBackgroundJobServerConfiguration().andPollIntervalInSeconds(5))
                .initialize();
    }

    @Test
    public void updateBericht() throws InterruptedException {
        System.out.println("berichtService=" + berichtService);

        Bericht beforeUpdate = berichtService.getBericht(20001);
        System.out.println("message before update: " + beforeUpdate.getMessage());
        BackgroundJob.scheduleRecurrently("*/5 * * * * *", () -> berichtService.updateBericht(20001));
        Thread.sleep(10000);
//        berichtService.updateBericht();
        Bericht afterUpdate = berichtService.getBericht(20001);
        assertEquals("TEST2", afterUpdate.getMessage());
    }
    @After
    public void stopJobRunr() {
        JobRunr.destroy();
    }
}
