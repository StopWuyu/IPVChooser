package tech.egglink.iv.mixin;

import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tech.egglink.iv.Utils;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(MultiplayerScreen.class)
public abstract class ScreenMixin extends Screen {

    @Shadow protected abstract void refresh();

    @Shadow @Final private static Logger LOGGER;

    protected ScreenMixin(Text title) {
        super(title);
    }

    @Inject(
            method = "init",
            at = @At("HEAD")
    )
    public void injectCustomElements(CallbackInfo info) {
        // 创建 IPv4 按钮
        ButtonWidget button = addDrawableChild(new ButtonWidget.Builder(
                Text.of("IPv4/IPv6"),
                (widget) -> {
                    if (++Utils.type == 2)
                        Utils.type = 0;
                    switch (Utils.type) {
                        case 0 -> widget.setMessage(Text.of("IPv4/IPv6"));
                        case 1 -> widget.setMessage(Text.of("IPv4"));
                    }
                    refresh();
                }
        ).size(100,20)
                .position(20, 6)
                .build());
        switch (Utils.type) {
            case 0 -> button.setMessage(Text.of("IPv4/IPv6"));
            case 1 -> button.setMessage(Text.of("IPv4"));
        }
    }

    @Inject(method = "connect(Lnet/minecraft/client/network/ServerInfo;)V", at = @At("HEAD"), cancellable = true)
    public void connectServer(ServerInfo entry, CallbackInfo info) {
        info.cancel();
        if (this.client != null) {
            LOGGER.info(entry.address);
            ServerAddress addr = ServerAddress.parse(entry.address);
            ConnectScreen.connect(this, this.client, ServerAddress.parse(Objects.requireNonNull(Utils.resolveAddress(addr.
                    getAddress(), addr.getPort(), Utils.type)).getAddress().getHostAddress() + ":" + addr.getPort()), entry, false);
        }
    }
}