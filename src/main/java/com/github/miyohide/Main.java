package com.github.miyohide;

import com.azure.core.credential.TokenCredential;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.Region;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;

public class Main {
    public static void main(String[] args) {
        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
        TokenCredential credential = new DefaultAzureCredentialBuilder()
                .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
                .build();
        AzureResourceManager azure = AzureResourceManager
                .authenticate(credential, profile)
                .withDefaultSubscription();
        ContainerInstanceService containerInstanceService
                = new ContainerInstanceService(
                "rg-aci-sample",
                Region.JAPAN_EAST,
                "mcr.microsoft.com/azuredocs/aci-helloworld",
                "acisample");

        containerInstanceService.runContainerInstance(azure);
    }
}
