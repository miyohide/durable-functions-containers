package com.github.miyohide;

import com.azure.core.management.Region;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.containerinstance.models.ContainerGroup;
import com.azure.resourcemanager.containerinstance.models.ContainerGroupRestartPolicy;
import com.azure.resourcemanager.resources.fluentcore.utils.ResourceManagerUtils;

import java.time.Duration;

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

        // 起動中
        System.out.println("起動中 ..." + containerGroup.ipAddress());
        Utils.sendGetRequest("http://" + containerGroup.ipAddress());
        ResourceManagerUtils.sleep(Duration.ofSeconds(15));
        System.out.println("CURLing " + containerGroup.ipAddress());
        System.out.println(Utils.sendGetRequest("http://" + containerGroup.ipAddress()));

        return true;
    }
}