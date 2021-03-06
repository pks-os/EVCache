package com.netflix.evcache;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.netflix.archaius.api.annotations.ConfigurationSource;
import com.netflix.evcache.event.hotkey.HotKeyListener;
import com.netflix.evcache.event.throttle.ThrottleListener;
import com.netflix.evcache.metrics.EVCacheMetricsFactory;
import com.netflix.evcache.pool.EVCacheClientPoolManager;
import com.netflix.servo.monitor.LongGauge;
import com.netflix.servo.tag.BasicTagList;

@Singleton
public class EVCacheModule extends AbstractModule {

    public EVCacheModule() {
    }

    @Singleton
    @ConfigurationSource("evcache")
    public static class EVCacheModuleConfigLoader {
    }


    @Override
    protected void configure() {
        bind(EVCacheModuleConfigLoader.class).asEagerSingleton();
        bind(EVCacheClientPoolManager.class).asEagerSingleton();
        
        bind(HotKeyListener.class).asEagerSingleton();
        bind(ThrottleListener.class).asEagerSingleton();
        bind(VersionTracker.class).asEagerSingleton();
        

        // Make sure connection factory provider Module is initialized in your Module when you init EVCacheModule 
        // bind(IConnectionFactoryProvider.class).toProvider(DefaultFactoryProvider.class);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
 
    @Override
    public boolean equals(Object obj) {
        return (obj != null) && (obj.getClass() == getClass());
    }

    @Singleton
    private static class VersionTracker {
        private VersionTracker() {
        	
            final String fullVersion;
            final String jarName;
            if(this.getClass().getPackage().getImplementationVersion() != null) {
                fullVersion = this.getClass().getPackage().getImplementationVersion();
            } else {
                fullVersion = "unknown";
            }
            if(this.getClass().getPackage().getImplementationTitle() != null) {
                jarName = this.getClass().getPackage().getImplementationTitle();
            } else {
                jarName = "unknown";
            }

            EVCacheMetricsFactory.getLongGauge("evcache-client", BasicTagList.of("version", fullVersion, "jarName", jarName)).set(Long.valueOf(1));
        }
    }
}