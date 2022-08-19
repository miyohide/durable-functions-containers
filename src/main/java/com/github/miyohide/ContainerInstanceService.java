package com.github.miyohide;

import com.azure.core.management.Region;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.containerinstance.models.ContainerGroup;
import com.azure.resourcemanager.containerinstance.models.ContainerGroupRestartPolicy;
import com.azure.resourcemanager.resources.fluentcore.utils.ResourceManagerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class ContainerInstanceService {
    Logger logger = LoggerFactory.getLogger(ContainerInstanceService.class);
    private ContainerGroup containerGroup;
    private final String resourceGroupName;
    private final Region region;
    private final String containerImage;
    private final String aciName;

    public ContainerInstanceService(String resourceGroupName, Region region, String containerImage, String aciName) {
        this.containerGroup = null;
        this.resourceGroupName = resourceGroupName;
        this.region = region;
        this.containerImage = containerImage;
        this.aciName = aciName;
    }

    public void runContainerInstance(AzureResourceManager azureResourceManager) {
        containerGroup = azureResourceManager.containerGroups().define(aciName)
                .withRegion(this.region)
                .withNewResourceGroup(this.resourceGroupName)
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
        logger.debug(getStatus());
        logger.debug("起動中 ..." + containerGroup.ipAddress());
        Utils.sendGetRequest("http://" + containerGroup.ipAddress());
        ResourceManagerUtils.sleep(Duration.ofSeconds(15));
        logger.debug(getStatus());
    }

    public String getStatus() {
        return containerGroup.state();
    }
}
