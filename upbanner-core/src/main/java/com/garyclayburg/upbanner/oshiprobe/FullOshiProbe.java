package com.garyclayburg.upbanner.oshiprobe;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OSProcess;
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
        log.debug("OSHI probing environment");
        try {
            SystemInfo si = new SystemInfo();
            HardwareAbstractionLayer hal = si.getHardware();
            CentralProcessor cpu = hal.getProcessor();
            int physicalProcessorCount = cpu.getPhysicalProcessorCount();

            OperatingSystem operatingSystem = si.getOperatingSystem();
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
            GlobalMemory globalMemory = hal.getMemory();
            printMemory(globalMemory, probeOut);
            probeOut.append("=============").append(System.lineSeparator());
            probeOut.append("  VM or container probe").append(System.lineSeparator());
            probeOut.append("name: ").append(DetectVM.identifyVM()).append(System.lineSeparator());
            probeOut.append("=============").append(System.lineSeparator());
            probeOut.append("  Operating System").append(System.lineSeparator());
            printOperatingSystem(operatingSystem, probeOut);

            printProcesses(operatingSystem, globalMemory,probeOut);
        } catch (NoClassDefFoundError e) {
            probeOut.append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append("WARN - Cannot completely probe underlying hardware/OS using OSHI.  ")
                    .append(System.lineSeparator())
                    .append("     NoClassDefFoundError: ").append(e.getMessage())
                    .append(System.lineSeparator())
                    .append("     JNA dependency conflict?")
                    .append(System.lineSeparator())
                    .append("     See OSHI documentation to fix dependencies for this application/platform: ")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator()
                    );
        }
/*
        requires native library to run
        NoClassDefFoundError: com/sun/jna/platform/linux/LibC  see oshi-core documentation
         */

    }
    private static void printProcesses(OperatingSystem os, GlobalMemory memory, StringBuilder probeOut) {
//        String pid = System.getProperty("PID");
//            OSProcess myProc = os.getProcess(Integer.parseInt(pid));
        OSProcess myProc = os.getProcess(os.getProcessId());
        // current process will never be null. Other code should check for null here
        probeOut.append("My PID: ")
                .append(myProc.getProcessID())
                .append(" with affinity ")
                .append(Long.toBinaryString(myProc.getAffinityMask()))
                .append(System.lineSeparator());
        probeOut.append("Processes: ")
                .append(os.getProcessCount())
                .append(", Threads: ")
                .append(os.getThreadCount())
                .append(System.lineSeparator());
        // Sort by highest CPU
        List<OSProcess> procs = os.getProcesses(5, OperatingSystem.ProcessSort.CPU);
        probeOut.append("   PID  %CPU %MEM       VSZ       RSS Name").append(System.lineSeparator());
        for (int i = 0; i < procs.size() && i < 5; i++) {
            OSProcess p = procs.get(i);
            probeOut.append(String.format(" %5d %5.1f %4.1f %9s %9s %s", p.getProcessID(),
                    100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
                    100d * p.getResidentSetSize() / memory.getTotal(), FormatUtil.formatBytes(p.getVirtualSize()),
                    FormatUtil.formatBytes(p.getResidentSetSize()), p.getName())).append(System.lineSeparator());
        }
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

        if (os.getSessions().size() > 0) {
            probeOut.append("  Sessions").append(System.lineSeparator());
            for (OSSession s : os.getSessions()) {
                probeOut.append(s.toString()).append(System.lineSeparator());
            }
        }
    }
}
