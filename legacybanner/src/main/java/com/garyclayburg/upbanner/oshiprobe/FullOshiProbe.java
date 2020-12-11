package com.garyclayburg.upbanner.oshiprobe;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OSSession;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

/**
 * <br><br>
 * Created 2020-12-10 17:47
 *
 * @author Gary Clayburg
 */
public class FullOshiProbe extends OshiProbe {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(FullOshiProbe.class);

    @Override
    public void createReport(StringBuilder probeOut) {
        log.info("probing environment");
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor cpu = hal.getProcessor();
        int physicalProcessorCount = cpu.getPhysicalProcessorCount();

        OperatingSystem operatingSystem = si.getOperatingSystem();
        probeOut.append("=============").append(System.lineSeparator());
        probeOut.append("  Host System").append(System.lineSeparator());
        ComputerSystem computerSystem = hal.getComputerSystem();
        printComputerSystem(computerSystem, probeOut);
        probeOut.append("=============").append(System.lineSeparator());
        probeOut.append("  CPU").append(System.lineSeparator());
        probeOut.append("physical cpu core count: ").append(physicalProcessorCount).append(System.lineSeparator());
        probeOut.append("logical cpu count: ").append(cpu.getLogicalProcessorCount()).append(System.lineSeparator());
        probeOut.append("model: ").append(hal.getComputerSystem().getModel()).append(System.lineSeparator());
        probeOut.append("=============").append(System.lineSeparator());
        probeOut.append("  Processor").append(System.lineSeparator());
        probeOut.append(hal.getProcessor().toString()).append(System.lineSeparator());
        probeOut.append("=============").append(System.lineSeparator());
        probeOut.append("  Memory").append(System.lineSeparator());
        printMemory(hal.getMemory(), probeOut);
        probeOut.append("=============").append(System.lineSeparator());
        probeOut.append("VM or container name: ").append(DetectVM.identifyVM()).append(System.lineSeparator());
        probeOut.append("=============").append(System.lineSeparator());
        probeOut.append("  Operating System").append(System.lineSeparator());
        printOperatingSystem(operatingSystem, probeOut);
    }

    private void printMemory(GlobalMemory memory, StringBuilder probeOut) {
        probeOut.append(memory.toString()).append(System.lineSeparator());
        probeOut.append(memory.getVirtualMemory().toString()).append(System.lineSeparator());

        List<PhysicalMemory> pmList = memory.getPhysicalMemory();
        if (!pmList.isEmpty()) {
            for (PhysicalMemory pm : pmList) {
                probeOut.append(" ").append(pm.toString()).append(System.lineSeparator());
            }
        }
    }

    private void printComputerSystem(ComputerSystem computerSystem, StringBuilder probeOut) {
        probeOut.append(computerSystem.toString()).append(System.lineSeparator());
        probeOut.append("Firmware: ").append(computerSystem.getFirmware().toString()).append(System.lineSeparator());
        probeOut.append("Baseboard: ").append(computerSystem.getBaseboard().toString()).append(System.lineSeparator());
    }

    private void printOperatingSystem(OperatingSystem os, StringBuilder probeOut) {
        probeOut.append(os).append(System.lineSeparator());
        probeOut.append("Booted: ").append(Instant.ofEpochSecond(os.getSystemBootTime())).append(System.lineSeparator());
        probeOut.append("Uptime: ").append(FormatUtil.formatElapsedSecs(os.getSystemUptime())).append(System.lineSeparator());
        probeOut.append("Running with").append(os.isElevated() ? "" : "out").append(" elevated permissions.").append(System.lineSeparator());

        try {
            if (os.getSessions().size() > 0) {
                probeOut.append("  Sessions").append(System.lineSeparator());
                for (OSSession s : os.getSessions()) {
                    probeOut.append(s.toString()).append(System.lineSeparator());
                }
            }
        } catch (NoClassDefFoundError e) {
            // Sessions need JNA artifacts
            // java.lang.NoClassDefFoundError: com/sun/jna/platform/linux/LibC
            log.warn("NoClassDefFoundError: " + e.getMessage() + "  see oshi-core documentation");
        }
    }
}
