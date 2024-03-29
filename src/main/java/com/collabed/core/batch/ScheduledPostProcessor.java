package com.collabed.core.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@Component
public class ScheduledPostProcessor {
    private final JobLauncher launcher;

    @Qualifier("postProcessJob")
    private final Job postProcessor;

    @Autowired
    public ScheduledPostProcessor(JobLauncher launcher, Job postProcessor) {
        this.launcher = launcher;
        this.postProcessor = postProcessor;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void launch() throws Exception {
        var params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();

        launcher.run(postProcessor, params);
    }
}
