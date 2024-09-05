package org.cloudbus.cloudsim.examples;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomVmAllocationPolicyRoundRobin extends VmAllocationPolicy {

    private int lastHostIndex;
    private Map<String, Host> vmTable;
    private List<? extends Host> hostList;

    public CustomVmAllocationPolicyRoundRobin(List<? extends Host> list) {
        super(list);
        this.hostList = list;
        this.lastHostIndex = -1;
        this.vmTable = new HashMap<>();
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        int numHosts = hostList.size();
        int nextHostIndex = (lastHostIndex + 1) % numHosts;
        Host nextHost = hostList.get(nextHostIndex);

        while (!nextHost.vmCreate(vm)) {
            nextHostIndex = (nextHostIndex + 1) % numHosts;
            if (nextHostIndex == lastHostIndex) {
                return false; // No available host found
            }
            nextHost = hostList.get(nextHostIndex);
        }

        lastHostIndex = nextHostIndex;
        vmTable.put(vm.getUid(), nextHost);
        return true;
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        if (host.vmCreate(vm)) {
            vmTable.put(vm.getUid(), host);
            return true;
        }
        return false;
    }

    @Override
    public void deallocateHostForVm(Vm vm) {
        Host host = vmTable.remove(vm.getUid());
        if (host != null) {
            host.vmDestroy(vm);
        }
    }

    @Override
    public Host getHost(Vm vm) {
        return vmTable.get(vm.getUid());
    }

    @Override
    public Host getHost(int vmId, int userId) {
        return vmTable.get(Vm.getUid(userId, vmId));
    }

    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
        return null; // No optimization for this simple policy
    }
}
