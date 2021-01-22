package com.garyclayburg.upbanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <br><br>
 * Created 2020-12-18 15:46
 *
 * @author Gary Clayburg
 */
public class CgroupCpuStats {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(CgroupCpuStats.class);
    public static final int DEFAULT_VALUE = -42;
    public static final String DEFAULT_VALUE_STRING = "";
    long cfs_period_us;
    long cfs_quota_us;
    long cpu_shares;
    long cpuacct_usage;
    String cpuacct_usage_percpu;
    String cpuset;
    long nr_periods;
    long nr_throttled;
    long throttled_time;

    public CgroupCpuStats() {
        cfs_period_us = DEFAULT_VALUE;
        cfs_quota_us = DEFAULT_VALUE;
        cpu_shares = DEFAULT_VALUE;
        cpuacct_usage = DEFAULT_VALUE;
        cpuacct_usage_percpu = DEFAULT_VALUE_STRING;
        cpuset = DEFAULT_VALUE_STRING;
        nr_periods = DEFAULT_VALUE;
        nr_throttled = DEFAULT_VALUE;
        throttled_time = DEFAULT_VALUE;
    }

    public long getCfs_period_us() {
        return cfs_period_us;
    }

    public CgroupCpuStats setCfs_period_us(long cfs_period_us) {
        this.cfs_period_us = cfs_period_us;
        return this;
    }

    public long getCfs_quota_us() {
        return cfs_quota_us;
    }

    public CgroupCpuStats setCfs_quota_us(long cfs_quota_us) {
        this.cfs_quota_us = cfs_quota_us;
        return this;
    }

    public long getCpu_shares() {
        return cpu_shares;
    }

    public CgroupCpuStats setCpu_shares(long cpu_shares) {
        this.cpu_shares = cpu_shares;
        return this;
    }

    public long getCpuacct_usage() {
        return cpuacct_usage;
    }

    public CgroupCpuStats setCpuacct_usage(long cpuacct_usage) {
        this.cpuacct_usage = cpuacct_usage;
        return this;
    }

    public String getCpuacct_usage_percpu() {
        return cpuacct_usage_percpu;
    }

    public CgroupCpuStats setCpuacct_usage_percpu(String cpuacct_usage_percpu) {
        this.cpuacct_usage_percpu = cpuacct_usage_percpu;
        return this;
    }

    public String getCpuset() {
        return cpuset;
    }

    public CgroupCpuStats setCpuset(String cpuset) {
        this.cpuset = cpuset;
        return this;
    }

    public long getNr_periods() {
        return nr_periods;
    }

    public CgroupCpuStats setNr_periods(long nr_periods) {
        this.nr_periods = nr_periods;
        return this;
    }

    public long getNr_throttled() {
        return nr_throttled;
    }

    public CgroupCpuStats setNr_throttled(long nr_throttled) {
        this.nr_throttled = nr_throttled;
        return this;
    }

    public long getThrottled_time() {
        return throttled_time;
    }

    public CgroupCpuStats setThrottled_time(long throttled_time) {
        this.throttled_time = throttled_time;
        return this;
    }
}
