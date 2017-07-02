/*
 * Author A.Vilkov, Copyright (c) 2017
 */

package org.dclou.example.demogpb.order.config;

import com.codahale.metrics.MetricRegistry;
import com.dripcloud.core.metrics.prometheus.PrometheusEndpointContextConfiguration;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.StandardExports;
import io.prometheus.client.spring.boot.EnablePrometheusEndpoint;
import io.prometheus.client.spring.boot.EnableSpringBootMetricsCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

/**
 * @author Artem Vilkov at 25.06.17.
 */
@Configuration
@EnablePrometheusEndpoint
@EnableSpringBootMetricsCollector
public class MetricsConfig {

	private MetricRegistry dropwizardMetricRegistry;

	@Autowired
	public MetricsConfig(MetricRegistry dropwizardMetricRegistry) {
		this.dropwizardMetricRegistry = dropwizardMetricRegistry;
	}

	@PostConstruct
	public void registerPrometheusCollectors() {
		CollectorRegistry.defaultRegistry.clear();
		new StandardExports().register();
		new MemoryPoolsExports().register();
		new DropwizardExports(dropwizardMetricRegistry).register();
	}
}