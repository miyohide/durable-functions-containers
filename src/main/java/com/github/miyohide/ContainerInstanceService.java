package com.github.miyohide;

import com.azure.core.management.Region;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.containerinstance.models.ContainerGroup;
import com.azure.resourcemanager.containerinstance.models.ContainerGroupRestartPolicy;

public class ContainerInstanceService {
    private String resourceGroupName;
    private Region region;
    private String containerImage;
    private String aciName;

    public ContainerInstanceService(String resourceGroupName, Region region, String containerImage, String aciName) {
        this.resourceGroupName = resourceGroupName;
        this.region = region;
        this.containerImage = containerImage;
        this.aciName = aciName;
    }

    public boolean runContainerInstance(AzureResourceManager azureResourceManager) {
        ContainerGroup containerGroup = azureResourceManager.containerGroups().define(aciName)
                .withRegion(this.region)
                .withExistingResourceGroup(this.resourceGroupName)
                .withLinux()
                .withPublicImageRegistryOnly()
                .withoutVolume()
                .defineContainerInstance(aciName)
                .withImage(this.containerImage)
                .withExternalTcpPort(80)
                .attach()
                .withRestartPolicy(ContainerGroupRestartPolicy.NEVER)
                .withDnsPrefix(aciName)
                .create();

        return true;
    }
}
