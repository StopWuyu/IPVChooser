package tech.egglink.iv.mixin;

import com.mojang.logging.LogUtils;
import io.netty.channel.ChannelFuture;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.egglink.iv.Utils;
import tech.egglink.iv.client.IPVChooserClient;

import java.net.InetSocketAddress;

@Mixin(ClientConnection.class)
public abstract class ConnectionMixin {
    @Shadow
    public static ChannelFuture connect(InetSocketAddress address, boolean useEpoll, final ClientConnection connection) {
        return null;
    }

    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();
    @Inject(
            method = "connect(Ljava/net/InetSocketAddress;Z)Lnet/minecraft/network/ClientConnection;",
            at = @At("HEAD"),
            cancellable = true)
    private static void connect(InetSocketAddress address, boolean useEpoll, CallbackInfoReturnable<ClientConnection> cir) {
        InetSocketAddress resolvedAddress = Utils.resolveAddress(address.getHostName(), address.getPort(), Utils.type);
        ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);
        ChannelFuture channelFuture = connect(resolvedAddress, useEpoll, clientConnection);
        if (channelFuture != null) {
            channelFuture.syncUninterruptibly();
        }
        if (resolvedAddress != null) {
            cir.setReturnValue(clientConnection);
        } else {
            cir.cancel();
        }
    }
}
